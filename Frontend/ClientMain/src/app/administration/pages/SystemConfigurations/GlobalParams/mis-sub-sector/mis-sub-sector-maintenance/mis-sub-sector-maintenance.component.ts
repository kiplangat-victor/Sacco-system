import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { DataStoreService } from 'src/@core/helpers/data-store.service';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';
import { MisSubSectorLookupComponent } from '../mis-sub-sector-lookup/mis-sub-sector-lookup.component';
import { MisSubSectorService } from '../mis-sub-sector.service';

@Component({
  selector: 'app-mis-sub-sector-maintenance',
  templateUrl: './mis-sub-sector-maintenance.component.html',
  styleUrls: ['./mis-sub-sector-maintenance.component.scss']
})
export class MisSubSectorMaintenanceComponent implements OnInit, OnDestroy {
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
  misSubcode: any;
  misSubSector: any;
  constructor(
    public fb: FormBuilder,
    private router: Router,
    private dialog: MatDialog,
    private dataStoreApi: DataStoreService,
    private subSectorAPI: MisSubSectorService,
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
    misSubcode: ['', Validators.required],
  });
  ngOnDestroy(): void {
    this.destroy$.next(true);
    this.destroy$.complete();
  }
  ngOnInit(): void {
    this.lookupData = {};
    this.randomCode = "MSB" + Math.floor(Math.random() * (999));
  }
  onSelectFunction(event: any) {
    this.function_type = event.target.value;
    if (this.function_type == 'ADD') {
      this.existingData = false;
      this.showCode = true;
      this.formData.controls.misSubcode.setValue(this.randomCode);
    } else if (this.function_type !== 'ADD') {
      this.existingData = true;
      this.showCode = true;
      this.formData.controls.misSubcode.setValue("");
    }
  }
  get f() { return this.formData.controls; }

  onSubmit() {
    if (this.formData.valid) {
      this.loading = true;
      this.misSubcode = this.formData.controls.misSubcode.value;
      if (this.function_type == 'ADD') {
        this.subSectorAPI.getSubSectorByCode(this.misSubcode).pipe(takeUntil(this.destroy$)).subscribe(
          (data) => {
            if (data.statusCode === 404) {
              this.loading = false;
              this.router.navigate([`/system/configurations/global/mis-sub-sector/data/view`], { skipLocationChange: true, queryParams: { formData: this.formData.value, fetchData: this.lookupData } });
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
        this.router.navigate([`/system/configurations/global/mis-sub-sector/data/view`], { skipLocationChange: true, queryParams: { formData: this.formData.value, fetchData: this.lookupData } });
      }
    }
    else if (this.formData.controls.function_type.value == "") {
      this.loading = false;
      this.submitted = true;
      this.notificationAPI.alertWarning("MIS SUB SECTOR FORM FUNCTION TYPE IS INVALID");
    }
    else if (this.formData.controls.misSubcode.value == "") {
      this.loading = false;
      this.submitted = true;
      this.notificationAPI.alertWarning("MIS SUB SECTOR FORM CODE IS INVALID");
    } else {
      this.loading = false;
      this.submitted = true;
      this.notificationAPI.alertWarning("CHOOSE FORM FUNCTION");
    }
  }
  misSubSectorCodeLookup(): void {
    const dialogRef = this.dialog.open(MisSubSectorLookupComponent, {
      width: "40%",
      autoFocus: false,
      maxHeight: '90vh'
    });
    dialogRef.afterClosed().subscribe(result => {
      this.lookupData = result.data;
      this.misSubcode = this.lookupData.misSubcode;
      this.misSubSector = this.lookupData.misSubSector;
      this.formData.controls.misSubcode.setValue(this.misSubcode);
    });
  }
}
