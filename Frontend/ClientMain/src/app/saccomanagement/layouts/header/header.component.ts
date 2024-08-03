import { DOCUMENT } from '@angular/common';
import { Component, EventEmitter, HostListener, Inject, OnDestroy, OnInit, Output } from '@angular/core';
import { MatSnackBar, MatSnackBarHorizontalPosition, MatSnackBarVerticalPosition } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
import { Subject, Subscription } from 'rxjs';
import { AuthService } from 'src/@core/AuthService/auth.service';
import { TokenStorageService } from 'src/@core/AuthService/token-storage.service';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';
import { AccountsService } from 'src/app/administration/Service/AccountsService/accounts/accounts.service';
import { GroupMembershipService } from 'src/app/administration/Service/GroupMembership/group-membership.service';
import { MembershipService } from 'src/app/administration/Service/Membership/membership.service';
import { TransactionExecutionService } from 'src/app/administration/pages/transaction-execution/transaction-execution.service';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent implements OnInit , OnDestroy{
  @Output() toggleSideBarForMe: EventEmitter<any> = new EventEmitter();
  elem: any;
  isFullScreen!: boolean;
  currentUser: any;
  message: any;
  sacconame: string;
  customTitlebarBg = "#004d99"
  themeColors: any;
  loading: boolean;
  systemDate: any;
  totalAccounts = 0;
  totalTransactions = 0.0;
  data: any;
  username: any;
  email: any;
  totalRetailMembers = 0;
  totalGroupMembers = 0;
  totalMembership = 0;
  results: any;
  submitted: boolean = false;
  destroy$: Subject<boolean> = new Subject<boolean>();
  transData: any;
  horizontalPosition: MatSnackBarHorizontalPosition;
  verticalPosition: MatSnackBarVerticalPosition;

  constructor(
    @Inject(DOCUMENT) private document: any,
    private _snackBar: MatSnackBar,
    private router: Router,
    private tokenStorage: TokenStorageService,
    private authService: AuthService,
    private accountsAPI: AccountsService,
    private membershipAPI: MembershipService,
    private transactionAPI: TransactionExecutionService,
    private groupMembershipAPI: GroupMembershipService
  ) {
    this.currentUser = JSON.parse(localStorage.getItem('auth-user'));
    this.themeColors = this.tokenStorage.getTheme();
  }
  ngOnDestroy() {
    this.destroy$.next(true);
    this.destroy$.complete();
  }

  ngOnInit() {
    this.getAccounts();
    this.chkScreenMode();
    this.elem = document.documentElement;
    this.systemDate = new Date();
    this.sacconame = this.tokenStorage.getSaccoName();
  }
  toggleSideBar() {
    this.toggleSideBarForMe.emit();
    setTimeout(() => {
      window.dispatchEvent(
        new Event('resize')
      );
    }, 300);
  }

  getAccounts() {
    this.accountsAPI.read().subscribe(
      (res) => {
        this.data = res;
        this.totalAccounts = Object.keys(this.data.filter((data: { verifiedFlag: string; }) => data.verifiedFlag == 'N')).length;
      }
    )
  }
  profile() {
    this.router.navigate([`/system/manage/user/profle/`], { skipLocationChange: true });
  }
  
  logout() {
    this.authService.signout(this.currentUser.id).subscribe((res) => {
      this.message = res;
      if (this.message.statusCode == 200) {
        this.loading = false;
        this._snackBar.open('Logout Successful', 'X', {
          horizontalPosition: this.horizontalPosition,
          verticalPosition: this.verticalPosition,
          duration: 3000,
          panelClass: ['green-snackbar', 'login-snackbar'],
        });
        this.router.navigate(['/sso']);
      } else {
        this.loading = false;
        this._snackBar.open(this.message.message, 'X', {
          horizontalPosition: this.horizontalPosition,
          verticalPosition: this.verticalPosition,
          duration: 3000,
          panelClass: ['red-snackbar', 'login-snackbar'],
        });
      }
      this.router.navigate(['/sso']);
    });
    this.router.navigate(['/sso']);
  }


  @HostListener('document:fullscreenchange', ['$event'])
  @HostListener('document:webkitfullscreenchange', ['$event'])
  @HostListener('document:mozfullscreenchange', ['$event'])
  @HostListener('document:MSFullscreenChange', ['$event'])
  fullscreenmodes() {
    this.chkScreenMode();
  }
  chkScreenMode() {
    if (document.fullscreenElement) {
      //fullscreen
      this.isFullScreen = true;
    } else {
      //not in full screen
      this.isFullScreen = false;
    }
  }
  openFullscreen() {
    if (this.elem.requestFullscreen) {
      this.elem.requestFullscreen();
    } else if (this.elem.mozRequestFullScreen) {
      // Firefox /
      this.elem.mozRequestFullScreen();
    } else if (this.elem.webkitRequestFullscreen) {
      // Chrome, Safari and Opera /
      this.elem.webkitRequestFullscreen();
    } else if (this.elem.msRequestFullscreen) {
      // IE/Edge /
      this.elem.msRequestFullscreen();
    }
  }
  // Close fullscreen /
  closeFullscreen() {
    if (this.document.exitFullscreen) {
      this.document.exitFullscreen();
    } else if (this.document.mozCancelFullScreen) {
      // Firefox /
      this.document.mozCancelFullScreen();
    } else if (this.document.webkitExitFullscreen) {
      // Chrome, Safari and Opera /
      this.document.webkitExitFullscreen();
    } else if (this.document.msExitFullscreen) {
      // IE/Edge /
      this.document.msExitFullscreen();
    }
  }
  getRetailMembers() {
    this.loading = true;
    this.membershipAPI.getTotalMembers().subscribe(
      {
        next: (
          (res) => {
            if (res.statusCode === 200) {
              this.loading = false;
              this.totalRetailMembers = res.entity;
            } else {
              this.loading = false;
              this.totalRetailMembers = 0;
            }
          }
        ), error: (
          (err) => {
            this.loading = false;
            this.totalRetailMembers = 0;
          }
        ), complete: (
          () => {

          }
        )
      }
    ), Subscription;
  }
  getGroupMembers() {
    this.groupMembershipAPI.read().subscribe(
      {
        next: (
          (res) => {
            if (res.statusCode === 302) {
              this.loading = false;
              this.data = res.entity;
              this.totalGroupMembers = Object.keys(this.data.filter((data: { verifiedFlag: string; }) => data.verifiedFlag == 'N')).length;

            } else {
              this.loading = false;
              this.totalGroupMembers = 0;
            }
          }
        ), error: (
          (err) => {
            this.loading = false;
            this.totalGroupMembers = 0;
          }
        ), complete: (
          () => {

          }
        )
      }
    ), Subscription;
  }

  getMembersAccounts() {
    this.loading = true;
    this.accountsAPI.getTotalAccounts().subscribe(
      {
        next: (
          (res) => {
            // if (res.statusCode === 200) {
            //   this.loading = false;
            //   this.totalAccounts = res.entity;
            // } else {
            //   this.loading = false;
            //   this.totalAccounts = 0;
            // }
          }
        ), error: (
          (err) => {
            this.loading = false;
            this.totalAccounts = 0;
          }
        ), complete: (
          () => {

          }
        )
      }
    ), Subscription;
  }
  getAccountTransactions() {
    this.loading = true;
    this.transactionAPI.getTotalTransactions().subscribe(
      {
        next: (
          (res) => {
            // if (res.statusCode === 200) {
            //   this.loading = false;
            //   this.totalTransactions = res.entity + 0.00;
            // } else {
            //   this.loading = false;
            //   this.totalTransactions = 0.00;
            // }
          }
        ), error: (
          (err) => {
            this.loading = false;
            this.totalTransactions = 0.00;
          }
        ), complete: (
          () => {

          }
        )
      }
    ), Subscription;
  }
  getTransactionSalaries() {
    this.loading = true;
    this.transactionAPI.getTotalTransactions().subscribe(
      {
        next: (
          (res) => {
            // if (res.statusCode === 200) {
            //   this.loading = false;
            //   this.totalTransactions = res.entity + 0.00;
            // } else {
            //   this.loading = false;
            //   this.totalTransactions = 0.00;
            // }
          }
        ), error: (
          (err) => {
            this.loading = false;
            this.totalTransactions = 0.00;
          }
        ), complete: (
          () => {

          }
        )
      }
    ), Subscription;
  }
  getBatchSalaries() {
    this.loading = true;
    this.transactionAPI.getTotalTransactions().subscribe(
      {
        next: (
          (res) => {
            // if (res.statusCode === 200) {
            //   this.loading = false;
            //   this.totalTransactions = res.entity + 0.00;
            // } else {
            //   this.loading = false;
            //   this.totalTransactions = 0.00;
            // }
          }
        ), error: (
          (err) => {
            this.loading = false;
            this.totalTransactions = 0.00;
          }
        ), complete: (
          () => {

          }
        )
      }
    ), Subscription;
  }
}
