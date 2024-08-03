import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';
import { DataStoreService } from 'src/@core/helpers/data-store.service';
import { EntityrolelookupComponent } from '../entityrolelookup/entityrolelookup.component';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { RolesService } from 'src/app/administration/pages/AccessManagement/roles-management/roles.service';


@Component({
  selector: 'app-entityrolesmaintenance',
  templateUrl: './entityrolesmaintenance.component.html',
  styleUrls: ['./entityrolesmaintenance.component.scss']
})
export class EntityrolesmaintenanceComponent implements OnInit, OnDestroy {
  lookupData: any;
  id: any;
  existingData: boolean;
  workClassName: any;
  function_type: any;
  resData: any;
  onsShowCode: boolean = false;
  roleDescription: any;
  randomCode: any;
  onShowSearchIcon: boolean = false;
  submitting: boolean = false;
  submitted = false;
  results: any;
  loading = false;
  functionArray: any;
  destroy$: Subject<boolean> = new Subject<boolean>();
  roleCode: string;
  constructor(
    public fb: FormBuilder,
    private router: Router,
    private dialog: MatDialog,
    private rolesService: RolesService,
    private dataStoreApi: DataStoreService,
    private notificationService: NotificationService
  ) {
    this.functionArray = this.dataStoreApi.getActionsByPrivilege("ACCESS MANAGEMENT");
    this.functionArray = this.functionArray.filter(
      (arr: string) => arr === 'ADD' ||
        arr === 'DELETE' ||
        arr === 'INQUIRE' ||
        arr === 'MODIFY' ||
        arr === 'VERIFY');
  }
  ngOnDestroy() {
    this.destroy$.next(true);
    this.destroy$.complete();
  }
  ngOnInit(): void {
    this.randomCode = "ROL" + Math.floor(Math.random() * (111));
  }

  formData = this.fb.group({
    function_type: ["", [Validators.required]],
    roleCode: ["", [Validators.required]],
  });

  onSelectFunction(event: any) {
    this.function_type = event.target.value;
    if (event.target.value == 'ADD') {
      this.onsShowCode = true;
      this.formData.controls.roleCode.setValue(this.randomCode);
    } else if (event.target.value != 'ADD') {
      this.onShowSearchIcon = true;
      this.onsShowCode = true;
    }
  }
  get f() {
    return this.formData.controls;
  }

  onSubmit() {
    this.loading = true;
    this.submitted = true;
    this.submitting = true;
    if (this.formData.valid) {
      this.roleCode = this.formData.controls.roleCode.value;
      if (this.function_type !== 'ADD') {
        this.rolesService.findById(this.roleCode).pipe(takeUntil(this.destroy$)).subscribe(
          (data) => {
            if (data.statusCode === 200) {
              this.loading = false;
              this.router.navigate([`/saccomanagement/entity-roles/data-view`], { skipLocationChange: true, queryParams: { formData: this.formData.value } });
            } else if (data.statusCode === 302) {
              this.loading = false;
              this.submitting = false;
              this.notificationService.alertWarning(data.message);
            }
          },
          (err) => {
            this.loading = false;
            this.submitting = false;
            this.notificationService.alertWarning("Server Error: !!");
          }
        )
      } else if (this.function_type == 'ADD') {
        this.router.navigate([`/saccomanagement/entity-roles/data-view`], { skipLocationChange: true, queryParams: { formData: this.formData.value } });
      }
    }
    else if (this.formData.controls.function_type.value == "") {
      this.loading = false;
      this.submitted = true;
      this.submitting = false;
      this.notificationService.alertWarning("Roles Form Function is Invalid");
    }
    else if (this.formData.controls.roleCode.value == "") {
      this.loading = false;
      this.submitted = true;
      this.submitting = false;
      this.notificationService.alertWarning("Role Code is invalid");
    } else {
      this.loading = false;
      this.submitted = true;
      this.submitting = false;
      this.notificationService.alertWarning("Choose Roles Form Function");
    }
  }
  rolesLookup(): void {
    const dialogRef = this.dialog.open(EntityrolelookupComponent, {
    });
    dialogRef.afterClosed().subscribe((result) => {
      this.lookupData = result.data;
      this.roleDescription = this.lookupData.name;
      this.formData.controls.roleCode.setValue(this.lookupData.id);

    });
  }
}
