import { Component, OnDestroy, OnInit } from "@angular/core";
import { FormBuilder, Validators } from "@angular/forms";
import { Router } from "@angular/router";
import { Subject, Subscription } from "rxjs";
import { takeUntil } from "rxjs/operators";
import { NotificationService } from "src/@core/helpers/NotificationService/notification.service";
import { LinkedorganizationService } from "./linkedorganization.service";

@Component({
  selector: 'app-linked-organization',
  templateUrl: './linked-organization.component.html',
  styleUrls: ['./linked-organization.component.scss']
})
export class LinkedOrganizationComponent implements OnInit, OnDestroy {
  loading = false;
  function_type: any;
  linkedOrganizationCode: any;
  fmData: any;
  submitted = false;
  onShowResults = false;
  hideBtn = false;
  btnColor: any;
  btnText: any;
  submitting: boolean = false;
  destroy$: Subject<boolean> = new Subject<boolean>();
  results: any;
  showWarning: boolean = true;

  currentUser: any;
  
  constructor(
    public fb: FormBuilder,
    private router: Router,
    private notificationAPI: NotificationService,
    private linkedOrganizationAPI: LinkedorganizationService
  ) {
    this.fmData = this.router.getCurrentNavigation().extras.queryParams.formData;
    this.function_type = this.fmData.function_type;
    this.linkedOrganizationCode = this.fmData.linkedOrganizationCode;
  }
  ngOnInit() {
    this.currentUser = JSON.parse(localStorage.getItem('auth-user'));

    this.getPage();
  }
  ngOnDestroy(): void {
    this.destroy$.next(true);
    this.destroy$.complete();
  }
  formData = this.fb.group({
    id: [''],
    entityId: [''],
    linkedOrganizationCode: [''],
    organization_name: ['', Validators.required],
    organization_tel: ['', Validators.required],
    organization_mail: ['', Validators.required, Validators.email],
    organization_address: ['', Validators.required],
    organization_website: ['', Validators.required],
    organization_country: ['', Validators.required],
    organization_main_office: ['', Validators.required]
  });
  get f() { return this.formData.controls; }
  disabledFormControll() {
    this.formData.disable();
    this.showWarning = false;
  }
  getData() {
    this.loading = true;
    this.linkedOrganizationAPI.organizationCode(this.fmData.linkedOrganizationCode).pipe(takeUntil(this.destroy$)).subscribe(
      {
        next: (
          (res) => {
            if (res.statusCode === 200) {
              this.loading = false;
              this.results = res.entity;
              this.formData = this.fb.group({
                id: [this.results.id],
                entityId: [this.results.entityId],
                linkedOrganizationCode: [this.linkedOrganizationCode],
                organization_name: [this.results.organization_name],
                organization_tel: [this.results.organization_tel],
                organization_mail: [this.results.organization_mail],
                organization_address: [this.results.organization_address],
                organization_website: [this.results.organization_website],
                organization_country: [this.results.organization_country],
                organization_main_office: [this.results.organization_main_office]
              })
            } else {
              this.loading = false;
              this.router.navigate([`system/configurations/global/linked/organization/maintenance`], { skipLocationChange: true });
              this.notificationAPI.alertWarning("Organization Data not found: !!");
            }
          }
        ),
        error: (
          (err) => {
            this.loading = false;
            this.router.navigate([`system/configurations/global/linked/organization/maintenance`], { skipLocationChange: true });
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
    if (this.function_type == "ADD") {
      this.formData = this.fb.group({
        id: [''],
        entityId: [this.currentUser.entityId],
        linkedOrganizationCode: [this.linkedOrganizationCode],
        organization_name: ['', Validators.required],
        organization_tel: ['', Validators.required],
        organization_mail: ['', Validators.required],
        organization_address: ['', Validators.required],
        organization_website: ['', Validators.required],
        organization_country: ['', Validators.required],
        organization_main_office: ['', Validators.required]
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
      this.onShowResults = true;
      this.disabledFormControll();
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
    this.submitting = true;
    if (this.function_type == "ADD") {
      if (this.formData.valid) {
        this.linkedOrganizationAPI.create(this.formData.value).pipe(takeUntil(this.destroy$)).subscribe(res => {
          if (res.statusCode == 201) {
            this.loading = false;
            this.notificationAPI.alertSuccess(res.message);
            this.router.navigate([`system/configurations/global/linked/organization/maintenance`], { skipLocationChange: true });
          } else {
            this.loading = false;
            this.submitting = false;
            this.notificationAPI.alertWarning(res.message);
          }
        }, err => {
          this.loading = false;
          this.submitting = false;
          this.notificationAPI.alertWarning("Server Error: !!");
        })

      } else {
        this.loading = false;
        this.submitting = false;
        this.notificationAPI.alertWarning("ORGANAIZATION FORM DATA IS INVALID");
      }
    }
    if (this.function_type == "MODIFY") {
      if (this.formData.valid) {
        this.linkedOrganizationAPI.modify(this.formData.value).pipe(takeUntil(this.destroy$)).subscribe(res => {
          if (res.statusCode == 200) {
            this.loading = false;
            this.notificationAPI.alertSuccess(res.message);
            this.router.navigate([`system/configurations/global/linked/organization/maintenance`], { skipLocationChange: true });
          } else {
            this.loading = false;
            this.submitting = false;
            this.notificationAPI.alertWarning(res.message);
          }
        }, err => {
          this.loading = false;
          this.submitting = false;
          this.notificationAPI.alertWarning("Server Error: !!");
        })
      } else {
        this.loading = false;
        this.submitting = false;
        this.notificationAPI.alertWarning("ORGANAIZATION FORM DATA IS INVALID");
      }
    }

    if (this.function_type == "VERIFY") {
      this.linkedOrganizationAPI.verify(this.results.id).pipe(takeUntil(this.destroy$)).subscribe(res => {
        if (res.statusCode == 200) {
          this.loading = false;
          this.notificationAPI.alertSuccess(res.message);
          this.router.navigate([`system/configurations/global/linked/organization/maintenance`], { skipLocationChange: true });
        } else {
          this.loading = false;
          this.submitting = false;
          this.notificationAPI.alertWarning(res.message);
        }
      }, err => {
        this.loading = false;
        this.submitting = false;
        this.notificationAPI.alertWarning("Server Error: !!");
      })
    }
    if (this.function_type == "DELETE") {
      this.linkedOrganizationAPI.delete(this.results.id).pipe(takeUntil(this.destroy$)).subscribe(res => {
        if (res.statusCode == 200) {
          this.loading = false;
          this.router.navigate([`system/configurations/global/linked/organization/maintenance`], { skipLocationChange: true });
          this.notificationAPI.alertSuccess(res.message);
        } else {
          this.loading = false;
          this.submitting = false;
          this.notificationAPI.alertWarning(res.message);
        }
      }, err => {
        this.loading = false;
        this.submitting = false;
        this.notificationAPI.alertWarning("Server Error: !!");;
      })
    }
  }
}
