import { Component, OnInit } from '@angular/core';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { TokenStorageService } from 'src/@core/AuthService/token-storage.service';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';
import { SaccoEntityService } from 'src/app/saccomanagement/pages/sacco-entity/sacco-entity.service';
import { environment } from 'src/environments/environment';

@Component({
  selector: 'app-sidebar',
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.scss']
})
export class SidebarComponent implements OnInit {
  role: any;
  sideBarColor: any;
  currentUser: any;

//rgb(0, 55, 20)


  themeColors: any;
  link = `${environment.reportsAPI}/api/v1/dynamic/css/customcss.json`
  authorized = true;
  status: any;
  userImg = 'assets/tt.png';
  forTeller = false


  destroy$: Subject<boolean> = new Subject<boolean>();


  constructor(
    private tokenStorage: TokenStorageService,
    private tokenService: TokenStorageService,
    private saccoEntityAPI: SaccoEntityService,
    private notificationAPI: NotificationService
  ) {
    this.currentUser = JSON.parse(localStorage.getItem('auth-user'))
    this.themeColors = this.tokenStorage.getTheme();
    if (this.themeColors != null) {
      console.log("log in sidebar");
      console.log(this.themeColors);
      //this.customSidebarBg = this.themeColors.customSidebarBg;
      // console.log(this.customSidebarBg);
    }
  }

  username: any;
  email: any;
  ngOnInit() {
    this.getSaccoData();
    this.Authorize();
  }

  // ngAfterViewInit(){
  //   this.getSaccoData();
  // }

  ngOnDestroy() {
    this.destroy$.next(true);
    this.destroy$.complete();
  }

  // defaultCustomSidebarBg = "rgb(255, 255, 255)"
  defaultCustomSidebarBg = "rgb(0, 71, 143)"

  loading: boolean = true;
  results: any;
  getSaccoData() {
    this.loading = true;
    this.saccoEntityAPI.getUserSaccoDetails(this.currentUser.entityId).pipe(takeUntil(this.destroy$)).subscribe(
      {
        next: (res) => {
          if (res.statusCode === 302) {
           
            this.results = res.entity;

            if (this.results.customSidebarBg != null && this.results.customSidebarBg !== undefined && this.results.customSidebarBg.trim() !== '') {

              this.defaultCustomSidebarBg = this.results.customSidebarBg;

            }

            console.log("this.results.customSidebarBg:: ", this.results.customSidebarBg);


            
            this.loading = false;
          } else {
            this.defaultCustomSidebarBg = "rgb(0, 71, 143)"
            this.loading = false;
            this.notificationAPI.alertWarning("Sacco Entity Details Not Found!!");
          }
        },
        error: (err) => {
          this.defaultCustomSidebarBg = "rgb(0, 71, 143)"
          this.loading = false;
          this.notificationAPI.alertWarning(err.message);
        },
        complete: () => {

        }
      }
    )
  }

  Authorize() {
    let currentUser = this.tokenService.getUser();
    console.log("currentUser", currentUser);
    this.role = currentUser.roles[0].name;
    this.username = currentUser.username;
    this.email = currentUser.email;
    this.status = currentUser.status;

    console.log("Role", this.role);

    if (this.role === "SUPERUSER" || this.role === "ROLE_SUPERUSER") {
      this.authorized = true;
    }

    if (this.role === "ROLE_TELLER") {
      this.authorized = false;
      this.forTeller = true
    }
    if (this.role == "USER") {
      this.authorized = false;
    }
  }
}
