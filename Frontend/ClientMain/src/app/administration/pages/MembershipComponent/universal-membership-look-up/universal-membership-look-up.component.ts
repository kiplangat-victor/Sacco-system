import { Subscription } from 'rxjs';
import { Component, ElementRef, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { DatePipe } from '@angular/common';
import { HttpParams } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { MembershipService } from 'src/app/administration/Service/Membership/membership.service';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';
import { MembershipConfigService } from '../../SystemConfigurations/GlobalParams/membership-config/membership-config.service';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

@Component({
  selector: 'app-universal-membership-look-up',
  templateUrl: './universal-membership-look-up.component.html',
  styleUrls: ['./universal-membership-look-up.component.scss']
})
export class UniversalMembershipLookUpComponent implements OnInit, OnDestroy {
  membershipTypeArray: any;
  displayedColumns: string[] = ['index', 'customerCode', 'customerUniqueId', 'customerName', 'postedOn', 'verifiedFlag'];
  dataSource!: MatTableDataSource<any>;
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;
  memeberIdentityArrArray: any[] = ["Individual", "Group"];
  customerType: any;
  identity: any;
  loading = false;
  today = new Date();
  params: HttpParams;
  results: any;
  uniqueId: any;
  customerCode: any;
  toDate: any = new Date();
  fromDate: any = new Date();
  verified: any;
  phoneNumber: any;
  name: any;
  destroy$: Subject<boolean> = new Subject<boolean>();
  constructor(
    public fb: FormBuilder,
    private datepipe: DatePipe,
    private membershipAPI: MembershipService,
    private notificationAPI: NotificationService,
    private customerConfigAPI: MembershipConfigService,
    public dialogRef: MatDialogRef<UniversalMembershipLookUpComponent>
  ) { }
  ngOnDestroy(): void {
    this.destroy$.next(true);
    this.destroy$.complete();
  }
  ngOnInit() {
    this.getCustomerType();
  }
  formData = this.fb.group({
    identity: [''],
    customerType: [''],
    uniqueId: [''],
    toDate: [new Date],
    fromDate: [new Date()],
    customerCode: [''],
    verified: ['Y'],
    phoneNumber: [''],
    name: [""]
  });

  getCustomerType() {
    this.loading = true;
    this.customerConfigAPI.findAll().pipe(takeUntil(this.destroy$)).subscribe(
      (data) => {
        if (data.statusCode === 302) {
          this.loading = false;
          this.membershipTypeArray = data.entity;
          console.log(data.entity, "membershipArray")
        } else {
          this.loading = false;
        }
      }, (err) => {
        this.loading = false;
        this.notificationAPI.alertWarning("Server error: !!");
      }
    );
  }
  onSubmit() {
    this.loading = true;
    this.identity = this.formData.controls.identity.value;
    this.phoneNumber = this.formData.controls.phoneNumber.value;
    this.customerType = this.formData.controls.customerType.value;
    this.uniqueId = this.formData.controls.uniqueId.value;
    this.customerCode = this.formData.controls.customerCode.value;
    this.verified = this.formData.controls.verified.value;
    this.name = this.formData.controls.name.value;
    this.fromDate = this.datepipe.transform(this.formData.controls.fromDate.value, 'yyyy-MM-dd h:mm:ss');
    this.toDate = this.datepipe.transform(this.formData.controls.toDate.value, "yyyy-MM-dd h:mm:ss");
    this.params = new HttpParams()
      .set('customerType', '')
      .set('identity', this.identity)
      .set('phoneNumber', this.phoneNumber)
      .set('uniqueId', this.uniqueId)
      .set('customerCode', this.customerCode)
      .set('fromDate', this.fromDate)
      .set('toDate', this.toDate)
      .set('verified', this.verified)
      .set('name', this.name);
    this.membershipAPI.getFilteredMembership(this.params).pipe(takeUntil(this.destroy$)).subscribe(
      {
        next: (
          (res) => {
            console.log(res);
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
