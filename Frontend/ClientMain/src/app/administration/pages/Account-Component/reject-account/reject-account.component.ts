import { HttpParams } from '@angular/common/http';
import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { MatSnackBar, MatSnackBarHorizontalPosition, MatSnackBarVerticalPosition } from '@angular/material/snack-bar';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';
import { AccountsService } from 'src/app/administration/Service/AccountsService/accounts/accounts.service';

@Component({
  selector: 'app-reject-account',
  templateUrl: './reject-account.component.html',
  styleUrls: ['./reject-account.component.scss']
})
export class RejectAccountComponent implements OnInit, OnDestroy {
  horizontalPosition: MatSnackBarHorizontalPosition = 'center';
  verticalPosition: MatSnackBarVerticalPosition = 'top';
  loading = false;
  params: HttpParams;
  customer_lookup: any;
  submitted: boolean = false;
  destroy$: Subject<boolean> = new Subject<boolean>();
  constructor(
    private _snackBar: MatSnackBar,
    private accountsAPI: AccountsService,
    private notificationAPI: NotificationService,
    @Inject(MAT_DIALOG_DATA) public accountsdata: any,
    private dialogRef: MatDialogRef<RejectAccountComponent>
  ) {
  }
  ngOnDestroy(): void {
    this.destroy$.next(true);
    this.destroy$.complete();
  }
  ngOnInit(): void {
  }
  onSubmit() {
    this.loading = true;
      this.params = new HttpParams()
        .set('acid', this.accountsdata.acid);
      this.accountsAPI.rejectAccount(this.params).pipe(takeUntil(this.destroy$)).subscribe(
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
