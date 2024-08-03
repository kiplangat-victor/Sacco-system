import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { Subject } from 'rxjs';
import { DataStoreService } from 'src/@core/helpers/data-store.service';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';
import { TransactionChequesLookupComponent } from '../transaction-cheques-lookup/transaction-cheques-lookup.component';

@Component({
  selector: 'app-transaction-cheques-maintenance',
  templateUrl: './transaction-cheques-maintenance.component.html',
  styleUrls: ['./transaction-cheques-maintenance.component.scss']
})
export class TransactionChequesMaintenanceComponent implements OnInit, OnDestroy {
  transactionType: any;
  existingData: boolean;
  loading: boolean = false;
  submitted = false;
  lookupData: any;
  chequeRandCode: any;
  showTransactionTypeField = false;
  resData: any;
  currentUser: any;
  transactionTypeArray: any;
  functionArray: any;
  functionMainArray: any[];
  function_type: any;
  destroy$: Subject<boolean> = new Subject<boolean>();
  constructor(
    private router: Router,
    private dialog: MatDialog,
    private fb: FormBuilder,
    private dataStoreApi: DataStoreService,
    private notificationAPI: NotificationService
  ) {
    this.currentUser = JSON.parse(localStorage.getItem('auth-user'));
    this.functionMainArray = this.dataStoreApi.getActionsByPrivilege("TRANSACTION MAINTENANCE");
    this.functionArray = this.functionMainArray.filter(
      arr =>
        arr === 'ENTER' ||
        arr === 'POST CHEQUE' ||
        arr === 'INQUIRE' ||
        arr === 'MODIFY' ||
        arr === 'VERIFY' ||
        arr === 'DELETE' ||
        arr === 'CLEAR CHEQUE' ||
        arr == 'BOUNCE CHEQUE');
  }
  ngOnDestroy(): void {
    this.destroy$.next(true);
    this.destroy$.complete();
  }
  ngOnInit(): void {

  }
  formData = this.fb.group({
    function_type: ['', Validators.required],
    chequeRandCode: ['', Validators.required]
  });

  onSelectFunction(event: any) {
    if (event.target.value == 'ENTER') {
      this.existingData = false;
      this.formData.controls.chequeRandCode.setValue(" ");
    } else if (event.target.value !== 'ENTER') {
      this.existingData = true;
    }
  }
  get f() {
    return this.formData.controls;
  }
  onSubmit() {
    this.submitted = true;
    if (this.formData.valid) {
      this.router.navigate([`/system/transactions/cheques-data-view`], { skipLocationChange: true, queryParams: { formData: this.formData.value } });
    }
    else if (this.formData.invalid) {
      if (this.formData.controls.function_type.value == "") {
        this.loading = false;
        this.submitted = true;
        this.notificationAPI.alertWarning("CHOOSE FUNCTION TYPE");
      }
      else if (this.formData.controls.chequeRandCode.value == "") {
        this.loading = false;
        this.submitted = true;
        this.notificationAPI.alertWarning("CHEQUE CODE CAN NOT BE EMPTY");
      } else {
        this.loading = false;
        this.submitted = true;
        this.notificationAPI.alertWarning("CHEQUE DETAILS NOT ALLOWED")
      }

    }
  }

  chequeRandCodeLookup(): void {
    const dialogRef = this.dialog.open(TransactionChequesLookupComponent, {
      width: "40%",
      autoFocus: false,
      maxHeight: '90vh'
    });
    dialogRef.afterClosed().subscribe(result => {
      this.lookupData = result.data;
      this.transactionType = this.lookupData.transactionType;
      this.chequeRandCode = this.lookupData.chequeRandCode;
      this.formData.patchValue({
        chequeRandCode: this.chequeRandCode,
      });
    });
  }
}
