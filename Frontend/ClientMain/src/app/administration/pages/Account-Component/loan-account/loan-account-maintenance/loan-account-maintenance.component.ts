import { Component, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { DataStoreService } from 'src/@core/helpers/data-store.service';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';
import { LoansAccountsLookUpComponent } from '../loans-accounts-look-up/loans-accounts-look-up.component';

@Component({
  selector: 'app-loan-account-maintenance',
  templateUrl: './loan-account-maintenance.component.html',
  styleUrls: ['./loan-account-maintenance.component.scss']
})
export class LoanAccountMaintenanceComponent implements OnInit {
  showFunctionType = false;
  showCustomerType = false;
  showAccountCode = false;
  function_type: any;
  account_type: any;
  customer_type: any;
  isRequired = false;
  function_type_data: any;
  event_type: any;
  event_description: any;
  error: any;
  event_type_data: any;
  params: any;
  lookupdata: any;
  lookupData: any;
  acid: any;
  id: any;
  loading = false;
  submitted = false;
  account_code: any;
  customerType: any;
  resultsData: any;
  functionArray: any;
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
        arr === 'ACCRUAL' ||
        arr === 'BOOKING' ||
        arr === 'DELETE' ||
        arr === 'DEMANDS' ||
        arr === 'INQUIRE' ||
        arr === 'MODIFY' ||
        arr === 'VERIFY' ||
        arr === 'REJECT' ||
        arr === 'MASSIVE INTEREST REVERSALS' || 
        arr === 'FORCE DEMANDS');

  }
  formData = this.fb.group({
    function_type: ['', Validators.required],
    account_code: ['', Validators.required],
    //acid: ['', Validators.required],
    id: [''],
    account_type: ['']
  });
  ngOnInit(): void {
  }
  onChange(event: any) {
    this.function_type = event.target.value;
    if (event.target.value == "ADD") {
      this.showCustomerType = true;
      this.showAccountCode = false;
      this.formData.controls.account_code.setValue("00000000000");
      //this.formData.controls.id.setValue("00000000000");
      this.formData.controls.account_type.setValue("LAA");
    } else if (event.target.value != "ADD") {
      this.showAccountCode = true;
      this.showCustomerType = true;
    }
  }

  get f() {
    return this.formData.controls;
  }
  onSubmit() {
    this.loading = true; 
    this.submitted = true;
    if (this.formData.valid) {
      if (this.function_type == 'FORCE DEMANDS') {
        this.loading = false;
        this.router.navigate([`system/loan-account-force-demands/data/view`], { skipLocationChange: true, queryParams: { formData: this.formData.value } });
      } else if (this.function_type == 'MASSIVE INTEREST REVERSALS') {
        this.loading = false;
        this.router.navigate([`system/loan-account-massive-interest/data/view`], { skipLocationChange: true, queryParams: { formData: this.formData.value } });
      } else if (this.function_type !== 'FORCE DEMANDS') {
        this.loading = false;
        this.router.navigate([`system/loan-account/data/view`], { skipLocationChange: true, queryParams: { formData: this.formData.value } });
      }
    
    else if (!this.formData.valid) {
      if (this.formData.controls.function_type.value == "") {
        this.loading = false;
        this.submitted = true;
        this.notificationAPI.alertWarning("Choose Loan Account function");
      } else if (this.formData.controls.id.value == "") {
        this.loading = false;
        this.submitted = true;
        this.notificationAPI.alertWarning("Loan Account id is empty");
      } else {
        this.loading = false;
        this.submitted = true;
        this.notificationAPI.alertWarning("Loan Account details not allowed");
      }
    }
  }
}
loansAccountLookUp(): void {
  const dialogRef = this.dialog.open(LoansAccountsLookUpComponent, {
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




