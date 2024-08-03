import { takeUntil } from 'rxjs/operators';
import { HttpParams } from '@angular/common/http';
import { Component, OnInit, OnDestroy } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { Subject } from 'rxjs';
import { AccountsNotificationService } from 'src/@core/helpers/accounts/accounts-notification.service';
import { DataStoreService } from 'src/@core/helpers/data-store.service';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';
import { LoansAccountsLookUpComponent } from '../../loans-accounts-look-up/loans-accounts-look-up.component';
import { LoanPayOffService } from '../loan-pay-off.service';

@Component({
  selector: 'app-loan-repayment-maintenance',
  templateUrl: './loan-repayment-maintenance.component.html',
  styleUrls: ['./loan-repayment-maintenance.component.scss']
})
export class LoanRepaymentMaintenanceComponent implements OnInit, OnDestroy {
  showCustomerType = false;
  showAccountCode = false;
  function_type: any;
  event_type: any;
  error: any;
  params: any;
  lookupData: any;
  acid: any;
  loading = false;
  submitted = false;
  account_code: any;
  functionArray: any;
  results: any;
  destroy$: Subject<boolean> = new Subject<boolean>();
  constructor(
    public fb: FormBuilder,
    private router: Router,
    private dialog: MatDialog,
    private payOffAPI: LoanPayOffService,
    private dataStoreApi: DataStoreService,
    private notificationAPI: NotificationService,
    private accountsNotification: AccountsNotificationService
  ) {
    this.functionArray = this.dataStoreApi.getActionsByPrivilege("ACCOUNTS MANAGEMENT");
    this.functionArray = this.functionArray.filter(
      (      arr: string) =>
        arr === 'ADD' ||
        arr === 'VERIFY' ||
        arr === 'GET PAY OFFS');
  }
  ngOnDestroy(): void {
    this.destroy$.next(true);
    this.destroy$.complete();
  }
  formData = this.fb.group({
    function_type: ['', Validators.required],
    account_code: ['', Validators.required],
    member_code: ['', Validators.required],


  });
  ngOnInit(): void {
  }
  onChange(event: any) {
    this.function_type = event.target.value;
    if (event.target.value === "ADD") {
      this.showAccountCode = true;
    } else if (event.target.value === "VERIFY") {
      this.showAccountCode = true;
    } else if (event.target.value === "GET PAY OFFS") {
      this.showAccountCode = true;
    }
  }
  disableControls(){
    this.formData.disable();
  }

  get f() {
    return this.formData.controls;
  }
  onSubmit() {
    this.loading = true;
    this.submitted = true;
    if (this.function_type === "ADD") {
      if (this.formData.valid) {
        this.loading = false;
        this.router.navigate([`system/loans-account/fee/repayment/data/view`], { skipLocationChange: true, queryParams: { formData: this.formData.value } });
      }
      else if (!this.formData.valid) {
        if (this.formData.controls.function_type.value == "") {
          this.loading = false;
          this.submitted = true;
          this.notificationAPI.alertWarning("CHOOSE REPAYMENT FUNCTION");
        } else if (this.formData.controls.account_code.value == "") {
          this.loading = false;
          this.submitted = true;
          this.notificationAPI.alertWarning("LOAN ACCOUNT ID IS EMPTY");
        } else {
          this.loading = false;
          this.submitted = true;
          this.notificationAPI.alertWarning("LOAN ACCOUNT DETAILS NOT ALLOWED");
        }
      }
    } else if (this.function_type === "VERIFY") {
      if (this.formData.valid) {
        this.params = new HttpParams()
          .set("loanAccountAcid", this.formData.controls.account_code.value);
        this.loading = true;
        this.payOffAPI.verify(this.params).pipe(takeUntil(this.destroy$)).subscribe(
          {
            next: (
              (res) => {
                if(res.statusCode === 200){
                  this.loading = false;
                  this.notificationAPI.alertSuccess(res.message);
                } else{
                  this.loading = false;
                  this.notificationAPI.alertWarning(res.message);
                }
              }
            ),
            error: (
              (err) => {
                this.loading = false;
                this.notificationAPI.alertWarning("SERVER ERROR:");
              }
            ),
            complete: (
              () => {

              }
            )
          }
        )

      } else if (!this.formData.valid) {
        this.loading = false;
        this.notificationAPI.alertWarning("LOAN PAY OFF FORM DATA IS INVALID");
      }
    } else if (this.function_type === "GET PAY OFFS") {
      if (this.formData.valid) {
        this.params = new HttpParams()
          .set("acid", this.formData.controls.account_code.value);
        this.loading = true;
        this.payOffAPI.getLoanPayOffs(this.params).pipe(takeUntil(this.destroy$)).subscribe(
          {
            next: (
              (res) => {
                if(res.statusCode === 200){
                  this.loading = false;
                  this.accountsNotification.alertSuccess(res.message);
                } else{
                  this.loading = false;
                  this.notificationAPI.alertWarning(res.message);
                };
              }
            ),
            error: (
              (err) => {
                this.loading = false;
                this.notificationAPI.alertWarning("SERVER ERROR:");
              }
            ),
            complete: (
              () => {

              }
            )
          }
        )

      } else if (!this.formData.valid) {
        this.loading = false;
        this.notificationAPI.alertWarning("LOAN PAY OFF FORM DATA IS INVALID");
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

      console.log("this.lookupData:: ", this.lookupData)

      this.acid = this.lookupData.acid;
      this.formData.controls.account_code.setValue(this.acid);
      this.formData.controls.member_code.setValue(this.lookupData.customer_code);
    });
  }
}

