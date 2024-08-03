import { HttpParams } from '@angular/common/http';
import { Component, OnDestroy, OnInit } from "@angular/core";
import { FormBuilder, Validators } from "@angular/forms";
import { MatDialog } from "@angular/material/dialog";
import { Router } from "@angular/router";
import { Subject } from "rxjs";
import { takeUntil } from "rxjs/operators";
import { NotificationService } from "src/@core/helpers/NotificationService/notification.service";
import { MisSectorLookupComponent } from "../mis-sector/mis-sector-lookup/mis-sector-lookup.component";
import { MisSubSectorService } from "./mis-sub-sector.service";

@Component({
  selector: 'app-mis-sub-sector',
  templateUrl: './mis-sub-sector.component.html',
  styleUrls: ['./mis-sub-sector.component.scss']
})
export class MisSubSectorComponent implements OnInit , OnDestroy{
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
  misCode: any;
  misSubcode: any;
  dialogData: any;
  misSectorId: any;
  params: HttpParams;
  showWarning: boolean = true;
  constructor(
    public fb: FormBuilder,
    private router: Router,
    private dialog: MatDialog,
    private subSectorAPI: MisSubSectorService,
    private notificationAPI: NotificationService
  ) {
    this.fmData = this.router.getCurrentNavigation().extras.queryParams.formData;
    this.fetchData = this.router.getCurrentNavigation().extras.queryParams.fetchData;
    if (this.router.getCurrentNavigation().extras.queryParams == null) {
      this.router.navigate([`/system/`], { skipLocationChange: true });
    }
    this.function_type = this.fmData.function_type;
    this.misSubcode = this.fmData.misSubcode;
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
    misSubSector: ['', Validators.required],
    misSubcode: ['', Validators.required],
    misCode: ['', Validators.required],
    description: ['', Validators.required]
  });
  disabledFormControll() {
    this.formData.disable();
    this.showWarning = false;
  }
  get f() {
    return this.formData.controls;
  }
  SectorLookup(): void {
    const dialogRef = this.dialog.open(MisSectorLookupComponent, {
      width: '45%'
    });
    dialogRef.afterClosed().subscribe(results => {
      this.dialogData = results.data;
      this.misSectorId = this.dialogData.id;
      this.misCode = this.dialogData.misCode;
      this.formData.controls.misCode.setValue(this.misCode);
    });
  }
  getData() {
    this.formData = this.fb.group({
      id: [this.fetchData.id],
      misSubSector: [this.fetchData.misSubSector],
      misSubcode: [this.fetchData.misSubcode],
      misCode: [this.fetchData.misCode],
      description: [this.fetchData.description]
    });
  }
  getPage() {
    this.loading = true;
    if (this.function_type == 'ADD') {
      this.loading = false;
      this.formData = this.fb.group({
        id: [''],
        misSubSector: ['', Validators.required],
        misSubcode: [this.misSubcode],
        misCode: ['', Validators.required],
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
    if (this.function_type == "ADD") {
      this.submitted = true;
      this.loading = true;
      if (this.formData.valid) {
        this.params = new HttpParams()
          .set('misSectorId', this.misSectorId);
        this.subSectorAPI.create(this.params, this.formData.value).pipe(takeUntil(this.destroy$)).subscribe(
          (data) => {
            if (data.statusCode === 201) {
              this.loading = false;
              this.notificationAPI.alertSuccess(data.message);
              this.router.navigate([`/system/configurations/global/mis-sub-sector/maintenance`], { skipLocationChange: true });
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
        this.notificationAPI.alertWarning("MIS SUB SECTOR FORM DATA IS INVALID");
      }
    }
    else if (this.function_type == "MODIFY") {
      this.submitted = true;
      if (this.formData.valid) {
        this.subSectorAPI.modify(this.formData.value).pipe(takeUntil(this.destroy$)).subscribe(res => {
          if (res.statusCode === 200) {
            this.loading = false;
            this.results = res;
            this.notificationAPI.alertSuccess(this.results.message);
            this.router.navigate([`/system/configurations/global/mis-sub-sector/maintenance`], { skipLocationChange: true });
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
        this.notificationAPI.alertWarning("MIS SUB SECTOR FORM DATA IS INVALID");
      }
    }
    else if (this.function_type == "VERIFY") {
      this.submitted = true;
      this.subSectorAPI.verify(this.fetchData.id).pipe(takeUntil(this.destroy$)).subscribe(res => {
        if (res.statusCode === 200) {
          this.results = res;
          this.loading = false;
          this.notificationAPI.alertSuccess(this.results.message);
          this.router.navigate([`/system/configurations/global/mis-sub-sector/maintenance`], { skipLocationChange: true });
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
      this.subSectorAPI.delete(this.fetchData.id).pipe(takeUntil(this.destroy$)).subscribe(res => {
        if (res.statusCode === 200) {
          this.results = res;
          this.loading = false;
          this.notificationAPI.alertSuccess(this.results.message);
          this.router.navigate([`/system/configurations/global/mis-sub-sector/maintenance`], { skipLocationChange: true });
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
