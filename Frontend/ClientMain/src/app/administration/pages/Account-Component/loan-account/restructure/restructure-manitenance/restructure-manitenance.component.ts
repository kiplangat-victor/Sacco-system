import { Component, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { DataStoreService } from 'src/@core/helpers/data-store.service';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';
import { LoansAccountsLookUpComponent } from '../../loans-accounts-look-up/loans-accounts-look-up.component';

@Component({
  selector: 'app-restructure-manitenance',
  templateUrl: './restructure-manitenance.component.html',
  styleUrls: ['./restructure-manitenance.component.scss']
})
export class RestructureManitenanceComponent implements OnInit {
  function_type: any;
  subscription!: Subscription;
  error: any;
  data: any;
  loading = false;
  account_code: any;
  functionArray: any;
  acid: any;
  customerType: any;
  submitted = false;
  constructor(
    public fb: FormBuilder,
    private router: Router,
    private dialog: MatDialog,
    private dataStoreApi: DataStoreService,
    private notificationAPI: NotificationService,
  ) {
    this.functionArray = this.dataStoreApi.getActionsByPrivilege("ACCOUNTS MANAGEMENT");
    this.functionArray = this.functionArray.filter(
      arr => arr === 'LOAN-RESCHEDULING');
  }
  formData = this.fb.group({
    function_type: ['LOAN RESTRUCTURE'],
    account_code: ['', Validators.required],
  });
  ngOnInit(): void {

  }
  get f() {
    return this.formData.controls;
  }
  onSubmit() {
    this.loading = true;
    if (this.formData.valid) {
      this.loading = false;
      this.router.navigate([`system/loan-rescheduling-data-view`], { skipLocationChange: true, queryParams: { formData: this.formData.value, fetchedData: this.data } });
    }
    else if (!this.formData.valid) {
      if (this.formData.controls.account_code.value == "") {
        this.loading = false;
        this.submitted = true;
        this.notificationAPI.alertWarning("LOAN ACOUNT ID IS INVALID");
      } else {
        this.loading = false;
        this.submitted = true;
        this.notificationAPI.alertWarning("LOAN RESTRUCTURE DATA NOT ALLOWED");
      }
    }
  }
  loanAccountLookUp(): void {
    const dialogRef = this.dialog.open(LoansAccountsLookUpComponent, {
      width: '50%',
      autoFocus: false,
      maxHeight: '90vh',
    });
    dialogRef.afterClosed().subscribe(result => {
      this.data = result.data;
      this.acid = this.data.acid;
      this.customerType = this.data.customerType;
      this.formData.controls.account_code.setValue(this.acid);
    });
  }
}
