import { DatePipe } from '@angular/common';
import { HttpParams } from '@angular/common/http';
import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { Subject, Subscription } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';
import { GroupMembershipService } from 'src/app/administration/Service/GroupMembership/group-membership.service';
@Component({
  selector: 'app-group-membership-look-up',
  templateUrl: './group-membership-look-up.component.html',
  styleUrls: ['./group-membership-look-up.component.scss']
})
export class GroupMembershipLookUpComponent implements OnInit, OnDestroy {
  displayedColumns: string[] = ['index', 'customerCode', 'customerUniqueId', 'customerName', 'postedOn', 'verifiedFlag'];
  dataSource!: MatTableDataSource<any>;
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;
  loading = false;
  params: HttpParams;
  results: any;
  toDate: any = new Date();
  uniqueId: any;
  fromDate: any = new Date();
  customerCode: any;
  customerType = 'Group';
  destroy$: Subject<boolean> = new Subject<boolean>();
  verified: any;
  name: any;
  constructor(
    private datepipe: DatePipe,
    public fb: FormBuilder,
    private groupMembershipAPI: GroupMembershipService,
    private notificationAPI: NotificationService,
    public dialogRef: MatDialogRef<GroupMembershipLookUpComponent>
  ) { }
  formData = this.fb.group({
    uniqueId: [''],
    toDate: [new Date()],
    fromDate: [new Date()],
    customerCode: [''],
    verified: [''],
    name: [""]
  });
  ngOnInit() {
  }
  ngOnDestroy(): void {
    this.destroy$.next(true);
    this.destroy$.complete();
  }

  onSubmit() {
    this.loading = true;
    this.uniqueId = this.formData.controls.uniqueId.value;
    this.customerCode = this.formData.controls.customerCode.value;
    this.verified = this.formData.controls.verified.value;
    this.name = this.formData.controls.name.value;
    this.fromDate = this.datepipe.transform(this.formData.controls.fromDate.value, 'yyyy-MM-dd h:mm:ss');
    this.toDate = this.datepipe.transform(this.formData.controls.toDate.value, "yyyy-MM-dd h:mm:ss");
    this.params = new HttpParams()
      .set('customerType', this.customerType)
      .set('uniqueId', this.uniqueId)
      .set('customerCode', this.customerCode)
      .set('fromDate', this.fromDate)
      .set('toDate', this.toDate)
      .set('verified', this.verified)
      .set('name', this.name);
    this.groupMembershipAPI.getAll(this.params).pipe(takeUntil(this.destroy$)).subscribe(
      {
        next: (
          (res) => {
            if (res.statusCode === 200) {
              this.results = res.entity;
              this.dataSource = new MatTableDataSource(this.results);
              this.dataSource.paginator = this.paginator;
              this.dataSource.sort = this.sort;
              this.loading = false;
            } else {
              this.loading = false;
            }
          }
        ),
        error: (
          (err) => {
            this.loading = false;
            this.notificationAPI.alertWarning("Server Error: !!");
          }
        ),
        complete: (
          () => {

          }
        )
      }
    ), Subscription;
  }
  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();
    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }
  onSelect(data: any) {
    this.dialogRef.close({ event: 'close', data: data });
  }
}
