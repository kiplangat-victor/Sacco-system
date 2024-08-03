import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';
import { GlSubheadService } from '../gl-subhead.service';

@Component({
  selector: 'app-gl-subhead-lookup',
  templateUrl: './gl-subhead-lookup.component.html',
  styleUrls: ['./gl-subhead-lookup.component.scss']
})
export class GlSubheadLookupComponent implements OnInit, OnDestroy {
  displayedColumns: string[] = ['index', 'glCode', 'glSubheadCode', 'glSubheadDescription', 'postedBy', 'verifiedFlag'];
  dataSource!: MatTableDataSource<any>;
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;
  data: any;
  error: any;
  employeeEmail: any;
  employee_id: any;
  creatingAccount = false;
  formData: any;
  respData: any;
  loading: boolean = false;
  destroy$: Subject<boolean> = new Subject<boolean>();
  constructor(
    private glSubheadCodeAPI: GlSubheadService,
    private notificationAPI: NotificationService,
    public dialogRef: MatDialogRef<GlSubheadLookupComponent>
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
    this.glSubheadCodeAPI.find().pipe(takeUntil(this.destroy$)).subscribe(
      res => {
        if (res.statusCode === 302) {
          this.respData = res;
          this.dataSource = new MatTableDataSource(this.respData.entity);
          this.dataSource.paginator = this.paginator;
          this.dataSource.sort = this.sort;
          this.loading = false;
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
