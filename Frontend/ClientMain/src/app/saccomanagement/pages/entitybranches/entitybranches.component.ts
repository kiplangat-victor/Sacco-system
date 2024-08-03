import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';
import { EntitybranchesService } from './entitybranches.service';
import { SaccoEntityLookupComponent } from '../sacco-entity/sacco-entity-lookup/sacco-entity-lookup.component';
import { MatDialog } from '@angular/material/dialog';
import { HttpParams } from '@angular/common/http';

@Component({
  selector: 'app-entitybranches',
  templateUrl: './entitybranches.component.html',
  styleUrls: ['./entitybranches.component.scss']
})
export class EntitybranchesComponent implements OnInit, OnDestroy {
  loading = false;
  function_type: string;
  branchCode: any;
  error: any;
  results: any;
  fmData: any;
  submitted = false;
  onShowResults = false;
  hideBtn = false;
  btnColor: any;
  btnText: any;
  destroy$: Subject<boolean> = new Subject<boolean>();
  submitting: boolean = false;
  showWarning: boolean = true;
  lookupData: any;
  fk_id: any;
  params: any;
  constructor(
    public fb: FormBuilder,
    private router: Router,
    private dialog: MatDialog,
    private branchAPI: EntitybranchesService,
    private notificationAPI: NotificationService
  ) {
    this.fmData = this.router.getCurrentNavigation().extras.queryParams.formData;
    this.function_type = this.fmData.function_type;
    this.branchCode = this.fmData.branchCode;
  }
  ngOnInit() {
    this.getPage();
  }
  ngOnDestroy() {
    this.destroy$.next(true);
    this.destroy$.complete();
  }
  formData = this.fb.group({
    branchCode: ['', Validators.required],
    branchDescription: ['', Validators.required],
    email: ['', Validators.required],
    id: [''],
    location: ['', Validators.required],
    phoneNumber: ['', Validators.required],
    saccoEntityCode: ['', Validators.required]
  });
  disabledFormControll() {
    this.formData.disable();
    this.showWarning = false;
  }
  get f() {
    return this.formData.controls;
  }
  entityIDLookup(): void {
    const dialogRef = this.dialog.open(SaccoEntityLookupComponent, {
      width: "45%",
      autoFocus: false,
      maxHeight: '90vh'
    });
    dialogRef.afterClosed().subscribe(result => {
      this.lookupData = result.data;
      this.fk_id = this.lookupData.id;
      this.formData.controls.saccoEntityCode.setValue(this.lookupData.entityId);
    });
  }
  getData() {
    this.loading = true;
    this.branchAPI.branchCode(this.fmData.branchCode).pipe(takeUntil(this.destroy$)).subscribe(
      {
        next: (res) => {
          if (res.statusCode === 302) {
            this.loading = false;
            this.results = res.entity;
            this.formData = this.fb.group({
              branchCode: [this.results.branchCode],
              branchDescription: [this.results.branchDescription],
              email: [this.results.email],
              id: [this.results.id],
              location: [this.results.location],
              phoneNumber: [this.results.phoneNumber],
              saccoEntityCode: [this.results.saccoEntityCode]
            });
          } else {
            this.loading = false;
            this.notificationAPI.alertWarning("BRANCH RECORDS NOT FOUND");
            this.router.navigate([`/saccomanagement/entity-branches/maintenance`], { skipLocationChange: true });
          }
        },
        error: (err) => {
          this.loading = false;
          this.notificationAPI.alertWarning("BRANCH SERVER ERROR");
        },
        complete: () => {

        }
      }
    )
  }

  getPage() {
    if (this.function_type == "ADD") {
      this.formData = this.fb.group({
        branchCode: [this.fmData.branchCode],
        branchDescription: ['', Validators.required],
        email: ['', Validators.required],
        id: [''],
        location: ['', Validators.required],
        phoneNumber: ['', Validators.required],
        saccoEntityCode: ['', Validators.required]
      });
      this.btnColor = 'primary';
      this.btnText = 'SUBMIT';
    }
    else if (this.function_type == "INQUIRE") {
      this.getData();
      this.onShowResults = true;
      this.disabledFormControll();
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
      this.disabledFormControll();
      this.onShowResults = true;
      this.btnColor = 'accent';
      this.btnText = 'DELETE';
    }
  }
  onSubmit() {
    this.loading = true;
    this.submitted = true;
    this.submitting = true;
    if (this.function_type == "ADD") {
      if (this.formData.valid) {
        this.params = new HttpParams()
          .set('fk_id', this.fk_id);
        this.branchAPI.create(this.params, this.formData.value).pipe(takeUntil(this.destroy$)).subscribe(
          (data) => {
            if (data.statusCode === 201) {
              this.loading = false;
              this.notificationAPI.alertSuccess(data.message);
              this.router.navigate([`/saccomanagement/entity-branches/maintenance`], { skipLocationChange: true });
            } else {
              this.loading = false;
              this.submitting = false;
              this.notificationAPI.alertWarning(data.message);
            }
          }, (err) => {
            this.loading = false;
            this.submitting = false;
            this.notificationAPI.alertWarning("Server Error: !!");
          }
        );
      } else {
        this.loading = false;
        this.submitting = false;
        this.notificationAPI.alertWarning("BRANCH FORM DATA IS INVALID");
      }
    }
    else if (this.function_type == "MODIFY") {
      if (this.formData.valid) {
        this.branchAPI.modify(this.formData.value).pipe(takeUntil(this.destroy$)).subscribe(res => {
          if (res.statusCode == 200) {
            this.loading = false;
            this.results = res;
            this.notificationAPI.alertSuccess(this.results.message);
            this.router.navigate([`/saccomanagement/entity-branches/maintenance`], { skipLocationChange: true });
          } else {
            this.loading = false;
            this.submitting = false;
            this.results = res;
            this.getData();
            this.notificationAPI.alertWarning(this.results.message);
          }
        }, err => {
          this.loading = false;
          this.submitting = false;
          this.getData();
          this.notificationAPI.alertWarning("Server Error: !!");
        })
      } else {
        this.loading = false;
        this.submitting = false;
        this.getData();
        this.notificationAPI.alertWarning("BRANCH FORM DATA IS INVALID");
      }
    }
    else if (this.function_type == "VERIFY") {
      this.branchAPI.verify(this.results.id).pipe(takeUntil(this.destroy$)).subscribe(res => {
        if (res.statusCode == 200) {
          this.results = res;
          this.loading = false;
          this.notificationAPI.alertSuccess(this.results.message);
          this.router.navigate([`/saccomanagement/entity-branches/maintenance`], { skipLocationChange: true });

        } else {
          this.results = res;
          this.loading = false;
          this.submitting = false;
          this.getData();
          this.notificationAPI.alertWarning(this.results.message);
        }
      }, err => {
        this.loading = false;
        this.submitting = false;
        this.getData();
        this.notificationAPI.alertWarning("Server Error: !!");
      })
    }
    else if (this.function_type == "DELETE") {
      this.branchAPI.temporarydelete(this.results.id).pipe(takeUntil(this.destroy$)).subscribe(res => {
        if (res.statusCode == 200) {
          this.results = res;
          this.loading = false;
          this.notificationAPI.alertSuccess(this.results.message);
          this.router.navigate([`/saccomanagement/entity-branches/maintenance`], { skipLocationChange: true });
        } else {
          this.results = res;
          this.loading = false;
          this.submitting = false;
          this.getData();
          this.notificationAPI.alertWarning(this.results.message);
        }
      }, err => {
        this.loading = false;
        this.submitting = false;
        this.getData();
        this.notificationAPI.alertWarning("Server Error: !!");
      })
    }
  }
}
