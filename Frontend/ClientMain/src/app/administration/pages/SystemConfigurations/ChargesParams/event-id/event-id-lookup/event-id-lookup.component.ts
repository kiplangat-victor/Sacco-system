import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { Subject, Subscription } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';
import { EventIdService } from '../event-id.service';

@Component({
  selector: 'app-event-id-lookup',
  templateUrl: './event-id-lookup.component.html',
  styleUrls: ['./event-id-lookup.component.scss']
})
export class EventIdLookupComponent implements OnInit, OnDestroy {
  displayedColumns: string[] = ['index', 'eventIdCode', 'chargeType', 'ac_placeholder', 'amt_derivation_type', 'event_type_desc', 'verifiedFlag'];
  dataSource!: MatTableDataSource<any>;
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;
  subscription!: Subscription;
  data: any;
  error: any;
  employeeEmail: any;
  employee_id: any;
  creatingAccount = false;
  formData: any;
  loading: boolean = false;
  destroy$: Subject<boolean> = new Subject<boolean>();
  constructor(
    private eventIdAPI: EventIdService,
    private notificationAPI: NotificationService,
    public dialogRef: MatDialogRef<EventIdLookupComponent>
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
    this.eventIdAPI.find().pipe(takeUntil(this.destroy$)).subscribe(res => {
      // console.log(res);
      if (res.statusCode === 302) {
        this.data = res;
        this.loading = false;
        this.dataSource = new MatTableDataSource(this.data.entity);
        this.dataSource.paginator = this.paginator;
        this.dataSource.sort = this.sort;
      } else {
        this.loading = false;
      }
    },
      err => {
        this.loading = false;
        this.notificationAPI.alertWarning("SERVER ERROR!!");
      })
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
