import { DatePipe } from '@angular/common';
import { HttpParams } from '@angular/common/http';
import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { Subject, Subscription } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';
import { TransactionExecutionService } from '../transaction-execution.service';

@Component({
  selector: 'app-general-transaction-look-up',
  templateUrl: './general-transaction-look-up.component.html',
  styleUrls: ['./general-transaction-look-up.component.scss']
})
export class GeneralTransactionLookUpComponent implements OnInit, OnDestroy {
  displayedColumns: string[] = [
    'index',
    'transactionCode',
    'transactionType',
    'totalAmount',
    'status',
    'enteredBy',
    'transactionDate'
  ];
  dataSource!: MatTableDataSource<any>;
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;
  loading = false;
  today = new Date();
  params: HttpParams;
  results: any;
  toDate: any = new Date();
  fromDate: any = new Date();
  submitted: boolean = false;

  transactionTypesArray: any[] = ['Transfer', 'Cash Deposit', 'Cash Withdrawal', 'Fund Teller', 'Collect Teller Fund',
    'Collect Teller Fund', 'Post Expense', 'Post Office Journals', 'Reconcile Accounts', 'Reverse Transactions',
    'Petty Cash', 'Cheque Clearence', 'Cheque Bounce'];
  transactionStatusArray: any[] = ['Entered', 'Posted', 'Modified', 'Reversed', 'Verified', 'Deleted'];
  destroy$: Subject<boolean> = new Subject<boolean>();
  transactionCode: string;
  transactionType: string;
  status: string;
  resData: any;
  constructor(
    public fb: FormBuilder,
    private datepipe: DatePipe,
    private notificationAPI: NotificationService,
    private transactionAPI: TransactionExecutionService,
    public dialogRef: MatDialogRef<GeneralTransactionLookUpComponent>
  ) {
  }
  ngOnDestroy(): void {
    this.destroy$.next(true);
    this.destroy$.complete();
  }
  ngOnInit() {
  }
  formData = this.fb.group({
    fromDate: [new Date()],
    status: [""],
    toDate: [ new Date((new Date()).getTime() + (1000 * 60 * 60 * 24))],
    transactionCode: [""],
    transactionType: ["", Validators.required]
  });

  get f() {
    return this.formData.controls;
  }
  onSubmit() {
    this.loading = true;
    this.submitted = true;
    if (this.formData.valid) {
      this.fromDate = this.datepipe.transform(this.formData.controls.fromDate.value, 'yyyy-MM-dd');
      this.status = this.formData.controls.status.value;
      this.toDate = this.datepipe.transform(this.formData.controls.toDate.value, 'yyyy-MM-dd');
      this.transactionCode = this.formData.controls.transactionCode.value;
      this.transactionType = this.formData.controls.transactionType.value;
      this.params = new HttpParams()
        .set('fromDate', this.fromDate)
        .set('status', this.status)
        .set('toDate', this.toDate)
        .set('transactionCode', this.transactionCode)
        .set('transactionType', this.transactionType);
      this.transactionAPI.getFilteredTransactions(this.params).pipe(takeUntil(this.destroy$)).subscribe(
        {
          next: (
            (res) => {
              if (res.statusCode === 200 || res.statusCode === 302) {
                this.results = res.entity;
                this.resData = this.results;
                this.loading = false;
                this.dataSource = new MatTableDataSource(this.resData);
                this.dataSource.paginator = this.paginator;
                this.dataSource.sort = this.sort;

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
    } else {
      this.loading = false;
      this.notificationAPI.alertWarning("Transaction form filter invalid: !!");
    }

  }
  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();
    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }
  onSelect(data: any) {
    this.dialogRef.close({ event: 'close', data: data });
  }
}
