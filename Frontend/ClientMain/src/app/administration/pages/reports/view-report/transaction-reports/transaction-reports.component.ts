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
  selector: 'app-transaction-reports',
  templateUrl: './transaction-reports.component.html',
  styleUrls: ['./transaction-reports.component.scss'],
})
export class TransactionReportsComponent implements OnInit {
  transactionsReportForm: FormGroup;
  action: string;
  dialogTitle: string;
  schemeTypes: any = [
    { code: 'LAA', description: 'Loan Accounts' },
    { code: 'SBA', description: 'Savings Accounts' },
    { code: 'OAB', description: 'Office Accounts' },
    { code: 'ODA', description: 'Overdraft Accounts' },
    { code: 'TDA', description: 'Term Deposits' },
    { code: 'CAA', description: 'Current Accounts' },
  ];
  scope: any = ['All Accounts', 'Branch Accounts'];
  subscription!: Subscription;
  error: any;
  loading: boolean;
  allDisburmentsReportsSelected: boolean = false;
  disbursedBySelected: boolean = false;
  branchSelected: boolean = false;
  accountTypeTransactionsSelected: boolean = false;
  customerAccountTypeTransactionsSelected: boolean = false;
  users: any[] = [];
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
  ) { }

  ngOnInit(): void {
    console.log(this.data);

    this.reportTitle = this.data.action;

    if (this.data.action == 'All Transactions') {
      this.allDisburmentsReportsSelected = true;
      this.disbursedBySelected = false;
      this.branchSelected = false;
      this.accountTypeTransactionsSelected = false;
    } else if (this.data.action == 'Branch Transactions') {
      this.allDisburmentsReportsSelected = false;
      this.disbursedBySelected = false;
      this.branchSelected = true;
      this.accountTypeTransactionsSelected = false;
    } else if (this.data.action == 'Account Type Transactions Reports') {
      this.allDisburmentsReportsSelected = false;
      this.disbursedBySelected = false;
      this.accountTypeTransactionsSelected = true;
      this.branchSelected = false;
      this.customerAccountTypeTransactionsSelected = false;
    } else if (this.data.action == 'Customer Account Type Transaction Report') {
      this.allDisburmentsReportsSelected = false;
      this.disbursedBySelected = false;
      this.accountTypeTransactionsSelected = false;
      this.branchSelected = false;
      this.customerAccountTypeTransactionsSelected = true;
    } else {
      this.allDisburmentsReportsSelected = false;
      this.disbursedBySelected = true;
      this.branchSelected = false;
      this.accountTypeTransactionsSelected = false;
      this.customerAccountTypeTransactionsSelected = false;
    }

    this.getData();

    this.transactionsReportForm = this.initTransactionsReportForm();
  }

  initTransactionsReportForm(): FormGroup {
    return this.fb.group({
      type: [''],
      username: [''],
      branchCode: [''],
      accountType: [''],
      customerCode: [''],
      fromdate: ['', [Validators.required]],
      todate: ['', [Validators.required]],
    });
  }

  customerLookup() {
    const dialogRef = this.dialog.open(MembershipLookUpComponent, {
      width: '60%',
      autoFocus: false,
      maxHeight: '90vh',
    });
    dialogRef.afterClosed().subscribe((result) => {
      this.transactionsReportForm.patchValue({
        customerCode: result.data.customercode,
      });
    });
  }

  specifyReportScope(event: any) {
    console.log(event.target.value);

    if (event.target.value == 'Branch Accounts') {
      this.branchSelected = true;
    } else {
      this.branchSelected = false;
    }
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
      this.transactionsReportForm.patchValue({ acid: result.data.acid });
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
      this.transactionsReportForm.patchValue({
        branchCode: result.data.branchCode,
      });
    });
  }

  submit() {
    if (this.allDisburmentsReportsSelected) {
      this.generateAllRepaymentsReport();
    } else if (this.branchSelected) {
      this.generateBranchRepaymentsReport();
    } else if (this.customerAccountTypeTransactionsSelected) {
      this.generateAllCustomerTypesAccountTypesTransactions();
    } else if (
      this.transactionsReportForm.value.type == 'All Accounts' &&
      this.accountTypeTransactionsSelected == true
    ) {
      this.generateAllAccountTypesTransactions();
    } else if (
      this.transactionsReportForm.value.type == 'Branch Accounts' &&
      this.accountTypeTransactionsSelected == true
    ) {
      this.generateBranchAccountTypesTransactions();
    } else {
      this.generateManagerRepaymentsReport();
    }
  }

  getData() {
    this.subscription = this.authService.allUsers().subscribe(
      (res) => {
        this.users = res;

        console.log('Users : ', this.users);
      },
      (err) => {
        console.log(err);
      }
    );
  }

  generateAllRepaymentsReport() {
    let fromDate = this.datepipe.transform(
      this.transactionsReportForm.value.fromdate,
      'yyyy-MM-ddTHH:mm:ss'
    );

    let toDate = this.datepipe.transform(
      this.transactionsReportForm.value.todate,
      'yyyy-MM-ddTHH:mm:ss'
    );

    const params = new HttpParams()
      .set('fromdate', fromDate)
      .set('todate', toDate);

    this.subscription = this.reportsService
      .generateAllTransactions(params)
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

  generateBranchRepaymentsReport() {
    let fromDate = this.datepipe.transform(
      this.transactionsReportForm.value.fromdate,
      'yyyy-MM-ddTHH:mm:ss'
    );

    let toDate = this.datepipe.transform(
      this.transactionsReportForm.value.todate,
      'yyyy-MM-ddTHH:mm:ss'
    );

    if (this.transactionsReportForm.value.branchCode == '') {
      this._snackBar.open('Please fill out the form correctly !', 'X', {
        horizontalPosition: this.horizontalPosition,
        verticalPosition: this.verticalPosition,
        duration: 3000,
        panelClass: ['red-snackbar', 'login-snackbar'],
      });
    } else {
      this.loading = true;


      const params = new HttpParams()
        .set('branchCode', this.transactionsReportForm.value.branchCode)
        .set('fromdate', fromDate)
        .set('todate', toDate);

      this.subscription = this.reportsService
        .generateBranchTransactions(params)
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

  generateManagerRepaymentsReport() {
    let fromDate = this.datepipe.transform(
      this.transactionsReportForm.value.fromdate,
      'yyyy-MM-ddTHH:mm:ss'
    );

    let toDate = this.datepipe.transform(
      this.transactionsReportForm.value.todate,
      'yyyy-MM-ddTHH:mm:ss'
    );

    if (this.transactionsReportForm.value.username == '') {
      this._snackBar.open('Please fill out the form correctly !', 'X', {
        horizontalPosition: this.horizontalPosition,
        verticalPosition: this.verticalPosition,
        duration: 3000,
        panelClass: ['red-snackbar', 'login-snackbar'],
      });
    } else {
      this.loading = true;

      const params = new HttpParams()
        .set('username', this.transactionsReportForm.value.username)
        .set('fromdate', fromDate)
        .set('todate', toDate);

      this.subscription = this.reportsService
        .generateManagerRepaymentsReport(params)
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

  generateAllAccountTypesTransactions() {
    let fromDate = this.datepipe.transform(
      this.transactionsReportForm.value.fromdate,
      'yyyy-MM-ddTHH:mm:ss'
    );

    let toDate = this.datepipe.transform(
      this.transactionsReportForm.value.todate,
      'yyyy-MM-ddTHH:mm:ss'
    );

    if (this.transactionsReportForm.value.accountType == '') {
      this._snackBar.open('Please fill out the form correctly !', 'X', {
        horizontalPosition: this.horizontalPosition,
        verticalPosition: this.verticalPosition,
        duration: 3000,
        panelClass: ['red-snackbar', 'login-snackbar'],
      });
    } else {
      this.loading = true;


      const params = new HttpParams()
        .set('accountType', this.transactionsReportForm.value.accountType)
        .set('fromdate', fromDate)
        .set('todate', toDate);

      this.subscription = this.reportsService
        .generateAllAccountTypesTransactions(params)
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

  generateBranchAccountTypesTransactions() {
    let fromDate = this.datepipe.transform(
      this.transactionsReportForm.value.fromdate,
      'yyyy-MM-ddTHH:mm:ss'
    );

    let toDate = this.datepipe.transform(
      this.transactionsReportForm.value.todate,
      'yyyy-MM-ddTHH:mm:ss'
    );

    if (this.transactionsReportForm.value.accountType == "" || this.transactionsReportForm.value.branchCode == "") {
      this._snackBar.open('Please fill out the form correctly !', 'X', {
        horizontalPosition: this.horizontalPosition,
        verticalPosition: this.verticalPosition,
        duration: 3000,
        panelClass: ['red-snackbar', 'login-snackbar'],
      });
    } else {
      this.loading = true;

      const params = new HttpParams()
        .set('accountType', this.transactionsReportForm.value.accountType)
        .set('branchCode', this.transactionsReportForm.value.branchCode)
        .set('fromdate', fromDate)
        .set('todate', toDate);

      this.subscription = this.reportsService
        .generateBranchAccountTypesTransactions(params)
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

  generateAllCustomerTypesAccountTypesTransactions() {
    let fromDate = this.datepipe.transform(
      this.transactionsReportForm.value.fromdate,
      'yyyy-MM-ddTHH:mm:ss'
    );

    let toDate = this.datepipe.transform(
      this.transactionsReportForm.value.todate,
      'yyyy-MM-ddTHH:mm:ss'
    );

    if (this.transactionsReportForm.value.accountType == "" || this.transactionsReportForm.value.customerCode == "") {
      this._snackBar.open('Please fill out the form correctly !', 'X', {
        horizontalPosition: this.horizontalPosition,
        verticalPosition: this.verticalPosition,
        duration: 3000,
        panelClass: ['red-snackbar', 'login-snackbar'],
      });
    } else {
      this.loading = true;

      const params = new HttpParams()
        .set('accountType', this.transactionsReportForm.value.accountType)
        .set('customerCode', this.transactionsReportForm.value.customerCode)
        .set('fromdate', fromDate)
        .set('todate', toDate);

      this.subscription = this.reportsService
        .generateAllCustomerTypesAccountTypesTransactions(params)
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
