import { DatePipe } from '@angular/common';
import { HttpParams } from '@angular/common/http';
import { Component, Inject, OnInit } from '@angular/core';
import { FormGroup, UntypedFormBuilder, Validators } from '@angular/forms';
import {
  MatDialog, MatDialogConfig, MatDialogRef,
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
import { AccountsLookupComponent } from '../../accounts-lookup/accounts-lookup.component';
import { SolCodeLookupComponent } from '../../accounts-lookup/sol-code-lookup/sol-code-lookup.component';
import { ViewReportComponent } from '../view-report.component';

@Component({
  selector: 'app-customer-report',
  templateUrl: './customer-report.component.html',
  styleUrls: ['./customer-report.component.scss'],
})
export class CustomerReportComponent implements OnInit {
  saccoMemberForm: FormGroup;
  action: string;
  dialogTitle: string;
  schemeTypes: any = [
    { code: 'LAA', description: 'Loan Accounts' },
    { code: 'ODA', description: 'Overdraft Accounts' },
    { code: 'SBA', description: 'Savings Accounts' },
    { code: 'TDA', description: 'Term Deposits' },
    { code: 'CAA', description: 'Current Accounts' },
  ];
  // types: any = ["Retail Accounts", "Cooperate Accounts"];
  scope: any = ['All Customers', 'Branch Customers'];
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
  customerManagerSelected: boolean = false;
  reportTitle: string;
  getAllMembersReport: boolean = false;
  getListOfHousesReport: boolean = false;
  getMemberOfHouseReport: boolean = false;
  getCorporateCustomersReport: boolean = false;

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

    this.reportTitle = this.data.reportTitle;
    this.retailAccountReportsSelected = this.data.action == 'Retail Customers';
    this.cooperateAccountReportsSelected = this.data.action == 'Cooperate Customers';
    this.getAllMembersReport = this.data.action == 'getAllMembersReport';
    this.getListOfHousesReport = this.data.action == 'getListOfHousesReport';
    this.getMemberOfHouseReport = this.data.action == 'getMemberOfHouseReport';
    this.getCorporateCustomersReport = this.data.action == 'getCorporateCustomersReport';
    this.getData();

    this.saccoMemberForm = this.initsaccoMemberForm();

    this.saccoMemberForm.patchValue({
      reportType: this.data.action,
    });
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
      this.saccoMemberForm.value.fromdate,
      'yyyy-MM-ddTHH:mm:ss'
    );
  }

  getToDate(){
    return this.datepipe.transform(
      this.saccoMemberForm.value.todate,
      'yyyy-MM-ddTHH:mm:ss'
    );
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

  initsaccoMemberForm(): FormGroup {
    return this.fb.group({
      reportType: ['', [Validators.required]],
      type: [''],
      acid: [''],
      username: [''],
      branchCode: [''],
      groupCode: [''],
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
      this.saccoMemberForm.patchValue({ acid: result.data.acid });
    });
  }

  specifyReportScope(event: any) {
    console.log(event.target.value);

    if (event.target.value == 'Branch Customers') {
      this.branchSelected = true;
      this.customerManagerSelected = false;
    } else if (event.target.value == 'Customer Manager') {
      this.branchSelected = false;
      this.customerManagerSelected = true;
    } else {
      this.branchSelected = false;
      this.customerManagerSelected = false;
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
      this.saccoMemberForm.patchValue({
        branchCode: result.data.branchCode,
      });
    });
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

  generateReport() {
    this.loading = true;

    let fromDate = this.datepipe.transform(
      this.saccoMemberForm.value.fromdate,
      'yyyy-MM-ddTHH:mm:ss'
    );

    let toDate = this.datepipe.transform(
      this.saccoMemberForm.value.todate,
      'yyyy-MM-ddTHH:mm:ss'
    );

    if (
      this.saccoMemberForm.value.acid == '' ||
      this.saccoMemberForm.value.type == ''
    ) {
      this._snackBar.open('Please fill out the form correctly !', 'X', {
        horizontalPosition: this.horizontalPosition,
        verticalPosition: this.verticalPosition,
        duration: 3000,
        panelClass: ['red-snackbar', 'login-snackbar'],
      });
    } else {
      const params = new HttpParams()
        .set('acid', this.saccoMemberForm.value.acid)
        .set('fromdate', fromDate)
        .set('todate', toDate);

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

  generateAllCorporateCustomersReport() {
    let fromDate = this.datepipe.transform(
      this.saccoMemberForm.value.fromdate,
      'yyyy-MM-ddTHH:mm:ss'
    );

    let toDate = this.datepipe.transform(
      this.saccoMemberForm.value.todate,
      'yyyy-MM-ddTHH:mm:ss'
    );

    if (this.saccoMemberForm.value.type == '') {
      this._snackBar.open('Please fill out the form correctly !', 'X', {
        horizontalPosition: this.horizontalPosition,
        verticalPosition: this.verticalPosition,
        duration: 3000,
        panelClass: ['red-snackbar', 'login-snackbar'],
      });
    } else {
      this.loading = true;

      const params = new HttpParams()
        .set('fromdate', fromDate)
        .set('todate', toDate);

      this.subscription = this.reportsService
        .generateAllCorporateCustomersReport(params)
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

  generateBranchCorporateCustomersReport() {
    this.loading = true;

    let fromDate = this.datepipe.transform(
      this.saccoMemberForm.value.fromdate,
      'yyyy-MM-ddTHH:mm:ss'
    );

    let toDate = this.datepipe.transform(
      this.saccoMemberForm.value.todate,
      'yyyy-MM-ddTHH:mm:ss'
    );

    if (
      this.saccoMemberForm.value.type == '' ||
      this.saccoMemberForm.value.branchCode == ''
    ) {
      this._snackBar.open('Please fill out the form correctly !', 'X', {
        horizontalPosition: this.horizontalPosition,
        verticalPosition: this.verticalPosition,
        duration: 3000,
        panelClass: ['red-snackbar', 'login-snackbar'],
      });
    } else {
      const params = new HttpParams()
        .set('branchCode', this.saccoMemberForm.value.branchCode)
        .set('fromdate', fromDate)
        .set('todate', toDate);

      this.subscription = this.reportsService
        .generateBranchCorporateCustomersReport(params)
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

  generateRetailCustomersReport() {
    let fromDate = this.datepipe.transform(
      this.saccoMemberForm.value.fromdate,
      'yyyy-MM-ddTHH:mm:ss'
    );

    let toDate = this.datepipe.transform(
      this.saccoMemberForm.value.todate,
      'yyyy-MM-ddTHH:mm:ss'
    );

    if (this.saccoMemberForm.value.type == '') {
      this._snackBar.open('Please fill out the form correctly !', 'X', {
        horizontalPosition: this.horizontalPosition,
        verticalPosition: this.verticalPosition,
        duration: 3000,
        panelClass: ['red-snackbar', 'login-snackbar'],
      });
    } else {
      const params = new HttpParams()
        .set('fromdate', fromDate)
        .set('todate', toDate);

      this.subscription = this.reportsService
        .generateRetailCustomersReport(params)
        .subscribe(
          (response) => {
            this.loading = true;

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

  generateUserCorporateCustomersReport() {
    this.loading = true;

    let fromDate = this.datepipe.transform(
      this.saccoMemberForm.value.fromdate,
      'yyyy-MM-ddTHH:mm:ss'
    );

    let toDate = this.datepipe.transform(
      this.saccoMemberForm.value.todate,
      'yyyy-MM-ddTHH:mm:ss'
    );

    if (
      this.saccoMemberForm.value.type == '' ||
      this.saccoMemberForm.value.username
    ) {
      this._snackBar.open('Please fill out the form correctly !', 'X', {
        horizontalPosition: this.horizontalPosition,
        verticalPosition: this.verticalPosition,
        duration: 3000,
        panelClass: ['red-snackbar', 'login-snackbar'],
      });
    } else {
      const params = new HttpParams()
        .set('username', this.saccoMemberForm.value.username)
        .set('fromdate', fromDate)
        .set('todate', toDate);

      this.subscription = this.reportsService
        .generateUserCorporateCustomersReport(params)
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

  generateUserRetailCustomersReport() {
    let fromDate = this.datepipe.transform(
      this.saccoMemberForm.value.fromdate,
      'yyyy-MM-ddTHH:mm:ss'
    );

    let toDate = this.datepipe.transform(
      this.saccoMemberForm.value.todate,
      'yyyy-MM-ddTHH:mm:ss'
    );

    if (
      this.saccoMemberForm.value.type == '' ||
      this.saccoMemberForm.value.username == ''
    ) {
      this._snackBar.open('Please fill out the form correctly !', 'X', {
        horizontalPosition: this.horizontalPosition,
        verticalPosition: this.verticalPosition,
        duration: 3000,
        panelClass: ['red-snackbar', 'login-snackbar'],
      });
    } else {
      const params = new HttpParams()
        .set('username', this.saccoMemberForm.value.username)
        .set('fromdate', fromDate)
        .set('todate', toDate);

      this.subscription = this.reportsService
        .generateUserRetailCustomersReport(params)
        .subscribe(
          (response) => {
            this.loading = true;

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

  generateAllBranchRetailAccountsByAccountType() {
    let fromDate = this.datepipe.transform(
      this.saccoMemberForm.value.fromdate,
      'yyyy-MM-ddTHH:mm:ss'
    );

    let toDate = this.datepipe.transform(
      this.saccoMemberForm.value.todate,
      'yyyy-MM-ddTHH:mm:ss'
    );

    if (
      this.saccoMemberForm.value.type == '' ||
      this.saccoMemberForm.value.branchCode == ''
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
        .set('branchCode', this.saccoMemberForm.value.branchCode)
        .set('fromdate', fromDate)
        .set('todate', toDate);

      this.subscription = this.reportsService
        .generateBranchRetailCustomersReport(params)
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


  submit() {
    if (
      this.saccoMemberForm.value.type == 'All Customers' &&
      this.saccoMemberForm.value.reportType == 'Retail Customers'
    ) {
      this.generateRetailCustomersReport();
    } else if (
      this.saccoMemberForm.value.type == 'All Customers' &&
      this.saccoMemberForm.value.reportType == 'Cooperate Customers'
    ) {
      this.generateAllCorporateCustomersReport();
    } else if (
      this.saccoMemberForm.value.type == 'Branch Customers' &&
      this.saccoMemberForm.value.reportType == 'Retail Customers'
    ) {
      this.generateAllBranchRetailAccountsByAccountType();
    } else if (
      this.saccoMemberForm.value.type == 'Branch Customers' &&
      this.saccoMemberForm.value.reportType == 'Cooperate Customers'
    ) {
      this.generateBranchCorporateCustomersReport();
    } else if (
      this.saccoMemberForm.value.type == 'Customer Manager' &&
      this.saccoMemberForm.value.reportType == 'Cooperate Customers'
    ) {
      this.generateUserCorporateCustomersReport();
    } else if(this.getAllMembersReport){
      console.log("To all sacco members")
      this.generateAllMembersReport();
    }else if(this.getListOfHousesReport){
      this.generateListOfHousesReport();
    }else if(this.getMemberOfHouseReport){
      this.generateMemberOfHouseReport();
    }else if(this.getCorporateCustomersReport){
      this.generateCorporateCustomersReport();
    } else {
      console.log("To else statement");
      this.generateUserRetailCustomersReport();
    }
  }
  generateCorporateCustomersReport() {
    if (
      false
    ) {
      this.fillFormCorrectly();
    } else {
      this.loading = true;
      const params = new HttpParams()
      .set('fromdate', this.getFromDate())
      .set('todate', this.getToDate())

        this.generateReportDynamically(params, this.reportsService.getCorporateCustomersReport, "ListOfCorporatesReport");
    }
  }
  generateAllMembersReport() {

    if (
      false
    ) {
      this.fillFormCorrectly();
    } else {
      this.loading = true;
      const params = new HttpParams()
      .set('fromdate', this.getFromDate())
      .set('todate', this.getToDate())

        this.generateReportDynamically(params, this.reportsService.getAllMembersReport, "AllMembersReport");
    }
  }
  generateListOfHousesReport() {
    if (
      false
    ) {
      this.fillFormCorrectly();
    } else {
      this.loading = true;
      const params = new HttpParams()
      .set('fromdate', this.getFromDate())
      .set('todate', this.getToDate())

        this.generateReportDynamically(params, this.reportsService.getListOfHousesReport, "ListOfHousesReport");
    }
  }
  generateMemberOfHouseReport() {
    if (
      this.saccoMemberForm.value.groupCode == ''
    ) {
      this.fillFormCorrectly();
    } else {
      this.loading = true;
      const params = new HttpParams()
      .set('fromdate', this.getFromDate())
      .set('todate', this.getToDate())
      .set('groupCode', this.saccoMemberForm.value.groupCode )

        this.generateReportDynamically(params, this.reportsService.getMemberOfHouseReport, "getMemberOfHouseReport");
    }
  }

  cancel() {
    this.dialogRef.close();
  }
}
