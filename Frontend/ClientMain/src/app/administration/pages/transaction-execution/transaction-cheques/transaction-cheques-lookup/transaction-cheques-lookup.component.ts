import { DatePipe } from '@angular/common';
import { HttpParams } from '@angular/common/http';
import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { Subject, Subscription } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';
import { ChequeTransactionsService } from '../cheque-transactions.service';

@Component({
  selector: 'app-transaction-cheques-lookup',
  templateUrl: './transaction-cheques-lookup.component.html',
  styleUrls: ['./transaction-cheques-lookup.component.scss']
})
export class TransactionChequesLookupComponent implements OnInit, OnDestroy {
  displayedColumns: string[] = ['index', 'chequeRandCode', 'bankName', 'bankBranch', 'debitAccount', 'creditCustOperativeAccount'];
  dataSource!: MatTableDataSource<any>;
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;
  transactionStatusArray: any[] = ['Entered','Cleared', 'Posted', 'Modified', 'Verified', 'Deleted', 'Bounced'];

  data: any;
  respData: any;
  loading: boolean = false;
  destroy$: Subject<boolean> = new Subject<boolean>();
  submitted: boolean = false;
  status: any;
  params: any;
  chequeRandCode: any;
  fromDate: any = new Date(Date.now() - (3600 * 1000 * 24));
  toDate: any = new Date(Date.now() + (3600 * 1000 * 24));
  results: any;
  constructor(
    public fb: FormBuilder,
    private datepipe: DatePipe,
    private chequeAPI: ChequeTransactionsService,
    private notificationAPI: NotificationService,
    public dialogRef: MatDialogRef<TransactionChequesLookupComponent>
  ) { }
  ngOnInit() {
  }
  ngOnDestroy() {
    this.destroy$.next(true);
    this.destroy$.complete();
  }
  formData = this.fb.group({
    chequeRandCode: [""],
    fromDate: [this.fromDate],
    status: ["Entered"],
    toDate: [this.toDate],
  });

  onSubmit() {
    this.loading = true;
    this.submitted = true;
    this.chequeRandCode = this.formData.controls.chequeRandCode.value;
    this.fromDate = this.datepipe.transform(this.formData.controls.fromDate.value, 'yyyy-MM-dd');
    this.status = this.formData.controls.status.value;
    this.toDate = this.datepipe.transform(this.formData.controls.toDate.value, "yyyy-MM-dd");
    this.params = new HttpParams()
      .set('chequeRandCode', this.chequeRandCode)
      .set('fromDate', this.fromDate)
      .set('status', this.status)
      .set('toDate', this.toDate);
    this.chequeAPI.findAll(this.params).subscribe(res => {
      {
        this.results = res.entity;
        console.log("RES", this.results);

        this.dataSource = new MatTableDataSource(this.results);
        this.dataSource.paginator = this.paginator;
        this.dataSource.sort = this.sort;
        this.loading = false;
      }

    }, err => {

    })
  }

  onSubmsit() {
    this.loading = true;
    this.submitted = true;
    this.chequeRandCode = this.formData.controls.chequeRandCode.value;
    this.fromDate = this.datepipe.transform(this.formData.controls.fromDate.value, 'yyyy-MM-dd');
    this.status = this.formData.controls.status.value;
    this.toDate = this.datepipe.transform(this.formData.controls.toDate.value, "yyyy-MM-dd");
    this.params = new HttpParams()
      .set('chequeRandCode', this.chequeRandCode)
      .set('fromDate', this.fromDate)
      .set('status', this.status)
      .set('toDate', this.toDate);
    this.chequeAPI.findAll(this.params).pipe(takeUntil(this.destroy$)).subscribe(
      {
        next: (
          (res) => {
            if (res.statusCode == 302 || res.statusCode == 200) {
              this.results = res.entity;
              this.dataSource = new MatTableDataSource(this.results);
              this.dataSource.paginator = this.paginator;
              this.dataSource.sort = this.sort;
              this.loading = false;
            } else {
              this.loading = false;
              this.notificationAPI.alertWarning(res.message);
            }
          }
        ),
        error: (
          (err) => {
            this.loading = false;
            this.notificationAPI.alertWarning(err);
            // this.notificationAPI.alertWarning("Server Error: !!");
          }
        ),
        complete: (
          () => {

          }
        )
      }
    ), Subscription;
  }

  onSelect(data: any) {
    this.dialogRef.close({ event: 'close', data: data });
  }
  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();
    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }

}
