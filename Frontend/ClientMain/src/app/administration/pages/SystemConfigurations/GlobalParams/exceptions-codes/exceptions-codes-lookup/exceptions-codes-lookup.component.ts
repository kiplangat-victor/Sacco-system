import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';
import { Subject, Subscription } from 'rxjs';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { ExceptionsCodesServiceService } from '../exceptions-codes-service.service';
import { takeUntil } from 'rxjs/operators';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';

@Component({
  selector: 'app-exceptions-codes-lookup',
  templateUrl: './exceptions-codes-lookup.component.html',
  styleUrls: ['./exceptions-codes-lookup.component.scss']
})
export class ExceptionsCodesLookupComponent implements OnInit, OnDestroy {
  title = 'export-table-data-to-any-format';
  displayedColumns: string[] = ['index', 'exceptionCode', 'exce_description', 'exce_code_type', 'exce_working_class', 'verifiedFlag'];
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
    private notificationAPI: NotificationService,
    private exceptionAPI: ExceptionsCodesServiceService,
    public dialogRef: MatDialogRef<ExceptionsCodesLookupComponent>
  ) { }
  ngOnInit() {
    this.getData();
  }
  ngOnDestroy(): void {
    this.destroy$.next(true);
    this.destroy$.complete();
  }

  getData() {
    this.loading = true;
    this.subscription = this.exceptionAPI.find().pipe(takeUntil(this.destroy$)).subscribe(
      res => {
        if (res.statusCode === 302) {
          this.data = res;
          this.dataSource = new MatTableDataSource(this.data.entity);
          this.dataSource.paginator = this.paginator;
          this.dataSource.sort = this.sort;
          this.loading = false;
        } else {
          this.loading = false;
        }
      },
      err => {
        this.loading = false;
        this.notificationAPI.alertWarning("SERVER ERROR: EXECTIONS NOT LOADED");
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
