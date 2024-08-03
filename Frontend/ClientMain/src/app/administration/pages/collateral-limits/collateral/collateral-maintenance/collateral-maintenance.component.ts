import { Component, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { DataStoreService } from 'src/@core/helpers/data-store.service';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';
import { CollateralLookupComponent } from '../collateral-lookup/collateral-lookup.component';

@Component({
  selector: 'app-collateral-maintenance',
  templateUrl: './collateral-maintenance.component.html',
  styleUrls: ['./collateral-maintenance.component.scss'],
})
export class CollateralMaintenanceComponent implements OnInit {
  loading = false;
  submitted = false;
  function_type: any;
  function_type_data: any;
  subscription!: Subscription;
  error: any;
  lookup_data: any;
  onShowSearch = false;
  showFunctionType = false;
  submittedCode = false;
  onShowProceedBtn = false;
  onShowCollateralCode = false;
  functionArray: any;
  constructor(
    public fb: FormBuilder,
    private router: Router,
    private dialog: MatDialog,
    private notificationAPI: NotificationService,
    private datastoreApi: DataStoreService
  ) {
    this.functionArray = this.datastoreApi.getActionsByPrivilege("COLLATERALS MANAGEMENT");
    this.functionArray = this.functionArray.filter(
      (arr: string) =>
      arr ===  'ADD' ||
      arr ===  'INQUIRE' ||
      arr ===  'MODIFY' ||
      arr ===  'VERIFY' ||
      arr ===  'DELETE');
  }
  formData = this.fb.group({
    function_type: ['', Validators.required],
    collateral_code: ['', Validators.required],
  });
  ngOnInit(): void { }

  onFunctionChange(event: any) {
    this.function_type = event.target.value;
    if (event.target.value != "ADD") {
      this.onShowSearch = true;
      this.onShowProceedBtn = true;
      this.onShowCollateralCode = true;
    } else if (event.target.value == "ADD") {
      this.onShowSearch = false;
      this.onShowProceedBtn = true;
      this.onShowCollateralCode = true;
    }
  }
  get f() {
    return this.formData.controls;
  }
  onSubmit() {
    this.loading = true;
    if (this.formData.valid) {
      this.router.navigate([`system/configurations-collateral-data-view`], { skipLocationChange: true, queryParams: { formData: this.formData.value, fetchedData: this.lookup_data } });
    }
    else if (this.formData.invalid) {
      if (this.formData.controls.function_type.value == "") {
        this.loading = false;
        this.submitted = true;
        this.notificationAPI.alertWarning("CHOOSE COLLATERAL FUNCTION");
      } else if (this.formData.controls.collateral_code.value == "") {
        this.loading = false;
        this.submittedCode = true;
        this.notificationAPI.alertWarning("COLLATERAL CODE CAN NOT BE EMPTY");
      } else {
        this.loading = false;
        this.submittedCode = true;
        this.notificationAPI.alertWarning("COLLATERAL FIELDS INVALID");
      }
    }
  }
  collateralLookup(): void {
    const dialogRef = this.dialog.open(CollateralLookupComponent, {
      width: '45%',
      autoFocus: false,
      maxHeight: '90vh',
    });
    dialogRef.afterClosed().subscribe(result => {
      this.lookup_data = result.data;
      this.formData.controls.collateral_code.setValue(this.lookup_data.collateralCode);
    });
  }
}
