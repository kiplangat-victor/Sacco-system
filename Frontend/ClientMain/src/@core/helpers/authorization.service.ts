// import { Injectable } from '@angular/core';
// import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot } from '@angular/router';
// import { Observable } from 'rxjs';
// import { NotificationService } from './NotificationService/notification.service';

// @Injectable({
//   providedIn: 'root'
// })
// export class AuthorizationService implements CanActivate {

//   constructor(
//     private notificationApi: NotificationService,
//     private router : Router
//   ) { }

//   // Check if user is logged in
//   isLoggedIn(){
//     let currentUser = JSON.parse(sessionStorage.getItem('auth-user'));
//     if (currentUser  === null) {
//         this.notificationApi.alertWarning("You must sign in")
//       this.router.navigateByUrl('sso');
//       return false;
//     }else{
//        return true;
//     }
//   }

//   canActivate(next: ActivatedRouteSnapshot,
//     state: RouterStateSnapshot): any{
//     return this.isLoggedIn();
//   }

//   // has Priviledges
//   hasPermission(component): Observable<boolean> {
//     return this.authService.isLoggedIn().map(res => {
// // Get permissions from user object.
//         this.permissions = this.getPermissions();
// // Check if user object has permissions to access the current component.
//         return this.checkPermission(component.data.permission);
//         // route.data;
//     });
// }





// }
