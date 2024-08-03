import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';
import { DataStoreService } from 'src/@core/helpers/data-store.service';
import { BatchSalariesLookupComponent } from '../batch-salaries-lookup/batch-salaries-lookup.component';

@Component({
  selector: 'app-batch-salaries-maintenance',
  templateUrl: './batch-salaries-maintenance.component.html',
  styleUrls: ['./batch-salaries-maintenance.component.scss']
})
export class BatchSalariesMaintenanceComponent implements OnInit, OnDestroy {
  existingData: boolean = false;
  loading: boolean = false;
  submitted = false;
  lookupData: any;
  destroy$: Subject<boolean> = new Subject<boolean>();
  batchUploadCode: any;
  proceeding: boolean = false;
  constructor(
    private router: Router,
    private dialog: MatDialog,
    private fb: FormBuilder,
    private dataStoreApi: DataStoreService,
    private notificationAPI: NotificationService
  ) {
    this.functionArray = this.dataStoreApi.getActionsByPrivilege("TRANSACTION MAINTENANCE");
    this.functionArray = this.functionArray.filter(
      (arr: string) =>
        arr === 'ADD' ||
        arr === 'INQUIRE' ||
        arr === 'MODIFY' ||
        arr === 'VERIFY' ||
        arr === 'POST');
  }
  ngOnDestroy(): void {
    this.destroy$.next(true);
    this.destroy$.complete();
  }
  ngOnInit(): void { }
  functionArray: any;
  formData = this.fb.group({
    function_type: ['', Validators.required],
    batchUploadCode: [''],
  });

  onSelectFunction(event: any) {
    if (event.target.value == 'ADD') {
      this.existingData = false;
      this.formData.controls.batchUploadCode.clearValidators();
      this.formData.controls.batchUploadCode.updateValueAndValidity();
    } else if (event.target.value != 'ADD') {
      this.existingData = true;
      this.formData.controls.batchUploadCode.setValidators([
        Validators.required
      ]);
    }
  }
  get f() {
    return this.formData.controls;
  }
  onSubmit() {
    this.loading = true;
    this.submitted = true;
    this.proceeding = true;
    if (this.formData.valid) {
      if (this.formData.value.function_type == 'ADD') {
        this.router.navigate([`/system/batch-salaries-transaction/data/view`], {
          skipLocationChange: true,
          queryParams: {
            formData: this.formData.value
          },
        });
      } else if (this.formData.value.function_type != 'ADD') {
        this.router.navigate([`/system/batch-salaries-transaction/data/view`], {
          skipLocationChange: true,
          queryParams: {
            formData: this.formData.value
          },
        });
      }
    } else {
      this.loading = false;
      this.proceeding = false;
      this.notificationAPI.alertWarning("Invalid form data Try Again!!");
    }
  }
  batchUploadCodeLookup() {
    const dialogRef = this.dialog.open(BatchSalariesLookupComponent, {});
    dialogRef.afterClosed().pipe(takeUntil(this.destroy$)).subscribe((result) => {
      this.lookupData = result.data;
      this.batchUploadCode = this.lookupData.batchUploadCode;
      this.formData.patchValue({
        batchUploadCode: this.batchUploadCode,
      });
    });
  }
}
