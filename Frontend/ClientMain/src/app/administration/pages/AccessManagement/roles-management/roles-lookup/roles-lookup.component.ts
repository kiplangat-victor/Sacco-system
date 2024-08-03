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
import { WorkClassService } from '../../work-class/work-class.service';
import { RolesManagementComponent } from '../roles-management.component';
import { RolesService } from '../roles.service';

@Component({
  selector: 'app-roles-lookup',
  templateUrl: './roles-lookup.component.html',
  styleUrls: ['./roles-lookup.component.scss']
})
export class RolesLookupComponent implements OnInit {

  horizontalPosition: MatSnackBarHorizontalPosition = 'end';
  verticalPosition: MatSnackBarVerticalPosition = 'top';
  title = 'export-table-data-to-any-format';
  displayedColumns: string[] = [
    'index',
    'role',
    'postedTime',
    'verifiedFlag',
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

  today = new Date();
  tommorrowDate = new Date(new Date().setDate(this.today.getDate() + 1));
  priorDate = new Date(new Date().setDate(this.today.getDate() - 30));

  loading= false;

  constructor(
    public dialogRef: MatDialogRef<RolesManagementComponent>,
    private router: Router,
    private ngZone: NgZone,
    private _snackBar: MatSnackBar,
    public fb: UntypedFormBuilder,
    private datepipe: DatePipe,
    private rolesService: RolesService
  ) {}
  ngOnInit() {
    this.getRoles();
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
  getRoles() {
    this.loading = true;
    this.subscription = this.rolesService
      .find()
      .subscribe((res) => {
        this.respData = res;
        this.loading = false;
        this.dataSource = new MatTableDataSource(this.respData.entity);
        this.dataSource.paginator = this.paginator;
        this.dataSource.sort = this.sort;
      });
  }
  onSelect(data: any) {
    this.dialogRef.close({ event: 'close', data: data });
  }
}
