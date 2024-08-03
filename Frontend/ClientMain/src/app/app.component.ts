import { Component, OnDestroy, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { AuthService } from 'src/@core/AuthService/auth.service';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit, OnDestroy {
  title = 'EMT_001_Sacco_Solution';
  elem: any;
  sideBarOpen = true;
  destroy$: Subject<boolean> = new Subject<boolean>();
  currentUser: any;
  loading: boolean = false;
  currentDate = new Date();
  constructor(
    private router: Router,
    private authService: AuthService,
    private notificationAPI: NotificationService
  ) {
    this.currentUser = JSON.parse(localStorage.getItem('auth-user'));
  }
  ngOnDestroy(): void {
    this.destroy$.next(true);
    this.destroy$.complete();
  }
  ngOnInit(): void {
    localStorage.removeItem('auth-user');
    this.authService.logout();
    this.router.navigate(['/sso']);
    // this.notificationAPI.alertSuccess("YOUR SESSION EXPIRED AT " + this.currentDate + " PLEASE LOGIN TO CONTINUE");
  }
}
