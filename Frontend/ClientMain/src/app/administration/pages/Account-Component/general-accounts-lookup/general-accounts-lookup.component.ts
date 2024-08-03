import { HttpParams } from '@angular/common/http';
import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';
import { AccountsService } from 'src/app/administration/Service/AccountsService/accounts/accounts.service';
import { BranchesService } from '../../SystemConfigurations/GlobalParams/branches/branches.service';
import { MembershipConfigService } from '../../SystemConfigurations/GlobalParams/membership-config/membership-config.service';

@Component({
  selector: 'app-general-accounts-lookup',
  templateUrl: './general-accounts-lookup.component.html',
  styleUrls: ['./general-accounts-lookup.component.scss'],
})
export class GeneralAccountsLookupComponent implements OnInit, OnDestroy {
  AccountTypeArray: any = [
    {
      accountType: 'LAA',
      accountRef: 'LOAN ACCOUNT',
    },
    {
      accountType: 'OAB',
      accountRef: 'OFFICE ACCOUNT',
    },
    {
      accountType: 'SBA',
      accountRef: 'SAVINGS ACCOUNT',
    },
     {
      accountType: 'TDA',
      accountRef: 'TERM DEPOSIT',
    }
  ];
  membershipTypeArray: any;
  accountStatusArray: any [] = ['ACTIVE', 'FROZEN', 'DORMANT', 'SUSPENDED'];
  displayedColumns: string[] = [
    'index',
    'cust_id',
    'cust_name',
    'accountType',
    'cust_joiningdate',
    'verifiedFlag',
  ];
  dataSource!: MatTableDataSource<any>;
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;
  data: any;
  error: any;
  respData: any;
  loading: boolean;
  acid: any;
  accountType: any;
  account_type: any;
  params: HttpParams;
  branchesdata: any;
  eventValue: any;
  customer_branch_array: any;
  accountStatusValue: any;
  branchCodeValue: any;
  customerTypeValue: any;
  accountStatus: any;
  customerType: any;
  solCode: string;
  account_type_Value: any;
  fromDate: any = new Date(Date.now() - (3600 * 1000 * 24));
  toDate: any = new Date(Date.now() + (3600 * 1000 * 24));
  destroy$: Subject<boolean> = new Subject<boolean>();

  formData: FormGroup;
  currentUser: any

  constructor(
    public fb: FormBuilder,
    private branchesAPI: BranchesService,
    private accountsAPI: AccountsService,
    private notificationAPI: NotificationService,
    private memberConfigAPI: MembershipConfigService,
    public dialogRef: MatDialogRef<GeneralAccountsLookupComponent>
  ) { }




  ngOnDestroy(): void {
    this.destroy$.next(true);
    this.destroy$.complete();
  }
  ngOnInit() {
    this.getBranches();
    this.getMemberType();

    this.currentUser = JSON.parse(localStorage.getItem('auth-user'));

  this.formData = this.fb.group({
    accountStatus: [''],
    accountName: [""],
    customerType: [''],
    solCode: [this.currentUser.entityId],
    accountType: [''],
    acid: [''],
    customerCode: [''],
    name: [""],
    nationalId: [''],
    fromDate: [this.fromDate],
    toDate: [this.toDate]
  });
  }
  getBranches() {
    this.loading = true;
     this.branchesAPI.find().pipe(takeUntil(this.destroy$)).subscribe(
       (res) => {
        this.customer_branch_array = res.entity;
         if (res.statusCode === 302) {
          this.loading = false;
          this.customer_branch_array = res.entity;
         } else {
           this.loading = false;
         }
      },
      (err) => {
        this.error = err;
        this.loading = false;
        this.notificationAPI.alertWarning("Server Error: !!")
      }
    );
  }
  getMemberType() {
    this.loading = true;
     this.memberConfigAPI.findAll().pipe(takeUntil(this.destroy$)).subscribe(
       (res) => {
        this.membershipTypeArray = res.entity;
         if (res.statusCode === 302) {
          this.loading = false;
           this.membershipTypeArray = res.entity;
         } else {
           this.loading = false;
         }
      },
      (err) => {
        this.error = err;
        this.loading = false;
        this.notificationAPI.alertWarning("Server Error: !!");
      }
    );
  }

  getFilteredCustomers() {
    this.loading = true;
    if (this.formData.valid) {
      this.params = new HttpParams()
        .set('accountName', this.formData.value.accountName)
        .set('accountStatus', this.formData.controls.accountStatus.value)
        .set('accountType', this.formData.controls.accountType.value)
        .set('customerType', this.formData.controls.customerType.value)
        .set('acid', this.formData.controls.acid.value)
        .set('customerCode', this.formData.controls.customerCode.value)
        .set('fromDate', this.formData.controls.fromDate.value)
        .set('nationalId', this.formData.controls.nationalId.value)
        .set('solCode', this.formData.controls.solCode.value)
        .set('toDate', this.formData.controls.toDate.value);
      this.accountsAPI
        .getGeneralAccounts(this.params).pipe(takeUntil(this.destroy$))
        .subscribe(
          (res) => {
            this.loading = false;
            this.data = res;
            this.respData = this.data.entity;
            this.dataSource = new MatTableDataSource(this.respData);
            this.dataSource.paginator = this.paginator;
            this.dataSource.sort = this.sort;
            this.loading = false;
          },
          (err) => {
            this.loading = false;
            this.notificationAPI.alertWarning("Server Error: !!")
          }
        );
    } else if (!this.formData.valid) {
      this.loading = false;
      this.notificationAPI.alertWarning("Account Lookup form data invalid");
    }
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
