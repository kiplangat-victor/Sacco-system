import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { DataStoreService } from 'src/@core/helpers/data-store.service';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';
import { MainClassificationLookupComponent } from '../main-classification-lookup/main-classification-lookup.component';
import { MainClassificationService } from '../main-classification.service';
@Component({
  selector: 'app-main-classification-maintenance',
  templateUrl: './main-classification-maintenance.component.html',
  styleUrls: ['./main-classification-maintenance.component.scss']
})
export class MainClassificationMaintenanceComponent implements OnInit, OnDestroy {
  lookupData: any;
  existingData: boolean = false;
  loading = false;
  submitted = false;
  functionArray: any;
  error: any;
  function_type: any;
  results: any;
  randomCode: any;
  showCode: boolean = false;
  assetClassificationCode: any;
  assetClassificationName: any;
  destroy$: Subject<boolean> = new Subject<boolean>();
  constructor(
    public fb: FormBuilder,
    private router: Router,
    private dialog: MatDialog,
    private dataStoreApi: DataStoreService,
    private notificationAPI: NotificationService,
    private mainAssetsAPI:MainClassificationService
  ) {
    this.functionArray = this.dataStoreApi.getActionsByPrivilege("CONFIGURATIONS");
    this.functionArray = this.functionArray.filter(
      (arr: string) => arr === 'ADD' ||
        arr === 'INQUIRE' ||
        arr === 'MODIFY' ||
        arr === 'VERIFY' ||
        arr === 'DELETE');
  }
  formData = this.fb.group({
    function_type: ['', [Validators.required]],
    assetClassificationCode: ['', [Validators.required]],
  });
  ngOnInit(): void {
    this.lookupData = {};
    this.randomCode = "CSF" + Math.floor(Math.random() * (999));
  }
  ngOnDestroy(): void {
    this.destroy$.next(true);
    this.destroy$.complete();
  }
 onSelectFunction(event: any) {
    this.function_type = event.target.value;
    if (this.function_type == 'ADD') {
      this.existingData = false;
      this.showCode = true;
      this.formData.controls.assetClassificationCode.setValue(this.randomCode);
    } else if (this.function_type !== 'ADD') {
      this.existingData = true;
      this.showCode = true;
      this.formData.controls.assetClassificationCode.setValue("");
    }
  }
  get f() { return this.formData.controls; }

  onSubmit() {
    if (this.formData.valid) {
      this.loading = true;
      this.assetClassificationCode = this.formData.controls.assetClassificationCode.value;
      if (this.function_type == 'ADD') {
        this.mainAssetsAPI.findByCode(this.assetClassificationCode).pipe(takeUntil(this.destroy$)).subscribe(
          (data) => {
            if (data.statusCode === 404) {
              this.loading = false;
              this.router.navigate([`/system/configurations/global/main-classification/data/view`], { skipLocationChange: true, queryParams: { formData: this.formData.value, fetchData: this.lookupData } });
            } else if (
              data.statusCode === 200) {
              this.loading = false;
              this.results = data;
              this.notificationAPI.alertWarning(this.results.message);
            }
          },
          (err) => {
            this.loading = false;
            this.error = err;
            this.notificationAPI.alertWarning(this.error);
          }
        )
      } else if (this.function_type !== 'ADD') {
        this.router.navigate([`/system/configurations/global/main-classification/data/view`], { skipLocationChange: true, queryParams: { formData: this.formData.value, fetchData: this.lookupData } });
      }
    }
    else if (this.formData.controls.function_type.value == "") {
      this.loading = false;
      this.submitted = true;
      this.notificationAPI.alertWarning("CLASSIFICATION FORM FUNCTION TYPE IS INVALID");
    }
    else if (this.formData.controls.assetClassificationCode.value == "") {
      this.loading = false;
      this.submitted = true;
      this.notificationAPI.alertWarning("CLASSIFICATION FORM CODE IS INVALID");
    } else {
      this.loading = false;
      this.submitted = true;
      this.notificationAPI.alertWarning("CHOOSE FORM FUNCTION");
    }
  }

  mainAssetsCodeLookup(): void {
    const dialogRef = this.dialog.open(MainClassificationLookupComponent, {
      width: "40%",
      autoFocus: false,
      maxHeight: '90vh'
    });
    dialogRef.afterClosed().subscribe(result => {
      this.lookupData = result.data;
      this.assetClassificationCode = this.lookupData.assetClassificationCode;
      this.assetClassificationName = this.lookupData.assetClassificationName;
      this.formData.controls.assetClassificationCode.setValue(this.assetClassificationCode);
    });
  }
}
