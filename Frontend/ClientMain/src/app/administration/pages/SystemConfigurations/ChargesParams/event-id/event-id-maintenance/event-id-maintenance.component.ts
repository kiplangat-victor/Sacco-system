import { Component,OnDestroy,OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { Router} from '@angular/router';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { DataStoreService } from 'src/@core/helpers/data-store.service';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';
import { EventIdLookupComponent } from '../event-id-lookup/event-id-lookup.component';
import { EventIdService } from '../event-id.service';

@Component({
  selector: 'app-event-id-maintenance',
  templateUrl: './event-id-maintenance.component.html',
  styleUrls: ['./event-id-maintenance.component.scss']
})
export class EventIdMaintenanceComponent implements OnInit, OnDestroy {
  lookupData: any;
  eventIdCode: any;
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
  constructor(
    public fb: FormBuilder,
    private router: Router,
    private dialog: MatDialog,
    private eventIDAPI: EventIdService,
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
    eventIdCode: ['', [Validators.required]],
  });
  ngOnInit(): void {
    this.lookupData = {};
    this.randomCode = "EID" + Math.floor(Math.random() * (100));
  }
  ngOnDestroy(): void {
    this.destroy$.next(true);
    this.destroy$.complete();
  }
 onSelectFunction(event: any) {
    this.function_type = event.target.value;
    if (this.function_type == 'ADD') {
      this.onShowSearchIcon = false;
      this.onsShowCode = true;
      this.formData.controls.eventIdCode.setValue(this.randomCode);
    } else if (this.function_type !== 'ADD') {
      this.onShowSearchIcon = true;
      this.onsShowCode = true;
      this.formData.controls.eventIdCode.setValue("");
    }
  }
  get f() { return this.formData.controls; }

  onSubmit() {
    if (this.formData.valid) {
      console.log("Got called!");

      this.loading = true;
      this.eventIdCode = this.formData.controls.eventIdCode.value;
      if (this.function_type == 'ADD') {
        this.eventIDAPI.findByCode(this.eventIdCode).pipe(takeUntil(this.destroy$)).subscribe(
          (data) => {
            console.log("Data Resp", data);



            if (data.statusCode === 404) {
              this.loading = false;
              this.router.navigate([`/system/configurations/charge/event-id/data/view`], { skipLocationChange: true, queryParams: { formData: this.formData.value, fetchData: this.lookupData } });
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
        this.router.navigate([`/system/configurations/charge/event-id/data/view`], { skipLocationChange: true, queryParams: { formData: this.formData.value, fetchData: this.lookupData } });
      }
    }
    else if (this.formData.controls.function_type.value == "") {
      this.loading = false;
      this.submitted = true;
      this.notificationAPI.alertWarning("EVENT ID FORM FUNCTION TYPE IS INVALID");
    }
    else if (this.formData.controls.eventIdCode.value == "") {
      this.loading = false;
      this.submitted = true;
      this.notificationAPI.alertWarning("EVENT ID FORM CODE IS INVALID");
    } else {
      this.loading = false;
      this.submitted = true;
      this.notificationAPI.alertWarning("CHOOSE FORM FUNCTION");
    }
  }

  eventIdCodeLookup(): void {
    const dialogRef = this.dialog.open(EventIdLookupComponent, {
      width: '40%'
    });
    dialogRef.afterClosed().subscribe(result => {
      this.lookupData = result.data;
      this.eventIdCode = this.lookupData.eventIdCode;
      this.eventIdDescription = this.lookupData.event_id_desc;
      this.formData.controls.eventIdCode.setValue(this.eventIdCode);
    });
  }
}
