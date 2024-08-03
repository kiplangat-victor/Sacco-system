import { Component, Inject, OnInit } from '@angular/core';
import { MatDialog, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';
import { AccountsService } from '../../Service/AccountsService/accounts/accounts.service';

@Component({
  selector: 'app-universal-lookup',
  templateUrl: './universal-inquiry.component.html',
  styleUrls: ['./universal-inquiry.component.scss']
})
export class UniversalInquiryComponent implements OnInit {
  key: any
  function_type = "INQUIRE"
  entity: any
  response: any
  received_data = false;
  loading = false;
  destroy$: Subject<boolean> = new Subject<boolean>();

  constructor(
    private accountsAPI: AccountsService,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private notificationAPI: NotificationService,
    private mdDialogRef: MatDialogRef<UniversalInquiryComponent>
  ) { }

  ngOnInit(): void {
    this.key = this.data.key;
    this.entity = this.data.entity;

    console.log(this.data);

    if (this.entity == 'accounts') {
      this.getAccount();
    }
  }

  getAccount() {
    this.accountsAPI.retrieveAccount(this.key).pipe(takeUntil(this.destroy$)).subscribe(
      data => {
        this.received_data = true;
        this.response = data;
        this.entity = data.entity.accountType.toLowerCase();
      }, err => {
        this.loading = false;
        this.notificationAPI.alertWarning(err);
      }
    );
  }

  cancel() {
    this.mdDialogRef.close(false);
  }
}
