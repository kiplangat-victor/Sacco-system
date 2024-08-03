import { HttpParams } from '@angular/common/http';
import { Component, OnInit, ViewChild } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { Router } from '@angular/router';
import { Subject, Subscription } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';
import { TransactionExecutionService } from '../../transaction-execution/transaction-execution.service';

@Component({
  selector: 'app-sbcnotifications',
  templateUrl: './sbcnotifications.component.html',
  styleUrls: ['./sbcnotifications.component.scss']
})
export class SBCNotificationsComponent implements OnInit {
  salariesColumns: string[] = [
    'index',
    'type',
    'amount',
    'account',
    'uniqueCode',
    'verified_flag',
    'verified_flag_2',
    'entryTime',
    'verify'
  ];

  salariesDataSource: MatTableDataSource<any>;
  @ViewChild("salariesPaginator") salariesPaginator!: MatPaginator;
  @ViewChild(MatSort) salariesSort!: MatSort;

  loading: boolean = false;
  params: HttpParams;
  results: any;
  destroy$: Subject<boolean> = new Subject<boolean>();
  fromDate: any = new Date(Date.now() - 360 * (3600 * 1000 * 24));
  toDate: any = new Date(Date.now() + (3600 * 1000 * 24));
  transData: any;
  constructor(public fb: FormBuilder,
    private router: Router,
    private notificationAPI: NotificationService,
    private transactionAPI: TransactionExecutionService
  ) { }

  ngOnInit(): void {
    this.getTransactionApprovals();
  }

  getTransactionApprovals() {
    this.loading = true;
    this.transactionAPI.getRoughApprovalList().pipe(takeUntil(this.destroy$)).subscribe(
      {
        next: (
          (res) => {
            console.log(res);
            if (res.statusCode === 200) {
              this.transData = res.entity;
              this.loading = false;
              this.salariesDataSource = new MatTableDataSource(this.transData);
              this.salariesDataSource.paginator = this.salariesPaginator;
              this.salariesDataSource.sort = this.salariesSort;
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


  verifyTransaction(tranCode: any, type: any) {
    if (type == 'salary') {
      this.router.navigate([`/system/salary-transaction/data/view`], {
        skipLocationChange: true,
        queryParams: {
          formData: {
            function_type: 'VERIFY',
            salaryUploadCode: tranCode,
            backBtn: ["APPROVAL"]
          }
        }
      });
    } else if (type == 'cheque') {
      this.router.navigate([`/system/transactions/cheques-data-view`], {
        skipLocationChange: true, queryParams: {
          formData: {
            function_type: 'VERIFY',
            chequeRandCode: tranCode,
            backBtn: ["APPROVAL"]
          }
        }
      });
    } else if (type == 'batch') {
      this.router.navigate([`/system/batch-salaries-transaction/data/view`], {
        skipLocationChange: true,
        queryParams: {
          formData: {
            function_type: 'VERIFY',
            batchUploadCode: tranCode,
            backBtn: ["APPROVAL"]
          }
        },
      });
    }
  }
  salariesFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.salariesDataSource.filter = filterValue.trim().toLowerCase();
    if (this.salariesDataSource.paginator) {
      this.salariesDataSource.paginator.firstPage();
    }
  }
}
