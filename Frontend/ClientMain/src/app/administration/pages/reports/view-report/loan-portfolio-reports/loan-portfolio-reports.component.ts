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
  selector: 'app-loan-portfolio-reports',
  templateUrl: './loan-portfolio-reports.component.html',
  styleUrls: ['./loan-portfolio-reports.component.scss']
})
export class LoanPortfolioReportsComponent implements OnInit {
  loansReportForm: FormGroup;
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

    if(this.data.action == "All Loan Portfolios"){
      this.allDisburmentsReportsSelected = true
      this.disbursedBySelected = false;
      this.branchSelected = false;
    }else if (this.data.action == "Branch Loan Portfolios"){
      this.allDisburmentsReportsSelected = false;
      this.disbursedBySelected = false;
      this.branchSelected = true;
    } else {
      this.allDisburmentsReportsSelected = false;
      this.disbursedBySelected = true;
      this.branchSelected = false;
    }

    this.getData();

    this.loansReportForm = this.initLoansReportForm();
  }


  initLoansReportForm(): FormGroup {
    return this.fb.group({
      username: [''],
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
      this.loansReportForm.patchValue({ acid: result.data.acid });
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

      this.loansReportForm.patchValue({ branchCode: result.data.branchCode })
    });
  }


  submit() {
    if(this.allDisburmentsReportsSelected){
      this.getAllLoanPortfolios();
    }else if(this.branchSelected){
      this.generateBranchLoanPortfolioReport()
    }else {
      this.getManagerLoanPortfolioReport()
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

  getAllLoanPortfolios() {
    this.loading = true;

    let fromDate = this.datepipe.transform(
      this.loansReportForm.value.fromdate,
      'yyyy-MM-ddTHH:mm:ss'
    );

    let toDate = this.datepipe.transform(
      this.loansReportForm.value.todate,
      'yyyy-MM-ddTHH:mm:ss'
    );

    const params = new HttpParams()
      .set('fromdate', fromDate)
      .set('todate', toDate);

    this.subscription = this.reportsService
      .generateLoanPortfoliosReport(params)
      .subscribe(
        (response) => {
          let url = window.URL.createObjectURL(response.data);


          window.open(url);

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

  generateBranchLoanPortfolioReport() {
    let fromDate = this.datepipe.transform(
      this.loansReportForm.value.fromdate,
      'yyyy-MM-ddTHH:mm:ss'
    );

    let toDate = this.datepipe.transform(
      this.loansReportForm.value.todate,
      'yyyy-MM-ddTHH:mm:ss'
    );

    if(this.loansReportForm.value.branchCode == ""){
      this._snackBar.open('Please fill out the form correctly !', 'X', {
        horizontalPosition: this.horizontalPosition,
        verticalPosition: this.verticalPosition,
        duration: 3000,
        panelClass: ['red-snackbar', 'login-snackbar'],
      })
    }else {
      const params = new HttpParams()
      .set('branchCode', this.loansReportForm.value.branchCode)
      .set('fromdate', fromDate)
      .set('todate', toDate);

    this.subscription = this.reportsService
      .generateBranchLoanPortfoliosReport(params)
      .subscribe(
        (response) => {
          this.loading = true;

          let url = window.URL.createObjectURL(response.data);

          // if you want to open PDF in new tab
          window.open(url);

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

  getManagerLoanPortfolioReport(){
    let fromDate = this.datepipe.transform(
      this.loansReportForm.value.fromdate,
      'yyyy-MM-ddTHH:mm:ss'
    );

    let toDate = this.datepipe.transform(
      this.loansReportForm.value.todate,
      'yyyy-MM-ddTHH:mm:ss'
    );

    if(this.loansReportForm.value.username == ""){
      this._snackBar.open('Please fill out the form correctly !', 'X', {
        horizontalPosition: this.horizontalPosition,
        verticalPosition: this.verticalPosition,
        duration: 3000,
        panelClass: ['red-snackbar', 'login-snackbar'],
      })
    }else {
      const params = new HttpParams()
      .set('username', this.loansReportForm.value.username)
      .set('fromdate', fromDate)
      .set('todate', toDate);

    this.subscription = this.reportsService
      .generateUserLoanPortfoliosReport(params)
      .subscribe(
        (response) => {
          let url = window.URL.createObjectURL(response.data);

          // if you want to open PDF in new tab
          window.open(url);

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
