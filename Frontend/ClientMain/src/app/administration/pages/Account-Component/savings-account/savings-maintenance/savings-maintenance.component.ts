import { takeUntil } from 'rxjs/operators';
import { Subject } from 'rxjs';
import { Component, OnInit, OnDestroy } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { DataStoreService } from 'src/@core/helpers/data-store.service';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';
import { SavingsLookupComponent } from '../savings-lookup/savings-lookup.component';

@Component({
  selector: 'app-savings-maintenance',
  templateUrl: './savings-maintenance.component.html',
  styleUrls: ['./savings-maintenance.component.scss']
})
export class SavingsMaintenanceComponent implements OnInit, OnDestroy {
  loading = false;
  submitted = false;
  account_code: any;
  functionArray: any;
  function_type: any;
  account_type: any;
  lookupData: any;
  acid: any;
  id: any;
  showFunctionType = false;
  showCustomerType = false;
  showAccountCode = false;
  destroy$: Subject<boolean> = new Subject<boolean>();
  constructor(
    public fb: FormBuilder,
    private router: Router,
    private dialog: MatDialog,
    private dataStoreApi: DataStoreService,
    private notificationAPI: NotificationService,
  ) {
    this.functionArray = this.dataStoreApi.getActionsByPrivilege("ACCOUNTS MANAGEMENT");
    this.functionArray = this.functionArray.filter(
      (arr: string) =>
        arr === 'ADD' ||
        arr === 'DELETE' ||
        arr === 'INQUIRE' ||
        arr === 'MODIFY' ||
        arr === 'REJECT' ||
        arr === 'VERIFY' ||
        arr === 'ACTIVATE' ||
        arr === 'STATEMENT');
  }
  ngOnDestroy(): void {
    this.destroy$.next(true);
    this.destroy$.complete();
  }
  formData = this.fb.group({
    id: [''],
    function_type: ['', Validators.required],
    //account_code: ['', Validators.required],
    account_type: ['']
  });
  ngOnInit(): void { }

  onChange(event: any) {
    this.function_type = event.target.value;
    if (event.target.value == "ADD") {
      this.showAccountCode = false;
      this.showCustomerType = true;
      this.formData.controls.id.setValue("0000000000");
      this.formData.controls.account_type.setValue("SBA");
    }
    else if (event.target.value !== "ADD") {
      this.showAccountCode = true;
      this.showCustomerType = true;
      this.formData.controls.id.setValue("");
    }
  }

  get f() {
    return this.formData.controls;
  }
  onSubmit() {
    this.loading = true;
    if (this.formData.valid) {
      this.loading = false;
      this.router.navigate([`system/savings-account/data/view`], { skipLocationChange: true, queryParams: { formData: this.formData.value, fetchedData: this.lookupData } });
    }
    else if (!this.formData.valid) {
      if (this.formData.controls.function_type.value == "") {
        this.loading = false;
        this.submitted = true;
        this.notificationAPI.alertWarning("CHOOSE SAVINGS FUNCTION")
      } else if (this.formData.controls.id.value == "") {
        this.loading = false;
        this.submitted = true;
        this.notificationAPI.alertWarning("SAVINGS ACCOUNT ID IS EMPTY")
      } else {
        this.loading = false;
        this.submitted = true;
        this.notificationAPI.alertWarning("SAVINGS ACCOUNT DETAILS NOT ALLOWED");
      }

    }
  }
  savingsCodeLookUp(): void {
    const dialogRef = this.dialog.open(SavingsLookupComponent, {
      width: '50%',
      autoFocus: false,
      maxHeight: '90vh'
    });
    dialogRef.afterClosed().pipe(takeUntil(this.destroy$)).subscribe(result => {
      this.lookupData = result.data;
      this.id = this.lookupData.id;
      this.formData.controls.id.setValue(this.id);
    });
  }
}
