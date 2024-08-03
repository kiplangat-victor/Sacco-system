import { Component, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { DataStoreService } from 'src/@core/helpers/data-store.service';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';
import { LoanDisbursementLookUpComponent } from '../loan-disbursement-look-up/loan-disbursement-look-up.component';
import { LoansAccountsLookUpComponent } from '../../loans-accounts-look-up/loans-accounts-look-up.component';

@Component({
  selector: 'app-loan-disbursement-maintenance',
  templateUrl: './loan-disbursement-maintenance.component.html',
  styleUrls: ['./loan-disbursement-maintenance.component.scss']
})
export class LoanDisbursementMaintenanceComponent implements OnInit {
  showDisbursementAccountCode = false;
  showLoanDisbursementVerification = false;
  showCustomerType = false;
  function_type: any;
  account_type: any;
  customer_type: any;
  subscription!: Subscription;
  event_type: any;
  error: any;
  data: any;
  loading = false;
  account_code: any;
  functionArray: any = ['STATEMENT', 'DISBURSEMENT', 'VERIFICATION'];
  acid: any;
  customerType: any;
  submitted = false;
  onShowSubmitButton = false;
  constructor(
    public fb: FormBuilder,
    private router: Router,
    private dialog: MatDialog,
    private dataStoreApi: DataStoreService,
    private notificationAPI: NotificationService,
  ) { }
  formData = this.fb.group({
    function_type: ['', Validators.required],
    account_code: ['', Validators.required],
   // acid: ['', Validators.required]
  });
  ngOnInit(): void {

  }
  onChange(event: any) {
    this.function_type = event.target.value;
      this.showDisbursementAccountCode = true;
      this.onShowSubmitButton = true;
      this.formData.controls.account_code.setValue("");
  }
  onAccountChange(event: any) {
    this.account_type = event.target.value;
  }
  get f() {
    return this.formData.controls;
  }
  onSubmit() {
    this.loading = true;
    this. submitted = true;
    if (this.formData.valid) {
      this.loading = false;
      this.router.navigate([`system/loan-account/data/view`], { skipLocationChange: false, queryParams: { formData: this.formData.value, fetchedData: this.data} });
    }
    else if (!this.formData.valid) {
      this.loading = false;
      this.notificationAPI.alertWarning("DISBURSMENT FORM INVALID");
    }
  }
  loansDisbursementAccountLookUp(): void {
    const dialogRef = this.dialog.open(LoansAccountsLookUpComponent, {
      width: '60%',
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
