import { Component, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { DataStoreService } from 'src/@core/helpers/data-store.service';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';
import { OfficeAccountsLookUpsComponent } from '../office-accounts-look-ups/office-accounts-look-ups.component';

@Component({
  selector: 'app-office-account-maintenance',
  templateUrl: './office-account-maintenance.component.html',
  styleUrls: ['./office-account-maintenance.component.scss']
})
export class OfficeAccountMaintenanceComponent implements OnInit {
  loading = false;
  submitted = false;
  account_code: any;
  functionArray: any;
  function_type: any;
  error: any;
  lookupData: any;
  acid: any;
  customerType: any;
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
      (arr: string) =>
        arr === 'ADD' ||
        arr === 'DELETE' ||
        arr === 'INQUIRE' ||
        arr === 'MODIFY' ||
        arr === 'VERIFY' ||
        arr === 'REJECT' ||
        arr === 'STATEMENT');
  }
  formData = this.fb.group({
    function_type: ['', Validators.required],
    account_code: ['', Validators.required],
    account_type: ['']
  });
  ngOnInit(): void { }

  onChange(event: any) {
    this.function_type = event.target.value;
    if (event.target.value == "ADD") {
      this.onShowSearch = false;
      this.showAccountCode = false;
      this.formData.controls.account_code.setValue("0000000000");
      this.formData.controls.account_type.setValue("OAB");
    }
    else if (event.target.value != "ADD") {
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
      this.router.navigate([`system/office-account/data/view`], { skipLocationChange: true, queryParams: { formData: this.formData.value, fetchedData: this.lookupData } });
    }
    else if (!this.formData.valid) {
      if (this.formData.controls.function_type.value == "") {
        this.loading = false;
        this.submitted = true;
        this.notificationAPI.alertWarning("CHOOSE OFFICE ACCOUNT FUNCTION");
      } else if (this.formData.controls.account_code.value == "") {
        this.loading = false;
        this.submitted = true;
        this.notificationAPI.alertWarning("OFFICE ACCOUNT ID IS EMPTY");
      } else {
        this.loading = false;
        this.submitted = true;
        this.notificationAPI.alertWarning("OFFICE ACCOUNT DETAILS NOT ALLOWED");
      }
    }
  }
  officeAccountLookup(): void {
    const dialogRef = this.dialog.open(OfficeAccountsLookUpsComponent, {
      width: '45%',
      autoFocus: false,
      maxHeight: '90vh',
    });
    dialogRef.afterClosed().subscribe(result => {
      this.lookupData = result.data;
      this.acid = this.lookupData.acid;
      this.customerType = this.lookupData.customerType;
      this.formData.controls.account_code.setValue(this.acid);
    });
  }
}
