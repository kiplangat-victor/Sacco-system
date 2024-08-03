import { Component, OnDestroy, OnInit } from "@angular/core";
import { FormBuilder, Validators } from "@angular/forms";
import { MatDialog } from "@angular/material/dialog";
import { Router } from "@angular/router";
import { Subject } from "rxjs";
import { takeUntil } from "rxjs/operators";
import { DataStoreService } from "src/@core/helpers/data-store.service";
import { NotificationService } from "src/@core/helpers/NotificationService/notification.service";
import { LinkedOrganizationLookupComponent } from "../linked-organization-lookup/linked-organization-lookup.component";
import { LinkedorganizationService } from "../linkedorganization.service";


@Component({
  selector: 'app-linked-organization-maintenance',
  templateUrl: './linked-organization-maintenance.component.html',
  styleUrls: ['./linked-organization-maintenance.component.scss']
})
export class LinkedOrganizationMaintenanceComponent implements OnInit, OnDestroy {
  lookupData: any;
  linkedOrganizationCode: any;
  organizationName: any;
  existingData: boolean = false; function_type: string;
  results: any;
  loading = false;
  submitted = false;
  functionArray: any;
  randomCode: any;
  showCode: boolean = false;
  destroy$: Subject<boolean> = new Subject<boolean>();
  submitting: boolean = false;
  constructor(
    public fb: FormBuilder,
    private router: Router,
    private dialog: MatDialog,
    private dataStoreApi: DataStoreService,
    private notificationAPI: NotificationService,
    private linkedOrganizationAPI: LinkedorganizationService
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
    this.randomCode = "ORG" + Math.floor(Math.random() * (999));
  }
  ngOnDestroy(): void {
    this.destroy$.next(true);
    this.destroy$.complete();
  }

  formData = this.fb.group({
    function_type: ['', [Validators.required]],
    linkedOrganizationCode: ['', [Validators.required]],
  });

  onSelectFunction(event: any) {
    this.function_type = event.target.value;
    if (this.function_type == 'ADD') {
      this.existingData = false;
      this.showCode = true;
      this.formData.controls.linkedOrganizationCode.setValue(this.randomCode);
    } else if (this.function_type !== 'ADD') {
      this.existingData = true;
      this.showCode = true;
      this.formData.controls.linkedOrganizationCode.setValue("");
    }
  }
  get f() { return this.formData.controls; }
  onSubmit() {
    this.loading = true;
    this.submitted = true;
    this.submitting = true;
    if (this.formData.valid) {
      this.linkedOrganizationCode = this.formData.controls.linkedOrganizationCode.value;
      if (this.function_type == 'ADD') {
        this.linkedOrganizationAPI.organizationCode(this.linkedOrganizationCode).pipe(takeUntil(this.destroy$)).subscribe(
          (data) => {
            if (data.statusCode === 404) {
              this.loading = false;
              this.router.navigate([`system/configurations/global/linked/organization/data/view`], { skipLocationChange: true, queryParams: { formData: this.formData.value, fetchData: this.lookupData } });
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
        this.router.navigate([`system/configurations/global/linked/organization/data/view`], { skipLocationChange: true, queryParams: { formData: this.formData.value, fetchData: this.lookupData } });
      }
    }
    else if (this.formData.controls.function_type.value == "") {
      this.loading = false;
      this.submitted = true;
      this.submitting = false;
      this.notificationAPI.alertWarning("ORGANIZATION FORM FUNCTION IS INVALID");
    }
    else if (this.formData.controls.linkedOrganizationCode.value == "") {
      this.loading = false;
      this.submitted = true;
      this.submitting = false;
      this.notificationAPI.alertWarning("ORGANIZATION FORM CODE IS INVALID");
    } else {
      this.loading = false;
      this.submitted = true;
      this.submitting = false;
      this.notificationAPI.alertWarning("ORGANIZATION FORM DATA IS INVALID");
    }
  }

  organizationLookup(): void {
    const dialogRef = this.dialog.open(LinkedOrganizationLookupComponent, {
      autoFocus: false,
      maxHeight: '90vh'
    });
    dialogRef.afterClosed().subscribe(result => {
      this.lookupData = result.data;
      this.linkedOrganizationCode = this.lookupData.linkedOrganizationCode;
      this.organizationName = this.lookupData.organization_name;
      this.formData.controls.linkedOrganizationCode.setValue(this.linkedOrganizationCode);
    });
  }
}
