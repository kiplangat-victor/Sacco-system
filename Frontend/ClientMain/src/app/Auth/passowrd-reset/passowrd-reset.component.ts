import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';
import { AuthenticationService } from '../authentication.service';
import { takeUntil } from 'rxjs/operators';
import { Subject } from 'rxjs';

@Component({
  selector: 'app-passowrd-reset',
  templateUrl: './passowrd-reset.component.html',
  styleUrls: ['./passowrd-reset.component.scss']
})
export class PassowrdResetComponent implements OnInit, OnDestroy {
  loading = false;
  hide = true;
  destroy$: Subject<boolean> = new Subject<boolean>()
  constructor(
    private fb: FormBuilder,
    private authAPI: AuthenticationService,
    private notificationAPI: NotificationService,
    @Inject(MAT_DIALOG_DATA) public useremail: any,
    private dialogRef: MatDialogRef<PassowrdResetComponent>
  ) { }
  ngOnDestroy(): void {
    this.destroy$.next(true);
    this.destroy$.complete();
  }
  ngOnInit(): void {
    this.onInitForm();
  }
  authForm = this.fb.group({
    emailAddress: ['', Validators.required],
    password: ['', Validators.required],
    confirmPassword: ['', Validators.required]
  });
  onInitForm() {
    this.authForm = this.fb.group({
      emailAddress: [this.useremail],
      password: ['', Validators.required],
      confirmPassword: ['', Validators.required]
    })
  }
  get f() { return this.authForm.controls; }
  onSubmit() {
    if (this.authForm.valid) {
      let password = this.authForm.controls.password.value;
      let confirmPassword = this.authForm.controls.confirmPassword.value;
      if (password != confirmPassword) {
        this.loading = false
        this.notificationAPI.alertWarning("PASSWORD DON'T MATCH !!");
      } else {
        this.loading = true;
        this.authAPI.forgotpassword(this.authForm.value).pipe(takeUntil(this.destroy$)).subscribe(
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
