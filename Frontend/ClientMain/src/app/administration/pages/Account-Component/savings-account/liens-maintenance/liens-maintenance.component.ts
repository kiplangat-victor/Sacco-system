import { takeUntil } from 'rxjs/operators';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { Subject } from 'rxjs';
import { DataStoreService } from 'src/@core/helpers/data-store.service';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';
import { SavingsLookupComponent } from '../savings-lookup/savings-lookup.component';
import { LiensLookupComponent } from '../liens-lookup/liens-lookup.component';

@Component({
  selector: 'app-liens-maintenance',
  templateUrl: './liens-maintenance.component.html',
  styleUrls: ['./liens-maintenance.component.scss']
})
export class LiensMaintenanceComponent implements OnInit, OnDestroy {
  loading = false;
  submitted = false;
  functionArray: any;
  function_type: any;
  account_type: any;
  error: any;
  params: any;
  lookupData: any;
  acid: any;
  onShowCode: boolean = false;
  destroy$: Subject<boolean> = new Subject<boolean>();
  liensCode: any;
  constructor(
    public fb: FormBuilder,
    private router: Router,
    private dialog: MatDialog,
    private dataStoreApi: DataStoreService,
    private notificationAPI: NotificationService,
  ) {
    this.functionArray = this.dataStoreApi.getActionsByPrivilege("ACCOUNTS MANAGEMENT");
    this.functionArray = this.functionArray.filter(
      (arr: string) =>
        arr === 'ADD' ||
        arr === 'INQUIRE' ||
        arr === 'MODIFY' ||
        arr === 'CLOSE LIEN' ||
        arr === 'VERIFY' ||
        arr === 'VERIFY CLOSURE');
  }
  ngOnDestroy(): void {
    this.destroy$.next(true);
    this.destroy$.complete();
  }
  formData = this.fb.group({
    function_type: ['', Validators.required],
    lien_code: ['', Validators.required]
  });
  ngOnInit(): void {
  }
  onChange(event: any) {
    this.function_type = event.target.value;
    if (this.function_type == 'ADD') {
      this.onShowCode = false;
      this.formData.controls.lien_code.setValue("LN0001");
    }
    else if (this.function_type !== 'ADD') {
      this.onShowCode = true;
    }
  }
  get f() {
    return this.formData.controls;
  }
  onSubmit() {
    this.loading = true;
    if (this.formData.valid) {
      if (this.function_type == 'ADD') {
        this.router.navigate([`system/liens-account/data/view`], { skipLocationChange: true, queryParams: { formData: this.formData.value } });
      }
      else if (this.function_type !== 'ADD')
        this.router.navigate([`system/liens-account/data/view`], { skipLocationChange: true, queryParams: { formData: this.formData.value } });
    }
    else if (!this.formData.valid) {
      if (this.formData.controls.function_type.value == "") {
        this.loading = false;
        this.submitted = true;
        this.notificationAPI.alertWarning("CHOOSE LIENS FUNCTION")
      } else if (this.formData.controls.lien_code.value == "") {
        this.loading = false;
        this.submitted = true;
        this.notificationAPI.alertWarning("LIENS ACCOUNT ID IS EMPTY")
      } else {
        this.loading = false;
        this.submitted = true;
        this.notificationAPI.alertWarning("LIENS ACCOUNT DETAILS NOT ALLOWED");
      }

    }
  }
  liensCodeLookUp(): void {
    const dialogRef = this.dialog.open(LiensLookupComponent, {
    });
    dialogRef.afterClosed().pipe(takeUntil(this.destroy$)).subscribe(result => {
      this.lookupData = result.data;
      this.acid = this.lookupData.acid;
      console.log("code passed",this.lookupData);
      this.formData.controls.lien_code.setValue(this.lookupData.lienCode);
    });
  }
}

