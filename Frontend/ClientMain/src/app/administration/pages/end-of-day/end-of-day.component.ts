import { DatePipe } from '@angular/common';
import { HttpParams } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { MatSnackBarHorizontalPosition, MatSnackBarVerticalPosition, MatSnackBar } from '@angular/material/snack-bar';
import { Subscription } from 'rxjs';
import { DashboardService } from '../dashboard/dashboard.service';
import { EndOfDayService } from './end-of-day.service';

@Component({
  selector: 'app-end-of-day',
  templateUrl: './end-of-day.component.html',
  styleUrls: ['./end-of-day.component.scss'],
})
export class EndOfDayComponent implements OnInit {
  horizontalPosition: MatSnackBarHorizontalPosition = 'end';
  verticalPosition: MatSnackBarVerticalPosition = 'top';

  subscription!: Subscription;
  loading = false;
  eodInitated = false;

  systemDates: any;

  successfulEods: any[] = [];
  failedEods: any[] = [];

  intervalID: any;
  counter = 0;
  seconds = 5;

  step1 = false;
  step2 = false;
  step3 = false;
  step4 = false;
  step5 = false;
  step6 = false;
  step7 = false;
  step8 = false;
  step9 = false;
  step10 = false;
  step11 = false;

  usersLoading=false;

  constructor(
    private endOfDayAPI: EndOfDayService,
    private datepipe: DatePipe,
    private dashboardService: DashboardService,
    private _snackbar: MatSnackBar,
  ) {}

  ngOnInit(): void {
    this.fetchSystDate();
  }

  fetchSystDate() {
    this.dashboardService.getSystemDate().subscribe((res) => {
      this.systemDates = res.entity[0].systemDate;
    });
  }


  beginEndOfDay() {
    this.loading = true;

    this.subscription = this.endOfDayAPI.initiateEndOfDay().subscribe(
      (res) => {
        res.eodRes.forEach((element) => {
          if (element.status == true) {
            this.successfulEods.push(element);
          }
          if (element.status == false) {
            this.failedEods.push(element);
          }

          if (
            element.eodStep == 'HTD DATA BACKUP TO A FILE' &&
            element.status == true
          ) {
            this.step1 = true;
          }
          if (
            element.eodStep == 'DTD DATA BACKUP TO A FILE' &&
            element.status == true
          ) {
            this.step2 = true;
          }
          if (
            element.eodStep == 'SIGN OUT ALL USERS' &&
            element.status == true
          ) {
            this.step3 = true;
          }
          if (
            element.eodStep == 'PERFORM PRE-EOD DATABASE BACKUP' &&
            element.status == true
          ) {
            this.step4 = true;
          }
          if (
            element.eodStep == 'CHECK NOT POSTED TRANSACTIONS' &&
            element.status == true
          ) {
            this.step5 = true;
          }
          if (
            element.eodStep == 'CHECK UNVERIFIED TRANSACTIONS' &&
            element.status == true
          ) {
            this.step6 = true;
          }
          if (
            element.eodStep == 'CHECK SUM OF DEBITS & CREDITS IN DTD!' &&
            element.status == true
          ) {
            this.step7 = true;
          }
          if (
            element.eodStep == 'MOVE DATA FROM DTD TABLE TO HTD!' &&
            element.status == true
          ) {
            this.step8 = true;
          }
          if (
            element.eodStep == 'MOVE SYSTEM DATE TO THE NEXT WORKING DAY!' &&
            element.status == true
          ) {
            this.step9 = true;
          }
          if (
            element.eodStep ==
              'ENABLE USER ACCOUNTS AFTER SUCCESSFUL EOD PROCESS' &&
            element.status == true
          ) {
            this.step10 = true;
          }
          if (
            element.eodStep == 'PERFORM POST-EOD DATABASE BACKUP' &&
            element.status == true
          ) {
            this.step11 = true;
          }
        });

        if (res.eodStatus == true) {
        }

        // if (res) {
        //   this.intervalLoop();
        // }

        this.loading = false;
        this.eodInitated = true;
      },
      (err) => {}
    );
  }

  // intervalLoop() {
  //   this.intervalID = setInterval(() => {
  //     this.counter++;

  //     this.monitorEndOfDay();
  //   }, this.seconds * 1000);
  // }

  // monitorEndOfDay() {
  //   let params = new HttpParams().set(
  //     'systemDate',
  //     this.datepipe.transform(this.systemDates, 'yyyy-MM-dd HH:mm:ss')
  //   );
  //   this.subscription = this.endOfDayAPI.trackEndOfDay(params).subscribe(
  //     (res) => {
  //       console.log('Monitoring: ', res);

  //       if (res) {
  //         clearInterval(this.intervalID);
  //         clearTimeout(this.intervalID);
  //       }
  //     },
  //     (err) => { }
  //   );
  // }

  resetUserAccounts(){
    this.usersLoading = true;
    this.subscription = this.endOfDayAPI.enableUserAccounts().subscribe(
      (res) => {
        this.usersLoading = false;

        this._snackbar.open(res.message, 'X', {
          horizontalPosition: this.horizontalPosition,
          verticalPosition: this.verticalPosition,
          duration: 5000,
          panelClass: ['green-snackbar', 'login-snackbar'],
        });

      },
      (err) => {}
    );
  }
}
