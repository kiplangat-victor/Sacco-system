import { NgModule, Component } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AuthComponent } from './auth.component';
import { AuthenticationOTPComponent } from './authentication-otp/authentication-otp.component';
import { ForgotPasswordComponent } from './forgot-password/forgot-password.component';
import { PageError404Component } from './page-error404/page-error404.component';
import { SignInComponent } from './sign-in/sign-in.component';
import { ResetPasswordComponent } from './reset-password/reset-password.component';
import { NewPasswordComponent } from './new-password/new-password.component';


const routes: Routes = [
  {
  path: '',
  component: AuthComponent,
  children: [
    {
      path: '',
      component: SignInComponent,
      pathMatch: 'full'
    },
    {
      path: 'signin',
      component: SignInComponent
    },
    {
      path: 'otp/verification',
      component: AuthenticationOTPComponent
    },
    {
      path: 'reset_password',
      component: ResetPasswordComponent,
    },
    {
      path: 'set_new_password/token/:{token}',
      component: NewPasswordComponent,
    },
    {
      path: 'forgot/password',
      component: ForgotPasswordComponent
    }
  ]
  },
  { path: "**", component: PageError404Component }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class AuthRoutingModule { }
