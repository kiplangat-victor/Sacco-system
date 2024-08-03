import { HttpParams } from '@angular/common/http';
import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { Router } from '@angular/router';
import { DataTableDirective } from 'angular-datatables';
import { Subject, Subscription } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';
import { AccountsNotificationService } from 'src/@core/helpers/accounts/accounts-notification.service';
import { DataStoreService } from 'src/@core/helpers/data-store.service';
import { GeneralAccountsLookupComponent } from '../../Account-Component/general-accounts-lookup/general-accounts-lookup.component';
import { AccountImagesLookupComponent } from '../account-images-lookup/account-images-lookup.component';
import { TransactionExecutionService } from '../transaction-execution.service';

@Component({
  selector: 'app-normal-cash-transactions',
  templateUrl: './normal-cash-transactions.component.html',
  styleUrls: ['./normal-cash-transactions.component.scss']
})
export class NormalCashTransactionsComponent implements OnInit, OnDestroy {
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

  transactionTypeArray: any;
  showTranRecieptButton: boolean = false;
  showtransactionData: boolean = false;
  enteredBy: any;
  verifiedFlag_2: any;
  verifiedFlag: any;
  verifiedBy_2: any;
  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();
    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }
  @ViewChild(DataTableDirective) dtElement: DataTableDirective;
  //dtOptions: DataTables.Settings = {};
  dtTrigger: Subject<any> = new Subject<any>();
  spinnerVisible: boolean = true;
  errorVisible: boolean = false;
  transactionTabIndex: number = 1;

  debit_value = 0.0;
  credit_value = 0.0;
  total_value = 0.0;
  functionArray: any;
  params: any;
  resMessage: any;
  resEntity: any;
  transactionType: string = 'NC';
  customerCode!: string;
  Valid = true;
  //unverified Transactions
  loading = false;
  submitted = false;
  currency: any;
  exchangeRate: any;
  partTrans: any;
  partTranType: any;
  transactionAmount: any;
  accountlookupData: any;
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
  hideBtn = false;
  btnColor = '';
  btnText = '';
  showTransactionsOnly = false;
  notAdd = false;
  dateToday: any;
  isAlert: boolean = false;
  transactionDate = new Date();
  destroy$: Subject<boolean> = new Subject<boolean>();
  showTransactionList: boolean = false;
  showTransactionForm: boolean = true;
  showWithdrawalTranRecieptButton: boolean = false;
  constructor(
    private router: Router,
    private fb: FormBuilder,
    private dialog: MatDialog,
    private dataStoreApi: DataStoreService,
    private notificationAPI: NotificationService,
    private transactionAPI: TransactionExecutionService,
    private accountsNotification: AccountsNotificationService,
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
    if (this.function_type != 'ENTER') {
      this.notAdd = true;
    }
    this.createFormData();
    this.initTransactionForm();
    this.createEmptyChequeForm();
    this.getPage();
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
      chequeInstruments: [[]]
    });
  }
  createPopulatedFormData() {
    this.transactionType = this.resData.transactionType;
    this.transactionArray = this.resData.partTrans;
    this.formData = this.fb.group({
      sn: [this.resData.sn],
      totalAmount: [this.resData.totalAmount],
      transactionCode: [this.resData.transactionCode],
      chequeNo: [this.resData.chequeNo],
      currency: [this.resData.currency],
      exchangeRate: [this.resData.exchangeRate],
      transactionType: [this.resData.transactionType, [Validators.required]],
      enteredBy: [this.resData.enteredBy],
      verifiedFlag_2: [this.resData.verifiedFlag_2],
      verifiedBy_2: [this.resData.verifiedBy_2],
      enteredFlag: [this.resData.enteredFlag],
      enteredTime: [this.resData.enteredTime],
      entityId: [this.resData.entityId],
      partTrans: [[]],
      chequeInstruments: [[]]
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

    if (this.transactionType == 'CASH DEPOSIT') {
      this.formData.patchValue({
        transactionParticulars: 'CASH DEPOSIT',
        tellerAccount: this.currentUser.tellerAc
      });
    } else if (this.transactionType == 'CASH WITHDRAWAL') {
      this.formData.patchValue({
        transactionParticulars: 'CASH WITHDRAWAL',
        tellerAccount: this.currentUser.tellerAc
      });
    }
    else if (this.transactionType == 'PETTY CASH') {
      this.formData.patchValue({
        transactionParticulars: 'PETTY CASH',
        tellerAccount: this.currentUser.tellerAc
      });
    }
    else if (this.transactionType == 'FUND TELLER') {
      this.formData.patchValue({
        transactionParticulars: 'FUND TELLER',
        tellerAccount: this.currentUser.tellerAc
      });
    }
    else if (this.transactionType == 'COLLECT TELLER FUND') {
      this.formData.patchValue({
        transactionParticulars: 'COLLECT TELLER FUND',
        tellerAccount: this.currentUser.tellerAc
      });
    }
  }
  formatForm() {
    if (this.transactionType == 'CASH DEPOSIT') {
      this.setReadonlys();
      this.transactionForm.patchValue({
        partTranType: 'Credit',
        transactionParticulars: 'CASH DEPOSIT',
      });
    } else if (this.transactionType == 'CASH WITHDRAWAL') {
      this.setReadonlys();
      this.transactionForm.patchValue({
        partTranType: 'Debit',
        transactionParticulars: 'CASH WITHDRAWAL'
      });
    }
    else if (this.transactionType == 'PETTY CASH') {
      this.setReadonlys();
      this.transactionForm.patchValue({
        partTranType: 'Debit',
        transactionParticulars: 'PETTY CASH'
      });
    }
    else if (this.transactionType == 'FUND TELLER') {
      this.setReadonlys();
      this.transactionForm.patchValue({
        partTranType: 'Debit',
        transactionParticulars: 'FUND TELLER'
      });
    }
    else if (this.transactionType == 'COLLECT TELLER FUND') {
      this.setReadonlys();
      this.transactionForm.patchValue({
        partTranType: 'Credit',
        transactionParticulars: 'COLLECT TELLER FUND'
      });
    }
  }
  transactionTypeSelected(event: any) {
  }
  initTransactionForm() {
    this.transactionForm = this.fb.group({
      isWelfare: [false],
      welfareCode: [''],
      welfareAction: [''],
      welfareMemberCode: [''],
      sn: [''],
      acid: ['', Validators.required],
      isoFlag: 'Y',
      partTranSn: [''],
      exchangeRate: [1],
      partTranType: ['', Validators.required],
      transactionAmount: ['', Validators.required],
      transactionDate: [this.transactionDate, Validators.required],
      transactionParticulars: ['', Validators.required],
      currency: ['KES'],
      accountType: [''],
      accountBalance: [''],
    });
  }

  initTransactionArray() {
    this.transactionArray = new Array();
  }
  addToArray() {
    if (this.transactionForm.valid) {
      this.transactionArray.push(this.transactionForm.value);
      this.formData.patchValue({
        partTrans: this.transactionArray
      });
      this.transactionForm.controls.acid.enable();
      this.validateData();
      this.accountBalance = '';
      this.accountCurrency = '';
      this.accountName = '';
      this.accountStatus = '';
    }
  }
  getTransactions() {
    this.dataSource = new MatTableDataSource(this.transactionArray);
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
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
    this.resetReadonlys();
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
            this.loading = true;
            this.transactionForm.patchValue({
              acid: this.accountlookupData.acid
            });
            this.transactionAPI.getAccountDetails(this.accountlookupData.acid).pipe(takeUntil(this.destroy$)).subscribe(
              {
                next: (
                  (res) => {
                    if (res.statusCode === 200) {
                      this.results = res.entity;
                      this.loading = false;
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
      .generateWithdrawalReciept(params).pipe(takeUntil(this.destroy$))
      .subscribe(
        (res) => {
          this.results = res;
          this.loading = false;
          this.results = res;
          this.loading = false;
          this.notificationAPI.alertSuccess("downloading " + this.results.filename);
          let url = window.URL.createObjectURL(res.data);
          window.URL.revokeObjectURL(url);
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
          this.results = res;
          this.notificationAPI.alertSuccess("downloading " + this.results.filename);
          let url = window.URL.createObjectURL(res.data);
          window.URL.revokeObjectURL(url);
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
    }
    if (this.resData.postedFlag == 'Y') {
      this.showTranRecieptButton = true;
    }
    if (this.resData.transactionType == 'CASH DEPOSIT') {
      if (this.resData.postedFlag == 'Y') {
        this.showRecieptButton = true;
      } else if (this.resData.postedFlag == 'N') {
        this.showRecieptButton = false;
      }
    }
    if (this.resData.transactionType == 'CASH WITHDRAWAL') {
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
     else if (this.function_type == 'POST') {
      this.btnColor = 'primary';
      this.submitData = 'POST';
      this.activateDelete = true;
      this.loading = true;
      this.transactionAPI.getTransactionByCode(this.transactionCode).pipe(takeUntil(this.destroy$)).subscribe(
        (res) => {
          this.resData = res.entity;
          this.transactionArray = this.resData.partTrans;
          this.transactionType = this.resData.transactionType;
          this.partTrans = this.resData.partTrans;
          this.btnColor = 'primary';
          this.submitData = 'POST';
          this.getTransactions();
          this.activateDelete = true;
          this.loading = true;
          this.disableForms();
          this.resData = res.entity;
          this.loading = false;
          this.createPopulatedFormData();
          this.formData.disable();
          this.disableAddButton = true;
          this.showTransactionForm = false;
          this.showTransactionList = true;
          this.showtransactionData = true;
          this.showRecieptButton = true;
          this.noneAddFunctions();
          this.validateData();
        },
        (err) => {
          this.loading = false;
          this.notificationAPI.alertWarning("Server Error: !!");
        }
      );
    }

    else if (this.function_type == 'CANCEL') {
      this.loading = true;
      this.btnColor = 'warn';
      this.btnText = 'CANCEL';
      this.disableForms();
      this.transactionAPI.getTransactionByCode(this.transactionCode).pipe(takeUntil(this.destroy$)).subscribe(
        (res) => {
          this.resData = res.entity;
          this.createPopulatedFormData();
          this.transactionArray = this.resData.partTrans;
          this.formData.disable();
          this.disableAddButton = true;
          this.getTransactions();
          this.loading = false;
          this.validateData();
        },
        (err) => {
          this.loading = false;
          this.notificationAPI.alertWarning("Server Error: !!");
        }
      );
    }
  }
  onSubmit() {
    this.loading = true;
    if (this.function_type == 'ENTER') {
      if (this.formData.valid) {
        this.addToArray();
        if (this.transactionArray.length > 1) {
          this.notificationAPI.alertWarning("ONE Transaction is Allowed Before Submitting!!");
        }
        else {
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
                      this.getPage();
                      this.createFormData();
                      this.formatForm();

                      this.params = new HttpParams()
                        .set('transactionCode', res.entity.transactionCode);
                      this.transactionAPI.getAcknowledgement(this.params).pipe(takeUntil(this.destroy$)).subscribe(
                        {
                          next: (
                            (res) => {
                              if (res.statusCode === 200) {
                                this.loading = false;
                                this.notificationAPI.alertSuccess(res.message);
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
                    } else {
                      this.loading = false;
                      this.resMessage = res.message;
                      this.resEntity = res.entity;
                      if (this.resEntity == null) {
                        this.notificationAPI.alertWarning(res.message);
                        this.isAlert = false;
                      } else {
                        this.isAlert = true;
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
                    this.transactionArray.length = 0;
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
                  if (this.resData.transactionType == 'CASH DEPOSIT') {
                    if (this.resData.postedFlag == 'Y') {
                      this.showRecieptButton = true;
                      this.showWithdrawalTranRecieptButton = false;
                    } else if (this.resData.postedFlag == 'N') {
                      this.showRecieptButton = false;
                      this.showWithdrawalTranRecieptButton = false;
                    }
                  }
                  if (this.resData.transactionType == 'CASH WITHDRAWAL') {
                    if (this.resData.postedFlag == 'Y') {
                      this.showRecieptButton = false;
                      this.showWithdrawalTranRecieptButton = true;
                    } else if (this.resData.postedFlag == 'N') {
                      this.showRecieptButton = false;
                      this.showWithdrawalTranRecieptButton = false;
                    }
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
  }
}
