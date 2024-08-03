import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';
import { DataStoreService } from 'src/@core/helpers/data-store.service';
import { ChequeParamsLookUpComponent } from '../cheque-params-look-up/cheque-params-look-up.component';
import { ChequeParamsService } from '../cheque-params.service';

@Component({
  selector: 'app-cheque-params-maintenance',
  templateUrl: './cheque-params-maintenance.component.html',
  styleUrls: ['./cheque-params-maintenance.component.scss']
})
export class ChequeParamsMaintenanceComponent implements OnInit, OnDestroy {
  lookupData: any;
  loading = false;
  submitted = false;
  onShowSearchIcon = false;
  functionArray: any;
  destroy$: Subject<boolean> = new Subject<boolean>();
  function_type: any;
  results: any;
  randomCode: any;
  onsShowCode: boolean = false;
  proceed: boolean = false;
  chargeCode: string;
  constructor(
    public fb: FormBuilder,
    private router: Router,
    private dialog: MatDialog,
    private dataStoreApi: DataStoreService,
    private chequeparamsAPI: ChequeParamsService,
    private notificationAPI: NotificationService
  ) {
    this.functionArray = this.dataStoreApi.getActionsByPrivilege("CONFIGURATIONS");
    this.functionArray = this.functionArray.filter(
      (arr: string) => arr === 'ADD' ||
        arr === 'INQUIRE' ||
        arr === 'MODIFY' ||
        arr === 'VERIFY' ||
        arr === 'DELETE');
  }
  formData = this.fb.group({
    function_type: ['', [Validators.required]],
    chargeCode: ['', [Validators.required]],
  });
  ngOnInit() {
    this.randomCode = "CQP" + Math.floor(Math.random() * (999 - 1));
  }
  ngOnDestroy() {
    this.destroy$.next(true);
    this.destroy$.complete();
  }
  onSelectFunction(event: any) {
    this.function_type = event.target.value;
    if (this.function_type == 'ADD') {
      this.onShowSearchIcon = false;
      this.onsShowCode = true;
      this.formData.controls.chargeCode.setValue(this.randomCode);
    } else if (this.function_type !== 'ADD') {
      this.onShowSearchIcon = true;
      this.onsShowCode = true;
    }
  }
  get f() { return this.formData.controls; }

  onSubmit() {
    this.loading = true;
    this.submitted = true;
    this.proceed = true;
    if (this.formData.valid) {
      this.chargeCode = this.formData.controls.chargeCode.value;
      if (this.function_type == 'ADD') {
        this.chequeparamsAPI.findByCode(this.chargeCode).pipe(takeUntil(this.destroy$)).subscribe(
          (data) => {
            if (data.statusCode === 404) {
              this.loading = false;
              this.router.navigate([`/system/configurations/cheque/charges/data/view`], { skipLocationChange: true, queryParams: { formData: this.formData.value, fetchData: this.lookupData } });
            } else if (
              data.statusCode === 302) {
              this.loading = false;
              this.results = data;
              this.proceed = false;
              this.notificationAPI.alertWarning(this.results.message);
            }
          },
          (err) => {
            this.loading = false;
            this.notificationAPI.alertWarning("Server Error: !!");
          }
        )
      } else if (this.function_type !== 'ADD') {
        this.router.navigate([`/system/configurations/cheque/charges/data/view`], { skipLocationChange: true, queryParams: { formData: this.formData.value, fetchData: this.lookupData } });
      }
    }
    else if (this.formData.controls.function_type.value == "") {
      this.loading = false;
      this.submitted = true;
      this.proceed = false;
      this.notificationAPI.alertWarning("Cheque Params function Type is invalid");
    }
    else if (this.formData.controls.chargeCode.value == "") {
      this.loading = false;
      this.submitted = true;
      this.proceed = false;
      this.notificationAPI.alertWarning("Cheque Params Code is Empty!");
    } else {
      this.loading = false;
      this.submitted = true;
      this.proceed = false;
      this.notificationAPI.alertWarning("Choose Form Function");
    }
  }

  chargeCodeLookup(): void {
    const dialogRef = this.dialog.open(ChequeParamsLookUpComponent, {
      width: "40%",
      autoFocus: false,
      maxHeight: '90vh'
    });
    dialogRef.afterClosed().subscribe(result => {
      this.lookupData = result.data;
      this.formData.controls.chargeCode.setValue(this.lookupData.chargeCode);
    });
  }
}
