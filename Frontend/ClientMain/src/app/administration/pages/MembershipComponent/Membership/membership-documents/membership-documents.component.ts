import { Component, Inject, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';
import { MembershipService } from 'src/app/administration/Service/Membership/membership.service';

@Component({
  selector: 'app-membership-documents',
  templateUrl: './membership-documents.component.html',
  styleUrls: ['./membership-documents.component.scss']
})
export class MembershipDocumentsComponent implements OnInit {
  error: any;
  results: any;
  loading = false;
  constructor(
    private membershipAPI: MembershipService,
    private notificationAPI: NotificationService,
    @Inject(MAT_DIALOG_DATA) public document: any,
    private matDialogRef: MatDialogRef<MembershipDocumentsComponent>
  ) {   }

  ngOnInit(): void {
    this.getDocument(this.document);
  }
  getDocument(document: any) {
    this.loading = true;
    this.membershipAPI.documents(this.document).subscribe(
      res => {
        if (res.statusCode === 200) {
          this.loading = false;
        this.results = res.entity;
        } else {
          this.loading = false;
          this.results = res;
          this.notificationAPI.alertWarning(this.results.message);
        }
      }, err => {
        this.error = err;
        this.loading = false;
        this.notificationAPI.alertWarning(this.error);
      }
    )
  }

}
