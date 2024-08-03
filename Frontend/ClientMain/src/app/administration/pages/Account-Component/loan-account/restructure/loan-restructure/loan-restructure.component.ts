import { DatePipe } from '@angular/common';
import { HttpParams } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';
import { LoanScheduleService } from 'src/app/administration/Service/AccountsService/Loans/LoanSchedule/loan-schedule.service';

@Component({
  selector: 'app-loan-restructure',
  templateUrl: './loan-restructure.component.html',
  styleUrls: ['./loan-restructure.component.scss']
})
export class LoanRestructureComponent implements OnInit {
  loanFrequency: any = ['MONTHS', 'YEARS'];
  error: any;
  date: any = new Date();
  data: any;
  loading = false;
  demandEvents: any;
  generatedEventData: any;
  generation_date: any = new Date();
  onDisplayGeneration = false;
  onDisplaySatisfaction = false;
  fmData: any;
  function_type: any;
  account_code: any;
  fetchData: any;
  onShowWarning = false;
  submitted = false;
  params: HttpParams;
  results: any;
  constructor(
    private router: Router,
    private fb: FormBuilder,
    private datepipe: DatePipe,
    private loanAPI: LoanScheduleService,
    private notificationAPI: NotificationService,
  ) {
    this.fmData = this.router.getCurrentNavigation().extras.queryParams.formData;
    this.function_type = this.fmData.function_type;
    this.account_code = this.fmData.account_code;
    this.fetchData = this.router.getCurrentNavigation().extras.queryParams.fetchedData;
  }
  formData: FormGroup = this.fb.group({
    acid: [''],
    freqPeriod: ['', Validators.required],
    installmentFreq: ['', Validators.required],
    installmentsNumber: ['', Validators.required],
    installmentStartDate: [new Date()],
  });
  onInitFormData() {
    this.formData = this.fb.group({
      acid: [this.fmData.account_code],
      freqPeriod: ['1'],
      installmentFreq: ['MONTHS'],
      installmentsNumber: [''],
      installmentStartDate: [this.datepipe.transform(this.formData.controls.installmentStartDate.value, "yyyy-MM-dd")],
    });
  }
  ngOnInit(): void {
    this.onInitFormData();
  }
  get f() {
    return this.formData.controls;
  }
  onSubmit() {
    this.submitted = true;
    this.loading = true;
    this.params = new HttpParams()
      .set("acid", this.fmData.account_code)
      .set("freqPeriod", this.formData.controls.freqPeriod.value)
      .set("installmentFreq", this.formData.controls.installmentFreq.value)
      .set("installmentsNumber", this.formData.controls.installmentsNumber.value)
      .set("installmentStartDate", this.datepipe.transform(this.formData.controls.installmentStartDate.value, "yyyy-MM-dd"));
    this.loanAPI.loanRestructure(this.params).subscribe(
      (res) => {
        this.results = res;
        this.loading = false;
        this.notificationAPI.alertSuccess(this.results.message);
      }, err => {
        this.loading = false;
        this.error = err;
        this.notificationAPI.alertWarning(this.results.error);
      }
    )

  }
}

