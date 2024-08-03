import { Component, OnInit, ViewChild } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';
import { SavingsContributionLookupComponent } from '../../../Account-Component/savings-account/savings-contribution/savings-contribution-lookup/savings-contribution-lookup.component';
import { SavingsInstructionsService } from '../../../Account-Component/savings-account/savings-contribution/savings-instructions.service';
import { Router } from '@angular/router';


@Component({
  selector: 'app-direct-approvals',
  templateUrl: './direct-approvals.component.html',
  styleUrls: ['./direct-approvals.component.scss']
})
export class DirectApprovalsComponent implements OnInit {
  displayedColumns: string[] = ['index', 'savingCode', 'customerCode', 'amount', 'frequency', 'status', 'actions'];
  dataSource!: MatTableDataSource<any>;
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;
  data: any;
  respData: any;
  loading: boolean = false;
  destroy$: Subject<boolean> = new Subject<boolean>();
  constructor(
    private notificationAPI: NotificationService,
    private router: Router,
    private savingsContPI: SavingsInstructionsService
  ) { }
  ngOnInit() {
    this.getData();
  }
  ngOnDestroy() {
    this.destroy$.next(true);
    this.destroy$.complete();
  }
  getData() {
    this.loading = true;
    this.savingsContPI.findUnverified().pipe(takeUntil(this.destroy$)).subscribe(
      (data) => {
        if (data.statusCode === 302) {
          this.respData = data.entity;
          console.log(this.respData);
          this.loading = false;
          this.dataSource = new MatTableDataSource(this.respData);
          this.dataSource.paginator = this.paginator;
        } else {
          this.loading = false;
        }
      }, (err) => {
        this.loading = false;
        this.notificationAPI.alertWarning("Server Error: !!");
      }
    );
  }
  onSelect(data: any) {
    
  }
  
  verify(row:any) {
    console.log(row);    
    this.savingsContPI.verify(row.id).pipe(takeUntil(this.destroy$)).subscribe(res => {
      if (res.statusCode == 200) {
        let results = res;
        this.loading = false;
        this.notificationAPI.alertSuccess(results.message);
        const index = this.respData.indexOf(row, 0);
        if (index > -1) {
          this.respData.splice(index, 1);
          this.dataSource = new MatTableDataSource(this.respData);
        }else{
          console.log("Did not find item to delete");
        }
      } else {
        let results = res;
        this.loading = false;
        this.getData();
        this.notificationAPI.alertWarning(results.message);
      }
    }, err => {
      this.loading = false;
      this.getData();
      this.notificationAPI.alertWarning("Server Error: !!");
    })
  }
  
  view(row:any){
    this.router.navigate([`/system/savings-instruction-contribution/data/view`], { skipLocationChange: true, 
      queryParams: { formData: {function_type:"VERIFY", savingCode: row.savingCode}, fetchData: row} });
    console.log(row);
  }
}
