import { Component, OnInit } from '@angular/core';
import { TokenStorageService } from 'src/@core/AuthService/token-storage.service';
import { environment } from 'src/environments/environment';

@Component({
  selector: 'app-side-bar',
  templateUrl: './side-bar.component.html',
  styleUrls: ['./side-bar.component.scss']
})
export class SideBarComponent implements OnInit {
  role: any;
  sideBarColor: any;
  themeColors: any;
  link = `${environment.reportsAPI}/api/v1/dynamic/css/customcss.json`;
  authorized = true;

  userImg = 'assets/tt.png';
  status: any;
  address: any;

  constructor(
    private tokenService: TokenStorageService
  ) { }

  username: any;
  email: any;
  ngOnInit() {
    this.Authorize();
  }

  Authorize() {
    let currentUser = this.tokenService.getUser();
    this.role = currentUser.roles[0].name;
    this.username = currentUser.username;
    this.email = currentUser.email;
    this.status = currentUser.status;
    this.address = currentUser.address;
    console.log; this.role


    if (this.role == 'SUPERUSER') {
      this.authorized = true;
    }

    if (this.role == "ROLE_TELLER") {      this.authorized = false;
    }
    if (this.role == 'USER') {
      this.authorized = false;
    }
  }
}
