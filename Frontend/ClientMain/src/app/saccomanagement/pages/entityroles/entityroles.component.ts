import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';
import { RolesService } from 'src/app/administration/pages/AccessManagement/roles-management/roles.service';


@Component({
  selector: 'app-entityroles',
  templateUrl: './entityroles.component.html',
  styleUrls: ['./entityroles.component.scss']
})
export class EntityrolesComponent implements OnInit, OnDestroy {
  loading = false;
  function_type: any;
  fmData: any;
  submitted: boolean = false;
  onShowResults = false;
  btnColor: any;
  btnText: any;
  hideBtn = false;
  roleCode: any;
  results: any;
  destroy$: Subject<boolean> = new Subject<boolean>();
  constructor(
    public fb: FormBuilder,
    private router: Router,
    private rolesAPI: RolesService,
    private notificationService: NotificationService
  ) {
    this.fmData = this.router.getCurrentNavigation().extras.queryParams.formData;
    this.function_type = this.fmData.function_type;
    this.roleCode = this.fmData.roleCode;
  }
  ngOnDestroy() {
    this.destroy$.next(true);
    this.destroy$.complete();
  }
  ngOnInit() {
    this.getPage();
  }

  formData = this.fb.group({
    id: [''],
    managerial: ["", Validators.required],
    name: ["", Validators.required],
    roleCode: ["", Validators.required],
    verifiedFlag: [''],
    verifiedBy: ['']
  });
  disabledFormControl() {
    this.formData.disable();
  }
  get f() {
    return this.formData.controls;
  }

  getData() {
    this.loading = true;
    this.rolesAPI.findById(this.fmData.roleCode).pipe(takeUntil(this.destroy$)).subscribe(
      {
        next: (res) => {
          if (res.statusCode === 200) {
            this.loading = false;
            this.results = res.entity;
            this.formData = this.fb.group({
              id: [this.results.id],
              managerial: [this.results.managerial],
              name: [this.results.name],
              roleCode: [this.results.roleCode],
              verifiedFlag: [this.results.verifiedFlag],
              verifiedBy: [this.results.verifiedBy]
            });
          } else {
            this.loading = false;
            this.notificationService.alertWarning("Entity Role Records not found: !!");
            this.router.navigate([`/saccomanagement/entity-roles/maintenance`], { skipLocationChange: true });
          }
        },
        error: (err) => {
          this.loading = false;
          this.notificationService.alertWarning("Server Error: !!");
        },
        complete: () => {

        }
      }
    )
  }
  getPage() {
    if (this.function_type == 'ADD') {
      this.formData = this.fb.group({
        id: [''],
        managerial: ["", Validators.required],
        name: ["", Validators.required],
        verifiedFlag: [''],
        verifiedBy: [''],
        roleCode: [this.fmData.roleCode]
      });
      this.btnColor = 'primary';
      this.btnText = 'SUBMIT';
    } else if (this.function_type == 'INQUIRE') {
      this.disabledFormControl();
      this.hideBtn = true;
      this.onShowResults = true;
      this.getData();
    } else if (this.function_type == 'MODIFY') {
      this.btnColor = 'primary';
      this.btnText = 'MODIFY';
      this.onShowResults = true;
      this.getData();
    } else if (this.function_type == 'VERIFY') {
      this.disabledFormControl();
      this.btnColor = 'primary';
      this.btnText = 'VERIFY';
      this.onShowResults = true;
      this.getData();
    } else if (this.function_type == 'DELETE') {
      this.disabledFormControl();
      this.btnColor = 'warn';
      this.btnText = 'DELETE';
      this.onShowResults = true;
      this.getData();
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
            this.router.navigate([`/saccomanagement/entity-roles/maintenance`], { skipLocationChange: true });
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
            this.router.navigate([`/saccomanagement/entity-roles/maintenance`], { skipLocationChange: true });
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
      this.rolesAPI.verify(this.results.id).subscribe(res => {
        if (res.statusCode == 200) {
          this.loading = false;
          this.notificationService.alertSuccess(res.message);
          this.router.navigate([`/saccomanagement/entity-roles/maintenance`], { skipLocationChange: true });
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
      this.rolesAPI.delete(this.results.id).subscribe(res => {
        if (res.statusCode == 200) {
          this.loading = false;
          this.notificationService.alertSuccess(res.message);
          this.router.navigate([`/saccomanagement/entity-roles/maintenance`], { skipLocationChange: true });
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
