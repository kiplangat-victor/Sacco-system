import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';
import { DataStoreService } from 'src/@core/helpers/data-store.service';
import { WelfareLookUpComponent } from '../welfare-look-up/welfare-look-up.component';
import { WelfareService } from '../welfare.service';

@Component({
  selector: 'app-welfare-maintenance',
  templateUrl: './welfare-maintenance.component.html',
  styleUrls: ['./welfare-maintenance.component.scss']
})
export class WelfareMaintenanceComponent implements OnInit, OnDestroy {
  lookupData: any;
  welfareCode: any;
  welfareName: any;
  loading = false;
  submitted = false;
  onShowSearchIcon = false;
  functionArray: any;
  error: any;
  destroy$: Subject<boolean> = new Subject<boolean>();
  function_type: any;
  results: any;
  randomCode: any;
  onsShowCode: boolean = false;
  submitting: boolean = false;
  showWelfareName: boolean = false;
  constructor(
    public fb: FormBuilder,
    private router: Router,
    private dialog: MatDialog,
    private welfareAPI: WelfareService,
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
  formData = this.fb.group({
    function_type: ['', [Validators.required]],
    welfareCode: ['', [Validators.required]],
    welfareName: ["", [Validators.required]]
  });
  ngOnInit() {
    this.lookupData = {};
    this.randomCode = "WFE" + Math.floor(Math.random() * (999 - 1));
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
      this.showWelfareName=true;
      this.formData.controls.welfareCode.setValue(this.randomCode);
      this.formData.controls.welfareName.setValue("Welfare Name / Description");
    } else if (this.function_type !== 'ADD') {
      this.onShowSearchIcon = true;
      this.onsShowCode = true;
      this.showWelfareName = true;
    }
  }
  get f() { return this.formData.controls; }

  onSubmit() {
    this.loading = true;
    this.submitted = true;
    this.submitting = true;
    if (this.formData.valid) {
      this.welfareCode = this.formData.controls.welfareCode.value;
      if (this.function_type == 'ADD') {
        this.welfareAPI.welfareCode(this.welfareCode).pipe(takeUntil(this.destroy$)).subscribe(
          (data) => {
            if (data.statusCode === 404) {
              this.loading = false;
              this.router.navigate([`/system/configurations/welfare/data-view`], { skipLocationChange: true, queryParams: { formData: this.formData.value, fetchData: this.lookupData } });
            } else if (
              data.statusCode === 200) {
              this.loading = false;
              this.submitting = false;
              this.results = data;
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
        this.router.navigate([`/system/configurations/welfare/data-view`], { skipLocationChange: true, queryParams: { formData: this.formData.value, fetchData: this.lookupData } });
      }
    }
    else if (this.formData.controls.function_type.value == "") {
      this.loading = false;
      this.submitted = true;
      this.submitting = false;
      this.notificationAPI.alertWarning("Welfare Form Function is invalid");
    }
    else if (this.formData.controls.welfareCode.value == "") {
      this.loading = false;
      this.submitted = true;
      this.submitting = false;
      this.notificationAPI.alertWarning("Welfare Code is invalid");
    }
    else if (this.formData.controls.welfareName.value == "") {
      this.loading = false;
      this.submitted = true;
      this.submitting = false;
      this.notificationAPI.alertWarning("Welfare Name is Required");
    } else {
      this.loading = false;
      this.submitted = true;
      this.submitting = false;
      this.notificationAPI.alertWarning("Choose Form Function");
    }
  }

  welfareCodeLookup(): void {
    const dialogRef = this.dialog.open(WelfareLookUpComponent, {
      width: "40%",
      autoFocus: false,
      maxHeight: '90vh'
    });
    dialogRef.afterClosed().subscribe(result => {
      this.lookupData = result.data;
      this.welfareName = this.lookupData.welfareName;
      this.formData.controls.welfareCode.setValue(this.lookupData.welfareCode);
      this.formData.controls.welfareName.setValue(this.lookupData.welfareName);
    });
  }
}
