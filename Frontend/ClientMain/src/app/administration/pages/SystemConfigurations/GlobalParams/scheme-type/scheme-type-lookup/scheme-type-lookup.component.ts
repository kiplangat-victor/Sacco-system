import { Component, NgZone, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { UntypedFormBuilder, Validators } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSnackBar, MatSnackBarHorizontalPosition, MatSnackBarVerticalPosition } from '@angular/material/snack-bar';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { AuthService } from 'src/@core/AuthService/auth.service';
import { SchemeTypeService } from '../scheme-type.service';

export interface  ApiData {
  id:String;
  scheme_type: String;
  scheme_abbreviation: String;
  scheme_category: String;
  scheme_description: String;

}
@Component({
  selector: 'app-scheme-type-lookup',
  templateUrl: './scheme-type-lookup.component.html',
  styleUrls: ['./scheme-type-lookup.component.scss']
})
export class SchemeTypeLookupComponent implements OnInit, OnDestroy {
  horizontalPosition: MatSnackBarHorizontalPosition = 'end';
  verticalPosition: MatSnackBarVerticalPosition = 'top';
  title = 'export-table-data-to-any-format';
  displayedColumns: string[] = [ 'index','scheme_type','scheme_abbreviation','scheme_category','postedBy','verifiedFlag','verifiedBy'];
  dataSource!: MatTableDataSource<ApiData>;
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;
  subscription!: Subscription;
  data: any;
  error: any;
  employeeEmail: any;
  employee_id: any;
  creatingAccount = false;
  formData: any;
  loading: boolean = false;

  constructor(
    public dialogRef: MatDialogRef<SchemeTypeLookupComponent>,
    private router: Router,
    private ngZone: NgZone,
    private _snackBar: MatSnackBar,
    private authAPI: AuthService,
    public fb: UntypedFormBuilder,
    private schemeTypeApi: SchemeTypeService,
    ) { }
    ngOnInit() {
      this.getData();
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
  getData() {
    this.loading = true;
      this.subscription = this.schemeTypeApi.getSchemetypes().subscribe(res => {
        this.data = res;
        this.loading = false;
        // Binding with the datasource
        this.dataSource = new MatTableDataSource(this.data);
        this.dataSource.paginator = this.paginator;
        this.dataSource.sort = this.sort;
      })
    }
    onSelect(data:any){
      this.dialogRef.close({ event: 'close', data:data });
    }

}
