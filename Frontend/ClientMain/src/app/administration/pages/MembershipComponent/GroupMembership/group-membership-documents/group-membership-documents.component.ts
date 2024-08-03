import { Component, Inject, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';
import { GroupMembershipService } from 'src/app/administration/Service/GroupMembership/group-membership.service';

@Component({
  selector: 'app-group-membership-documents',
  templateUrl: './group-membership-documents.component.html',
  styleUrls: ['./group-membership-documents.component.scss']
})
export class GroupMembershipDocumentsComponent implements OnInit {
  error: any;
  results: any;
  loading = false;
  isEnabled = false;
  constructor(
    private groupMembershipAPI: GroupMembershipService,
    private notificationAPI: NotificationService,
    @Inject(MAT_DIALOG_DATA) public document: any,
    private matDialogRef: MatDialogRef<GroupMembershipDocumentsComponent>
  ) {}

  ngOnInit(): void {
    this.getDocument();
  }
  getDocument() {
    this.loading = true;
    this.groupMembershipAPI.documents(this.document).subscribe(
      res => {
        if (res.statusCode === 200) {
          this.loading = false;
          this.isEnabled = true;
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
