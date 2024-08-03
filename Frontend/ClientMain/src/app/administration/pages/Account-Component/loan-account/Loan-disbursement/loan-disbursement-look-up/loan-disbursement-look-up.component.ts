import { HttpParams } from "@angular/common/http";
import { Component, OnInit, ViewChild } from "@angular/core";
import { FormBuilder } from "@angular/forms";
import { MatDialogRef } from "@angular/material/dialog";
import { MatPaginator } from "@angular/material/paginator";
import { MatSort } from "@angular/material/sort";
import { MatTableDataSource } from "@angular/material/table";
import { Subscription } from "rxjs";
import { NotificationService } from "src/@core/helpers/NotificationService/notification.service";
import { AccountsService } from "src/app/administration/Service/AccountsService/accounts/accounts.service";

@Component({
  selector: 'app-loan-disbursement-look-up',
  templateUrl: './loan-disbursement-look-up.component.html',
  styleUrls: ['./loan-disbursement-look-up.component.scss']
})
export class LoanDisbursementLookUpComponent implements OnInit {
  displayedColumns: string[] = [
    'index',
    'acid',
    'customerCode',
    'accountName',
    'verifiedFlag',
  ];
  dataSource!: MatTableDataSource<any>;
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;
  subscription!: Subscription;
  data: any;
  error: any;
  respData: any;
  loading: boolean;
  acid: any;
  params: HttpParams;
  constructor(
    public fb: FormBuilder,
    private accountsAPI: AccountsService,
    private notificationAPI: NotificationService,
    public dialogRef: MatDialogRef<LoanDisbursementLookUpComponent>
  ) { }
  formData = this.fb.group({
    acid: [''],
    customerCode: [''],
    nationalId: [''],
  });

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }
  ngOnInit() {
  }

  getAccounts() {
    this.loading = true;
    if (this.formData.valid) {
      this.params = new HttpParams()
        .set("accountType", "LAA")
        .set('acid', this.formData.value.acid)
        .set('customerCode', this.formData.value.customerCode)
        .set('nationalId', this.formData.value.nationalId);

      this.subscription = this.accountsAPI
        .getAccounts(this.params).subscribe(
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
            this.error = err;
            this.notificationAPI.alertWarning(this.error);
          }
        );
    } else if (!this.formData.valid) {
      this.loading = false;
      this.notificationAPI.alertWarning("ACCOUNT LOOKUP FORM DATA INVALID");
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
