import { Component, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';
import { DataStoreService } from 'src/@core/helpers/data-store.service';
import { EntityworkclasslookupComponent } from '../entityworkclasslookup/entityworkclasslookup.component';

@Component({
  selector: 'app-entityworkclassmaintenance',
  templateUrl: './entityworkclassmaintenance.component.html',
  styleUrls: ['./entityworkclassmaintenance.component.scss']
})
export class EntityworkclassmaintenanceComponent implements OnInit {
loading = false;
  submitted = false;
  functionArray: any;
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
    private dataStoreApi: DataStoreService,
    private notificationService: NotificationService
  ) {
    this.functionArray = this.dataStoreApi.getActionsByPrivilege("ACCESS MANAGEMENT");
    this.functionArray = this.functionArray.filter(
      (arr: string) =>
        arr === 'ADD' ||
        arr === 'DELETE' ||
        arr === 'INQUIRE' ||
        arr === 'MODIFY' ||
        arr === 'VERIFY'
    );
  }
  ngOnInit(): void {
    this.lookupData = {};
  }
  formData = this.fb.group({
    id: ['', Validators.required],
    function_type: ['', Validators.required],
  });

  onSelectFunction(event: any) {
    this.function_type = event.target.value;
    if (event.target.value == 'ADD') {
      this.existingData = false;
      this.formData.controls.id.setValue(" ");
    } else if (event.target.value != 'ADD') {
      this.existingData = true;
    }
  }
  get f() {
    return this.formData.controls;
  }
  onSubmit() {
    this.loading = true;
    this.submitted = true;
    if (this.formData.valid) {
      this.router.navigate([`/saccomanagement/entity-work-class/data-view`], {
        skipLocationChange: true, queryParams: {formData: this.formData.value, fetchData: this.lookupData,
        },
      });
    } else {
      this.loading = false;
      this.notificationService.alertWarning("Work Class Form Data is invalid: !!");
    }
  }
  workClassLookup(): void {
    const dialogRef = this.dialog.open(EntityworkclasslookupComponent, {
    });
    dialogRef.afterClosed().subscribe((result) => {
      this.lookupData = result.data;
      this.id = this.lookupData.id;
      this.formData.controls.id.setValue(this.lookupData.id);
    });
  }
}
