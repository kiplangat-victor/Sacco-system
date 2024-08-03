import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import {
  MatSnackBar,
  MatSnackBarHorizontalPosition,
  MatSnackBarVerticalPosition
} from '@angular/material/snack-bar';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { TransactionExecutionMainComponent } from '../transaction-execution-main/transaction-execution-main.component';
import { TransactionExecutionService } from '../transaction-execution.service';

@Component({
  selector: 'app-account-images-lookup',
  templateUrl: './account-images-lookup.component.html',
  styleUrls: ['./account-images-lookup.component.scss'],
})
export class AccountImagesLookupComponent implements OnInit, OnDestroy {
  horizontalPosition: MatSnackBarHorizontalPosition = 'end';
  verticalPosition: MatSnackBarVerticalPosition = 'top';
  employeeEmail: any;
  employee_id: any;
  creatingAccount = false;
  respData: any;
  loading = true;
  customer_branch_array: Object;
  accountsImgData: any;
  passportImg: any;
  signatureImg: any;
  Data: any;
  destroy$: Subject<boolean> = new Subject<boolean>();
  constructor(
    public dialogRef: MatDialogRef<TransactionExecutionMainComponent>,
    @Inject(MAT_DIALOG_DATA) public data,
    private transactionAPI: TransactionExecutionService,
    public fb: UntypedFormBuilder,
    private _snackBar: MatSnackBar
  ) { }
  ngOnInit() {
    this.getAccountImages();
  }
  ngOnDestroy() {
    this.destroy$.next(true);
    this.destroy$.complete();
  }

  getAccountImages() {
    this.transactionAPI.getAccountImagesByCode(this.data.data).pipe(takeUntil(this.destroy$))
      .subscribe(
        (res) => {
          if (res.statusCode == 404) {
            this._snackBar.open('No Images Found For this Account !!', 'X', {
              horizontalPosition: this.horizontalPosition,
              verticalPosition: this.verticalPosition,
              duration: 3000,
              panelClass: ['red-snackbar', 'login-snackbar'],
            });
          } else {
            this.accountsImgData = res.entity;
            this.passportImg = this.accountsImgData[0].documentImage;
            this.signatureImg = this.accountsImgData[1].documentImage;
            this.loading = false;
          }
        },
        (err) => {
          this._snackBar.open('SERVER ERROR: ', 'X', {
            horizontalPosition: this.horizontalPosition,
            verticalPosition: this.verticalPosition,
            duration: 3000,
            panelClass: ['red-snackbar', 'login-snackbar'],
          });
        }
      );
  }
  closeDialog() {
    this.dialogRef.close();
  }

  onSelect(data: any) {
    this.dialogRef.close({ event: 'close', data: data });
  }
}
