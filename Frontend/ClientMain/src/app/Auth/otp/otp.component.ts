import { HttpParams } from '@angular/common/http';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { takeUntil } from 'rxjs/operators';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { Subject, Subscription } from 'rxjs';
import { AuthService } from 'src/@core/AuthService/auth.service';
import { TokenStorageService } from 'src/@core/AuthService/token-storage.service';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';
import { environment } from 'src/environments/environment';
import { PassowrdResetComponent } from '../passowrd-reset/passowrd-reset.component';

@Component({
  selector: 'app-otp',
  templateUrl: './otp.component.html',
  styleUrls: ['./otp.component.scss']
})
export class OtpComponent implements OnInit, OnDestroy {
  loading = false;
  submitted = false;
  logolink: string = `${environment.reportsAPI}/api/v1/dynamic/saccologo`;
  message: any;
  dialogConfig: any
  destroy$: Subject<boolean> = new Subject<boolean>();
  fetchData: any;
  data: any;
  constructor(
    private fb: FormBuilder,
    private router: Router,
    private dialog: MatDialog,
    private authService: AuthService,
    private tokenStorage: TokenStorageService,
    private notificationAPI: NotificationService
  ) {
    this.data = this.router.getCurrentNavigation().extras.queryParams;
  }
  ngOnDestroy(): void {
    this.destroy$.next(true);
    this.destroy$.complete();
  }
  ngOnInit() {
    this.initLoginForm();
  }
  formData = this.fb.group({
    username: ['', Validators.required],
    otpCode: ['', Validators.required]
  });

  initLoginForm() {
    this.formData = this.fb.group({
      username: [this.data.fetchData.username],
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
    const dialogRef = this.dialog.open(PassowrdResetComponent, this.dialogConfig);
    dialogRef.afterClosed().pipe(takeUntil(this.destroy$)).subscribe(
      {
        next: (
          (res) => {
            if (res.statusCode === 200) {
              this.loading = false;
              this.router.navigate([`/system`]);
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
      .set('usedUserName', this.formData.controls.username.value);
    this.authService.validateOTP(params).pipe(takeUntil(this.destroy$)).subscribe(
      {
        next: (
          (res) => {
            if (res.statusCode === 200) {
              this.loading = false;
              this.data = res.entity;
              this.tokenStorage.saveToken(this.data.accessToken);
              this.tokenStorage.saveUser(this.data);
              if (this.data.isSystemGenPassword == 'Y') {
                this.loading = false;
                this.onResetPass(this.data.email);
              } else if (this.data.isSystemGenPassword == 'N') {
                this.loading = false;
                this.router.navigate([`/system`]);
              }
              this.notificationAPI.alertSuccess("Welcome " + this.data.username + ", You are Fully Authenticated.");
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
  reloadPage(): void {
    window.location.reload();
  }
}
