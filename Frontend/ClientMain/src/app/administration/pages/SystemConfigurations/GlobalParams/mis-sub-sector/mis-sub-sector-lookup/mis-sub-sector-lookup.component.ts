import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';
import { MisSubSectorService } from '../mis-sub-sector.service';

@Component({
  selector: 'app-mis-sub-sector-lookup',
  templateUrl: './mis-sub-sector-lookup.component.html',
  styleUrls: ['./mis-sub-sector-lookup.component.scss']
})
export class MisSubSectorLookupComponent implements OnInit, OnDestroy {
  displayedColumns: string[] = [
    'index',
    'misSubcode',
    'misSubSector',
    'postedBy',
    'verifiedFlag'];
  dataSource!: MatTableDataSource<any>;
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;
  loading: boolean = false;
  fromDialog: any;
  subSectorData: any;
  destroy$: Subject<boolean> = new Subject<boolean>();
  constructor(
    private subSectorAPI: MisSubSectorService,
    private notificationAPI: NotificationService,
    public dialogRef: MatDialogRef<MisSubSectorLookupComponent>
  ) { }
  ngOnDestroy(): void {
    this.destroy$.next(true);
    this.destroy$.complete();
  }
  ngOnInit(): void {
    this.getData();
  }
  getData() {
    this.loading = false;
    this.subSectorAPI.find().pipe(takeUntil(this.destroy$)).subscribe(
      res => {
        if (res.statusCode === 302) {
          this.subSectorData = res.entity;
          this.loading = false;
          this.dataSource = new MatTableDataSource(this.subSectorData);
          this.dataSource.paginator = this.paginator;
          this.dataSource.sort = this.sort;
        } else {
          this.loading = false;
        }
      },
      err => {
        this.loading = false;
        this.notificationAPI.alertWarning("SERVER ERROR!!")
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
