import { Injectable } from '@angular/core';
import {
  CanActivate,
  ActivatedRouteSnapshot,
  RouterStateSnapshot,
} from '@angular/router';
import { DataStoreService } from './data-store.service';
import { NotificationService } from './NotificationService/notification.service';

@Injectable({ providedIn: 'root' })
export class CanActivateModuleGuard implements CanActivate {
  compPermision: any;
  permission: Set<string>;
  actionPermission: Set<String>;
  constructor(
    private notificationAPI: NotificationService,
    private dataStoreService: DataStoreService
  ) {}
  // ACCOUNTS
  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
    this.compPermision = route.data;
    this.permission = this.dataStoreService.getPriviledges();
    console.log('User Permissions:', this.permission);
    console.log("compPermision", this.compPermision.permission);

    if (this.permission.has(this.compPermision.permission)) {
console.log('Required Permission:', this.compPermision.permission);

      return true;
    }else {
      // this.notificationAPI.alertWarning('Unauthorized');
      return false;
    }
  }
}
