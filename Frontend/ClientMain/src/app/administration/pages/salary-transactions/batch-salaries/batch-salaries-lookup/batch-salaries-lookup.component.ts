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
import { MembershipService } from 'src/app/administration/Service/Membership/membership.service';
import { MembershipLookUpComponent } from '../../../MembershipComponent/Membership/membership-look-up/membership-look-up.component';
import { TransactionExecutionService } from '../../../transaction-execution/transaction-execution.service';
import { BatchSalariesService } from '../batch-salaries.service';

@Component({
  selector: 'app-batch-salaries-lookup',
  templateUrl: './batch-salaries-lookup.component.html',
  styleUrls: ['./batch-salaries-lookup.component.scss']
})
export class BatchSalariesLookupComponent implements OnInit, OnDestroy {
  displayedColumns: string[] = ['index', 'batchUploadCode', 'amount', 'debitAccount', 'tranParticulars', 'status', 'verifiedFlag', 'enteredTime'];
  dataSource!: MatTableDataSource<any>;
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;
  actionsArray: any[] = [ 'Entered','Posted', 'Modified', 'Verified', 'Deleted'];
  loading = false;
  params: HttpParams;
  results: any;
  action: any;
  toDate: any = new Date();
  fromDate: any = new Date();
  destroy$: Subject<boolean> = new Subject<boolean>();
  constructor(
    public fb: FormBuilder,
    private datepipe: DatePipe,
    private notificationAPI: NotificationService,
    private batchSalariesAPI: BatchSalariesService,
    private transactionAPI: TransactionExecutionService,
    public dialogRef: MatDialogRef<BatchSalariesLookupComponent>
  ) { }
  formData = this.fb.group({
    action: [''],
    toDate: [new Date()],
    fromDate: [new Date()]
  });

  ngOnInit() {
  }
  ngOnDestroy(): void {
    this.destroy$.next(true);
    this.destroy$.complete();
  }

  onSubmit() {
    this.loading = true;
    this.action = this.formData.controls.action.value;
    this.fromDate = this.datepipe.transform(this.formData.controls.fromDate.value, 'yyyy-MM-dd');
    this.toDate = this.datepipe.transform(this.formData.controls.toDate.value, "yyyy-MM-dd");

    this.params = new HttpParams()
      .set('action', this.action)
      .set('fromDate', this.fromDate)
      .set('toDate', this.toDate)
    this.batchSalariesAPI.getBatchesTransactions(this.params).pipe(takeUntil(this.destroy$)).subscribe(
      {
        next: (
          (res) => {
            if (res.statusCode === 200) {
              this.results = res.entity;
              this.dataSource = new MatTableDataSource(this.results);
              this.dataSource.paginator = this.paginator;
              this.dataSource.sort = this.sort;
              this.loading = false;
            } else {
              this.loading = false;
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
