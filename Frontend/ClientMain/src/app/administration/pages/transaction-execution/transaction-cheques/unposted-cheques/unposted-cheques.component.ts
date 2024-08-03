import { Component, OnInit,ViewChild } from '@angular/core';
import {MatTableModule} from '@angular/material/table';
import { HttpParams } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { Router } from '@angular/router';
import { Subject, Subscription } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';
import { TransactionExecutionService } from '../../transaction-execution.service';
import {MatButtonModule} from '@angular/material/button';
import { DataStoreService } from 'src/@core/helpers/data-store.service';





@Component({
  selector: 'app-unposted-cheques',
  templateUrl: './unposted-cheques.component.html',
  styleUrls: ['./unposted-cheques.component.scss'] ,
  standalone: true,
  imports: [MatTableModule,MatButtonModule],
})
export class UnpostedChequesComponent implements OnInit {


  salariesColumns: string[] = [
    'index',
    'type',
    'amount',
    'account',
    'uniqueCode',
    'verified_flag',
    'verified_flag_2',
    'entryTime',
    'status',
    'verify'
    
  ];

  salariesDataSource: MatTableDataSource<any>;
  @ViewChild("salariesPaginator") salariesPaginator!: MatPaginator;
  @ViewChild(MatSort) salariesSort!: MatSort;
  currentUser: any;
  functionMainArray: any[];
  functionArray: any[];
  loading: boolean = false;
  params: HttpParams;
  results: any;
  destroy$: Subject<boolean> = new Subject<boolean>();
  fromDate: any = new Date(Date.now() - 360 * (3600 * 1000 * 24));
  toDate: any = new Date(Date.now() + (3600 * 1000 * 24));
  transData: any;
  constructor(public fb: FormBuilder,
    private router: Router,
    private dataStoreApi: DataStoreService,
    private notificationAPI: NotificationService,
    private transactionAPI: TransactionExecutionService,
  ) {    
    this.currentUser = JSON.parse(localStorage.getItem('auth-user'));
    this.functionMainArray = this.dataStoreApi.getActionsByPrivilege("TRANSACTION MAINTENANCE");

    this.functionArray = this.functionMainArray.filter(
      arr =>
        arr === 'POST CHEQUE' ||
        arr === 'CLEAR CHEQUE' );
   }

  ngOnInit(): void {
    console.log(this.functionArray);
    if(this.functionArray.length > 0) {
      this.getCheques();
    }
  }

  getCheques() {
    this.loading = true;
    this.transactionAPI.getUnclosedCheques().pipe(takeUntil(this.destroy$)).subscribe(
      {
        next: (
          (res) => {
            console.log(res);
            if (res.statusCode === 200) {
              this.transData = res.entity;
              this.loading = false;
              let newData =  []
              this.transData.forEach(element => {
                if(element.status == "Cleared" && this.functionArray.includes('POST CHEQUE')) {
                    element.action = "Post";
                    newData.push(element);
                } else if(element.status == "Not Cleared" && this.functionArray.includes('CLEAR CHEQUE')) {
                  element.action = "Clear";
                  newData.push(element);
                }
              });
              this.salariesDataSource = new MatTableDataSource(newData);
              this.salariesDataSource.paginator = this.salariesPaginator;
              this.salariesDataSource.sort = this.salariesSort;
            } else {
              this.loading = false;
            }
          }
        ),
        error: (
          (err) => {
            console.log(err);
            this.loading = false;
            this.notificationAPI.alertWarning("Server Error: !!");
          }
        ),
        complete: (
          () => {

          }
        )
      }
    ), Subscription;
  }


  action(element) {
    if(element.status == "Cleared"){
      this.router.navigate([`/system/transactions/cheques-data-view`], {
        skipLocationChange: true, queryParams: {
          formData: {
            function_type: 'POST CHEQUE',
            chequeRandCode: element.uniqueCode,
            backBtn: ["APPROVAL"]
          }
        }
      });
  }else{
    this.router.navigate([`/system/transactions/cheques-data-view`], {
      skipLocationChange: true, queryParams: {
        formData: {
          function_type: 'CLEAR CHEQUE',
          chequeRandCode: element.uniqueCode,
          backBtn: ["APPROVAL"]
        }
      }
    });
  }
   
  }
  salariesFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.salariesDataSource.filter = filterValue.trim().toLowerCase();
    if (this.salariesDataSource.paginator) {
      this.salariesDataSource.paginator.firstPage();
    }
  }
}

   


