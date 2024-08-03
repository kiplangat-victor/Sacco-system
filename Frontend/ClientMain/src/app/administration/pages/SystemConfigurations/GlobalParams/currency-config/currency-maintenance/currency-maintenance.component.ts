import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { DataStoreService } from 'src/@core/helpers/data-store.service';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';
import { CurrencyLookupComponent } from '../currency-lookup/currency-lookup.component';
import { CurrencyService } from '../currency.service';

@Component({
  selector: 'app-currency-maintenance',
  templateUrl: './currency-maintenance.component.html',
  styleUrls: ['./currency-maintenance.component.scss']
})
export class CurrencyMaintenanceComponent implements OnInit, OnDestroy {
  lookupData: any;
  currencyCode: any;
  showCode: boolean = false;
  existingData: boolean = false;
  currencyDescription: any;
  loading = false;
  submitted = false;
  functionArray: any;
  destroy$: Subject<boolean> = new Subject<boolean>();
  randomCode: any;
  function_type: any;
  results: any;
  submitting: boolean = false;
  constructor(
    public fb: FormBuilder,
    private router: Router,
    private dialog: MatDialog,
    private currencyAPI: CurrencyService,
    private dataStoreApi: DataStoreService,
    private notificationAPI: NotificationService
  ) {
    this.functionArray = this.dataStoreApi.getActionsByPrivilege("CONFIGURATIONS");
    this.functionArray = this.functionArray.filter(
      (arr: string) =>
        arr === 'ADD' ||
        arr === 'INQUIRE' ||
        arr === 'MODIFY' ||
        arr === 'VERIFY' ||
        arr === 'DELETE');
  }
  ngOnInit(): void {
    this.randomCode = "CUR" + Math.floor(Math.random() * (999-1));
  }
  ngOnDestroy(): void {
    this.destroy$.next(true);
    this.destroy$.complete();
  }

  formData = this.fb.group({
    function_type: ['', Validators.required],
    currencyCode: ['', Validators.required],
  });

  onSelectFunction(event: any) {
    this.function_type = event.target.value;
    if (event.target.value == 'ADD') {
      this.existingData = false;
      this.showCode = true;
      this.formData.controls.currencyCode.setValue(this.randomCode);
    } else if (event.target.value !== 'ADD') {
      this.existingData = true;
      this.showCode = true;
      this.formData.controls.currencyCode.setValue("");
    }
  }
  get f() { return this.formData.controls; }
  onSubmit() {
    this.loading = true;
    this.submitted = true;
    this.submitting = true;
    if (this.formData.valid) {
      this.currencyCode = this.formData.controls.currencyCode.value;
      if (this.function_type == 'ADD') {
        this.currencyAPI.currencyCode(this.currencyCode).pipe(takeUntil(this.destroy$)).subscribe(
          (data) => {
            if (data.statusCode === 404) {
              this.loading = false;
              this.router.navigate([`/system/configurations/global/currency/data/view`], { skipLocationChange: true, queryParams: { formData: this.formData.value, fetchData: this.lookupData } });
            } else if (
              data.statusCode === 200) {
              this.loading = false;
              this.results = data;
              this.notificationAPI.alertWarning(this.results.message);
            }
          },
          (err) => {
            this.loading = false;
            this.notificationAPI.alertWarning("Server Error: !!");
          }
        )
      } else if (this.function_type !== 'ADD') {
        this.router.navigate([`/system/configurations/global/currency/data/view`], { skipLocationChange: true, queryParams: { formData: this.formData.value, fetchData: this.lookupData } });
      }
    }
    else if (this.formData.controls.function_type.value == "") {
      this.loading = false;
      this.submitted = true;
      this.submitting = false;
      this.notificationAPI.alertWarning("CURRENCY FORM FUNCTION IS INVALID");
    }
    else if (this.formData.controls.currencyCode.value == "") {
      this.loading = false;
      this.submitted = true;
      this.submitting = false;
      this.notificationAPI.alertWarning("CURRENCY FORM CODE IS INVALID");
    } else {
      this.loading = false;
      this.submitted = true;
      this.submitting = false;
      this.notificationAPI.alertWarning("CURRENCY FORM DATA IS INVALID");
    }
  }
  currencyesCodeLookup(): void {
    const dialogRef = this.dialog.open(CurrencyLookupComponent, {
      width: '35%',
      autoFocus: false,
      maxHeight: '90vh'
    });
    dialogRef.afterClosed().subscribe(result => {
      this.lookupData = result.data;
      this.currencyCode = this.lookupData.currencyCode;
      this.currencyDescription = this.lookupData.ccy_name;
      this.formData.controls.currencyCode.setValue(this.currencyCode);
    });
  }
}
