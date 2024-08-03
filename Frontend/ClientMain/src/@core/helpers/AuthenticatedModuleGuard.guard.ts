// import { CanLoad, Route, Router } from '@angular/router';
// import { Injectable } from '@angular/core';
// import { MatSnackBar, MatSnackBarHorizontalPosition, MatSnackBarVerticalPosition } from '@angular/material/snack-bar';
// import { AuthService } from '../AuthService/auth.service';
// @Injectable()
// export class AuthenticatedModuleGuard implements CanLoad {
//   routeRole: any;
//   horizontalPosition: MatSnackBarHorizontalPosition = 'end';
//   verticalPosition: MatSnackBarVerticalPosition = 'top';
//   constructor(
//     private router: Router,
//     private _snackBar: MatSnackBar,
//     private authService: AuthService
//   ) {
//   }
//   canLoad(route: Route) {
//     let currentUser = JSON.parse(sessionStorage.getItem('auth-user'));
//     if (currentUser) {
//       return true;
//     }
//     this._snackBar.open("Kindly Signin!", "X", {
//       horizontalPosition: this.horizontalPosition,
//       verticalPosition: this.verticalPosition,
//       duration: 5000,
//       panelClass: ['red-snackbar', 'login-snackbar'],
//     });
//     this.router.navigateByUrl('sso');
//     return false;
//   }
// }

