import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { AuthService } from 'src/@core/AuthService/auth.service';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';
@Component({
  selector: 'app-account-users',
  templateUrl: './account-users.component.html',
  styleUrls: ['./account-users.component.scss']
})
export class AccountUsersComponent implements OnInit, OnDestroy {
  displayedColumns: string[] = ['index', 'entityId', 'fullName', 'phoneNo', 'email', 'solCode'];
  dataSource!: MatTableDataSource<any>;
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;
  data: any;
  error: any;
  loading = false;
  results: any;
  destroy$: Subject<boolean> = new Subject<boolean>();
  constructor(
    private authService: AuthService,
    private notificationAPI: NotificationService,
    public dialogRef: MatDialogRef<AccountUsersComponent>
  ) { }

  ngOnDestroy() {
    this.destroy$.next(true);
    this.destroy$.complete();
  }
  ngOnInit() {
    this.getData();
  }
  getData() {
    this.loading = true;
    this.authService.allUsers().pipe(takeUntil(this.destroy$)).subscribe(
      (res) => {
          this.data = res
          this.loading = false;
          this.dataSource = new MatTableDataSource(this.data);
          this.dataSource.paginator = this.paginator;
          this.dataSource.sort = this.sort;
      }, (err) => {
        this.loading = false;
        this.notificationAPI.alertWarning("Server Error: !!");
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
