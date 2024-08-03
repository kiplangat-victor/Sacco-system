import { Component, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { DataStoreService } from 'src/@core/helpers/data-store.service';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';
import { WorkClassLookupComponent } from '../work-class-lookup/work-class-lookup.component';
@Component({
  selector: 'app-work-class-maintenance',
  templateUrl: './work-class-maintenance.component.html',
  styleUrls: ['./work-class-maintenance.component.scss'],
})
export class WorkClassMaintenanceComponent implements OnInit {
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
      this.router.navigate([`/system/workclass/data/view`], {
        skipLocationChange: true, queryParams: {formData: this.formData.value, fetchData: this.lookupData,
        },
      });
    } else {
      this.loading = false;
      this.notificationService.alertWarning("WORK CLASS FORM DATA IS INVALID");
    }
  }
  workClassLookup(): void {
    const dialogRef = this.dialog.open(WorkClassLookupComponent, {
    });
    dialogRef.afterClosed().subscribe((result) => {
      this.lookupData = result.data;
      this.id = this.lookupData.id;
      this.formData.controls.id.setValue(this.lookupData.id);
    });
  }
}
