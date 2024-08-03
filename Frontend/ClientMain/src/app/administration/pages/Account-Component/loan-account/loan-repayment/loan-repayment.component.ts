import { Component, OnDestroy, OnInit } from '@angular/core';
import { Validators, FormBuilder } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';
import { AccountsService } from 'src/app/administration/Service/AccountsService/accounts/accounts.service';
import { LoansAccountsLookUpComponent } from '../loans-accounts-look-up/loans-accounts-look-up.component';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { MatRadioChange } from '@angular/material/radio';

@Component({
  selector: 'app-loan-repayment',
  templateUrl: './loan-repayment.component.html',
  styleUrls: ['./loan-repayment.component.scss']
})
export class LoanRepaymentComponent implements OnInit, OnDestroy {
  loading = false;
  showAmount = false
  destroy$: Subject<boolean> = new Subject<boolean>();
  constructor(
    public fb: FormBuilder,
    private router: Router,
    private dialog: MatDialog,
    private notificationAPI: NotificationService,
    private accountsAPI: AccountsService) { }
  ngOnDestroy() {
    this.destroy$.next(true);
    this.destroy$.complete();
  }
  ngOnInit(): void {

  }

  formData = this.fb.group({
    amount: ["0"],
    loanAcid: ["", Validators.required],
    repayOneInstallment: ["Y", Validators.required],
    repaymentAccount: [""],
    useRepaymentAccount: ["N", Validators.required],
  });

  amountChange(event: MatRadioChange) {
    if (event.value == 'N') {
      this.showAmount = true;
    }
    else if (event.value == 'Y') {
      this.showAmount = false;
    }
  }

  onSubmit() {
    if (this.formData.valid) {
      this.accountsAPI.repayLoan(this.formData.value).pipe(takeUntil(this.destroy$)).subscribe(
        (data) => {
          if (data.statusCode === 200) {
            this.loading = false;
            this.notificationAPI.alertSuccess(data.message);
            this.router.navigate([`/system`], { skipLocationChange: true });
          } else {
            this.loading = false;
            this.notificationAPI.alertWarning(data.message);
          }
        }, (err) => {
          this.loading = false;
          this.notificationAPI.alertWarning("Server Error: !!");
        }
      );
    } else {
      this.loading = false;
      this.notificationAPI.alertWarning("Salary Charges Form Data is Invalid");
    }
  }

  // handleChange(event: MatRadioChange) {
  //   if (event.value == 'Y') {
  //     this.showRepayment = true;
  //   }
  //   else if (event.value == 'N') {
  //     this.showEventId = false;
  //   }
  // }

  loansAccountLookUp(): void {
    const dialogRef = this.dialog.open(LoansAccountsLookUpComponent, {
      width: '45%',
      autoFocus: false,
      maxHeight: '90vh',
    });
    dialogRef.afterClosed().subscribe(result => {
      this.formData.controls.loanAcid.setValue(result.data.acid);
    });
  }
}
