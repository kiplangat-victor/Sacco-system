import { Component, OnDestroy, OnInit } from '@angular/core';
import { Subscription } from 'rxjs';
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
  selector: 'app-massive-interest',
  templateUrl: './massive-interest.component.html',
  styleUrls: ['./massive-interest.component.scss']
})
export class MassiveInterestComponent implements OnInit ,OnDestroy{
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
    startdate: ["2023-08-26", Validators.required]
  });
  initFormData() {
    this.formData = this.fb.group({
      acid: [this.fmData.account_code],
      startdate: ["2023-08-26", Validators.required]
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
        .set("startDate", this.formData.controls.startdate.value);
      this.loanInterestApi.MassiveIntrest(this.params).pipe(takeUntil(this.destroy$)).subscribe(
        {
          next: (
            (res) => {
              console.log(res);
              this.loading = false;
              this.demandRes = res;
              this.resdata = this.demandRes;
              
              this.notificationAPI.alertSuccess(res.message);
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



