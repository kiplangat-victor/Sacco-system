import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { Subject, Subscription } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { InterestcodeserviceService } from '../interestcodeservice.service';

@Component({
  selector: 'app-interestcodelookup',
  templateUrl: './interestcodelookup.component.html',
  styleUrls: ['./interestcodelookup.component.scss']
})
export class InterestcodelookupComponent implements OnInit, OnDestroy {
  displayedColumns: string[] = ['index', 'interestCode', 'interestName', 'interestType', 'postedBy', 'verifiedFlag'];
  dataSource!: MatTableDataSource<any>;
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;
  subscription!: Subscription;
  data: any;
  error: any;
  employeeEmail: any;
  employee_id: any;
  creatingAccount = false;
  formData: any;
  respData: any;
  loading: boolean = false;
  destroy$: Subject<boolean> = new Subject<boolean>();
  constructor(
    public dialogRef: MatDialogRef<InterestcodelookupComponent>,
    private interestAPI: InterestcodeserviceService
  ) { }
  ngOnInit() {
    this.getData();
  }
  ngOnDestroy(): void {
    this.destroy$.next(true);
    this.destroy$.complete();
  }

  getData() {
    this.loading = true;
    this.subscription = this.interestAPI.find().pipe(takeUntil(this.destroy$)).subscribe(
      res => {
        this.respData = res;
        this.loading = false;
        this.dataSource = new MatTableDataSource(this.respData.entity);
        this.dataSource.paginator = this.paginator;
        this.dataSource.sort = this.sort;
      }, err => {
        this.loading = false;

      })
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
