import { Component, OnInit } from '@angular/core';
import { FormArray, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';
import { RolesService } from 'src/app/administration/pages/AccessManagement/roles-management/roles.service';

import { WorkClassService } from 'src/app/administration/pages/AccessManagement/work-class/work-class.service';

@Component({
  selector: 'app-entityworkclass',
  templateUrl: './entityworkclass.component.html',
  styleUrls: ['./entityworkclass.component.scss']
})
export class EntityworkclassComponent implements OnInit {

  loading = false;
  function_type: any;
  branchCode: any;
  error: any;
  results: any;
  fetchData: any;
  fmData: any;
  isDisabled: boolean = false;
  postedBy: any;
  verifiedFlag: any;
  verifiedBy: any;
  lookUpData: any;
  disableCheckBoxes = false;
  submitted = false;
  rolesData: any[] = [];
  btnColor: any;
  btnText: any;
  hideBtn = false;
  onShowResults = false;
  showWorkClass = false;
  classdata: any;
  priviledgedata: any;
  privilegesAddOns = [
    { id: '', name: 'DASHBOARD', selected: true, code: 1 },
    { id: '', name: 'CONFIGURATIONS', selected: false, code: 2 },
    { id: '', name: 'CHARGE PARAMS', selected: false, code: 3 },
    { id: '', name: 'INTEREST MAINTENANCE', selected: false, code: 4 },
    { id: '', name: 'PRODUCTS', selected: false, code: 5 },
    { id: '', name: 'MEMBERSHIP MANAGEMENT', selected: false, code: 6 },
    { id: '', name: 'ACCOUNTS MANAGEMENT', selected: false, code: 7 },
    { id: '', name: 'COLLATERALS MANAGEMENT', selected: false, code: 8 },
    { id: '', name: 'TRANSACTION MAINTENANCE', selected: false, code: 9 },
    { id: '', name: 'EOD MANAGEMENT', selected: false, code: 10 },
    { id: '', name: 'REPORTS', selected: false, code: 11 },
    { id: '', name: 'ACCESS MANAGEMENT', selected: false, code: 12 },
    { id: '', name: 'ACCOUNTS STATEMENT', selected: false, code: 13 },
  ];

  priviledges: FormArray<any>;
  displayArray: { id: string, name: string; selected: boolean; code: number; }[];
  id: any;
  roleId: any;
  postedTime: any;
  roleName: any;

  constructor(
    private router: Router,
    public fb: FormBuilder,
    private rolesService: RolesService,
    private workClassAPI: WorkClassService,
    private notificationAPI: NotificationService,
  ) {
    this.fmData = this.router.getCurrentNavigation().extras.queryParams.formData;
    this.function_type = this.fmData.function_type;
    this.id = this.fmData.id;
  }
  ngOnInit() {
    this.getPage();
    this.getAllRoles();
  }
  getAllRoles() {
    this.loading = true;
    this.rolesService.find().subscribe((res) => {
      this.loading = false;
      this.rolesData = res.entity;
    },
      err => {
        this.loading = false;
        this.notificationAPI.alertWarning(err);
      });
  }

  formData = this.fb.group({
    id: [''],
    roleId: ['', Validators.required],
    workClass: ['', Validators.required],
    privileges: new FormArray([])
  })
  get f() {
    return this.formData.controls;
  }
  onInitForm() {
    this.formData = this.fb.group({
      id: [''],
      roleId: [''],
      workClass: [''],
      privileges: new FormArray([])
    })
  }
  createEmptyPriviledgeForm(): FormGroup {
    return this.fb.group({
      code: [''],
      id: [''],
      name: [''],
      selected: [''],
    });
  }
  addPriviledges(): void {
    this.priviledges = this.formData.get('privileges') as FormArray;
    this.priviledges.push(this.createEmptyPriviledgeForm());
  }
  updateEmptyPriviledgeForm(data: any): FormGroup {
    return this.fb.group({
      code: [data.code],
      id: [data.id],
      name: [data.name],
      selected: [data.selected],
    });
  }
  updatePriviledges(data: any): void {
    this.priviledges = this.formData.get('privileges') as FormArray;
    this.priviledges.push(this.updateEmptyPriviledgeForm(data));
  }
  onChange(e: any, i: any) {
    this.displayArray[i].selected = e.checked
  }
  disabledFormControl() {
    this.formData.disable();
    this.isDisabled = true;
  }
  getData() {
    this.loading = true;
    this.workClassAPI.findById(this.fmData.id).subscribe(
      (res) => {
        this.loading = false;
        this.results = res.entity;
        this.roleId = this.results.roleId;
        this.postedBy = this.results.postedBy;
        this.postedTime = this.results.postedTime;
        this.verifiedFlag = this.results.verifiedFlag;
        this.verifiedBy = this.results.verifiedBy;
        this.formData = this.fb.group({
          id: [this.results.id],
          roleId: [this.results.roleId],
          workClass: [this.results.workClass],
          privileges: new FormArray([])
        })
        this.displayArray = this.results.privileges;
        return this.displayArray;

      }
    )
  }
  getPage() {
    if (this.function_type == 'ADD') {
      this.displayArray = this.privilegesAddOns
      this.privilegesAddOns = this.privilegesAddOns;
      this.onInitForm();
      this.btnColor = 'primary';
      this.btnText = 'SUBMIT';
    } else if (this.function_type == 'INQUIRE') {
      this.isDisabled = true;
      this.hideBtn = true;
      this.onShowResults = true;
      this.getData();
      this.disabledFormControl();
    } else if (this.function_type == 'MODIFY') {
      this.onInitForm();
      this.btnColor = 'primary';
      this.btnText = 'MODIFY';
      this.getData();
      this.onShowResults = true;
    } else if (this.function_type == 'DELETE') {
      this.isDisabled = true;
      this.btnColor = 'warn';
      this.btnText = 'DELETE';
      this.getData();
      this.disabledFormControl();
      this.onShowResults = true;
    }
  }

  onSubmit() {
    this.submitted = true;
    for (let i = 0; i < this.displayArray.length; i++) {
      this.updatePriviledges(this.displayArray[i]);
    }
    if (this.formData.valid) {
      this.loading = true;
      if (this.function_type == 'ADD') {
        this.workClassAPI.create(this.formData.value).subscribe(res => {
          if (res.statusCode == 200 || res.statusCode === 201) {
            this.loading = false;
            this.notificationAPI.alertSuccess(res.message);
            this.router.navigate([`/saccomanagement/entity-work-class/maintenance`], { skipLocationChange: true, });
          } else {
            this.loading = false;
            this.notificationAPI.alertWarning(res.message);
          }
        }, err => {
          this.loading = false;
          this.notificationAPI.alertWarning(err);
        })
      }
    } else if (this.formData.invalid) {
      this.loading = false;
      this.notificationAPI.alertWarning("WORK CLASS FORM DATA INVALID");
    }

    if (this.function_type == 'MODIFY') {
      this.loading = true;
      this.workClassAPI.modify(this.formData.value).subscribe(
        res => {
          if (res.statusCode === 200) {
            this.loading = false;
            this.notificationAPI.alertSuccess(res.message);
            this.router.navigate([`/saccomanagement/entity-work-class/maintenance`], { skipLocationChange: true, });
          } else {
            this.loading = false;
            this.notificationAPI.alertWarning(res.message);
          }
        }, err => {
          this.loading = false;
          this.notificationAPI.alertWarning(err);
        })
    }
    if (this.function_type == 'VERIFY') {
      this.loading = true;
      this.workClassAPI.verify(this.fmData.id).subscribe(res => {
        if (res.statusCode == 200) {
          this.loading = false;
          this.notificationAPI.alertSuccess(res.message);
          this.router.navigate([`/saccomanagement/entity-work-class/maintenance`], { skipLocationChange: true, });
        } else {
          this.loading = false;
          this.notificationAPI.alertWarning(res.message);
        }
      }, err => {
        this.loading = false;
        this.notificationAPI.alertWarning(err.message);
      })
    }
    if (this.function_type == 'DELETE') {
      this.loading = true;
      this.workClassAPI.delete(this.fmData.id).subscribe(res => {
        if (res.statusCode == 200) {
          this.loading = false;
          this.notificationAPI.alertSuccess(res.message);
          this.router.navigate([`/saccomanagement/entity-work-class/maintenance`], { skipLocationChange: true, });
        } else {
          this.notificationAPI.alertWarning(res.message);
        }
      }, err => {
        this.loading = false;
        this.notificationAPI.alertWarning(err.message);
      })
    }
  }
}
