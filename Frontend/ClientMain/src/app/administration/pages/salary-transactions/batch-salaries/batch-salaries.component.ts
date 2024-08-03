import { HttpParams } from '@angular/common/http';
import { Subscription } from 'rxjs';
import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { Router } from '@angular/router';
import { BehaviorSubject, Observable, Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';
import * as XLSX from 'xlsx';
import { GeneralAccountsLookupComponent } from '../../Account-Component/general-accounts-lookup/general-accounts-lookup.component';
import { TransactionExecutionService } from '../../transaction-execution/transaction-execution.service';
import { FilesService, SelectedFiles } from '../salary-transactions-data/fileconversion/files.service';
import { BatchSalariesService } from './batch-salaries.service';
import { MatRadioChange } from '@angular/material/radio';
import { EventIdLookupComponent } from '../../SystemConfigurations/ChargesParams/event-id/event-id-lookup/event-id-lookup.component';

@Component({
  selector: 'app-batch-salaries',
  templateUrl: './batch-salaries.component.html',
  styleUrls: ['./batch-salaries.component.scss']
})
export class BatchSalariesComponent implements OnInit, OnDestroy {
  displayedColumns: string[] = [
    'index',
    'memberNumber',
    'idNumber',
    'account',
    'accountName',
    'amount',
    'externalTranCode',
    'particulars'
  ];
  dataSource!: MatTableDataSource<any>;
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;
  exceldata: [][] | undefined;
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
  fmData: any;
  showCode = false;
  btnColor: any;
  submitData: any;
  loading = false;
  resData: any;
  transactionArray: any[] = [];
  accountlookupData: any;
  accountBalance: any;
  accountCurrency: any;
  accountName: any;
  public selectedFiles: SelectedFiles[] = [];
  public isFileLoading = new BehaviorSubject(false);
  activateChequeDetails: boolean = false;
  results: any;
  excelHeaderRef: any[] = [
    'memberNumber', 'externalTranCode', 'account', 'amount', 'particulars'
  ];
  excelFileAccepted = false;
  excelDataPresent = false;
  sumofCredits = 0;
  transactionType = '';
  disableUploadFile = false;
  hideSubmit = false;
  currentUser: any;
  destroy$: Subject<boolean> = new Subject<boolean>();
  showAccountData: boolean = false;
  accountStatus: any;
  dialogConfig: any;
  batchUploadCode: any;
  function_type: any;
  showchargesAccountData: boolean = false;
  params: HttpParams;
  onShowWarning: boolean = true;
  showSearch: boolean = true;
  showEventIdCode: boolean = false;
  lookupdata: any;
  hideRejectBtn: boolean = false;
  btnRejectColor: any;
  rejectBtnText: any;
  rejectionData: any;
  displayBackButton: boolean = true;
  displayApprovalBtn: boolean = false;
  onShowResults: boolean = false;
  constructor(
    private router: Router,
    private fb: FormBuilder,
    private dialog: MatDialog,
    private filesService: FilesService,
    private notificationAPI: NotificationService,
    private batchSalariesAPI: BatchSalariesService,
    private transactionAPI: TransactionExecutionService
  ) {
    this.currentUser = JSON.parse(localStorage.getItem('auth-user'));
    this.fmData = this.router.getCurrentNavigation().extras.queryParams.formData;
    this.function_type = this.fmData.function_type;
    this.batchUploadCode = this.fmData.batchUploadCode;
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
    this.createFormData();
    this.getPage();
  }
  createFormData() {
    this.formData = this.fb.group({
      amount: ['', [Validators.required]],
      batchUploadCode: [""],
      collectCharges: ["", Validators.required],
      creditaccounts: [[]],
      debitAccount: ['', [Validators.required]],
      drTransactionCode: [""],
      drTransactionDate: [new Date()],
      entityId: [""],
      eventIdCode: [""],
      id: [""],
      status: [""],
      tellerAccount: [''],
      tranParticulars: ['']
    });
  }
  get f() {
    return this.formData.controls;
  }
  handleChange(event: MatRadioChange) {
    if (event.value == 'Y') {
      this.showEventIdCode = true;
    }
    else if (event.value == 'N') {
      this.showEventIdCode = false;
    }
  }
  eventIdCodeLookup(): void {
    const dialogRef = this.dialog.open(EventIdLookupComponent, {
      width: '40%'
    });
    dialogRef.afterClosed().subscribe(result => {
      this.lookupdata = result.data;
      this.formData.controls.eventIdCode.setValue(this.lookupdata.eventIdCode);
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

  getAccountBalance(acid: any) {
    this.transactionAPI.getAccountDetails(acid).pipe(takeUntil(this.destroy$)).subscribe((res) => {
      this.accountBalance = res.account_balance;
      this.accountCurrency = res.currency;
      this.accountName = res.account_name;
    });
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
      this.headerRows = this.headerRows.filter((e) => e.length);

      this.otherRows = this.otherRows.filter((e) => e.length);

      this.values = this.otherRows.map((e) =>
        this.headerRows.reduce((o: any, f: any, j: string | number) => Object.assign(o, { [f]: e[j] }), {})
      );

      this.finalValues = this.values.filter(
        (value: {}) => Object.keys(value).length !== 0
      );
      this.headerRows = this.headerRows.map(function (el: any) {
        return el.trim();
      });
      this.sumofCredits = 0;
      this.finalValues.forEach((element) => {
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
      this.notificationAPI.alertWarning("PLEASE CONFIRM ALL FIELDS ARE PRESENT INCHECK THE UPLOADED EXCEL FILE!");
      this.excelFileAccepted = true;
      this.formData.patchValue({
        creditaccounts: this.finalValues,
      });
    } else if (this.headerRows.length == this.excelHeaderRef.length) {
      if (
        JSON.stringify(this.headerRows) === JSON.stringify(this.excelHeaderRef)
      ) {
        this.excelFileAccepted = true;
        this.formData.patchValue({
          creditaccounts: this.finalValues,
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

  nonAddFunctionalities() {
    this.showCode = true;
    this.excelDataPresent = true;
    this.excelSelected = false;
  }
  getBatchData() {
    this.loading = true;
    this.batchSalariesAPI.findByCode(this.fmData.batchUploadCode).pipe(takeUntil(this.destroy$)).subscribe(
      {
        next: (
          (res) => {
            if (res.statusCode === 200) {
              this.loading = false;
              this.resData = res.entity;
              if (this.resData.collectCharges == 'Y') {
                this.showEventIdCode = true;
              }
              if (this.resData.collectCharges == 'N') {
                this.showEventIdCode = false;
              }
              if (this.resData.verifiedFlag_2 == 'Y' ) {
                this.hideRejectBtn = false;
              } else if (this.resData.verifiedFlag_2 == 'N' ) {
                this.btnRejectColor = 'accent';
                this.rejectBtnText = 'REJECT';
                this.hideRejectBtn = true;
              }
              this.formData = this.fb.group({
                amount: [this.resData.amount],
                batchUploadCode: [this.resData.batchUploadCode],
                collectCharges: [this.resData.collectCharges],
                creditaccounts: [[]],
                debitAccount: [this.resData.debitAccount],
                drTransactionCode: [this.resData.drTransactionCode],
                drTransactionDate: [this.resData.drTransactionDate],
                entityId: [this.resData.entityId],
                eventIdCode: [this.resData.eventIdCode],
                id: [this.resData.id],
                status: [this.resData.status],
                tellerAccount: [this.resData.tellerAccount],
                tranParticulars: [this.resData.tranParticulars]
              });
              this.transactionArray = this.resData.creditaccounts;
              this.dataSource = new MatTableDataSource(this.transactionArray);
              this.dataSource.paginator = this.paginator;
              this.dataSource.sort = this.sort;
            } else {
              this.loading = false;
              this.notificationAPI.alertWarning(res.message);
            }
          }
        ), error: (
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

  getPage() {
    if (this.function_type == 'ADD') {
      this.createFormData();
      this.btnColor = 'primary';
      this.submitData = 'SUBMIT';
      this.formData.patchValue({
        tellerAccount: this.currentUser.tellerAc
      });
    }
    else if (this.function_type == 'INQUIRE') {
      this.getBatchData();
      this.loading = true;
      this.onShowResults = true;
      this.disableUploadFile = true;
      this.disableForms();
      this.formData.disable();
      this.nonAddFunctionalities();
    }
    if (this.function_type == 'MODIFY') {
      this.getBatchData();
      this.formData.disable();
      this.onShowResults = true;
      this.btnColor = 'primary';
      this.submitData = 'MODIFY';
    }
    if (this.function_type == 'POST') {
      this.getBatchData();
      this.onShowResults = true;
      this.loading = true;
      this.disableUploadFile = true;
      this.disableForms();
      this.formData.disable();
      this.nonAddFunctionalities();
      this.btnColor = 'primary';
      this.submitData = 'POST';
    }
    if (this.function_type == 'VERIFY') {
      this.getBatchData();
      this.loading = true;
      this.onShowResults = true;
      this.disableUploadFile = true;
      this.disableForms();
      this.formData.disable();
      this.nonAddFunctionalities();
      this.btnColor = 'primary';
      this.submitData = 'VERIFY';
    }
    if (this.function_type == 'DELETE') {
      this.getBatchData();
      this.loading = true;
      this.onShowResults = true;
      this.disableUploadFile = true;
      this.disableForms();
      this.formData.disable();
      this.nonAddFunctionalities();
      this.btnColor = 'accent';
      this.submitData = 'DELETE';
    }
  }

  onSubmit() {
    this.loading = true;
    if (this.function_type == 'ADD') {
      if (!this.formData.invalid) {
        this.loading = true;
        if (this.excelFileAccepted) {
          this.batchSalariesAPI.create(this.formData.value).pipe(takeUntil(this.destroy$))
            .subscribe(
              (res) => {
                if (res.statusCode == 201) {
                  this.loading = false;
                  this.notificationAPI.alertSuccess(res.message);
                  this.router.navigate([`/system/batch-salaries-transaction/maintenance`], {
                    skipLocationChange: true
                  });
                } else {
                  this.loading = false;
                  this.notificationAPI.alertWarning(res.message);
                }
              },
              (err) => {
                this.loading = false;
                this.notificationAPI.alertWarning("Server Error: !!");
              }
            );
        } else {
          this.loading = false;
          this.notificationAPI.alertWarning("PLEASE ATTACH AN EXCEL DOCUMENT BEFORE SUBMITTING");
        }
      }
      else {
        this.loading = false;
        this.notificationAPI.alertWarning("INVALID FORM! CHECK YOUR INPUTS");
      }
    }
    if (this.function_type == 'MODIFY') {
      if (!this.formData.invalid) {
        this.loading = true;
        if (this.excelFileAccepted) {
          this.batchSalariesAPI.modify(this.formData.value).pipe(takeUntil(this.destroy$))
            .subscribe(
              (res) => {
                if (res.statusCode == 200) {
                  this.loading = false;
                  this.notificationAPI.alertSuccess(res.message);
                  this.router.navigate([`/system/batch-salaries-transaction/maintenance`], {
                    skipLocationChange: true
                  });
                } else {
                  this.loading = false;
                  this.notificationAPI.alertWarning(res.message);
                }
              },
              (err) => {
                this.loading = false;
                this.notificationAPI.alertWarning("Server Error: !!");
              }
            );
        } else {
          this.loading = false;
          this.notificationAPI.alertWarning("PLEASE ATTACH AN EXCEL DOCUMENT BEFORE SUBMITTING");
        }
      }
      else {
        this.loading = false;
        this.notificationAPI.alertWarning("INVALID FORM! CHECK YOUR INPUTS");
      }
    }
    if (this.function_type == 'POST') {
      this.loading = true;
      this.batchSalariesAPI.post(this.fmData.batchUploadCode).pipe(takeUntil(this.destroy$)).subscribe(
        {
          next: (
            (res) => {
              if (res.statusCode == 200) {
                this.loading = false;
                this.notificationAPI.alertSuccess(res.message);
                this.router.navigate([`/system/batch-salaries-transaction/maintenance`], {
                  skipLocationChange: true});
              } else {
                this.loading = false;
                this.getBatchData();
                this.notificationAPI.alertWarning(res.message);
              }
            }
          ), error: (
            (err) => {
              this.loading = false;
              this.getBatchData();
              this.notificationAPI.alertWarning("Server Error: !!");
            }
          ), complete: (
            () => {

            }
          )
        }
      ), Subscription;
    }
    if (this.function_type == 'VERIFY') {
      this.loading = true;
      this.batchSalariesAPI.verify(this.fmData.batchUploadCode).pipe(takeUntil(this.destroy$)).subscribe(
        {
          next: (
            (res) => {
              if (res.statusCode == 200) {
                this.loading = false;
                this.notificationAPI.alertSuccess(res.message);
                this.router.navigate([`/system/batch-salaries-transaction/maintenance`], {
                  skipLocationChange: true
                });
              } else {
                this.loading = false;
                this.getBatchData();
                this.notificationAPI.alertWarning(res.message);
              }
            }
          ), error: (
            (err) => {
              this.loading = false;
              this.getBatchData();
              this.notificationAPI.alertWarning("Server Error: !!");
            }
          ), complete: (
            () => {

            }
          )
        }
      ), Subscription;
    }
    if (this.function_type == 'DELETE') {
      this.loading = true;
      this.params = new HttpParams()
        .set('id', this.resData.id)
      this.batchSalariesAPI.delete(this.params).pipe(takeUntil(this.destroy$)).subscribe(
        {
          next: (
            (res) => {
              if (res.statusCode == 200) {
                this.loading = false;
                this.notificationAPI.alertSuccess(res.message);
                this.router.navigate([`/system/batch-salaries-transaction/maintenance`], {
                  skipLocationChange: true
                });
              } else {
                this.loading = false;
                this.getBatchData();
                this.notificationAPI.alertWarning(res.message);
              }
            }
          ), error: (
            (err) => {
              this.loading = false;
              this.getBatchData();
              this.notificationAPI.alertWarning("Server Error: !!");
            }
          ), complete: (
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
    if (window.confirm("ARE YOU SURE YOU WANT TO REJECT BATCH TRANSACTION FOR " + this.resData.tranParticulars + " WITH TRANSACION CODE: " + this.resData.batchUploadCode + " ?")) {
      this.batchSalariesAPI.reject(this.resData.batchUploadCode).pipe(takeUntil(this.destroy$)).subscribe(
        {
          next: (
            (res) => {
              if (res.statusCode === 200) {
                this.notificationAPI.alertSuccess(res.message);
                if (this.fmData.backBtn == 'APPROVAL') {
                  this.router.navigate([`/system/new/notification/allerts`], { skipLocationChange: true });
                } else {
                  this.router.navigate([`/system/salary-transaction/maintenance`], { skipLocationChange: true });
                }
              } else {
                this.notificationAPI.alertWarning(res.message);
              }
            }
          ),
          error: (
            (err) => {
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
