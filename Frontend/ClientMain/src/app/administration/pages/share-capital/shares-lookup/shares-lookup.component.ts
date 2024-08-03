import { DatePipe } from '@angular/common';
import { HttpParams } from '@angular/common/http';
import { Component, OnInit, ViewChild } from '@angular/core';
import { UntypedFormBuilder, Validators } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSnackBarHorizontalPosition, MatSnackBarVerticalPosition } from '@angular/material/snack-bar';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { Subscription } from 'rxjs';
import { ShareCapitalService } from '../share-capital.service';

@Component({
  selector: 'app-shares-lookup',
  templateUrl: './shares-lookup.component.html',
  styleUrls: ['./shares-lookup.component.scss']
})
export class SharesLookupComponent implements OnInit {
  horizontalPosition: MatSnackBarHorizontalPosition = 'end';
  verticalPosition: MatSnackBarVerticalPosition = 'top';
  title = 'export-table-data-to-any-format';
  displayedColumns: string[] = [ 'index','cust_code','cust_name','cust_joiningdate'];
  dataSource!: MatTableDataSource<any>;
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;
  subscription!: Subscription;
  data: any;
  error: any;
  employeeEmail: any;
  employee_id: any;
  creatingAccount = false;
  respData: any;
  loading = false;
  customer_branch_array: Object;
  today = new Date();
  priorDate = new Date(new Date().setDate(this.today.getDate() - 15));
  aheadDate = new Date(new Date().setDate(this.today.getDate() + 15));
  branches: Object;
  branchesRes: any;
  constructor(
    public dialogRef: MatDialogRef<SharesLookupComponent>,
    private datepipe: DatePipe,
    private shareService:ShareCapitalService,
    public fb: UntypedFormBuilder,
    ) { }
    ngOnInit() {

    }
    ngOnDestroy(): void {
      this.subscription.unsubscribe();
    }

    formData = this.fb.group({
      toDate: [this.aheadDate, [Validators.required]],
      fromDate: [this.priorDate, [Validators.required]],
    })

    loadData(params) {
      this.loading = true;
      this.subscription = this.shareService.getAllWithFilter( this.formData.value).subscribe(res=>{
        console.log(`received  retailers`);
        console.log(res);
        this.loading = false;
        this.respData = res.entity;
        this.dataSource = new MatTableDataSource(this.respData);
        this.dataSource.paginator = this.paginator;
        this.dataSource.sort = this.sort;
      }, err=>{
        this.loading = false;
        console.log("error getting all retailers");
      });
    }

    getFilteredShares() {
      console.log("Starting filter");
      console.log("form Data", this.formData.value);
     let params = new HttpParams()
     .set('fromDate', this.datepipe.transform(
       this.formData.controls.fromDate.value,
       "yyyy-MM-ddTHH:mm:ss"
     ))
     .set('toDate', this.datepipe.transform(
       this.formData.controls.toDate.value,
       "yyyy-MM-ddTHH:mm:ss"
     ));
     this.loadData( params);
    }

  onSubmit() {

    }

    applyFilter(event: Event) {
      const filterValue = (event.target as HTMLInputElement).value;
      this.dataSource.filter = filterValue.trim().toLowerCase();
      if (this.dataSource.paginator) {
        this.dataSource.paginator.firstPage();
      }
    }

    onSelect(data:any){
      this.dialogRef.close({ event: 'close', data:data });
    }
}
