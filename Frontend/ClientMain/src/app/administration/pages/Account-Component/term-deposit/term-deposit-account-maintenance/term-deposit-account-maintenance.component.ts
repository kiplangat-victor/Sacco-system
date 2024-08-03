import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { DataStoreService } from 'src/@core/helpers/data-store.service';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';
import { TermDepositAccountLookUpComponent } from '../term-deposit-account-look-up/term-deposit-account-look-up.component';

@Component({
  selector: 'app-term-deposit-account-maintenance',
  templateUrl: './term-deposit-account-maintenance.component.html',
  styleUrls: ['./term-deposit-account-maintenance.component.scss']
})
export class TermDepositAccountMaintenanceComponent implements OnInit, OnDestroy {
  loading = false;
  submitted = false;
  account_code: any;
  functionArray: any;
  function_type: any;
  account_type: any;
  lookupData: any;
  acid: any;
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
        arr === 'VERIFY' ||
        arr === 'REJECT' ||
        arr === 'STATEMENT' ||
        arr === 'CLOSE TDA');
  }
  ngOnDestroy(): void {
    this.destroy$.next(true);
    this.destroy$.complete();
  }
  formData = this.fb.group({
    function_type: ['', Validators.required],
    account_code: ['', Validators.required],
    account_type: ['']
  });
  ngOnInit(): void {

  }

  onChange(event: any) {
    this.function_type = event.target.value;
    if (event.target.value == "ADD") {
      this.showAccountCode = false;
      this.showCustomerType = true;
      this.formData.controls.account_code.setValue("0000000000");
      this.formData.controls.account_type.setValue("TDA");
    }
    else if (event.target.value !== "ADD") {
      this.showAccountCode = true;
      this.showCustomerType = true;
      this.formData.controls.account_code.setValue("");
    }
  }

  get f() {
    return this.formData.controls;
  }
  onSubmit() {
    this.loading = true;
    if (this.formData.valid) {
      this.loading = false;
      this.router.navigate([`system/term-deposit/account/data-view`], { skipLocationChange: true, queryParams: { formData: this.formData.value, fetchedData: this.lookupData } });
    }
    else if (!this.formData.valid) {
      if (this.formData.controls.function_type.value == "") {
        this.loading = false;
        this.submitted = true;
        this.notificationAPI.alertWarning("CHOOSE FIXED DEPOSIT FUNCTION")
      } else if (this.formData.controls.account_code.value == "") {
        this.loading = false;
        this.submitted = true;
        this.notificationAPI.alertWarning("FIXED DEPOSIT ACCOUNT ID IS EMPTY")
      } else {
        this.loading = false;
        this.submitted = true;
        this.notificationAPI.alertWarning("FIXED DEPOSIT  DETAILS NOT ALLOWED");
      }

    }
  }
  termDepositCodeLookUp(): void {
    const dialogRef = this.dialog.open(TermDepositAccountLookUpComponent, {
      width: '50%',
      autoFocus: false,
      maxHeight: '90vh'
    });
    dialogRef.afterClosed().pipe(takeUntil(this.destroy$)).subscribe(result => {
      this.lookupData = result.data;
      this.formData.controls.account_code.setValue(this.lookupData.acid);
    });
  }
}
