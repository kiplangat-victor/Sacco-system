import { HttpParams } from "@angular/common/http";
import { Component, Inject, OnDestroy, OnInit, ViewChild } from "@angular/core";
import { MatDialogRef, MAT_DIALOG_DATA } from "@angular/material/dialog";
import { MatPaginator } from "@angular/material/paginator";
import { MatSort } from "@angular/material/sort";
import { MatTableDataSource } from "@angular/material/table";
import { Subject, Subscription } from "rxjs";
import { takeUntil } from "rxjs/operators";
import { NotificationService } from "src/@core/helpers/NotificationService/notification.service";
import { AccountsService } from "src/app/administration/Service/AccountsService/accounts/accounts.service";

@Component({
  selector: 'app-repayment-accounts',
  templateUrl: './repayment-accounts.component.html',
  styleUrls: ['./repayment-accounts.component.scss']
})
export class RepaymentAccountsComponent implements OnInit, OnDestroy {
  displayedColumns: string[] = ['index', 'acid', 'accountType', 'accountStatus', 'accountName'];
  dataSource!: MatTableDataSource<any>;
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;
  data: any;
  error: any;
  results: any;
  id: any;
  loading = false;
  params: HttpParams;
  destroy$: Subject<boolean> = new Subject<boolean>();
  constructor(
    private accountsAPI: AccountsService,
    private notificationAPI: NotificationService,
    @Inject(MAT_DIALOG_DATA) public customer_account_code: any,
    public dialogRef: MatDialogRef<RepaymentAccountsComponent>
  ) {}
  ngOnInit() {


    console.log("customer_account_code", this.customer_account_code)

    this.getData();
    this.dialogRef.backdropClick().subscribe(() => {
      this.dialogRef.close();
    });
  }
  ngOnDestroy(): void {
    this.destroy$.next(true);
    this.destroy$.complete();
  }

  getData() {
    this.loading = true;
    this.accountsAPI.retrieveOperativeAccountPerCustomerCode(this.customer_account_code).pipe(takeUntil(this.destroy$)).subscribe(
      {
        next: (
          (res) => {
            if (res.statusCode === 302) {
              this.loading = false;
              this.data = res.entity;
              this.dataSource = new MatTableDataSource(this.data);
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
