import { HttpParams } from '@angular/common/http';
import { Component, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { Router } from '@angular/router';
import { Subject, Subscription } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';
import { AccountsNotificationService } from 'src/@core/helpers/accounts/accounts-notification.service';
import { DataStoreService } from 'src/@core/helpers/data-store.service';
import { AccountsService } from 'src/app/administration/Service/AccountsService/accounts/accounts.service';
import { AllAccountsLookUpComponent } from '../../Account-Component/all-accounts-look-up/all-accounts-look-up.component';
import { GeneralAccountDetailsComponent } from '../../Account-Component/general-account-details/general-account-details.component';
import { GeneralAccountsLookupComponent } from '../../Account-Component/general-accounts-lookup/general-accounts-lookup.component';
import { AccountImagesLookupComponent } from '../account-images-lookup/account-images-lookup.component';
import { RejectTransactionComponent } from '../reject-transaction/reject-transaction.component';
import { TransactionApprovalComponent } from '../transaction-approval/transaction-approval.component';
import { TransactionExecutionService } from '../transaction-execution.service';
import { MemberDocumentsComponent } from '../member-documents/member-documents.component';

@Component({
  selector: 'app-cash-transaction-execution',
  templateUrl: './cash-transaction-execution.component.html',
  styleUrls: ['./cash-transaction-execution.component.scss']
})
export class CashTransactionExecutionComponent implements OnInit {
  displayedColumns: string[] = [
    'index',
    'acid',
    'partTranType',
    'transactionAmount',
    'transactionDate',
    'transactionParticulars',
    'exchangeRate',
    'actions',
  ];
  dataSource!: MatTableDataSource<any>;
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;
  partTransactionTypeArray: any = [
    {
      name: 'Debit',
    },
    {
      name: 'Credit',
    },
  ];
  debit_value = 0.0;
  credit_value = 0.0;
  total_value = 0.0;
  index: number;
  partTransForm!: FormGroup;
  partTransArray: any[] = [];
  editButton: boolean = false;
  addButton: boolean = true;
  disableAddButton: boolean = false;
  disableActions: boolean = false;
  currentUser: any;
  functionArray: any[];
  fmData: any;
  function_type: any;
  transactionCode: any;
  transactionType: any;
  hideBtn = false;
  btnColor: any;
  btnText: any;
  enteredBy: any;
  verifiedFlag_2: any;
  verifiedFlag: any;
  verifiedBy_2: any;
  loading: boolean = false;
  isAlert: boolean = false;
  submitted: boolean = false;
  showAccountData: boolean = false;
  showTranRecieptButton: boolean = false;
  showtransactionData: boolean = false;
  showRecieptButton: boolean = false;
  showTransactionList: boolean = false;
  showTransactionForm: boolean = true;
  onAccountDetailsLookup: boolean = false;
  showAccountImagesLookup: boolean = false;
  destroy$: Subject<boolean> = new Subject<boolean>();
  accountlookupData: any;
  results: any;
  accountBalance: any;
  accountCurrency: any;
  accountName: any;
  accountStatus: any;
  resMessage: any;
  resEntity: any;
  partTrans: any;
  params: any;
  account_acid_id: any;
  showWithdrawalTranRecieptButton: boolean = false;
  resData: any;
  showWithdrawalFees: boolean = false;
  accountbalanceresults: any;
  accountbalance: any;
  balance_value = 0.0;
  dialogConfig: MatDialogConfig<any>;
  showApprovalbtn: boolean = false;
  conductedBy: any;
  hideRejectBtn: boolean = false;
  btnRejectColor: any;
  rejectBtnText: any;
  rejectionData: any;
  workclasses: any;
  customer_lookup: any;
  customerCode: any;
  constructor(
    private router: Router,
    private fb: FormBuilder,
    private dialog: MatDialog,
    private accountsAPI: AccountsService,
    private dataStoreApi: DataStoreService,
    private notificationAPI: NotificationService,
    private transactionAPI: TransactionExecutionService,
    private accountsNotification: AccountsNotificationService
  ) {
    this.currentUser = JSON.parse(localStorage.getItem('auth-user'));
    this.functionArray = this.dataStoreApi.getActionsByPrivilege("TRANSACTION MAINTENANCE");
    this.fmData = this.router.getCurrentNavigation().extras.queryParams.formData;
    this.function_type = this.fmData.function_type;
    this.transactionCode = this.fmData.transactionCode;
    this.transactionType = this.fmData.transactionType;
  }
  ngOnDestroy(): void {
    this.destroy$.next(true);
    this.destroy$.complete();
  }
  ngOnInit(): void {
    this.currentUser = JSON.parse(localStorage.getItem('auth-user'));
    this.getAccountBalance();
    this.initializePartTransForm();
    this.getPage();
  }
  getAccountBalance() {
    this.loading = true;
    this.accountsAPI.retrieveAccount(this.currentUser.tellerAc).pipe(takeUntil(this.destroy$)).subscribe(
      {
        next: (
          (res) => {
            if (res.statusCode === 302) {
              this.loading = false;
              this.accountbalanceresults = res.entity;
              this.accountbalance = this.balance_value + this.accountbalanceresults.accountBalance;
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
    ), Subscription;
  }
  formData: FormGroup = this.fb.group({
    batchCode: [''],
    chargePartran: [[]],
    chequeInstruments: [[]],
    chequeType: [''],
    currency: ['UGX'],
    entityId: [''],
    eodStatus: [''],
    mpesacode: [''],
    partTrans: [[]],
    reversalTransactionCode: [''],
    salaryuploadCode: [''],
    sn: [''],
    staffCustomerCode: [''],
    status: [''],
    tellerAccount: [''],
    totalAmount: [''],
    transactionCode: [''],
    transactionDate: [new Date()],
    transactionType: ['']
  });
  get f() {
    return this.formData.controls;
  }
  get pt() {
    return this.partTransForm.controls;
  }
  initializePartTransForm() {
    this.initPartTransForm();
    this.partTransForm.controls.accountBalance.setValidators([]);
    this.partTransForm.controls.accountType.setValidators([Validators.required]);
    this.partTransForm.controls.acid.setValidators([Validators.required]);
    this.partTransForm.controls.batchCode.setValidators([]);
    this.partTransForm.controls.conductedBy.setValidators([Validators.required]);
    this.partTransForm.controls.currency.setValue(Validators.required);
    this.partTransForm.controls.chargeFee.setValue("Y");
    this.partTransForm.controls.exchangeRate.setValue(1);
    this.partTransForm.controls.isoFlag.setValue("Y");
    this.partTransForm.controls.partTranType.setValidators([Validators.required]);
    this.partTransForm.controls.sn.setValidators([]);
    this.partTransForm.controls.transactionAmount.setValidators([Validators.required]);
    this.partTransForm.controls.transactionCode.setValidators([]);
    this.partTransForm.controls.transactionDate.setValue(new Date());
    this.partTransForm.controls.transactionParticulars.setValidators([Validators.required]);

  }
  initPartTransForm() {
    this.partTransForm = this.fb.group({
      accountBalance: [''],
      accountType: [''],
      acid: [''],
      batchCode: [''],
      conductedBy: [''],
      currency: [''],
      chargeFee: ['Y'],
      exchangeRate: [''],
      isoFlag: [''],
      partTranType: [''],
      sn: [''],
      transactionAmount: [''],
      transactionCode: [''],
      transactionDate: [new Date()],
      transactionParticulars: ['']
    });
  }
  addPartran() {
    if (this.partTransForm.valid) {
      this.partTransArray.push(this.partTransForm.value);
      this.resetPartransForm();
    }
  }
  getTransactions() {
    this.dataSource = new MatTableDataSource(this.partTransArray);
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
  }
  resetPartransForm() {
    this.formData.patchValue({
      partTrans: this.partTransArray
    });
    this.validateData();
    this.getTransactions();
    this.initializePartTransForm();
  }
  validateData() {
    this.partTransArray.forEach((element) => {
      if (element.partTranType == 'Debit') {
        this.debit_value = this.debit_value + element.transactionAmount;
      } else if (element.partTranType == 'Credit') {
        this.credit_value = this.credit_value + element.transactionAmount;
      }
    });
    this.resetReadonlys();
  }
  resetReadonlys() {
    if (this.transactionType == 'CASH DEPOSIT') {
      this.formData.patchValue({
        transactionParticulars: 'CASH DEPOSIT',
        tellerAccount: this.currentUser.tellerAc,
        totalAmount: this.credit_value,
        transactionType: 'CASH DEPOSIT'
      });
    }
    if (this.transactionType == 'CASH WITHDRAWAL') {
      this.showWithdrawalFees = true;
      this.formData.patchValue({
        transactionParticulars: 'CASH WITHDRAWAL',
        tellerAccount: this.currentUser.tellerAc,
        totalAmount: this.debit_value,
        transactionType: 'CASH WITHDRAWAL'
      });
    }
    if (this.transactionType == 'PETTY CASH') {
      this.formData.patchValue({
        transactionParticulars: 'PETTY CASH',
        tellerAccount: this.currentUser.tellerAc,
        totalAmount: this.debit_value,
        transactionType: 'PETTY CASH'
      });
    }
    if (this.transactionType == 'FUND TELLER') {
      this.formData.patchValue({
        transactionParticulars: 'FUND TELLER',
        tellerAccount: this.currentUser.tellerAc,
        totalAmount: this.debit_value,
        transactionType: 'FUND TELLER'
      });
    }
    if (this.transactionType == 'AGENCY NET DEPOSIT') {
      this.formData.patchValue({
        transactionParticulars: 'AGENCY NET DEPOSIT',
        tellerAccount: this.currentUser.tellerAc,
        totalAmount: this.debit_value,
        transactionType: 'AGENCY NET DEPOSIT'
      });
    }
    if (this.transactionType == 'AGENCY NET WITHDRAWAL') {
      this.formData.patchValue({
        transactionParticulars: 'AGENCY NET WITHDRAWAL',
        tellerAccount: this.currentUser.agencyAc,
        totalAmount: this.debit_value,
        transactionType: 'AGENCY NET WITHDRAWAL'
      });
    }
    if (this.transactionType == 'COLLECT TELLER FUND') {
      this.formData.patchValue({
        transactionParticulars: 'COLLECT TELLER FUND',
        tellerAccount: this.currentUser.tellerAc,
        totalAmount: this.credit_value,
        transactionType: 'COLLECT TELLER FUND'
      });
    }
    if (this.transactionType == 'PAYBILL WITHDRAWAL') {
      this.formData.patchValue({
        transactionParticulars: 'PAYBILL WITHDRAWAL',
        tellerAccount: this.currentUser.tellerAc,
        totalAmount: this.credit_value,
        transactionType: 'PAYBILL WITHDRAWAL'
      });
    }
  }
  formatForm() {
    if (this.transactionType == 'CASH DEPOSIT') {
      this.partTransForm.patchValue({
        partTranType: 'Credit',
        transactionParticulars: 'CASH DEPOSIT',
      });
    }
    if (this.transactionType == 'CASH WITHDRAWAL') {
      this.showWithdrawalFees = true;
      this.partTransForm.patchValue({
        partTranType: 'Debit',
        transactionParticulars: 'CASH WITHDRAWAL',
      });
    }
    if (this.transactionType == 'AGENCY NET DEPOSIT') {
      this.partTransForm.patchValue({
        partTranType: 'Credit',
        transactionParticulars: 'AGENCY NET DEPOSIT',
      });
    }
    if (this.transactionType == 'AGENCY NET WITHDRAWAL') {
      this.showWithdrawalFees = true;
      this.partTransForm.patchValue({
        partTranType: 'Debit',
        transactionParticulars: 'AGENCY NET WITHDRAWAL',
      });
    }
    if (this.transactionType == 'PETTY CASH') {
      this.partTransForm.patchValue({
        partTranType: 'Debit',
      });
    }
    if (this.transactionType == 'FUND TELLER') {
      this.partTransForm.patchValue({
        partTranType: 'Debit',
        transactionParticulars: 'FUND TELLER',
      });
    }
    if (this.transactionType == 'COLLECT TELLER FUND') {
      this.partTransForm.patchValue({
        partTranType: 'Credit',
        transactionParticulars: 'COLLECT TELLER FUND',
      });
    }
    if (this.transactionType == 'PAYBILL WITHDRAWAL') {
      this.partTransForm.patchValue({
        partTranType: 'Debit',
        transactionParticulars: 'PAYBILL WITHDRAWAL',
      });
    }
  }
  onaccountkeyup(event: any) {

  }
  generalAccountsLookup() {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.disableClose = false;
    dialogConfig.autoFocus = true;
    dialogConfig.width = '950px';
    dialogConfig.data = {
      data: '',
    };
    const dialogRef = this.dialog.open(GeneralAccountsLookupComponent, dialogConfig);
    dialogRef.afterClosed().subscribe((result) => {
      this.accountlookupData = result.data;
      this.customerCode = this.accountlookupData.customerCode;
      this.account_acid_id = this.accountlookupData.acid;
      this.partTransForm.patchValue({
        acid: this.accountlookupData.acid
      });
      if (this.accountlookupData.acid === null || this.accountlookupData.acid === undefined || this.accountlookupData.acid === '-') {
        this.onAccountDetailsLookup = false;
        this.showAccountImagesLookup = false;
      } else {
        this.onAccountDetailsLookup = true;
        this.showAccountImagesLookup = true;
      }
      this.loading = true;
      this.loadAcidData(this.accountlookupData.acid);

    });
  }

  loadAcidData(acid: any) {
    this.transactionAPI.getAccountDetails(acid).pipe(takeUntil(this.destroy$)).subscribe(
      {
        next: (
          (res) => {
            if (res.statusCode === 200) {
              this.results = res.entity;
              this.loading = false;
              this.showAccountData = true;
              this.accountBalance = this.results.account_balance - this.results.book_balance;
              this.accountCurrency = this.results.currency;
              this.accountName = this.results.account_name;
              this.accountStatus = this.results.account_status;
              this.partTransForm.patchValue({
                currency: this.results.currency,
                accountBalance: this.results.account_balance,
                accountType: this.results.account_type,
              });
            } else {
              this.loading = false;
              this.notificationAPI.alertWarning("ACCOUNT DEATILS NOT FOUND !!");
            }
          }
        ),
        error: (
          (err) => {
            this.loading = false;
            this.notificationAPI.alertWarning("Server Error: !!");
          }
        ),
        complete: (
          () => {

          }
        )
      }
    )
  }
  accountLookup(): void {
    if (true) {
      this.generalAccountsLookup();
      return;
    }
    const dialogRef = this.dialog.open(
      AllAccountsLookUpComponent, {
      width: '50%'
    });
    dialogRef.afterClosed().pipe(takeUntil(this.destroy$)).subscribe(
      {
        next: (
          (res) => {
            this.accountlookupData = res.data;
            this.account_acid_id = this.accountlookupData.acid;
            this.partTransForm.patchValue({
              acid: this.accountlookupData.acid
            });
            if (this.accountlookupData.acid === null || this.accountlookupData.acid === undefined || this.accountlookupData.acid === '-') {
              this.onAccountDetailsLookup = false;
              this.showAccountImagesLookup = false;
            } else {
              this.onAccountDetailsLookup = true;
              this.showAccountImagesLookup = true;
            }
            this.loading = true;
            this.loadAcidData(this.accountlookupData.acid);
          }
        ),
        error: (
          (err) => {
            this.loading = false;
            this.notificationAPI.alertWarning("Server Error: !!");
          }
        ),
        complete: (
          () => {

          }
        )
      }
    );
  }
  accountDetailsLookup(data: any) {
    this.dialog.open(GeneralAccountDetailsComponent, {
      maxWidth: '100vw',
      maxHeight: '100vh',
      height: '100%',
      width: '100%',
      panelClass: 'full-screen-modal',
      data: this.account_acid_id
    });
  }
  memberDocuments() {
    this.dialogConfig = new MatDialogConfig();
    this.dialogConfig.disableClose = false;
    this.dialogConfig.autoFocus = true;
    this.dialogConfig.width = "30%";
    this.dialogConfig.data = this.accountlookupData;
    const dialogRef = this.dialog.open(MemberDocumentsComponent, this.dialogConfig);
    dialogRef.afterClosed().subscribe(
      (_res: any) => {
        this.loading = false;
      })
  }
  accountImagesLookup() {
    if (this.partTransForm.value.acid) {
      const dialogConfig = new MatDialogConfig();
      dialogConfig.disableClose = false;
      dialogConfig.autoFocus = true;
      dialogConfig.data = {
        data: this.partTransForm.value.acid,
      };
      const dialogRef = this.dialog.open(
        AccountImagesLookupComponent,
        dialogConfig
      );
      dialogRef.afterClosed().pipe(takeUntil(this.destroy$)).subscribe((result) => { });
    } else {
      this.notificationAPI.alertWarning("PLEASE SELECT AN ACCOUNT");
    }
  }
  accountImagesAsParam(account: any) {
    if (account) {
      const dialogConfig = new MatDialogConfig();
      dialogConfig.disableClose = false;
      dialogConfig.autoFocus = true;
      dialogConfig.data = {
        data: account,
      };
      const dialogRef = this.dialog.open(
        AccountImagesLookupComponent,
        dialogConfig
      );
      dialogRef.afterClosed().subscribe((result) => { });
    } else {
      this.notificationAPI.alertWarning("Please select an account for this Transaction");
    }
  }
  depositReciept() {
    let transactionCode = this.resData.transactionCode;
    const params = new HttpParams()
      .set('transactionCode', transactionCode);
    this.transactionAPI
      .generateDepositReciept(params).pipe(takeUntil(this.destroy$))
      .subscribe(
        (res) => {
          this.results = res;
          this.loading = false;
          this.notificationAPI.alertSuccess("downloading " + this.results.filename);
          let url = window.URL.createObjectURL(res.data);
          window.open(url);
        },
        (err) => {
          this.loading = false;
          this.notificationAPI.alertWarning("Error generating reciept !");
        }
      );
  }

  withdrawalReciept() {
    let transactionCode = this.resData.transactionCode;
    const params = new HttpParams()
      .set('transactionCode', transactionCode);
    this.transactionAPI
      .generateWithdrawalReciept(params).pipe(takeUntil(this.destroy$))
      .subscribe(
        (res) => {
          this.results = res;
          this.loading = false;
          this.notificationAPI.alertSuccess("downloading " + this.results.filename);
          let url = window.URL.createObjectURL(res.data);
          window.open(url);
          // if you want to open PDF in new tab
          // let a = document.createElement('a');
          // document.body.appendChild(a);
          // a.setAttribute('style', 'display: none');
          // a.setAttribute('target', 'blank');
          // a.href = url;
          // a.download = res.filename;
          // a.click();
          // window.URL.revokeObjectURL(url);
          // a.remove();

        },
        (err) => {
          this.loading = false;
          this.notificationAPI.alertWarning("Error generating reciept !");
        }
      );
  }

  getPage() {
    if (this.function_type == 'ENTER') {
      this.btnColor = 'primary';
      this.btnText = 'ENTER';
      this.formatForm();
      this.disableAddButton = false;
    }
    else if (this.function_type == 'POST') {
      this.btnColor = 'primary';
      this.btnText = 'POST';
      this.loading = true;
      this.transactionAPI.getTransactionByCode(this.transactionCode).pipe(takeUntil(this.destroy$)).subscribe(
        (res) => {
          this.results = res.entity;
          this.rejectionData = res.entity;
          this.partTransArray = this.results.partTrans;
          if (this.rejectionData.postedFlag == 'Y') {
            this.hideRejectBtn = false;
          } else if (this.rejectionData.postedFlag == 'N') {
            this.btnRejectColor = 'accent';
            this.rejectBtnText = 'REJECT';
            this.hideRejectBtn = true;
          }
          if (this.transactionType === "CASH DEPOSIT") {
            if (this.results.postedFlag == 'Y') {
              this.showRecieptButton = true;
              this.btnText = '';
              this.showWithdrawalTranRecieptButton = false;
            } else if (this.results.postedFlag == 'N') {
              this.showRecieptButton = false;
              this.showWithdrawalTranRecieptButton = false;
            }
          }

          if (this.transactionType === "CASH WITHDRAWAL") {
            if (this.results.postedFlag == 'Y') {
              this.showRecieptButton = false;
              this.btnText = '';
              this.showWithdrawalTranRecieptButton = true;
            } else if (this.results.postedFlag == 'N') {
              this.showRecieptButton = false;
              this.showWithdrawalTranRecieptButton = false;
            }
          }
          this.transactionType = this.results.transactionType;
          this.partTrans = this.results.partTrans;
          this.btnColor = 'primary';
          this.btnText = 'POST';
          this.getTransactions();
          this.loading = true;
          this.loading = false;
          this.formData.disable();
          this.disableActions = true;
          this.showTransactionForm = false;
          this.showTransactionList = true;
          this.showtransactionData = true;
          this.validateData();
        },
        (err) => {
          this.loading = false;
          this.notificationAPI.alertWarning("Server Error: !!");
        }
      );
    }
  }
  getAcknowldgement() {
    this.loading = true;
  }
  onSubmit() {
    this.submitted = true;
    this.loading = true;
    this.addPartran();
    if (this.function_type == 'ENTER') {
      if (this.partTransForm.valid) {
        this.resetReadonlys();
        this.formatForm();
        this.partTransArray = [];
        if (this.partTransArray.length > 1) {
          this.notificationAPI.alertWarning("ONE Transaction is Allowed Before Submitting !!");
        }
        else {
          this.transactionAPI
            .enter(this.formData.value).pipe(takeUntil(this.destroy$))
            .subscribe(
              {
                next: (
                  (res) => {
                    console.log(res);
                    if (res.statusCode === 201) {
                      this.loading = false;
                      this.showAccountData = false;
                      this.accountsNotification.alertSuccess(res.message);
                      this.partTransArray = [];
                      this.function_type = 'POST';
                      this.rejectionData = res.entity;
                      if (this.rejectionData.postedFlag == 'Y') {
                        this.hideRejectBtn = false;
                      } else if (this.rejectionData.postedFlag == 'N') {
                        this.btnRejectColor = 'accent';
                        this.rejectBtnText = 'REJECT';
                        this.hideRejectBtn = true;
                      }
                      this.transactionCode = res.entity.transactionCode;
                      this.enteredBy = res.entity.enteredBy;
                      this.verifiedFlag_2 = res.entity.verifiedFlag_2;
                      this.verifiedFlag = res.entity.verifiedFlag;
                      this.verifiedBy_2 = res.entity.verifiedBy_2;
                      this.conductedBy = res.entity.conductedBy;
                      this.getPage();
                      this.resetReadonlys();
                      this.formatForm();
                      this.onAccountDetailsLookup = false;
                      this.showAccountImagesLookup = false;
                    } else {
                      if (this.resEntity == null) {
                        this.notificationAPI.alertWarning(res.message);
                        // this.isAlert = false;
                      } else { 
                        // this.isAlert = true;
                        for (let i = 0; i < this.resEntity.length; i++) {
                          this.notificationAPI.alertWarning(this.resEntity[i].message);
                        }
                      }
                      // this.getPage();
                      // this.resetReadonlys();
                      // this.formatForm();
                      this.onAccountDetailsLookup = false;
                      this.showAccountImagesLookup = false;
                      this.loading = false;
                      this.showAccountData = false;
                      this.resMessage = res.message;
                      this.resEntity = res.entity;
                    }
                  }
                ),
                error: (
                  (err) => {
                    console.log(err);
                    this.loading = false;
                    // this.showAccountData = false;
                    this.notificationAPI.alertWarning("Server Error: !!");
                    // this.getPage();
                    // this.resetReadonlys();
                    // this.formatForm();
                    // this.onAccountDetailsLookup = false;
                    // this.showAccountImagesLookup = false;
                  }
                ),
                complete: (
                  () => {

                  }
                )
              }
            ), Subscription;
        }
      } else {
        this.resetReadonlys();
        this.formatForm();
        this.onAccountDetailsLookup = true;
        this.showAccountImagesLookup = true;
        this.loading = false;
        this.notificationAPI.alertWarning("Patrans Form Data is invalid!!");
      }
    }
    else if (this.function_type == 'POST') {
      this.params = new HttpParams()
        .set('transactionCode', this.results.transactionCode);
      this.transactionAPI
        .post(this.params).pipe(takeUntil(this.destroy$))
        .subscribe(
          {
            next: (
              (res) => {
                if (res.statusCode === 200) {
                  this.loading = false;
                  this.resData = res.entity;
                  this.rejectionData = res.entity;
                  this.notificationAPI.alertSuccess(res.message);
                  if (this.rejectionData.postedFlag == 'Y') {
                    this.hideRejectBtn = false;
                  } else if (this.rejectionData.postedFlag == 'N') {
                    this.btnRejectColor = 'accent';
                    this.rejectBtnText = 'REJECT';
                    this.hideRejectBtn = true;
                  }
                  if (this.transactionType === "CASH DEPOSIT") {
                    if (this.resData.postedFlag == 'Y') {
                      this.showRecieptButton = true;
                      this.showWithdrawalTranRecieptButton = false;
                    } else if (this.resData.postedFlag == 'N') {
                      this.showRecieptButton = false;
                      this.showWithdrawalTranRecieptButton = false;
                    }
                  }
                  if (this.transactionType === "CASH WITHDRAWAL") {
                    if (this.resData.postedFlag == 'Y') {
                      this.showRecieptButton = false;
                      this.showWithdrawalTranRecieptButton = true;
                    } else if (this.resData.postedFlag == 'N') {
                      this.showRecieptButton = false;
                      this.showWithdrawalTranRecieptButton = false;
                    }
                  }
                  this.function_type = 'POST';
                } else {
                  this.loading = false;
                  this.notificationAPI.alertWarning(res.message);

                }
              }
            ),
            error: (
              (err) => {
                this.loading = false;
                this.notificationAPI.alertSuccess("Server Error: !!");
                this.getPage();
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
  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();
    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }
  onRejectTransaction() {
    this.dialogConfig = new MatDialogConfig();
    this.dialogConfig.disableClose = true;
    this.dialogConfig.autoFocus = true;
    this.dialogConfig.width = "600px";
    this.dialogConfig.data = this.rejectionData
    const dialogRef = this.dialog.open(RejectTransactionComponent, this.dialogConfig);
    dialogRef.afterClosed().subscribe(
      (_res: any) => {
        this.loading = false;
      });
  }
  onApproval() {
    this.dialogConfig = new MatDialogConfig();
    this.dialogConfig.disableClose = true;
    this.dialogConfig.autoFocus = true;
    this.dialogConfig.width = "600px";
    this.dialogConfig.data = this.results;
    const dialogRef = this.dialog.open(TransactionApprovalComponent, this.dialogConfig);
    dialogRef.afterClosed().subscribe(
      (_res: any) => {
        this.loading = false;
      });
  }
}
