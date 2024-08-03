import { HttpParams } from '@angular/common/http';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { Subject, Subscription } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';
import { RepaymentAccountsComponent } from '../../../repayment-accounts/repayment-accounts.component';
import { LoanPayOffService } from '../loan-pay-off.service';

@Component({
  selector: 'app-loan-fee-repayment',
  templateUrl: './loan-fee-repayment.component.html',
  styleUrls: ['./loan-fee-repayment.component.scss'],
})
export class LoanFeeRepaymentComponent implements OnInit, OnDestroy {
  submitted = false;
  loading = false;
  lookupData: any;
  acid: any;
  loansForm: any;
  fmData: any;
  function_type: any;
  account_code: any;
  params: HttpParams;
  results: any;
  destroy$: Subject<boolean> = new Subject<boolean>();
  constructor(
    public fb: FormBuilder,
    private router: Router,
    private dialog: MatDialog,
    private payOffAPI: LoanPayOffService,
    private notificationAPI: NotificationService
  ) {
    this.fmData =
      this.router.getCurrentNavigation().extras.queryParams.formData;
    this.function_type = this.fmData.function_type;
    this.account_code = this.fmData.account_code;
  }
  ngOnDestroy() {
    this.destroy$.next(true);
    this.destroy$.complete();
  }

  formData = this.fb.group({
    loanAccountAcid: ['', Validators.required],
    operativeAccountAcid: ['', Validators.required],
  });
  initFormData() {
    this.formData = this.fb.group({
      loanAccountAcid: [this.account_code],
      operativeAccountAcid: ['', Validators.required],
    });
  }

  ngOnInit(): void {
    this.initFormData();
  }
  get f() {
    return this.formData.controls;
  }
  onSubmit() {
    this.submitted = true;
    if (this.function_type === 'ADD') {
      if (this.formData.valid) {
        this.params = new HttpParams()
          .set('loanAccountAcid', this.formData.controls.loanAccountAcid.value)
          .set(
            'operativeAccountAcid',
            this.formData.controls.operativeAccountAcid.value
          );
        this.loading = true;
        this.payOffAPI
          .addPayOff(this.params)
          .pipe(takeUntil(this.destroy$))
          .subscribe({
            next: (res) => {
              if (res.statusCode === 200) {
                this.loading = false;
                this.results = res;
                this.notificationAPI.alertSuccess(res.message);
              } else {
                this.loading = false;
                this.notificationAPI.alertWarning(res.message);
              }
            },
            error: (err) => {
              this.loading = false;
              this.notificationAPI.alertWarning('SERVER ERROR:');
            },
            complete: () => {},
          });
      } else if (!this.formData.valid) {
        this.loading = false;
        this.notificationAPI.alertWarning('LOAN PAY OFF FORM DATA IS INVALID');
      }
    }
  }

  dialogConfig: any;

  customerSavingCodeLookUp() {
    this.dialogConfig = new MatDialogConfig();
    this.dialogConfig.disableClose = true;
    this.dialogConfig.autoFocus = true;
    this.dialogConfig.data = this.fmData.member_code;
    const dialogRef = this.dialog.open(RepaymentAccountsComponent, this.dialogConfig);
    dialogRef.afterClosed().subscribe(
      {
        next: (
          (res) => {
            this.lookupData = res.data;
            this.acid = this.lookupData.acid;
            this.formData.controls.operativeAccountAcid.setValue(this.acid);
          }
        ),
        error: (
          (err) => {
            this.loading = false;
            this.notificationAPI.alertWarning("Server Error: !!");
          }
        ),
        complete: (
          () => {

          }
        )
      }
    ), Subscription;
}

  
  
}

