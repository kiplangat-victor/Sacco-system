import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { AuthService } from 'src/@core/AuthService/auth.service';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';
import { ExceptionsCodesServiceService } from './exceptions-codes-service.service';

@Component({
  selector: 'app-exceptions-codes',
  templateUrl: './exceptions-codes.component.html',
  styleUrls: ['./exceptions-codes.component.scss']
})
export class ExceptionsCodesComponent implements OnInit, OnDestroy {
  excceptionTypeArray: any[] = ['EXCEPTION', 'ERROR', 'WARNING'];
  loading = false;
  function_type: string;
  results: any;
  fmData: any;
  exceptionCode: any;
  submitted: boolean = false;
  rolesArray: any;
  onShowResults = false;
  hideBtn = false;
  btnColor: any;
  btnText: any;
  showWarning: boolean = true;
  destroy$: Subject<boolean> = new Subject<boolean>();
  constructor(
    public fb: FormBuilder,
    private router: Router,
    private authService: AuthService,
    private notificationAPI: NotificationService,
    private exceptionCodeApi: ExceptionsCodesServiceService,
  ) {
    this.fmData = this.router.getCurrentNavigation().extras.queryParams.formData;
    this.function_type = this.fmData.function_type;
    this.exceptionCode = this.fmData.exceptionCode;
  }
  ngOnDestroy(): void {
    this.destroy$.next(true);
    this.destroy$.complete();
  }
  ngOnInit() {
    this.getPage();
    this.getRoles();
  }
  getRoles() {
    this.loading = true;
    this.authService.getRoles().subscribe(res => {
      this.rolesArray = res;
      this.loading = false;
    }, err => {
      this.loading = false;
    })
  }
  formData = this.fb.group({
    id: [''],
    exceptionCode: ['', Validators.required],
    exce_description: ['', Validators.required],
    exce_code_type: ['', Validators.required],
    exce_work_class_role: ['', Validators.required],
    exce_ignore_exce_overriding_events: ['', Validators.required]
  });
  disabledFormControll() {
    this.formData.disable();
    this.showWarning = false;
  }

  get f() { return this.formData.controls; }

  getData() {
    this.loading = true;
    this.exceptionCodeApi.findByCode(this.fmData.exceptionCode).pipe(takeUntil(this.destroy$)).subscribe(
      {
        next: (
          (res) => {
            if (res.statusCode === 200) {
              this.loading = false;
              this.results = res.entity;
              this.formData = this.fb.group({
                id: [this.results.id],
                exceptionCode: [this.results.exceptionCode],
                exce_description: [this.results.exce_description],
                exce_code_type: [this.results.exce_code_type],
                exce_work_class_role: [this.results.exce_work_class_role],
                exce_ignore_exce_overriding_events: [this.results.exce_ignore_exce_overriding_events],
              });
            } else {
              this.loading = false;
              this.router.navigate([`/system/configurations/global/exceptions-codes/maintenance`], { skipLocationChange: true });
              this.notificationAPI.alertWarning("Exception Code Data not found: !!");
            }
          }
        ),
        error: (
          (err) => {
            this.loading = false;
            this.router.navigate([`/system/configurations/global/exceptions-codes/maintenance`], { skipLocationChange: true });
            this.notificationAPI.alertWarning("Server Error: !!");
          }
        ),
        complete: (
          () => {

          }
        )
      }
    )
  }
  getPage() {
    if (this.function_type == "ADD") {
      this.formData = this.fb.group({
        id: [''],
        exceptionCode: [this.exceptionCode],
        exce_description: ['', Validators.required],
        exce_code_type: ['', Validators.required],
        exce_work_class_role: ['', Validators.required],
        exce_ignore_exce_overriding_events: ['', Validators.required]
      });
      this.btnColor = 'primary';
      this.btnText = 'SUBMIT';
    }
    else if (this.function_type == "INQUIRE") {
      this.getData();
      this.disabledFormControll();
    }
    else if (this.function_type == "MODIFY") {
      this.getData();
      this.btnColor = 'primary';
      this.btnText = 'MODIFY';
    }
    else if (this.function_type == "VERIFY") {
      this.getData();
      this.disabledFormControll();
      this.btnColor = 'primary';
      this.btnText = 'VERIFY';
    }
    else if (this.function_type == "DELETE") {
      this.getData();
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
        this.exceptionCodeApi.create(this.formData.value).pipe(takeUntil(this.destroy$)).subscribe(res => {
          if (res.statusCode == 201) {
            this.loading = false;
            this.notificationAPI.alertSuccess(res.message);
            this.router.navigate([`/system/configurations/global/exceptions-codes/maintenance`], { skipLocationChange: true });
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
        this.notificationAPI.alertSuccess("EXCEPTION FORM DATA IS INVALID");
      }
    }
    if (this.function_type == "MODIFY") {
      if (this.formData.valid) {
        this.exceptionCodeApi.modify(this.formData.value).pipe(takeUntil(this.destroy$)).subscribe(res => {
          if (res.statusCode == 200) {
            this.loading = false;
            this.notificationAPI.alertSuccess(res.message);
            this.router.navigate([`/system/configurations/global/exceptions-codes/maintenance`], { skipLocationChange: true });
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
        this.notificationAPI.alertSuccess("EXCEPTION FORM DATA IS INVALID");
      }

    }
    if (this.function_type == "VERIFY") {
      this.exceptionCodeApi.verify(this.results.id).pipe(takeUntil(this.destroy$)).subscribe(res => {
        if (res.statusCode == 200) {
          this.loading = false;
          this.notificationAPI.alertSuccess(res.message);
          this.router.navigate([`/system/configurations/global/exceptions-codes/maintenance`], { skipLocationChange: true });
        } else {
          this.loading = false;
          this.hideBtn = false;
          this.notificationAPI.alertWarning(res.message);
        }
        this.loading = false;
      }, err => {
        this.loading = false;
        this.hideBtn = false;
        this.notificationAPI.alertWarning("Server Error: !!");
      });
    }
    if (this.function_type == "DELETE") {
      this.exceptionCodeApi.delete(this.results.id).pipe(takeUntil(this.destroy$)).subscribe(res => {
        if (res.statusCode == 200) {
          this.loading = false;
          this.notificationAPI.alertSuccess(res.message);
          this.router.navigate([`/system/configurations/global/exceptions-codes/maintenance`], { skipLocationChange: true });
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
    }
  }
}
