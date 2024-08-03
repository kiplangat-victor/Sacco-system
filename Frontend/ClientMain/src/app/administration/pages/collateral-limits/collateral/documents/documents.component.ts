import { Component, Inject, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';
import { CollateralService } from 'src/app/administration/Service/Collaterals/collateral.service';

@Component({
  selector: 'app-documents',
  templateUrl: './documents.component.html',
  styleUrls: ['./documents.component.scss']
})
export class DocumentsComponent implements OnInit {
  error: any;
  results: any;
  loading = false;
  constructor(
    private collateralAPI: CollateralService,
    private notificationAPI: NotificationService,
    @Inject(MAT_DIALOG_DATA) public document: any,
    private matDialogRef: MatDialogRef<DocumentsComponent>
  ) {}

  ngOnInit(): void {
    this.getCollateralDocument(this.document);
  }
  getCollateralDocument(document: any) {
    this.loading = true;
    this.collateralAPI.retrieveDocument(this.document).subscribe(
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
