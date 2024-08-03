import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { DataStoreService } from 'src/@core/helpers/data-store.service';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';
import { OfficeProductLookUpComponent } from '../office-product-look-up/office-product-look-up.component';
import { OfficeProductService } from '../office-product.service';

@Component({
  selector: 'app-office-product-maintenance',
  templateUrl: './office-product-maintenance.component.html',
  styleUrls: ['./office-product-maintenance.component.scss']
})
export class OfficeProductMaintenanceComponent implements OnInit, OnDestroy {
  lookupdata: any;
  showDetails: boolean = false;
  existingData: boolean = false;
  loading = false;
  submitted = false;
  functionArray: any;
  destroy$: Subject<boolean> = new Subject<boolean>();
  randomCode: any;
  function_type: any;
  productCodeDesc: any;
  productCode: any;
  constructor(
    private fb: FormBuilder,
    private router: Router,
    private dialog: MatDialog,
    private productsAPI: OfficeProductService,
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
    this.randomCode = "OAB" + Math.floor(Math.random() * (1000));
  }
  formData = this.fb.group({
    function_type: ['', Validators.required],
    productCode: ['', Validators.required],
    productCodeDesc: ['', Validators.required],
    productType: ['OAB']
  });
  onSelectFunction(event: any) {
    this.function_type = event.target.value;
    if (event.target.value == 'ADD') {
      this.existingData = false;
      this.showDetails = true;
      this.formData.controls.productCode.setValue(this.randomCode);
      this.formData.controls.productCodeDesc.setValue("Office Product Description ...");
    } else if (event.target.value !== 'ADD') {
      this.existingData = true;
      this.showDetails = true;
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
      this.productCode = this.f.productCode.value;
      if (this.function_type == 'ADD') {
        this.productsAPI.oabCode(this.productCode).pipe(takeUntil(this.destroy$)).subscribe(
          {
            next: (
              (res) => {
                if (res.statusCode === 404) {
                  this.loading = false;
                  this.router.navigate([`/system/product/office-product/data/view`], { skipLocationChange: true, queryParams: { formData: this.formData.value } });
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
        this.router.navigate([`/system/product/office-product/data/view`], { skipLocationChange: true, queryParams: { formData: this.formData.value } });
      }
    } else {
      this.notificationAPI.alertWarning("OFFICE PRODUCT FORM DATA INVALID");
    }
  }
  schemeCodeLookup(): void {
    const dialogRef = this.dialog.open(OfficeProductLookUpComponent, {
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
