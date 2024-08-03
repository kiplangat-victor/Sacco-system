import { Subscription } from 'rxjs';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { DataStoreService } from 'src/@core/helpers/data-store.service';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';
import { EmployeeConfigLookupComponent } from '../employee-config-lookup/employee-config-lookup.component';
import { EmployeeConfigService } from '../employee-config.service';

@Component({
  selector: 'app-employee-config-maintenance',
  templateUrl: './employee-config-maintenance.component.html',
  styleUrls: ['./employee-config-maintenance.component.scss']
})
export class EmployeeConfigMaintenanceComponent implements OnInit, OnDestroy {
  lookupdata: any;
  loading = false;
  submitted = false;
  onShowSearchIcon = false;
  functionArray: any;
  destroy$: Subject<boolean> = new Subject<boolean>();
  function_type: any;
  randomCode: any;
  onsShowCode: boolean = false;
  employerCode: string;
  employerDescription: any;
  name: any;
  submitting: boolean = false;
  constructor(
    public fb: FormBuilder,
    private router: Router,
    private dialog: MatDialog,
    private dataStoreApi: DataStoreService,
    private employeeAPI: EmployeeConfigService,
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
  formData = this.fb.group({
    function_type: ['', Validators.required],
    employerCode: ['', Validators.required],
  });
  ngOnInit() {
    this.randomCode = "EMP" + Math.floor(Math.random() * (999));
  }
  ngOnDestroy() {
    this.destroy$.next(true);
    this.destroy$.complete();
  }
  onSelectFunction(event: any) {
    this.function_type = event.target.value;
    if (this.function_type == 'ADD') {
      this.onShowSearchIcon = false;
      this.onsShowCode = true;
      this.formData.controls.employerCode.setValue(this.randomCode);
    } else if (this.function_type !== 'ADD') {
      this.onShowSearchIcon = true;
      this.onsShowCode = true;
      this.formData.controls.employerCode.setValue("");
    }
  }
  get f() { return this.formData.controls; }

  onSubmit() {
    this.loading = true;
    this.submitting = true;
    if (this.formData.valid) {
      this.employerCode = this.formData.controls.employerCode.value;
      if (this.function_type == 'ADD') {
        this.employeeAPI.findBYCode(this.employerCode).pipe(takeUntil(this.destroy$)).subscribe(
          {
            next: (
              (res) => {
                if (res.statusCode === 404) {
                  this.loading = false;
                  this.router.navigate([`/system/configurations-employee/data/view`], { skipLocationChange: true, queryParams: { formData: this.formData.value } });
                } else if (
                  res.statusCode === 200) {
                  this.loading = false;
                  this.submitting = false;
                  this.notificationAPI.alertWarning(res.message);
                }
              }
            ),
            error: (
              (err) => {
                this.loading = false;
                this.submitting = false;
                this.notificationAPI.alertWarning("Server Error: !!");
              }
            ),
            complete: (
              () => {

              }
            )
          }
        ), Subscription;
      } else if (this.function_type !== 'ADD') {
        this.router.navigate([`/system/configurations-employee/data/view`], { skipLocationChange: true, queryParams: { formData: this.formData.value } });
      }
    }
    else if (this.formData.controls.function_type.value == "") {
      this.loading = false;
      this.submitted = true;
      this.submitting = false;
      this.notificationAPI.alertWarning("Employer Form function Type is invalid");
    }
    else if (this.formData.controls.employerCode.value == "") {
      this.loading = false;
      this.submitted = true;
      this.submitting = false;
      this.notificationAPI.alertWarning("Employer form code is invalid");
    } else {
      this.loading = false;
      this.submitted = true;
      this.submitting = false;
      this.notificationAPI.alertWarning("Choose form function");
    }
  }

  employerCodeLookup(): void {
    const dialogRef = this.dialog.open(EmployeeConfigLookupComponent, {
      width: '45%',
      autoFocus: false,
      maxHeight: '90vh'
    });
    dialogRef.afterClosed().subscribe(result => {
      this.lookupdata = result.data;
      this.formData.controls.employerCode.setValue(this.lookupdata.employerCode);
      this.name = this.lookupdata.name;
    });
  }
}
