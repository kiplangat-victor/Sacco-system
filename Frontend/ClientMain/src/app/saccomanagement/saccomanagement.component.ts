import { Component, HostListener, OnDestroy, OnInit } from '@angular/core';
import { TokenStorageService } from 'src/@core/AuthService/token-storage.service';
import { ReportsService } from '../administration/Service/reports/reports.service';
import { Subject } from 'rxjs';

@Component({
  selector: 'app-saccomanagement',
  templateUrl: './saccomanagement.component.html',
  styleUrls: ['./saccomanagement.component.scss']
})
export class SaccomanagementComponent implements OnInit, OnDestroy {
  title = 'Appclient';
  elem: any;
  customSidebarBg = "#004d99"
  themeColors: any;
  sideBarOpen = true;
  destroy$: Subject<boolean> = new Subject<boolean>()
  constructor(
    private tokenStorage: TokenStorageService,
    private reportsService: ReportsService,) {
    // this.settheme();
    this.loadSaccoThemes();
  }
  ngOnDestroy(): void {
    this.destroy$.next(true);
    this.destroy$.complete();
  }
  ngOnInit(): void {
    this.elem = document.documentElement;
    //this.openFullscreen();
  }

  // settheme() {
  //   this.themeColors = this.tokenStorage.getTheme();
  //   if (this.themeColors) {
  //     console.log("log in header");
  //     console.log(this.themeColors);
  //     this.customSidebarBg = this.themeColors.customSidebarBg;
  //     console.log(this.customSidebarBg);
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
    // this.reportsService.saccotheme().subscribe(
    //   (res) => {
    //     console.log(res);
    //     if (res.statusCode == 200) {
    //       console.log("Saving");
    //       this.tokenStorage.saveTheme(res.entity);
    //       this.settheme();
    //     }
    //   },
      // (err)=>{
      //   console.log(err);
      // }
     // )
  }
}
