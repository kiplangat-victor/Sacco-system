import { Component, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { DataStoreService } from 'src/@core/helpers/data-store.service';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';
import { GroupMembershipLookUpComponent } from '../group-membership-look-up/group-membership-look-up.component';

@Component({
  selector: 'app-group-membership-maintenance',
  templateUrl: './group-membership-maintenance.component.html',
  styleUrls: ['./group-membership-maintenance.component.scss']
})
export class GroupMembershipMaintenanceComponent implements OnInit {
  functionArray: any;
  onShowGroupCode = false;
  submitted = false
  function_type: any
  dialogData: any
  loading = false;
  onShowSearchIcon = true;
  snValue: any;
  dialogRef: any;
  constructor(
    private router: Router,
    private fb: FormBuilder,
    private dialog: MatDialog,
    private dataStoreApi: DataStoreService,
    private notificationAPI: NotificationService,
  ) {
    this.functionArray = this.dataStoreApi.getActionsByPrivilege("MEMBERSHIP MANAGEMENT");
    this.functionArray = this.functionArray.filter(
      (arr: string) =>
        arr === 'ADD' ||
        arr === 'DELETE' ||
        arr === 'INQUIRE' ||
        arr === 'MODIFY' ||
        arr === 'VERIFY' ||
        arr === 'REJECT'
    );
  }
  ngOnInit(): void {
  }
  formData = this.fb.group({
    function_type: ['', Validators.required],
    customerName: ['', Validators.required],
    id: ['', Validators.required]
  })
  get f() {
    return this.formData.controls;
  }
  onSelectFunction(event: any) {
    if (event.target.value == "ADD") {
      this.onShowGroupCode = false;
      this.onShowSearchIcon = false;
      this.formData.controls.id.setValue("1");
      this.formData.controls.customerName.setValue("name");
    } else if (event.target.value != "ADD") {
      this.onShowGroupCode = true;
      this.onShowSearchIcon = true;
    }
  }
  onSubmit() {
    this.loading = true;
    if (this.formData.valid) {
      this.loading = false;
      this.router.navigate([`/system/group-membership/data/view`], { skipLocationChange: true, queryParams: { formData: this.formData.value, fetchData: this.dialogData } });
    } else if (!this.formData.valid) {
      if (this.formData.controls.function_type.value == "") {
        this.loading = false;
        this.submitted = true;
        this.notificationAPI.alertWarning("CHOOSE MEMBERSHIP FUNCTION");
      }
      else if (this.formData.controls.id.value == "") {
        this.loading = false;
        this.submitted = true;
        this.notificationAPI.alertWarning("GROUP ID CAN NOT BE EMPTY");
      } else {
        this.loading = false;
        this.submitted = true;
        this.notificationAPI.alertWarning("GROUP DETAILS NOT INVALID");
      }
    }
  }
  groupMembershipLookup(): void {
    this.dialogRef = this.dialog.open(GroupMembershipLookUpComponent, {
    width: '50%',
      autoFocus: false,
      maxHeight: '90vh'
    });
    this.dialogRef.afterClosed().subscribe(results => {
      this.dialogData = results.data;
      this.formData.controls.id.setValue(this.dialogData.id);
      this.formData.controls.customerName.setValue(this.dialogData.customerName);
    })
  }
}
