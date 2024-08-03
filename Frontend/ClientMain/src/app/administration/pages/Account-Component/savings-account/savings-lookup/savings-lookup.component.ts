import { DatePipe } from "@angular/common";
import { HttpParams } from "@angular/common/http";
import { Component, OnDestroy, OnInit, ViewChild } from "@angular/core";
import { FormBuilder } from "@angular/forms";
import { MatDialogRef } from "@angular/material/dialog";
import { MatPaginator } from "@angular/material/paginator";
import { MatSort } from "@angular/material/sort";
import { MatTableDataSource } from "@angular/material/table";
import { Subject, Subscription } from "rxjs";
import { takeUntil } from "rxjs/operators";
import { NotificationService } from "src/@core/helpers/NotificationService/notification.service";
import { AccountsService } from "src/app/administration/Service/AccountsService/accounts/accounts.service";

@Component({
  selector: 'app-savings-lookup',
  templateUrl: './savings-lookup.component.html',
  styleUrls: ['./savings-lookup.component.scss']
})
export class SavingsLookupComponent implements OnInit, OnDestroy {
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
  id: any;
  params: HttpParams;
  accountType = "SBA";
  fromDate: any = new Date(Date.now() - (3600 * 1000 * 24));
  toDate: any = new Date(Date.now() + (3600 * 1000 * 24));
  destroy$: Subject<boolean> = new Subject<boolean>();
  constructor(
    public fb: FormBuilder,
    private datepipe: DatePipe,
    private accountsAPI: AccountsService,
    private notificationAPI: NotificationService,
    public dialogRef: MatDialogRef<SavingsLookupComponent>
  ) { }
  formData = this.fb.group({
    acid: [''],
    id: [''],
    accountName: [""],
    customerCode: [''],
    nationalId: [''],
    fromDate: [this.fromDate],
    toDate: [this.toDate]
  });

  ngOnDestroy(): void {
    this.destroy$.next(true);
    this.destroy$.complete();
  }
  ngOnInit() {
    this.getAccounts();
  }

  getAccounts() {
    this.loading = true;
    if (this.formData.valid) {
      this.params = new HttpParams()
        .set("accountType", this.accountType)
        .set('accountName', this.formData.value.accountName)
        .set('acid', this.formData.value.acid)
        .set('id', this.formData.value.id)
        .set('customerCode', this.formData.value.customerCode)
        .set('fromDate', this.datepipe.transform(this.formData.controls.fromDate.value, 'yyyy-MM-dd'))
        .set('nationalId', this.formData.value.nationalId)
        .set('toDate', this.datepipe.transform(this.formData.controls.toDate.value, 'yyyy-MM-dd'));
      this.subscription = this.accountsAPI
        .getAccounts(this.params).pipe(takeUntil(this.destroy$)).subscribe(
          (res) => {
            console.log(res);
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
