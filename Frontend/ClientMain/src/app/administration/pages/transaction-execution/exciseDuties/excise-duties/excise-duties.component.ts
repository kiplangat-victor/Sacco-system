import { Component, OnInit, OnDestroy, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { Subject, Subscription } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';
import { ExcisedutyService } from '../exciseduty.service';

@Component({
  selector: 'app-excise-duties',
  templateUrl: './excise-duties.component.html',
  styleUrls: ['./excise-duties.component.scss']
})
export class ExciseDutiesComponent implements OnInit, OnDestroy {
  displayedColumns: string[] = [
    'index',
    'pin',
    'accNo',
    'invId',
    'trnAmt',
    'exRt',
    'exDutyAmt',
    'postedTime',
    'status',
    'signatureGenerated'
  ];

  dataSource!: MatTableDataSource<any>;
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;
  destroy$: Subject<boolean> = new Subject<boolean>();
  exciseduties: any;
  loading: boolean = false;
  constructor(
    private exciseDutyAPI: ExcisedutyService,
    private notificationAPI: NotificationService
  ) { }
  ngOnDestroy(): void {
    this.destroy$.next(true);
    this.destroy$.complete();
  }
  ngOnInit(): void {
    this.getExciseDuties();
  }
  getExciseDuties() {
    this.loading = true;
    this.exciseDutyAPI.findExcDuties().pipe(takeUntil(this.destroy$)).subscribe(
      {
        next: (
          (res) => {
            if (res.statusCode == 302) {
              this.loading = false;
              this.exciseduties = res.entity;
              this.dataSource = new MatTableDataSource(this.exciseduties);
              this.dataSource.paginator = this.paginator;
              this.dataSource.sort = this.sort;
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
}
