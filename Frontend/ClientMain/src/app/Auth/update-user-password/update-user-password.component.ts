import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';
import { AuthenticationService } from '../authentication.service';

@Component({
  selector: 'app-update-user-password',
  templateUrl: './update-user-password.component.html',
  styleUrls: ['./update-user-password.component.scss']
})
export class UpdateUserPasswordComponent implements OnInit, OnDestroy {
  loading = false;
  hide = true;
  destroy$: Subject<boolean> = new Subject<boolean>()
  constructor(
    private fb: FormBuilder,
    private authAPI: AuthenticationService,
    private notificationAPI: NotificationService,
    @Inject(MAT_DIALOG_DATA) public useremail: any,
    private dialogRef: MatDialogRef<UpdateUserPasswordComponent>
  ) { }
  ngOnDestroy(): void {
    this.destroy$.next(true);
    this.destroy$.complete();
  }
  ngOnInit(): void {
    this.onInitForm();
  }
  formData = this.fb.group({
    emailAddress: ['', Validators.required],
    password: ['', Validators.required],
    confirmPassword: ['', Validators.required]
  });
  onInitForm() {
    this.formData = this.fb.group({
      emailAddress: [this.useremail],
      password: ['', Validators.required],
      confirmPassword: ['', Validators.required]
    })
  }
  get f() { return this.formData.controls; }
  onSubmit() {
    if (this.formData.valid) {
      let password = this.formData.controls.password.value;
      let confirmPassword = this.formData.controls.confirmPassword.value;
      if (password != confirmPassword) {
        this.loading = false
        this.notificationAPI.alertWarning("Password Don't Match: !!");
      } else {
        this.loading = true;
        console.log(this.formData.value);
        this.authAPI.forgotpassword(this.formData.value).pipe(takeUntil(this.destroy$)).subscribe(
          {
            next: (
              (res) => {
                if (res.statusCode == 200) {
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
                this.notificationAPI.alertWarning(err);
              }
            ),
            complete: (
              () => {

              }
            )
          }
        )
      }
    } else {
      this.loading = false;
      this.notificationAPI.alertWarning("PASSWORD RESET FORM DATA INVALID");
    }
  }
}
