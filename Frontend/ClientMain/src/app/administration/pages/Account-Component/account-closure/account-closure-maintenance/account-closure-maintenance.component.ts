import { Component, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { DataStoreService } from 'src/@core/helpers/data-store.service';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';
import { GeneralAccountsLookupComponent } from '../../general-accounts-lookup/general-accounts-lookup.component';

@Component({
  selector: 'app-account-closure-maintenance',
  templateUrl: './account-closure-maintenance.component.html',
  styleUrls: ['./account-closure-maintenance.component.scss']
})
export class AccountClosureMaintenanceComponent implements OnInit {
  loading = false;
  submitted = false;
  account_code: any;
  functionArray: any;
  function_type: any;
  error: any;
  lookupData: any;
  acid: any;
  onShowSearch = true;
  showAccountCode = false;
  constructor(
    public fb: FormBuilder,
    private router: Router,
    private dialog: MatDialog,
    private dataStoreApi: DataStoreService,
    private notificationAPI: NotificationService,
  ) {
    this.functionArray = this.dataStoreApi.getActionsByPrivilege("ACCOUNTS MANAGEMENT");
    this.functionArray = this.functionArray.filter(
      (arr: string) => arr === 'CLOSE' ||
        arr === 'VERIFY CLOSURE' || arr === 'ACTIVATE' || arr === 'VERIFY ACTIVATION');
  }
  formData = this.fb.group({
    function_type: ['', Validators.required],
    account_code: ['', Validators.required]
  });
  ngOnInit(): void { }

  onChange(event: any) {
    this.function_type = event.target.value;
    if (event.target.value == 'CLOSE') {
      this.onShowSearch = true;
      this.showAccountCode = true;
    }
    else if (event.target.value == 'VERIFY CLOSURE') {
      this.onShowSearch = true;
      this.showAccountCode = true;
    }
    else if (event.target.value == 'ACTIVATE') {
      this.onShowSearch = true;
      this.showAccountCode = true;
    }
    else if (event.target.value == 'VERIFY ACTIVATION') {
      this.onShowSearch = true;
      this.showAccountCode = true;
    }
  }
  get f() {
    return this.formData.controls;
  }
  onSubmit() {
    this.loading = true;
    if (this.formData.valid) {
      this.router.navigate([`system/account-closure/data/view`], { skipLocationChange: true, queryParams: { formData: this.formData.value, fetchedData: this.lookupData } });
    }
    else if (!this.formData.valid) {
      if (this.formData.controls.function_type.value == "") {
        this.loading = false;
        this.submitted = true;
        this.notificationAPI.alertWarning("CHOOSE ACCOUNT CLOSURE FUNCTION");
      } else if (this.formData.controls.account_code.value == "") {
        this.loading = false;
        this.submitted = true;
        this.notificationAPI.alertWarning("ACCOUNT CLOSURE ID IS EMPTY");
      } else {
        this.loading = false;
        this.submitted = true;
        this.notificationAPI.alertWarning("ACCOUNT CLOSURE DETAILS NOT ALLOWED");
      }
    }
  }
  accountLookup(): void {
    const dialogRef = this.dialog.open(GeneralAccountsLookupComponent, {
      width: '45%',
      autoFocus: false,
      maxHeight: '90vh'
    });
    dialogRef.afterClosed().subscribe(result => {
      this.lookupData = result.data;
      this.acid = this.lookupData.acid;
      this.formData.controls.account_code.setValue(this.acid);
    });
  }
}
