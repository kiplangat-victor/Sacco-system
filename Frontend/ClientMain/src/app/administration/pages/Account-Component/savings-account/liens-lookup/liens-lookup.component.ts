import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { DatePipe } from "@angular/common";
import { FormBuilder } from "@angular/forms";
import { MatDialogRef } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { Subject, Subscription } from "rxjs";
import { takeUntil } from 'rxjs/operators';
import { AccountsService } from "src/app/administration/Service/AccountsService/accounts/accounts.service";
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';
import { LiensServiceService } from '../liens-service.service';
import { HttpParams } from '@angular/common/http';

@Component({
  selector: 'app-liens-lookup',
  templateUrl: './liens-lookup.component.html',
  styleUrls: ['./liens-lookup.component.scss']
})
export class LiensLookupComponent implements OnInit, OnDestroy {

  displayedColumns: string[] = ['index', 'lienCode', 'sourceAcid', 'destinationAcid', 'lienType', 'lienAmount', 'verifiedFlag','postedTime'];
  dataSource!: MatTableDataSource<any>;
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;
  subscription!: Subscription;
  data: any;
  params: HttpParams;
  respData: any;
  loading: boolean = false;
  fromDate: any = new Date(Date.now() - (3600 * 1000 * 24));
  toDate: any = new Date(Date.now() + (3600 * 1000 * 24));
  destroy$: Subject<boolean> = new Subject<boolean>();
  constructor(
   private liensAPI: LiensServiceService,
   public fb: FormBuilder,
    private datepipe: DatePipe,
    private accountsAPI: AccountsService,
    private notificationAPI: NotificationService,
    public dialogRef: MatDialogRef<LiensLookupComponent>
  ) { }

  formData = this.fb.group({
    acid: [''],
    accountName: [""],
    customerCode: [''],
    nationalId: [''],
    fromDate: [this.fromDate],
    toDate: [this.toDate]
  });

  ngOnInit() {
    this.getData();
    this.getAccounts();
  }
  ngOnDestroy(): void {
    this.destroy$.next(true);
    this.destroy$.complete();
  }
  getData() {
    this.loading = true;
    this.liensAPI.getAll().pipe(takeUntil(this.destroy$)).subscribe(
      (data) => {
        if (data.statusCode === 302) {
          this.respData = data.entity;
          this.loading = false;
          this.dataSource = new MatTableDataSource(this.respData);
          this.dataSource.paginator = this.paginator;
        } else {
          this.loading = false;
        }
      }, (err) => {
        this.loading = false;
        this.notificationAPI.alertWarning("SERVER ERROR: TRY AGAIN LATER");
      }
    );
  }
  onSelect(data: any) {
    this.dialogRef.close({ event: 'close', data: data });
    
  }
  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();
    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }

  getAccounts() {
    this.loading = true;
    if (this.formData.valid) {
      // Build the params for API call
      // ...

      // Make the API call
      this.subscription = this.liensAPI
        .getAll().pipe(takeUntil(this.destroy$)).subscribe(
          (res) => {  // Handle API response and data binding
            this.loading = false;
            this.dataSource = new MatTableDataSource(res.entity);
            this.dataSource.paginator = this.paginator;
            this.dataSource.sort = this.sort;
            this.loading = false;
          },
          (err) => {
            // Handle API error
            this.loading = false;
            // Handle error notification
          }
        );
    } else if (!this.formData.valid) {
      this.loading = false;
      // Handle invalid form data notification
    }
  }

}