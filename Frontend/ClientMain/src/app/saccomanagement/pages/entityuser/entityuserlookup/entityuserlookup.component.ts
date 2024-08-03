import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { Subject, Subscription } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';
import { EntityuserService } from '../entityuser.service';

@Component({
  selector: 'app-entityuserlookup',
  templateUrl: './entityuserlookup.component.html',
  styleUrls: ['./entityuserlookup.component.scss']
})
export class EntityuserlookupComponent implements OnInit, OnDestroy {
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
    private entityUserAPI: EntityuserService,
    private notificationAPI: NotificationService,
    public dialogRef: MatDialogRef<EntityuserlookupComponent>
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
    this.entityUserAPI.entityUsers().pipe(takeUntil(this.destroy$)).subscribe(
      {
        next: (res) => {
          if (res.statusCode === 302) {
            this.data = res.entity;
            this.loading = false;
            this.dataSource = new MatTableDataSource(this.data);
            this.dataSource.paginator = this.paginator;
            this.dataSource.sort = this.sort;
          } else {
            this.loading = false;
          }
        },
        error: (err) => {
          this.loading = false;
          this.notificationAPI.alertWarning("SERVER ERROR: TRY AGAIN LATER");
        },
        complete: () => {

        }
      }), Subscription;
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
