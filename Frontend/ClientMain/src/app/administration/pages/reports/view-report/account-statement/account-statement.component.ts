import { LoansProductLookUpComponent } from './../../../ProductModule/loans-product/loans-product-look-up/loans-product-look-up.component';
import { DatePipe } from '@angular/common';
import { HttpParams } from '@angular/common/http';
import { Component, Inject, OnInit } from '@angular/core';
import {
  FormGroup,
  UntypedFormBuilder,
  Validators
} from '@angular/forms';
import {
  MatDialog,
  MatDialogConfig,
  MatDialogRef,
  MAT_DIALOG_DATA
} from '@angular/material/dialog';
import {
  MatSnackBar,
  MatSnackBarHorizontalPosition,
  MatSnackBarVerticalPosition
} from '@angular/material/snack-bar';
import { Subscription } from 'rxjs';
import { AuthService } from 'src/@core/AuthService/auth.service';
import { ReportsService } from 'src/app/administration/Service/reports/reports.service';
import { GeneralAccountsLookupComponent } from '../../../Account-Component/general-accounts-lookup/general-accounts-lookup.component';
import { SolCodeLookupComponent } from '../../accounts-lookup/sol-code-lookup/sol-code-lookup.component';
import { ViewReportComponent } from '../view-report.component';
import { GeneralProductLookUpComponent } from '../../../ProductModule/general-product-look-up/general-product-look-up.component';

@Component({
  selector: 'app-account-statement',
  templateUrl: './account-statement.component.html',
  styleUrls: ['./account-statement.component.scss'],
})
export class AccountStatementComponent implements OnInit {
  accountStatementForm: FormGroup;
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
  types: any = ['Retail Accounts', 'Cooperate Accounts'];
  scope: any = ['All Accounts', 'Branch Accounts'];
  statuses: any[] = ['Active', 'Dormant', 'Frozen', 'Suspended'];
  users: any[] = [];
  subscription!: Subscription;
  error: any;
  loading: boolean;
  accountTypesReports: boolean = true;
  accountStatetement: boolean = false;
  horizontalPosition: MatSnackBarHorizontalPosition = 'end';
  verticalPosition: MatSnackBarVerticalPosition = 'top';
  retailAccountReportsSelected: boolean = false;
  cooperateAccountReportsSelected: boolean = false;
  accountStatementSelected: boolean = true;
  branchSelected: boolean = false;
  accountByStatusSelected: boolean = false;
  producttransactions: boolean = false;
  reportTitle: string;
  isLoading: boolean = false;
  accountsBySchemeReport: boolean = false;
  accountByMemberReport: boolean = false;
  usertransactions: boolean = false;
  accountsByGroupMembershipReport: boolean = false;
  memberAccountTransactionsreport: boolean = false;
  productLoanDemandsReport: boolean = false;
  memberLoanDemandsReport: boolean = false;
  accountsByProductReport: boolean = false;
  customerTransactionsReport: boolean = false;
  schemeTransactionsReport: boolean = false;
  allTransactionsAnalysis: boolean = false;
  corporateAccountsReport: boolean;
  groupAccountsReport: boolean;

  constructor (
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
    this.reportTitle = this.data.reportTitle;
    this.retailAccountReportsSelected = this.data.action == 'Retail Accounts';
    this.cooperateAccountReportsSelected = this.data.action == 'Cooperate Accounts'
    this.accountByStatusSelected  = this.data.action == 'Accounts By Status';
    this.usertransactions  = this.data.action == 'usertransactions';
    this.memberLoanDemandsReport  = this.data.action == 'memberLoanDemandsReport';
    this.customerTransactionsReport  = this.data.action == 'customerTransactionsReport';
    this.producttransactions  = this.data.action == 'producttransactions';
    this.productLoanDemandsReport  = this.data.action == 'productLoanDemandsReport';
    this.schemeTransactionsReport  = this.data.action == 'schemeTransactionsReport';
    this.allTransactionsAnalysis  = this.data.action == 'allTransactionsAnalysis';
    this.accountByStatusSelected  = this.data.action == '';
    this.accountStatementSelected = this.data.action == 'Account Statements';
    this.accountsBySchemeReport  = this.data.action == 'accountsBySchemeReport';
    this.producttransactions  = this.data.action == 'producttransactions';
    this.accountByMemberReport  = this.data.action == 'accountByMemberReport';
    this.accountsByGroupMembershipReport  = this.data.action == 'accountsByGroupMembershipReport';
    this.accountsByProductReport  = this.data.action == 'accountsByProductReport';
    this.corporateAccountsReport  = this.data.action == 'corporateAccountsReport';
    this.groupAccountsReport  = this.data.action == 'groupAccountsReport';
    this.memberAccountTransactionsreport  = this.data.action == 'memberAccountTransactionsreport';
    this.accountStatementForm = this.initAccountStatementForm();
    this.accountStatementForm.patchValue({
      reportType: this.data.action,
    });
    this.getData();
  }


  submit() {
    if (
      this.accountStatementForm.value.type == 'All Accounts' &&
      this.accountStatementForm.value.reportType == 'Retail Accounts'
    ) {
      this.generateAllRetailAccountsByAccountType();
    } else if (
      this.accountStatementForm.value.type == 'All Accounts' &&
      this.accountStatementForm.value.reportType == 'Cooperate Accounts'
    ) {
      this.generateAllCooperateAccountsByTypeReport();
    } else if (
      this.accountStatementForm.value.type == 'All Accounts' &&
      this.accountStatementForm.value.reportType == 'Accounts By Status'
    ) {
      this.generateAllActiveDormantAccountsReport();
    } else if (
      this.accountStatementForm.value.type == 'Branch Accounts' &&
      this.accountStatementForm.value.reportType == 'Retail Accounts'
    ) {
      this.generateAllBranchRetailAccountsByAccountType();
    } else if (
      this.accountStatementForm.value.type == 'Branch Accounts' &&
      this.accountStatementForm.value.reportType == 'Cooperate Accounts'
    ) {
      this.generateBranchCorporateAccountsByAccountType();
    } else if (
      this.accountStatementForm.value.type == 'Branch Accounts' &&
      this.accountStatementForm.value.reportType == 'Accounts By Status'
    ) {
      this.generateBranchActiveDormantAccountsReport();
    } else if(this.accountByMemberReport) {
      this.generateccountByMemberReport();
    } else if(this.accountsByGroupMembershipReport) {
      this.generateAccountsByGroupMembershipReport();
    } else if(this.accountsBySchemeReport) {
      this.generateAccountsBySchemeReport();
    }else if(this.accountsByProductReport) {
      this.generateAccountsByProductReport();
    }else if(this.usertransactions) {
      this.generateUsertransactions();
    }else if(this.producttransactions) {
      this.generateProducttransactions();
    }else if(this.memberAccountTransactionsreport) {
      this.generateMemberAccountTransactionsreport();
    }else if(this.memberLoanDemandsReport) {
      this.generateMemberLoanDemandsReport();
    }else if(this.productLoanDemandsReport) {
      this.generateProductLoanDemandsReport();
    }else if(this.customerTransactionsReport) {
      this.generateCustomerTransactionsReport();
    }else if(this.allTransactionsAnalysis) {
      this.generateAllTransactionsAnalysis();
    }else if(this.schemeTransactionsReport) {
      this.generateSchemeTransactionsReport();
    }
    else {
      this.generateReport();
    }
  }

  generateAllTransactionsAnalysis() {
    if (
      false
    ) {
      this.fillFormCorrectly();
    } else {
      this.loading = true;
      const params = new HttpParams()
      .set('fromdate', this.getFromDate())
      .set('todate', this.getToDate())
        this.generateReportDynamically(params, this.reportsService.generateAllTransactionsAnalysis, "TransactionAnalysis");
    }
  }

  generateSchemeTransactionsReport() {
    if (
      this.accountStatementForm.value.accountType == ''
    ) {
      this.fillFormCorrectly();
    } else {
      this.loading = true;
      const params = new HttpParams()
      .set('fromdate', this.getFromDate())
      .set('todate', this.getToDate())
      .set('accountType', this.accountStatementForm.value.accountType);
        this.generateReportDynamically(params, this.reportsService.generateSchemeTransactionsReport, "SchemeTransactionAnalysis");
    }
  }

  generateProductLoanDemandsReport() {
    if (
      this.accountStatementForm.value.productCode == ''
    ) {
      this.fillFormCorrectly();
    } else {
      this.loading = true;
      const params = new HttpParams()
      .set('fromdate', this.getFromDate())
      .set('todate', this.getToDate())
      .set('productCode', this.accountStatementForm.value.productCode)
        this.generateReportDynamically(params, this.reportsService.productLoanDemandsReport, "DemandsByCustomerCode");
    }
  }

  generateMemberLoanDemandsReport() {
    if (
      this.accountStatementForm.value.customerCode == ''
    ) {
      this.fillFormCorrectly();
    } else {
      this.loading = true;
      const params = new HttpParams()
      .set('fromdate', this.getFromDate())
      .set('todate', this.getToDate())
      .set('customerCode', this.accountStatementForm.value.customerCode)
        this.generateReportDynamically(params, this.reportsService.memberLoanDemandsReport, "DemandsByCustomerCode");
    }
  }

  generateCustomerTransactionsReport() {
    if (
      this.accountStatementForm.value.customerCode == ''
    ) {
      this.fillFormCorrectly();
    } else {
      this.loading = true;
      const params = new HttpParams()
      .set('fromdate', this.getFromDate())
      .set('todate', this.getToDate())
      .set('customerCode', this.accountStatementForm.value.customerCode)
        this.generateReportDynamically(params, this.reportsService.customerTransactionsReport, "TransactionsAnalysisByCustomerCode");
    }
  }


  getData() {
    this.subscription = this.authService.allUsers().subscribe(
      (res) => {
        console.log(res);
        this.users = res;
        this.users.push({username: "Migrator"});
      },
      (err) => {
      }
    );
  }

  initAccountStatementForm(): FormGroup {
    return this.fb.group({
      reportType: ['', [Validators.required]],
      type: [''],
      acid: [''],
      customerCode: [''],
      productCode: [''],
      groupCode: [''],
      username: [''],
      accountStatus: [''],
      accountType: [''],
      branchCode: [''],
      fromdate: ['2010-01-01', [Validators.required]],
      todate: ['2024-01-01', [Validators.required]],
    });
  }

  schemeCodeLookup(): void {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.disableClose = false;
    dialogConfig.autoFocus = true;
    dialogConfig.width = '600px';
    dialogConfig.data = {
      data: '',
    };
    const dialogRef = this.dialog.open(
      GeneralProductLookUpComponent,
      dialogConfig
    );
    dialogRef.afterClosed().subscribe((result) => {
      console.log(result);
      this.accountStatementForm.controls.productCode.setValue(
        result.data.productCode
      );
    });
  }

  accountLookup() {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.disableClose = false;
    dialogConfig.autoFocus = true;
    dialogConfig.width = '950px';
    dialogConfig.data = {
      data: '',
    };
    const dialogRef = this.dialog.open(GeneralAccountsLookupComponent, dialogConfig);
    dialogRef.afterClosed().subscribe((result) => {
      this.accountStatementForm.patchValue({ acid: result.data.acid });
    });
  }

  specifyReportScope(event: any) {
    if (event.target.value == 'Branch Accounts') {
      this.branchSelected = true;
    } else {
      this.branchSelected = false;
    }
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
      this.accountStatementForm.patchValue({
        branchCode: result.data.branchCode,
      });
    });
  }

  generateReport() {
    let fromDate = this.datepipe.transform(
      this.accountStatementForm.value.fromdate,
      'yyyy-MM-ddTHH:mm:ss'
    );

    let toDate = this.datepipe.transform(
      this.accountStatementForm.value.todate,
      'yyyy-MM-ddTHH:mm:ss'
    );

    if (this.accountStatementForm.value.acid == '') {
      this._snackBar.open('Please fill out the form correctly !', 'X', {
        horizontalPosition: this.horizontalPosition,
        verticalPosition: this.verticalPosition,
        duration: 3000,
        panelClass: ['red-snackbar', 'login-snackbar'],
      });
    } else {
      this.loading = true;

      const params = new HttpParams()
        .set('acid', this.accountStatementForm.value.acid)
        .set('fromdate', fromDate)
        .set('todate', toDate);

      this.loading = true;

      this.subscription = this.reportsService
        .generateAccountStatementReport(params)
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

  generateAllCooperateAccountsByTypeReport() {
    let fromDate = this.datepipe.transform(
      this.accountStatementForm.value.fromdate,
      'yyyy-MM-ddTHH:mm:ss'
    );

    let toDate = this.datepipe.transform(
      this.accountStatementForm.value.todate,
      'yyyy-MM-ddTHH:mm:ss'
    );

    if (
      this.accountStatementForm.value.accountType == '' ||
      this.accountStatementForm.value.type == ''
    ) {
      this._snackBar.open('Please fill out the form correctly !', 'X', {
        horizontalPosition: this.horizontalPosition,
        verticalPosition: this.verticalPosition,
        duration: 3000,
        panelClass: ['red-snackbar', 'login-snackbar'],
      });
    } else {
      this.loading = true;

      const params = new HttpParams()
        .set('accountType', this.accountStatementForm.value.accountType)
        .set('fromdate', fromDate)
        .set('todate', toDate);

      this.subscription = this.reportsService
        .generateAllCooperateAccountsByTypeReport(params)
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

  generateBranchCorporateAccountsByAccountType() {
    let fromDate = this.datepipe.transform(
      this.accountStatementForm.value.fromdate,
      'yyyy-MM-ddTHH:mm:ss'
    );

    let toDate = this.datepipe.transform(
      this.accountStatementForm.value.todate,
      'yyyy-MM-ddTHH:mm:ss'
    );

    if (
      this.accountStatementForm.value.accountType == '' ||
      this.accountStatementForm.value.type == '' ||
      this.accountStatementForm.value.branchCode == ''
    ) {
      this._snackBar.open('Please fill out the form correctly !', 'X', {
        horizontalPosition: this.horizontalPosition,
        verticalPosition: this.verticalPosition,
        duration: 3000,
        panelClass: ['red-snackbar', 'login-snackbar'],
      });
    } else {
      this.loading = true;

      const params = new HttpParams()
        .set('accountType', this.accountStatementForm.value.accountType)
        .set('branchCode', this.accountStatementForm.value.branchCode)
        .set('fromdate', fromDate)
        .set('todate', toDate);

      this.subscription = this.reportsService
        .generateBranchCorporateAccountsByAccountType(params)
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

  generateAllRetailAccountsByAccountType() {
    let fromDate = this.datepipe.transform(
      this.accountStatementForm.value.fromdate,
      'yyyy-MM-ddTHH:mm:ss'
    );

    let toDate = this.datepipe.transform(
      this.accountStatementForm.value.todate,
      'yyyy-MM-ddTHH:mm:ss'
    );

    if (
      this.accountStatementForm.value.accountType == '' ||
      this.accountStatementForm.value.type == ''
    ) {
      this._snackBar.open('Please fill out the form correctly !', 'X', {
        horizontalPosition: this.horizontalPosition,
        verticalPosition: this.verticalPosition,
        duration: 3000,
        panelClass: ['red-snackbar', 'login-snackbar'],
      });
    } else {
      this.loading = true;

      const params = new HttpParams()
        .set('accountType', this.accountStatementForm.value.accountType)
        .set('fromdate', fromDate)
        .set('todate', toDate);

      this.subscription = this.reportsService
        .generateAllRetailAccountsByAccountType(params)
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

  generateAllBranchRetailAccountsByAccountType() {
    let fromDate = this.datepipe.transform(
      this.accountStatementForm.value.fromdate,
      'yyyy-MM-ddTHH:mm:ss'
    );

    let toDate = this.datepipe.transform(
      this.accountStatementForm.value.todate,
      'yyyy-MM-ddTHH:mm:ss'
    );

    if (
      this.accountStatementForm.value.accountType == '' ||
      this.accountStatementForm.value.type == '' ||
      this.accountStatementForm.value.branchCode == ''
    ) {
      this._snackBar.open('Please fill out the form correctly !', 'X', {
        horizontalPosition: this.horizontalPosition,
        verticalPosition: this.verticalPosition,
        duration: 3000,
        panelClass: ['red-snackbar', 'login-snackbar'],
      });
    } else {
      this.loading = true;

      const params = new HttpParams()
        .set('accountType', this.accountStatementForm.value.accountType)
        .set('branchCode', this.accountStatementForm.value.branchCode)
        .set('fromdate', fromDate)
        .set('todate', toDate);

      this.subscription = this.reportsService
        .generateAllBranchRetailAccountsByAccountType(params)
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

  generateAllActiveDormantAccountsReport() {
    let fromDate = this.datepipe.transform(
      this.accountStatementForm.value.fromdate,
      'yyyy-MM-ddTHH:mm:ss'
    );

    let toDate = this.datepipe.transform(
      this.accountStatementForm.value.todate,
      'yyyy-MM-ddTHH:mm:ss'
    );

    if (
      this.accountStatementForm.value.accountType == '' ||
      this.accountStatementForm.value.type == '' ||
      this.accountStatementForm.value.accountStatus == ''
    ) {
      this._snackBar.open('Please fill out the form correctly !', 'X', {
        horizontalPosition: this.horizontalPosition,
        verticalPosition: this.verticalPosition,
        duration: 3000,
        panelClass: ['red-snackbar', 'login-snackbar'],
      });
    } else {
      this.loading = true;

      const params = new HttpParams()
        .set('accountType', this.accountStatementForm.value.accountType)
        .set('accountStatus', this.accountStatementForm.value.accountStatus)
        .set('fromdate', fromDate)
        .set('todate', toDate);

      this.subscription = this.reportsService
        .generateAllActiveDormantAccountsReport(params)
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

  generateBranchActiveDormantAccountsReport() {
    let fromDate = this.datepipe.transform(
      this.accountStatementForm.value.fromdate,
      'yyyy-MM-ddTHH:mm:ss'
    );

    let toDate = this.datepipe.transform(
      this.accountStatementForm.value.todate,
      'yyyy-MM-ddTHH:mm:ss'
    );

    if (
      this.accountStatementForm.value.accountType == '' ||
      this.accountStatementForm.value.type == '' ||
      this.accountStatementForm.value.accountStatus == '' ||
      this.accountStatementForm.value.branchCode == ''
    ) {
      this._snackBar.open('Please fill out the form correctly !', 'X', {
        horizontalPosition: this.horizontalPosition,
        verticalPosition: this.verticalPosition,
        duration: 3000,
        panelClass: ['red-snackbar', 'login-snackbar'],
      });
    } else {
      this.loading = true;

      const params = new HttpParams()
        .set('accountType', this.accountStatementForm.value.accountType)
        .set('accountStatus', this.accountStatementForm.value.accountStatus)
        .set('branchCode', this.accountStatementForm.value.branchCode)
        .set('fromdate', fromDate)
        .set('todate', toDate);

      this.subscription = this.reportsService
        .generateBranchActiveDormantAccountsReport(params)
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

  generateAccountsByGroupMembershipReport() {
    if (
      this.accountStatementForm.value.groupCode == '' ||
      this.accountStatementForm.value.productCode == ''
    ) {
      this.fillFormCorrectly();
    } else {
      this.loading = true;
      const params = new HttpParams()
        .set('groupCode', this.accountStatementForm.value.groupCode)
        .set('productCode', this.accountStatementForm.value.productCode)
        this.generateReportDynamically(params, this.reportsService.accountsByGroupMembershipReport, "AccountsByGroupMembership");
    }
  }

  generateAccountsByProductReport() {
    if (
      this.accountStatementForm.value.productCode == ''
    ) {
      this.fillFormCorrectly();
    } else {
      this.loading = true;
      const params = new HttpParams()
      .set('fromdate', this.getFromDate())
      .set('solCode', this.accountStatementForm.value.branchCode)
      .set('todate', this.getToDate())
      .set('productCode', this.accountStatementForm.value.productCode)
        this.generateReportDynamically(params, this.reportsService.accountsByProductReport, "AccountsByProductCode");
    }
  }

  generateUsertransactions() {
    if (
      this.accountStatementForm.value.username == ''
    ) {
      this.fillFormCorrectly();
    } else {
      this.loading = true;
      const params = new HttpParams()
      .set('fromdate', this.getFromDate())
      .set('todate', this.getToDate())
      .set('user', this.accountStatementForm.value.username)
        this.generateReportDynamically(params, this.reportsService.usertransactions, "Usertransactions");
    }
  }


  generateProducttransactions() {
    if (
      this.accountStatementForm.value.productCode == ''
    ) {
      this.fillFormCorrectly();
    } else {
      this.loading = true;
      const params = new HttpParams()
      .set('fromdate', this.getFromDate())
      .set('todate', this.getToDate())
      .set('product_code', this.accountStatementForm.value.productCode)
        this.generateReportDynamically(params, this.reportsService.analysisproducttransactions, "Usertransactions");
    }
  }



  generateMemberAccountTransactionsreport() {
    if (
      this.accountStatementForm.value.account_type == '' || this.accountStatementForm.value.customerCode == ''
    ) {
      this.fillFormCorrectly();
    } else {
      this.loading = true;
      const params = new HttpParams()
      .set('fromdate', this.getFromDate())
      .set('todate', this.getToDate())
      .set('accountType', this.accountStatementForm.value.accountType)
      .set('customerCode', this.accountStatementForm.value.customerCode)
        this.generateReportDynamically(params, this.reportsService.memberAccountTransactionsreport, "memberAccountTransactions");
    }
  }



  generateAccountsBySchemeReport() {
    if (
      false
    ) {
      this.fillFormCorrectly();
    } else {
      this.loading = true;
      const params = new HttpParams()
      .set('fromdate', this.getFromDate())
      .set('todate', this.getToDate())
      .set('accountType',this.accountStatementForm.value.accountType);

        this.generateReportDynamically(params, this.reportsService.accountsBySchemeReport, "AccountsByGroupScheme");
    }
  }

  generateccountByMemberReport() {
    if (
      this.accountStatementForm.value.customerCode == ''
    ) {
      this.fillFormCorrectly();
    } else {
      this.loading = true;
      const params = new HttpParams()
        .set('customerCode', this.accountStatementForm.value.customerCode)
        this.generateReportDynamically(params, this.reportsService.getMemberAccounts, "MyAccounts");
    }
  }

  cancel() {
    this.dialogRef.close();
  }


  getFromDate(){
    return this.datepipe.transform(
      this.accountStatementForm.value.fromdate,
      'yyyy-MM-ddTHH:mm:ss'
    );
  }

  getToDate(){
    return this.datepipe.transform(
      this.accountStatementForm.value.todate,
      'yyyy-MM-ddTHH:mm:ss'
    );
  }

  fillFormCorrectly() {
    this._snackBar.open('Please fill out the form correctly !', 'X', {
      horizontalPosition: this.horizontalPosition,
      verticalPosition: this.verticalPosition,
      duration: 3000,
      panelClass: ['red-snackbar', 'login-snackbar'],
    });
  }

  generateReportDynamically(params, url, filename) {
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

}
