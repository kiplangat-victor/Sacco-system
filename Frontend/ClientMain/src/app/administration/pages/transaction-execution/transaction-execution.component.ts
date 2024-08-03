import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';
import { DataStoreService } from 'src/@core/helpers/data-store.service';
import { GeneralTransactionLookUpComponent } from './general-transaction-look-up/general-transaction-look-up.component';
import { TransactionExecutionService } from './transaction-execution.service';

@Component({
  selector: 'app-transaction-execution',
  templateUrl: './transaction-execution.component.html',
  styleUrls: ['./transaction-execution.component.scss'],
})
export class TransactionExecutionComponent implements OnInit, OnDestroy {
  transactionType: any;
  existingData: boolean;
  loading: boolean = false;
  submitted = false;
  lookupData: any;
  transactionCode: any;
  showTransactionTypeField = false;
  resData: any;
  currentUser: any;
  transactionTypeArray: any;
  functionArray: any;
  functionMainArray: any[];
  function_type: any;
  showWarning: boolean = true;
  proceeding: boolean = false;
  showSearch: boolean = true;
  destroy$: Subject<boolean> = new Subject<boolean>();
  constructor(
    private router: Router,
    private dialog: MatDialog,
    private fb: FormBuilder,
    private dataStoreApi: DataStoreService,
    private notificationAPI: NotificationService,
    private transactionAPI: TransactionExecutionService
  ) {
    this.currentUser = JSON.parse(localStorage.getItem('auth-user'));
    this.functionMainArray = this.dataStoreApi.getActionsByPrivilege("TRANSACTION MAINTENANCE");
    this.functionArray = this.functionMainArray.filter(
      arr =>
        arr === 'ENTER' ||
        arr === 'POST' ||
        arr === 'INQUIRE' ||
        arr === 'MODIFY' ||
        arr === 'VERIFY' ||
        arr === 'DELETE' ||
        arr === 'ACKNOWLEDGE' ||
        arr === 'SYSTEM REVERSAL' ||
        arr === 'REVERSE TRANSACTIONS');
    this.transactionTypeArray = this.functionMainArray.filter(
      arr =>
        arr === 'CASH DEPOSIT' ||
        arr === 'CASH WITHDRAWAL' ||
        arr === 'AGENCY NET DEPOSIT' ||
        arr === 'AGENCY NET WITHDRAWAL' ||
        arr === 'TRANSFER' ||
        arr === 'PETTY CASH' ||
        arr === 'POST EXPENSE' ||
        arr === 'POST OFFICE JOURNALS' ||
        arr === 'RECONCILE ACCOUNTS' ||
        arr === 'FUND TELLER' ||
        arr === 'COLLECT TELLER FUND' ||
        arr === 'PAYBILL WITHDRAWAL');
  }
  ngOnDestroy(): void {
    this.destroy$.next(true);
    this.destroy$.complete();
  }
  ngOnInit(): void {

  }
  formData = this.fb.group({
    function_type: ['', Validators.required],
    transactionCode: [''],
    transactionType: [''],
  });

  onSelectFunction(event: any) {
    if (event.target.value == 'ENTER') {
      this.existingData = false;
      this.showSearch = false;
      this.showTransactionTypeField = true;
      this.formData.controls['transactionType'].setValidators([
        Validators.required,
      ]);
      this.formData.controls.transactionCode.clearValidators();
      this.formData.controls.transactionCode.updateValueAndValidity();
    } else if (event.target.value !== 'ENTER') {
      this.existingData = true;
      this.showTransactionTypeField = false;
      this.formData.controls['transactionCode'].setValidators([
        Validators.required,
      ]);
      this.formData.controls['transactionType'].clearValidators();
      this.formData.controls['transactionType'].updateValueAndValidity();
    }
  }

  get f() {
    return this.formData.controls;
  }
  onSubmit() {
    this.submitted = true;
    this.proceeding = true;
    if (this.formData.valid) {
      this.showWarning = false;
      this.showSearch = false;
      this.function_type = this.formData.value.function_type;
      this.transactionType = this.f.transactionType.value;
      if (this.function_type == 'ENTER' && this.transactionType == 'CASH DEPOSIT' ||
        this.function_type == 'ENTER' && this.transactionType == 'CASH WITHDRAWAL' ||
        this.function_type == 'ENTER' && this.transactionType == 'PETTY CASH' ||
        this.function_type == 'ENTER' && this.transactionType == 'FUND TELLER' ||
        this.function_type == 'ENTER' && this.transactionType == 'COLLECT TELLER FUND' ||
        this.function_type == 'ENTER' && this.transactionType == 'AGENCY NET DEPOSIT' ||
        this.function_type == 'ENTER' && this.transactionType == 'AGENCY NET WITHDRAWAL' ||
        this.function_type == 'ENTER' && this.transactionType == 'PAYBILL WITHDRAWAL') {
        this.router.navigate([`/system/transactions/normal-cash/data/view`], { skipLocationChange: true, queryParams: { formData: this.formData.value } });
      }
      else if (this.function_type == 'ENTER' && this.transactionType == 'TRANSFER' ||
        this.function_type == 'ENTER' && this.transactionType == 'POST EXPENSE' ||
        this.function_type == 'ENTER' && this.transactionType == 'POST OFFICE JOURNALS' ||
        this.function_type == 'ENTER' && this.transactionType == 'RECONCILE ACCOUNTS') {
        this.router.navigate([`/system/transactions/normal/data/view`], { skipLocationChange: true, queryParams: { formData: this.formData.value } });
      }
      else if (this.function_type !== 'ENTER') {
        this.loading = true;
        this.proceeding = true;
        this.transactionAPI
          .getTransactionByCode(this.formData.value.transactionCode).pipe(takeUntil(this.destroy$))
          .subscribe(
            (res) => {
              this.resData = res;
              this.loading = false;
              this.proceeding = false;
              this.showSearch = false;
              if (this.resData) {
                this.loading = false;
                this.proceeding = false;
                this.showSearch = false;
                this.router.navigate([`/system/transactions/normal/data/view`], { skipLocationChange: true, queryParams: { formData: this.formData.value } });
              } else {
                this.loading = false;
                this.proceeding = false;
                this.showSearch = true;
              }
            },
            (err) => {
              this.loading = false;
              this.proceeding = false;
              this.showSearch = true;
               this.notificationAPI.alertWarning("Server Error: !!");
            }
          );
      }
    } else {
      this.loading = false;
      this.proceeding = false;
      this.notificationAPI.alertWarning("Transactions Form invalid: !!");
    }
  }

  transactionLookup(): void {
    const dialogRef = this.dialog.open(GeneralTransactionLookUpComponent, {
      width: '45%',
      autoFocus: false,
      maxHeight: '90vh'
    });
    dialogRef.afterClosed().subscribe(result => {
      this.lookupData = result.data;
      this.transactionCode = this.lookupData.transactionCode;
      this.formData.patchValue({
        transactionCode: this.transactionCode,
      });
    });
  }
}
