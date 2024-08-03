import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';
import { MainClassificationMaintenanceComponent } from '../main-classification-maintenance/main-classification-maintenance.component';
import { MainClassificationService } from '../main-classification.service';

@Component({
  selector: 'app-main-classification-lookup',
  templateUrl: './main-classification-lookup.component.html',
  styleUrls: ['./main-classification-lookup.component.scss']
})
export class MainClassificationLookupComponent implements OnInit, OnDestroy {
  displayedColumns: string[] = [
    'index', 'assetClassificationCode', 'assetClassificationName', 'assetClassificationDescription', 'postedBy', 'verifiedFlag'];

  dataSource!: MatTableDataSource<any>;
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;
  loading: boolean = false;
  error: any;
  fromDialog: any;
  mainData: any;
  destroy$: Subject<boolean> = new Subject<boolean>();
  constructor(
    private notificationAPI: NotificationService,
    private mainClassificationAPI: MainClassificationService,
    public dialogRef: MatDialogRef<MainClassificationLookupComponent>
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
    this.mainClassificationAPI.find().pipe(takeUntil(this.destroy$)).subscribe(
      res => {
        if (res.statusCode === 302) {
          this.mainData = res.entity;
          this.loading = false;
          this.dataSource = new MatTableDataSource(this.mainData);
          this.dataSource.paginator = this.paginator;
          this.dataSource.sort = this.sort;
        } else {
          this.loading = false;
        }
      },
      err => {
        this.loading = false;
        this.notificationAPI.alertWarning("SERVER ERROR!!");
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
