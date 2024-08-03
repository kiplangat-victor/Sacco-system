import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { DataStoreService } from 'src/@core/helpers/data-store.service';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';
import { SegmentLookupComponent } from '../segment-lookup/segment-lookup.component';
import { SegmentsService } from '../segments.service';

@Component({
  selector: 'app-segment-maintenance',
  templateUrl: './segment-maintenance.component.html',
  styleUrls: ['./segment-maintenance.component.scss']
})
export class SegmentMaintenanceComponent implements OnInit, OnDestroy {
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
  segmentCode: string;
  segmentName: any;
  constructor(
    public fb: FormBuilder,
    private router: Router,
    private dialog: MatDialog,
    private segmentAPI: SegmentsService,
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
    segmentCode: ['', Validators.required],
  });
  ngOnDestroy(): void {
    this.destroy$.next(true);
    this.destroy$.complete();
  }
  ngOnInit(): void {
    this.lookupData = {};
    this.randomCode = "SEG" + Math.floor(Math.random() * (100));
  }
  onSelectFunction(event: any) {
    this.function_type = event.target.value;
    if (this.function_type == 'ADD') {
      this.existingData = false;
      this.showCode = true;
      this.formData.controls.segmentCode.setValue(this.randomCode);
    } else if (this.function_type !== 'ADD') {
      this.existingData = true;
      this.showCode = true;
      this.formData.controls.segmentCode.setValue("");
    }
  }
  get f() { return this.formData.controls; }

  onSubmit() {
    if (this.formData.valid) {
      this.loading = true;
      this.segmentCode = this.formData.controls.segmentCode.value;
      if (this.function_type == 'ADD') {
        this.segmentAPI.getSegmentByCode(this.segmentCode).pipe(takeUntil(this.destroy$)).subscribe(
          (data) => {
            if (data.statusCode === 404) {
              this.loading = false;
              this.router.navigate([`/system/configurations/global/segment/data-view`], { skipLocationChange: true, queryParams: { formData: this.formData.value, fetchData: this.lookupData } });
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
        this.router.navigate([`/system/configurations/global/segment/data-view`], { skipLocationChange: true, queryParams: { formData: this.formData.value, fetchData: this.lookupData } });
      }
    }
    else if (this.formData.controls.function_type.value == "") {
      this.loading = false;
      this.submitted = true;
      this.notificationAPI.alertWarning("SEGMENT FORM FUNCTION TYPE IS INVALID");
    }
    else if (this.formData.controls.segmentCode.value == "") {
      this.loading = false;
      this.submitted = true;
      this.notificationAPI.alertWarning("SEGMENT FORM CODE IS INVALID");
    } else {
      this.loading = false;
      this.submitted = true;
      this.notificationAPI.alertWarning("CHOOSE FORM FUNCTION");
    }
  }
  segmentLookup(): void {
    const dialogRef = this.dialog.open(SegmentLookupComponent, {
      width: "40%",
      autoFocus: false,
      maxHeight: '90vh'
    });
    dialogRef.afterClosed().subscribe(result => {
      this.lookupData = result.data;
      this.segmentCode = this.lookupData.segmentCode;
      this.segmentName = this.lookupData.segmentName;
      this.formData.controls.segmentCode.setValue(this.segmentCode);
    });
  }
}
