import { DOCUMENT } from '@angular/common';
import {
  Component,
  OnInit,
  Output,
  EventEmitter,
  Inject,
  HostListener,
} from '@angular/core';
import {
  MatSnackBar,
  MatSnackBarHorizontalPosition,
  MatSnackBarVerticalPosition,
} from '@angular/material/snack-bar';
import { Router } from '@angular/router';
import { AuthService } from 'src/@core/AuthService/auth.service';
import { TokenStorageService } from 'src/@core/AuthService/token-storage.service';
import { environment } from 'src/environments/environment';
import { AccountsService } from '../../Service/AccountsService/accounts/accounts.service';
import { GroupMembershipService } from '../../Service/GroupMembership/group-membership.service';
import { MembershipService } from '../../Service/Membership/membership.service';
import { Subject, Subscription } from 'rxjs';
import { TransactionExecutionService } from '../../pages/transaction-execution/transaction-execution.service';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';
import { SalaryTransactionsService } from '../../pages/salary-transactions/salary-transactions.service';
import { BatchSalariesService } from '../../pages/salary-transactions/batch-salaries/batch-salaries.service';
import { event } from 'jquery';
import { SaccoEntityService } from 'src/app/saccomanagement/pages/sacco-entity/sacco-entity.service';
import { takeUntil } from 'rxjs/internal/operators/takeUntil';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss'],
})
export class HeaderComponent implements OnInit {
  horizontalPosition: MatSnackBarHorizontalPosition = 'end';
  verticalPosition: MatSnackBarVerticalPosition = 'top';
  @Output() toggleSideBarForMe: EventEmitter<any> = new EventEmitter();
  elem: any;
  isFullScreen!: boolean;
  currentUser: any;
  message: any;
  sacconame: string;

  themeColors: any;
  logolink: string = `${environment.reportsAPI}/api/v1/dynamic/saccologo`;
  saccoLogo: string = '../../../../assets/PBlogo.png';
  defaultSaccoLogo: string = '../../../../assets/tt.png';
  systemDate: any;
  totalTransactions = 0;
  data: any;
  totalRetailMembers = 0;
  totalGroupMembers = 0;
  results: any;
  submitted: boolean = false;
  destroy$: Subject<boolean> = new Subject<boolean>();
  transData: any;
  username: any;
  email: any;
  loading: boolean = false;
  totalAccounts = 0;
  totalSalaries = 0;

  //customTitlebarBg = 'rgb(0, 77, 153)';

  //rgb(0, 77, 23)
  

  constructor(
    private _snackBar: MatSnackBar,

    private router: Router,
    private authService: AuthService,
    private accountsAPI: AccountsService,
    @Inject(DOCUMENT) private document: any,
    private membershipAPI: MembershipService,
    private tokenStorage: TokenStorageService,

    private batchSalariesAPI: BatchSalariesService,
    private salariesAPI: SalaryTransactionsService,

    private transactionAPI: TransactionExecutionService,
    private groupMembershipAPI: GroupMembershipService,

    private saccoEntityAPI: SaccoEntityService,
    private notificationAPI: NotificationService
  ) {
    this.currentUser = JSON.parse(localStorage.getItem('auth-user'));
  }

  ngOnInit() {
    this.getSaccoData();
    this.getRetailMembers();
    this.getGroupMembers();
    this.getMembersAccounts();
    this.getAccountTransactions();
    this.getTransactionSalaries();
    this.getBatchSalaries();
    this.elem = document.documentElement;
    this.systemDate = new Date();
    this.sacconame = this.tokenStorage.getSaccoName();
    this.themeColors = this.tokenStorage.getTheme();
    // if (this.themeColors) {
    //   this.customTitlebarBg = this.themeColors.customTitlebarBg;
    // }
  }

  // ngAfterViewInit(){
  //   this.getSaccoData();
  // }

  ngOnDestroy() {
    this.destroy$.next(true);
    this.destroy$.complete();
  }

  // defaultCustomTitlebarBg = "rgb(255, 255, 255)"
  defaultCustomTitlebarBg = "rgb(0, 71, 143)"

  saccoDataloading: boolean = true;
  saccoResults: any;
  getSaccoData() {

    this.loading = true;
    this.saccoEntityAPI
      .getUserSaccoDetails(this.currentUser.entityId)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (res) => {

          console.log("getUserSaccoDetails:: ", res)

          if (res.statusCode === 302) {
           
            this.results = res.entity;

            if (this.results.customTitlebarBg != null && this.results.customTitlebarBg !== undefined && this.results.customTitlebarBg.trim() !== '') {
              this.defaultCustomTitlebarBg = this.results.customTitlebarBg;
            }
           
            if (this.results.entityImageLogo != null && this.results.entityImageLogo !== undefined && this.results.entityImageLogo.trim() !== '') {
            this.saccoLogo = this.results.entityImageLogo;
            }

            console.log(
              'this.results.customSidebarBg:: ',
              this.results.customSidebarBg
            );

            console.log("this.results.customTitlebarBg:: ", this.results.customTitlebarBg);

            this.saccoDataloading = false;

          } else {
            this.defaultCustomTitlebarBg = "rgb(0, 71, 143)"
            this.saccoDataloading = false;
            this.notificationAPI.alertWarning(
              'Sacco Entity Details Not Found!!'
            );
          }
        },
        error: (err) => {
          this.defaultCustomTitlebarBg = "rgb(0, 71, 143)"
          this.saccoDataloading = false;
          this.notificationAPI.alertWarning(err.message);
        },
        complete: () => { },
      });

    // if (this.currentUser.entityId !== '20993') {
     
    // }

  }

  toggleSideBar() {
    this.toggleSideBarForMe.emit();
    setTimeout(() => {
      window.dispatchEvent(new Event('resize'));
    }, 300);
  }

  profile() {
    this.router.navigate([`/system/manage/user/profle/`], {
      skipLocationChange: true,
    });
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
    this.membershipAPI.getTotalMembers().subscribe({
      next: (res) => {
        if (res.statusCode === 200) {
          this.loading = false;
          this.totalRetailMembers = res.entity;
        } else {
          this.loading = false;
        }
      },
      error: (err) => {
        this.loading = false;
      },
      complete: () => { },
    }),
      Subscription;
  }

  getGroupMembers() {
    this.groupMembershipAPI.read().subscribe({
      next: (res) => {
        if (res.statusCode === 200) {
          this.loading = false;
          this.totalGroupMembers = res.entity;
        } else {
          this.loading = false;
        }
      },
      error: (err) => {
        this.loading = false;
      },
      complete: () => { },
    }),
      Subscription;
  }

  getMembersAccounts() {
    this.loading = true;
    this.accountsAPI.getTotalAccounts().subscribe({
      next: (res) => {
        if (res.statusCode == 200) {
          this.loading = false;
          this.totalAccounts = res.entity;
        } else {
        }
      },
      error: (err) => {
        this.loading = false;
      },
      complete: () => { },
    }),
      Subscription;
  }

  getAccountTransactions() {
    this.loading = true;
    this.transactionAPI.getTotalTransactions().subscribe({
      next: (res) => {
        if (res.statusCode === 200) {
          this.loading = false;
          this.totalTransactions = res.entity;
        } else {
          this.loading = false;
        }
      },
      error: (err) => {
        this.loading = false;
      },
      complete: () => { },
    }),
      Subscription;
  }

  getTransactionSalaries() {
    this.loading = true;
    this.salariesAPI.getTotalTransactions().subscribe({
      next: (res) => {
        if (res.statusCode === 200) {
          this.loading = false;
          this.totalSalaries = res.entity;
        } else {
          this.loading = false;
        }
      },
      error: (err) => {
        this.loading = false;
      },
      complete: () => { },
    }),
      Subscription;
  }

  getBatchSalaries() {
    this.loading = true;
    this.batchSalariesAPI.totalBatchTransactions().subscribe({
      next: (res) => {
        if (res.statusCode === 200) {
          this.loading = false;
          this.totalTransactions = res.entity;
        } else {
          this.loading = false;
        }
      },
      error: (err) => {
        this.loading = false;
      },
      complete: () => { },
    }),
      Subscription;
  }
}
