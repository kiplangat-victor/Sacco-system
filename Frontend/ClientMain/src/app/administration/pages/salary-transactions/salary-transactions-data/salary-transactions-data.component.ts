import { HttpParams } from '@angular/common/http';
import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { Router } from '@angular/router';
import { BehaviorSubject, Observable, Subject, Subscription } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';
import * as XLSX from 'xlsx';
import { GeneralAccountsLookupComponent } from '../../Account-Component/general-accounts-lookup/general-accounts-lookup.component';
import { OfficeAccountsLookUpsComponent } from '../../Account-Component/office-account/office-accounts-look-ups/office-accounts-look-ups.component';
import { EmployeeConfigLookupComponent } from '../../SystemConfigurations/GlobalParams/employee-config/employee-config-lookup/employee-config-lookup.component';
import { SalaryChargesService } from '../../SystemConfigurations/GlobalParams/salary-charges/salary-charges.service';
import { TransactionExecutionService } from '../../transaction-execution/transaction-execution.service';
import { SalaryTransactionsService } from '../salary-transactions.service';
import { FilesService, SelectedFiles } from './fileconversion/files.service';

@Component({
  selector: 'app-salary-transactions-data',
  templateUrl: './salary-transactions-data.component.html',
  styleUrls: ['./salary-transactions-data.component.scss'],
})
export class SalaryTransactionsDataComponent implements OnInit, OnDestroy {
  displayedColumns: string[] = [
    'index',
    'account',
    'accountName',
    'idNumber',
    'amount',
    'memberNumber',
  ];
  dataSource!: MatTableDataSource<any>;
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;
  function_type: any;
  exceldata: [][] | undefined;
  subscription!: Subscription;
  keys: string[];
  otherRows: any[];
  dataSheet = new Subject();
  headerRows: any;
  fileAcess: any;
  firstElement: any;
  excelSelected: any;
  fileInfos?: Observable<any>;
  values: any;
  finalValues: any;
  valuesArray: any;
  submitted = false;
  formData: FormGroup;
  transactionTypeArray = [
    {
      name: 'CASH',
    },
    {
      name: 'TRANSFER',
    },
    {
      name: 'CHEQUE',
    },
  ];
  activateDelete = false;
  fmData: any;
  showCode = false;
  salaryUploadCode: any;
  submitData: any;
  loading = false;
  loadingRejections: boolean = false;
  resData: any;
  disableAddButton = false;
  transactionArray: any[] = [];
  error: any;
  dialogData: any;
  accountlookupData: any;
  accountBalance: any;
  accountCurrency: any;
  accountName: any;
  chequeTypeArray = [
    {
      name: 'Bankers Cheque',
    },
    {
      name: 'Personal Cheque',
    },
  ];
  public selectedFiles: SelectedFiles[] = [];
  public isFileLoading = new BehaviorSubject(false);
  activateChequeDetails: boolean = false;
  results: any;
  excelHeaderRef: any[] = [
    'account',
    'idNumber',
    'amount',
    'memberNumber',
  ];
  
  recipientTypeArray: string[] = ["Employees", "Suppliers"]
  excelFileAccepted = false;
  chargesAccountlookupData: any;
  chargesAccountBalance: any;
  chargesAccountCurrency: any;
  chargesAccountName: any;
  excelDataPresent = false;
  disableActions = false;
  sumofCredits = 0;
  deleteText = '';
  transactionType = '';
  disableUploadFile = false;
  hideSubmit = false;
  currentUser: any;
  destroy$: Subject<boolean> = new Subject<boolean>();
  showchargesAccountData: boolean = false;
  showAccountData: boolean = false;
  chargesaccountStatus: any;
  chargesaccountName: any;
  chargesaccountBalance: any;
  chargesaccountCurrency: any;
  accountStatus: any;
  salaryCharges: any;
  dialogConfig: any;
  hideRejectBtn: boolean = false;
  submitting: boolean = false;
  btnRejectColor: any;
  rejectBtnText: any;
  rejectionData: any;
  hideBtn = false;
  btnColor: any;
  btnText: any;
  displayBackButton: boolean = true;
  displayApprovalBtn: boolean = false;
  onShowResults: boolean = false;
  params: HttpParams;
  constructor(
    private router: Router,
    private fb: FormBuilder,
    private dialog: MatDialog,
    private filesService: FilesService,
    private notificationAPI: NotificationService,
    private salariesAPI: SalaryTransactionsService,
    private transactionAPI: TransactionExecutionService,
    private salaryChargesPI: SalaryChargesService
  ) {
    this.fmData = this.router.getCurrentNavigation().extras.queryParams.formData;
    this.function_type = this.fmData.function_type;
    this.salaryUploadCode = this.fmData.salaryUploadCode;
    this.currentUser = JSON.parse(localStorage.getItem('auth-user'));
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
    this.getSalaryCharges();
    this.createFormData();
    this.getPage();
  }
  createFormData() {
    this.formData = this.fb.group({
      amount: ['', [Validators.required]],
      debitAccount: ['', [Validators.required]],
      tranParticulars: [''],
      employerCode: ['', [Validators.required]],
      salaryUploadCode: [''],
      collectCharges: ['Y'],
      eventIdCode: [''],
      chargeFrom: ['Employer', [Validators.required]],
      recipientType: [''],
      salaryChargeCode: ['', [Validators.required]],
      transactionDate: [new Date(), Validators.required],
      employeeDetails: [[]],
      chequeNo: [''],
      drTransactionCode: [''],
      chequeType: [''],
      chequeDate: [''],
      chequeStatus: [''],
      tellerAccount: ['']
    });
  }
  chargeTypeChange(event: any) {
    this.salaryCharges.forEach((element) => {
      if (element.salaryChargeCode == this.formData.controls.salaryChargeCode.value) {
        this.formData.patchValue({
          chargeFrom: element.chargeFrom,
          collectCharges: element.collectCharges,
          eventIdCode: element.eventId
        });
      }
    });
  }
  getSalaryCharges() {
    this.loading = true;
    this.salaryChargesPI.findAll().subscribe (
      (res) => {
        this.salaryCharges = res.entity;
        this.loading = false;
      }
    )
  }
  createPopulatedFormData() {
    this.transactionType = this.resData.transactionType;
    this.formData = this.fb.group({
      id: [this.resData.id, [Validators.required]],
      amount: [this.resData.amount, [Validators.required]],
      debitAccount: [this.resData.debitAccount, [Validators.required]],
      tranParticulars: [this.resData.tranParticulars],
      employerCode: [this.resData.employerCode, [Validators.required]],
      recipientType: [this.resData.recipientType, [Validators.required]],
      salaryUploadCode: [this.resData.salaryUploadCode],
      collectCharges: [this.resData.collectCharges, [Validators.required]],
      eventIdCode: [this.resData.eventIdCode],
      chargeFrom: [this.resData.chargeFrom],
      salaryChargeCode: [this.resData.salaryChargeCode, [Validators.required]],
      transactionDate: [this.resData.enteredTime, Validators.required],
      employeeDetails: [this.resData.employeeDetails],
      
      chequeNo: [this.resData.chequeNo],
      chequeType: [this.resData.chequeType],
      chequeDate: [this.resData.chequeDate],
      chequeStatus: [this.resData.chequeStatus],
      tellerAccount: [this.resData.tellerAccount]
    });
  }
  get f() {
    return this.formData.controls;
  }
  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();
    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }
  getTransactions() {
    this.dataSource = new MatTableDataSource(this.transactionArray);
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
  }
  employerCodeLookup(): void {
    const dialogRef = this.dialog.open(EmployeeConfigLookupComponent, {
      width: '45%',
      autoFocus: false,
      maxHeight: '90vh'
    });
    dialogRef.afterClosed().subscribe(result => {
      this.dialogData = result.data;
      this.formData.controls.employerCode.setValue(this.dialogData.employerCode);
    });
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
            this.formData.patchValue({
              debitAccount: this.accountlookupData.acid,
            });
            this.loading = true;
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
                    } else {
                      this.loading = false;
                      this.notificationAPI.alertWarning("Account Records Not Found!!");
                      this.showchargesAccountData = false;
                    }
                  }
                ),
                error: (
                  (err) => {
                    this.loading = false;
                    this.notificationAPI.alertWarning("Server Error: !!");
                    this.showchargesAccountData = false;
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
  accountForChargesLookup(): void {
    const dialogRef = this.dialog.open(
      OfficeAccountsLookUpsComponent, {
      width: '50%'
    });
    dialogRef.afterClosed().pipe(takeUntil(this.destroy$)).subscribe(
      {
        next: (
          (res) => {
            this.accountlookupData = res.data;
            this.formData.patchValue({
              eventIdCode: this.accountlookupData.acid,
            });
            this.loading = true;
            this.transactionAPI.getAccountDetails(this.accountlookupData.acid).pipe(takeUntil(this.destroy$)).subscribe(
              {
                next: (
                  (res) => {
                    if (res.statusCode === 200) {
                      this.results = res.entity;
                      this.loading = false;
                      this.showchargesAccountData = true;
                      this.chargesaccountBalance = this.results.account_balance;
                      this.chargesaccountCurrency = this.results.currency;
                      this.chargesaccountName = this.results.account_name;
                      this.chargesaccountStatus = this.results.account_status;

                    } else {
                      this.loading = false;
                      this.notificationAPI.alertWarning("Account Records Not Found!!");
                      this.showchargesAccountData = false;
                    }
                  }
                ),
                error: (
                  (err) => {
                    this.loading = false;
                    this.notificationAPI.alertWarning("Server Error: !!");
                    this.showchargesAccountData = false;
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
  getChargesAccountBalance(acid) {
    this.transactionAPI.getAccountDetails(acid).pipe(takeUntil(this.destroy$)).subscribe((res) => {
      this.chargesAccountBalance = res.account_balance;
      this.chargesAccountCurrency = res.currency;
      this.chargesAccountName = res.account_name;
    });
  }
  getAccountBalance(acid: any) {
    this.transactionAPI.getAccountDetails(acid).pipe(takeUntil(this.destroy$)).subscribe((res) => {
      this.accountBalance = res.account_balance;
      this.accountCurrency = res.currency;
      this.accountName = res.account_name;
    });
  }
  onIncurCharge(event: any) { }

  transactionTypeChange(event: any) {
    if (event.target.value == 'CASH') {
      this.activateChequeDetails = false;
      this.formData.controls.chequeNo.clearValidators();
      this.formData.controls.chequeType.clearValidators();
      this.formData.controls.chequeDate.clearValidators();
      this.formData.controls.chequeStatus.clearValidators();
      this.formData.controls.chequeNo.updateValueAndValidity();
      this.formData.controls.chequeType.updateValueAndValidity();
      this.formData.controls.chequeDate.updateValueAndValidity();
      this.formData.controls.chequeStatus.updateValueAndValidity();
    } else if (event.target.value == 'TRANSFER') {
      this.activateChequeDetails = false;
      this.formData.controls.chequeNo.clearValidators();
      this.formData.controls.chequeType.clearValidators();
      this.formData.controls.chequeDate.clearValidators();
      this.formData.controls.chequeStatus.clearValidators();
      this.formData.controls.chequeNo.updateValueAndValidity();
      this.formData.controls.chequeType.updateValueAndValidity();
      this.formData.controls.chequeDate.updateValueAndValidity();
      this.formData.controls.chequeStatus.updateValueAndValidity();
    } else if (event.target.value == 'CHEQUE') {
      this.activateChequeDetails = true;
      this.formData.controls.chequeNo.setValidators([Validators.required]);
      this.formData.controls.chequeType.setValidators([Validators.required]);
      this.formData.controls.chequeDate.setValidators([Validators.required]);
      this.formData.controls.chequeStatus.setValidators([Validators.required]);
    }
  }

  onSelectFile(files: any) {
    this.isFileLoading.next(true);
    this.selectedFiles = [];
    this.filesService.toBase64(files, this.selectedFiles).pipe(takeUntil(this.destroy$)).subscribe((res) => {
      if (res) {
        this.isFileLoading.next(false);
        this.selectedFiles = res;
      }
    });
  }

  onFileChange(evt: any) {
    const file: File = evt.target.files[0];
    this.fileAcess = file;
    const target: DataTransfer = <DataTransfer>evt.target;
    if (target.files.length !== 1)
      throw new Error('Multiple Files Not Supported!');
    const reader: FileReader = new FileReader();
    reader.onload = (e: any) => {
      const bstr: string = e.target.result;
      const wb: XLSX.WorkBook = XLSX.read(bstr, { type: 'binary' });
      const wsname: string = wb.SheetNames[0];
      const ws: XLSX.WorkSheet = wb.Sheets[wsname];
      this.exceldata = XLSX.utils.sheet_to_json(ws, { header: 1 });
      if (this.exceldata) {
        this.excelSelected = true;
        this.excelDataPresent = false;
      }
      this.headerRows = this.exceldata[0];
      this.otherRows = this.exceldata.slice(1);
      this.firstElement = this.headerRows[0];
      this.headerRows = this.headerRows.filter((e: any) => e.length);

      this.otherRows = this.otherRows.filter((e) => e.length);

      this.values = this.otherRows.map((e) =>
        this.headerRows.reduce((o, f, j) => Object.assign(o, { [f]: e[j] }), {})
      );

      this.finalValues = this.values.filter(
        (value: {}) => Object.keys(value).length !== 0
      );
      this.headerRows = this.headerRows.map(function (el: any) {
        return el.trim();
      });
      this.sumofCredits = 0;
      this.finalValues.forEach((element: any) => {
        Object.keys(element).forEach((key) => {
          // var replacedKey = key.trim().replace(/\s\s+/g, '');
          var replacedKey = key.trim().replace(/\s\s+/g, '');
          if (key !== replacedKey) {
            element[replacedKey] = element[key];
            delete element[key];
          }
        });
        this.sumofCredits += parseInt(element.amount);
      });
      this.formData.patchValue({
        amount: this.sumofCredits,
      });

      this.compareExcelHeaders();
    };
    reader.readAsBinaryString(target.files[0]);
  }
  compareExcelHeaders() {
    if (this.headerRows.length != this.excelHeaderRef.length) {
      this.notificationAPI.alertWarning("PLEASE CONFIRM ALL FIELDS ARE PRESENT INCHECK THE UPLOADED EXCEL FILE!")
    } else if (this.headerRows.length == this.excelHeaderRef.length) {
      if (
        JSON.stringify(this.headerRows) === JSON.stringify(this.excelHeaderRef)
      ) {
        this.excelFileAccepted = true;
        this.formData.patchValue({
          employeeDetails: this.finalValues,
        });
        this.notificationAPI.alertSuccess("THE EXCEL FILE IS VALID!");
      } else if (
        JSON.stringify(this.headerRows) !== JSON.stringify(this.excelHeaderRef)
      ) {
        this.excelFileAccepted = false;
        this.notificationAPI.alertWarning("PLEASE CHECK THE UPLOADED EXCEL FILE, AND TRY AGAIN!");
      }
    }
  }
  disableForms() { }
  changeFunctionToDelete() {
    this.function_type = 'DELETE';
  }
  nonAddFunctionalities() {
    this.showCode = true;
    this.createPopulatedFormData();
    this.transactionArray = this.resData.employeeDetails;
    this.getTransactions();
    this.excelDataPresent = true;
    this.excelSelected = false;
  }
  getPage() {
    if (this.function_type == 'ADD') {
      this.createFormData();
      this.formData.patchValue({
        tellerAccount: this.currentUser.tellerAc
      });
      this.btnColor = 'primary';
      this.btnText = 'ENTER';
    } else if (this.function_type == 'INQUIRE') {
      this.loading = true;
      this.hideSubmit = true;
      this.onShowResults = true;
      this.disableForms();
      this.salariesAPI.getSalaryTransactionByCode(this.salaryUploadCode).pipe(takeUntil(this.destroy$)).subscribe(
        (res) => {
          this.disableUploadFile = true;
          this.resData = res.entity;
          this.loading = false;
          this.nonAddFunctionalities();
          this.formData.disable();
          this.disableActions = true;
          this.submitting = false;
        },
        (err) => {
          this.error = err;
          this.loading = false;
          this.notificationAPI.alertWarning(this.error);
        }
      );
    } else if (this.function_type == 'MODIFY') {
      this.loading = true;
      this.btnColor = 'primary';
      this.btnText = 'MODIFY';
      this.onShowResults = true;
      this.salariesAPI.getSalaryTransactionByCode(this.salaryUploadCode).pipe(takeUntil(this.destroy$)).subscribe(
        (res) => {
          this.resData = res.entity;
          this.loading = false;
          this.nonAddFunctionalities();
        },
        (err) => {
          this.loading = false;
          this.notificationAPI.alertWarning("Server Error: !!");
        }
      );
    } else if (this.function_type == 'VERIFY') {
      this.loading = true;
      this.onShowResults = true;
      this.btnColor = 'primary';
      this.btnText = 'VERIFY';
      this.disableForms();
      this.salariesAPI.getSalaryTransactionByCode(this.salaryUploadCode).pipe(takeUntil(this.destroy$)).subscribe(
        (res) => {
          this.disableUploadFile = true;
          this.resData = res.entity;
          if (this.resData.verifiedFlag_2 == 'Y') {
            this.hideRejectBtn = false;
          }
          if (this.resData.verifiedFlag_2 == 'N') {
            this.btnRejectColor = 'accent';
            this.rejectBtnText = 'REJECT';
            this.hideRejectBtn = true;
          }

          this.loading = false;
          this.nonAddFunctionalities();
          this.formData.disable();
          this.disableAddButton = true;
          this.activateDelete = true;
        },
        (err) => {
          this.loading = false;
          this.notificationAPI.alertWarning("Server Error: !!");
        }
      );
    } else if (this.function_type == 'POST') {
      this.loading = true;
      this.btnColor = 'primary';
      this.btnText = 'POST';
      this.onShowResults = true;
      this.disableForms();
      this.salariesAPI.getSalaryTransactionByCode(this.salaryUploadCode).pipe(takeUntil(this.destroy$)).subscribe(
        (res) => {
          this.disableUploadFile = true;
          this.resData = res.entity;
          this.loading = false;
          this.nonAddFunctionalities();
          this.formData.disable();
          this.disableAddButton = true;
          this.activateDelete = true;
        },
        (err) => {
          this.loading = false;
          this.notificationAPI.alertWarning("Server Error: !!");
        }
      );
    } else if (this.function_type == 'CANCEL') {
      this.loading = true;
      this.activateDelete = true;
      this.onShowResults = true;
      this.btnColor = 'primary';
      this.btnText = 'CANCEL';
      this.disableForms();
      this.salariesAPI.getSalaryTransactionByCode(this.salaryUploadCode).pipe(takeUntil(this.destroy$)).subscribe(
        (res) => {
          this.resData = res.entity;
          this.loading = false;
          this.nonAddFunctionalities();
          this.formData.disable();
          this.disableAddButton = true;
          this.activateDelete = true;
          this.disableUploadFile = true;
        },
        (err) => {
          this.loading = false;
          this.notificationAPI.alertWarning("Server Error: !!");
        }
      );
    } else if (this.function_type == 'DELETE') {
      this.loading = true;
      this.activateDelete = true;
      this.hideSubmit = true;
      this.onShowResults = true;
      this.btnColor = 'primary';
      this.btnText = 'DELETE';
      this.disableForms();
      this.salariesAPI.getSalaryTransactionByCode(this.salaryUploadCode).pipe(takeUntil(this.destroy$)).subscribe(
        (res) => {
          this.disableUploadFile = true;
          this.resData = res.entity;
          this.loading = false;
          this.nonAddFunctionalities();
          this.formData.disable();
          this.disableAddButton = true;
          this.activateDelete = true;
          this.btnText = 'DELETE';
        },
        (err) => {
          this.loading = false;
          this.notificationAPI.alertWarning("Server Error: !!");
        }
      );
    }
  }

  onSubmit() {
    if (this.loading) {
      return;
    }
    if (!this.formData.invalid) {
      this.loading = true;
      if (this.function_type == 'ADD') {
        if (this.excelFileAccepted) {
          this.salariesAPI.createSalaryTransaction(this.formData.value).pipe(takeUntil(this.destroy$))
            .subscribe(
              (res) => {
                this.submitting = false;
                if (res.statusCode == 201) {
                  this.loading = false;
                  this.notificationAPI.alertSuccess(res.message);
                  this.salaryUploadCode = res.entity.salaryUploadCode;
                  this.function_type = 'INQUIRE';
                  this.getPage();
                } else {
                  this.loading = false;
                  this.notificationAPI.alertWarning(res.message);
                }
              },
              (err) => {
                this.loading = false;
                this.submitting = false;
                this.notificationAPI.alertWarning("Server Error: !!");
              }
            );
        } else {
          this.loading = false;
          this.notificationAPI.alertWarning("PLEASE ATTACH AN EXCEL DOCUMENT BEFORE SUBMITTING");
        }
      } else if (this.function_type == 'MODIFY') {
        this.salariesAPI.updateSalaryTransaction(this.formData.value).pipe(takeUntil(this.destroy$)).subscribe(
          (res) => {
            this.loading = false;
            this.results = res;
            this.notificationAPI.alertSuccess(res.message);
            this.submitted = true;
            this.router.navigate(['/system/salary-transaction/maintenance'], { skipLocationChange: true });
          },
          (err) => {
            this.loading = false;
            this.notificationAPI.alertWarning("Server Error: !!");
          }
        );
      } else if (this.function_type == 'VERIFY') {
        this.salariesAPI.verifySalaryTransaction(this.salaryUploadCode).pipe(takeUntil(this.destroy$))
          .subscribe(
            (res) => {
              if (res.statusCode == 200) {
                this.results = res;
                this.notificationAPI.alertSuccess(res.message);
                this.loading = false;
                this.submitted = true;
                this.function_type = 'INQUIRE';
                this.getPage();
                // this.router.navigate(['/system/salary-transaction/maintenance']);
              } else {
                this.loading = false;
                let message = res.message;
                if(res.entity != null && Array.isArray(res.entity)) {
                  res.entity.forEach(s => {
                    message = message +" <br> "+s.message;
                  });
                }
                this.notificationAPI.alertWarning(message);
              }
            },
            (err) => {
              this.loading = false;
              this.error = err;
              this.notificationAPI.alertWarning(this.error);
            }
          );
      } else if (this.function_type == 'POST') {
        this.salariesAPI.postSalaryTransaction(this.salaryUploadCode).pipe(takeUntil(this.destroy$))
          .subscribe(
            (res) => {
              if (res.statusCode == 200) {
                this.results = res;
                this.notificationAPI.alertSuccess(res.message);
                this.loading = false;
                this.submitted = true;
                this.router.navigate(['/system/salary-transaction/maintenance']);
              } else {
                this.loading = false;
                this.notificationAPI.alertWarning(res.message);
              }
            },
            (err) => {
              this.loading = false;
              this.error = err;
              this.notificationAPI.alertWarning(this.error);
            }
          );
      } else if (this.function_type == 'CANCEL') {
        let params = new HttpParams().set('id', this.resData.id);
        this.salariesAPI.cancelSalaryTransactionById(params).pipe(takeUntil(this.destroy$)).subscribe(
          (res) => {
            this.results = res;
            this.loading = false;
            this.notificationAPI.alertSuccess("SUCCESS");
            this.router.navigate(['/system/salary-transaction/maintenance'], { skipLocationChange: true });
          },
          (err) => {
            this.loading = false;
            this.notificationAPI.alertWarning("Server Error: !!");
            this.submitted = true;
          }
        );
      } else if (this.function_type == 'DELETE') {
        let params = new HttpParams().set('id', this.resData.id);
        this.salariesAPI.deleteSalaryTransactionById(params).pipe(takeUntil(this.destroy$)).subscribe(
          (res) => {
            this.results = res;
            this.loading = false;
            this.notificationAPI.alertSuccess("SUCCESS");
            this.submitted = true;
            this.router.navigate(['/system/salary-transaction/maintenance'], { skipLocationChange: true });
          },
          (err) => {
            this.loading = false;
            this.notificationAPI.alertWarning("Server Error: !!");
          }
        );
      }
    } else {
      this.loading = false;
      this.notificationAPI.alertWarning("INVALID FORM! CHECK YOUR INPUTS");
    }
  }
  onRejectTransaction() {
    if (window.confirm("ARE YOU SURE YOU WANT TO REJECT SALARY UPLOAD  WITH CODE NO: " + this.salaryUploadCode + " ?")) {
      this.loadingRejections = true;
      this.salariesAPI.rejectSalaryTransaction(this.salaryUploadCode).pipe(takeUntil(this.destroy$)).subscribe(
        {
          next: (
            (res) => {
              if (res.statusCode === 200) {
                this.loadingRejections = false;
                this.notificationAPI.alertSuccess(res.message);
                if (this.fmData.backBtn == 'APPROVAL') {
                  this.router.navigate([`/system/new/notification/allerts`], { skipLocationChange: true });
                } else {
                  this.router.navigate([`/system/salary-transaction/maintenance`], { skipLocationChange: true });
                }
              } else {
                this.loadingRejections = false;
                this.notificationAPI.alertWarning(res.message);
              }
            }
          ),
          error: (
            (err) => {
              this.loadingRejections = false;
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
