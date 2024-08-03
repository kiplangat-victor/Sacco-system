import { DatePipe } from '@angular/common';
import { HttpParams } from '@angular/common/http';
import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { Subject, Subscription } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { SalaryTransactionsComponent } from '../salary-transactions.component';
import { SalaryTransactionsService } from '../salary-transactions.service';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';

@Component({
  selector: 'app-salary-transactions-lookup',
  templateUrl: './salary-transactions-lookup.component.html',
  styleUrls: ['./salary-transactions-lookup.component.scss'],
})
export class SalaryTransactionsLookupComponent implements OnInit, OnDestroy {
  displayedColumns: string[] = [
    'index',
    'salaryUploadCode',
    'tranParticulars',
    'debitAccount',
    'amount',
    'enteredFlag',
    'enteredBy',
    'enteredTime',
    'verifiedFlag'
  ];
  dataSource!: MatTableDataSource<any>;
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;
  destroy$: Subject<boolean> = new Subject<boolean>();
  data: any;
  respData: any;
  paramsData!: FormGroup;
  today = new Date();
  tommorrowDate = new Date(new Date().setDate(this.today.getDate() + 1));
  priorDate = new Date(new Date().setDate(this.today.getDate()));
  loading = false;
  submitted: boolean = false;
  actions: any[] = ['Entered', 'Modified', 'Verified', 'Deleted', 'Posted'];
  constructor(
    public fb: FormBuilder,
    private datepipe: DatePipe,
    private notificationAPI: NotificationService,
    private salaryTransactionAPI: SalaryTransactionsService,
    public dialogRef: MatDialogRef<SalaryTransactionsLookupComponent>
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
      action: ["Entered", [Validators.required]],
      toDate: [this.tommorrowDate, [Validators.required]],
      fromDate: [this.priorDate, [Validators.required]],
    });
  }
  get f() {
    return this.paramsData.controls;
  }
  getData() {
    this.loading = true;
    this.submitted = true;
    if (this.paramsData.valid) {
      let params = new HttpParams()
        .set('action', this.paramsData.controls.action.value)
        .set('fromDate', this.datepipe.transform(this.paramsData.controls.fromDate.value, 'yyyy-MM-dd'))
        .set('toDate', this.datepipe.transform(this.paramsData.controls.toDate.value, 'yyyy-MM-dd'));
      this.salaryTransactionAPI.getSalaryTransactions(params).pipe(takeUntil(this.destroy$)).subscribe(
        {
          next: (
            (res) => {
              if (res.statusCode === 200) {
                this.loading = false;
                this.respData = res.entity;
                this.dataSource = new MatTableDataSource(this.respData);
                this.dataSource.paginator = this.paginator;
                this.dataSource.sort = this.sort;
              } else {
                this.loading = false;
                this.notificationAPI.alertWarning("No Salary Transactions for " + this.paramsData.controls.action.value + " For Period " + this.datepipe.transform(this.paramsData.controls.fromDate.value, 'yyyy-MM-dd') + " - " + this.datepipe.transform(this.paramsData.controls.toDate.value, 'yyyy-MM-dd'));
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
    } else if (this.paramsData.invalid) {
      this.loading = false;
      this.notificationAPI.alertWarning("No Params filter Selected: !!");
    }
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
