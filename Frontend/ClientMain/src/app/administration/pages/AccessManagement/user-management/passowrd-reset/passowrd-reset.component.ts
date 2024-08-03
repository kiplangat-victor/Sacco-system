import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { Subject, Subscription } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { AuthService } from 'src/@core/AuthService/auth.service';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';

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
    private authService: AuthService,
    private notificationAPI: NotificationService,
    @Inject(MAT_DIALOG_DATA) public userData: any,
    private dialogRef: MatDialogRef<PassowrdResetComponent>
  ) {    }
  ngOnDestroy(): void {
    this.destroy$.next(true);
    this.destroy$.complete();
  }
  ngOnInit(): void {
    this.onInitForm();
  }
  authForm = this.fb.group({
    emailAddress: [''],
    password: ['', Validators.required],
    confirmPassword: ['', Validators.required]
  });
  onInitForm() {
    this.authForm = this.fb.group({
      emailAddress: [this.userData],
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
        this.notificationAPI.alertWarning("PASSWORD DON'T MATCH");
      } else {
        this.loading = true;
        this.authService.resetPassword(this.authForm.value).pipe(takeUntil(this.destroy$)).subscribe(
          {
            next: (
              (res) => {
                if (res.statusCode == 200) {
                  this.loading = false;
                  this.notificationAPI.alertSuccess(res.message);
                  this.dialogRef.close();
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
        ), Subscription;
      }
    } else {
      this.loading = false;
      this.notificationAPI.alertWarning("PASSWORD RESET FORM DATA INVALID");
    }
  }
}
