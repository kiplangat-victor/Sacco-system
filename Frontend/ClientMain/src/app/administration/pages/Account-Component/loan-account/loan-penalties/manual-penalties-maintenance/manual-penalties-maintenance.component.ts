import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { DataStoreService } from 'src/@core/helpers/data-store.service';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';
import { LoansAccountsLookUpComponent } from '../../loans-accounts-look-up/loans-accounts-look-up.component';
import { LoanPenaltiesLookupComponent } from '../loan-penalties-lookup/loan-penalties-lookup.component';

@Component({
  selector: 'app-manual-penalties-maintenance',
  templateUrl: './manual-penalties-maintenance.component.html',
  styleUrls: ['./manual-penalties-maintenance.component.scss']
})
export class ManualPenaltiesMaintenanceComponent implements OnInit, OnDestroy {
  loading = false;
  submitted = false;
  account_code: any;
  functionArray: any;
  function_type: any;
  lookupData: any;
  showAccountCode = false;
  showAccountID: boolean = false;
  destroy$: Subject<boolean> = new Subject<boolean>();
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
        arr === 'DELETE' ||
        arr === 'INQUIRE' ||
        arr === 'MODIFY' ||
        arr === 'VERIFY');
  }
  ngOnDestroy(): void {
    this.destroy$.next(true);
    this.destroy$.complete();
  }
  formData = this.fb.group({
    function_type: ['', Validators.required],
    id: ['', Validators.required],
    acid: ['', Validators.required]
  });
  ngOnInit(): void {
  }

  onChange(event: any) {
    this.function_type = event.target.value;
    if (event.target.value == "ADD") {
      this.showAccountCode = true;
      this.showAccountID = false;
      this.formData.controls.id.setValue("-");
    }
    else if (event.target.value !== "ADD") {
      this.showAccountID = true;
      this.showAccountCode = false;
      this.formData.controls.acid.setValue("-");
    }
  }

  get f() {
    return this.formData.controls;
  }
  onSubmit() {
    this.loading = true;
    if (this.formData.valid) {
      this.loading = false;
      this.router.navigate([`system/loan/account/penalties/data/view`], { skipLocationChange: true, queryParams: { formData: this.formData.value } });
    }
    else if (!this.formData.valid) {
      if (this.formData.controls.function_type.value == "") {
        this.loading = false;
        this.submitted = true;
        this.notificationAPI.alertWarning("Choose Loan Penalty Account Function");
      } else if (this.formData.controls.id.value == "") {
        this.loading = false;
        this.submitted = true;
        this.notificationAPI.alertWarning("Loan Penalty Account ID is Empty");
      } else {
        this.loading = false;
        this.submitted = true;
        this.notificationAPI.alertWarning("Loan Penalty Details not Allowed");
      }

    }
  }
  loansAccountLookUp(): void {
    const dialogRef = this.dialog.open(LoansAccountsLookUpComponent, {
      width: '45%',
      autoFocus: false,
      maxHeight: '90vh',
    });
    dialogRef.afterClosed().subscribe(result => {
      this.lookupData = result.data;
      this.formData.controls.acid.setValue(this.lookupData.acid);
    });
  }
  loanPenaltyCodeLookUp(): void {
    const dialogRef = this.dialog.open(LoanPenaltiesLookupComponent, {
      width: '75%',
      autoFocus: false,
      maxHeight: '90vh'
    });
    dialogRef.afterClosed().pipe(takeUntil(this.destroy$)).subscribe(result => {
      this.lookupData = result.data;
      this.formData.controls.id.setValue(this.lookupData.id);
    });
  }
}
