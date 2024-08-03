import { HttpParams } from '@angular/common/http';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { UntypedFormBuilder, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { Subject, Subscription } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { AuthService } from 'src/@core/AuthService/auth.service';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';
import { environment } from 'src/environments/environment';

@Component({
  selector: 'app-forgot-password',
  templateUrl: './forgot-password.component.html',
  styleUrls: ['./forgot-password.component.scss']
})
export class ForgotPasswordComponent implements OnInit, OnDestroy {
  loading = false;
  submitted = false;
  submitting: boolean = false;
  logolink: string = `${environment.reportsAPI}/api/v1/dynamic/saccologo`;
  message: any;
  dialogConfig: any
  destroy$: Subject<boolean> = new Subject<boolean>();
  fetchData: any;
  data: any;
  constructor(
    private formBuilder: UntypedFormBuilder,
    private router: Router,
    private authService: AuthService,
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
    this.initLoginForm();
  }
  formData = this.formBuilder.group({
    username: ['', Validators.required],
    otpCode: ['', Validators.required]
  });

  initLoginForm() {
    this.formData = this.formBuilder.group({
      username: [this.data.fetchData.username],
      otpCode: ['', Validators.required]
    });
  }
  get f() { return this.formData.controls; }
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
              this.notificationAPI.alertSuccess(res.message);
              this.router.navigate([`/authentication`], { skipLocationChange: true });
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
