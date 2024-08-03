import { HttpParams } from '@angular/common/http';
import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';
import { ProductsService } from 'src/app/administration/Service/products/products.service';

@Component({
  selector: 'app-general-product-look-up',
  templateUrl: './general-product-look-up.component.html',
  styleUrls: ['./general-product-look-up.component.scss']
})
export class GeneralProductLookUpComponent implements OnInit, OnDestroy {
  displayedColumns: string[] = ['index', 'productCode', 'productCodeDesc', 'verifiedFlag', 'deletedFlag'];
  dataSource!: MatTableDataSource<any>;
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;
  loading = false;
  params: HttpParams;
  results: any;
  destroy$: Subject<boolean> = new Subject<boolean>();
  schemeTypes: any = [
    { code: 'LAA', description: 'Loan Products' },
    { code: 'SBA', description: 'Savings Products' },
    { code: 'OAB', description: 'Office Products' },
    { code: 'ODA', description: 'Overdraft Products' },
    { code: 'TDA', description: 'Fixed Deposits Products' },
    { code: 'CAA', description: 'Current Accounts Products' },
  ];
  productType: any;
  productCode: any;
  constructor(
    public fb: FormBuilder,
    private productsAPI: ProductsService,
    private notificationAPI: NotificationService,
    public dialogRef: MatDialogRef<GeneralProductLookUpComponent>
  ) { }
  formData = this.fb.group({
    productCode: [""],
    productType: ["", Validators.required]
  });

  ngOnInit() {
  }
  ngOnDestroy(): void {
    this.destroy$.next(true);
    this.destroy$.complete();
  }

  onSubmit() {
    this.loading = true;
    this.productType = this.formData.controls.productType.value;
    this.productCode = this.formData.controls.productCode.value;
    this.params = new HttpParams()
      .set('productCode', this.productCode)
      .set('productType', this.productType)
    this.productsAPI.findProduct(this.params).pipe(takeUntil(this.destroy$)).subscribe(
      {
        next: (
          (res) => {
            if (res.statusCode === 302) {
              this.loading = false;
              this.results = res;
              this.dataSource = new MatTableDataSource(this.results.entity);
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
    );
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
