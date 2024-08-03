import { Component, HostBinding, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';
import { SaccoEntityService } from './sacco-entity.service';
import { DomSanitizer, SafeStyle } from '@angular/platform-browser';

import { ColorEvent } from 'ngx-color';

@Component({
  selector: 'app-sacco-entity',
  templateUrl: './sacco-entity.component.html',
  styleUrls: ['./sacco-entity.component.scss']
})
export class SaccoEntityComponent implements OnInit, OnDestroy {
  loading = false;
  function_type: string;
  entityId: any;
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
  constructor(
    public fb: FormBuilder,
    private router: Router,
    private saccoEntityAPI: SaccoEntityService,
    private notificationAPI: NotificationService,
    private sanitizer: DomSanitizer
  ) {
    this.fmData = this.router.getCurrentNavigation().extras.queryParams.formData;
    this.function_type = this.fmData.function_type;
    this.entityId = this.fmData.entityId;
  }
  ngOnInit() {
    this.getPage();
  }
  ngOnDestroy() {
    this.destroy$.next(true);
    this.destroy$.complete();
  }


  formData = this.fb.group({
    // General Details
    entityAddress: ['', Validators.required],
    entityDescription: ['', Validators.required],
    entityEmail: ['', [Validators.required, Validators.email]],
    entityLocation: ['', Validators.required],
    entityName: ['', Validators.required],
    entityPhoneNumber: ['', [Validators.required, Validators.minLength(10), Validators.maxLength(12)]],
    entityWebsite: ['', [Validators.required]],

    // Theme Details
    customSidebarBg: ['', Validators.required],
    customTitlebarBg: ['', Validators.required],

    // Email Details
    emailMessage: ['', Validators.required],
    emailRegards: ['', Validators.required],
    emailRemarks: ['', Validators.required],
    port: [0, [Validators.required, Validators.min(0)]],
    host: ['', Validators.required],
    protocol: ['', Validators.required],
    smtpPassword: ['', Validators.required],
    smtpUsername: ['', Validators.required],
    sslTrust: ['', Validators.required],
    entityImageBanner: ['', Validators.required],
    entityImageLogo: ['', Validators.required],

    // Additional controls
    entityId: [''],
    entityStatus: [''],
    id: ['']
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
    this.saccoEntityAPI.saccoentity(this.fmData.entityId).pipe(takeUntil(this.destroy$)).subscribe(
      {
        next: (res) => {
          if (res.statusCode === 302) {
            this.loading = false;
            this.results = res.entity;

            console.log("this.results.customSidebarBg:: ", this.results.customSidebarBg)
            console.log("this.results.customTitlebarBg:: ", this.results.customTitlebarBg)


            this.formData = this.fb.group({
              // General Details
              entityAddress: [this.results.entityAddress],
              entityDescription: [this.results.entityDescription, Validators.required],
              entityEmail: [this.results.entityEmail, [Validators.required, Validators.email]],
              entityLocation: [this.results.entityLocation, Validators.required],
              entityName: [this.results.entityName, Validators.required],
              entityPhoneNumber: [this.results.entityPhoneNumber, [Validators.required, Validators.minLength(10), Validators.maxLength(12)]],
              entityWebsite: [this.results.entityWebsite, [Validators.required]],
      

              // Theme Details
              customSidebarBg: [this.results.customSidebarBg, Validators.required],
              customTitlebarBg: [this.results.customTitlebarBg, Validators.required],

              // Email Details
              emailMessage: [this.results.emailMessage, Validators.required],
              emailRegards: [this.results.emailRegards, Validators.required],
              emailRemarks: [this.results.emailRemarks, Validators.required],
              port: [this.results.port, [Validators.required, Validators.min(0)]],
              host: [this.results.host, Validators.required],
              protocol: [this.results.protocol, Validators.required],
              smtpPassword: [this.results.smtpPassword, Validators.required],
              smtpUsername: [this.results.smtpUsername, Validators.required],
              sslTrust: [this.results.sslTrust, Validators.required],
              entityImageBanner: [this.results.entityImageBanner, Validators.required],
              entityImageLogo: [this.results.entityImageLogo, Validators.required],

              // Additional controls
              entityId: [this.results.entityId],
              entityStatus: [this.results.entityStatus],
              id: [this.results.id]
            });

            console.log("this.formData before:: ", this.formData.value)
           


            // this.formData.patchValue({
            //   customSidebarBg: this.results.customSidebarBg,
            //   customTitlebarBg: this.results.customTitlebarBg
            // })

            console.log("this.formData after:: ", this.formData.value)

          } else {
            this.loading = false;
            this.notificationAPI.alertWarning("Sacco Entity Records not found: !!");
            this.router.navigate([`/saccomanagement/sacco-entity/maintenance`], { skipLocationChange: true });
          }
        },
        error: (err) => {
          this.loading = false;
          this.notificationAPI.alertWarning("Server Error: !!");
        },
        complete: () => {

        }
      }
    )
  }

  getPage() {
    if (this.function_type == "ADD") {

      this.formData = this.fb.group({
        // General Details
        entityAddress: ['', Validators.required],
        entityDescription: ['', Validators.required],
        entityEmail: ['', [Validators.required, Validators.email]],
        entityLocation: ['', Validators.required],
        entityName: ['', Validators.required],
        entityPhoneNumber: ['', [Validators.required, Validators.minLength(10), Validators.maxLength(12)]],
        entityWebsite: ['', [Validators.required]],

        // Theme Details
        customSidebarBg: ['', Validators.required],
        customTitlebarBg: ['', Validators.required],
      

        // Email Details
        emailMessage: ['', Validators.required],
        emailRegards: ['', Validators.required],
        emailRemarks: ['', Validators.required],
        port: [0, [Validators.required, Validators.min(0)]],
        host: ['', Validators.required],
        protocol: ['', Validators.required],
        smtpPassword: ['', Validators.required],
        smtpUsername: ['', Validators.required],
        sslTrust: ['', Validators.required],
        entityImageBanner: ['', Validators.required],
        entityImageLogo: ['', Validators.required],

        // Additional controls
   
        entityId: [''],
        entityStatus: [''],
        id: ['']
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

  //************************************************************************************ */

  onLogoSelected(event: any) {
    const file: File = event.target.files[0];
    const reader: FileReader = new FileReader();
  
    reader.onload = (e: any) => {
      this.formData.get('entityImageLogo').setValue(e.target.result);
    };
  
    reader.readAsDataURL(file);
  }

  onBannerSelected(event: any) {
    const file: File = event.target.files[0];
    const reader: FileReader = new FileReader();
  
    reader.onload = (e: any) => {
      this.formData.get('entityImageBanner').setValue(e.target.result);
    };
  
    reader.readAsDataURL(file);
  }
  //************************************************************************************** */

  colorTitleBar: Object;
  hexColorTitleBar: String;
  rgbaColorTitleBar: Object;
  @HostBinding('class') headerClass1: SafeStyle;
  handleTitleBarChange($event: ColorEvent) {
    this.colorTitleBar = $event.color;
    this.hexColorTitleBar = $event.color.hex;
    this.rgbaColorTitleBar = $event.color.rgb;
    this.headerClass1 = this.sanitizer.bypassSecurityTrustStyle('background-color:'+ this.hexColorTitleBar +';');
  }

  colorSideBar: Object;
  hexColorSideBar: String;
  rgbaColorSideBar: Object;
  @HostBinding('class') headerClass2: SafeStyle;
  handleSideBarChange($event: ColorEvent) {
    this.colorSideBar = $event.color;
    this.hexColorSideBar = $event.color.hex;
    this.rgbaColorSideBar = $event.color.rgb;
    this.headerClass2 = this.sanitizer.bypassSecurityTrustStyle('background-color:'+ this.hexColorSideBar +';');
  }  
				

				
				

  //*************************************************************************************** */
  onSubmit() {

    console.log("this.formData.value:: ", this.formData.value)

    this.loading = true;
    this.submitted = true;
    this.submitting = true;
    if (this.function_type == "ADD") {
      if (this.formData.valid) {
        this.saccoEntityAPI.create(this.formData.value).pipe(takeUntil(this.destroy$)).subscribe(
          (data) => {
            if (data.statusCode === 201) {
              this.loading = false;
              this.notificationAPI.alertSuccess(data.message);
              this.router.navigate([`/saccomanagement/sacco-entity/maintenance`], { skipLocationChange: true });
            } else {
              this.loading = false;
              this.submitting = false;
              this.notificationAPI.alertWarning(data.message);
            }
          }, (err) => {
            this.loading = false;
            this.submitting = false;
            this.notificationAPI.alertWarning(err.message);
          }
        );
      } else {
        this.loading = false;
        this.submitting = false;
        this.notificationAPI.alertWarning("Sacco Entity Form Data is Invalid: !!");
      }
    }
    else if (this.function_type == "MODIFY") {
      if (this.formData.valid) {
        this.saccoEntityAPI.modify(this.formData.value).pipe(takeUntil(this.destroy$)).subscribe(res => {
          if (res.statusCode == 200) {
            this.loading = false;
            this.results = res;
            this.notificationAPI.alertSuccess(this.results.message);
            this.router.navigate([`/saccomanagement/sacco-entity/maintenance`], { skipLocationChange: true });
          } else {
            this.loading = false;
            this.submitting = false;
            this.results = res;
            this.notificationAPI.alertWarning(this.results.message);
          }
        }, err => {
          this.loading = false;
          this.submitting = false;
          this.notificationAPI.alertWarning(err.message);
        })
      } else {
        this.loading = false;
        this.submitting = false;
        this.notificationAPI.alertWarning("Sacco Entity Form Data is Invalid: !!");
      }
    }
    else if (this.function_type == "VERIFY") {
      this.saccoEntityAPI.verify(this.results.id).pipe(takeUntil(this.destroy$)).subscribe(res => {
        if (res.statusCode == 200) {
          this.results = res;
          this.loading = false;
          this.notificationAPI.alertSuccess(this.results.message);
          this.router.navigate([`/saccomanagement/sacco-entity/maintenance`], { skipLocationChange: true });

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
      this.saccoEntityAPI.temporarydelete(this.results.id).pipe(takeUntil(this.destroy$)).subscribe(res => {
        if (res.statusCode == 200) {
          this.results = res;
          this.loading = false;
          this.notificationAPI.alertSuccess(this.results.message);
          this.router.navigate([`/saccomanagement/sacco-entity/maintenance`], { skipLocationChange: true });
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
