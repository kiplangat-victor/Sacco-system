import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { Subject, Subscription } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';
import { ProductsService } from 'src/app/administration/Service/products/products.service';

@Component({
  selector: 'app-term-deposit-lookup',
  templateUrl: './term-deposit-lookup.component.html',
  styleUrls: ['./term-deposit-lookup.component.scss']
})
export class TermDepositLookupComponent implements OnInit, OnDestroy {
  displayedColumns: string[] = ['index', 'productCode', 'productCodeDesc', 'postedTime', 'verifiedFlag', 'deletedFlag'];
  dataSource!: MatTableDataSource<any>;
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;
  formData: any;
  loading: boolean = true;
  data: any;
  destroy$: Subject<boolean> = new Subject<boolean>();
  constructor(
    private productsAPI: ProductsService,
    private notificationAPI: NotificationService,
    private dialogRef: MatDialogRef<TermDepositLookupComponent>
  ) { }
  ngOnDestroy() {
    this.destroy$.next(true);
    this.destroy$.complete();
  }
  ngOnInit() {
    this.getData();
  }
  getData() {
    this.productsAPI.findTda().pipe(takeUntil(this.destroy$)).subscribe(
      {
        next: (
          (res) => {
            if (res.statusCode === 302) {
              this.data = res;
              this.dataSource = new MatTableDataSource(this.data.entity);
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
        ), complete: (
          () => {

          }
        )
      }
    ), Subscription
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
