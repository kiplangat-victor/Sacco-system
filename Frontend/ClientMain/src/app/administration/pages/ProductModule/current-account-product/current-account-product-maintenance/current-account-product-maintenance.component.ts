import { HttpParams } from '@angular/common/http';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { DataStoreService } from 'src/@core/helpers/data-store.service';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';
import { ProductsService } from 'src/app/administration/Service/products/products.service';
import { CurrentAccountProductLookupComponent } from '../current-account-product-lookup/current-account-product-lookup.component';

@Component({
  selector: 'app-current-account-product-maintenance',
  templateUrl: './current-account-product-maintenance.component.html',
  styleUrls: ['./current-account-product-maintenance.component.scss']
})
export class CurrentAccountProductMaintenanceComponent implements OnInit, OnDestroy {
  params: HttpParams;
  lookupdata: any;
  productCode: any;
  productCodeDesc: any;
  showDetails: boolean = false;
  existingData: boolean = false;
  loading = false;
  submitted = false;
  functionArray: any;
  destroy$: Subject<boolean> = new Subject<boolean>();
  randomCode: any;
  function_type: any;
  results: any;
  constructor(
    public fb: FormBuilder,
    private router: Router,
    private dialog: MatDialog,
    private productsAPI: ProductsService,
    private datastoreApi: DataStoreService,
    private notificationAPI: NotificationService
  ) {
    this.functionArray = this.datastoreApi.getActionsByPrivilege("PRODUCTS");
    this.functionArray = this.functionArray.filter(
      (arr: string) =>
        arr === 'ADD' ||
        arr === 'INQUIRE' ||
        arr === 'MODIFY' ||
        arr === 'VERIFY' ||
        arr === 'DELETE');
  }
  ngOnDestroy(): void {
    this.destroy$.next(true);
    this.destroy$.complete();
  }
  ngOnInit(): void {
    this.randomCode = "CAA" + Math.floor(Math.random() * (10000));
  }
  formData = this.fb.group({
    function_type: ['', Validators.required],
    productCode: ['', Validators.required],
    productCodeDesc: ['CAA Product Description ...'],
    productType: ['CAA']
  });
  onSelectFunction(event: any) {
    this.function_type = event.target.value;
    if (event.target.value == 'ADD') {
      this.existingData = false;
      this.showDetails = true;
      this.formData.controls.productCode.setValue(this.randomCode);
    } else if (event.target.value !== 'ADD') {
      this.existingData = true;
      this.showDetails = true;
      this.formData.controls.productCode.setValue("");
    }
  }
  get f() {
    return this.formData.controls;
  }
  onSubmit() {
    this.loading = true;
    this.submitted = true;
    if (this.formData.valid) {
      this.function_type = this.f.function_type.value;
      if (this.function_type == 'ADD') {
        this.params = new HttpParams()
          .set('productCode', this.formData.value.productCode);
        this.productsAPI.caaCode(this.params).pipe(takeUntil(this.destroy$)).subscribe(
          {
            next: (
              (res) => {
                if (res.statusCode === 404) {
                  this.loading = false;
                  this.router.navigate([`/system/product/current-account/data/view`], { skipLocationChange: true, queryParams: { formData: this.formData.value } });
                } else if (res.statusCode === 200) {
                  this.loading = false;
                  this.notificationAPI.alertWarning(res.message);
                }
              }
            ),
            error: (
              (err) => {
                this.loading = false;
                this.notificationAPI.alertWarning("Server Error: !!!");
              }
            ),
            complete: (
              () => {

              }
            )
          }
        )

      } else if (this.function_type !== 'ADD') {
        this.loading = false;
        this.router.navigate([`/system/product/current-account/data/view`], { skipLocationChange: true, queryParams: { formData: this.formData.value } });
      }
    } else {
      this.notificationAPI.alertWarning("CURRENT ACCOUNT PRODUCT FORM DATA INVALID");
    }
  }

  schemeCodeLookup(): void {
    const dialogRef = this.dialog.open(CurrentAccountProductLookupComponent, {
      width: '40%',
      autoFocus: false,
      maxHeight: '90vh'
    });
    dialogRef.afterClosed().subscribe(result => {
      this.lookupdata = result.data;
      this.productCode = this.lookupdata.productCode;
      this.productCodeDesc = this.lookupdata.productCodeDesc;
      this.formData.controls.productCode.setValue(this.productCode);
      this.formData.controls.productCodeDesc.setValue(this.productCodeDesc);
    });
  }
}
