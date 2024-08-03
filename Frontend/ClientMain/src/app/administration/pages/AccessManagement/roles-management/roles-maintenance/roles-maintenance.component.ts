import { Component, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
import { DataStoreService } from 'src/@core/helpers/data-store.service';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';
import { RolesLookupComponent } from '../roles-lookup/roles-lookup.component';
import { RolesService } from '../roles.service';

@Component({
  selector: 'app-roles-maintenance',
  templateUrl: './roles-maintenance.component.html',
  styleUrls: ['./roles-maintenance.component.scss'],
})
export class RolesMaintenanceComponent implements OnInit {
  lookupData: any;
  id: any;
  existingData: boolean;
  workClassName: any;
  function_type: any;
  resData: any;

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
      arr ===  'DELETE' ||
      arr ===  'INQUIRE' ||
      arr ===  'MODIFY' ||
      arr ===  'VERIFY');
  }
  ngOnInit(): void {
    this.lookupData = {};
  }
  loading = false;
  submitted = false;
  functionArray: any;
  formData = this.fb.group({
    function_type: ['', [Validators.required]],
    id: [''],
  });
  rolesLookup(): void {
    const dialogRef = this.dialog.open(RolesLookupComponent, {
    });
    dialogRef.afterClosed().subscribe((result) => {
      this.lookupData = result.data;
      this.id = this.lookupData.id;
      this.workClassName = this.lookupData.workClassName;
      this.formData.controls.id.setValue(this.id);
    });
  }
  onSelectFunction(event: any) {
    this.function_type = event.target.value;
    if (event.target.value == 'ADD') {
      this.existingData = false;
      this.formData.controls.id.clearValidators();
      this.formData.controls.id.updateValueAndValidity();
    } else if (event.target.value != 'ADD') {
      this.existingData = true;
      this.formData.controls['id'].setValidators([Validators.required]);
    }
  }
  get f() {
    return this.formData.controls;
  }
  onSubmit() {
    this.loading = true;
    this.submitted = true;

    if (this.formData.valid) {
      if (this.function_type == 'ADD') {
        this.router.navigate([`/system/roles/data/view`], {
          skipLocationChange: true,
          queryParams: {
            formData: this.formData.value,
            fetchData: this.lookupData,
          },
        });
      } else if (this.function_type != 'ADD') {
        this.rolesService.findById(this.formData.value.id).subscribe(
          (res) => {
            this.resData = res;
            this.loading = false;
            if (this.resData) {
              this.lookupData = this.resData;
              this.router.navigate([`/system/roles/data/view`], {
                skipLocationChange: true,
                queryParams: {
                  formData: this.formData.value,
                  fetchData: this.lookupData,
                },
              });
            } else {
              this.loading = false;
              this.notificationService.alertWarning("ROLE ID NOT FOUND");
            }
          },
          (err) => {
            this.loading = false;
            this.notificationService.alertWarning(err);
          }
        );
      }
    } else {
      this.loading = false;
      this.notificationService.alertWarning("ROLE FORM DATA INVALID");
    }
  }
}
