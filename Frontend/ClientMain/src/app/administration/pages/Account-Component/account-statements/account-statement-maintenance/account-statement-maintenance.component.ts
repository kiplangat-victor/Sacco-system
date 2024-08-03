import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { Subject } from 'rxjs';
import { DataStoreService } from 'src/@core/helpers/data-store.service';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';

@Component({
  selector: 'app-account-statement-maintenance',
  templateUrl: './account-statement-maintenance.component.html',
  styleUrls: ['./account-statement-maintenance.component.scss']
})
export class AccountStatementMaintenanceComponent implements OnInit, OnDestroy {
  loading = false;
  submitted = false;
  functionArray: any;
  error: any;
  destroy$: Subject<boolean> = new Subject<boolean>();
  function_type: any;
  constructor(
    public fb: FormBuilder,
    private router: Router,
    private dataStoreApi: DataStoreService,
    private notificationAPI: NotificationService
  ) {
    this.functionArray = this.dataStoreApi.getActionsByPrivilege("ACCOUNTS MANAGEMENT");
    this.functionArray = this.functionArray.filter(
      (arr: string) =>
        arr === 'INQUIRE');
  }
  formData = this.fb.group({
    function_type: ['', [Validators.required]]
  });
  ngOnInit() {
  }
  ngOnDestroy() {
    this.destroy$.next(true);
    this.destroy$.complete();
  }
  onSelectFunction(event: any) {
    this.function_type = event.target.value;
    if (this.function_type == 'INQUIRE') {
      this.notificationAPI.alertSuccess("Click Proceed Button for account Statement");
    } else if (this.function_type !== 'INQUIRE') {
      this.notificationAPI.alertWarning("Function Not Allowed: !!");
    }
  }
  get f() { return this.formData.controls; }
  onSubmit() {
    if (this.formData.valid) {
      this.loading = true;
      if (this.function_type == 'INQUIRE') {
        this.router.navigate([`/system/accounts/statement/data/view`], { skipLocationChange: true, queryParams: { formData: this.formData.value} });

      } else if (this.function_type !== 'INQUIRE') {
        this.router.navigate([`/system`], { skipLocationChange: true, queryParams: { formData: this.formData.value } });
      }
    }
    else if (this.formData.controls.function_type.value == "") {
      this.loading = false;
      this.submitted = true;
      this.notificationAPI.alertWarning("FORM FUNCTION TYPE IS INVALID");
    } else {
      this.loading = false;
      this.submitted = true;
      this.notificationAPI.alertWarning("CHOOSE FORM FUNCTION");
    }
  }
}
