import { DatePipe } from '@angular/common';
import { Component, NgZone, OnInit, ViewChild } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSnackBarHorizontalPosition, MatSnackBarVerticalPosition, MatSnackBar } from '@angular/material/snack-bar';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { AuthService } from 'src/@core/AuthService/auth.service';
import { WorkClassComponent } from '../../work-class/work-class.component';
import { WorkClassService } from '../../work-class/work-class.service';

@Component({
  selector: 'app-teller-lookup',
  templateUrl: './teller-lookup.component.html',
  styleUrls: ['./teller-lookup.component.scss']
})
export class TellerLookupComponent implements OnInit {

  horizontalPosition: MatSnackBarHorizontalPosition = 'end';
  verticalPosition: MatSnackBarVerticalPosition = 'top';
  title = 'export-table-data-to-any-format';
  displayedColumns: string[] = [
    'index',
    'username','role','phoneNo','email','solCode',
  ];

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
  loading= false;
  constructor(
    public dialogRef: MatDialogRef<WorkClassComponent>,
    public fb: UntypedFormBuilder,
    private authService: AuthService,
  ) {}
  ngOnInit() {
    this.getUsers();
  }
  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }
  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();
    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }
  getUsers() {
    this.loading = true;
    this.subscription =  this.authService.allTellers().subscribe(res => {
        this.respData = res;
        this.loading = false;
        this.dataSource = new MatTableDataSource(this.respData);
        this.dataSource.paginator = this.paginator;
        this.dataSource.sort = this.sort;
      });
  }
  onSelect(data: any) {
    this.dialogRef.close({ event: 'close', data: data });
  }
}
