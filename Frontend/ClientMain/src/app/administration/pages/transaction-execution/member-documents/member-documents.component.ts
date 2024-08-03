import { Component, Inject, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialog, MatDialogConfig, MatDialogRef } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { Subject } from 'rxjs';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';
import { MembershipService } from 'src/app/administration/Service/Membership/membership.service';
import { MembershipDocumentsComponent } from '../../MembershipComponent/Membership/membership-documents/membership-documents.component';
import { AccountDoumentsComponent } from '../../Account-Component/account-douments/account-douments.component';

@Component({
  selector: 'app-member-documents',
  templateUrl: './member-documents.component.html',
  styleUrls: ['./member-documents.component.scss']
})
export class MemberDocumentsComponent implements OnInit, OnDestroy {
  memberDocumentsColumns: string[] = [
    "index",
    "documentTitle",
    'view'
  ];
  accountDocumentsColumns: string[] = [
    "index",
    "documentTitle",
    'view'
  ];
  memberDocumentsDataSource: MatTableDataSource<any>;
  @ViewChild("memberDocumentsPaginator") memberDocumentsPaginator!: MatPaginator;
  @ViewChild(MatSort) memberDocumentsSort!: MatSort;



  accountDocumentsDataSource: MatTableDataSource<any>;
  @ViewChild("accountDocumentsPaginator") accountDocumentsPaginator!: MatPaginator;
  @ViewChild(MatSort) accountDocumentsSort!: MatSort;
  error: any;
  results: any;
  loading = false;
  dialogConfig: MatDialogConfig<any>;
  destroy$: Subject<boolean> = new Subject<boolean>();
  customerCode: any;
  accuntsDocuments: any;
  constructor(
    private dialog: MatDialog,
    private membershipAPI: MembershipService,
    private notificationAPI: NotificationService,
    @Inject(MAT_DIALOG_DATA) public accountlookupData: any
  ) {
    this.customerCode = accountlookupData.customerCode;
    this.accuntsDocuments = accountlookupData.accountDocuments;
    console.log("accountlookupData", accountlookupData);
  }
  ngOnDestroy(): void {
    this.destroy$.next(true);
    this.destroy$.complete();
  }
  ngOnInit(): void {
    this.getMemberDocuments();
    this.getAccountDocuments();
  }
  getMemberDocuments() {
    this.loading = true;
    this.membershipAPI.documentsByCode(this.customerCode).subscribe(
      res => {
        if (res.statusCode === 200) {
          this.loading = false;
          this.results = res.entity;
          this.memberDocumentsDataSource = new MatTableDataSource(this.results);
          this.memberDocumentsDataSource.paginator = this.memberDocumentsPaginator;
          this.memberDocumentsDataSource.sort = this.memberDocumentsSort;
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
  getAccountDocuments() {
    this.loading = true;
    this.accountDocumentsDataSource = new MatTableDataSource(this.accuntsDocuments);
    this.accountDocumentsDataSource.paginator = this.accountDocumentsPaginator;
    this.accountDocumentsDataSource.sort = this.accountDocumentsSort;
  }
  onViewMemberDoument(id: number) {
    this.dialogConfig = new MatDialogConfig();
    this.dialogConfig.disableClose = true;
    this.dialogConfig.autoFocus = true;
    this.dialogConfig.width = "30%";
    this.dialogConfig.data = id;
    const dialogRef = this.dialog.open(MembershipDocumentsComponent, this.dialogConfig);
    dialogRef.afterClosed().subscribe(
      (_res: any) => {
        this.loading = false;
      })
  }
  onViewAccountDoument(sn: number) {
    this.dialogConfig = new MatDialogConfig();
    this.dialogConfig.disableClose = true;
    this.dialogConfig.autoFocus = true;
    this.dialogConfig.width = "30%";
    this.dialogConfig.data = sn;
    const dialogRef = this.dialog.open(AccountDoumentsComponent, this.dialogConfig);
    dialogRef.afterClosed().subscribe(
      (_res: any) => {
        this.loading = false;
      })
  }
  applyMemberDocumentsFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.accountDocumentsDataSource.filter = filterValue.trim().toLowerCase();
    if (this.accountDocumentsDataSource.paginator) {
      this.accountDocumentsDataSource.paginator.firstPage();
    }
  }
  applyAccountDocumentsFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.accountDocumentsDataSource.filter = filterValue.trim().toLowerCase();
    if (this.accountDocumentsDataSource.paginator) {
      this.accountDocumentsDataSource.paginator.firstPage();
    }
  }
}
