import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';
import { StandingOrdersService } from '../standing-orders.service';

@Component({
  selector: 'app-standing-orders-lookup',
  templateUrl: './standing-orders-lookup.component.html',
  styleUrls: ['./standing-orders-lookup.component.scss']
})
export class StandingOrdersLookupComponent implements OnInit, OnDestroy {
  displayedColumns: string[] = ['index', 'standingOrderCode', 'customerCode', 'sourceAccountNo','amount', 'verifiedFlag'];
  dataSource!: MatTableDataSource<any>;
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;
  data: any;
  respData: any;
  loading: boolean = false;
  destroy$: Subject<boolean> = new Subject<boolean>();
  constructor(
    private notificationAPI: NotificationService,
    private standingOrdersAPI: StandingOrdersService,
    public dialogRef: MatDialogRef<StandingOrdersLookupComponent>
  ) { }
  ngOnInit() {
    this.getData();
  }
  ngOnDestroy() {
    this.destroy$.next(true);
    this.destroy$.complete();
  }
  getData() {
    this.loading = true;
    this.standingOrdersAPI.read().pipe(takeUntil(this.destroy$)).subscribe(
      (data) => {
        this.respData = data.entity;
        this.loading = false;
        this.dataSource = new MatTableDataSource(this.respData);
        this.dataSource.paginator = this.paginator;

        // if (data.statusCode === 302) {
        //   this.respData = data.entity;
        //   this.loading = false;
        //   this.dataSource = new MatTableDataSource(this.respData);
        //   this.dataSource.paginator = this.paginator;
        // } else {
        //   this.loading = false;
        // }
      }, (err) => {
        this.loading = false;
        this.notificationAPI.alertWarning("SERVER ERROR: TRY AGAIN LATER");
      }
    );
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
