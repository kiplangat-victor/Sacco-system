import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';
import { DataStoreService } from 'src/@core/helpers/data-store.service';
import { MisSectorLookupComponent } from '../mis-sector-lookup/mis-sector-lookup.component';
import { MisSectorService } from '../mis-sector.service';

@Component({
  selector: 'app-mis-sector-maintenance',
  templateUrl: './mis-sector-maintenance.component.html',
  styleUrls: ['./mis-sector-maintenance.component.scss']
})
export class MisSectorMaintenanceComponent implements OnInit, OnDestroy {
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
  destroy$: Subject<boolean> = new Subject<boolean>();
  misCode: any;
  misSector: any;
  proceed: boolean = false;
  constructor(
    public fb: FormBuilder,
    private router: Router,
    private dialog: MatDialog,
    private misSectorAPI: MisSectorService,
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
    function_type: ['', Validators.required],
    misCode: ['', Validators.required],
  });
  ngOnDestroy(): void {
    this.destroy$.next(true);
    this.destroy$.complete();
  }
  ngOnInit(): void {
    this.randomCode = "MIS" + Math.floor(Math.random() * (999));
  }
  onSelectFunction(event: any) {
    this.function_type = event.target.value;
    if (this.function_type == 'ADD') {
      this.existingData = false;
      this.showCode = true;
      this.formData.controls.misCode.setValue(this.randomCode);
    } else if (this.function_type !== 'ADD') {
      this.existingData = true;
      this.showCode = true;
      this.formData.controls.misCode.setValue("");
    }
  }
  get f() { return this.formData.controls; }

  onSubmit() {
    this.loading = true;
    this.submitted = true;
    this.proceed = true;
    if (this.formData.valid) {
      this.misCode = this.formData.controls.misCode.value;
      if (this.function_type == 'ADD') {
        this.misSectorAPI.findByCode(this.misCode).pipe(takeUntil(this.destroy$)).subscribe(
          (data) => {
            if (data.statusCode === 404) {
              this.loading = false;
              this.router.navigate([`/system/configurations/global/mis-sector/data/view`], { skipLocationChange: true, queryParams: { formData: this.formData.value, fetchData: this.lookupData } });
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
            this.notificationAPI.alertWarning("Server Errpr: !!!");
          }
        )
      } else if (this.function_type !== 'ADD') {
        this.router.navigate([`/system/configurations/global/mis-sector/data/view`], { skipLocationChange: true, queryParams: { formData: this.formData.value, fetchData: this.lookupData } });
      }
    }
    else if (this.formData.controls.function_type.value == "") {
      this.loading = false;
      this.submitted = true;
      this.proceed = false;
      this.notificationAPI.alertWarning("MIS SECTOR FORM FUNCTION TYPE IS INVALID");
    }
    else if (this.formData.controls.misCode.value == "") {
      this.loading = false;
      this.submitted = true;
      this.proceed = false;
      this.notificationAPI.alertWarning("MIS SECTOR FORM CODE IS INVALID");
    } else {
      this.loading = false;
      this.submitted = true;
      this.proceed = false;
      this.notificationAPI.alertWarning("CHOOSE FORM FUNCTION");
    }
  }
  misCodeLookup(): void {
    const dialogRef = this.dialog.open(MisSectorLookupComponent, {
      width: "40%",
      autoFocus: false,
      maxHeight: '90vh'
    });
    dialogRef.afterClosed().subscribe(result => {
      this.lookupData = result.data;
      this.misCode = this.lookupData.misCode;
      this.misSector = this.lookupData.misSector;
      this.formData.controls.misCode.setValue(this.misCode);
    });
  }
}
