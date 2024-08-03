import { DatePipe } from '@angular/common';
import { HttpParams } from '@angular/common/http';
import { Component, Inject, OnInit } from '@angular/core';
import { FormGroup, UntypedFormBuilder, Validators } from '@angular/forms';
import {
  MatDialog,
  MatDialogRef,
  MAT_DIALOG_DATA,
  MatDialogConfig,
} from '@angular/material/dialog';
import {
  MatSnackBarHorizontalPosition,
  MatSnackBarVerticalPosition,
  MatSnackBar,
} from '@angular/material/snack-bar';
import { Subscription } from 'rxjs';
import { AuthService } from 'src/@core/AuthService/auth.service';
import { ReportsService } from 'src/app/administration/Service/reports/reports.service';
import { AccountsLookupComponent } from '../../accounts-lookup/accounts-lookup.component';
import { SolCodeLookupComponent } from '../../accounts-lookup/sol-code-lookup/sol-code-lookup.component';
import { ViewReportComponent } from '../../view-report/view-report.component';

@Component({
  selector: 'app-sasra-report-dialogue',
  templateUrl: './sasra-report-dialogue.component.html',
  styleUrls: ['./sasra-report-dialogue.component.scss'],
})
export class SasraReportDialogueComponent implements OnInit {
  arrearsRepotForm: FormGroup;
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
  users: any[] = [];
  horizontalPosition: MatSnackBarHorizontalPosition = 'end';
  verticalPosition: MatSnackBarVerticalPosition = 'top';
  adequacyReport: boolean = false;
  statementComprehensiveReport: boolean = false;
  statementOfFinancialPositionSelected: boolean = false;
  investmentReturnReportSelected: boolean = false;
  consolidatedDailyLiquidityReportselected: boolean = false;
  statementofDepositReturnSelected: boolean = false;
  riskClassificationReportSelected: boolean = false;
  liquiditystatementSelected: boolean = false;
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

    if (this.data.action == 'Adequacy Report') {
      this.adequacyReport = true;
      this.statementComprehensiveReport = false;
      this.statementOfFinancialPositionSelected = false;
      this.investmentReturnReportSelected = false;
      this.consolidatedDailyLiquidityReportselected = false;
      this.statementofDepositReturnSelected = false;
      this.riskClassificationReportSelected = false;
      this.liquiditystatementSelected = false;
    } else if (this.data.action == 'Statement Comprehensive Report') {
      this.adequacyReport = false;
      this.statementComprehensiveReport = true;
      this.statementOfFinancialPositionSelected = false;
      this.investmentReturnReportSelected = false;
      this.consolidatedDailyLiquidityReportselected = false;
      this.statementofDepositReturnSelected = false;
      this.riskClassificationReportSelected = false;
      this.liquiditystatementSelected = false;
    } else if (this.data.action == 'Statement of Finacial Position') {
      this.adequacyReport = false;
      this.statementComprehensiveReport = false;
      this.statementOfFinancialPositionSelected = true;
      this.investmentReturnReportSelected = false;
      this.consolidatedDailyLiquidityReportselected = false;
      this.statementofDepositReturnSelected = false;
      this.riskClassificationReportSelected = false;
      this.liquiditystatementSelected = false;
    } else if (this.data.action == 'Investment Return Report') {
      this.adequacyReport = false;
      this.statementComprehensiveReport = false;
      this.statementOfFinancialPositionSelected = false;
      this.investmentReturnReportSelected = true;
      this.consolidatedDailyLiquidityReportselected = false;
      this.statementofDepositReturnSelected = false;
      this.riskClassificationReportSelected = false;
      this.liquiditystatementSelected = false;
    } else if (this.data.action == 'Consolidated Daily Liquidity Report') {
      this.adequacyReport = false;
      this.statementComprehensiveReport = false;
      this.statementOfFinancialPositionSelected = false;
      this.investmentReturnReportSelected = false;
      this.consolidatedDailyLiquidityReportselected = true;
      this.statementofDepositReturnSelected = false;
      this.riskClassificationReportSelected = false;
      this.liquiditystatementSelected = false;
    } else if (this.data.action == 'Statement of Deposit Return') {
      this.adequacyReport = false;
      this.statementComprehensiveReport = false;
      this.statementOfFinancialPositionSelected = false;
      this.investmentReturnReportSelected = false;
      this.consolidatedDailyLiquidityReportselected = false;
      this.statementofDepositReturnSelected = true;
      this.riskClassificationReportSelected = false;
      this.liquiditystatementSelected = false;
    } else if (this.data.action == 'Risk Classification Report') {
      this.adequacyReport = false;
      this.statementComprehensiveReport = false;
      this.statementOfFinancialPositionSelected = false;
      this.investmentReturnReportSelected = false;
      this.consolidatedDailyLiquidityReportselected = false;
      this.statementofDepositReturnSelected = false;
      this.riskClassificationReportSelected = true;
      this.liquiditystatementSelected = false;
    }else if (this.data.action == 'Liquidity Statement') {
      this.adequacyReport = false;
      this.statementComprehensiveReport = false;
      this.statementOfFinancialPositionSelected = false;
      this.investmentReturnReportSelected = false;
      this.consolidatedDailyLiquidityReportselected = false;
      this.statementofDepositReturnSelected = false;
      this.riskClassificationReportSelected = false;
      this.liquiditystatementSelected = true;
    } else {
      this.adequacyReport = false;
      this.statementComprehensiveReport = false;
      this.statementOfFinancialPositionSelected = false;
      this.investmentReturnReportSelected = true;
      this.consolidatedDailyLiquidityReportselected = false;
      this.statementofDepositReturnSelected = false;
      this.riskClassificationReportSelected = false;
      this.liquiditystatementSelected = false;
    }

    this.getData();

    this.arrearsRepotForm = this.initArrearsReportForm();
  }

  initArrearsReportForm(): FormGroup {
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
      this.arrearsRepotForm.patchValue({ acid: result.data.acid });
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
      this.arrearsRepotForm.patchValue({ branchCode: result.data.branchCode });
    });
  }

  submit() {
    if (this.adequacyReport) {
      this.generateCapitalAdequacyReport();
    } else if (this.statementComprehensiveReport) {
      this.generateStatementComprehensiveReport();
    } else if (this.statementOfFinancialPositionSelected) {
      this.generateStatementofFinancialPositionReport();
    } else if (this.consolidatedDailyLiquidityReportselected) {
      this.generateConsolidatedDailyLiquidityReport();
    } else if (this.statementofDepositReturnSelected) {
      this.generateDepositReturnReport();
    } else if (this.riskClassificationReportSelected) {
      this.generateRiskClassificationReport();
    } else if (this.investmentReturnReportSelected){
      this.generateInvestmentReturnReport();
    }else if (this.liquiditystatementSelected){
      this.generateLiquidityReport();
    }  else {
      this.generateInvestmentReturnReport();
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

  generateCapitalAdequacyReport() {
    this.loading = true;
    let fromDate = this.datepipe.transform(
      this.arrearsRepotForm.value.fromdate,
      'yyyy-MM-ddTHH:mm:ss'
    );

    let toDate = this.datepipe.transform(
      this.arrearsRepotForm.value.todate,
      'yyyy-MM-ddTHH:mm:ss'
    );

    const params = new HttpParams()
      .set('fromdate', fromDate)
      .set('todate', toDate);

    this.subscription = this.reportsService
      .generateCapitalAdequacyReport(params)
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

  generateStatementComprehensiveReport() {
    this.loading = true;

    let fromDate = this.datepipe.transform(
      this.arrearsRepotForm.value.fromdate,
      'yyyy-MM-ddTHH:mm:ss'
    );

    let toDate = this.datepipe.transform(
      this.arrearsRepotForm.value.todate,
      'yyyy-MM-ddTHH:mm:ss'
    );

    const params = new HttpParams()
      .set('fromdate', fromDate)
      .set('todate', toDate);

    this.subscription = this.reportsService
      .generateStatementComprehensiveReport(params)
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

  generateStatementofFinancialPositionReport() {
    this.loading = true;

    let fromDate = this.datepipe.transform(
      this.arrearsRepotForm.value.fromdate,
      'yyyy-MM-ddTHH:mm:ss'
    );

    let toDate = this.datepipe.transform(
      this.arrearsRepotForm.value.todate,
      'yyyy-MM-ddTHH:mm:ss'
    );

    const params = new HttpParams()
      .set('fromdate', fromDate)
      .set('todate', toDate);

    this.subscription = this.reportsService
      .generateStatementofFinancialPositionReport(params)
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

  generateInvestmentReturnReport() {
    this.loading = true;

    let fromDate = this.datepipe.transform(
      this.arrearsRepotForm.value.fromdate,
      'yyyy-MM-ddTHH:mm:ss'
    );

    let toDate = this.datepipe.transform(
      this.arrearsRepotForm.value.todate,
      'yyyy-MM-ddTHH:mm:ss'
    );

    const params = new HttpParams()
      .set('fromdate', fromDate)
      .set('todate', toDate);

    this.subscription = this.reportsService
      .generateInvestmentReturnReport(params)
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

  generateConsolidatedDailyLiquidityReport() {
    this.loading = true;

    let fromDate = this.datepipe.transform(
      this.arrearsRepotForm.value.fromdate,
      'yyyy-MM-ddTHH:mm:ss'
    );

    let toDate = this.datepipe.transform(
      this.arrearsRepotForm.value.todate,
      'yyyy-MM-ddTHH:mm:ss'
    );

    const params = new HttpParams()
      .set('fromdate', fromDate)
      .set('todate', toDate);

    this.subscription = this.reportsService
      .generateConsolidatedDailyLiquidityReport(params)
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

  generateDepositReturnReport() {
    this.loading = true;

    let fromDate = this.datepipe.transform(
      this.arrearsRepotForm.value.fromdate,
      'yyyy-MM-ddTHH:mm:ss'
    );

    let toDate = this.datepipe.transform(
      this.arrearsRepotForm.value.todate,
      'yyyy-MM-ddTHH:mm:ss'
    );

    const params = new HttpParams()
      .set('fromdate', fromDate)
      .set('todate', toDate);

    this.subscription = this.reportsService
      .generateDepositReturnReport(params)
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

  generateRiskClassificationReport() {
    this.loading = true;

    let fromDate = this.datepipe.transform(
      this.arrearsRepotForm.value.fromdate,
      'yyyy-MM-ddTHH:mm:ss'
    );

    let toDate = this.datepipe.transform(
      this.arrearsRepotForm.value.todate,
      'yyyy-MM-ddTHH:mm:ss'
    );

    const params = new HttpParams()
      .set('fromdate', fromDate)
      .set('todate', toDate);

    this.subscription = this.reportsService
      .generateRiskClassificationReport(params)
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

  generateLiquidityReport() {
    this.loading = true;
    
    let fromDate = this.datepipe.transform(
      this.arrearsRepotForm.value.fromdate,
      'yyyy-MM-ddTHH:mm:ss'
    );

    let toDate = this.datepipe.transform(
      this.arrearsRepotForm.value.todate,
      'yyyy-MM-ddTHH:mm:ss'
    );

    const params = new HttpParams()
      .set('fromdate', fromDate)
      .set('todate', toDate);

    this.subscription = this.reportsService
      .generateLiquidityStatementReport(params)
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

  cancel() {
    this.dialogRef.close();
  }
}
