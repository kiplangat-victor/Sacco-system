import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';
import { SegmentsService } from '../segments.service';

@Component({
  selector: 'app-segment-lookup',
  templateUrl: './segment-lookup.component.html',
  styleUrls: ['./segment-lookup.component.scss']
})
export class SegmentLookupComponent implements OnInit , OnDestroy{
  displayedColumns: string[] = [
    'index',
    'segmentCode',
    'segmentName',
    'postedBy',
    'verifiedFlag'];
  dataSource!: MatTableDataSource<any>;
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;
  loading: boolean = false;
  error: any;
  segmentData: any;
  destroy$: Subject<boolean> = new Subject<boolean>();
  constructor(
    private segmentAPI: SegmentsService,
    private notificationAPI: NotificationService,
    public dialogRef: MatDialogRef<SegmentLookupComponent>
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
    this.segmentAPI.getAllSegments().pipe(takeUntil(this.destroy$)).subscribe(
      (res) => {
        if (res.statusCode === 302) {
          this.segmentData = res.entity;
          this.loading = false;
          this.dataSource = new MatTableDataSource(this.segmentData);
          this.dataSource.paginator = this.paginator;
          this.dataSource.sort = this.sort;
        } else {
          this.loading = false;
        }

      },
      (err) => {
        this.loading = false;
        this.notificationAPI.alertWarning("SERVER ERROR!!");
      }
    );
  }

  onSelect(data:any) {
    this.dialogRef.close({ event: 'close', data:data });
  }
  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();
    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }
}
