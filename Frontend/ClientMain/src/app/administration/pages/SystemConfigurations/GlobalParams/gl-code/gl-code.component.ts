import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { Subject, Subscription } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';
import { GlCodeService } from './gl-code.service';

@Component({
  selector: 'app-gl-code',
  templateUrl: './gl-code.component.html',
  styleUrls: ['./gl-code.component.scss']
})
export class GlCodeComponent implements OnInit, OnDestroy {
  classificationArray: any = [
    'ASSETS', 'LIABILITIES', 'INCOMES', 'EXPENSES', 'SHARES', 'RESERVE'
  ]
  loading = false;
  function_type: any;
  glCode: any;
  results: any;
  fmData: any;
  submitted: boolean = false;
  onShowResults = false;
  hideBtn = false;
  btnColor: any;
  btnText: any;
  destroy$: Subject<boolean> = new Subject<boolean>();
  postedTime: any;
  showWarning: boolean = true;
  constructor(
    public fb: FormBuilder,
    private router: Router,
    private glcodeAPI: GlCodeService,
    private notificationAPI: NotificationService
  ) {
    this.fmData = this.router.getCurrentNavigation().extras.queryParams.formData;
    this.function_type = this.fmData.function_type;
    this.glCode = this.fmData.glCode;
  }
  ngOnInit() {
    this.getPage();
  }
  ngOnDestroy(): void {
    this.destroy$.next(true);
    this.destroy$.complete();
  }
  formData = this.fb.group({
    glCode: ['', Validators.required],
    glDescription: ['', Validators.required],
    classification: ['', Validators.required],
    fromRange: ['', Validators.required],
    toRange: ['', Validators.required]
  });
  disabledFormControll() {
    this.formData.disable();
    this.showWarning = false;
  }
  get f() { return this.formData.controls; }
  getData() {
    this.loading = true;
    this.glcodeAPI.findCode(this.fmData.glCode).pipe(takeUntil(this.destroy$)).subscribe(
      {
        next: (
          (res) => {
            if (res.statusCode === 200) {
              this.loading = false;
              this.results = res.entity;
              this.formData = this.fb.group({
                glCode: [this.results.glCode],
                glDescription: [this.results.glDescription],
                classification: [this.results.classification],
                fromRange: [this.results.fromRange],
                toRange: [this.results.toRange]
              });
            } else {
              this.loading = false;
              this.notificationAPI.alertWarning("GL Data Not Found !!");
              this.router.navigate([`/system/configurations/global/gl-code/maintenance`], { skipLocationChange: true });
            }
          }
        ),
        error: (
          (err) => {
            this.loading = false;
            this.notificationAPI.alertWarning("Server Error: !!");
            this.router.navigate([`/system/configurations/global/gl-code/maintenance`], { skipLocationChange: true });
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
    if (this.function_type == "ADD") {
      this.formData = this.fb.group({
        glCode: [this.glCode],
        glDescription: ['', Validators.required],
        classification: ['', Validators.required],
        fromRange: ['', Validators.required],
        toRange: ['', Validators.required]
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
      this.onShowResults = true;
      this.btnColor = 'primary';
      this.btnText = 'MODIFY';
    }
    else if (this.function_type == "VERIFY") {
      this.getData();
      this.disabledFormControll();
      this.onShowResults = true;
      this.btnColor = 'primary';
      this.btnText = 'VERIFY';
    }
    else if (this.function_type == "DELETE") {
      this.getData();
      this.onShowResults = true;
      this.disabledFormControll();
      this.btnColor = 'accent';
      this.btnText = 'DELETE';
    }
  }
  onSubmit() {
    this.loading = true;
    this.submitted = true;
    this.hideBtn = true;
    if (this.function_type == "ADD") {
      if (this.formData.valid) {
        this.glcodeAPI.create(this.formData.value).pipe(takeUntil(this.destroy$)).subscribe(res => {
          if (res.statusCode == 201) {
            this.loading = false;
            this.notificationAPI.alertSuccess(res.message);
            this.router.navigate([`/system/configurations/global/gl-code/maintenance`], { skipLocationChange: true });
          } else {
            this.loading = false;
            this.hideBtn = false;
            this.notificationAPI.alertWarning(res.message);
          }
        }, err => {
          this.loading = false;
          this.hideBtn = false;
          this.notificationAPI.alertWarning("Server Error: !!");
        });
      } else {
        this.loading = false;
        this.hideBtn = false;
        this.notificationAPI.alertWarning("GL FORM DATA IS INVALID");
      }
    }
    if (this.function_type == "MODIFY") {
      if (this.formData.valid) {
        this.glcodeAPI.modify(this.formData.value).pipe(takeUntil(this.destroy$)).subscribe(res => {
          if (res.statusCode == 200) {
            this.loading = false;
            this.notificationAPI.alertSuccess(res.message);
            this.router.navigate([`/system/configurations/global/gl-code/maintenance`], { skipLocationChange: true });
          } else {
            this.loading = false;
            this.hideBtn = false;
            this.getData();
            this.notificationAPI.alertWarning(res.message);
          }
        }, err => {
          this.loading = false;
          this.hideBtn = false;
          this.getData();
          this.notificationAPI.alertWarning("Server Error: !!");
        });
      } else {
        this.loading = false;
        this.hideBtn = false;
        this.getData();
        this.notificationAPI.alertWarning("GL FORM DATA IS INVALID");
      }
    }
    if (this.function_type == "VERIFY") {
      this.glcodeAPI.verify(this.results.id).pipe(takeUntil(this.destroy$)).subscribe(res => {
        if (res.statusCode == 200) {
          this.loading = false;
          this.notificationAPI.alertSuccess(res.message);
          this.router.navigate([`/system/configurations/global/gl-code/maintenance`], { skipLocationChange: true });
        } else {
          this.loading = false;
          this.hideBtn = false;
          this.getData();
          this.notificationAPI.alertWarning(res.message);
        }
      }, err => {
        this.loading = false;
        this.hideBtn = false;
        this.getData();
        this.notificationAPI.alertWarning("Server Error: !!");
      });
    }
    if (this.function_type == "DELETE") {
      this.glcodeAPI.delete(this.results.id).pipe(takeUntil(this.destroy$)).subscribe(res => {
        if (res.statusCode == 200) {
          this.loading = false;
          this.notificationAPI.alertSuccess(res.message);
          this.router.navigate([`/system/configurations/global/gl-code/maintenance`], { skipLocationChange: true });
        } else {
          this.loading = false;
          this.hideBtn = false;
          this.getData();
          this.notificationAPI.alertWarning(res.message);
        }
      }, err => {
        this.loading = false;
        this.hideBtn = false;
        this.getData();
        this.notificationAPI.alertWarning("Server Error: !!");
      });
    }
  }
}
