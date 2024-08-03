import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { DataStoreService } from 'src/@core/helpers/data-store.service';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';
import { MembershipLookUpComponent } from '../membership-look-up/membership-look-up.component';

@Component({
  selector: 'app-membership-maintenance',
  templateUrl: './membership-maintenance.component.html',
  styleUrls: ['./membership-maintenance.component.scss']
})
export class MembershipMaintenanceComponent implements OnInit, OnDestroy {
  functionArray: any;
  submitted = false
  function_type: any
  customerCode: any
  loading = false;
  onShowSearchIcon = true;
  showCustCode = false;
  destroy$: Subject<boolean> = new Subject<boolean>();
  lookupData: any;
  constructor(
    private router: Router,
    private fb: FormBuilder,
    private dialog: MatDialog,
    private dataStoreApi: DataStoreService,
    private notificationAPI: NotificationService
  ) {
    this.functionArray = this.dataStoreApi.getActionsByPrivilege("MEMBERSHIP MANAGEMENT");
    this.functionArray = this.functionArray.filter(
      (arr: string) =>
        arr === 'ADD' ||
        arr === 'DELETE' ||
        arr === 'INQUIRE' ||
        arr === 'MODIFY' ||
        arr === 'VERIFY' ||
        arr === 'REJECT'
    );
  }
  ngOnDestroy(): void {
    this.destroy$.next(true);
    this.destroy$.complete();
  }
  ngOnInit(): void {
  }
  formData = this.fb.group({
    function_type: ['', Validators.required],
    uniqueId: [''],
    customerCode: [''],
    id: ['', Validators.required]
  })
  get f() {
    return this.formData.controls;
  }
  onSelectFunction(event: any) {
    if (event.target.value == "ADD") {
      this.showCustCode = false;
      this.onShowSearchIcon = false;
      this.formData.controls.id.setValue(" ");
      this.formData.controls.customerCode.setValue(" ");
      this.formData.controls.uniqueId.setValue(" ");
    } else if (event.target.value !== "ADD") {
      this.showCustCode = true;
      this.onShowSearchIcon = true;
    }
  }
  onSubmit() {
    this.loading = true;
    this.submitted = true;
    if (this.formData.valid) {
      this.loading = false;
      this.router.navigate([`/system/membership/data/view`], { skipLocationChange: true, queryParams: { formData: this.formData.value } });
    } else if (!this.formData.valid) {
      if (this.formData.controls.function_type.value == "") {
        this.loading = false;
        this.submitted = true;
        this.notificationAPI.alertWarning("CHOOSE MEMBERSHIP FUNCTION");
      }
      else if (this.formData.controls.uniqueId.value == "") {
        this.loading = false;
        this.submitted = true;
        this.notificationAPI.alertWarning("MEMBERSHIP CODE CAN NOT BE EMPTY");
      } else {
        this.loading = false;
        this.submitted = true;
        this.notificationAPI.alertWarning("MEMBERSHIP DETAILS NOT ALLOWED")
      }
    }
  }
  membershipLookup(): void {
    const dialogRef = this.dialog.open(MembershipLookUpComponent, {
      width: '50%',
      autoFocus: false,
      maxHeight: '90vh'
    });
    dialogRef.afterClosed().pipe(takeUntil(this.destroy$)).subscribe(results => {
      this.lookupData = results.data;
      this.formData.controls.id.setValue(this.lookupData.id);
      this.formData.controls.customerCode.setValue(this.lookupData.customerCode);
      this.formData.controls.uniqueId.setValue(this.lookupData.customerUniqueId);
    })
  }
}
