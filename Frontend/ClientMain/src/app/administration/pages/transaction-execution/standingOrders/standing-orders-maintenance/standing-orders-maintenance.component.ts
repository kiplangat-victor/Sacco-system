import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { Subject } from 'rxjs';
import { DataStoreService } from 'src/@core/helpers/data-store.service';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';
import { StandingOrdersLookupComponent } from '../standing-orders-lookup/standing-orders-lookup.component';
import { StandingOrdersService } from '../standing-orders.service';

@Component({
  selector: 'app-standing-orders-maintenance',
  templateUrl: './standing-orders-maintenance.component.html',
  styleUrls: ['./standing-orders-maintenance.component.scss']
})
export class StandingOrdersMaintenanceComponent implements OnInit, OnDestroy {
  lookupData: any;
  loading = false;
  submitted = false;
  onShowSearchIcon = false;
  functionArray: any;
  error: any;
  showCode: boolean = false;
  randomCode: any;
  destroy$: Subject<boolean> = new Subject<boolean>();
  function_type: any;
  constructor(
    public fb: FormBuilder,
    private router: Router,
    private dialog: MatDialog,
    private dataStoreApi: DataStoreService,
    private notificationAPI: NotificationService,
  ) {
    this.functionArray = this.dataStoreApi.getActionsByPrivilege("TRANSACTION MAINTENANCE");
    this.functionArray = this.functionArray.filter(
      (arr: string) =>
        arr === 'ADD' ||
        arr === 'INQUIRE' ||
        arr ==='VERIFY' ||
        arr === 'MODIFY' ||
        arr === 'DELETE');
  }
  ngOnInit(): void {
    this.randomCode = "STO" + Math.floor(Math.random() * (999 - 10));
  }
  ngOnDestroy(): void {
    this.destroy$.next(true);
    this.destroy$.complete();
  }
  formData = this.fb.group({
    id: [''],
    function_type: ['', Validators.required],
    standingOrderCode: ['', Validators.required],
  });
  onSelectFunction(event: any) {
    this.function_type = event.target.value;
    if (this.function_type == 'ADD') {
      this.onShowSearchIcon = false;
      this.showCode = true;
      this.formData.controls.standingOrderCode.setValue(this.randomCode);
    } else if (this.function_type !== 'ADD') {
      this.showCode = true;
      this.onShowSearchIcon = true;
    }
  }
  get f() { return this.formData.controls; }

  onSubmit() {
    this.loading = true;
    if (this.formData.valid) {
      if (this.function_type == 'ADD') {
        this.router.navigate([`/system/transactions/standing/orders/view`], { skipLocationChange: true, queryParams: { formData: this.formData.value, fetchData: this.lookupData } });

      } else if (this.function_type !== 'ADD') {
        this.router.navigate([`/system/transactions/standing/orders/view`], { skipLocationChange: true, queryParams: { formData: this.formData.value, fetchData: this.lookupData } });
      }
    } else {
      if (this.formData.controls.function_type.value == "") {
        this.loading = false;
        this.submitted = true;
        this.notificationAPI.alertWarning("CHOOSE FORM FUNCTION");
      } else if (this.formData.controls.standingOrderCode.value == "") {
        this.loading = false;
        this.submitted = true;
        this.notificationAPI.alertWarning("STANDING ORDER CODE IS INVALID");
      } else {
        this.loading = false;
        this.submitted = true;
        this.notificationAPI.alertWarning("STANDING ORDER FORM DATA IS INVALID");
      }
    }
  }
  standingOrdersLookup(): void {
    const dialogRef = this.dialog.open(StandingOrdersLookupComponent, {
      width: '35%',
      autoFocus: false,
      maxHeight: '90vh',
    });
    dialogRef.afterClosed().subscribe((result) => {
      this.lookupData = result.data;
      this.formData.patchValue({
        id: this.lookupData.id,
        standingOrderCode: this.lookupData.standingOrderCode
      }
      )
    });
  }
}
