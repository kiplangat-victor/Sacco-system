import { Subscription } from 'rxjs';
import { Component, OnInit, OnDestroy } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';
import { AccountsNotificationService } from 'src/@core/helpers/accounts/accounts-notification.service';
import { AccountsService } from 'src/app/administration/Service/AccountsService/accounts/accounts.service';
import { LoanScheduleService } from 'src/app/administration/Service/AccountsService/Loans/LoanSchedule/loan-schedule.service';
import { HttpParams } from '@angular/common/http';

@Component({
  selector: 'app-force-loan-demands',
  templateUrl: './force-loan-demands.component.html',
  styleUrls: ['./force-loan-demands.component.scss']
})
export class ForceLoanDemandsComponent implements OnInit, OnDestroy {
  loading: boolean = false;
  onShowResults = false;
  currentUser: any;
  fmData: any;
  function_type: any;
  account_code: any;
  destroy$: Subject<boolean> = new Subject<boolean>();
  account_type: any;
  showAccountCode: boolean = true;
  results: any;
  submitted = false;
  params: HttpParams;
  demandRes: any;
  resdata: any;

  constructor(
    public fb: FormBuilder,
    private router: Router,
    private accountsAPI: AccountsService,
    private notificationAPI: NotificationService,
    private loanInterestApi: LoanScheduleService,
    private accountsNotification: AccountsNotificationService
  ) {
    this.currentUser = JSON.parse(localStorage.getItem('auth-user'));
    this.fmData = this.router.getCurrentNavigation().extras.queryParams.formData;
    this.function_type = this.fmData.function_type;
    this.account_code = this.fmData.account_code;
  }
  ngOnDestroy(): void {
    this.destroy$.next(true);
    this.destroy$.complete();
  }
  ngOnInit(): void {
    this.getData();
    this.initFormData();

  }
  formData = this.fb.group({
    acid: ["", Validators.required],
    daysAhead: ["0", Validators.required]
  });
  initFormData() {
    this.formData = this.fb.group({
      acid: [this.fmData.account_code],
      daysAhead: ["0", Validators.required]
    });
  }
  get f() {
    return this.formData.controls;
  }
  getData() {
    this.loading = true;
    this.accountsAPI.retrieveAccount(this.fmData.account_code).pipe(takeUntil(this.destroy$)).subscribe(
      {
        next: (
          (res) => {
            if (res.statusCode === 302) {
              this.loading = false;
              this.results = res.entity;
            } else {
              this.loading = false;
              this.notificationAPI.alertWarning("No Loan Data for Account " + this.fmData.account_code + " Found: !!");
              this.router.navigate([`system/loan-account/maintenance`], { skipLocationChange: true });
            }
          }
        ),
        error: (
          (err) => {
            this.loading = false;
            this.notificationAPI.alertWarning("Server Error: !!");
            this.router.navigate([`system/loan-account/maintenance`], { skipLocationChange: true });

          }
        ),
        complete: (
          () => {

          }
        )
      }
    ), Subscription;
  }
  onSubmit() {
    this.loading = true;
    this.submitted = true;
    if (this.formData.valid) {
      this.params = new HttpParams()
        .set("acid", this.fmData.account_code)
        .set("daysAhead", this.formData.controls.daysAhead.value);
      this.loanInterestApi.loanDemands(this.params).pipe(takeUntil(this.destroy$)).subscribe(
        {
          next: (
            (res) => {
              this.loading = false;
              this.demandRes = res;
              this.resdata = this.demandRes;
              for (let i = 0; i < this.resdata.length; i++) {
                this.notificationAPI.alertSuccess(this.resdata[i].message);
              }
            }
          ),
          error: (
            (err) => {
              this.loading = false;
              this.notificationAPI.alertWarning("Server Error: !!");
            }
          ),
          complete: (
            () => {

            }
          )
        }
      ), Subscription;
    }
  }
}
