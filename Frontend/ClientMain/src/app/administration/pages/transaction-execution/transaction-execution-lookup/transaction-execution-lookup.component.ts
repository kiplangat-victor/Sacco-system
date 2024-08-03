import { DatePipe } from '@angular/common';
import { HttpParams } from '@angular/common/http';
import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { TransactionExecutionService } from '../transaction-execution.service';

@Component({
  selector: 'app-transaction-execution-lookup',
  templateUrl: './transaction-execution-lookup.component.html',
  styleUrls: ['./transaction-execution-lookup.component.scss'],
})
export class TransactionExecutionLookupComponent implements OnInit, OnDestroy {
  displayedColumns: string[] = [
    'index',
    'transactionCode',
    'transactionType',
    'enteredTime',
    'enteredBy',
    'verifiedFlag',
    'verifiedBy',
    'postedTime',
    'deletedBy',
    'deletedFlag',
  ];
  dataSource!: MatTableDataSource<any>;
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;
  respData: any;
  paramsData!: FormGroup;
  loading = false;
  accountTypeArray: any = ['Office Account', 'Customer Account'];
  actions: any[] = ['Entered', 'Posted', 'Modified', 'Verified', 'Deleted']
  today = new Date();
  tommorrowDate = new Date(new Date().setDate(this.today.getDate() + 1));
  priorDate = new Date(new Date().setDate(this.today.getDate() - 1));
  destroy$: Subject<boolean> = new Subject<boolean>();
  constructor(
    private fb: FormBuilder,
    private datepipe: DatePipe,
    private transactionAPI: TransactionExecutionService,
    private dialogRef: MatDialogRef<TransactionExecutionLookupComponent>,
  ) { }
  ngOnDestroy(): void {
    this.destroy$.next(true);
    this.destroy$.complete();
  }
  ngOnInit() {
    this.createParamsForm();
    this.getData();
  }
  createParamsForm() {
    this.paramsData = this.fb.group({
      action: ['Entered', [Validators.required]],
      toDate: [this.tommorrowDate, [Validators.required]],
      fromDate: [this.priorDate, [Validators.required]],
    });
  }
  getData() {
    this.loading = true;
    let params = new HttpParams()
      .set('action', this.paramsData.controls.action.value)
      .set(
        'fromDate',
        this.datepipe.transform(
          this.paramsData.controls.fromDate.value,
          'yyyy-MM-ddTHH:mm:ss'
        )
      )
      .set(
        'toDate',
        this.datepipe.transform(
          this.paramsData.controls.toDate.value,
          'yyyy-MM-ddTHH:mm:ss'
        )
      );

    this.transactionAPI
      .getTransactions(params).pipe(takeUntil(this.destroy$))
      .subscribe((res) => {
        this.respData = res;
        this.loading = false;
        this.dataSource = new MatTableDataSource(this.respData.entity);
        this.dataSource.paginator = this.paginator;
        this.dataSource.sort = this.sort;
      });
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
