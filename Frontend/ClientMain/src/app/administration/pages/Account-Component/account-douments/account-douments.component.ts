import { Component, Inject, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';
import { AccountsService } from 'src/app/administration/Service/AccountsService/accounts/accounts.service';


@Component({
  selector: 'app-account-douments',
  templateUrl: './account-douments.component.html',
  styleUrls: ['./account-douments.component.scss']
})
export class AccountDoumentsComponent implements OnInit {

  error: any;
  results: any;
  isEnabled = false;
  loading = false;
  constructor(
    private accountsAPI: AccountsService,
    private notificationAPI: NotificationService,
    @Inject(MAT_DIALOG_DATA) public documentSn: any,
    private matDialogRef: MatDialogRef<AccountDoumentsComponent>
  ) { console.log("Dialog data", document) }

  ngOnInit(): void {
    this.getDocument();
  }
  getDocument() {
    this.loading = true;
    this.accountsAPI.documents(this.documentSn).subscribe(
      {
        next: (
          (res) => {
            if (res.statusCode === 302) {
              this.loading = false;
              this.isEnabled = true;
              this.results = res.entity;
            } else {
              this.loading = false;
              this.isEnabled = false;
              this.notificationAPI.alertWarning(res.message);
              this.matDialogRef.close();
            }
          }
        ),
        error: (
          (err) => {
            this.loading = false;
            this.isEnabled = false;
            this.notificationAPI.alertWarning("Server Error: !!");
            this.matDialogRef.close();
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
