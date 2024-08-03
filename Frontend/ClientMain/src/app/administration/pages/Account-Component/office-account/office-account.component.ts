import { DatePipe } from '@angular/common';
import { HttpParams } from '@angular/common/http';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormArray, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { Router } from '@angular/router';
import html2canvas from 'html2canvas';
import jspdf from 'jspdf';
import { Subject, Subscription } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';
import { AccountsNotificationService } from 'src/@core/helpers/accounts/accounts-notification.service';
import { AccountsService } from 'src/app/administration/Service/AccountsService/accounts/accounts.service';
import { AccountStatementService } from 'src/app/administration/Service/transaction/account-statement.service';
import { environment } from 'src/environments/environment';
import { BranchesLookupComponent } from '../../SystemConfigurations/GlobalParams/branches/branches-lookup/branches-lookup.component';
import { CurrencyLookupComponent } from '../../SystemConfigurations/GlobalParams/currency-config/currency-lookup/currency-lookup.component';
import { ExceptionsCodesLookupComponent } from '../../SystemConfigurations/GlobalParams/exceptions-codes/exceptions-codes-lookup/exceptions-codes-lookup.component';
import { GlSubheadLookupComponent } from '../../SystemConfigurations/GlobalParams/gl-subhead/gl-subhead-lookup/gl-subhead-lookup.component';
import { MisSubSectorLookupComponent } from '../../SystemConfigurations/GlobalParams/mis-sub-sector/mis-sub-sector-lookup/mis-sub-sector-lookup.component';
import { ReportService } from '../../reports/report.service';
import { AccountUsersComponent } from '../account-users/account-users.component';
import { OfficeProductLookUpComponent } from './../../ProductModule/office-product/office-product-look-up/office-product-look-up.component';
import { AccountsApprovalComponent } from '../accounts-approval/accounts-approval.component';

@Component({
  selector: 'app-office-account',
  templateUrl: './office-account.component.html',
  styleUrls: ['./office-account.component.scss']
})
export class OfficeAccountComponent implements OnInit, OnDestroy {
  accountStatusArray: any = ['ACTIVE', 'DORMANT', 'NOT-ACTIVE'];
  tranferRestrictionArray: any = ["CREDIT ALLOWED", "DEBIT ALLOWED", "BOTH CR & DB ALLOWED"];
  accountsLabelOption: String[] = ['YES', 'NO'];
  currencyData: any;
  customer_lookup: any;
  loading: boolean = false;
  lookupdata: any;
  glCode: any;
  error: any;
  results: any;
  i: number;
  branchCode: any;
  fetchData: any;
  fmData: any;
  lookupData: any;
  function_type: any;
  account_code: any;
  account_type: any;
  isSubmitting = false;
  glSubheadCode: any;
  glSubheadDescription: any;
  showAccountCode = false;
  showSearch = true;
  showVerification = false;
  currentUser: any;
  userName: any;
  id: any;
  params: any;
  Acid: any;
  showTableOperation = true;
  showButtons = true;
  onShowAddButton = true;
  savingsDetails: any;
  savingsData: any;
  nomineeElement: any;
  arrayIndex: any;
  relPartyElement: any;
  onShowAccountStatement: boolean;
  officeDetails: any;
  onShowResults = false;
  showAccountType = false;
  onShowAccountsLabelForm = true;
  onShowOpeningDate = false;
  exceptionCode: any;
  exce_description: any;
  user_lookup: any;
  submitted = false;
  onShowWarning = true;
  onDisplayAccountRecords = false;
  onShowAccountStatus = false;
  onShowGlCode = false;
  onShowAccountBalance = false;
  account_statement: any;
  onShowAccountStatementTab = false;
  onShowSpecificDetailsTab = true;
  onShowGeneralDetailsTab = true;
  onShowAccountStatementDateParams = false;
  fromdate: Date;
  todate: any;
  hideBtn = false;
  btnColor: any;
  btnText: any;
  destroy$: Subject<boolean> = new Subject<boolean>();
  deletedBy: any;
  logolink: string = `${environment.reportsAPI}/api/v1/dynamic/saccologo`;
  saccoNamelink: any;
  showApprovalbtn: boolean = false;
  accountsdata: any;
  hideRejectBtn: boolean = false;
  btnRejectColor: any;
  rejectBtnText: any;
  rejectionData: any;
  displayBackButton: boolean = true;
  displayApprovalBtn: boolean = false;
  dialogConfig: MatDialogConfig<any>;
  constructor(
    private router: Router,
    private fb: FormBuilder,
    private datepipe: DatePipe,
    private dialog: MatDialog,
    private reportsAPI: ReportService,
    private accountsAPI: AccountsService,
    private notificationAPI: NotificationService,
    private statementAPI: AccountStatementService,
    private accountsNotification: AccountsNotificationService
  ) {
    this.currentUser = JSON.parse(localStorage.getItem('auth-user'));
    this.fmData = this.router.getCurrentNavigation().extras.queryParams.formData;
    this.function_type = this.fmData.function_type;
    this.account_code = this.fmData.account_code;
    this.account_type = this.fmData.account_type;
    if (this.fmData.backBtn == 'APPROVAL') {
      this.displayBackButton = false;
      this.displayApprovalBtn = true;
    }
  }
  ngOnDestroy(): void {
    this.destroy$.next(true);
    this.destroy$.complete();
  }
  ngOnInit(): void {
    this.getPage();
    this.getSaccoName();
  }
  getSaccoName() {
    this.loading = true;
    this.reportsAPI.sacconame().pipe(takeUntil(this.destroy$)).subscribe(
      {
        next: (
          (res) => {
            if (res.statusCode === 302) {
              this.loading = false;
              this.saccoNamelink = res.entity;
            } else {
              this.loading = false;
            }
          }
        ),
        error: (
          (err) => {
            this.loading = false;
          }
        ),
        complete: (
          () => {

          }
        )
      }
    )
  }
  formData: FormGroup = this.fb.group({
    id: [''],
    acid: [''],
    entityId: [''],
    accountBalance: ['0'],
    productCode: ['', Validators.required],
    accountType: ['OFFICE'],
    accountOwnership: ['OFFICE ACCOUNT'],
    accountManager: ['', Validators.required],
    accountName: ['', Validators.required],
    managerCode: ['', Validators.required],
    accountStatus: [''],
    currency: ['KES'],
    customerCode: [''],
    glSubhead: ['', Validators.required],
    glCode: [''],
    openingDate: [new Date()],
    solCode: ['', Validators.required],
    cashExceptionCr: ['0'],
    cashExceptionLimitDr: ['0'],
    tellerAccount: [],
    transferExceptionLimitCr: ['0'],
    transferExceptionLimitDr: ['0'],
    officeAccount: ['']
  });
  accountsLabelForm = this.fb.group({
    id: [''],
    accountLabel: [''],
    labelValue: [''],
  });
  onInitaccountsLabelForm() {
    this.accountsLabelForm = this.fb.group({
      id: [''],
      accountLabel: [''],
      labelValue: [''],
    })
  }
  officeAccountsForm: FormGroup = this.fb.group({
    id: [''],
    accountHeadName: [''],
    accountSupervisorId: ['', Validators.required],
    accountSupervisorName: ['', Validators.required],
    cashLimitDr: ['', Validators.required],
    cashLimitCr: ['', Validators.required],
    transferLimitDr: ['', Validators.required],
    transferLimitCr: ['', Validators.required],
    clearingLimitCrExce: ['', Validators.required]
  })
  get f() {
    return this.formData.controls;
  }
  get ol() {
    return this.f.officeAccountLabels as FormArray;
  }
  get officeAccount() {
    return this.officeAccountsForm.controls;
  }

  subSectorLookup(): void {
    const dialogRef = this.dialog.open(MisSubSectorLookupComponent, {
      width: '35%'
    });
    dialogRef.afterClosed().pipe(takeUntil(this.destroy$)).subscribe(results => {
      this.lookupData = results.data;
      this.formData.controls.missubcode.setValue(results.data.missubcode);
    });
  }
  currencyLookup(): void {
    const dialogRef = this.dialog.open(CurrencyLookupComponent, {
      width: '30%',
      autoFocus: false,
      maxHeight: '90vh'
    });
    dialogRef.afterClosed().pipe(takeUntil(this.destroy$)).subscribe(result => {
      this.currencyData = result.data;
      this.formData.controls.currency.setValue(this.currencyData.ccy)
    })
  }
  branchesCodeLookup(): void {
    const dialogRef = this.dialog.open(BranchesLookupComponent, {
      width: '35%',
      autoFocus: false,
      maxHeight: '90vh'
    });
    dialogRef.afterClosed().pipe(takeUntil(this.destroy$)).subscribe(result => {
      this.lookupData = result.data;
      this.branchCode = this.lookupData.branchCode;
      this.formData.controls.solCode.setValue(this.branchCode);
    });
  }
  glSubheadCodeLookup(): void {
    const dialogRef = this.dialog.open(GlSubheadLookupComponent, {
      width: '35%',
      autoFocus: false,
      maxHeight: '90vh'
    });
    dialogRef.afterClosed().pipe(takeUntil(this.destroy$)).subscribe(result => {
      this.lookupData = result.data;
      this.glCode = this.lookupData.glCode;
      this.glSubheadCode = this.lookupData.glSubheadCode;
      this.glSubheadDescription = this.lookupData.glSubheadDescription;
      this.formData.controls.glSubhead.setValue(this.glSubheadCode);
      this.formData.controls.glCode.setValue(this.glCode);
      this.formData.controls.accountName.setValue(this.glSubheadDescription);
    });
  }

  officeAccountProductCodeLookUp(): void {
    const dialogRef = this.dialog.open(OfficeProductLookUpComponent, {
      width: '35%',
      autoFocus: false,
      maxHeight: '90vh'
    });
    dialogRef.afterClosed().pipe(takeUntil(this.destroy$)).subscribe(result => {
      this.lookupdata = result.data;
      this.formData.controls.productCode.setValue(this.lookupdata.productCode);
    });
  }
  accountManagerLookup(): void {
    const dialogRef = this.dialog.open(AccountUsersComponent, {
      width: '60%',
      autoFocus: false,
      maxHeight: '90vh'
    });
    dialogRef.afterClosed().pipe(takeUntil(this.destroy$)).subscribe(result => {
      this.customer_lookup = result.data;
      this.formData.controls.accountManager.setValue(this.customer_lookup.firstName + " " + this.customer_lookup.lastName);
      this.formData.controls.managerCode.setValue(this.customer_lookup.entityId + this.customer_lookup.solCode);
    });
  }
  tellerCodeLookup(): void {
    const dialogRef = this.dialog.open(AccountUsersComponent, {
      width: '60%',
      autoFocus: false,
      maxHeight: '90vh'
    });
    dialogRef.afterClosed().pipe(takeUntil(this.destroy$)).subscribe(result => {
      this.customer_lookup = result.data;
      this.formData.controls.customerCode.setValue(this.customer_lookup.entityId + this.customer_lookup.solCode);
    });
  }
  supervisorCodeLookup(): void {
    const dialogRef = this.dialog.open(AccountUsersComponent, {
      width: '60%',
      autoFocus: false,
      maxHeight: '90vh'
    });
    dialogRef.afterClosed().pipe(takeUntil(this.destroy$)).subscribe(result => {
      this.customer_lookup = result.data;
      this.officeAccountsForm.controls.accountSupervisorName.setValue(this.customer_lookup.firstName + " " + this.customer_lookup.lastName);
      this.officeAccountsForm.controls.accountSupervisorId.setValue(this.customer_lookup.entityId + this.customer_lookup.solCode);
    });
  }

  exceptionCodeLookupDR(): void {
    const dialogRef = this.dialog.open(ExceptionsCodesLookupComponent, {
      width: '45%',
      autoFocus: false,
      maxHeight: '90vh'
    });
    dialogRef.afterClosed().pipe(takeUntil(this.destroy$)).subscribe(result => {
      this.lookupData = result.data;
      this.exceptionCode = this.lookupData.exceptionCode;
      this.exce_description = this.lookupData.exce_description;
      this.formData.controls.cashExceptionLimitDr.setValue(this.exceptionCode);
    });
  }
  exceptionCodeLookupCR(): void {
    const dialogRef = this.dialog.open(ExceptionsCodesLookupComponent, {
      width: '45%',
      autoFocus: false,
      maxHeight: '90vh'
    });
    dialogRef.afterClosed().pipe(takeUntil(this.destroy$)).subscribe(result => {
      this.lookupData = result.data;
      this.exceptionCode = this.lookupData.exceptionCode;
      this.exce_description = this.lookupData.exce_description;
      this.formData.controls.cashExceptionCr.setValue(this.exceptionCode);
    });
  }
  getAccountStatementFunctions() {
    this.onShowAccountStatementDateParams = true;
    this.onShowAccountStatementTab = true;
    this.onShowSpecificDetailsTab = false;
    this.onShowGeneralDetailsTab = false;
  }
  statementForm = this.fb.group({
    acid: [''],
    fromdate: [new Date()],
    todate: [new Date()]
  })
  eventStart(event: any) {
    this.fromdate = event.target.value;
  }
  eventEnd(event: any) {
    this.todate = event.target.value;
  }

  getAccounStatementRecords() {
    this.loading = true;
    this.params = new HttpParams()
      .set("acid", this.fmData.account_code)
      .set('fromdate', this.datepipe.transform(this.statementForm.controls.fromdate.value, "yyyy-MM-dd"))
      .set('todate', this.datepipe.transform(this.statementForm.controls.todate.value, "yyyy-MM-dd"));
    this.statementAPI.getStatement(this.params).pipe(takeUntil(this.destroy$)).subscribe(
      res => {
        if (res.statusCode === 200) {
          this.loading = false;
          this.results = res;
          this.account_statement = this.results.entity;
          this.getOfficeAccountDetailsData();
          this.onDisplayAccountRecords = true;
          this.notificationAPI.alertSuccess(this.results.message);
        } else {
          this.loading = false;
          this.results = res;
          this.account_statement = this.results.entity;
          this.onDisplayAccountRecords = true;
          this.getOfficeAccountDetailsData();
          this.notificationAPI.alertWarning(this.results.message);
        }

      }, err => {
        this.loading = false;
        this.error = err;
        this.notificationAPI.alertWarning("SERVER ERROR!!");
      }
    )
  }
  getFunctions() {
    this.loading = false;
    this.showAccountCode = true;
    this.showSearch = false;
    this.showTableOperation = false;
    this.showButtons = false;
    this.onShowAccountsLabelForm = false;
    this.formData.disable();
    this.onShowResults = true;
    this.onShowAccountBalance = true;
    this.officeAccountsForm.disable();
  }
  getOfficeAccountDetailsData() {
    this.loading = true;
    this.accountsAPI.retrieveAccount(this.fmData.account_code).pipe(takeUntil(this.destroy$)).subscribe(
      data => {
        if (data.statusCode === 302) {
          this.loading = false;
          this.accountsdata = data.entity;
          this.results = data.entity;
          this.officeDetails = this.results.officeAccount;
          if (this.results.verifiedFlag == 'Y') {
            this.hideRejectBtn = false;
          } else if (this.results.verifiedFlag == 'N') {
            this.btnRejectColor = 'accent';
            this.rejectBtnText = 'REJECT';
            this.hideRejectBtn = true;
          }
          this.officeAccountsForm = this.fb.group({
            id: [this.officeDetails.id],
            accountHeadName: [this.officeDetails.accountHeadName],
            accountSupervisorId: [this.officeDetails.accountSupervisorId],
            accountSupervisorName: [this.officeDetails.accountSupervisorName],
            cashLimitDr: [this.officeDetails.cashLimitDr],
            cashLimitCr: [this.officeDetails.cashLimitCr],
            transferLimitDr: [this.officeDetails.transferLimitDr],
            transferLimitCr: [this.officeDetails.transferLimitCr],
            clearingLimitCrExce: [this.officeDetails.clearingLimitCrExce],
          })
          this.formData = this.fb.group({
            id: [this.results.id],
            acid: [this.results.acid],
            entityId: [this.results.entityId],
            accountType: [this.results.accountType],
            customerType: [this.results.customerType],
            accountName: [this.results.accountName],
            productCode: [this.results.productCode],
            accountManager: [this.results.accountManager],
            accountBalance: [this.results.accountBalance],
            accountOwnership: [this.results.accountOwnership],
            accountStatus: [this.results.accountStatus],
            cashExceptionCr: [this.results.cashExceptionCr],
            cashExceptionLimitCr: [this.results.cashExceptionLimitCr],
            cashExceptionLimitDr: [this.results.cashExceptionLimitDr],
            tellerAccount: [this.results.tellerAccount],
            transferExceptionLimitCr: [this.results.transferExceptionLimitCr],
            transferExceptionLimitDr: [this.results.transferExceptionLimitDr],
            currency: [this.results.currency],
            customerCode: [this.results.customerCode],
            managerCode: [this.results.managerCode],
            openingDate: [this.results.openingDate],
            solCode: [this.results.solCode],
            glSubhead: [this.results.glSubhead],
            glCode: [this.results.glCode],
            lienAmount: [this.results.lienAmount],
            officeAccount: ['']
          });
        } else {
          this.loading = false;
          this.notificationAPI.alertWarning(data.message);
          this.router.navigate([`system/office/account/maintenance`], { skipLocationChange: true });
        }
      }, err => {
        this.error = err;
        this.loading = false;
        this.notificationAPI.alertWarning(this.error);
      }
    )
  }
  getPage() {
    this.loading = true;
    if (this.fmData.function_type == 'ADD') {
      this.loading = false;
      this.showAccountCode = false;
      this.formData = this.fb.group({
        id: [''],
        acid: [''],
        entityId: [''],
        accountType: [this.account_type],
        productCode: ['', Validators.required],
        customerType: ['OFFICE'],
        accountBalance: ['0'],
        accountOwnership: ['OFFICE ACCOUNT'],
        solCode: ['', Validators.required],
        accountManager: [this.currentUser.firstName + " " + this.currentUser.lastName],
        accountName: ['', Validators.required],
        managerCode: [this.currentUser.memberCode],
        accountStatus: [''],
        currency: ['UGX'],
        customerCode: [''],
        glSubhead: ['', Validators.required],
        glCode: [''],
        openingDate: [new Date()],
        cashExceptionCr: ['0'],
        cashExceptionLimitDr: ['0'],
        transferExceptionLimitCr: ['0'],
        tellerAccount: [],
        transferExceptionLimitDr: ['0'],
        officeAccount: ['']
      });
      this.officeAccountsForm = this.fb.group({
        id: [''],
        accountHeadName: [''],
        accountSupervisorId: ['', Validators.required],
        accountSupervisorName: ['', Validators.required],
        cashLimitDr: ['', Validators.required],
        cashLimitCr: ['', Validators.required],
        transferLimitDr: ['', Validators.required],
        transferLimitCr: ['', Validators.required],
        clearingLimitCrExce: ['', Validators.required]
      });
      this.btnColor = 'primary';
      this.btnText = 'SUBMIT';
    } else if (this.fmData.function_type == 'INQUIRE') {
      this.getFunctions();
      this.getOfficeAccountDetailsData();
    } else if (this.fmData.function_type == 'MODIFY') {
      this.loading = true;
      this.showAccountCode = true;
      this.onShowAddButton = false;
      this.showSearch = true;
      this.showVerification = true;
      this.onShowAccountBalance = true;
      this.getOfficeAccountDetailsData();
      this.btnColor = 'primary';
      this.btnText = 'MODIFY';
    } else if (this.fmData.function_type == 'VERIFY') {
      this.getFunctions();
      this.getOfficeAccountDetailsData();
      this.showApprovalbtn = true;
      this.btnColor = 'primary';
      this.btnText = 'VERIFY';
    }
    else if (this.fmData.function_type == 'REJECT') {
      this.getFunctions();
      this.getOfficeAccountDetailsData();
      this.btnColor = 'accent';
      this.btnText = 'REJECT';
    }
    else if (this.fmData.function_type == 'STATEMENT') {
      this.loading = false;
      this.getAccountStatementFunctions();
      this.getOfficeAccountDetailsData();
    }
    else if (this.fmData.function_type == 'DELETE') {
      this.getFunctions();
      this.getOfficeAccountDetailsData();
      this.btnColor = 'accent';
      this.btnText = 'DELETE';
    }
  }
  onSubmit() {
    this.submitted = true;
    if (this.fmData.function_type == 'ADD') {
      if (this.formData.valid) {
        this.formData.controls.officeAccount.setValue(this.officeAccountsForm.value);
        this.isSubmitting = true;
        this.accountsAPI.createAccount(this.formData.value).pipe(takeUntil(this.destroy$)).subscribe(
          res => {
            if (res.statusCode === 201) {
              this.isSubmitting = false;
              this.accountsNotification.alertSuccess(res.message);
              this.router.navigate([`system/office/account/maintenance`], { skipLocationChange: true });
            } else {
              this.loading = false;
              this.notificationAPI.alertWarning(res.message);
            }
          },
          err => {
            this.error = err;
            this.isSubmitting = false;
            this.notificationAPI.alertWarning("Server Error: !!");
          }
        )
      }
      else if (this.formData.invalid) {
        this.isSubmitting = false;
        this.notificationAPI.alertWarning("OFFICE ACCOUNT FORM DATA INVLID");
      }
    } else if (this.fmData.function_type == 'VERIFY') {
      this.loading = true;
      this.showSearch = false;
      this.Acid = this.fmData.account_code;
      this.params = new HttpParams().set('Acid', this.Acid);
      this.accountsAPI.verifyAccount(this.params).pipe(takeUntil(this.destroy$)).subscribe(
        res => {
          if (this.results.statusCode == 200 || this.results.statusCode == 201) {
            this.loading = false;
            this.results = res;
            this.notificationAPI.alertSuccess(this.results.message);
            this.router.navigate([`system/office/account/maintenance`], { skipLocationChange: true });
          } else {
            this.loading = false;
            this.results = res;
            this.getOfficeAccountDetailsData();
            this.notificationAPI.alertWarning(this.results.message);
          }
        },
        err => {
          this.error = err;
          this.loading = false;
          this.getOfficeAccountDetailsData();
          this.notificationAPI.alertWarning("SERVER ERROR!!");
        }
      )
    }
    if (this.fmData.function_type == 'MODIFY') {
      this.loading = true;
      if (this.formData.valid) {
        this.formData.controls.officeAccount.setValue(this.officeAccountsForm.value);
        this.accountsAPI.updateAccounts(this.formData.value).pipe(takeUntil(this.destroy$)).subscribe(
          res => {
            this.loading = false
            this.results = res;
            this.notificationAPI.alertSuccess(this.results.message);
            this.router.navigate([`system/office/account/maintenance`], { skipLocationChange: true });
          },
          err => {
            this.error = err;
            this.loading = false;
            this.getOfficeAccountDetailsData();
            this.notificationAPI.alertWarning("SERVER ERROR!!");
          }
        )
      }
      else if (this.formData.invalid) {
        this.loading = false;
        this.getOfficeAccountDetailsData();
        this.notificationAPI.alertWarning("OFFICE ACCOUNT FORM DATA IS INVALID");
      }
    }
    else if (this.fmData.function_type == 'REJECT') {
      this.loading = true;
      this.showSearch = false;
      this.params = new HttpParams().set('acid', this.fmData.account_code);
      this.accountsAPI.rejectAccount(this.params).pipe(takeUntil(this.destroy$)).subscribe(
        res => {
          if (res.statusCode == 200) {
            this.loading = false;
            this.accountsNotification.alertSuccess(res.message);
            this.router.navigate([`system/loan-account/maintenance`], { skipLocationChange: true });
          } else {
            this.loading = false;
            this.getOfficeAccountDetailsData();
            this.notificationAPI.alertWarning(res.message);
          }
        },
        err => {
          this.loading = false;
          this.getOfficeAccountDetailsData();
          this.notificationAPI.alertWarning("Server Error: !!!");
        }
      )
    }
    else if (this.fmData.function_type == 'DELETE') {
      if (window.confirm("ARE YOU SURE YOU WANT TO DELETE THE OFFICE ACCOUNT? ALL RECORDS WILL BE DELETED!!")) {
        this.loading = true;
        this.id = this.results.id;
        this.deletedBy = this.userName;
        this.params = new HttpParams().set("id", this.id).set("deletedBy", this.deletedBy);
        this.accountsAPI.temporaryDeleteAccount(this.params).pipe(takeUntil(this.destroy$)).subscribe(
          res => {
            this.loading = false;
            this.results = res;
            this.notificationAPI.alertSuccess(this.results.message);
            this.router.navigate([`system/office/account/maintenance`], { skipLocationChange: true });
          },
          err => {
            this.loading = false;
            this.error = err;
            this.getOfficeAccountDetailsData();
            this.notificationAPI.alertWarning("Server Error: !!");
          }
        );
      }
    }
  }
  printAccountStatement(): void {
    let printContents: any, popupWin: any;
    printContents = document.getElementById('print-section').innerHTML;
    popupWin = window.open('', '_blank', 'top=0,left=0,height=100%,width=auto');
    popupWin.document.open();
    popupWin.document.write(`
      <html>
        <head>
          <title>MWAMBA IMARA | ACCOUNT STATEMENT</title>
          <hr/>
           <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
        </head>
    <body onload="window.print();window.close()">${printContents}</body>
      </html>`
    );
    printContents = setTimeout(function () { popupWin.close(); }, 900000);
    popupWin.print();
  }

  onExportAccountStatement() {
    var data = document.getElementById('print-section');  //Id of the table
    html2canvas(data).then(canvas => {
      let imgWidth = 208;
      let pageHeight = 295;
      let imgHeight = canvas.height * imgWidth / canvas.width;
      let heightLeft = imgHeight;

      const contentDataURL = canvas.toDataURL('image/png')
      let pdf = new jspdf('p', 'mm', 'a4'); // A4 size page of PDF
      let position = 0;
      pdf.addImage(contentDataURL, 'PNG', 0, position, imgWidth, heightLeft)
      pdf.save('account-statement.pdf'); // Generated PDF
    });
  }
  onApproval() {
    this.dialogConfig = new MatDialogConfig();
    this.dialogConfig.disableClose = true;
    this.dialogConfig.autoFocus = true;
    this.dialogConfig.width = "600px";
    this.dialogConfig.data = this.accountsdata
    const dialogRef = this.dialog.open(AccountsApprovalComponent, this.dialogConfig);
    dialogRef.afterClosed().subscribe(
      (_res: any) => {
        this.loading = false;
      });
  }
   onRejectAccount() {
     if (window.confirm("ARE YOU SURE YOU WANT TO REJECT ACCOUNT " + this.results.accountType +  " WITH ACCOUNT NO: " + this.results.acid + " ?")) {
      this.loading = true;
      this.params = new HttpParams()
        .set('acid', this.accountsdata.acid);
      this.accountsAPI.rejectAccount(this.params).pipe(takeUntil(this.destroy$)).subscribe(
        {
          next: (
            (res) => {
              if (res.statusCode === 200) {
                this.loading = false;
                this.accountsNotification.alertSuccess(res.message);
                if (this.fmData.backBtn == 'APPROVAL') {
                  this.router.navigate([`/system/new/notification/allerts`], { skipLocationChange: true });
                } else {
                  this.router.navigate([`/system/office/account/maintenance`], { skipLocationChange: true });
                }
              } else {
                this.loading = false;
                this.notificationAPI.alertWarning(res.message);
              }
            }
          ),
          error: (
            (err) => {
              this.loading = false;
              this.notificationAPI.alertWarning("Service Error: !!!");
            }
          ),
          complete: (
            () => {

            }
          )
        }
      ), Subscription;
    }
  }
}
