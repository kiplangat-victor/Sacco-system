import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';
import { GlCodeService } from '../gl-code.service';

@Component({
  selector: 'app-gl-code-lookup',
  templateUrl: './gl-code-lookup.component.html',
  styleUrls: ['./gl-code-lookup.component.scss']
})
export class GlCodeLookupComponent implements OnInit, OnDestroy {
  displayedColumns: string[] = ['index', 'glCode', 'fromRange', 'toRange', 'glDescription', 'verifiedFlag'];
  dataSource!: MatTableDataSource<any>;
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;
  data: any;
  error: any;
  respData: any;
  loading: boolean = false;
  destroy$: Subject<boolean> = new Subject<boolean>();
  constructor(
    public dialogRef: MatDialogRef<GlCodeLookupComponent>,
    private glcodeAPI: GlCodeService,
    private notificationAPI: NotificationService
  ) { }
  ngOnDestroy(): void {
    this.destroy$.next(true);
    this.destroy$.complete();
  }
  ngOnInit() {
    this.getData();
  }
  getData() {
    this.loading = true;
    this.glcodeAPI.find().pipe(takeUntil(this.destroy$)).subscribe(res => {
      console.log(res);
      if (res.statusCode === 302) {
        this.respData = res;
        this.loading = false;
        this.dataSource = new MatTableDataSource(this.respData.entity);
        this.dataSource.paginator = this.paginator;
        this.dataSource.sort = this.sort;
      } else {
        this, this.loading = false;
      }
    },
      err => {
        this.error = err;
        this.loading = false;
        this.notificationAPI.alertWarning("SERVER ERROR: TRY AGAIN LATER");
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
