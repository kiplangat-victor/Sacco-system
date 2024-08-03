import { Component, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';
import { DataStoreService } from 'src/@core/helpers/data-store.service';

@Component({
  selector: 'app-work-class-actions-maintenance',
  templateUrl: './work-class-actions-maintenance.component.html',
  styleUrls: ['./work-class-actions-maintenance.component.scss']
})
export class WorkClassActionsMaintenanceComponent implements OnInit {
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
    private dataStoreApi: DataStoreService,
    private notificationService: NotificationService,
  ) {
    this.functionArray = this.dataStoreApi.getActionsByPrivilege("ACCESS MANAGEMENT");
    this.functionArray = this.functionArray.filter(
      (arr: string) => arr === 'ADD' ||
      arr ===  'DELETE' ||
      arr ===  'INQUIRE' ||
      arr ===  'MODIFY'
      // arr ===  'VERIFY'
      );
  }
  ngOnInit(): void {
    this.lookupData = {};
}
  formData = this.fb.group({
    function_type: ['', [Validators.required]],
  });

  onSelectFunction(event: any) {
    this.function_type = event.target.value;
    if (event.target.value == 'ADD') {
      this.existingData = false;
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
      this.router.navigate([`/system/workclassactions/data/view`], {
        skipLocationChange: true,
        queryParams: {
          formData: this.formData.value,
          fetchData: this.lookupData,
        },
      });
    } else {
      this.loading = false;
      this.notificationService.alertWarning("WORK CLASS FORM DATA IS INVALID");
    }
  }
}
