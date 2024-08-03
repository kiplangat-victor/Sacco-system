import { HttpParams } from '@angular/common/http';
import { Component, NgZone, OnDestroy, OnInit } from '@angular/core';
import { UntypedFormBuilder, Validators } from '@angular/forms';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { Subject, Subscription } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { AuthService } from 'src/@core/AuthService/auth.service';
import { TokenStorageService } from 'src/@core/AuthService/token-storage.service';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';
import { environment } from 'src/environments/environment';
import { UpdateUserPasswordComponent } from '../update-user-password/update-user-password.component';

@Component({
  selector: 'app-authentication-otp',
  templateUrl: './authentication-otp.component.html',
  styleUrls: ['./authentication-otp.component.scss']
})
export class AuthenticationOTPComponent implements OnInit, OnDestroy {
  loading = false;
  submitted = false;
  submitting: boolean = false;
  logolink: string = `${environment.reportsAPI}/api/v1/dynamic/saccologo`;
  message: any;
  dialogConfig: any
  destroy$: Subject<boolean> = new Subject<boolean>();
  fetchData: any;
  data: any;
  roles: any;
  constructor(
    private formBuilder: UntypedFormBuilder,
    private router: Router,
    private dialog: MatDialog,
    private authService: AuthService,
    private tokenStorage: TokenStorageService,
    private notificationAPI: NotificationService
  ) {
    if (this.router.getCurrentNavigation().extras.queryParams == null) {
      this.router.navigate([`/authentication`], { skipLocationChange: true });
    } else if (this.router.getCurrentNavigation().extras.queryParams != null) {
      this.data = this.router.getCurrentNavigation().extras.queryParams;
    }
  }
  ngOnDestroy(): void {
    this.destroy$.next(true);
    this.destroy$.complete();
  }
  ngOnInit() {
    this.initFormData();
  }
  formData = this.formBuilder.group({
    username: ['', Validators.required],
    otpCode: ['', Validators.required]
  });

  initFormData() {
    this.formData = this.formBuilder.group({
      username: [this.data.fetchData.username],
      otpCode: ['', Validators.required]
    });
  }
  removeUser() {
    localStorage.removeItem('currentUser');
    this.formData = this.formBuilder.group({
      otpCode: ['', Validators.required]
    });
  }
  get f() { return this.formData.controls; }

  onResetPass(useremail: any) {
    this.dialogConfig = new MatDialogConfig();
    this.dialogConfig.disableClose = true;
    this.dialogConfig.autoFocus = true;
    this.dialogConfig.width = "30%";
    this.dialogConfig.data = useremail;
    const dialogRef = this.dialog.open(UpdateUserPasswordComponent, this.dialogConfig);
    dialogRef.afterClosed().pipe(takeUntil(this.destroy$)).subscribe(
      {
        next: (
          (res) => {
            if (res.statusCode === 200) {
              this.loading = false;
              this.router.navigate([`/administration`]);
              this.notificationAPI.alertSuccess(res.message);
            } else {
              this.loading = false;
              this.notificationAPI.alertWarning("Passowrd must be be updated!!");
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
    ), Subscription
  }

  onSubmit(): void {
    this.loading = false;
    let params = new HttpParams()
      .set('otpCode', this.formData.controls.otpCode.value)
      .set('username', this.formData.controls.username.value);
    this.authService.validateOTP(params).pipe(takeUntil(this.destroy$)).subscribe(
      {
        next: (
          (res) => {
            if (res.statusCode === 200) {
              this.loading = false;
              this.data = res.entity;
              console.log("RES", this.data);
              this.tokenStorage.saveToken(this.data.accessToken);
              this.tokenStorage.saveUser(this.data);
              if (this.data.isSystemGenPassword == 'Y') {
                this.loading = false;
                this.onResetPass(this.data.email);
              } else if (this.data.isSystemGenPassword == 'N') {
                this.loading = false;
                this.roles = this.data.roles[0].name;
                if (this.roles == 'ROLE_MANAGER') {
                  this.notificationAPI.alertSuccess(res.message);
                  this.router.navigate([`/saccomanagement`]);
                } else if (this.roles !== 'ROLE_MANAGER') {
                  this.notificationAPI.alertSuccess(res.message);
                  this.router.navigate([`/administration`]);
                }
              }
            } else {
              this.loading = false;
              this.notificationAPI.alertWarning(res.message);
              this.router.navigate([`/authentication`], { skipLocationChange: true });
            }
          }
        ),
        error: (
          (err) => {
            this.loading = false;
            this.notificationAPI.alertWarning("Server Error: !!");
            this.router.navigate([`/authentication`], { skipLocationChange: true });
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
