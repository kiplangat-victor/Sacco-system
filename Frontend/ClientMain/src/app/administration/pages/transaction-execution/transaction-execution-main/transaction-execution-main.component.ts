import { HttpParams } from '@angular/common/http';
import { Component, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatCheckboxChange } from '@angular/material/checkbox';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import {
  MatSnackBarHorizontalPosition,
  MatSnackBarVerticalPosition
} from '@angular/material/snack-bar';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { Router } from '@angular/router';
import { DataTableDirective } from 'angular-datatables';
import { Observable, Subject, Subscription } from 'rxjs';
import { DataStoreService } from 'src/@core/helpers/data-store.service';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';
import { UniversalInquiryService } from 'src/app/administration/Service/UniversalInquiry/universal-inquiry.service';
import { WelfareService } from 'src/app/administration/Service/Welfare/welfare.service';
import { GeneralAccountDetailsComponent } from '../../Account-Component/general-account-details/general-account-details.component';
import { GeneralAccountsLookupComponent } from '../../Account-Component/general-accounts-lookup/general-accounts-lookup.component';
import { UniversalMembershipLookUpComponent } from '../../MembershipComponent/universal-membership-look-up/universal-membership-look-up.component';
import { CurrencyLookupComponent } from '../../SystemConfigurations/GlobalParams/currency-config/currency-lookup/currency-lookup.component';
import { AccountImagesLookupComponent } from '../account-images-lookup/account-images-lookup.component';
import { TransactionExecutionService } from '../transaction-execution.service';

@Component({
  selector: 'app-transaction-execution-main',
  templateUrl: './transaction-execution-main.component.html',
  styleUrls: ['./transaction-execution-main.component.scss'],
})
export class TransactionExecutionMainComponent implements OnInit {
  horizontalPosition: MatSnackBarHorizontalPosition = 'end';
  verticalPosition: MatSnackBarVerticalPosition = 'top';
  // Data Table
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
  onAccountDetailsLookup: boolean = false;
  account_acid_id: any;
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
  transactionDate = new Date();
  showAccountImagesLookup: boolean = false;
  constructor(
    private router: Router,
    private fb: FormBuilder,
    private dialog: MatDialog,
    private dataStoreApi: DataStoreService,
    private notificationAPI: NotificationService,
    private transactionAPI: TransactionExecutionService,
    private universalInquiryService: UniversalInquiryService,
    private welfareService: WelfareService,
  ) {
    this.functionArray = this.dataStoreApi.getActionsByPrivilege("TRANSACTION MAINTENANCE");
    this.functionArray = this.functionArray.filter(
      (arr: string) =>
        arr === 'ENTER' ||
        arr === 'INQUIRE' ||
        arr === 'MODIFY' ||
        arr === 'VERIFY' ||
        arr === 'DELETE');
    this.transactionTypeArray = this.functionArray.filter(
      (arr: string) =>
        arr === 'CASH DEPOSIT' ||
        arr === 'CASH WITHDRAWAL' ||
        arr === 'TRANSFER');
    this.fmData = this.router.getCurrentNavigation().extras.queryParams.formData;
    this.function_type = this.fmData.function_type;
    this.transactionCode = this.fmData.transactionCode;
    this.transactionType = this.fmData.transactionType;
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
  createFormData() {
    this.formData = this.fb.group({
      totalAmount: [''],
      transactionCode: [''],
      chequeNo: [''],
      currency: ['KES'],
      exchangeRate: [''],
      transactionType: [this.transactionType, [Validators.required]],
      partTrans: [[]],
      transactionDate: [this.transactionDate, Validators.required],
      tellerAccount: [''],
      chequeInstruments: [[]],
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
      enteredFlag: [this.resData.enteredFlag],
      enteredTime: [this.resData.enteredTime],
      entityId: [this.resData.entityId],
      chequeInstruments: [this.resData.chequeInstruments],
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
        welfareMemberCode: '',
      });
    }
  }

  getWelfare() {
    this.loading = true;
    this.welfareService.all().subscribe(
      (res) => {
        this.welfareTypeArray = res.entity;
        this.loading = false;
      }
    )
  }

  onWelfareTypeChange(): void {
    this.welfareTypeArray.forEach(element => {
      if (element.welfareCode == this.transactionForm.value.welfareCode) {
        this.welfareActionsArray = element.actions;
      }
    });
  }
  onWelfareActionChange(): void {
    this.welfareActionsArray.forEach(element => {
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

  customerLookup(): void {
    const dialogRef = this.dialog.open(UniversalMembershipLookUpComponent, {
      width: '60%',
      autoFocus: false,
      maxHeight: '90vh'
    });
    dialogRef.afterClosed().subscribe(result => {
      let customer_lookup = result.data;
      this.transactionForm.controls.welfareMemberCode.setValue(customer_lookup.customerCode);
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
    if (this.transactionType == 'CASH DEPOSIT') {
      this.transactionForm.patchValue({
        partTranType: 'Credit',
        transactionParticulars: 'CASH DEPOSIT',
      });
    } else if (this.transactionType == 'CASH WITHDRAWAL') {
      this.transactionForm.patchValue({
        partTranType: 'Debit',
        transactionParticulars: 'CASH WITHDRAWAL',
      });
    }
  }
  formatForm() {
    if (this.transactionType == 'CASH DEPOSIT') {
      this.setReadonlys();
      this.transactionForm.patchValue({
        partTranType: 'Debit',
        transactionParticulars: 'CASH DEPOSIT',
        acid: this.currentUser.tellerAc,
      });
      this.getAccountBalance(this.transactionForm.value.acid);
    } else if (this.transactionType == 'CASH WITHDRAWAL') {
      this.setReadonlys();
      this.transactionForm.patchValue({
        partTranType: 'Credit',
        transactionParticulars: 'CASH WITHDRAWAL',
        acid: this.currentUser.tellerAc,
      });
      this.getAccountBalance(this.transactionForm.value.acid);
    }
    if (this.transactionType == 'TRANSFER') {
    }
    if (this.transactionType == 'PROCESS CHEQUE') {
      this.createChequeForm();
      this.showChequeDetails = true;
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
    this.debit_value = 0.0;
    this.credit_value = 0.0;
    this.total_value = 0.0;
    this.transactionArray.forEach((element) => {
      if (element.partTranType == 'Debit') {
        this.debit_value = this.debit_value + element.transactionAmount;
      } else if (element.partTranType == 'Credit') {
        this.credit_value = this.credit_value + element.transactionAmount;
      }
    });

    this.total_value = this.debit_value - this.credit_value;

    if (this.total_value != 0) {
      this.Valid = false;
      this.notificationAPI.alertWarning("TRANSACTION IS NOT VALID! BALANCE DEBIT & CREDIT");
    } else {
      this.Valid = true;
      this.formData.patchValue({
        totalAmount: this.credit_value,
      });
    }
    this.getTransactions();
    this.resetReadonlys();
  }

  editItem(data) {
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

  currencyLookup(): void {
    const dialogRef = this.dialog.open(CurrencyLookupComponent, {});
    dialogRef.afterClosed().subscribe((result) => {
      this.dialogData = result.data;
      this.ccy_name = this.dialogData.ccy;

      this.formData.patchValue({
        currency: this.ccy_name,
      });
      this.selectedCurrency = this.dialogData.ccy;
    });
  }

  accountLookup(): void {
    if (!this.transactionForm.value.acid.trim()) {
      const dialogRef = this.dialog.open(
        GeneralAccountsLookupComponent, {
          width: '50%'
        });
      dialogRef.afterClosed().subscribe((result) => {
        this.accountlookupData = result.data;
        this.account_acid_id = this.accountlookupData.acid;
        this.transactionForm.patchValue({
          acid: this.accountlookupData.acid
        });
        if (this.accountlookupData.acid) {
          this.getAccountBalance(this.accountlookupData.acid);
        }
      });
      if (this.accountlookupData.acid === null || this.accountlookupData.acid === undefined || this.accountlookupData.acid === '-') {
        this.onAccountDetailsLookup = false;
        this.showAccountImagesLookup = false;
      } else {
        this.onAccountDetailsLookup = true;
        this.showAccountImagesLookup = true;
      }
    } else if (this.transactionForm.value.acid.trim()) {
      this.getAccountBalance(this.transactionForm.value.acid);
    }
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
      dialogRef.afterClosed().subscribe((result) => { });
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

  getAccountBalance(acid: any) {
    this.transactionAPI.getAccountDetails(acid).subscribe(
      (res) => {
        this.accountBalance = res.account_balance;
        this.accountCurrency = res.currency;
        this.accountName = res.account_name;
        this.accountStatus = res.account_status;
        this.transactionForm.patchValue({
          currency: res.currency,
          accountBalance: res.account_balance,
          accountType: res.account_type,
        });
      },
      (err) => {
        this.error = err;
        this.notificationAPI.alertWarning(err);
      }
    );
  }

  disableForms() {
    this.showTransactionsOnly = true;
    this.formData.disable();
    this.transactionForm.disable();
    this.chequeForm.disable();
    this.disableAddButton = true;
    this.disableActions = true;
  }
  generateTranReciept() {
    let transactionCode = this.resData.transactionCode;
    let params = new HttpParams()
      .set('transactionCode', transactionCode);
    this.subscription = this.transactionAPI
      .generateTranRecieptReport(params)
      .subscribe(
        (response) => {
          let url = window.URL.createObjectURL(response.data);
          // window.open(url);
          this.loading = false;
          this.notificationAPI.alertSuccess("Reciept generated successfully.");
        },
        (err) => {
          this.error = err;
          this.loading = false;
          this.notificationAPI.alertWarning("Error generating reciept !");
        }
      );
  }


  generateReciept() {
    let transactionCode = this.resData.transactionCode;
    const params = new HttpParams()
      .set('servedBy', this.currentUser.username)
      .set('transactionCode', transactionCode)
      .set('amount', this.resData.totalAmount)
      .set('receiptType', this.resData.transactionType);
    this.subscription = this.transactionAPI
      .generateRecieptReport(params)
      .subscribe(
        (response) => {
          let url = window.URL.createObjectURL(response.data);
          // if you want to open PDF in new tab
          // window.open(url);
          this.loading = false;
          this.notificationAPI.alertSuccess("Reciept generated successfully.");
        },
        (err) => {
          this.error = err;
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
  }

  getPage() {
    if (this.function_type == 'ENTER') {
      this.btnColor = 'primary';
      this.submitData = 'ENTER';
      this.disableAddButton = false;

      this.createFormData();
      this.formatForm();
      this.formData.patchValue({
        tellerAccount: this.currentUser.tellerAc,
      });
    } else if (this.function_type == 'INQUIRE') {
      this.loading = true;
      this.disableForms();
      this.hideSubmit = true;
      this.transactionAPI.getTransactionByCode(this.transactionCode).subscribe(
        (res) => {
          this.resData = res.entity;
          this.loading = false;
          this.createPopulatedFormData();
          this.transactionArray = this.resData.partTrans;
          this.getTransactions();
          this.noneAddFunctions();
          this.validateData();
        },
        (err) => {
          this.error = err;
          this.notificationAPI.alertWarning(err);
        }
      );
    } else if (this.function_type == 'MODIFY') {
      this.btnColor = 'primary';
      this.submitData = 'MODIFY';
      this.loading = true;
      this.transactionAPI.getTransactionByCode(this.transactionCode).subscribe(
        (res) => {
          this.resData = res.entity;
          this.loading = false;
          this.disableAddButton = false;
          this.createPopulatedFormData();
          this.transactionArray = this.resData.partTrans;
          this.getTransactions();
          this.noneAddFunctions();
          this.validateData();
        },
        (err) => {
          this.error = err;
          this.notificationAPI.alertWarning(err);
        }
      );
    } else if (this.function_type == 'VERIFY') {
      this.btnColor = 'primary';
      this.submitData = 'VERIFY';
      this.activateDelete = true;
      this.loading = true;
      this.disableForms();
      this.transactionAPI.getTransactionByCode(this.transactionCode).subscribe(
        (res) => {
          this.resData = res.entity;
          this.loading = false;
          this.createPopulatedFormData();
          this.transactionArray = this.resData.partTrans;
          this.getTransactions();
          this.formData.disable();
          this.disableAddButton = true;
          //this.showRecieptButton = true;
          this.noneAddFunctions();
          this.validateData();
        },
        (err) => {
          this.error = err;
          this.notificationAPI.alertWarning(err);
        }
      );
    } else if (this.function_type == 'POST') {
      this.btnColor = 'primary';
      this.submitData = 'POST';
      this.activateDelete = true;
      this.loading = true;
      this.disableForms();

      this.transactionAPI.getTransactionByCode(this.transactionCode).subscribe(
        (res) => {
          this.resData = res.entity;
          this.loading = false;
          this.createPopulatedFormData();
          this.transactionArray = this.resData.partTrans;
          this.getTransactions();
          this.formData.disable();
          this.disableAddButton = true;
          //this.showRecieptButton = true;
          this.noneAddFunctions();
          this.validateData();
        },
        (err) => {
          this.error = err;
          this.notificationAPI.alertWarning(err);
        }
      );
    } else if (this.function_type == 'CANCEL') {
      this.loading = true;
      this.btnColor = 'warn';
      this.btnText = 'CANCEL';
      this.disableForms();
      this.transactionAPI.getTransactionByCode(this.transactionCode).subscribe(
        (res) => {
          this.resData = res.entity;
          this.createPopulatedFormData();
          this.transactionArray = this.resData.partTrans;
          this.getTransactions();

          this.formData.disable();
          this.disableAddButton = true;

          this.loading = false;
          this.validateData();
        },
        (err) => {
          this.loading = false;
          this.error = err;
          this.notificationAPI.alertWarning(err);
        }
      );
    } else if (this.function_type == 'DELETE') {
      this.loading = true;
      this.deleteText = 'DELETE';
      this.hideSubmit = true;
      this.activateDelete = true;
      this.disableForms();

      this.transactionAPI.getTransactionByCode(this.transactionCode).subscribe(
        (res) => {
          this.resData = res.entity;
          this.createPopulatedFormData();
          this.transactionArray = this.resData.partTrans;
          this.getTransactions();

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
    if (!this.formData.invalid) {
      if (this.transactionArray.length < 2) {
        this.notificationAPI.alertWarning("Please Add atleast one Transaction before submitting!");
      } else {
        if (this.total_value != 0) {
          this.notificationAPI.alertWarning("TRANSACTION IS NOT VALID! BALANCE DEBIT & CREDIT");
        } else {
          // Call api to submit transactions
          if (this.function_type == 'ENTER') {
            this.transactionAPI
              .createTransaction(this.formData.value)
              .subscribe(
                (res) => {
                  if (res.statusCode == 201) {
                    this.notificationAPI.alertSuccess(res.message);
                    this.function_type = 'POST';
                    this.transactionCode = res.entity.transactionCode;
                    this.getPage();
                    // this.submitted = true;
                    // this.router.navigate(['system/transactions/maintenance']);
                  } else {
                    this.notificationAPI.alertWarning(res.message);
                  }
                },
                (err) => {
                  this.error = err;
                  this.notificationAPI.alertWarning(err);
                }
              );
          } else if (this.function_type == 'MODIFY') {
            this.transactionAPI
              .updateTransaction(this.formData.value)
              .subscribe(
                (res) => {
                  this.results = res;
                  this.notificationAPI.alertSuccess("SUCCESS");
                  this.loading = false;
                  this.submitted = true;
                  this.router.navigate(['system/transactions/maintenance'], { skipLocationChange: true });
                },
                (err) => {
                  this.loading = false;
                  this.notificationAPI.alertWarning(err);
                }
              );
          } else if (this.function_type == 'VERIFY') {
            this.transactionAPI
              .verifyTransaction(this.resData.transactionCode)
              .subscribe(
                (res) => {
                  this.results = res;
                  this.notificationAPI.alertSuccess("SUCCESS");
                  this.loading = false;
                  this.submitted = true;
                  this.router.navigate(['system/transactions/maintenance'], { skipLocationChange: true });
                },
                (err) => {
                  this.loading = false;
                  this.notificationAPI.alertWarning(err);
                }
              );
          } else if (this.function_type == 'POST') {
            this.transactionAPI
              .postTransaction(this.resData.transactionCode)
              .subscribe(
                (res) => {
                  this.results = res;
                  this.notificationAPI.alertSuccess(this.results.message);
                  this.loading = false;
                  this.submitted = true;
                  this.router.navigate(['system/transactions/maintenance'], { skipLocationChange: true });
                },
                (err) => {
                  this.loading = false;
                  this.notificationAPI.alertWarning(err);
                }
              );
          } else if (this.function_type == 'CANCEL') {
            let params = new HttpParams().set('id', this.resData.sn);
            this.transactionAPI.cancelTransactionById(params).subscribe(
              (res) => {
                this.results = res;
                this.notificationAPI.alertWarning("SUCESS");
              },
              (err) => {
                this.loading = false;
                this.notificationAPI.alertWarning(err);
              }
            );
          } else if (this.function_type == 'DELETE') {
            let params = new HttpParams().set('id', this.resData.sn);
            this.transactionAPI.deleteTransactionById(params).subscribe(
              (res) => {
                this.results = res;
                this.notificationAPI.alertWarning("SUCESS");
                this.submitted = true;
                this.router.navigate(['system/transactions/maintenance'], { skipLocationChange: true });
              },
              (err) => {
                this.loading = false;
                this.notificationAPI.alertWarning(err);
              }
            );
          }
        }
      }
    } else {
      this.notificationAPI.alertWarning("Invalid Form! Check your inputs");
    }
  }
}
