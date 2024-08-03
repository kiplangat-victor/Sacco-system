import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';
import { SubSegmentService } from '../sub-segment.service';

@Component({
  selector: 'app-sub-segment-lookup',
  templateUrl: './sub-segment-lookup.component.html',
  styleUrls: ['./sub-segment-lookup.component.scss']
})
export class SubSegmentLookupComponent implements OnInit, OnDestroy {
  displayedColumns: string[] = ['index', 'subSegmentCode', 'subSegmentName', 'postedBy', 'verifiedFlag'];
  dataSource!: MatTableDataSource<any>;
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;
  loading: boolean = false;
  fromDialog: any;
  subSegmentData: any;
  destroy$: Subject<boolean> = new Subject<boolean>();
  constructor(
    private subSegmentAPI: SubSegmentService,
    private notificationAPI: NotificationService,
    public dialogRef: MatDialogRef<SubSegmentLookupComponent>
  ) { }
  ngOnDestroy(): void {
    this.destroy$.next(true);
    this.destroy$.complete();
  }
  ngOnInit(): void {
    this.getData();
  }
  getData() {
    this.loading = true;
    this.subSegmentAPI.getAllSubSegment().pipe(takeUntil(this.destroy$)).subscribe(
      res => {
        if (res.statusCode === 302) {
          this.subSegmentData = res.entity;
          this.loading = false;
          this.dataSource = new MatTableDataSource(this.subSegmentData);
          this.dataSource.paginator = this.paginator;
          this.dataSource.sort = this.sort;
        } else {
          this.loading = false;
        }
      },
      err => {
        this.loading = false;
        this.notificationAPI.alertWarning("SERVER ERROR");
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
