import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder, Validators } from '@angular/forms';
import { Router } from '@angular/router';

import { HttpParams } from '@angular/common/http';
import { AuthService } from 'src/@core/AuthService/auth.service';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';

@Component({
  selector: 'app-reset-password',
  templateUrl: './reset-password.component.html',
  styleUrls: ['./reset-password.component.scss']
})
export class ResetPasswordComponent implements OnInit {
  loading = false;
  submitted = false;
  error = '';
  success = '';
  params!: HttpParams;
  res: any;
  errorMessage: any;

  constructor(
    private formBuilder: UntypedFormBuilder,
    private router: Router,
    private authService: AuthService,
    private notificationAPI: NotificationService
  ) {
    // redirect to home if already logged in

  }

  ngOnInit() {
    this.onInit();
  }

  formData = this.formBuilder.group({
    emailAddress: ['', Validators.required],
  });
  onInit(){
    this.formData = this.formBuilder.group({
      emailAddress: ['', Validators.required],
    });
  }
  // convenience getter for easy access to form fields
  get f() { return this.formData.controls; }


  onSubmit() {
    this.loading = true;
    if(this.formData.valid){
      this.authService.forgotPassword(this.formData.value).subscribe(res => {
        if(res.statusCode == 200 ){
          this.notificationAPI.alertSuccess(res.message);
          this.loading = false;
          this.router.navigate['/sso']
        }else{
          this.notificationAPI.alertWarning(res.message);
          this.loading = false;
        }
      }, error => {
        this.notificationAPI.alertWarning(error.message);
        this.loading = false;
      });
    }else{
      this.notificationAPI.alertWarning("Invalid Form!");
      this.loading = false;
    }
  }

}
