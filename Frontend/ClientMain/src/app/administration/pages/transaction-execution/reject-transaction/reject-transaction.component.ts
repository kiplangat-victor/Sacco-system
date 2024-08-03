import { HttpParams } from '@angular/common/http';
import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { MatSnackBar, MatSnackBarHorizontalPosition, MatSnackBarVerticalPosition } from '@angular/material/snack-bar';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';
import { TransactionExecutionService } from '../transaction-execution.service';

@Component({
  selector: 'app-reject-transaction',
  templateUrl: './reject-transaction.component.html',
  styleUrls: ['./reject-transaction.component.scss']
})
export class RejectTransactionComponent implements OnInit, OnDestroy {
  horizontalPosition: MatSnackBarHorizontalPosition = 'center';
  verticalPosition: MatSnackBarVerticalPosition = 'top';
  loading = false;
  params: HttpParams;
  customer_lookup: any;
  submitted: boolean = false;
  destroy$: Subject<boolean> = new Subject<boolean>();
  constructor(
    private fb: FormBuilder,
    private _snackBar: MatSnackBar,
    private notificationAPI: NotificationService,
    private transactionAPI: TransactionExecutionService,
    @Inject(MAT_DIALOG_DATA) public transactiondata: any,
    private dialogRef: MatDialogRef<RejectTransactionComponent>
  ) {   }
  ngOnDestroy(): void {
    this.destroy$.next(true);
    this.destroy$.complete();
  }
  ngOnInit(): void {
    this.onInitFormData();
  }
  formData: FormGroup = this.fb.group({
    reason: ["", Validators.required],
    transactionCode: ["", Validators.required]
  });
  onInitFormData() {
    this.formData = this.fb.group({
      reason: ["Wrong Data", Validators.required],
      transactionCode: [this.transactiondata.transactionCode]
    });
  }
  get f(){
    return this.formData.controls;
  }
  onSubmit() {
    this.loading = true;
    this.submitted = true;
      this.params = new HttpParams()
        .set('reason', this.formData.controls.reason.value)
        .set('transactionCode', this.formData.controls.transactionCode.value);
      this.transactionAPI.rejectTransaction(this.params).pipe(takeUntil(this.destroy$)).subscribe(
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
  }
}
