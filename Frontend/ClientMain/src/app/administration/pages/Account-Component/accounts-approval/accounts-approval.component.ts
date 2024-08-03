import { HttpParams } from '@angular/common/http';
import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { takeUntil } from 'rxjs/operators';
import { AccountUsersComponent } from '../account-users/account-users.component';
import { MAT_DIALOG_DATA, MatDialog, MatDialogRef } from '@angular/material/dialog';
import { MatSnackBar, MatSnackBarHorizontalPosition, MatSnackBarVerticalPosition } from '@angular/material/snack-bar';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';
import { AccountsService } from 'src/app/administration/Service/AccountsService/accounts/accounts.service';
import { Subject } from 'rxjs';

@Component({
  selector: 'app-accounts-approval',
  templateUrl: './accounts-approval.component.html',
  styleUrls: ['./accounts-approval.component.scss']
})
export class AccountsApprovalComponent implements OnInit, OnDestroy {
  horizontalPosition: MatSnackBarHorizontalPosition = 'center';
  verticalPosition: MatSnackBarVerticalPosition = 'top';
  error: any;
  results: any;
  isEnabled = false;
  loading = false;
  params: HttpParams;
  customer_lookup: any;
  submitted: boolean = false;
  destroy$: Subject<boolean> = new Subject<boolean>();
  constructor(
    private fb: FormBuilder,
    private dialog: MatDialog,
    private _snackBar: MatSnackBar,
    private accountsAPI: AccountsService,
    private notificationAPI: NotificationService,
    @Inject(MAT_DIALOG_DATA) public accountsdata: any,
    private dialogRef: MatDialogRef<AccountsApprovalComponent>
  ) {
  }
  ngOnDestroy(): void {
    this.destroy$.next(true);
    this.destroy$.complete();
  }
  ngOnInit(): void {
    this.initForm();
  }
  formData: FormGroup = this.fb.group({
    approver: ['', Validators.required],
    transactionCode: ['', Validators.required]
  });
  get f() {
    return this.formData.controls;
  }
  initForm() {
    this.formData = this.fb.group({
      approver: ['', Validators.required],
      transactionCode: [this.accountsdata.acid]
    });
  }
  approverCodeLookup(): void {
    const dialogRef = this.dialog.open(AccountUsersComponent, {
      width: '60%',
      autoFocus: false,
      maxHeight: '90vh'
    });
    dialogRef.afterClosed().pipe(takeUntil(this.destroy$)).subscribe(
      (result: { data: any; }) => {
      this.customer_lookup = result.data;
      this.formData.controls.approver.setValue(this.customer_lookup.username);
    });
  }
  onSubmit() {
    this.loading = true;
    this.submitted = true;
    if (this.formData.valid) {
      this.params = new HttpParams()
        .set('acid', this.accountsdata.acid)
        .set('approver', this.formData.controls.approver.value);
      this.accountsAPI.approval(this.params).pipe(takeUntil(this.destroy$)).subscribe(
        {
          next: (
            (res) => {
              if (res.statusCode === 200) {
                this.loading = false;
                this.notificationAPI.alertSuccess(res.message);
                this.dialogRef.close();
              } else {
                this.loading = false;
                this._snackBar.open(res.message, "X", {
                  horizontalPosition: this.horizontalPosition,
                  verticalPosition: this.verticalPosition,
                  duration: 5000,
                  panelClass: ['red-snackbar', 'login-snackbar'],
                });
                this.dialogRef.close();
              }
            }
          ),
          error: (
            (err) => {
              this.loading = false;
              this._snackBar.open("Server Error: !!", "X", {
                horizontalPosition: this.horizontalPosition,
                verticalPosition: this.verticalPosition,
                duration: 5000,
                panelClass: ['red-snackbar', 'login-snackbar'],
              });
            }
          ),
          complete: (
            () => {

            }
          )
        }
      )
    } else {
      this.loading = false;
      this._snackBar.open("Form Request invalid: !!", "X", {
        horizontalPosition: this.horizontalPosition,
        verticalPosition: this.verticalPosition,
        duration: 5000,
        panelClass: ['red-snackbar', 'login-snackbar'],
      });
    }
  }
}
