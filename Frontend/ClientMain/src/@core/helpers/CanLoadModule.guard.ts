import { CanLoad, Route, Router } from '@angular/router';
import { Injectable } from '@angular/core';
import { NotificationService } from './NotificationService/notification.service';
import { TokenStorageService } from '../AuthService/token-storage.service';
@Injectable()
export class CanLoadModuleGuard implements CanLoad {
  constructor(
    private router: Router,
    private tokenStorageService: TokenStorageService,
    private notificationService : NotificationService
  ) {
  }
  canLoad(route: Route) {
    let currentUser =   this.tokenStorageService.getUser();
    if (currentUser) {
      return true;
    }
    this.notificationService.alertWarning("Kindly Sign in!");
    this.router.navigateByUrl('sso');
    return false;

  }
}
