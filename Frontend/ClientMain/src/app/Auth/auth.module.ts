import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';

import { HTTP_INTERCEPTORS } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { ErrorInterceptor } from 'src/@core/helpers/error.interceptor';
import { MaterialModule } from '../material.module';
import { AuthRoutingModule } from './auth-routing.module';
import { AuthComponent } from './auth.component';
import { PageError404Component } from './page-error404/page-error404.component';
import { PageError500Component } from './page-error500/page-error500.component';
import { SignInComponent } from './sign-in/sign-in.component';
import { AuthenticationOTPComponent } from './authentication-otp/authentication-otp.component';
import { UpdateUserPasswordComponent } from './update-user-password/update-user-password.component';
import { ForgotPasswordComponent } from './forgot-password/forgot-password.component';
import { NewPasswordComponent } from './new-password/new-password.component';
import { PassowrdResetComponent } from './passowrd-reset/passowrd-reset.component';
import { ResetPasswordComponent } from './reset-password/reset-password.component';


@NgModule({
  declarations: [
    AuthComponent,
    ResetPasswordComponent,
    NewPasswordComponent,
    PassowrdResetComponent,
    PageError500Component,
    PageError404Component,
    SignInComponent,
    AuthenticationOTPComponent,
    UpdateUserPasswordComponent,
    ForgotPasswordComponent],
  imports: [
    CommonModule,
    FormsModule,
    MaterialModule,
    AuthRoutingModule,

  ],
  providers: [
     { provide: HTTP_INTERCEPTORS, useClass:ErrorInterceptor, multi: true },
   // { provide: HTTP_INTERCEPTORS, useClass: ErrorInterceptor, multi: true },
  ],
})
export class AuthModule { }
