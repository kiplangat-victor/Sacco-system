import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import {
  MatDialog
} from '@angular/material/dialog';
import { Router } from '@angular/router';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { DataStoreService } from 'src/@core/helpers/data-store.service';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';
import { EventTypeLookupComponent } from '../event-type-lookup/event-type-lookup.component';
import { EventTypeService } from '../event-type.service';

@Component({
  selector: 'app-event-type-maintenance',
  templateUrl: './event-type-maintenance.component.html',
  styleUrls: ['./event-type-maintenance.component.scss'],
})
export class EventTypeMaintenanceComponent implements OnInit, OnDestroy {
  lookupData: any;
  eventTypeCode: any;
  branchDescription: any;
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
  eventIdDescription: any;
  eventTypeName: any;
  constructor(
    public fb: FormBuilder,
    private router: Router,
    private dialog: MatDialog,
    private eventTpypeAPI: EventTypeService,
    private dataStoreApi: DataStoreService,
    private notificationAPI: NotificationService
  ) {
    this.functionArray = this.dataStoreApi.getActionsByPrivilege("CHARGE PARAMS");
    this.functionArray = this.functionArray.filter(
      (arr: string) => arr === 'ADD' ||
        arr === 'INQUIRE' ||
        arr === 'MODIFY' ||
        arr === 'VERIFY' ||
        arr === 'DELETE');
  }
  formData = this.fb.group({
    function_type: ['', [Validators.required]],
    eventTypeCode: ['', [Validators.required]],
  });
  ngOnInit() {
    this.lookupData = {};
    this.randomCode = "EPE" + Math.floor(Math.random() * (100));
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
      this.formData.controls.eventTypeCode.setValue(this.randomCode);
    } else if (this.function_type !== 'ADD') {
      this.onShowSearchIcon = true;
      this.onsShowCode = true;
      this.formData.controls.eventTypeCode.setValue("");
    }
  }
  get f() { return this.formData.controls; }

  onSubmit() {

    console.log("Got called");
    if (this.formData.valid) {
      console.log("Data is valid");

      this.loading = true;
      this.eventTypeCode = this.formData.controls.eventTypeCode.value;
      if (this.function_type == 'ADD') {
        this.eventTpypeAPI.findByCode(this.eventTypeCode).pipe(takeUntil(this.destroy$)).subscribe(
          (data) => {
            console.log("Data is", data);

            if (data.statusCode === 404) {
              this.loading = false;
              this.router.navigate([`/system/configurations/charge/event-type/data/view`], { skipLocationChange: true, queryParams: { formData: this.formData.value, fetchData: this.lookupData } });
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
        this.router.navigate([`/system/configurations/charge/event-type/data/view`], { skipLocationChange: true, queryParams: { formData: this.formData.value, fetchData: this.lookupData } });
      }
    }
    else if (this.formData.controls.function_type.value == "") {
      this.loading = false;
      this.submitted = true;
      this.notificationAPI.alertWarning("EVENT TYPE FORM FUNCTION TYPE IS INVALID");
    }
    else if (this.formData.controls.eventTypeCode.value == "") {
      this.loading = false;
      this.submitted = true;
      this.notificationAPI.alertWarning("EVENT TYPE FORM CODE IS INVALID");
    } else {
      this.loading = false;
      this.submitted = true;
      this.notificationAPI.alertWarning("CHOOSE FORM FUNCTION");
    }
  }

  eventTypeCodeLookup(): void {
    const dialogRef = this.dialog.open(EventTypeLookupComponent, {

    });
    dialogRef.afterClosed().subscribe(result => {
      this.lookupData = result.data;
      this.eventTypeCode = this.lookupData.eventTypeCode;
      this.eventTypeName = this.lookupData.eventTypeName;
      this.formData.controls.eventTypeCode.setValue(this.eventTypeCode);
    });
  }
}
