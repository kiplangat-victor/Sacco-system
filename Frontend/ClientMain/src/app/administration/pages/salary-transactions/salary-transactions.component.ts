import { takeUntil } from 'rxjs/operators';
import { Component, OnInit, ViewChild, OnDestroy } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { DataTableDirective } from 'angular-datatables';
import { Subject } from 'rxjs';
import { DataStoreService } from 'src/@core/helpers/data-store.service';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';
import { SalaryTransactionsLookupComponent } from './salary-transactions-lookup/salary-transactions-lookup.component';
import DataTables from 'datatables.net';

@Component({
  selector: 'app-salary-transactions',
  templateUrl: './salary-transactions.component.html',
  styleUrls: ['./salary-transactions.component.scss'],
})
export class SalaryTransactionsComponent implements OnInit, OnDestroy {
  @ViewChild(DataTableDirective) dtElement: DataTableDirective;
 // dtOptions: DataTables.Settings = {};
  existingData: boolean = false;
  loading: boolean = false;
  submitted = false;
  lookupData: any;
  salaryUploadCode: any;
  currentUser: any;
  destroy$: Subject<boolean> = new Subject<boolean>();
  constructor(
    private router: Router,
    private dialog: MatDialog,
    private fb: FormBuilder,
    private dataStoreApi: DataStoreService,
    private notificationAPI: NotificationService
  ) {
    this.currentUser = JSON.parse(localStorage.getItem('auth-user'));
      this.functionArray = this.dataStoreApi.getActionsByPrivilege("TRANSACTION MAINTENANCE");
      this.functionArray = this.functionArray.filter(
        (arr: string) =>
        arr === 'ADD' ||
        arr ===  'INQUIRE' ||
        arr ===  'MODIFY' ||
        arr ===  'VERIFY' ||
        arr ===  'POST' ||
        arr ===  'DELETE' ||
        arr ===  'CANCEL');
  }
  ngOnDestroy(): void {
    this.destroy$.next(true);
    this.destroy$.complete();
  }
  ngOnInit(): void {}
  functionArray: any;
  formData = this.fb.group({
    function_type: ['', Validators.required],
    salaryUploadCode: [''],
  });
  salaryTransactionLookup() {
    const dialogRef = this.dialog.open(SalaryTransactionsLookupComponent, {});
    dialogRef.afterClosed().pipe(takeUntil(this.destroy$)).subscribe((result) => {
      this.lookupData = result.data;
      this.salaryUploadCode = this.lookupData.salaryUploadCode;
      this.formData.patchValue({
        salaryUploadCode: this.salaryUploadCode,
      });
    });
  }
  onSelectFunction(event: any) {
    if (event.target.value == 'ADD') {
      this.existingData = false;
      this.formData.controls.salaryUploadCode.clearValidators();
      this.formData.controls.salaryUploadCode.updateValueAndValidity();
    } else if (event.target.value != 'ADD') {
      this.existingData = true;
      this.formData.controls.salaryUploadCode.setValidators([
        Validators.required
      ]);
    }
  }
  get f() {
    return this.formData.controls;
  }
  onSubmit() {
      this.submitted = true;
      if (this.formData.valid) {
        this.submitted = true;
        if (this.formData.value.function_type == 'ADD') {
          this.router.navigate([`/system/salary-transaction/data/view`], {
            skipLocationChange: true,
            queryParams: {
              formData: this.formData.value, }, });
        } else if (this.formData.value.function_type != 'ADD') {
          this.router.navigate([`/system/salary-transaction/data/view`], {
            skipLocationChange: true,
            queryParams: {
              formData: this.formData.value,
            },
          });
        }
      } else {
        this.loading = false;
        this.notificationAPI.alertWarning("Invalid form data Try Again!!");
      }

  }
}
