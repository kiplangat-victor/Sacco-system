import { Component, OnDestroy, OnInit } from "@angular/core";
import { FormBuilder, Validators } from "@angular/forms";
import { Router } from "@angular/router";
import { Subject } from "rxjs";
import { takeUntil } from "rxjs/operators";
import { NotificationService } from "src/@core/helpers/NotificationService/notification.service";
import { MainClassificationService } from "./main-classification.service";
@Component({
  selector: 'app-main-classifications',
  templateUrl: './main-classifications.component.html',
  styleUrls: ['./main-classifications.component.scss']
})
export class MainClassificationsComponent implements OnInit, OnDestroy {
  loading = false;
  function_type: any;
  error: any;
  results: any;
  fetchData: any;
  fmData: any;
  postedBy: any;
  verifiedFlag: any;
  verifiedBy: any;
  submitted = false;
  onShowResults = false;
  hideBtn = false;
  btnColor: any;
  btnText: any;
  postedTime: any;
  onShowDate = true;
  showStatus: boolean = false;
  destroy$: Subject<boolean> = new Subject<boolean>();
  assetClassificationCode: any;
  showWarning: boolean = true;
  constructor(
    public fb: FormBuilder,
    private router: Router,
    private notificationAPI: NotificationService,
    private mainAssetsAPI:MainClassificationService
  ) {
    this.fmData = this.router.getCurrentNavigation().extras.queryParams.formData;
    this.fetchData = this.router.getCurrentNavigation().extras.queryParams.fetchData;
    if (this.router.getCurrentNavigation().extras.queryParams == null) {
      this.router.navigate([`/system/`], { skipLocationChange: true });
    }
    this.function_type = this.fmData.function_type;
    this.assetClassificationCode = this.fmData.assetClassificationCode;
    this.postedBy = this.fetchData.postedBy;
    this.postedTime = this.fetchData.postedTime;
    this.verifiedFlag = this.fetchData.verifiedFlag;
    this.verifiedBy = this.fetchData.verifiedBy;
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
    assetClassificationCode: ['', Validators.required],
    assetClassificationName: ['', Validators.required],
    assetClassificationDescription: ['', Validators.required]
  });
  disabledFormControll() {
    this.formData.disable();
    this.showWarning = false;
  }
  get f() {
    return this.formData.controls;
  }
  getData() {
    this.formData = this.fb.group({
      id: [this.fetchData.id],
      assetClassificationCode: [this.fetchData.assetClassificationCode],
      assetClassificationName: [this.fetchData.assetClassificationName],
      assetClassificationDescription: [this.fetchData.assetClassificationDescription]
    });
  }
  getPage() {
    this.loading = true;
    if (this.function_type == 'ADD') {
      this.loading = false;
      this.formData = this.fb.group({
        id: [''],
        assetClassificationCode: [this.assetClassificationCode],
        assetClassificationName: ['', Validators.required],
        assetClassificationDescription: ['', Validators.required]
      });
      this.btnColor = 'primary';
      this.btnText = 'SUBMIT';
    }
    else if (this.function_type == 'INQUIRE') {
      this.loading = false;
      this.getData();
      this.onShowResults = true;
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
      this.onShowResults = true;
      this.disabledFormControll();
      this.onShowDate = false;
      this.showStatus = true;
      this.btnColor = 'primary';
      this.btnText = 'VERIFY';
    }
    else if (this.function_type == 'DELETE') {
      this.getData();
      this.loading = false;
      this.onShowResults = true;
      this.disabledFormControll();
      this.onShowDate = false;
      this.showStatus = true;
      this.btnColor = 'accent';
      this.btnText = 'DELETE';
    }
  }
  onSubmit() {
    this.loading = true;
    if (this.function_type == "ADD") {
      this.submitted = true;
      this.loading = true;
      if (this.formData.valid) {
        this.mainAssetsAPI.create(this.formData.value).pipe(takeUntil(this.destroy$)).subscribe(
          (data) => {
            if (data.statusCode === 201) {
              this.loading = false;
              this.notificationAPI.alertSuccess(data.message);
              this.router.navigate([`/system/configurations/global/main-classification/maintenance`], { skipLocationChange: true });
            } else {
              this.loading = false;
              this.notificationAPI.alertSuccess(data.message);
            }
          }, (err) => {
            this.loading = false;
            this.notificationAPI.alertWarning("SERVER ERROR: TRY AGAIN LATER");
          }
        );
      } else {
        this.loading = false;
        this.notificationAPI.alertWarning("CLASSIFICATION FORM DATA IS INVALID");
      }
    }
    else if (this.function_type == "MODIFY") {
      this.submitted = true;
      if (this.formData.valid) {
        this.mainAssetsAPI.modify(this.formData.value).pipe(takeUntil(this.destroy$)).subscribe(res => {
          if (res.statusCode == 200) {
            this.loading = false;
            this.results = res;
            this.notificationAPI.alertSuccess(this.results.message);
            this.router.navigate([`/system/configurations/global/main-classification/maintenance`], { skipLocationChange: true });
          } else {
            this.loading = false;
            this.results = res;
            this.notificationAPI.alertWarning(this.results.message);
          }
        }, err => {
          this.loading = false;
          this.notificationAPI.alertWarning("SERVER ERROR: TRY AGAIN LATER");
        })
      } else {
        this.loading = false;
        this.notificationAPI.alertWarning("CLASSIFICATION FORM DATA IS INVALID");
      }
    }
    else if (this.function_type == "VERIFY") {
      this.submitted = true;
      this.mainAssetsAPI.verify(this.fetchData.id).pipe(takeUntil(this.destroy$)).subscribe(res => {
        if (res.statusCode == 200) {
          this.results = res;
          this.loading = false;
          this.notificationAPI.alertSuccess(this.results.message);
          this.router.navigate([`/system/configurations/global/main-classification/maintenance`], { skipLocationChange: true });

        } else {
          this.results = res;
          this.loading = false;
          this.notificationAPI.alertWarning(this.results.message);
        }
      }, err => {
        this.loading = false;
        this.notificationAPI.alertWarning("SERVER ERROR: TRY AGAIN LATER");
      })
    }

    else if (this.function_type == "DELETE") {
      this.submitted = true;
      this.mainAssetsAPI.delete(this.fetchData.id).pipe(takeUntil(this.destroy$)).subscribe(res => {
        if (res.statusCode == 200) {
          this.results = res;
          this.loading = false;
          this.notificationAPI.alertSuccess(this.results.message);
          this.router.navigate([`/system/configurations/global/main-classification/maintenance`], { skipLocationChange: true });
        } else {
          this.results = res;
          this.loading = false;
          this.notificationAPI.alertWarning(this.results.message);
        }

      }, err => {
        this.loading = false;
        this.notificationAPI.alertWarning("SERVER ERROR: TRY AGAIN LATER");
      })
    }
  }
}
