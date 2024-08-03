import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { Subject, Subscription } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';
import { ManualLoanPenaltiesService } from '../manual-loan-penalties.service';

@Component({
  selector: 'app-manual-loan-penalties',
  templateUrl: './manual-loan-penalties.component.html',
  styleUrls: ['./manual-loan-penalties.component.scss']
})
export class ManualLoanPenaltiesComponent implements OnInit, OnDestroy {
  loading = false;
  function_type: string;
  holidayCode: any;
  error: any;
  results: any;
  fmData: any;
  submitted = false;
  onShowResults = false;
  hideBtn = false;
  btnColor: any;
  btnText: any;
  showAccountCode: boolean = true;
  destroy$: Subject<boolean> = new Subject<boolean>();
  id: number;
  acid: any;
  resultsData: any;
  fetcheddata: any;
  constructor(
    public fb: FormBuilder,
    private router: Router,
    private notificationAPI: NotificationService,
    private loanPenaltyAPI: ManualLoanPenaltiesService,
  ) {
    this.fmData = this.router.getCurrentNavigation().extras.queryParams.formData;
    console.log("PARAMS ", this.fmData);

    this.function_type = this.fmData.function_type;
    this.id = this.fmData.id;
    this.acid = this.fmData.acid;
  }
  ngOnInit() {
    this.getPage();
  }
  ngOnDestroy(): void {
    this.destroy$.next(true);
    this.destroy$.complete();
  }
  formData = this.fb.group({
    id: [""],
    penaltyAmount: ["", Validators.required],
    loanAcid: [""],
    penaltyDescription: ["", Validators.required]
  });
  disabledFormControll() {
    this.formData.disable()
  }
  get f() {
    return this.formData.controls;
  }

  getData() {
    this.loading = true;
    this.loanPenaltyAPI.findById(this.fmData.id).pipe(takeUntil(this.destroy$)).subscribe(
      {
        next: (
          (res) => {
            if (res.statusCode === 302) {
              this.loading = false;
              this.resultsData = res.entity;
              this.formData = this.fb.group({
                id: [this.resultsData.id],
                penaltyAmount: [this.resultsData.penaltyAmount],
                loanAcid: [this.resultsData.loanAcid],
                penaltyDescription: [this.resultsData.penaltyDescription]
              });
              this.acid = this.resultsData.loanAcid;
            } else {
              this.loading = false;
              this.notificationAPI.alertWarning("Loan Penalty For this Account not Found !!");
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
  getPage() {
    this.loading = true;
    if (this.function_type == 'ADD') {
      this.loading = false;
      this.formData = this.fb.group({
        id: [""],
        penaltyAmount: ["", Validators.required],
        loanAcid: [this.fmData.acid, Validators.required],
        penaltyDescription: ["", Validators.required]
      });
      this.btnColor = 'primary';
      this.btnText = 'SUBMIT';
    }
    else if (this.function_type == 'INQUIRE') {
      this.getData();
      this.onShowResults = true;
      this.showAccountCode = false;
      this.disabledFormControll();
    }
    else if (this.function_type == 'MODIFY') {
      this.getData();
      this.showAccountCode = false;
      this.btnColor = 'primary';
      this.btnText = 'MODIFY';
    }
    else if (this.function_type == 'VERIFY') {
      this.getData();
      this.showAccountCode = false;
      this.disabledFormControll();
      this.onShowResults = true;
      this.btnColor = 'primary';
      this.btnText = 'VERIFY';
    }
    else if (this.function_type == 'DELETE') {
      this.getData();
      this.showAccountCode = false;
      this.disabledFormControll();
      this.onShowResults = true;
      this.btnColor = 'accent';
      this.btnText = 'DELETE';
    }
  }
  onSubmit() {
    this.submitted = true;
    this.loading = true;
    if (this.function_type == "ADD") {
      if (this.formData.valid) {
        this.loanPenaltyAPI.create(this.fmData.acid, this.formData.value).pipe(takeUntil(this.destroy$)).subscribe(
          (data) => {
            if (data.statusCode === 201) {
              this.loading = false;
              this.notificationAPI.alertSuccess(data.message);
              this.router.navigate([`/system/loan/account/penalties/maintenance`], { skipLocationChange: true });
            } else {
              this.loading = false;
              this.notificationAPI.alertWarning(data.message);
            }
          }, (err) => {
            this.loading = false;
            this.notificationAPI.alertWarning("Server Error: !!");
          }
        );
      } else {
        this.loading = false;
        this.notificationAPI.alertWarning("Manual Penalty Form Data is Invalid: !!");
      }
    }
    else if (this.function_type == "MODIFY") {
      this.submitted = true;
      if (this.formData.valid) {
        this.loanPenaltyAPI.modify(this.formData.value).pipe(takeUntil(this.destroy$)).subscribe(res => {
          if (res.statusCode == 200) {
            this.loading = false;
            this.results = res;
            this.notificationAPI.alertSuccess(this.results.message);
            this.router.navigate([`/system/loan/account/penalties/maintenance`], { skipLocationChange: true });
          } else {
            this.loading = false;
            this.results = res;
            this.notificationAPI.alertWarning(this.results.message);
          }
        }, err => {
          this.loading = false;
          this.notificationAPI.alertWarning("Server Error: !!");
        })
      } else {
        this.loading = false;
        this.notificationAPI.alertWarning("Manual Penalty Form Data is Invalid: !!");
      }
    }
    else if (this.function_type == "VERIFY") {
      this.submitted = true;
      this.loanPenaltyAPI.verify(this.resultsData.id).pipe(takeUntil(this.destroy$)).subscribe(res => {
        if (res.statusCode == 200) {
          this.results = res;
          this.loading = false;
          this.notificationAPI.alertSuccess(this.results.message);
          this.router.navigate([`/system/loan/account/penalties/maintenance`], { skipLocationChange: true });
        } else {
          this.results = res;
          this.loading = false;
          this.notificationAPI.alertWarning(this.results.message);
        }
      }, err => {
        this.loading = false;
        this.notificationAPI.alertWarning("Server Error: !!");
      })
    }

    else if (this.function_type == "DELETE") {
      this.submitted = true;
      this.loanPenaltyAPI.delete(this.resultsData.id).pipe(takeUntil(this.destroy$)).subscribe(res => {
        if (res.statusCode == 200) {
          this.results = res;
          this.loading = false;
          this.notificationAPI.alertSuccess(this.results.message);
          this.router.navigate([`/system/loan/account/penalties/maintenance`], { skipLocationChange: true });
        } else {
          this.results = res;
          this.loading = false;
          this.notificationAPI.alertWarning(this.results.message);
        }
      }, err => {
        this.loading = false;
        this.notificationAPI.alertWarning("Server Error: !!");
      })
    }
  }
}
