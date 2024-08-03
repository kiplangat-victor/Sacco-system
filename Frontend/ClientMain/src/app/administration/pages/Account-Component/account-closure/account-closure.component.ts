import { HttpParams } from '@angular/common/http';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';
import { AccountClosureService } from './account-closure.service';

@Component({
  selector: 'app-account-closure',
  templateUrl: './account-closure.component.html',
  styleUrls: ['./account-closure.component.scss']
})
export class AccountClosureComponent implements OnInit, OnDestroy {
  fmData: any;
  function_type: any;
  account_code: any;
  fetchData: any;
  loading: boolean = false;
  submitted: boolean = false;
  results: any;
  error: any;
  hideBtn = false;
  btnColor: any;
  btnText: any;
  showAccountId = true;
  showResults = false;
  destroy$: Subject<boolean> = new Subject<boolean>();
  params: HttpParams;

  constructor(
    private router: Router,
    private fb: FormBuilder,
    private notificationAPI: NotificationService,
    private accountClosureAPI: AccountClosureService
  ) {
    this.fmData = this.router.getCurrentNavigation().extras.queryParams.formData;
    this.function_type = this.fmData.function_type;
    this.account_code = this.fmData.account_code;
    this.fetchData = this.router.getCurrentNavigation().extras.queryParams.fetchedData;
  }
  ngOnDestroy(): void {
    this.destroy$.next(true);
    this.destroy$.complete();
  }
  ngOnInit(): void {
    this.getPage();
  }
  formData: FormGroup = this.fb.group({
    acid: ['', Validators.required],
    closureReason: ['', Validators.required]
  });
  get f() {
    return this.formData.controls;
  }
  getData() {
    this.loading = true;
    this.accountClosureAPI.getClosure(this.fmData.account_code).pipe(takeUntil(this.destroy$)).subscribe(
      data => {
        if (data.statusCode === 302) {
          this.loading = false;
          this.results = data.entity;   
          //this.btnText = 'SUBMIT';       
          this.formData = this.fb.group({
            acid: [this.account_code],
            closureReason: [this.results.closureReason]
          });
        } else {
          this.loading = false;
          this.router.navigate([`system/account-closure/maintenance`], { skipLocationChange: true });
        }
      },
      err => {
        this.loading = false;
        this.error = err;
        this.notificationAPI.alertWarning(this.error);
        this.router.navigate([`system/account-closure/maintenance`], { skipLocationChange: true });
      })
  }
  getActivationData() {
    this.loading = true;
    this.accountClosureAPI.getActivation(this.fmData.account_code).pipe(takeUntil(this.destroy$)).subscribe(
      data => {
        if (data.statusCode === 302) {
          this.loading = false;
          this.results = data.entity;   
          //this.btnText = 'SUBMIT';       
          this.formData = this.fb.group({
            acid: [this.account_code],
            closureReason: [this.results.closureReason]
          });
        } else {
          this.loading = false;
          this.router.navigate([`system/account-closure/maintenance`], { skipLocationChange: true });
        }
      },
      err => {
        this.loading = false;
        this.error = err;
        this.notificationAPI.alertWarning(this.error);
        this.router.navigate([`system/account-closure/maintenance`], { skipLocationChange: true });
      })
  }
  getPage() {
    this.loading = true;
    if (this.function_type == 'CLOSE') {
      this.loading = false;  
      this.formData = this.fb.group({
        acid: [this.account_code],
        closureReason: ['', Validators.required]
      });
      this.btnColor = 'accent';
      this.btnText = 'CLOSE ACCOUNT';
    } else if (this.fmData.function_type == 'VERIFY CLOSURE') {
      this.loading = false;
      this.getData();
      this.showAccountId = false;
      this.showResults = true;
      this.formData.disable();
      this.btnText = 'VERIFY CLOSURE';
      this.btnColor = 'accent';
    }else if (this.fmData.function_type == 'ACTIVATE') {
      this.loading = false;  
      this.formData = this.fb.group({
        acid: [this.account_code],
        closureReason: ['', Validators.required]
      });
      this.btnColor = 'accent';
      this.btnText = 'ACTIVATE ACCOUNT';
    }
    else if (this.fmData.function_type == 'VERIFY ACTIVATION') {
      this.loading = false;
      this.getActivationData();
      this.showAccountId = true;
      this.showResults = true;
      this.formData.disable();
      this.btnText = 'VERIFY ACTIVATION';
      this.btnColor = 'primary';
    }
  }
  onSubmit() {
    if (this.function_type == 'CLOSE') {
      this.loading = true;
      this.submitted = true;
      if (this.formData.valid) {
        if (window.confirm("ARE YOU SURE YOU WANT TO CLOSE THIS ACCOUNT? ALL RECORDS WILL BE UNAVAILABLE FROM THE ACCOUNT HOLDER!!")) {
          this.params = new HttpParams()
            .set("acid", this.account_code)
            .set("closureReason", this.formData.controls.closureReason.value);
          this.accountClosureAPI.closeAccount(this.params).pipe(takeUntil(this.destroy$)).subscribe(
            data => {
              if (data.statusCode === 201) {
                this.loading = false;
                this.notificationAPI.alertSuccess(data.message);
                this.router.navigate([`system/account-closure/maintenance`], { skipLocationChange: true });
              } else {
                this.loading = false;
                this.notificationAPI.alertWarning(data.message);
              }
            }, err => {
              this.loading = false;
              this.error = err;
              this.notificationAPI.alertWarning(this.error);
            }
          )
        }

      } else if (this.formData.controls.closureReason.value == "") {
        this.loading = false;
        this.notificationAPI.alertWarning("ACCOUNT CLOSURE REASON IS INVALID");
      } else {
        this.loading = false;
        this.notificationAPI.alertWarning("ACCOUNT CLOSURE FORM DATA IS INVALID");
      }
    }
    else if (this.function_type == 'VERIFY CLOSURE') {
      this.loading = true;
      this.accountClosureAPI.verifyClosure(this.account_code).pipe(takeUntil(this.destroy$)).subscribe(
        data => {
          if (data.statusCode === 200) {
            this.loading = false;
            this.notificationAPI.alertSuccess(data.message);
            this.router.navigate([`system/account-closure/maintenance`], { skipLocationChange: true });
          } else {
            this.loading = false;
            this.notificationAPI.alertWarning(data.message);
          }
        },
        err => {
          this.error = err;
          this.loading = false;
          this.notificationAPI.alertWarning(this.error);
        }
      )
    }
    if (this.function_type == 'ACTIVATE') {
      this.loading = true;
      this.submitted = true;
      if (this.formData.valid) {
        if (window.confirm("ARE YOU SURE YOU WANT TO ACTIVATE THIS ACCOUNT?")) {
          this.params = new HttpParams()
            .set("acid", this.account_code)
            .set("closureReason", this.formData.controls.closureReason.value);
          this.accountClosureAPI.activateAccount(this.params).pipe(takeUntil(this.destroy$)).subscribe(
            data => {
              if (data.statusCode === 201) {
                this.loading = false;
                this.notificationAPI.alertSuccess(data.message);
                this.router.navigate([`system/account-closure/maintenance`], { skipLocationChange: true });
              } else {
                this.loading = false;
                this.notificationAPI.alertWarning(data.message);
              }
            }, err => {
              this.loading = false;
              this.error = err;
              this.notificationAPI.alertWarning(this.error);
            }
          )
        }

      } else if (this.formData.controls.closureReason.value == "") {
        this.loading = false;
        this.notificationAPI.alertWarning("ACCOUNT ACTIVATION REASON IS INVALID");
      } else {
        this.loading = false;
        this.notificationAPI.alertWarning("ACCOUNT ACTIVATION FORM DATA IS INVALID");
      }
    }
    else if (this.function_type == 'VERIFY ACTIVATION') {
      this.loading = true;
      this.accountClosureAPI.verifyActivation(this.account_code).pipe(takeUntil(this.destroy$)).subscribe(
        data => {
          if (data.statusCode === 200) {
            this.loading = false;
            this.notificationAPI.alertSuccess(data.message);
            this.router.navigate([`system/account-closure/maintenance`], { skipLocationChange: true });
          } else {
            this.loading = false;
            this.notificationAPI.alertWarning(data.message);
          }
        },
        err => {
          this.error = err;
          this.loading = false;
          this.notificationAPI.alertWarning(this.error);
        }
      )
    }    
    
  }
}

