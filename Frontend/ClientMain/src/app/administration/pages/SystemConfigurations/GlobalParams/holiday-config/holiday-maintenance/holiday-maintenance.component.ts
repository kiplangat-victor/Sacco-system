import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { DataStoreService } from 'src/@core/helpers/data-store.service';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';
import { HolidayLookupComponent } from '../holiday-lookup/holiday-lookup.component';
import { HolidayService } from '../holiday.service';

@Component({
  selector: 'app-holiday-maintenance',
  templateUrl: './holiday-maintenance.component.html',
  styleUrls: ['./holiday-maintenance.component.scss']
})
export class HolidayMaintenanceComponent implements OnInit, OnDestroy {
  lookupData: any;
  holidayCode: any;
  existingData: boolean = false;
  loading = false;
  submitted = false;
  functionArray: any;
  error: any;
  destroy$: Subject<boolean> = new Subject<boolean>();
  function_type: any;
  results: any;
  randomCode: any;
  showCode: boolean = false;
  holidayName: any;
  submitting: boolean = false;
  constructor(
    public fb: FormBuilder,
    private router: Router,
    private dialog: MatDialog,
    private holidayAPI: HolidayService,
    private dataStoreApi: DataStoreService,
    private notificationAPI: NotificationService
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
    holidayCode: ['', [Validators.required]],
  });
  ngOnInit(): void {
    this.lookupData = {};
    this.randomCode = "HOL" + Math.floor(Math.random() * (999 - 100));
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
      this.formData.controls.holidayCode.setValue(this.randomCode);
    } else if (this.function_type !== 'ADD') {
      this.existingData = true;
      this.showCode = true;
      this.formData.controls.holidayCode.setValue("");
    }
  }
  get f() { return this.formData.controls; }

  onSubmit() {
    this.loading = true;
    this.submitting = true;
    if (this.formData.valid) {
      this.holidayCode = this.formData.controls.holidayCode.value;
      if (this.function_type == 'ADD') {
        this.holidayAPI.findByCode(this.holidayCode).pipe(takeUntil(this.destroy$)).subscribe(
          (data) => {
            if (data.statusCode === 404) {
              this.loading = false;
              this.router.navigate([`/system/configurations/global/holiday/data/view`], { skipLocationChange: true, queryParams: { formData: this.formData.value, fetchData: this.lookupData } });
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
            this.submitting = false;
            this.notificationAPI.alertWarning("Server Error: !!");
          }
        )
      } else if (this.function_type !== 'ADD') {
        this.router.navigate([`/system/configurations/global/holiday/data/view`], { skipLocationChange: true, queryParams: { formData: this.formData.value, fetchData: this.lookupData } });
      }
    }
    else if (this.formData.controls.function_type.value == "") {
      this.loading = false;
      this.submitted = true;
      this.submitting = false;
      this.notificationAPI.alertWarning("HOLIDAY FORM FUNCTION TYPE IS INVALID");
    }
    else if (this.formData.controls.holidayCode.value == "") {
      this.loading = false;
      this.submitted = true;
      this.submitting = false;
      this.notificationAPI.alertWarning("HOLIDAY FORM CODE IS INVALID");
    } else {
      this.loading = false;
      this.submitted = true;
      this.submitting = false;
      this.notificationAPI.alertWarning("CHOOSE FORM FUNCTION");
    }
  }

  holidayCodeLookup(): void {
    const dialogRef = this.dialog.open(HolidayLookupComponent, {
      width: "40%",
      autoFocus: false,
      maxHeight: '90vh'
    });
    dialogRef.afterClosed().subscribe(result => {
      this.lookupData = result.data;
      this.holidayCode = this.lookupData.holidayCode;
      this.holidayName = this.lookupData.holidayName;
      this.formData.controls.holidayCode.setValue(this.holidayCode);
    });
  }
}
