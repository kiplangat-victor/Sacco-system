import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { DataStoreService } from 'src/@core/helpers/data-store.service';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';
import { ExceptionsCodesLookupComponent } from '../exceptions-codes-lookup/exceptions-codes-lookup.component';
import { ExceptionsCodesServiceService } from '../exceptions-codes-service.service';
@Component({
  selector: 'app-exceptions-codes-maintenance',
  templateUrl: './exceptions-codes-maintenance.component.html',
  styleUrls: ['./exceptions-codes-maintenance.component.scss']
})
export class ExceptionsCodesMaintenanceComponent implements OnInit, OnDestroy {
  lookupData: any;
  exceptionCode: any;
  showCode: boolean = false;
  existingData: boolean = false;
  currencyDescription: any;
  loading = false;
  submitted = false;
  functionArray: any;
  exce_description: any;
  randomCode: any;
  function_type: any;
  error: any;
  results: any;
  submitting: boolean = false;
  destroy$: Subject<boolean> = new Subject<boolean>();
  constructor(
    public fb: FormBuilder,
    private router: Router,
    private dialog: MatDialog,
    private dataStoreApi: DataStoreService,
    private notificationAPI: NotificationService,
    private exeptionAPI: ExceptionsCodesServiceService
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
    this.lookupData = {};
    this.randomCode = "EXE" + Math.floor(Math.random() * (999));
  }
  ngOnDestroy(): void {
    this.destroy$.next(true);
    this.destroy$.complete();
  }

  formData = this.fb.group({
    function_type: ['', Validators.required],
    exceptionCode: ['', Validators.required],
  });

  onSelectFunction(event: any) {
    this.function_type = event.target.value;
    if (event.target.value == 'ADD') {
      this.existingData = false;
      this.showCode = true;
      this.formData.controls.exceptionCode.setValue(this.randomCode);
    } else if (event.target.value !== 'ADD') {
      this.existingData = true;
      this.showCode = true;
      this.formData.controls.exceptionCode.setValue("");
    }
  }
  get f() { return this.formData.controls; }
  onSubmit() {
    this.loading = true;
    this.submitting = true;
    if (this.formData.valid) {
      this.exceptionCode = this.formData.controls.exceptionCode.value;
      if (this.function_type == 'ADD') {
        this.exeptionAPI.findByCode(this.exceptionCode).pipe(takeUntil(this.destroy$)).subscribe(
          (data) => {
            if (data.statusCode === 404) {
              this.loading = false;
              this.router.navigate([`/system/configurations/global/exceptions-codes/data/view`], { skipLocationChange: true, queryParams: { formData: this.formData.value, fetchData: this.lookupData } });
            } else if (
              data.statusCode === 200) {
              this.loading = false;
              this.results = data;
              this.submitting = false;
              this.notificationAPI.alertWarning(this.results.message);
            }
          },
          (err) => {
            this.loading = false;
            this.error = err;
            this.submitting = false;
            this.notificationAPI.alertWarning("Server Error: !!");
          }
        )
      } else if (this.function_type !== 'ADD') {
        this.router.navigate([`/system/configurations/global/exceptions-codes/data/view`], { skipLocationChange: true, queryParams: { formData: this.formData.value, fetchData: this.lookupData } });
      }
    }
    else if (this.formData.controls.function_type.value == "") {
      this.loading = false;
      this.submitted = true;
      this.submitting = false;
      this.notificationAPI.alertWarning("CURRENCY FORM FUNCTION IS INVALID");
    }
    else if (this.formData.controls.exceptionCode.value == "") {
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
  exceptionCodeLookup(): void {
    const dialogRef = this.dialog.open(ExceptionsCodesLookupComponent, {
      width: '35%',
      autoFocus: false,
      maxHeight: '90vh'
    });
    dialogRef.afterClosed().subscribe(result => {
      this.lookupData = result.data;
      this.exceptionCode = this.lookupData.exceptionCode;
      this.exce_description = this.lookupData.exce_description;
      this.formData.controls.exceptionCode.setValue(this.exceptionCode);
    });
  }
}
