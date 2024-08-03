import { HttpParams } from '@angular/common/http';
import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';
import { AccountsService } from 'src/app/administration/Service/AccountsService/accounts/accounts.service';
import { CollateralDialogComponent } from '../collateral-dialog/collateral-dialog.component';

@Component({
  selector: 'app-update-collateral-dialog',
  templateUrl: './update-collateral-dialog.component.html',
  styleUrls: ['./update-collateral-dialog.component.scss']
})
export class UpdateCollateralDialogComponent implements OnInit, OnDestroy {
  loading = false;
  hide = true;
  destroy$: Subject<boolean> = new Subject<boolean>()
  params: any;
  constructor(
    private fb: FormBuilder,
    private accountsAPI: AccountsService,
    private notificationAPI: NotificationService,
    @Inject(MAT_DIALOG_DATA) public collateralCode: any,
    private dialogRef: MatDialogRef<UpdateCollateralDialogComponent>
  ) {
  }
  ngOnDestroy(): void {
    this.destroy$.next(true);
    this.destroy$.complete();
  }
  ngOnInit(): void {
    this.initForm();
  }
  formData: FormGroup = this.fb.group({
    collateralCode: ['', Validators.required],
    otpCode: ['', Validators.required]
  });
  initForm() {
    this.formData = this.fb.group({
      collateralCode: [this.collateralCode],
      otpCode: ['', Validators.required]
    });
  }
  onSubmit() {
    this.loading = true;
    this.params = new HttpParams()
      .set('colateralCode', this.collateralCode)
      .set('otp', this.formData.controls.otpCode.value)
    this.accountsAPI.verifyOtp(this.params).pipe(takeUntil(this.destroy$)).subscribe(
      {
        next: (
          (res) => {
            if (res.statusCode === 200) {
              this.loading = false;
              this.notificationAPI.alertSuccess(res.message);
              this.dialogRef.close(res);
            } else {
              this.loading = false;
              this.notificationAPI.alertWarning(res.message);
            }
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
    )
  }
}
