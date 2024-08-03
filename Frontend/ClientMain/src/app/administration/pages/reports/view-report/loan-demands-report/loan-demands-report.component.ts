import { DatePipe } from '@angular/common';
import { HttpParams } from '@angular/common/http';
import { Component, Inject, OnInit } from '@angular/core';
import { FormGroup, UntypedFormBuilder, Validators } from '@angular/forms';
import {
  MatDialog, MatDialogConfig, MatDialogRef,
  MAT_DIALOG_DATA
} from '@angular/material/dialog';
import {
  MatSnackBar, MatSnackBarHorizontalPosition,
  MatSnackBarVerticalPosition
} from '@angular/material/snack-bar';
import { Subscription } from 'rxjs';
import { AuthService } from 'src/@core/AuthService/auth.service';
import { ReportsService } from 'src/app/administration/Service/reports/reports.service';
import { MembershipLookUpComponent } from '../../../MembershipComponent/Membership/membership-look-up/membership-look-up.component';
import { AccountsLookupComponent } from '../../accounts-lookup/accounts-lookup.component';
import { SolCodeLookupComponent } from '../../accounts-lookup/sol-code-lookup/sol-code-lookup.component';
import { ViewReportComponent } from '../view-report.component';

@Component({
  selector: 'app-loan-demands-report',
  templateUrl: './loan-demands-report.component.html',
  styleUrls: ['./loan-demands-report.component.scss'],
})
export class LoanDemandsReportComponent implements OnInit {
  loanDemandsReport: FormGroup;
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
  loansGuarantorReport: boolean = false;
  users: any[] = [];
  horizontalPosition: MatSnackBarHorizontalPosition = 'end';
  verticalPosition: MatSnackBarVerticalPosition = 'top';
  reportTitle: string;
  allGuarantorsReport: boolean = false;
  guarantorshipByLoanReport: boolean = false;

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

    this.reportTitle = this.data.reportTitles;
    this.allDisburmentsReportsSelected = this.data.action  == 'All Loan Demands Reports';
    this.branchSelected = this.data.action  == 'Branch Loan Demands Reports';
    this.loansGuarantorReport =  this.data.action  == 'Guarantor Loans Report';
    this.allGuarantorsReport =  this.data.action  == 'allGuarantorsReport';
    this.guarantorshipByLoanReport =  this.data.action  == 'guarantorshipByLoanReport';

    // if (this.data.action == 'All Loan Demands Reports') {
    //   this.allDisburmentsReportsSelected = true;
    //   this.disbursedBySelected = false;
    //   this.branchSelected = false;
    //   this.loansGuarantorReport = false;
    // } else if (this.data.action == 'Branch Loan Demands Reports') {
    //   this.allDisburmentsReportsSelected = false;
    //   this.disbursedBySelected = false;
    //   this.branchSelected = true;
    //   this.loansGuarantorReport = false;
    // } else if (this.data.action == 'Guarantor Loans Report') {
    //   this.allDisburmentsReportsSelected = false;
    //   this.disbursedBySelected = false;
    //   this.branchSelected = false;
    //   this.loansGuarantorReport = true;
    // } else {
    //   this.allDisburmentsReportsSelected = false;
    //   this.disbursedBySelected = true;
    //   this.branchSelected = false;
    //   this.loansGuarantorReport = false;
    // }

    this.getData();

    this.loanDemandsReport = this.initLoanDemandsReportForm();
  }

  initLoanDemandsReportForm(): FormGroup {
    return this.fb.group({
      username: [''],
      branchCode: [''],
      guarantorCode: [''],
      acid: [''],
      fromdate: ['2010-01-01', [Validators.required]],
      todate: ['2024-01-01', [Validators.required]],
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
      this.loanDemandsReport.patchValue({ acid: result.data.acid });
    });
  }

  solCodeLookup() {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.disableClose = false;
    dialogConfig.autoFocus = true;
    dialogConfig.width = '400px';
    dialogConfig.data = {
      data: '',
    };
    const dialogRef = this.dialog.open(SolCodeLookupComponent, dialogConfig);
    dialogRef.afterClosed().subscribe((result) => {
      this.loanDemandsReport.patchValue({ branchCode: result.data.branchCode });
    });
  }

  customerLookup() {
    const dialogRef = this.dialog.open(MembershipLookUpComponent, {
      width: '60%',
      autoFocus: false,
      maxHeight: '90vh',
    });
    dialogRef.afterClosed().subscribe((result) => {
      this.loanDemandsReport.patchValue({
        guarantorCode: result.data.customercode,
      });
    });
  }

  submit() {
    if (this.allDisburmentsReportsSelected) {
      this.generateAllLoanDemandsReport();
    } else if (this.branchSelected) {
      this.generateBranchLoanDemandsReports();
    } else if (this.loansGuarantorReport) {
      this.generateMyGuaranteedLoansReport();
    } else if (this.guarantorshipByLoanReport) {
      this.generateGuarantorshipByLoanReport();
    } else if (this.allGuarantorsReport) {
      this.generateAllGuarantorsReport();
    } else {
      this.generateUserLoanDemandsReports();
    }
  }

  fillFormCorrectly() {
    this._snackBar.open('Please fill out the form correctly !', 'X', {
      horizontalPosition: this.horizontalPosition,
      verticalPosition: this.verticalPosition,
      duration: 3000,
      panelClass: ['red-snackbar', 'login-snackbar'],
    });
  }

  getFromDate(){
    return this.datepipe.transform(
      this.loanDemandsReport.value.fromdate,
      'yyyy-MM-ddTHH:mm:ss'
    );
  }

  getToDate(){
    return this.datepipe.transform(
      this.loanDemandsReport.value.todate,
      'yyyy-MM-ddTHH:mm:ss'
    );
  }


  generateGuarantorshipByLoanReport() {
    if (
      this.loanDemandsReport.value.acid == ''
    ) {
      this.fillFormCorrectly();
    } else {
      this.loading = true;
      const params = new HttpParams()
      .set('acid',this.loanDemandsReport.value.acid);

        this.generateReportDynamically(params, this.reportsService.guarantorshipByLoanReport, "guarantorshipByLoanReport");
    }
  }

  generateAllGuarantorsReport() {
    if (
      false
    ) {
      this.fillFormCorrectly();
    } else {
      this.loading = true;
      const params = new HttpParams()
      .set('fromdate', this.getFromDate())
      .set('todate', this.getToDate())

        this.generateReportDynamically(params, this.reportsService.allGuarantorsReport, "accountsBySchemeReport");
    }
  }

  getData() {
    this.subscription = this.authService.allUsers().subscribe (
      (res) => {
        this.users = res;

        console.log('Users : ', this.users);
      },
      (err) => {
        console.log(err);
      }
    );
  }

  generateAllLoanDemandsReport() {
    this.loading = true;

    let fromDate = this.datepipe.transform(
      this.loanDemandsReport.value.fromdate,
      'yyyy-MM-ddTHH:mm:ss'
    );

    let toDate = this.datepipe.transform(
      this.loanDemandsReport.value.todate,
      'yyyy-MM-ddTHH:mm:ss'
    );

    const params = new HttpParams()
      .set('fromdate', fromDate)
      .set('todate', toDate);

    this.subscription = this.reportsService
      .generateAllLoanDemandsReport(params)
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

  generateReportDynamically(params, url, filename) {
    console.log(`${url}   -    ${filename}`);
    this.subscription = this.reportsService
    .dynamicReportGenerator(params, url, filename)
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




  generateMyGuaranteedLoansReport() {
    if (this.loanDemandsReport.value.guarantorCode == '') {
      this._snackBar.open('Please fill out the form correctly !', 'X', {
        horizontalPosition: this.horizontalPosition,
        verticalPosition: this.verticalPosition,
        duration: 3000,
        panelClass: ['red-snackbar', 'login-snackbar'],
      });
    } else {
      this.loading = true;

      const params = new HttpParams()
        .set('guarantorCode', this.loanDemandsReport.value.guarantorCode)
        // .set('fromdate', this.getFromDate())
        // .set('todate', this.getToDate());

      this.subscription = this.reportsService
        .generateMyGuaranteedLoansReport(params)
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
  }

  generateBranchLoanDemandsReports() {
    let fromDate = this.datepipe.transform(
      this.loanDemandsReport.value.fromdate,
      'yyyy-MM-ddTHH:mm:ss'
    );

    let toDate = this.datepipe.transform(
      this.loanDemandsReport.value.todate,
      'yyyy-MM-ddTHH:mm:ss'
    );

    if (this.loanDemandsReport.value.branchCode == '') {
      this._snackBar.open('Please fill out the form correctly !', 'X', {
        horizontalPosition: this.horizontalPosition,
        verticalPosition: this.verticalPosition,
        duration: 3000,
        panelClass: ['red-snackbar', 'login-snackbar'],
      });
    } else {
      this.loading = true;

      const params = new HttpParams()
        .set('branchCode', this.loanDemandsReport.value.branchCode)
        .set('fromdate', fromDate)
        .set('todate', toDate);

      this.subscription = this.reportsService
        .generateBranchLoanDemandsReports(params)
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
  }

  generateUserLoanDemandsReports() {
    let fromDate = this.datepipe.transform(
      this.loanDemandsReport.value.fromdate,
      'yyyy-MM-ddTHH:mm:ss'
    );

    let toDate = this.datepipe.transform(
      this.loanDemandsReport.value.todate,
      'yyyy-MM-ddTHH:mm:ss'
    );

    if (this.loanDemandsReport.value.username == '') {
      this._snackBar.open('Please fill out the form correctly !', 'X', {
        horizontalPosition: this.horizontalPosition,
        verticalPosition: this.verticalPosition,
        duration: 3000,
        panelClass: ['red-snackbar', 'login-snackbar'],
      });
    } else {
      this.loading = true;

      const params = new HttpParams()
        .set('username', this.loanDemandsReport.value.username)
        .set('fromdate', fromDate)
        .set('todate', toDate);

      this.subscription = this.reportsService
        .generateUserLoanDemandsReports(params)
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
  }

  cancel() {
    this.dialogRef.close();
  }
}
