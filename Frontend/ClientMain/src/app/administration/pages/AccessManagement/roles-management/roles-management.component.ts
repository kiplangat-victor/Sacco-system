import { Component, OnInit } from '@angular/core';
import { FormGroup, FormBuilder } from '@angular/forms';
import { Router } from '@angular/router';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';
import { RolesService } from './roles.service';

@Component({
  selector: 'app-roles-management',
  templateUrl: './roles-management.component.html',
  styleUrls: ['./roles-management.component.scss'],
})
export class RolesManagementComponent implements OnInit {
  loading = false;
  function_type: any;
  fetchData: any;
  fmData: any;
  isDisabled: boolean;
  postedBy: any;
  verifiedFlag: any;
  verifiedBy: any;
  lookupData: any;
  submitted: boolean = false;
  formData: FormGroup;
  onShowResults = false;
  btnColor: any;
  btnText: any;
  hideBtn = false;
  id: any;
  postedTime: any;
  name: any;
  constructor(
    public fb: FormBuilder,
    private router: Router,
    private rolesAPI: RolesService,
    private notificationService: NotificationService
  ) {
    this.fmData =
      this.router.getCurrentNavigation().extras.queryParams.formData;
    this.fetchData =
      this.router.getCurrentNavigation().extras.queryParams.fetchData;
    if (this.router.getCurrentNavigation().extras.queryParams == null) {
      this.router.navigate([`/system/`], { skipLocationChange: true });
    }
    this.function_type = this.fmData.function_type;
    if (this.fetchData.entity != null) {
      this.id = this.fetchData.entity.id;
      this.name = this.fetchData.entity.name;
      this.postedBy = this.fetchData.entity.postedBy;
      this.postedTime = this.fetchData.entity.postedTime;
      this.verifiedFlag = this.fetchData.entity.verifiedFlag;
      this.verifiedBy = this.fetchData.entity.verifiedBy;
    }
  }
  ngOnInit() {
    this.getPage();
  }
  onInitEmptyForm() {
    this.formData = this.fb.group({
      id: [''],
      name: [''],
    });
  }
  onInitPrefilledForm() {
    this.formData = this.fb.group({
      id: [this.id],
      name: [this.name],
    });
  }
  disabledFormControl() {
    this.formData.disable();
  }
  get f() {
    return this.formData.controls;
  }
  getPage() {
    if (this.function_type == 'ADD') {
      this.onInitEmptyForm();
      this.btnColor = 'primary';
      this.btnText = 'SUBMIT';
    } else if (this.function_type == 'INQUIRE') {
      this.onInitPrefilledForm();
      this.disabledFormControl();
      this.isDisabled = true;
      this.hideBtn = true;
      this.onShowResults = true;
    } else if (this.function_type == 'MODIFY') {
      this.onInitPrefilledForm();
      this.btnColor = 'primary';
      this.btnText = 'MODIFY';
      this.onShowResults = true;
    } else if (this.function_type == 'VERIFY') {
      this.onInitPrefilledForm();
      this.disabledFormControl();
      this.isDisabled = true;
      this.btnColor = 'primary';
      this.btnText = 'VERIFY';
      this.onShowResults = true;
    } else if (this.function_type == 'DELETE') {
      this.onInitPrefilledForm();
      this.disabledFormControl();
      this.isDisabled = true;
      this.btnColor = 'warn';
      this.btnText = 'DELETE';
      this.onShowResults = true;
    }
  }

  onSubmit() {
    this.submitted = true;
    this.loading = true;
    if (this.function_type == "ADD") {
      if (this.formData.valid) {
        this.rolesAPI.create(this.formData.value).subscribe(res => {
          if (res.statusCode == 201) {
            this.loading = false;
            this.notificationService.alertSuccess(res.message);
            this.router.navigate([`/system/roles/maintenance`], { skipLocationChange: true });
          } else {
            this.loading = false;
            this.notificationService.alertWarning(res.message);
          }
        }, err => {
          this.loading = false;
          this.notificationService.alertWarning(err);
        })
      } else {
        this.loading = false;
        this.notificationService.alertWarning("ROLE FORM DATA IS INVALID");
      }
    }
    if (this.function_type == "MODIFY") {
      if (this.formData.valid) {
        this.rolesAPI.modify(this.formData.value).subscribe(res => {
          if (res.statusCode == 200) {
            this.loading = false;
            this.notificationService.alertSuccess(res.message);
            this.router.navigate([`/system/roles/maintenance`], { skipLocationChange: true });
          } else {
            this.loading = false;
            this.notificationService.alertWarning(res.message);
          }
        }, err => {
          this.loading = false;
          this.notificationService.alertWarning(err);
        })
      } else {
        this.loading = false;
        this.notificationService.alertWarning("ROLE FORM DATA IS INVALID");
      }
    }
    if (this.function_type == "VERIFY") {
      this.rolesAPI.verify(this.id).subscribe(res => {
        if (res.statusCode == 200) {
          this.loading = false;
            this.notificationService.alertSuccess(res.message);
            this.router.navigate([`/system/roles/maintenance`], { skipLocationChange: true });
        } else {
          this.loading = false;
          this.notificationService.alertWarning(res.message);
       }
      }, err => {
        this.loading = false;
        this.notificationService.alertWarning(err);
      })
    }
    if (this.function_type == "DELETE") {
      this.rolesAPI.delete(this.id).subscribe(res => {
        if (res.statusCode == 200) {
          this.loading = false;
            this.notificationService.alertSuccess(res.message);
            this.router.navigate([`/system/roles/maintenance`], { skipLocationChange: true });
        } else {
          this.loading = false;
          this.notificationService.alertWarning(res.message);
       }
      }, err => {
        this.loading = false;
        this.notificationService.alertWarning(err);
      })
    }
  }
}
