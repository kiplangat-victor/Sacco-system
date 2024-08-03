import { DatePipe } from '@angular/common';
import { HttpParams } from '@angular/common/http';
import { Component, OnInit, ViewChild } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { Subject, Subscription } from 'rxjs';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';
import { AccountsService } from 'src/app/administration/Service/AccountsService/accounts/accounts.service';

@Component({
  selector: 'app-cardnumber-look-up',
  templateUrl: './cardnumber-look-up.component.html',
  styleUrls: ['./cardnumber-look-up.component.scss']
})
export class CardnumberLookUpComponent implements OnInit {
 displayedColumns: string[] = [
    'index',
    'acid',
    'customerCode',
    'accountName',
    'verifiedFlag',
  ];
  dataSource!: MatTableDataSource<any>;
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;
  subscription!: Subscription;
  data: any;
  error: any;
  respData: any;
  loading: boolean;
  acid: any;
  params: HttpParams;
  destroy$: Subject<boolean> = new Subject<boolean>();
  fromDate: any = new Date(Date.now() - (3600 * 1000 * 24));
  toDate: any = new Date(Date.now() + (3600 * 1000 * 24));
  accountName: any;
  cardNumber: any;
  verifiedFlag: any;
  //datepipe: any;
  //productsAPI: any;
  //notificationAPI: any;

  
  constructor(public fb:FormBuilder, private datepipe:DatePipe, private accountsAPI: AccountsService
    ,private notificationAPI: NotificationService, public dialogRef:MatDialogRef<CardnumberLookUpComponent>) { }

    formData = this.fb.group({
      acid: [''],
      id: [''],
      accountName: [""],
      customerCode: [''],
      nationalId: [''],
      fromDate: [this.fromDate],
      toDate: [this.toDate]
    });

    ngOnDestroy(): void {
      this.destroy$.next(true);
      this.destroy$.complete();
    }

  
  ngOnInit(){this.getCards();
  }
  getCards() {
    this.loading = true;
    if (this.formData.valid) {
      this.params = new HttpParams()
        // .set("accountType", "SBA")
        // .set('acid', this.formData.value.acid)
        // .set('accountName', this.formData.value.accountName)
        // .set('customerCode', this.formData.value.customerCode)
        // .set('fromDate', this.datepipe.transform(this.formData.controls.fromDate.value, 'yyyy-MM-dd'))
        // .set('nationalId', this.formData.value.nationalId)
        // .set('toDate', this.datepipe.transform(this.formData.controls.toDate.value, 'yyyy-MM-dd'));
      this.subscription = this.accountsAPI
        .getCards(this.params).subscribe (
          (res) => {
            this.loading = false;
            this.data = res;
            this.respData = this.data.entity;
            this.dataSource = new MatTableDataSource(this.respData);
            this.dataSource.paginator = this.paginator;
            this.dataSource.sort = this.sort;
            this.loading = false;
            console.log(this.respData , "resp")
            this.accountName = this.respData.accountName
            this.cardNumber = this.respData.cardNumber
            this.verifiedFlag = this.respData.verifiedFlag
            //console.log(this.accountName,'accountName');

          },
          (err) => {
            this.loading = false;
            this.error = err;
            this.notificationAPI.alertWarning(this.error);
          }
        );
    } else if (!this.formData.valid) {
      this.loading = false;
      this.notificationAPI.alertWarning("ATM CARDNUMBER LOOKUP FORM DATA INVALID");
    }
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
  
  }



