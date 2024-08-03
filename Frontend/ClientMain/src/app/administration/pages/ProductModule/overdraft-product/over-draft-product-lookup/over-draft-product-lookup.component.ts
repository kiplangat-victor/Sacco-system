import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { Subject, Subscription } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';
import { ProductsService } from 'src/app/administration/Service/products/products.service';
import { OfficeProductLookUpComponent } from '../../office-product/office-product-look-up/office-product-look-up.component';

@Component({
  selector: 'app-over-draft-product-lookup',
  templateUrl: './over-draft-product-lookup.component.html',
  styleUrls: ['./over-draft-product-lookup.component.scss']
})
export class OverDraftProductLookupComponent implements OnInit, OnDestroy {
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
    public dialogRef: MatDialogRef<OfficeProductLookUpComponent>,

  ) { }
  ngOnDestroy() {
    this.destroy$.next(true);
    this.destroy$.complete();
  }
  ngOnInit() {
    this.getData();
  }
  getData() {
    this.productsAPI.findOda().pipe(takeUntil(this.destroy$)).subscribe(
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
