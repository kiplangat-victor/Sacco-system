import { Component, Inject, OnInit, Optional, ViewChild } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatTableDataSource } from '@angular/material/table';
import { MatSort } from '@angular/material/sort';
import { MatPaginator } from '@angular/material/paginator';
import { MatSnackBarHorizontalPosition, MatSnackBarVerticalPosition } from '@angular/material/snack-bar';

import { Subscription } from 'rxjs';
import { ChrgPreferentialServiceService } from '../chrg-preferential-service.service';

@Component({
  selector: 'app-chrg-preferential-lookup',
  templateUrl: './chrg-preferential-lookup.component.html',
  styleUrls: ['./chrg-preferential-lookup.component.scss']
})
export class ChrgPreferentialLookupComponent implements OnInit {

  displayedColumns: string[] = [
    'index',
    'event_id',
    'event_type',
    'chrg_preferential',
    'action'
  ];
  dataSource!: MatTableDataSource<any>;
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;
  horizontalPosition: MatSnackBarHorizontalPosition = 'end';
  verticalPosition: MatSnackBarVerticalPosition = 'top';
  loading: boolean = false;
  error: any;

  fromDialog: any;
  subscription!:Subscription;
  chrgPrefData: any;
  constructor(
    public dialogRef: MatDialogRef<ChrgPreferentialLookupComponent>,
    private chrgPreferentialAPI: ChrgPreferentialServiceService,
    @Optional() @Inject(MAT_DIALOG_DATA) public data: any
  ) { }

  ngOnInit(): void {
    this.getData();
  }
  getData(){
    this.subscription = this.chrgPreferentialAPI.getChrgPreferentials().subscribe(res=>{
      this.chrgPrefData = res;
      this.loading = false;
      this.dataSource = new MatTableDataSource(this.chrgPrefData);
      this.dataSource.paginator = this.paginator;
      this.dataSource.sort = this.sort;
    },
      err => {
        this.error = err;
    });
  }

  onSelect(data:any){
    this.dialogRef.close({ event: 'close', data:data });
  }

  closeDialog() {
    this.dialogRef.close({ event: 'close', data: this.fromDialog });
  }
applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();
    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }
}