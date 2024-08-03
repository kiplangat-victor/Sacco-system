import { DatePipe } from '@angular/common';
import { HttpParams } from '@angular/common/http';
import { Component, Inject, OnInit } from '@angular/core';
import { FormGroup, UntypedFormBuilder } from '@angular/forms';
import { MatDialog, MatDialogConfig, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatSnackBar, MatSnackBarHorizontalPosition, MatSnackBarVerticalPosition } from '@angular/material/snack-bar';
import { Subject, Subscription } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { AuthService } from 'src/@core/AuthService/auth.service';
import { ReportsService } from 'src/app/administration/Service/reports/reports.service';
import { WelfareService } from 'src/app/administration/Service/Welfare/welfare.service';
import { AccountUsersComponent } from '../../Account-Component/account-users/account-users.component';
import { GeneralAccountsLookupComponent } from '../../Account-Component/general-accounts-lookup/general-accounts-lookup.component';
import { GeneralProductLookUpComponent } from '../../ProductModule/general-product-look-up/general-product-look-up.component';
import { GlCodeLookupComponent } from '../../SystemConfigurations/GlobalParams/gl-code/gl-code-lookup/gl-code-lookup.component';
import { GlSubheadLookupComponent } from '../../SystemConfigurations/GlobalParams/gl-subhead/gl-subhead-lookup/gl-subhead-lookup.component';
import { MembershipConfigService } from '../../SystemConfigurations/GlobalParams/membership-config/membership-config.service';
import { SolCodeLookupComponent } from '../accounts-lookup/sol-code-lookup/sol-code-lookup.component';
import { ReportInterface } from '../view-report/view-report.component';
import { LoansProductLookUpComponent } from './../../ProductModule/loans-product/loans-product-look-up/loans-product-look-up.component';
import { UniversalMembershipLookUpComponent } from '../../MembershipComponent/universal-membership-look-up/universal-membership-look-up.component';
import { EmployeeConfigLookupComponent } from '../../SystemConfigurations/GlobalParams/employee-config/employee-config-lookup/employee-config-lookup.component';
import { ClassificationLookupComponent } from '../../GeneralLookups/classification-lookup/classification-lookup.component';

@Component({
  selector: 'app-dynamic-report-lookup',
  templateUrl: './dynamic-report-lookup.component.html',
  styleUrls: ['./dynamic-report-lookup.component.scss']
})
export class DynamicReportLookupComponent implements OnInit {

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
  doctypes: any = [
    { code: 'xlsx', description: 'XSLS'} ,
    { code: 'pdf', description: 'PDF' } ,
    { code: 'csv', description: 'CSV' } ,
    { code: 'xlsx2', description: 'XSLS2' },
  ];
  chargeYN: any = [
    { code: 'Y', description: 'Charge Printing Fees' },
    { code: 'N', description: 'No Printing Fees' }
  ];
  transactionTypesArray: any[] = ['Transfer', 'Cash Deposit', 'Cash Withdrawal', 'Fund Teller', 'Collect Teller Fund',
  'Collect Teller Fund', 'Post Expense', 'Post Office Journals', 'Reconcile Accounts', 'Reverse Transactions',
  'Petty Cash', 'Cheque Clearence', 'Cheque Bounce', 'Salary upload','SYSTEM'];
  types: any = ['Retail Accounts', 'Cooperate Accounts'];
  scope: any = ['All Accounts', 'Branch Accounts'];
  statuses: any[] = ['Active', 'Dormant', 'Frozen', 'Suspended'];
  users: any[] = [];
  subscription!: Subscription;
  error: any;
  membershipTypeArray: any;
  report: ReportInterface;
  loading: boolean;
  horizontalPosition: MatSnackBarHorizontalPosition = 'end';
  verticalPosition: MatSnackBarVerticalPosition = 'top';
  branchSelected: boolean = false;
  reportTitle: string;
  isLoading: boolean = false;
  welfareTypeArray: any = []
  
  destroy$: Subject<boolean> = new Subject<boolean>();

  constructor(
    private fb: UntypedFormBuilder,
    private datepipe: DatePipe,
    private dialog: MatDialog,
    private _snackBar: MatSnackBar,
    private reportsService: ReportsService,
    private memberConfigAPI: MembershipConfigService,
    public dialogRef: MatDialogRef<DynamicReportLookupComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private welfareService: WelfareService,
    private authService: AuthService
  ) { }

  ngOnInit(): void {
    this.reportTitle = this.data.reportTitle;
    this.report = this.data.report;
    console.log("In dynamic");
    console.log(this.report);
    this.accountStatementForm = this.initAccountStatementForm();
    this.accountStatementForm.patchValue({
      reportType: this.data.action,
    });
    if(this.report.defaults != null && this.report.defaults.productCode != null ) {
      this.accountStatementForm.patchValue({
        productCode: this.report.defaults.productCode,
      });
    }
    this.getData();
  }
  initAccountStatementForm(): FormGroup {
    return this.fb.group({
      reportType: [''],
      reportName: [this.report.authName],
      fileName: [this.report.filename],
      type: [''],
      acid: [''],
      doctype: ['pdf'],
      customerCode: [''],
      productCode: [''],
      groupCode: [''],
      memberType: [''],
      classification: ['ALL'],
      glSubheadCode: [''],
      glCode: [''],
      welfareCode: [''],
      transactionType: [''],
      employerCode: [''],
      username: [''],
      accountStatus: [''],
      showReversals: [''],
      accountType: [''],
      solCode: [''],
      charge: ['N'],
      todaydate: [`${this.datepipe.transform(new Date(), 'YYYY-MM-dd')}`],
      fromdate: ['2023-01-01'],
      todate: ['2024-01-01'],
    });
  }
  getMemberType() {
    this.loading = true;
     this.memberConfigAPI.findAll().pipe(takeUntil(this.destroy$)).subscribe(
       (res) => {
        this.membershipTypeArray = res.entity;
         if (res.statusCode === 302) {
          this.loading = false;
           this.membershipTypeArray = res.entity;
         } else {
           this.loading = false;
         }
      },
      (err) => {
        this.error = err;
        this.loading = false;
        // this.notificationAPI.alertWarning("Server Error: !!");
      }
    );
  }

  submit() {
    console.log("Requesting report");
    let v = this.accountStatementForm.value;
    if(this.report.alternatives != null) {
      this.report.alternatives.forEach(element => {
        console.log(element);
        element.conditions.forEach(e => {
          console.log(e);
          if(this.accountStatementForm.get(e.name).value == e.value) {
            console.log("In");
            console.log(element.filename);
            v.fileName = element.filename;
          }
        })
      })
    }
    console.log(v);
    v.todaydate = this.datepipe.transform(new Date(v.todaydate), 'YYYY-MM-dd');
    console.log(v);
    const params = new HttpParams()
      .set('reportRequest', JSON.stringify(v));
    // this.dialogRef.close({ event: 'display', data: { params: params, details: this.report } });
    if(this.accountStatementForm.value.doctype == 'xlsx') {
      this.generateReportDynamicallyXlsx(params, this.report.authName);
    }else if(this.accountStatementForm.value.doctype == 'csv') {
      this.generateReportDynamicallyCSV(params, this.report.authName);
    }else if(this.accountStatementForm.value.doctype == 'xlsx2') {
      this.generateReportDynamicallyXlsx2(params, this.report.authName);
    }else {
      this.generateReportDynamically(params, this.report.authName);
    }
  }
  getData() {
    this.getMemberType();
    this.getWelfare();
    this.subscription = this.authService.allUsers().subscribe(
      (res) => {
        console.log(res);
        this.users = res;
        this.users.push({ username: "Migrator" });
      },
      (err) => {
        console.log(err);
      }
    );
  }

  getWelfare() {
    this.loading = true;
    this.welfareService.all().subscribe(
      (res) => {
        this.welfareTypeArray = res.entity;
        console.log(this.welfareTypeArray);
        this.loading = false;
      }
    )
  }

  userLookup(): void {
    const dialogRef = this.dialog.open(AccountUsersComponent, {
      width: '60%',
      autoFocus: false,
      maxHeight: '90vh'
    });
    dialogRef.afterClosed().subscribe(result => {
      this.accountStatementForm.controls.username.setValue(
        result.data.username
      );
    });
  }

  employerCodeLookup(): void {
    const dialogRef = this.dialog.open(EmployeeConfigLookupComponent, {
      width: '45%',
      autoFocus: false,
      maxHeight: '90vh'
    });
    dialogRef.afterClosed().subscribe(result => {
      this.accountStatementForm.controls.employerCode.setValue(
        result.data.employerCode
      );
    });
  }

  glSubheadCodeLookup(): void {
    const dialogRef = this.dialog.open(GlSubheadLookupComponent, {
      width: '35%',
      autoFocus: false,
      maxHeight: '90vh'
    });
    dialogRef.afterClosed().subscribe(result => {
      this.accountStatementForm.controls.glSubheadCode.setValue(
        result.data.glSubheadCode
      );
    });
  }

  
  glCodeLookup(): void {
    const dialogRef = this.dialog.open(GlCodeLookupComponent, {
      autoFocus: false,
      maxHeight: '90vh'
    });
    dialogRef.afterClosed().subscribe(result => {
      this.accountStatementForm.controls.glCode.setValue(
        result.data.glCode
      );
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

  classificationLookup(): void {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.disableClose = false;
    dialogConfig.autoFocus = true;
    dialogConfig.width = '600px';
    dialogConfig.data = {
      data: '',
    };
    const dialogRef = this.dialog.open(
      ClassificationLookupComponent,
      dialogConfig
    );
    dialogRef.afterClosed().subscribe((result) => {
      console.log(result);
      this.accountStatementForm.controls.classification.setValue(
        result.classification
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
  customerLookup(): void {
    const dialogRef = this.dialog.open(UniversalMembershipLookUpComponent, {
      width: '60%',
      autoFocus: false,
      maxHeight: '90vh'
    });
    dialogRef.afterClosed().subscribe(result => {
      this.accountStatementForm.patchValue({
        customerCode: result.data.customerCode,
      });
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
      this.accountStatementForm.patchValue({
        solCode: result.data.branchCode,
      });
    });
  }

  cancel() {
    this.dialogRef.close();
  }


  getFromDate() {
    return this.datepipe.transform(
      this.accountStatementForm.value.fromdate,
      'yyyy-MM-ddTHH:mm:ss'
    );
  }

  getToDate() {
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

  generateReportDynamically(params, filename) {
    this.loading = true;
    this.subscription = this.reportsService
      .loadDynamic(params, filename)
      .subscribe(
        (response) => {
          console.log("Received data");
          console.log(response.data);
          let url = window.URL.createObjectURL(response.data);
          console.log(url);

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



  generateReportDynamicallyXlsx(params, filename) {
    this.loading = true;
    this.subscription = this.reportsService
      .loadDynamicXlsx(params, filename)
      .subscribe(
        (response) => {
          console.log("Received data");
          console.log(response.data);
          let url = window.URL.createObjectURL(response.data);

          let a = document.createElement('a');
          document.body.appendChild(a);
          a.setAttribute('style', 'display: none');
          a.setAttribute('target', 'blank');
          a.href = url;
          a.download = filename+".xlsx";
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


  generateReportDynamicallyXlsx2(params, filename) {
    this.loading = true;
    this.subscription = this.reportsService
      .loadDynamicXlsx2(params, filename)
      .subscribe(
        (response) => {
          console.log("Received data");
          console.log(response.data);
          let url = window.URL.createObjectURL(response.data);

          let a = document.createElement('a');
          document.body.appendChild(a);
          a.setAttribute('style', 'display: none');
          a.setAttribute('target', 'blank');
          a.href = url;
          a.download = filename+".xlsx";
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


  generateReportDynamicallyCSV(params, filename) {
    this.loading = true;
    this.subscription = this.reportsService
      .loadDynamicCSV(params, filename)
      .subscribe(
        (response) => {
          console.log("Received data");
          console.log(response.data);
          let url = window.URL.createObjectURL(response.data);

          let a = document.createElement('a');
          document.body.appendChild(a);
          a.setAttribute('style', 'display: none');
          a.setAttribute('target', 'blank');
          a.href = url;
          a.download = filename+".csv";
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
