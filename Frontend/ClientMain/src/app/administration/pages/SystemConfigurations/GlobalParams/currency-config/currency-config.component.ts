import { Component, OnDestroy, OnInit } from "@angular/core";
import { FormBuilder, Validators } from "@angular/forms";
import { Router } from "@angular/router";
import { Subject } from "rxjs";
import { takeUntil } from "rxjs/operators";
import { CountriesService } from "src/@core/helpers/countries/countries.service";
import { NotificationService } from "src/@core/helpers/NotificationService/notification.service";
import { CurrencyService } from "./currency.service";


@Component({
  selector: 'app-currency-config',
  templateUrl: './currency-config.component.html',
  styleUrls: ['./currency-config.component.scss']
})
export class CurrencyConfigComponent implements OnInit, OnDestroy {
  loading = false;
  function_type: any;
  currencyCode: any;
  error: any;
  results: any;
  fmData: any;
  decimalprecision: any;
  ccyName: any;
  country: any;
  submitted = false;
  countries: any;
  hideBtn = false;
  btnColor: any;
  btnText: any;
  onShowResults: boolean = false;
  showWarning: boolean = true;
  destroy$: Subject<boolean> = new Subject<boolean>();
  constructor(
    public fb: FormBuilder,
    private router: Router,
    private currencyAPI: CurrencyService,
    private countriesAPI: CountriesService,
    private notificationAPI: NotificationService
  ) {
    this.fmData = this.router.getCurrentNavigation().extras.queryParams.formData;
    this.function_type = this.fmData.function_type;
    this.currencyCode = this.fmData.currencyCode;
  }
  ngOnDestroy(): void {
    this.destroy$.next(true);
    this.destroy$.complete();
  }
  ngOnInit() {
    this.getPage();
    this.getCurrencies();
  }
  formData = this.fb.group({
    id: [''],
    currencyCode: [''],
    ccy: ['', Validators.required],
    country: ['', [Validators.required]],
    ccyName: ['', [Validators.required]],
    decimalprecision: ['', Validators.required]
  });
  disabledFormControll() {
    this.formData.disable();
    this.showWarning = false;
  }
  get f() { return this.formData.controls; }
  getCurrencies() {
    this.countries = this.countriesAPI.countries();
  }
  getData() {
    this.loading = true;
    this.currencyAPI.currencyCode(this.fmData.currencyCode).pipe(takeUntil(this.destroy$)).subscribe(
      {
        next: (res) => {
          if (res.statusCode === 200) {
            this.results = res.entity;
            this.loading = false;
            this.formData = this.fb.group({
              id: [this.results.id],
              currencyCode: [this.results.currencyCode],
              ccy: [this.results.ccy],
              country: [this.results.country],
              ccyName: [this.results.ccyName],
              decimalprecision: [this.results.decimalprecision]
            });
          } else {
            this.loading = false;
            this.notificationAPI.alertWarning("CURRENCY RECORDS NOT FOUND");
            this.router.navigate([`/system/configurations/global/currency/maintenance`], { skipLocationChange: true });
          }
        },
        error: (err) => {
          this.loading = false;
          this.notificationAPI.alertWarning("server Error: !!");
        },
        complete: () => {

        }
      }
    )
  }
  getPage() {
    if (this.function_type == "ADD") {
      this.formData = this.fb.group({
        id: [''],
        currencyCode: [this.currencyCode],
        ccy: ['', Validators.required],
        country: ['', [Validators.required]],
        ccyName: ['', [Validators.required]],
        decimalprecision: ['', Validators.required]
      });
      this.btnColor = 'primary';
      this.btnText = 'SUBMIT';
    }
    else if (this.function_type == "INQUIRE") {
      this.getData();
      this.disabledFormControll();
      this.onShowResults = true;
    }
    else if (this.function_type == "MODIFY") {
      this.getData();
      this.btnColor = 'primary';
      this.btnText = 'MODIFY';
      this.onShowResults = true;
    }
    else if (this.function_type == "VERIFY") {
      this.getData();
      this.disabledFormControll();
      this.btnColor = 'primary';
      this.btnText = 'VERIFY';
      this.onShowResults = true;
    }
    else if (this.function_type == "DELETE") {
      this.getData();
      this.disabledFormControll();
      this.btnColor = 'accent';
      this.btnText = 'DELETE';
      this.onShowResults = true;
    }
  }
  onSubmit() {
    this.loading = true;
    this.submitted = true;
    this.hideBtn = true;
    if (this.function_type == "ADD") {
      if (this.formData.valid) {
        this.currencyAPI.create(this.formData.value).pipe(takeUntil(this.destroy$)).subscribe(res => {
          if (res.statusCode == 201) {
            this.loading = false;
            this.notificationAPI.alertSuccess(res.message);
            this.router.navigate([`/system/configurations/global/currency/maintenance`], { skipLocationChange: true });
          } else {
            this.loading = false;
            this.hideBtn = false;
            this.notificationAPI.alertWarning(res.message);
          }

        }, err => {
          this.loading = false;
          this.hideBtn = false;
          this.notificationAPI.alertWarning("server Error: !!");
        })
      } else {
        this.loading = false;
        this.hideBtn = false;
        this.notificationAPI.alertWarning("CURRENCY FORM DATA IS INVALID");
      }
    }
    if (this.function_type == "MODIFY") {
      if (this.formData.valid) {
        this.currencyAPI.modify(this.formData.value).pipe(takeUntil(this.destroy$)).subscribe(res => {
          if (res.statusCode == 200) {
            this.loading = false;
            this.notificationAPI.alertSuccess(res.message);
            this.router.navigate([`/system/configurations/global/currency/maintenance`], { skipLocationChange: true });
          } else {
            this.loading = false;
            this.hideBtn = false;
            this.notificationAPI.alertWarning(res.message);
          }
        }, err => {
          this.loading = false;
          this.hideBtn = false;
          this.notificationAPI.alertWarning("SERVER ERROR:");
        })
      } else {
        this.loading = false;
        this.hideBtn = false;
        this.notificationAPI.alertWarning("CURRENCY FORM DATA IS INVALID");
      }
    }
    if (this.function_type == "VERIFY") {
      this.currencyAPI.verify(this.results.id).pipe(takeUntil(this.destroy$)).subscribe(res => {
        if (res.statusCode == 200) {
          this.loading = false;
          this.notificationAPI.alertSuccess(res.message);
          this.router.navigate([`/system/configurations/global/currency/maintenance`], { skipLocationChange: true });
        } else {
          this.loading = false;
          this.hideBtn = false;
          this.notificationAPI.alertWarning(res.message);
        }
      }, err => {
        this.loading = false;
        this.hideBtn = false;
        this.notificationAPI.alertWarning("server Error: !!");
      })
    }
    if (this.function_type == "DELETE") {
      this.currencyAPI.delete(this.results.id).pipe(takeUntil(this.destroy$)).subscribe(res => {
        if (res.statusCode == 200) {
          this.loading = false;
          this.notificationAPI.alertSuccess(res.message);
          this.router.navigate([`/system/configurations/global/currency/maintenance`], { skipLocationChange: true });

        } else {
          this.loading = false;
          this.hideBtn = false;
          this.notificationAPI.alertWarning(res.message);
        }
      }, err => {
        this.loading = false;
        this.hideBtn = false;
        this.notificationAPI.alertWarning("server Error: !!");
      })
    }

  }
}
