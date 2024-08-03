import { Component, OnInit, ViewChild } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSnackBarHorizontalPosition, MatSnackBarVerticalPosition, MatSnackBar } from '@angular/material/snack-bar';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { Subscription } from 'rxjs';
import { WorkClassComponent } from '../work-class.component';
import { WorkClassService } from '../work-class.service';

@Component({
  selector: 'app-work-class-lookup',
  templateUrl: './work-class-lookup.component.html',
  styleUrls: ['./work-class-lookup.component.scss']
})
export class WorkClassLookupComponent implements OnInit {
  horizontalPosition: MatSnackBarHorizontalPosition = 'end';
  verticalPosition: MatSnackBarVerticalPosition = 'top';
  title = 'export-table-data-to-any-format';
  displayedColumns: string[] = [
    'index',
    'workClass',
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
  loading= false;

  constructor(
    public fb: FormBuilder,
    private workClassService: WorkClassService,
    public dialogRef: MatDialogRef<WorkClassComponent>
  ) {}
  ngOnInit() {
    this.getWorkClasses();
  }
  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }

  getWorkClasses() {
    this.loading = true;
    this.subscription = this.workClassService.find().subscribe((res) => {
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
  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();
    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }
}
