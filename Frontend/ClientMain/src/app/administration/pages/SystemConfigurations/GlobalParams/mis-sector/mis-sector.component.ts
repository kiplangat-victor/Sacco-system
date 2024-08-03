import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { MisSectorService } from './mis-sector.service';
import { Router } from '@angular/router';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';
import { Subject, Subscription } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
@Component({
  selector: 'app-mis-sector',
  templateUrl: './mis-sector.component.html',
  styleUrls: ['./mis-sector.component.scss']
})
export class MisSectorComponent implements OnInit, OnDestroy {
  loading = false;
  function_type: any;
  results: any;
  fmData: any;
  submitted = false;
  onShowResults = false;
  hideBtn = false;
  btnColor: any;
  btnText: any;
  onShowDate = true;
  showStatus: boolean = false;
  destroy$: Subject<boolean> = new Subject<boolean>();
  misCode: any;
  showWarning: boolean = true;
  constructor(
    public fb: FormBuilder,
    private router: Router,
    private misSectorAPI: MisSectorService,
    private notificationAPI: NotificationService
  ) {
    this.fmData = this.router.getCurrentNavigation().extras.queryParams.formData;
    this.function_type = this.fmData.function_type;
    this.misCode = this.fmData.misCode;
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
    misCode: ['', Validators.required],
    misSector: ['', Validators.required],
    description: ['', Validators.required]
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
    this.misSectorAPI.findByCode(this.fmData.misCode).pipe(takeUntil(this.destroy$)).subscribe(
      {
        next: (
          (res) => {
            if (res.statusCode === 200) {
              this.loading = false;
              this.results = res.entity;
              this.formData = this.fb.group({
                id: [this.results.id],
                misCode: [this.results.misCode],
                misSector: [this.results.misSector],
                description: [this.results.description]
              });
            } else {
              this.loading = false;
              this.notificationAPI.alertWarning("GL Subhead Data not found");
              this.router.navigate([`/system/configurations/global/mis-sector/maintenance`], { skipLocationChange: true });
            }
          }
        ),
        error: (
          (err) => {
            this.loading = false;
            this.notificationAPI.alertWarning("Server Error: !!");
            this.router.navigate([`/system/configurations/global/mis-sector/maintenance`], { skipLocationChange: true });
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
        misCode: [this.misCode],
        misSector: ['', Validators.required],
        description: ['', Validators.required]
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
    this.submitted = true;
    this.hideBtn = true;
    if (this.function_type == "ADD") {
      if (this.formData.valid) {
        this.misSectorAPI.create(this.formData.value).pipe(takeUntil(this.destroy$)).subscribe(
          (data) => {
            if (data.statusCode === 201) {
              this.loading = false;
              this.notificationAPI.alertSuccess(data.message);
              this.router.navigate([`/system/configurations/global/mis-sector/maintenance`], { skipLocationChange: true });
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
        this.notificationAPI.alertWarning("MIS SECTOR FORM DATA IS INVALID");
      }
    }
    else if (this.function_type == "MODIFY") {
      if (this.formData.valid) {
        this.misSectorAPI.modify(this.formData.value).pipe(takeUntil(this.destroy$)).subscribe(res => {
          if (res.statusCode === 200) {
            this.loading = false;
            this.results = res;
            this.notificationAPI.alertSuccess(this.results.message);
            this.router.navigate([`/system/configurations/global/mis-sector/maintenance`], { skipLocationChange: true });
          } else {
            this.loading = false;
            this.results = res;
            this.hideBtn = false;
            this.getData();
            this.notificationAPI.alertWarning(this.results.message);
          }
        }, err => {
          this.loading = false;
          this.hideBtn = false;
          this.getData();
         this.notificationAPI.alertWarning("Server Error: !!");
        })
      } else {
        this.loading = false;
        this.hideBtn = false;
        this.getData();
        this.notificationAPI.alertWarning("MIS SECTOR FORM DATA IS INVALID");
      }
    }
    else if (this.function_type == "VERIFY") {
      this.misSectorAPI.verify(this.results.id).pipe(takeUntil(this.destroy$)).subscribe(res => {
        if (res.statusCode === 200) {
          this.results = res;
          this.loading = false;
          this.notificationAPI.alertSuccess(this.results.message);
          this.router.navigate([`/system/configurations/global/mis-sector/maintenance`], { skipLocationChange: true });
        } else {
          this.results = res;
          this.loading = false;
          this.hideBtn = false;
          this.getData();
          this.notificationAPI.alertWarning(this.results.message);
        }
      }, err => {
        this.loading = false;
        this.hideBtn = false;
        this.getData();
       this.notificationAPI.alertWarning("Server Error: !!");
      })
    }

    else if (this.function_type == "DELETE") {
      this.misSectorAPI.delete(this.results.id).pipe(takeUntil(this.destroy$)).subscribe(res => {
        if (res.statusCode === 200) {
          this.results = res;
          this.loading = false;
          this.notificationAPI.alertSuccess(this.results.message);
          this.router.navigate([`/system/configurations/global/mis-sector/maintenance`], { skipLocationChange: true });
        } else {
          this.results = res;
          this.loading = false;
          this.hideBtn = false;
          this.getData();
          this.notificationAPI.alertWarning(this.results.message);
        }

      }, err => {
        this.loading = false;
        this.hideBtn = false;
        this.getData();
       this.notificationAPI.alertWarning("Server Error: !!");
      })
    }
  }
}
