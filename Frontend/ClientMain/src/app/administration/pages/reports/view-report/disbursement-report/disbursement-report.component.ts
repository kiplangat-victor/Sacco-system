import { DatePipe } from '@angular/common';
import { HttpParams } from '@angular/common/http';
import { Component, Inject, OnInit } from '@angular/core';
import { FormGroup, UntypedFormBuilder, Validators } from '@angular/forms';
import { MatDialog, MatDialogConfig, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatSnackBar, MatSnackBarHorizontalPosition, MatSnackBarVerticalPosition } from '@angular/material/snack-bar';
import { Subscription } from 'rxjs';
import { AuthService } from 'src/@core/AuthService/auth.service';
import { ReportsService } from 'src/app/administration/Service/reports/reports.service';
import { AccountsLookupComponent } from '../../accounts-lookup/accounts-lookup.component';
import { SolCodeLookupComponent } from '../../accounts-lookup/sol-code-lookup/sol-code-lookup.component';
import { ViewReportComponent } from '../view-report.component';

@Component({
  selector: 'app-disbursement-report',
  templateUrl: './disbursement-report.component.html',
  styleUrls: ['./disbursement-report.component.scss']
})
export class DisbursementReportComponent implements OnInit {
  disbursementReportForm: FormGroup;
  action: string;
  dialogTitle: string;
  schemeTypes: any = [
    { code: 'LAA', description: 'Loan Accounts' },
    { code: 'ODA', description: 'Overdraft Accounts' },
    { code: 'SBA', description: 'Savings Accounts' },
    { code: 'TDA', description: 'Term Deposits' },
    { code: 'CAA', description: 'Current Accounts' },
  ];
  subscription!: Subscription;
  error: any;
  loading: boolean;
  allDisburmentsReportsSelected: boolean = false;
  disbursedBySelected: boolean = false;
  branchSelected: boolean = false;
  users: any [] = [];
  horizontalPosition: MatSnackBarHorizontalPosition = 'end';
  verticalPosition: MatSnackBarVerticalPosition = 'top';
  reportTitle: string;

  constructor(
    private fb: UntypedFormBuilder,
    private datepipe: DatePipe,
    private dialog: MatDialog,
    private _snackBar: MatSnackBar,
    private reportsService: ReportsService,
    public dialogRef: MatDialogRef<ViewReportComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    console.log(this.data);

    this.reportTitle = this.data.action;

    if(this.data.action == "All Disbursements Report"){
      this.allDisburmentsReportsSelected = true
      this.disbursedBySelected = false;
      this.branchSelected = false;
    }else if (this.data.action == "Branch Disbursements"){
      this.allDisburmentsReportsSelected = false;
      this.disbursedBySelected = false;
      this.branchSelected = true;
    } else {
      this.allDisburmentsReportsSelected = false;
      this.disbursedBySelected = true;
      this.branchSelected = false;
    }

    this.getData();

    this.disbursementReportForm = this.initDisbursementsReportForm();
  }


  initDisbursementsReportForm(): FormGroup {
    return this.fb.group({
      disbursedBy: [''],
      branchCode: [''],
      fromdate: ['', [Validators.required]],
      todate: ['', [Validators.required]],
    });
  }

  accountLookup() {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.disableClose = false;
    dialogConfig.autoFocus = true;
    dialogConfig.width = '600px';
    dialogConfig.data = {
      data: '',
    };
    const dialogRef = this.dialog.open(AccountsLookupComponent, dialogConfig);
    dialogRef.afterClosed().subscribe((result) => {
      console.log(result);

      console.log(result.data.acid);
      this.disbursementReportForm.patchValue({ acid: result.data.acid });
    });
  }

  solCodeLookup(){
    const dialogConfig = new MatDialogConfig();
    dialogConfig.disableClose = false;
    dialogConfig.autoFocus = true;
    dialogConfig.width = '400px';
    dialogConfig.data = {
      data: '',
    };
    const dialogRef = this.dialog.open(SolCodeLookupComponent, dialogConfig);
    dialogRef.afterClosed().subscribe((result) => {

      this.disbursementReportForm.patchValue({ branchCode: result.data.branchCode })
    });
  }


  submit() {
    if(this.allDisburmentsReportsSelected){
      this.generateAllLoanPortfoliosReport();
    }else if(this.branchSelected){
      this.generateBranchDisbursementsReport()
    }else {
      this.getUserDisbursements()
    }
  }


  getData() {
    this.subscription =  this.authService.allUsers().subscribe(res => {
      this.users = res;

      console.log("Users : ", this.users)
     }, err => {
      console.log(err);
     })
   }

  generateAllLoanPortfoliosReport() {
    this.loading = true;

    let fromDate = this.datepipe.transform(
      this.disbursementReportForm.value.fromdate,
      'yyyy-MM-ddTHH:mm:ss'
    );

    let toDate = this.datepipe.transform(
      this.disbursementReportForm.value.todate,
      'yyyy-MM-ddTHH:mm:ss'
    );

    const params = new HttpParams()
      .set('fromdate', fromDate)
      .set('todate', toDate);

    this.subscription = this.reportsService
      .generateAllDisbursementsReport(params)
      .subscribe(
        (response) => {
          let url = window.URL.createObjectURL(response.data);

          // if you want to open PDF in new tab
          window.open(url);

          let a = document.createElement('a');
          document.body.appendChild(a);
          a.setAttribute('style', 'display: none');
          a.setAttribute('target', 'blank');
          a.href = url;
          a.download = response.filename;
          a.click();
          window.URL.revokeObjectURL(url);
          a.remove();

          this.loading = false;

          this.dialogRef.close();


          this._snackBar.open('Report generated successfully !', 'X', {
            horizontalPosition: this.horizontalPosition,
            verticalPosition: this.verticalPosition,
            duration: 3000,
            panelClass: ['green-snackbar', 'login-snackbar'],
          });
        },
        (err) => {
          this.error = err;
          this.loading = false;

          this.dialogRef.close();

          this._snackBar.open(`Error generating report !`, 'X', {
            horizontalPosition: this.horizontalPosition,
            verticalPosition: this.verticalPosition,
            duration: 3000,
            panelClass: ['red-snackbar', 'login-snackbar'],
          });
        }
      );
  }

  generateBranchDisbursementsReport() {
    let fromDate = this.datepipe.transform(
      this.disbursementReportForm.value.fromdate,
      'yyyy-MM-ddTHH:mm:ss'
    );

    let toDate = this.datepipe.transform(
      this.disbursementReportForm.value.todate,
      'yyyy-MM-ddTHH:mm:ss'
    );

    if(this.disbursementReportForm.value.branchCode == ""){
      this._snackBar.open('Please fill out the form correctly !', 'X', {
        horizontalPosition: this.horizontalPosition,
        verticalPosition: this.verticalPosition,
        duration: 3000,
        panelClass: ['red-snackbar', 'login-snackbar'],
      });
    }else{
      this.loading = true;

      const params = new HttpParams()
      .set('branchCode', this.disbursementReportForm.value.branchCode)
      .set('fromdate', fromDate)
      .set('todate', toDate);

    this.subscription = this.reportsService
      .generateBranchDisbursementsReport(params)
      .subscribe(
        (response) => {
          let url = window.URL.createObjectURL(response.data);


          window.open(url);

          let a = document.createElement('a');
          document.body.appendChild(a);
          a.setAttribute('style', 'display: none');
          a.setAttribute('target', 'blank');
          a.href = url;
          a.download = response.filename;
          a.click();
          window.URL.revokeObjectURL(url);
          a.remove();

          this.loading = false;

          this.dialogRef.close();

          this._snackBar.open('Report generated successfully !', 'X', {
            horizontalPosition: this.horizontalPosition,
            verticalPosition: this.verticalPosition,
            duration: 3000,
            panelClass: ['green-snackbar', 'login-snackbar'],
          });
        },
        (err) => {
          this.error = err;
          this.loading = false;

          this.dialogRef.close();

          this._snackBar.open(`Error generating report !`, 'X', {
            horizontalPosition: this.horizontalPosition,
            verticalPosition: this.verticalPosition,
            duration: 3000,
            panelClass: ['red-snackbar', 'login-snackbar'],
          });
        }
      );
    }
  }

  getUserDisbursements(){
    let fromDate = this.datepipe.transform(
      this.disbursementReportForm.value.fromdate,
      'yyyy-MM-ddTHH:mm:ss'
    );

    let toDate = this.datepipe.transform(
      this.disbursementReportForm.value.todate,
      'yyyy-MM-ddTHH:mm:ss'
    );

    if(this.disbursementReportForm.value.disbursedBy == ""){
      this._snackBar.open('Please fill out the form correctly !', 'X', {
        horizontalPosition: this.horizontalPosition,
        verticalPosition: this.verticalPosition,
        duration: 3000,
        panelClass: ['red-snackbar', 'login-snackbar'],
      });
    }else {
      this.loading = true;

      const params = new HttpParams()
      .set('disbursedBy', this.disbursementReportForm.value.disbursedBy)
      .set('fromdate', fromDate)
      .set('todate', toDate);

    this.subscription = this.reportsService
      .generateUserDisbursementsReport(params)
      .subscribe(
        (response) => {
          let url = window.URL.createObjectURL(response.data);

          window.open(url);

          let a = document.createElement('a');
          document.body.appendChild(a);
          a.setAttribute('style', 'display: none');
          a.setAttribute('target', 'blank');
          a.href = url;
          a.download = response.filename;
          a.click();
          window.URL.revokeObjectURL(url);
          a.remove();

          this.loading = false;

          this.dialogRef.close();

          this._snackBar.open('Report generated successfully !', 'X', {
            horizontalPosition: this.horizontalPosition,
            verticalPosition: this.verticalPosition,
            duration: 3000,
            panelClass: ['green-snackbar', 'login-snackbar'],
          });
        },
        (err) => {
          this.error = err;
          this.loading = false;

          this.dialogRef.close();
          this._snackBar.open(`Error generating report !`, 'X', {
            horizontalPosition: this.horizontalPosition,
            verticalPosition: this.verticalPosition,
            duration: 3000,
            panelClass: ['red-snackbar', 'login-snackbar'],
          });
        }
      );
    }
  }

  cancel(){
    this.dialogRef.close();
  }
}
