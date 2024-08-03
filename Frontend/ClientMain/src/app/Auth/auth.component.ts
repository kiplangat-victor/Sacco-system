import { Component, OnDestroy, OnInit } from '@angular/core';
import { NavigationEnd, Router } from '@angular/router';
import { Subject, Subscription } from 'rxjs';
import { filter } from "rxjs/operators";
import { AuthService } from 'src/@core/AuthService/auth.service';
import { TokenStorageService } from 'src/@core/AuthService/token-storage.service';
import { User } from 'src/@core/Models/user/user.model';
import { environment } from 'src/environments/environment';
import { ReportsService } from '../administration/Service/reports/reports.service';
@Component({
  selector: 'app-auth',
  templateUrl: './auth.component.html',
  styleUrls: ['./auth.component.scss']
})
export class AuthComponent implements  OnInit, OnDestroy {
  currentUser: User = new User;
 HEAD
  
  authimage: string =  `${environment.reportsAPI}/api/v1/dynamic/authimage`;

  // authimage: string = `${environment.reportsAPI}/api/v1/dynamic/authimage`;
  destroy$: Subject<boolean> = new Subject<boolean>();
  constructor(
      private router: Router,
      private authService: AuthService,
      private tokenStorage: TokenStorageService,
      private reportsService: ReportsService,
  ) {
    this.loadSaccoThemes();
    this.authService.currentUser.subscribe(x => this.currentUser = x);
  }

  subscription: Subscription = new Subscription;
  ngOnInit(): void {
    this.subscription = this.router.events.pipe(
      filter(event => event instanceof NavigationEnd)
    )
      .subscribe(() => window.scrollTo(0, 0));
  }

  ngOnDestroy() {
    this.subscription.unsubscribe();
  }

  loadSaccoThemes() {
    this.reportsService.saccotheme().subscribe (
      (res) => {
        console.log(res);
        if(res.statusCode == 200) {
          console.log("Saving theme in auth");
          this.tokenStorage.saveTheme(res.entity);
        }
      }
    )
}

  logout() {
      this.authService.logout();
      this.router.navigate(['/Auth/login']);
  }
}
