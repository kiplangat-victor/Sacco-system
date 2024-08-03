import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';
import { GlCodeLookupComponent } from '../../gl-code/gl-code-lookup/gl-code-lookup.component';
import { LinkedorganizationService } from '../linkedorganization.service';

@Component({
  selector: 'app-linked-organization-lookup',
  templateUrl: './linked-organization-lookup.component.html',
  styleUrls: ['./linked-organization-lookup.component.scss']
})
export class LinkedOrganizationLookupComponent implements OnInit, OnDestroy {
  displayedColumns: string[] = ['index','linkedOrganizationCode','organization_name', 'organization_main_office','verifiedFlag'];
  dataSource!: MatTableDataSource<any>;
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;
  respData: any;
  loading: boolean = false;
  destroy$: Subject<boolean> = new Subject<boolean>();
  constructor(
    private notificationAPI: NotificationService,
    public dialogRef: MatDialogRef<LinkedOrganizationLookupComponent>,
    private linkedOrganizationAPI: LinkedorganizationService
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
    this.linkedOrganizationAPI.find().pipe(takeUntil(this.destroy$)).subscribe(
      (data) => {
        if (data.statusCode === 302) {
          this.respData = data.entity;
          this.loading = false;
          this.dataSource = new MatTableDataSource(this.respData);
          this.dataSource.paginator = this.paginator;
        } else {
          this.loading = false;
        }
      }, (err) => {
        this.loading = false;
        this.notificationAPI.alertWarning("SERVER ERROR: TRY AGAIN LATER");
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
