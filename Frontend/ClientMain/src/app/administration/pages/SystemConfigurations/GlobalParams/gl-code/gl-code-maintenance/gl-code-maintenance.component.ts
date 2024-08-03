import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { DataStoreService } from 'src/@core/helpers/data-store.service';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';
import { GlCodeLookupComponent } from '../gl-code-lookup/gl-code-lookup.component';
import { GlCodeService } from '../gl-code.service';

@Component({
  selector: 'app-gl-code-maintenance',
  templateUrl: './gl-code-maintenance.component.html',
  styleUrls: ['./gl-code-maintenance.component.scss']
})
export class GlCodeMaintenanceComponent implements OnInit, OnDestroy {
  lookupData: any;
  glCode: any;
  existingData: boolean;
  glDescription: any;
  functionArray: any;
  showCode: boolean = false;
  randomCode: any;
  loading = false;
  submitted = false;
  results: any;
  error: any;
  function_type: any;
  proceed: boolean = false;
  destroy$: Subject<boolean> = new Subject<boolean>();
  constructor(
    public fb: FormBuilder,
    private router: Router,
    private dialog: MatDialog,
    private glsAPI: GlCodeService,
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
  ngOnDestroy(): void {
    this.destroy$.next(true);
    this.destroy$.complete();
  }
  ngOnInit(): void {
    this.lookupData = {};
    this.randomCode = "GL" + Math.floor(Math.random() * (999 - 100));
  }
  formData = this.fb.group({
    function_type: ['', [Validators.required]],
    glCode: ['', [Validators.required]],
  });
  onSelectFunction(event: any) {
    this.function_type = event.target.value;
    if (this.function_type  == 'ADD') {
      this.existingData = false;
      this.showCode = true;
      this.formData.controls.glCode.setValue(this.randomCode);
    } else if (this.function_type  !== 'ADD') {
      this.existingData = true;
      this.showCode = true;
      this.formData.controls.glCode.setValue("");
    }
  }
  get f() { return this.formData.controls; }

  onSubmit() {
    this.loading = true;
    this.submitted = true;
    this.proceed = true;
    if (this.formData.valid) {
      this.glCode = this.formData.controls.glCode.value;
      if (this.function_type == 'ADD') {
        this.glsAPI.findCode(this.glCode).pipe(takeUntil(this.destroy$)).subscribe(
          (data) => {
            if (data.statusCode === 404) {
              this.loading = false;
              this.router.navigate(['system/configurations/global/gl-code'], { skipLocationChange: true, queryParams: { formData: this.formData.value, fetchData: this.lookupData } });
            } else if (
              data.statusCode === 200) {
              this.loading = false;
              this.results = data;
              this.proceed = false;
              this.notificationAPI.alertWarning(this.results.message);
            }
          },
          (err) => {
            this.loading = false;
            this.error = err;
            this.proceed = false;
            this.notificationAPI.alertWarning("Server Error: !!");
          }
        )
      } else if (this.function_type !== 'ADD') {
        this.router.navigate(['system/configurations/global/gl-code'], { skipLocationChange: true, queryParams: { formData: this.formData.value, fetchData: this.lookupData } });

      }
    }
    else if (this.formData.controls.function_type.value == "") {
      this.loading = false;
      this.submitted = true;
      this.proceed = false;
      this.notificationAPI.alertWarning("GL FORM FUNCTION TYPE IS INVALID");
    }
    else if (this.formData.controls.glCode.value == "") {
      this.loading = false;
      this.submitted = true;
      this.proceed = false;
      this.notificationAPI.alertWarning("GL FORM CODE IS INVALID");
    } else {
      this.loading = false;
      this.submitted = true;
      this.proceed = false;
      this.notificationAPI.alertWarning("GL FORM FUNCTION INVALID");
    }
  }

  glCodeLookup(): void {
    const dialogRef = this.dialog.open(GlCodeLookupComponent, {
      autoFocus: false,
      maxHeight: '90vh'
    });
    dialogRef.afterClosed().subscribe(result => {
      this.lookupData = result.data;
      this.glCode = this.lookupData.glCode;
      this.glDescription = this.lookupData.glDescription;
      this.formData.controls.glCode.setValue(this.glCode);
    });
  }
}
