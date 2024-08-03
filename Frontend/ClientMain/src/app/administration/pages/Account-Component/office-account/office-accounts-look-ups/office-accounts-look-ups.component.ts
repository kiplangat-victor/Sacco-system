import { takeUntil } from 'rxjs/operators';
import { HttpParams } from "@angular/common/http";
import { Component, OnDestroy, OnInit, ViewChild } from "@angular/core";
import { MatDialogRef } from "@angular/material/dialog";
import { MatPaginator } from "@angular/material/paginator";
import { MatSort } from "@angular/material/sort";
import { MatTableDataSource } from "@angular/material/table";
import { Subject, Subscription } from "rxjs";
import { NotificationService } from "src/@core/helpers/NotificationService/notification.service";
import { AccountsService } from "src/app/administration/Service/AccountsService/accounts/accounts.service";

@Component({
  selector: 'app-office-accounts-look-ups',
  templateUrl: './office-accounts-look-ups.component.html',
  styleUrls: ['./office-accounts-look-ups.component.scss']
})
export class OfficeAccountsLookUpsComponent implements OnInit, OnDestroy {
  displayedColumns: string[] = ['index', 'acid', 'accountName', 'solCode', 'verifiedFlag'];
  dataSource!: MatTableDataSource<any>;
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;
  data: any;
  error: any;
  employeeEmail: any;
  employee_id: any;
  creatingAccount = false;
  formData: any;
  respData: any;
  loading: boolean;
  acid: any;
  account_type = 'OAB';
  accountType: any;
  params: HttpParams;
  destroy$: Subject<boolean> = new Subject<boolean>();
  constructor(
    private accountsAPI: AccountsService,
    private notificationAPI: NotificationService,
    public dialogRef: MatDialogRef<OfficeAccountsLookUpsComponent>
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
    this.accountType = this.account_type;
    this.params = new HttpParams().set('accountType', this.accountType);
    this.accountsAPI.getAccountsPerType(this.params).pipe(takeUntil(this.destroy$)).subscribe(
      {
        next: (
          (res) => {
            if (res.statusCode === 302) {
              this.loading = false;
              this.data = res;
              this.respData = this.data.entity;
              this.dataSource = new MatTableDataSource(this.respData);
              this.dataSource.paginator = this.paginator;
              this.dataSource.sort = this.sort;
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
