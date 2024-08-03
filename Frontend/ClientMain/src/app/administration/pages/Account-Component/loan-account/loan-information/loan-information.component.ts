import { HttpParams } from '@angular/common/http';
import { DatePipe } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';
import { LoanScheduleService } from 'src/app/administration/Service/AccountsService/Loans/LoanSchedule/loan-schedule.service';
import { LoansAccountsLookUpComponent } from '../loans-accounts-look-up/loans-accounts-look-up.component';
import { DataStoreService } from 'src/@core/helpers/data-store.service';

@Component({
  selector: 'app-loan-information',
  templateUrl: './loan-information.component.html',
  styleUrls: ['./loan-information.component.scss']
})
export class LoanInformationComponent implements OnInit {
  error: any;
  date: any = new Date();
  data: any;
  notBooked: any;
  results: any;
  loading = false;
  onDisplay = true;
  onEnabled = false;
  demandEvents: any;
  total_bookings: any;
  generatedEventData: any;
  generation_date: any = new Date();
  onDisplayGeneration = false;
  onDisplaySatisfaction = false;
  onDisplayLoansNotBooked = false;
  onDisplayLoansNotAccrued = false;
  onDisplayLoansDemands = false;
  not_accrued: any;
  total_accrual: any;
  total_demands: any;
  not_demanded: any;
  demand_res: any;
  onDisplayDisbursementInfor = false;
  submitted = false;
  lookupData: any;
  account_id: any;
  params: HttpParams;
  disbursment: any;
  constructor(
    public fb: FormBuilder,
    private dialog: MatDialog,
    private datepipe: DatePipe,
    private dataStoreApi: DataStoreService,
    private loanResouseAPI: LoanScheduleService,
    private notificationAPI: NotificationService,
  ) { }
  formData = this.fb.group({
    account_code: ['', Validators.required]
  });
  get f() {
    return this.formData.controls;
  }
  ngOnInit(): void {
    this.fetchUnsatisfiedDemands();
    this.date = this.datepipe.transform(new Date(), "yyyy-MM-dd");
  }
  loanDateEvent(event: any) {
    this.date = this.datepipe.transform(event.target.value, "yyyy-MM-dd");
  }
  fetchUnsatisfiedDemands() {
    this.loading = true;
    this.loanResouseAPI.unsatisfiedDemands().subscribe(
      (res) => {
        this.results = res;
        this.demand_res = this.results.entity;
        this.loading = false;
      }, err => {
        this.error = err;
        this.loading = false;
        this.notificationAPI.alertWarning(this.error);
      }
    )
  }

  getDemands() {
    this.loading = true;
    this.loanResouseAPI.demandEvent(this.date).subscribe(
      response => {
        if (response.status === 200 || response.statusCode == 302) {
          this.loading = false;
          this.data = response;
          this.demandEvents = this.data.entity;
          this.onDisplaySatisfaction = true;
        } else {
          this.loading = false;
          this.data = response;
          this.notificationAPI.alertWarning(this.data.message);
          this.onDisplaySatisfaction = false;
        }
      },
      err => {
        this.error = err;
        this.loading = false;
        this.notificationAPI.alertWarning(this.error);
      }
    )
  }
  generateDemands() {
    this.loading = true;
    this.loanResouseAPI.generatedEvent(this.date).subscribe(
      res => {
        if (res.status === 200 || res.statusCode == 302) {
          this.loading = false;
          this.data = res;
          this.generatedEventData = this.data.entity;
          this.onDisplayGeneration = true;
        } else {
          this.loading = false;
          this.data = res;
          this.notificationAPI.alertWarning(this.data.message);
          this.onDisplayGeneration = false;
        }
      },
      err => {
        this.error = err;
        this.loading = false;
        this.notificationAPI.alertWarning(this.error);
      }
    )
  }

  getNotBookedEvents() {
    this.loading = true;
    this.loanResouseAPI.notBookedEvents(this.date).subscribe(
      (res) => {
        this.loading = false;
        this.results = res;
        this.notBooked = this.results.entity;
        this.onDisplayLoansNotBooked = true;
      }, (err) => {
        this.loading = false;
        this.error = err;
        this.notificationAPI.alertWarning(this.error);
      }
    )
  }
  getTotalBookings() {
    this.loading = true;
    this.loanResouseAPI.totalBookings(this.date).subscribe(
      (res) => {
        this.results = res;
        this.onEnabled = true;
        this.onDisplay = false;
        this.loading = false;
        this.total_bookings = this.results.entity;
        this.onDisplayLoansNotBooked = false;
      }, (err) => {
        this.loading = false;
        this.error = err;
        this.notificationAPI.alertWarning(this.error);
      }
    )
  }
  getNotAccruedEvents() {
    this.loading = true;
    this.loanResouseAPI.notAccruedEvents(this.date).subscribe(
      (res) => {
        this.loading = false;
        this.results = res;
        this.not_accrued = this.results.entity;
        this.onDisplayLoansNotAccrued = true;
      }, (err) => {
        this.loading = false;
        this.error = err;
        this.notificationAPI.alertWarning(this.error);
      }
    )
  }
  getTotalAccruals() {
    this.loading = true;
    this.loanResouseAPI.totalAccrual(this.date).subscribe(
      (res) => {
        this.results = res;
        this.onEnabled = true;
        this.onDisplay = false;
        this.loading = false;
        this.total_accrual = this.results.entity;
        this.onDisplayLoansNotAccrued = false;
      }, (err) => {
        this.loading = false;
        this.error = err;
        this.notificationAPI.alertWarning(this.error);
      }
    )
  }
  getNotDemandedEvents() {
    this.loading = true;
    this.loanResouseAPI.notDemandedEvents(this.date).subscribe(
      (res) => {
        this.loading = false;
        this.results = res;
        this.not_demanded = this.results.entity;
        this.onDisplayLoansDemands = true;
      }, (err) => {
        this.loading = false;
        this.error = err;
        this.notificationAPI.alertWarning(this.error);
      }
    )
  }
  getTotalADemands() {
    this.loading = true;
    this.loanResouseAPI.totalDemanded(this.date).subscribe(
      (res) => {
        this.results = res;
        this.onEnabled = true;
        this.onDisplay = false;
        this.loading = false;
        this.total_demands = this.results.entity;
        this.onDisplayLoansDemands = false;
      }, (err) => {
        this.loading = false;
        this.error = err;
        this.notificationAPI.alertWarning(this.error);
      }
    )
  }
  loansAccountLookUp(): void {
    const dialogRef = this.dialog.open(LoansAccountsLookUpComponent, {
      width: '45%',
      autoFocus: false,
      maxHeight: '90vh',
    });
    dialogRef.afterClosed().subscribe(result => {
      this.lookupData = result.data;
      this.account_id = this.lookupData.acid;
      this.formData.controls.account_code.setValue(this.account_id);
    });
  }
  getDisbursmentInfor() {
    this.submitted = true;
    this.loading = true;
    this.params = new HttpParams().set("acid", this.formData.controls.account_code.value);
    this.loanResouseAPI.retrieveDisursment(this.params).subscribe(
      (res) => {
        this.loading = false;
        this.disbursment = res.entity;
        this.onDisplayDisbursementInfor = true;

      }, err => {
        this.loading = false;
        this.error = err;
        this.onDisplayDisbursementInfor = false;
        this.notificationAPI.alertWarning(this.error);
      }
    )
  }
}

