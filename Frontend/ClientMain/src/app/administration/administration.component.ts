import { Component, HostListener, OnInit } from '@angular/core';
import { TokenStorageService } from 'src/@core/AuthService/token-storage.service';
import { ReportsService } from './Service/reports/reports.service';
import { AutoLogoutService } from './Service/AutoLogout/auto-logout.service';
import { takeUntil } from 'rxjs/operators';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';
import { SaccoEntityService } from '../saccomanagement/pages/sacco-entity/sacco-entity.service';
import { Subject } from 'rxjs';

@Component({
  selector: 'app-administration',
  templateUrl: './administration.component.html',
  styleUrls: ['./administration.component.scss'],
})
export class AdministrationComponent implements OnInit {
  title = 'EMT_001_SACCO_SOLUTION';
  elem: any;

  //customSidebarBg="linear-gradient(to right, #0a3466, #206ab7)";
  //rgb(0, 77, 23)

  themeColors: any;
  currentUser: any;
  sideBarOpen = true;
  autoLogOut: AutoLogoutService;

  destroy$: Subject<boolean> = new Subject<boolean>();

  constructor(
    private tokenStorage: TokenStorageService,
    private reportsService: ReportsService,
    private autoLogOutService: AutoLogoutService,

    private saccoEntityAPI: SaccoEntityService,
    private notificationAPI: NotificationService
  ) {
    // this.settheme();
    // this.loadSaccoThemes();
    this.autoLogOut = autoLogOutService; // Assign it to autoLogOut
    this.currentUser = JSON.parse(localStorage.getItem('auth-user'));
  }

  ngOnInit(): void {
    this.getSaccoData();
    this.elem = document.documentElement;
  }

  ngOnDestroy() {
    this.destroy$.next(true);
    this.destroy$.complete();
  }

  // rgb(255, 255, 255)
  // defaultCustomSidebarBg = 'rgb(255, 255, 255)';

  defaultCustomSidebarBg = 'rgb(0, 71, 143)';

  loading: boolean = true;
  results: any;
  getSaccoData() {
    this.loading = true;
    this.saccoEntityAPI
      .getUserSaccoDetails(this.currentUser.entityId)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (res) => {
          if (res.statusCode === 302) {
            this.loading = false;
            this.results = res.entity;

            if (
              this.results.customSidebarBg != null &&
              this.results.customSidebarBg !== undefined &&
              this.results.customSidebarBg.trim() !== ''
            ) {
              this.defaultCustomSidebarBg = this.results.customSidebarBg;
            }

            console.log(
              'this.results.customSidebarBg:: ',
              this.results.customSidebarBg
            );
          } else {
            this.defaultCustomSidebarBg = 'rgb(0, 71, 143)';
            this.loading = false;
            this.notificationAPI.alertWarning(
              'Sacco Entity Details Not Found!!'
            );
          }
        },
        error: (err) => {
          this.defaultCustomSidebarBg = 'rgb(0, 71, 143)';
          this.loading = false;
          this.notificationAPI.alertWarning(err.message);
        },
        complete: () => {},
      });
  }

  // settheme(){
  //   this.themeColors = this.tokenStorage.getTheme();
  //   if(this.themeColors) {
  //     this.customSidebarBg = this.themeColors.customSidebarBg;
  //   }
  // }
  sideBarToggler() {
    this.sideBarOpen = !this.sideBarOpen;
  }

  @HostListener('document:fullscreenchange', ['$event'])
  @HostListener('document:webkitfullscreenchange', ['$event'])
  @HostListener('document:mozfullscreenchange', ['$event'])
  @HostListener('document:MSFullscreenChange', ['$event'])
  openFullscreen() {
    if (this.elem.requestFullscreen) {
      this.elem.exitFullscreen();
    } else if (this.elem.mozRequestFullScreen) {
      /* Firefox */
      this.elem.mozRequestFullScreen();
    } else if (this.elem.webkitRequestFullscreen) {
      /* Chrome, Safari and Opera */
      this.elem.webkitRequestFullscreen();
    } else if (this.elem.msRequestFullscreen) {
      /* IE/Edge */
      this.elem.msRequestFullscreen();
    }
  }

  loadSaccoThemes() {
    // this.reportsService.saccotheme().subscribe (
    //   (res) => {
    //     if(res.statusCode == 200) {
    //       this.tokenStorage.saveTheme(res.entity);
    //       this.settheme();
    //     }
    //   },
    // )
  }
}
