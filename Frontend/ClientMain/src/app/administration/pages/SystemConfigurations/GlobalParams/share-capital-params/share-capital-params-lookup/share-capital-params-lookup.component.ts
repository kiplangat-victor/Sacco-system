import { Component,OnDestroy, OnInit, ViewChild } from '@angular/core';
import { UntypedFormBuilder} from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSnackBarHorizontalPosition, MatSnackBarVerticalPosition } from '@angular/material/snack-bar';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { AuthService } from 'src/@core/AuthService/auth.service';
import { ShareCapitalParamsService } from '../share-capital-params.service';

@Component({
  selector: 'app-share-capital-params-lookup',
  templateUrl: './share-capital-params-lookup.component.html',
  styleUrls: ['./share-capital-params-lookup.component.scss']
})
export class ShareCapitalParamsLookupComponent  implements OnInit, OnDestroy {
  horizontalPosition: MatSnackBarHorizontalPosition = 'end';
  verticalPosition: MatSnackBarVerticalPosition = 'top';
  title = 'export-table-data-to-any-format';
  displayedColumns: string[] = [ 'index','share_capital_unit','share_capital_amount_per_unit','share_min_unit', 'isActive','postedBy','verifiedFlag','verifiedBy'];
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
  loading: boolean = false;

  constructor(
    public fb: UntypedFormBuilder,
    private ParamsService:ShareCapitalParamsService,
    public dialogRef: MatDialogRef<ShareCapitalParamsLookupComponent >,
    ) { }
    ngOnInit() {
      this.getData();
    }
    ngOnDestroy(): void {
      this.subscription.unsubscribe();
    }

  getData() {
    this.loading = true;
      this.subscription = this.ParamsService.findAll().subscribe(res => {
        this.data = res;
        console.log("Data", res);

        this.loading = false;
        // Binding with the datasource
        this.dataSource = new MatTableDataSource(this.data.entity);
        this.dataSource.paginator = this.paginator;
        this.dataSource.sort = this.sort;
      })
    }
    onSelect(data:any){
      this.dialogRef.close({ event: 'close', data:data });
    }
    applyFilter(event: Event) {
      const filterValue = (event.target as HTMLInputElement).value;
      this.dataSource.filter = filterValue.trim().toLowerCase();
      if (this.dataSource.paginator) {
        this.dataSource.paginator.firstPage();
      }
    }
}
