import { Component, OnDestroy, OnInit } from "@angular/core";
import { FormBuilder, Validators } from "@angular/forms";
import { Router } from "@angular/router";
import { Subject, Subscription } from "rxjs";
import { takeUntil } from "rxjs/operators";
import { NotificationService } from "src/@core/helpers/NotificationService/notification.service";
import { HolidayService } from "./holiday.service";

@Component({
  selector: 'app-holiday-config',
  templateUrl: './holiday-config.component.html',
  styleUrls: ['./holiday-config.component.scss']
})
export class HolidayConfigComponent implements OnInit, OnDestroy {
  loading = false;
  function_type: string;
  holidayCode: any;
  results: any;
  fmData: any;
  submitted = false;
  onShowResults = false;
  hideBtn = false;
  btnColor: any;
  btnText: any;
  onShowDate = true;
  showStatus: boolean = false;
  showWarning: boolean = true;
  destroy$: Subject<boolean> = new Subject<boolean>();
  constructor(
    public fb: FormBuilder,
    private router: Router,
    private holidayAPI: HolidayService,
    private notificationAPI: NotificationService
  ) {
    this.fmData = this.router.getCurrentNavigation().extras.queryParams.formData;
    this.function_type = this.fmData.function_type;
    this.holidayCode = this.fmData.holidayCode;
  }
  ngOnInit() {
    this.getPage();
  }
  ngOnDestroy(): void {
    this.destroy$.next(true);
    this.destroy$.complete();
  }
  formData = this.fb.group({
    id: [''],
    holidayCode: ['', Validators.required],
    holidayDate: [new Date()],
    holidayDescription: ['', Validators.required],
    holidayName: ['', Validators.required],
    isActive: ['true']
  });
  disabledFormControll() {
    this.formData.disable();
    this.showWarning = false;
  }
  get f() {
    return this.formData.controls;
  }

  getData() {
    this.loading = true;
    this.holidayAPI.findByCode(this.fmData.holidayCode).pipe(takeUntil(this.destroy$)).subscribe(
      {
        next: (
          (res) => {
            if (res.statusCode === 200) {
              this.loading = false;
              this.results = res.entity;
              this.formData = this.fb.group({
                id: [this.results.id],
                holidayCode: [this.results.holidayCode],
                holidayDate: [this.results.holidayDate],
                holidayDescription: [this.results.holidayDescription],
                holidayName: [this.results.holidayName],
                isActive: [this.results.isActive]
              });
            } else {
              this.loading = false;
              this.router.navigate([`/system/configurations/global/holiday/maintenance`], { skipLocationChange: true });
              this.notificationAPI.alertWarning("Holiday Data not found: !!");
            }
          }
        ),
        error: (
          (err) => {
            this.loading = false;
            this.router.navigate([`/system/configurations/global/holiday/maintenance`], { skipLocationChange: true });
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
        id: [''],
        holidayCode: [this.holidayCode],
        holidayDate: [new Date()],
        holidayDescription: ['', Validators.required],
        holidayName: ['', Validators.required],
        isActive: ['true']
      });
      this.btnColor = 'primary';
      this.btnText = 'SUBMIT';
    }
    else if (this.function_type == 'INQUIRE') {
      this.loading = false;
      this.getData();
      this.loading = false;
      this.onShowResults = true;;
      this.disabledFormControll();
      this.onShowDate = false;
      this.showStatus = true;
    }
    else if (this.function_type == 'MODIFY') {
      this.loading = false;
      this.getData();
      this.onShowResults = true;
      this.showStatus = true;
      this.btnColor = 'primary';
      this.btnText = 'MODIFY';
    }
    else if (this.function_type == 'VERIFY') {
      this.getData();
      this.loading = false;
      this.disabledFormControll();
      this.onShowResults = true;
      this.onShowDate = false;
      this.showStatus = true;
      this.btnColor = 'primary';
      this.btnText = 'VERIFY';
    }
    else if (this.function_type == 'DELETE') {
      this.getData();
      this.loading = false;
      this.disabledFormControll();
      this.onShowResults = true;
      this.onShowDate = false;
      this.showStatus = true;
      this.btnColor = 'accent';
      this.btnText = 'DELETE';
    }
  }
  onSubmit() {
    this.submitted = true;
    this.loading = true;
    this.hideBtn = true;
    if (this.function_type == "ADD") {
      if (this.formData.valid) {
        this.holidayAPI.create(this.formData.value).pipe(takeUntil(this.destroy$)).subscribe(
          (data) => {
            if (data.statusCode === 201) {
              this.loading = false;
              this.notificationAPI.alertSuccess(data.message);
              this.router.navigate([`/system/configurations/global/holiday/maintenance`], { skipLocationChange: true });
            } else {
              this.loading = false;
              this.hideBtn = false;
              this.notificationAPI.alertSuccess(data.message);
            }
          }, (err) => {
            this.loading = false;
            this.hideBtn = false;
            this.notificationAPI.alertWarning("Server Error: !!");
          }
        );
      } else {
        this.loading = false;
        this.hideBtn = false;
        this.notificationAPI.alertWarning("HOLIDAY FORM DATA IS INVALID");
      }
    }
    else if (this.function_type == "MODIFY") {
      if (this.formData.valid) {
        this.holidayAPI.modify(this.formData.value).pipe(takeUntil(this.destroy$)).subscribe(res => {
          if (res.statusCode == 200) {
            this.loading = false;
            this.results = res;
            this.notificationAPI.alertSuccess(this.results.message);
            this.router.navigate([`/system/configurations/global/holiday/maintenance`], { skipLocationChange: true });
          } else {
            this.loading = false;
            this.results = res;
            this.hideBtn = false;
            this.notificationAPI.alertWarning(this.results.message);
          }
        }, err => {
          this.loading = false;
          this.hideBtn = false;
          this.notificationAPI.alertWarning("Server Error: !!");
        })
      } else {
        this.loading = false;
        this.notificationAPI.alertWarning("HOLIDAY FORM DATA IS INVALID");
      }
    }
    else if (this.function_type == "VERIFY") {
      this.holidayAPI.verify(this.results.id).pipe(takeUntil(this.destroy$)).subscribe(res => {
        if (res.statusCode == 200) {
          this.results = res;
          this.loading = false;
          this.notificationAPI.alertSuccess(this.results.message);
          this.router.navigate([`/system/configurations/global/holiday/maintenance`], { skipLocationChange: true });

        } else {
          this.results = res;
          this.loading = false;
          this.hideBtn = false;
          this.notificationAPI.alertWarning(this.results.message);
        }
      }, err => {
        this.loading = false;
        this.hideBtn = false;
        this.notificationAPI.alertWarning("Server Error: !!");
      })
    }
    else if (this.function_type == "DELETE") {
      this.holidayAPI.delete(this.results.id).pipe(takeUntil(this.destroy$)).subscribe(res => {
        if (res.statusCode == 200) {
          this.results = res;
          this.loading = false;
          this.notificationAPI.alertSuccess(this.results.message);
          this.router.navigate([`/system/configurations/global/holiday/maintenance`], { skipLocationChange: true });
        } else {
          this.results = res;
          this.loading = false;
          this.hideBtn = false;
          this.notificationAPI.alertWarning(this.results.message);
        }
      }, err => {
        this.loading = false;
        this.hideBtn = false;
        this.notificationAPI.alertWarning("Server Error: !!");
      })
    }
  }
}
