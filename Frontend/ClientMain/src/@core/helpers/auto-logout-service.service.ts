
import { Router } from '@angular/router';
import { Injectable, NgZone } from '@angular/core';
import * as store from 'store';
import { MatSnackBar, MatSnackBarHorizontalPosition, MatSnackBarVerticalPosition } from '@angular/material/snack-bar';

const MINUTES_UNITL_AUTO_LOGOUT = 1 // in Minutes
const CHECK_INTERVALL = 100 // in ms
const STORE_KEY = 'lastAction';

@Injectable()
export class AutoLogoutService {
  horizontalPosition: MatSnackBarHorizontalPosition = 'end';
  verticalPosition: MatSnackBarVerticalPosition = 'top';
  constructor(
    private _snackBar: MatSnackBar,
    private router: Router,
    private ngZone: NgZone
  ) {
    this.check();
    this.initListener();
    this.initInterval();
  }

  get lastAction() {
    return parseInt(store.get(STORE_KEY));
  }
  set lastAction(value) {
    store.set(STORE_KEY, value);
  }

  initListener() {
    this.ngZone.runOutsideAngular(() => {
      document.body.addEventListener('click', () => this.reset());
    });
  }

  initInterval() {
    this.ngZone.runOutsideAngular(() => {
      setInterval(() => {
        this.check();
      }, CHECK_INTERVALL);
    })
  }

  reset() {
    this.lastAction = Date.now();
  }

  check() {
    console.log("check");

    const now = Date.now();

    const timeleft = this.lastAction + (MINUTES_UNITL_AUTO_LOGOUT * 60 * 1);
    const diff = timeleft - now;
    const isTimeout = diff < 0;

    this.ngZone.run(() => {
      let currentUser = JSON.parse(sessionStorage.getItem('auth-user'));
          // check timeout and toke
      if(diff<6000 && currentUser !== null){
        console.log("Should Logout");
        this._snackBar.open('The system sign you out after '+ MINUTES_UNITL_AUTO_LOGOUT + ' '+'Min of Inactivity', 'X', {
          horizontalPosition: this.horizontalPosition,
          verticalPosition: this.verticalPosition,
          duration: 5000000,
          panelClass: ['red-snackbar', 'login-snackbar'],
        });
        if (isTimeout) {
          // clear token
         // remove user from local storage to log user out
            sessionStorage.removeItem('currentUser');
            window.sessionStorage.clear();
            this.router.navigate(['/']);
        }
      }
    });
  }
}
