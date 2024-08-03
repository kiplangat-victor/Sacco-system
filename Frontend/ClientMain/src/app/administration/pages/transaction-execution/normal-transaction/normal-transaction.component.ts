import { HttpParams } from '@angular/common/http';
import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatCheckboxChange } from '@angular/material/checkbox';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatRadioChange } from '@angular/material/radio';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { Router } from '@angular/router';
import { Observable, Subject, Subscription } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';
import { AccountsNotificationService } from 'src/@core/helpers/accounts/accounts-notification.service';
import { AccountsService } from 'src/app/administration/Service/AccountsService/accounts/accounts.service';
import { WelfareService } from 'src/app/administration/Service/Welfare/welfare.service';
import { GeneralAccountDetailsComponent } from '../../Account-Component/general-account-details/general-account-details.component';
import { GeneralAccountsLookupComponent } from '../../Account-Component/general-accounts-lookup/general-accounts-lookup.component';
import { GroupMembershipDetailsComponent } from '../../MembershipComponent/GroupMembership/group-membership-details/group-membership-details.component';
import { UniversalMembershipLookUpComponent } from '../../MembershipComponent/universal-membership-look-up/universal-membership-look-up.component';
import { AccountImagesLookupComponent } from '../account-images-lookup/account-images-lookup.component';
import { RejectTransactionComponent } from '../reject-transaction/reject-transaction.component';
import { TransactionApprovalComponent } from '../transaction-approval/transaction-approval.component';
import { TransactionExecutionService } from '../transaction-execution.service';
import { DataStoreService } from 'src/@core/helpers/data-store.service';
import { MemberDocumentsComponent } from '../member-documents/member-documents.component';

@Component({
  selector: 'app-normal-transaction',
  templateUrl: './normal-transaction.component.html',
  styleUrls: ['./normal-transaction.component.scss']
})
export class NormalTransactionComponent implements OnInit, OnDestroy {
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
  welfareTypeArray: any = [
    {
      welfareCode: 'BBF',
      welfareName: 'Burial Benevolent Fund',
    }
  ];
  welfareActionsArray: any = null
  dataSource!: MatTableDataSource<any>;
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;
  debit_value = 0.0;
  credit_value = 0.0;
  total_value = 0.0;
  dialogData: any;
  ccy_name: any;
  selectedCurrency: any;
  functionArray: any;
  transactionTypeArray: any;
  showTranRecieptButton: boolean = false;
  customer_lookup: any;
  params: any;
  resMessage: any;
  resEntity: any;
  enteredBy: any;
  verifiedFlag_2: any;
  verifiedFlag: any;
  verifiedBy_2: any;
  account_acid_id: any;
  approvingbtn: boolean = false;
  showApprovalbtn: boolean = false;
  onAccountDetailsLookup: boolean = false;
  notificationresData: any;
  conductedBy: any;
  onMemberDetailsLookup: boolean = false;
  customer_account_code: any;
  customer_account_id: any;
  workclasses: any;
  privileges: any;
  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();
    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }
  showtransactionData: boolean = false;
  spinnerVisible: boolean = true;
  errorVisible: boolean = false;
  transactionTabIndex: number = 1;
  selection: number = 1;
  transactionType: string = 'NC';
  transactionNumber!: string;
  customerCode!: string;
  Valid = true;
  // transaction!: Transac
  selectedFiles?: FileList;
  currentFile?: File;
  progress = 0;
  message = '';
  fileInfos?: Observable<any>;
  //unverified Transactions
  existingData: boolean;
  loading = false;
  transaction_code: number;
  submitted = false;
  currency: any;
  exchangeRate: any;
  error_message: string;
  partTrans: any;
  partTranType: any;
  transactionAmount: any;
  accountlookupData: any;
  error: any;
  accountReference: any;
  description: any;
  partTransForm: any;
  messageData: any;
  function_type: any;
  transactionCode: any;
  resData: any;
  isDisable = false;
  hideSubmit = false;
  showChequeDetails = false;
  partTransactionTypeArray: any = [
    {
      name: 'Debit',
    },
    {
      name: 'Credit',
    },
  ];
  transactionForm!: FormGroup;
  formData!: FormGroup;
  chequeForm!: FormGroup;
  transactionArray = new Array();
  fmData: any;
  index: number;
  editButton: boolean = false;
  addButton: boolean = true;
  accountStatus: any;
  accountBalance: any;
  accountCurrency: any;
  accountName: any;
  disableAddButton: boolean = false;
  disableActions: boolean = false;
  activateDelete: boolean = false;
  submitData: string = '';
  // systemDate: any;
  results: any;
  accountReadonly = false;
  partTranTypeReadonly = false;
  transactionParticularsReadonly = false;
  transactionTypeReadonly = false;
  deleteText = '';
  data: any;
  disableUploadFile = false;
  currentUser: any;
  showRecieptButton = false;
  showWelfare = true;
  welfareON = false;
  subscription!: Subscription;
  hideBtn = false;
  btnColor = '';
  btnText = '';
  showTransactionsOnly = false;
  notAdd = false;
  dateToday: any;
  isAlert: boolean = false;
  transactionDate = new Date();
  showAccountImagesLookup: boolean = false;
  showWithdrawalTranRecieptButton: boolean = false;
  accountbalanceresults: any;
  accountbalance: any;
  balance_value = 0.0;
  dialogConfig: any;
  showAccountData: boolean = false;
  hideRejectBtn: boolean = false;
  btnRejectColor: any;
  rejectBtnText: any;
  rejectionData: any;
  displayBackButton: boolean = true;
  displayApprovalBtn: boolean = false;
  destroy$: Subject<boolean> = new Subject<boolean>();
  constructor(
    private router: Router,
    private fb: FormBuilder,
    private dialog: MatDialog,
    private welfareService: WelfareService,
    private accountsAPI: AccountsService,
    private dataStoreApi: DataStoreService,
    private notificationAPI: NotificationService,
    private transactionAPI: TransactionExecutionService,
    private accountsNotification: AccountsNotificationService
  ) {
    this.currentUser = JSON.parse(localStorage.getItem('auth-user'));
    this.workclasses = this.currentUser.workclasses;
    this.privileges = this.workclasses.privileges;
    this.functionArray = this.dataStoreApi.getActionsByPrivilege("TRANSACTION MAINTENANCE");
    this.fmData = this.router.getCurrentNavigation().extras.queryParams.formData;
    this.function_type = this.fmData.function_type;
    this.transactionCode = this.fmData.transactionCode;
    this.transactionType = this.fmData.transactionType;
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
    if (this.function_type != 'ENTER') {
      this.notAdd = true;
    }
    this.currentUser = JSON.parse(localStorage.getItem('auth-user'));
    this.createFormData();
    this.initTransactionForm();
    this.createEmptyChequeForm();
    this.getPage();
    this.getTransactions();
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
  createFormData() {
    this.formData = this.fb.group({
      totalAmount: [''],
      transactionCode: [''],
      chequeNo: [''],
      currency: ['KES'],
      exchangeRate: [''],
      transactionType: [this.transactionType, [Validators.required]],
      partTrans: [[]],
      staffCustomerCode: [''],
      transactionDate: [this.transactionDate, Validators.required],
      tellerAccount: [''],
      chequeInstruments: [[]],
      conductedBy: ["", Validators.required]
    });
  }
  createPopulatedFormData() {
    this.transactionType = this.resData.transactionType;
    this.formData = this.fb.group({
      sn: [this.resData.sn],
      totalAmount: [this.resData.totalAmount],
      transactionCode: [this.resData.transactionCode],
      chequeNo: [this.resData.chequeNo],
      currency: [this.resData.currency],
      exchangeRate: [this.resData.exchangeRate],
      transactionType: [this.resData.transactionType, [Validators.required]],
      partTrans: [this.resData.partTrans],
      enteredBy: [this.resData.enteredBy],
      verifiedFlag: [this.resData.verifiedFlag],
      verifiedFlag_2: [this.resData.verifiedFlag_2],
      verifiedBy_2: [this.resData.verifiedBy_2],
      enteredFlag: [this.resData.enteredFlag],
      enteredTime: [this.resData.enteredTime],
      entityId: [this.resData.entityId],
      chequeInstruments: [this.resData.chequeInstruments],
      conductedBy: [this.resData.conductedBy]
    });
  }
  createChequeForm() {
    this.chequeForm = this.fb.group({
      sn: [''],
      instrumentDate: ['', [Validators.required]],
      instrumentNo: ['', [Validators.required]],
      instrumentType: ['CHQ', [Validators.required]],
      leafNo: ['', [Validators.required]],
    });
    if (this.notAdd) {
      this.chequeForm.patchValue({
        sn: this.resData.chequeInstruments[0].sn,
        instrumentDate: this.resData.chequeInstruments[0].instrumentDate,
        instrumentNo: this.resData.chequeInstruments[0].instrumentNo,
        instrumentType: this.resData.chequeInstruments[0].instrumentType,
        leafNo: this.resData.chequeInstruments[0].leafNo,
      });
    }
  }
  createEmptyChequeForm() {
    this.chequeForm = this.fb.group({});
  }
  onWelfareStatusChange(event: MatCheckboxChange): void {
    if (event.checked) {
      this.welfareON = true;
      this.getWelfare();
    } else if (!event.checked) {
      this.welfareON = false;
      this.transactionForm.controls.acid.enable();
      this.transactionForm.controls.partTranType.enable();
      this.transactionForm.patchValue({
        welfareCode: '',
        acid: '',
        partTranType: '',
        transactionParticulars: '',
        welfareAction: '',
        conductedBy: '',
        welfareMemberCode: '',
      });
    }
  }

  getWelfare() {
    this.loading = true;
    this.welfareService.all().pipe(takeUntil(this.destroy$)).subscribe(
      (res) => {
        this.welfareTypeArray = res.entity;
        this.loading = false;
      }
    )
  }
  onWelfareTypeChange(): void {
    this.welfareTypeArray.forEach((element: { welfareCode: any; actions: any; }) => {
      if (element.welfareCode == this.transactionForm.value.welfareCode) {
        this.welfareActionsArray = element.actions;
      }
    });
  }
  onWelfareActionChange(): void {
    this.welfareActionsArray.forEach((element: { actionCode: any; actionAccount: any; tranAction: any; actionName: any; allowAccountChange: any; }) => {
      if (element.actionCode == this.transactionForm.value.welfareAction) {
        this.transactionForm.patchValue({ acid: element.actionAccount });
        this.transactionForm.patchValue({ partTranType: element.tranAction });
        this.transactionForm.patchValue({ transactionParticulars: element.actionName });
        if (!element.allowAccountChange) {
          this.transactionForm.controls.acid.disable();
          this.transactionForm.controls.partTranType.disable();
        }
      }
    });
  }
  setReadonlys() {
    this.accountReadonly = true;
    this.partTranTypeReadonly = true;
    this.transactionParticularsReadonly = true;
    this.transactionTypeReadonly = true;
  }
  resetReadonlys() {
    this.accountReadonly = false;
    this.partTranTypeReadonly = false;
    this.transactionParticularsReadonly = false;
    this.initTransactionForm();
    if (this.transactionType == 'TRANSFER') {
      this.formData.patchValue({
        tellerAccount: this.currentUser.tellerAc,
        conductedBy: this.currentUser.firstName + " " + this.currentUser.lastName
      });
    } else if (this.transactionType == 'POST EXPENSE') {
      this.formData.patchValue({
        tellerAccount: this.currentUser.tellerAc,
        conductedBy: this.currentUser.firstName + " " + this.currentUser.lastName
      });
    }
    else if (this.transactionType == 'POST OFFICE JOURNALS') {
      this.formData.patchValue({
        tellerAccount: this.currentUser.tellerAc,
        conductedBy: this.currentUser.firstName + " " + this.currentUser.lastName
      });
    }
    else if (this.transactionType == 'RECONCILE ACCOUNTS') {
      this.formData.patchValue({
        tellerAccount: this.currentUser.tellerAc,
        conductedBy: this.currentUser.firstName + " " + this.currentUser.lastName
      });
    }
    else if (this.transactionType == 'PROCESS CHEQUE') {
      this.transactionForm.patchValue({
        conductedBy: this.currentUser.firstName + " " + this.currentUser.lastName
      });
    }
  }
  formatForm() {
    if (this.transactionType == 'PROCESS CHEQUE') {
      this.createChequeForm();
      this.showChequeDetails = true;
    }
  }
  transactionTypeSelected(event: any) {

  }
  initTransactionForm() {
    this.transactionForm = this.fb.group({
      accountBalance: [""],
      accountType: [""],
      acid: ["", Validators.required],
      batchCode: [''],
      chargeFee: [''],
      currency: ["KES"],
      exchangeRate: ["1"],
      isWelfare: [""],
      isoFlag: ["Y"],
      partTranType: ["", Validators.required],
      parttranIdentity: [''],
      sn: [""],
      transactionAmount: ["", Validators.required],
      transactionCode: [""],
      transactionDate: [new Date()],
      transactionParticulars: ["", Validators.required],
      welfareAction: [""],
      welfareCode: [""],
      conductedBy: [""],
      welfareMemberCode: [""]
    });
  }
  handleFeeChange(event: MatRadioChange) {
    if (event.value == true) {
    }
    else if (event.value == false) {
    }
  }
  customerLookup(): void {
    const dialogRef = this.dialog.open(UniversalMembershipLookUpComponent, {
      width: '50%',
      autoFocus: false,
      maxHeight: '90vh'
    });
    dialogRef.afterClosed().subscribe(result => {
      this.customer_lookup = result.data;
      this.customer_account_id = this.customer_lookup.id;
      this.customer_account_code = this.customer_lookup.customerCode;
      this.transactionForm.controls.welfareMemberCode.setValue(this.customer_lookup.customerCode);
      if (this.customer_account_code === null || this.customer_account_code === undefined || this.customer_account_code === '-') {
        this.onMemberDetailsLookup = false;
      } else {
        this.onMemberDetailsLookup = true;
      }
    });
  }
  // memberDetailsLookup(data: number) {
  //   if (this.customer_lookup.customerType == '12') {
  //     this.dialog.open(GroupMembershipDetailsComponent, {
  //       maxWidth: '100vw',
  //       maxHeight: '100vh',
  //       height: '100%',
  //       width: '100%',
  //       panelClass: 'full-screen-modal',
  //       data: this.customer_account_id
  //     });
  //   } else if (this.customer_lookup.customerType !== '12') {
  //     this.dialog.open(MemberDetailsComponent, {
  //       maxWidth: '100vw',
  //       maxHeight: '100vh',
  //       height: '100%',
  //       width: '100%',
  //       panelClass: 'full-screen-modal',
  //       data: this.customer_account_id
  //     });
  //   }
  // }
  memberDocuments() {
    this.dialogConfig = new MatDialogConfig();
    this.dialogConfig.disableClose = false;
    this.dialogConfig.autoFocus = true;
    this.dialogConfig.width = "30%";
    this.dialogConfig.data = this.customerCode;
    const dialogRef = this.dialog.open(MemberDocumentsComponent, this.dialogConfig);
    dialogRef.afterClosed().subscribe(
      (_res: any) => {
        this.loading = false;
      })
  }

  initTransactionArray() {
    this.transactionArray = new Array();
  }
  getTransactions() {
    this.dataSource = new MatTableDataSource(this.transactionArray);
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
  }
  addToArray() {
    if (this.welfareON) {
      if (this.transactionForm.value.welfareMemberCode == ''
        || this.transactionForm.value.welfareAction == ''
        || this.transactionForm.value.welfareCode == '') {
        this.notificationAPI.alertWarning("Fill all welfare details!");
        return;
      }
    }
    if (this.transactionForm.valid) {
      this.transactionArray.push(this.transactionForm.getRawValue());
      this.formData.patchValue({
        partTrans: this.transactionArray,
      });
      this.transactionForm.controls.acid.enable();
      this.validateData();
      this.accountBalance = '';
      this.accountCurrency = '';
      this.accountName = '';
      this.accountStatus = '';
      this.welfareON = false;
    }
  }
  validateData() {
    this.transactionArray.forEach((element) => {
      if (element.partTranType == 'Debit') {
        this.debit_value = this.debit_value + element.transactionAmount;
      } else if (element.partTranType == 'Credit') {
        this.credit_value = this.credit_value + element.transactionAmount;
      }
    });

    this.Valid = true;
    this.formData.patchValue({
      totalAmount: this.credit_value,
    });
    this.getTransactions();
    this.resetReadonlys();
  }

  editItem(data: any) {
    this.index = this.transactionArray.indexOf(data);
    if (this.index == 0) {
      this.setReadonlys();
    }
    this.editButton = true;
    this.addButton = false;
    this.transactionForm.patchValue({
      sn: data.sn,
      acid: data.acid,
      partTranSn: data.partTranSn,
      exchangeRate: data.exchangeRate,
      partTranType: data.partTranType,
      transactionAmount: data.transactionAmount,
      transactionDate: data.transactionDate,
      conductedBy: data.conductedBy,
      transactionParticulars: data.transactionParticulars,
    });
  }
  updateTransactionDetails() {
    this.editButton = false;
    this.addButton = true;
    this.transactionArray[this.index] = this.transactionForm.value;
    this.formData.patchValue({
      partTrans: this.transactionArray,
    });
    this.validateData();
    this.accountBalance = '';
    this.accountCurrency = '';
    this.accountName = '';
    this.accountStatus = '';
  }
  deleteItem(i: any) {
    this.transactionArray.splice(i, 1);
    this.validateData();
  }

  get f() {
    return this.formData.controls;
  }
  accountLookup(): void {
    const dialogRef = this.dialog.open(
      GeneralAccountsLookupComponent, {
      width: '50%'
    });
    dialogRef.afterClosed().pipe(takeUntil(this.destroy$)).subscribe(
      {
        next: (
          (res) => {
            this.accountlookupData = res.data;
            this.account_acid_id = this.accountlookupData.acid;
            this.customerCode = this.accountlookupData.customerCode;
            this.transactionForm.patchValue({
              acid: this.accountlookupData.acid
            });
            if (this.accountlookupData.acid === null || this.accountlookupData.acid === undefined || this.accountlookupData.acid === '-') {
              this.onAccountDetailsLookup = false;
              this.showAccountImagesLookup = false;
            } else {
              this.onAccountDetailsLookup = true;
              this.showAccountImagesLookup = true;
            } this.loading = true;
            this.transactionAPI.getAccountDetails(this.accountlookupData.acid).pipe(takeUntil(this.destroy$)).subscribe(
              {
                next: (
                  (res) => {
                    if (res.statusCode === 200) {
                      this.results = res.entity;
                      this.loading = false;
                      this.showAccountData = true;
                      this.accountBalance = this.results.account_balance;
                      this.accountCurrency = this.results.currency;
                      this.accountName = this.results.account_name;
                      this.accountStatus = this.results.account_status;
                      this.transactionForm.patchValue({
                        currency: this.results.currency,
                        accountBalance: this.results.account_balance,
                        accountType: this.results.account_type,
                      });
                    } else {
                      this.loading = false;
                      this.showAccountData = false;
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

  accountImagesLookup() {
    if (this.transactionForm.value.acid) {
      const dialogConfig = new MatDialogConfig();
      dialogConfig.disableClose = false;
      dialogConfig.autoFocus = true;
      dialogConfig.data = {
        data: this.transactionForm.value.acid,
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

  disableForms() {
    this.showTransactionsOnly = true;
    this.formData.disable();
    this.transactionForm.disable();
    this.chequeForm.disable();
    this.disableAddButton = true;
    this.disableActions = true;
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

          // if you want to open PDF in new tab
          window.open(url);
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
        },
        (err) => {
          this.loading = false;
          this.notificationAPI.alertWarning("Error generating reciept !");
        }
      );
  }

  noneAddFunctions() {
    if (this.resData.transactionType == 'PROCESS CHEQUE') {
      this.showChequeDetails = true;
      if (this.resData.postedFlag == 'Y') {
        this.showTranRecieptButton = true;
      }
    }

    if (this.resData.transactionType == 'CASH DEPOSIT') {
      if (this.resData.postedFlag == 'Y') {
        this.showRecieptButton = true;
      } else if (this.resData.postedFlag == 'N') {
        this.showRecieptButton = false;
      }
    }
  }

  getPage() {
    if (this.function_type == 'ENTER') {
      this.btnColor = 'primary';
      this.submitData = 'ENTER';
      this.disableAddButton = false;
      this.createFormData();
      this.formatForm();
    }
    else if (this.function_type == 'INQUIRE') {
      this.loading = true;
      this.disableForms();
      this.hideSubmit = true;
      this.transactionAPI.getTransactionByCode(this.transactionCode).pipe(takeUntil(this.destroy$)).subscribe(
        (res) => {
          this.resData = res.entity;
          if (this.resData.transactionType === "PETTY CASH") {
          }
          if (this.resData.transactionType === "CASH DEPOSIT") {
            if (this.resData.verifiedFlag_2 == 'Y') {
              this.showRecieptButton = true;
              this.showWithdrawalTranRecieptButton = false;
            } else if (this.resData.verifiedFlag_2 == 'N') {
              this.showRecieptButton = false;
              this.showWithdrawalTranRecieptButton = false;
            }
          }
          if (this.resData.transactionType === "CASH WITHDRAWAL") {
            if (this.resData.verifiedFlag_2 == 'Y') {
              this.showRecieptButton = false;
              this.showWithdrawalTranRecieptButton = true;
            } else if (this.resData.verifiedFlag_2 == 'N') {
              this.showRecieptButton = false;
              this.showWithdrawalTranRecieptButton = false;
            }
          }
          this.loading = false;
          this.createPopulatedFormData();
          this.transactionArray = this.resData.partTrans;
          this.transactionCode = res.entity.transactionCode;
          this.enteredBy = res.entity.enteredBy;
          this.verifiedFlag_2 = res.entity.verifiedFlag_2;
          this.verifiedFlag = res.entity.verifiedFlag;
          this.verifiedBy_2 = res.entity.verifiedBy_2;
          this.conductedBy = res.entity.conductedBy;
          this.getTransactions();
          this.noneAddFunctions();
          this.validateData();
          this.showtransactionData = true;
        },
        (err) => {
          this.error = err;
          this.notificationAPI.alertWarning(err);
        }
      );
    } else if (this.function_type == 'MODIFY') {
      this.loading = true;
      this.transactionAPI.getTransactionByCode(this.transactionCode).pipe(takeUntil(this.destroy$)).subscribe(
        (res) => {
          this.resData = res.entity;
          if (this.resData.status == "Entered") {
            this.btnColor = 'primary';
            this.submitData = 'MODIFY';
          } else if (this.resData.status !== "Entered") {
            this.btnColor = '';
            this.submitData = '';
          }
          this.loading = false;
          this.disableAddButton = false;
          this.createPopulatedFormData();
          this.transactionArray = this.resData.partTrans;
          this.transactionCode = res.entity.transactionCode;
          this.enteredBy = res.entity.enteredBy;
          this.verifiedFlag_2 = res.entity.verifiedFlag_2;
          this.verifiedFlag = res.entity.verifiedFlag;
          this.verifiedBy_2 = res.entity.verifiedBy_2;
          this.conductedBy = res.entity.conductedBy;
          this.getTransactions();
          this.noneAddFunctions();
          this.validateData();
          this.showtransactionData = true;
        },
        (err) => {
          this.error = err;
          this.notificationAPI.alertWarning(err);
        }
      );
    }
    else if (this.function_type == 'VERIFY') {

      this.loading = true;
      this.activateDelete = true;
      this.loading = true;
      this.showApprovalbtn = true;
      this.disableForms();
      this.transactionAPI.getTransactionByCode(this.transactionCode).pipe(takeUntil(this.destroy$)).subscribe(
        (res) => {
          this.loading = false;
          this.resData = res.entity;
          this.rejectionData = res.entity;
          if (this.rejectionData.postedFlag == 'Y') {
            this.hideRejectBtn = false;
          } else if (this.rejectionData.postedFlag == 'N') {
            this.btnRejectColor = 'accent';
            this.rejectBtnText = 'REJECT';
            this.hideRejectBtn = true;
          }
          this.createPopulatedFormData();
          this.transactionArray = this.resData.partTrans;
          this.transactionCode = res.entity.transactionCode;
          this.enteredBy = res.entity.enteredBy;
          this.verifiedFlag_2 = res.entity.verifiedFlag_2;
          this.verifiedFlag = res.entity.verifiedFlag;
          this.verifiedBy_2 = res.entity.verifiedBy_2;
          this.conductedBy = res.entity.conductedBy;
          // this.checkVerifyPermission();
          if (this.transactionType === "CASH DEPOSIT") {
            if (this.resData.postedFlag == 'Y') {
              this.showRecieptButton = true;
              this.btnText = '';
              this.showWithdrawalTranRecieptButton = false;
            } else if (this.resData.postedFlag == 'N') {
              this.showRecieptButton = false;
              this.showWithdrawalTranRecieptButton = false;
            }
          }
          if (this.transactionType === "CASH WITHDRAWAL") {
            if (this.resData.postedFlag == 'Y') {
              this.showRecieptButton = false;
              this.btnText = '';
              this.showWithdrawalTranRecieptButton = true;
            } else if (this.results.postedFlag == 'N') {
              this.showRecieptButton = false;
              this.showWithdrawalTranRecieptButton = false;
            }
          }
          this.getTransactions();
          this.formData.disable();
          this.disableAddButton = true;
          this.noneAddFunctions();
          this.validateData();
          this.showtransactionData = true;
        },
        (err) => {
          this.error = err;
          this.notificationAPI.alertWarning(err);
        }
      );
    }
    else if (this.function_type == 'PAYBILL WITHDRAWAL') {
      this.btnColor = 'primary';
      this.submitData = 'INITIATE PAYBILL WITHDRAWAL';
      this.activateDelete = true;
      this.loading = true;
      this.disableForms();
      this.transactionAPI.getTransactionByCode(this.transactionCode).pipe(takeUntil(this.destroy$)).subscribe(
        (res) => {
          this.resData = res.entity;
          this.loading = false;
          this.rejectionData = res.entity;
          if (this.rejectionData.postedFlag == 'Y') {
            this.hideRejectBtn = false;
          } else if (this.rejectionData.postedFlag == 'N') {
            this.btnRejectColor = 'accent';
            this.rejectBtnText = 'REJECT';
            this.hideRejectBtn = true;
          }
          this.createPopulatedFormData();
          this.transactionArray = this.resData.partTrans;
          this.transactionCode = res.entity.transactionCode;
          this.enteredBy = res.entity.enteredBy;
          this.verifiedFlag_2 = res.entity.verifiedFlag_2;
          this.verifiedFlag = res.entity.verifiedFlag;
          this.verifiedBy_2 = res.entity.verifiedBy_2;
          this.conductedBy = res.entity.conductedBy;
          if (this.transactionType === "CASH DEPOSIT") {
            if (this.resData.postedFlag == 'Y') {
              this.showRecieptButton = true;
              this.btnText = '';
              this.showWithdrawalTranRecieptButton = false;
            } else if (this.resData.postedFlag == 'N') {
              this.showRecieptButton = false;
              this.showWithdrawalTranRecieptButton = false;
            }
          }
          if (this.transactionType === "CASH WITHDRAWAL") {
            if (this.resData.postedFlag == 'Y') {
              this.showRecieptButton = false;
              this.btnText = '';
              this.showWithdrawalTranRecieptButton = true;
            } else if (this.results.postedFlag == 'N') {
              this.showRecieptButton = false;
              this.showWithdrawalTranRecieptButton = false;
            }
          }
          this.getTransactions();
          this.formData.disable();
          this.disableAddButton = true;
          this.noneAddFunctions();
          this.validateData();
          this.showtransactionData = true;
        },
        (err) => {
          this.error = err;
          this.notificationAPI.alertWarning(err);
        }
      );
    }
    else if (this.function_type == 'ACKNOWLEDGE') {
      this.btnColor = 'primary';
      this.submitData = 'ACKNOWLEDGE';
      this.activateDelete = true;
      this.loading = true;
      this.disableForms();
      this.transactionAPI.getTransactionByCode(this.transactionCode).pipe(takeUntil(this.destroy$)).subscribe(
        (res) => {
          this.resData = res.entity;
          this.loading = false;
          this.rejectionData = res.entity;
          if (this.rejectionData.postedFlag == 'Y') {
            this.hideRejectBtn = false;
          } else if (this.rejectionData.postedFlag == 'N') {
            this.btnRejectColor = 'accent';
            this.rejectBtnText = 'REJECT';
            this.hideRejectBtn = true;
          }
          this.createPopulatedFormData();
          this.transactionArray = this.resData.partTrans;
          this.transactionCode = res.entity.transactionCode;
          this.enteredBy = res.entity.enteredBy;
          this.verifiedFlag_2 = res.entity.verifiedFlag_2;
          this.verifiedFlag = res.entity.verifiedFlag;
          this.verifiedBy_2 = res.entity.verifiedBy_2;
          this.conductedBy = res.entity.conductedBy;
          if (this.transactionType === "CASH DEPOSIT") {
            if (this.resData.postedFlag == 'Y') {
              this.showRecieptButton = true;
              this.btnText = '';
              this.showWithdrawalTranRecieptButton = false;
            } else if (this.resData.postedFlag == 'N') {
              this.showRecieptButton = false;
              this.showWithdrawalTranRecieptButton = false;
            }
          }
          if (this.transactionType === "CASH WITHDRAWAL") {
            if (this.resData.postedFlag == 'Y') {
              this.showRecieptButton = false;
              this.btnText = '';
              this.showWithdrawalTranRecieptButton = true;
            } else if (this.results.postedFlag == 'N') {
              this.showRecieptButton = false;
              this.showWithdrawalTranRecieptButton = false;
            }
          }
          this.getTransactions();
          this.formData.disable();
          this.disableAddButton = true;
          this.noneAddFunctions();
          this.validateData();
          this.showtransactionData = true;
        },
        (err) => {
          this.error = err;
          this.notificationAPI.alertWarning(err);
        }
      );
    }
    else if (this.function_type == 'REVERSE TRANSACTIONS'  || this.function_type == 'SYSTEM REVERSAL' ) {
      this.btnColor = 'primary';
      this.submitData = 'REVERSE';
      this.activateDelete = true;
      this.loading = true;
      this.disableForms();
      this.transactionAPI.getTransactionByCode(this.transactionCode).pipe(takeUntil(this.destroy$)).subscribe(
        (res) => {
          this.resData = res.entity;
          this.loading = false;
          this.createPopulatedFormData();
          this.rejectionData = res.entity;
          if (this.rejectionData.postedFlag == 'Y') {
            this.hideRejectBtn = false;
          } else if (this.rejectionData.postedFlag == 'N') {
            this.btnRejectColor = 'accent';
            this.rejectBtnText = 'REJECT';
            this.hideRejectBtn = true;
          }
          this.transactionArray = this.resData.partTrans;
          this.transactionCode = res.entity.transactionCode;
          this.enteredBy = res.entity.enteredBy;
          this.verifiedFlag_2 = res.entity.verifiedFlag_2;
          this.verifiedFlag = res.entity.verifiedFlag;
          this.verifiedBy_2 = res.entity.verifiedBy_2;
          this.conductedBy = res.entity.conductedBy;
          this.getTransactions();
          this.formData.disable();
          this.disableAddButton = true;
          this.noneAddFunctions();
          this.validateData();
          this.showtransactionData = true;
        },
        (err) => {
          this.error = err;
          this.notificationAPI.alertWarning(err);
        }
      );
    }
    else if (this.function_type == 'POST') {
      this.btnColor = 'primary';
      this.submitData = 'POST';
      this.activateDelete = true;
      this.loading = true;
      this.disableForms();
      this.transactionAPI.getTransactionByCode(this.transactionCode).pipe(takeUntil(this.destroy$)).subscribe(
        (res) => {
          this.resData = res.entity;
          this.loading = false;
          this.createPopulatedFormData();
          this.rejectionData = res.entity;
          if (this.rejectionData.postedFlag == 'Y') {
            this.hideRejectBtn = false;
          } else if (this.rejectionData.postedFlag == 'N') {
            this.btnRejectColor = 'accent';
            this.rejectBtnText = 'REJECT';
            this.hideRejectBtn = true;
          }
          this.transactionArray = this.resData.partTrans;
          this.transactionCode = res.entity.transactionCode;
          this.enteredBy = res.entity.enteredBy;
          this.verifiedFlag_2 = res.entity.verifiedFlag_2;
          this.verifiedFlag = res.entity.verifiedFlag;
          this.verifiedBy_2 = res.entity.verifiedBy_2;
          this.conductedBy = res.entity.conductedBy;
          if (this.transactionType === "CASH DEPOSIT") {
            if (this.resData.postedFlag == 'Y') {
              this.showRecieptButton = true;
              this.btnText = '';
              this.showWithdrawalTranRecieptButton = false;
            } else if (this.resData.postedFlag == 'N') {
              this.showRecieptButton = false;
              this.showWithdrawalTranRecieptButton = false;
            }
          }
          if (this.transactionType === "CASH WITHDRAWAL") {
            if (this.resData.postedFlag == 'Y') {
              this.showRecieptButton = false;
              this.btnText = '';
              this.showWithdrawalTranRecieptButton = true;
            } else if (this.results.postedFlag == 'N') {
              this.showRecieptButton = false;
              this.showWithdrawalTranRecieptButton = false;
            }
          }
          this.getTransactions();
          this.formData.disable();
          this.disableAddButton = true;
          this.validateData();
          this.showtransactionData = true;
        },
        (err) => {
          this.error = err;
          this.notificationAPI.alertWarning(err);
        }
      );
    } else if (this.function_type == 'DELETE') {
      this.loading = true;
      this.hideSubmit = true;
      this.activateDelete = true;
      this.disableForms();
      this.transactionAPI.getTransactionByCode(this.transactionCode).pipe(takeUntil(this.destroy$)).subscribe(
        (res) => {
          this.loading = false;
          this.resData = res.entity;
          if (this.resData.status === "Entered") {
            this.btnColor = 'primary';
            this.submitData = 'DELETE';
          } else if (this.resData.status === "Entered") {
            this.btnColor = '';
            this.submitData = '';
          }
          this.createPopulatedFormData();
          this.transactionArray = this.resData.partTrans;
          this.transactionCode = res.entity.transactionCode;
          this.enteredBy = res.entity.enteredBy;
          this.verifiedFlag_2 = res.entity.verifiedFlag_2;
          this.verifiedFlag = res.entity.verifiedFlag;
          this.verifiedBy_2 = res.entity.verifiedBy_2;
          this.conductedBy = res.entity.conductedBy;
          this.getTransactions();
          this.showtransactionData = true;
          this.formData.disable();
          this.disableAddButton = true;
          this.noneAddFunctions();
          this.validateData();
          this.loading = false;
        },
        (err) => {
          this.loading = false;
          this.error = err;
          this.notificationAPI.alertWarning(err);
        }
      );
    }
  }
  checkVerifyPermission() {
    let permitted = false;
    this.functionArray.forEach(element => {
      if (`VERIFY - ${this.resData.transactionType.toUpperCase()}` == element) {
        permitted = true;
      }
    });
    if (!permitted) {
      this.router.navigate(['system/transactions/maintenance']);
    }
  }
  // this.functionArray
  onSubmit() {
    if (this.formData.value.transactionType == 'PROCESS CHEQUE') {
      if (this.chequeForm.valid) {
        this.formData.patchValue({
          chequeInstruments: [this.chequeForm.value],
        });
        this.submit();
      } else {
        this.notificationAPI.alertWarning("lease fill cheque details before submitting!");
      }
    } else {
      this.submit();
    }
  }

  submit() {
    this.loading = true;
    if (this.function_type == 'ENTER') {
      if (this.formData.valid) {
        if (this.transactionArray.length < 1) {
          this.notificationAPI.alertWarning("Please Add Atleast ONE Transaction Before Submitting!");
        } else {
          this.transactionAPI
            .enter(this.formData.value).pipe(takeUntil(this.destroy$))
            .subscribe(
              {
                next: (
                  (res) => {
                    if (res.statusCode === 201) {
                      this.loading = false;
                      this.accountsNotification.alertSuccess(res.message);
                      this.function_type = 'POST';
                      this.transactionCode = res.entity.transactionCode;
                      this.enteredBy = res.entity.enteredBy;
                      this.verifiedFlag_2 = res.entity.verifiedFlag_2;
                      this.verifiedFlag = res.entity.verifiedFlag;
                      this.verifiedBy_2 = res.entity.verifiedBy_2;
                      this.conductedBy = res.entity.conductedBy;
                      this.getPage();
                      this.submitted = true;
                      this.rejectionData = res.entity;
                      if (this.rejectionData.postedFlag == 'Y') {
                        this.hideRejectBtn = false;
                      } else if (this.rejectionData.postedFlag == 'N') {
                        this.btnRejectColor = 'accent';
                        this.rejectBtnText = 'REJECT';
                        this.hideRejectBtn = true;
                      }
                    } else {
                      this.loading = false;
                      this.resMessage = res.message;
                      this.resEntity = res.entity;
                      if (this.resEntity == null) {
                        this.notificationAPI.alertWarning(res.message);
                        // this.isAlert = false;
                      } else {
                        // this.isAlert = true;
                        for (let i = 0; i < this.resEntity.length; i++) {
                          this.notificationAPI.alertWarning(this.resEntity[i].message);
                        }
                      }
                    }
                  }
                ),
                error: (
                  (err) => {
                    this.loading = false;
                    this.notificationAPI.alertWarning("Server Error: !!");
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
      } else {
        this.loading = false;
        this.notificationAPI.alertWarning("Invalid Form! Check your inputs");
      }
    }
    else if (this.function_type == 'MODIFY') {
      if (this.formData.valid) {
        if (this.transactionArray.length < 1) {
          this.notificationAPI.alertWarning("Please Add Atleast ONE Transaction Before Submitting!");
        } else {
          this.transactionAPI
            .updateTransaction(this.formData.value).pipe(takeUntil(this.destroy$))
            .subscribe(
              {
                next: (
                  (res) => {
                    if (res.statusCode === 200) {
                      this.loading = false;
                      this.notificationAPI.alertSuccess(res.message);
                      this.submitted = true;
                      this.router.navigate(['system/transactions/maintenance']);
                    } else {
                      this.loading = false;
                      this.notificationAPI.alertWarning(res.message);
                    }
                  }
                ),
                error: (
                  (err) => {
                    this.loading = false;
                    this.notificationAPI.alertWarning("Server Error: !!");
                    this.getPage();
                  }
                ),
                complete: (
                  () => {

                  }
                )
              }), Subscription;
        }
      } else {
        this.loading = false;
        this.notificationAPI.alertWarning("Invalid Form! Check your inputs");
      }
    }
    else if (this.function_type == 'VERIFY') {
      let params = new HttpParams()
        .set('transactionCode', this.resData.transactionCode);
      this.transactionAPI
        .verify(params).pipe(takeUntil(this.destroy$))
        .subscribe(
          {
            next: (
              (res) => {
                if (res.statusCode === 200) {
                  this.loading = false;
                  this.notificationAPI.alertSuccess(res.message);
                  this.router.navigate(['system/transactions/maintenance']);
                } else {
                  this.loading = false;
                  this.notificationAPI.alertWarning(res.message);
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
        ), Subscription;
    }
    else if (this.function_type == 'ACKNOWLEDGE') {
      let params = new HttpParams()
        .set('transactionCode', this.resData.transactionCode);
      this.transactionAPI.getAcknowledgement(params).pipe(takeUntil(this.destroy$)).subscribe(
        {
          next: (
            (res) => {
              if (res.statusCode === 200) {
                this.loading = false;
                this.notificationAPI.alertSuccess(res.message);
                this.router.navigate(['system/transactions/maintenance']);
              } else {
                this.loading = false;
                this.notificationAPI.alertWarning(res.message);
              }
            }
          ),
          error: (
            (err) => {
              this.loading = false;
              this.notificationAPI.alertWarning(" Server Error: !!");
            }
          ),
          complete: (
            () => {

            }
          )
        }
      ), Subscription;

    }
    else if (this.function_type == 'REVERSE TRANSACTIONS') {
      let params = new HttpParams()
        .set('transactionCode', this.resData.transactionCode);
      this.transactionAPI
        .reverse(params).pipe(takeUntil(this.destroy$))
        .subscribe(
          {
            next: (
              (res) => {
                if (res.statusCode === 200) {
                  this.loading = false;
                  this.notificationAPI.alertSuccess(res.message);
                  this.router.navigate(['system/transactions/maintenance']);
                } else {
                  this.loading = false;
                  this.notificationAPI.alertWarning(res.message);
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
        ), Subscription;
    }
    else if (this.function_type == 'SYSTEM REVERSAL') {
      let params = new HttpParams()
        .set('transactionCode', this.resData.transactionCode);
      this.transactionAPI
        .systemReversal(params).pipe(takeUntil(this.destroy$))
        .subscribe(
          {
            next: (
              (res) => {
                if (res.statusCode === 200) {
                  this.loading = false;
                  this.notificationAPI.alertSuccess(res.message);
                  this.router.navigate(['system/transactions/maintenance']);
                } else {
                  this.loading = false;
                  this.notificationAPI.alertWarning(res.message);
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
        ), Subscription;
    }
    else if (this.function_type == 'POST') {
      this.params = new HttpParams()
        .set('transactionCode', this.resData.transactionCode);
      this.transactionAPI
        .post(this.params).pipe(takeUntil(this.destroy$))
        .subscribe(
          {
            next: (
              (res) => {
                if (res.statusCode === 200) {
                  this.loading = false;
                  this.notificationAPI.alertSuccess(res.message);
                  if (this.resData.postedFlag == 'Y') {
                    this.showRecieptButton = true;
                  } else if (this.resData.postedFlag == 'N') {
                    this.showRecieptButton = false;
                  }
                  this.rejectionData = res.entity;
                  if (this.rejectionData.postedFlag == 'Y') {
                    this.hideRejectBtn = false;
                  } else if (this.rejectionData.postedFlag == 'N') {
                    this.btnRejectColor = 'accent';
                    this.rejectBtnText = 'REJECT';
                    this.hideRejectBtn = true;
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
    else if (this.function_type == 'DELETE') {
      let params = new HttpParams().set('id', this.resData.sn);
      this.transactionAPI.deleteTransactionById(params).pipe(takeUntil(this.destroy$)).subscribe(
        {
          next: (
            (res) => {
              if (res.statusCode === 200) {
                this.loading = false;
                this.notificationAPI.alertSuccess(res.message);
                this.router.navigate(['system/transactions/maintenance']);
              } else {
                this.loading = false;
                this.notificationAPI.alertSuccess(res.message);
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
    this.dialogConfig.data = this.resData
    const dialogRef = this.dialog.open(TransactionApprovalComponent, this.dialogConfig);
    dialogRef.afterClosed().subscribe(
      (_res: any) => {
        this.loading = false;
      });
  }
}
