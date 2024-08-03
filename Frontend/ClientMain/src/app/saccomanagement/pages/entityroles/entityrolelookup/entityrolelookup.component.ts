import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { Subject, Subscription } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';
import { RolesService } from 'src/app/administration/pages/AccessManagement/roles-management/roles.service';

@Component({
  selector: 'app-entityrolelookup',
  templateUrl: './entityrolelookup.component.html',
  styleUrls: ['./entityrolelookup.component.scss']
})
export class EntityrolelookupComponent implements OnInit, OnDestroy {
  displayedColumns: string[] = ['index', 'name', 'postedBy', 'postedFlag', 'postedTime', 'verifiedFlag'];
  dataSource!: MatTableDataSource<any>;
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;
  data: any;
  respData: any;
  loading: boolean = false;
  destroy$: Subject<boolean> = new Subject<boolean>();
  constructor(
    private rolesService: RolesService,
    private notificationAPI: NotificationService,
    public dialogRef: MatDialogRef<EntityrolelookupComponent>
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
    this.rolesService.find().pipe(takeUntil(this.destroy$)).subscribe(
      {
        next: (res) => {
          if (res.statusCode === 200) {
            this.respData = res.entity;
            this.loading = false;
            this.dataSource = new MatTableDataSource(this.respData);
            this.dataSource.paginator = this.paginator;
          } else {
            this.loading = false;
          }
        },
        error: (err) => {
          this.loading = false;
          this.notificationAPI.alertWarning("Server Error: !!");
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
