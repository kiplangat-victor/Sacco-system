import { Subscription } from 'rxjs';
import { HttpParams } from '@angular/common/http';
import { Component, OnInit, ViewChild } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';
import { AccountsService } from 'src/app/administration/Service/AccountsService/accounts/accounts.service';
@Component({
  selector: 'app-all-accounts-look-up',
  templateUrl: './all-accounts-look-up.component.html',
  styleUrls: ['./all-accounts-look-up.component.scss']
})
export class AllAccountsLookUpComponent implements OnInit {
  displayedColumns: string[] = [
    'index',
    'acid',
    'account_name',
    'account_type',
    'customer_code',
    'product_code',
    'product_code_desc',
    'verified_flag'
  ];

  dataSource!: MatTableDataSource<any>;
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;
  data: any;
  error: any;
  respData: any;
  loading: boolean;
  acid: any;
  id: any;
  params: HttpParams;
  accountStatus: string;
  accountType: string;
  customerType: string;
  customerCode: string;
  nationalId: string;
  solCode: string;
  fromDate: any = new Date(Date.now() - (3600 * 1000 * 24));
  toDate: any = new Date(Date.now() + (3600 * 1000 * 24));
  accountName: any;
  constructor(
    public fb: FormBuilder,
    private accountsAPI: AccountsService,
    private notificationAPI: NotificationService,
    public dialogRef: MatDialogRef<AllAccountsLookUpComponent>
  ) { }
  formData = this.fb.group({
    accountName: [""],
    accountStatus: [""],
    customerType: [""],
    solCode: [""],
    accountType: [""],
    acid: [""],
    id: [""],
    customerCode: [""],
    nationalId: [""],
    fromDate: [this.fromDate],
    toDate: [this.toDate]
  });

  ngOnDestroy(): void {
  }
  ngOnInit() {
  }

  getAccounts() {
    this.loading = true;
    if (this.formData.valid) {
      this.accountStatus = this.formData.controls.accountStatus.value;
      this.accountType = this.formData.controls.accountType.value;
      this.customerType = this.formData.controls.customerType.value;
      this.acid = this.formData.controls.acid.value;
      this.id = this.formData.controls.id.value;
      this.customerCode = this.formData.controls.customerCode.value;
      this.fromDate = this.formData.controls.fromDate.value;
      this.accountName = this.formData.controls.accountName.value;
      this.nationalId = this.formData.controls.nationalId.value;
      this.solCode = this.formData.controls.solCode.value;
      this.toDate= this.formData.controls.toDate.value;
      this.params = new HttpParams()
        .set('accountName', this.accountName)
        .set('accountStatus', this.accountStatus)
        .set('accountType', this.accountType)
        .set('customerType', this.customerType)
        .set('acid', this.acid)
        .set('id', this.id)
        .set('customerCode', this.customerCode)
        .set('fromDate', this.fromDate)
        .set('nationalId', this.nationalId)
        .set('solCode', this.solCode)
        .set('toDate', this.toDate);
      this.accountsAPI
        .findAccountDetails(this.params).subscribe(
          {
            next: (
              (res) => {
                if (res.statusCode === 302) {
                  this.loading = false;
                  this.data = res.entity;
                  this.dataSource = new MatTableDataSource(this.data);
                  this.dataSource.paginator = this.paginator;
                  this.dataSource.sort = this.sort;
                }
              }
            ),
            error: (
              (err) => {
                this.loading = false;
              }
            ),
            complete: (
              () => {

              }
            )
          }
      ), Subscription;
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
