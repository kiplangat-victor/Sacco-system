import { takeUntil } from 'rxjs/operators';
import { Component, OnInit, OnDestroy } from '@angular/core';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { Subject } from 'rxjs';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';
import { GeneralAccountsLookupComponent } from '../../Account-Component/general-accounts-lookup/general-accounts-lookup.component';
import { MembershipLookUpComponent } from '../../MembershipComponent/Membership/membership-look-up/membership-look-up.component';
import { AccountImagesLookupComponent } from '../../transaction-execution/account-images-lookup/account-images-lookup.component';
import { TransactionExecutionService } from '../../transaction-execution/transaction-execution.service';
import { ChequeBookService } from '../cheque-books.service';

@Component({
  selector: 'app-cheque-book-maintenance',
  templateUrl: './cheque-book-maintenance.component.html',
  styleUrls: ['./cheque-book-maintenance.component.scss'],
})
export class ChequeBookMaintenanceComponent implements OnInit, OnDestroy {
  loading = false;
  function_type: string;
  branchCode: any;
  error: any;
  results: any;
  fetchData: any;
  fmData: any;
  isDisabled: boolean;
  postedBy: any;
  verifiedFlag: any;
  verifiedBy: any;
  lookupData: any;
  submitted: boolean;
  tellAccounName: any;
  tellName: any;
  formData: FormGroup;
  onShowResults = false;
  btnColor: any;
  btnText: any;
  hideBtn = false;
  accountlookupData: any;
  accountStatus: any;
  accountBalance: any;
  accountCurrency: any;
  accountName: any;
  accountReadonly = false;
  memberdialogData: any;
  chequeId: any;
  destroy$: Subject<boolean> = new Subject<boolean>();
  constructor(
    public fb: FormBuilder,
    private router: Router,
    private chequebookAPI: ChequeBookService,
    private dialog: MatDialog,
    private notificationService: NotificationService,
    private transactionAPI: TransactionExecutionService
  ) {
    this.fmData =
      this.router.getCurrentNavigation().extras.queryParams.formData;
    this.fetchData =
      this.router.getCurrentNavigation().extras.queryParams.fetchData;
    if (this.router.getCurrentNavigation().extras.queryParams == null) {
      this.router.navigate([`/system/`], { skipLocationChange: true });
    }
    this.function_type = this.fmData.function_type;
    this.chequeId = this.fmData.id;
    if (this.fetchData.entity != null) {
      this.postedBy = this.fetchData.entity.postedBy;
      this.verifiedFlag = this.fetchData.entity.verifiedFlag;
      this.verifiedBy = this.fetchData.entity.verifiedBy;
    }
  }
  ngOnDestroy(): void {
    this.destroy$.next(true);
    this.destroy$.complete();
  }
  ngOnInit() {
    this.getPage();
  }
  onInitEmptyForm() {
    this.formData = this.fb.group({
      accountName: ['', Validators.required],
      accountNumber: ['', Validators.required],
      expiryDate: [new Date()],
      dateOfIssue: [new Date()],
      noOfLeafs: ['', Validators.required],
      startSerialNo: ['', Validators.required],
      endSerialNo: ['', Validators.required],
      customerCode: ['', Validators.required]
    });
  }
  onInitPrefilledForm() {
    this.formData = this.fb.group({
      id: [this.fetchData.entity.id, Validators.required],
      accountName: [this.fetchData.entity.accountName, Validators.required],
      accountNumber: [this.fetchData.entity.accountNumber, Validators.required],
      expiryDate: [this.fetchData.entity.expiryDate, Validators.required],
      dateOfIssue: [this.fetchData.entity.dateOfIssue, Validators.required],
      noOfLeafs: [this.fetchData.entity.noOfLeafs, Validators.required],
      startSerialNo: [this.fetchData.entity.startSerialNo, Validators.required],
      endSerialNo: [this.fetchData.entity.endSerialNo, Validators.required],
      customerCode: [this.fetchData.entity.customerCode, Validators.required]
    });
    this.getAccountBalance(this.formData.value.accountNumber);
  }
  membershipLookup(): void {
    const dialogRef = this.dialog.open(MembershipLookUpComponent, {
      width: '50%',
      autoFocus: false,
      maxHeight: '90vh'
    });
    dialogRef.afterClosed().pipe(takeUntil(this.destroy$)).subscribe((results) => {
      this.memberdialogData = results.data;
      this.formData.patchValue({
        customerCode: this.memberdialogData.customerCode
      });
    });
  }
  accountLookup(): void {
    if (!this.formData.value.accountNumber.trim()) {
      const dialogRef = this.dialog.open(GeneralAccountsLookupComponent, {
        width: '50%',
      autoFocus: false,
      maxHeight: '90vh'
      });
      dialogRef.afterClosed().pipe(takeUntil(this.destroy$)).subscribe((result) => {
        this.accountlookupData = result.data;
        if (this.accountlookupData.acid) {
          this.formData.patchValue({
            accountNumber: this.accountlookupData.acid,
            accountName: this.accountlookupData.accountName,
          });
          this.getAccountBalance(this.accountlookupData.acid);
        }
      });
    } else if (this.formData.value.accountNumber.trim()) {
      this.getAccountBalance(this.formData.value.accountNumber);
    }
  }
  getAccountBalance(acid: any) {
    this.transactionAPI.getAccountDetails(acid).pipe(takeUntil(this.destroy$)).subscribe(
      (res) => {
        this.accountBalance = res.account_balance;
        this.accountCurrency = res.currency;
        this.accountName = res.account_name;
        this.accountStatus = res.account_status;
      },
      (err) => {
        this.error = err;
        this.loading = false;
        this.notificationService.alertWarning(this.error);
      }
    );
  }
  accountImagesLookup() {
    this.loading = true;
    if (this.formData.value.accountNumber) {
      this.loading = false;
      const dialogConfig = new MatDialogConfig();
      dialogConfig.disableClose = false;
      dialogConfig.autoFocus = true;
      dialogConfig.data = {
        data: this.formData.value.accountNumber,
      };
      const dialogRef = this.dialog.open(
        AccountImagesLookupComponent,
        dialogConfig
      );
      dialogRef.afterClosed().pipe(takeUntil(this.destroy$)).subscribe((result) => { });
    } else {
      this.loading = false;
      this.notificationService.alertWarning("Please select an account");
    }
  }
  disabledFormControl() {
    this.formData.disable();
  }
  get f() {
    return this.formData.controls;
  }
  getPage() {
    if (this.function_type == 'ADD') {
      this.onInitEmptyForm();
      this.btnColor = 'primary';
      this.btnText = 'SUBMIT';
    } else if (this.function_type == 'INQUIRE') {
      this.onInitPrefilledForm();
      this.disabledFormControl();
      this.isDisabled = true;
      this.hideBtn = true;
      this.onShowResults = true;
    } else if (this.function_type == 'MODIFY') {
      this.onInitPrefilledForm();
      this.btnColor = 'primary';
      this.btnText = 'MODIFY';
      this.onShowResults = true;
    } else if (this.function_type == 'VERIFY') {
      this.onInitPrefilledForm();
      this.disabledFormControl();
      this.isDisabled = true;
      this.btnColor = 'primary';
      this.btnText = 'VERIFY';
      this.onShowResults = true;
    } else if (this.function_type == 'DELETE') {
      this.onInitPrefilledForm();
      this.disabledFormControl();
      this.isDisabled = true;
      this.btnColor = 'warn';
      this.btnText = 'DELETE';
      this.onShowResults = true;
    }
  }
  onSubmit() {
    this.submitted = true;
    if (this.function_type == 'ADD') {
      this.loading = true;
      if (this.formData.valid) {
        this.chequebookAPI.create(this.formData.value).pipe(takeUntil(this.destroy$)).subscribe(
          (res) => {
            if (res.statusCode == 201) {
              this.loading = false;
              this.notificationService.alertSuccess(res.message);
            } else {
              this.loading = false;
              this.notificationService.alertWarning(res.message);
            }
            this.loading = false;
            this.router.navigate([`/system/chequebook/maintenance`], {
              skipLocationChange: true,
            });
          },
          (err) => {
            this.error = err;
            this.loading = false;
            this.notificationService.alertWarning(this.error);
          }
        );
      } else {
        this.loading = false;
        this.notificationService.alertWarning("Invalid Form Data");
      }
    }
    if (this.function_type == 'MODIFY') {
      this.loading = true;
      if (this.formData.valid) {
        this.chequebookAPI.modify(this.formData.value).pipe(takeUntil(this.destroy$)).subscribe(
          (res) => {
            this.results = res;
            this.loading = false;
            this.notificationService.alertSuccess("SUCCESS");
            this.router.navigate([`/system/chequebook/maintenance`], {
              skipLocationChange: true,
            });
          },
          (err) => {
            this.loading = false;
            this.error = err;
          }
        );
      } else {
        this.loading = false;
        this.notificationService.alertWarning("Invalid Form Data");
      }
    }
    if (this.function_type == 'VERIFY') {
      this.loading = true;
      this.chequebookAPI.verify(this.fetchData.id).pipe(takeUntil(this.destroy$)).subscribe(
        (res) => {
          this.results = res;
          this.loading = false;
          this.notificationService.alertSuccess("SUCCESS");
        },
        (err) => {
          this.loading = false;
          this.error = err;
          this.notificationService.alertWarning(this.error);
        }
      );
    }
    if (this.function_type == 'DELETE') {
      this.loading = true;
      this.chequebookAPI.delete(this.fetchData.id).pipe(takeUntil(this.destroy$)).subscribe(
        (res) => {
          this.results = res;
          this.loading = false;
          this.notificationService.alertSuccess("SUCCESS");
          this.router.navigate([`/system/chequebook/maintenance`], {
            skipLocationChange: true,
          });
        },
        (err) => {
          this.loading = false;
          this.error = err;
          this.notificationService.alertWarning(this.error);
        }
      );
    }
  }
}
