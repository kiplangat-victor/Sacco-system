import { Validators } from '@angular/forms';
import { Component, Inject, OnInit, OnDestroy } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { Subject } from 'rxjs';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';
import { AccountsService } from 'src/app/administration/Service/AccountsService/accounts/accounts.service';
import { HttpParams } from '@angular/common/http';
import { takeUntil } from 'rxjs/operators';

@Component({
  selector: 'app-collateral-dialog',
  templateUrl: './collateral-dialog.component.html',
  styleUrls: ['./collateral-dialog.component.scss']
})
export class CollateralDialogComponent implements OnInit, OnDestroy {
  loading = false;
  hide = true;
  destroy$: Subject<boolean> = new Subject<boolean>()
  params: any;
  constructor(
    private fb: FormBuilder,
    private accountsAPI: AccountsService,
    private notificationAPI: NotificationService,
    @Inject(MAT_DIALOG_DATA) public collateralCode: any,
    private dialogRef: MatDialogRef<CollateralDialogComponent>
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
