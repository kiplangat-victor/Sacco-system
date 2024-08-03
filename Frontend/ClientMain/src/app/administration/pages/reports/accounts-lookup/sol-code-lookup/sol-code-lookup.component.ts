import { Component, OnInit, ViewChild } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSnackBarHorizontalPosition, MatSnackBarVerticalPosition } from '@angular/material/snack-bar';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { Subscription } from 'rxjs';
import { ProductService } from 'src/app/administration/Service/product-maintainance/product.service';
import { BranchesService } from '../../../SystemConfigurations/GlobalParams/branches/branches.service';

@Component({
  selector: 'app-sol-code-lookup',
  templateUrl: './sol-code-lookup.component.html',
  styleUrls: ['./sol-code-lookup.component.scss']
})
export class SolCodeLookupComponent implements OnInit {
  results:any;
  formData : any;
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;
  horizontalPosition: MatSnackBarHorizontalPosition = 'end';
  verticalPosition: MatSnackBarVerticalPosition = 'top';
  title = 'export-table-data-to-any-format';
  displayedColumns: string[] = [ 'index','branchCode','branchDescription'];
  dataSource!: MatTableDataSource<any>;
  subscription!: Subscription;
  data: any;
  error: any;
  employeeEmail: any;
  employee_id: any;
  creatingAccount = false;

   constructor(
     private dialogRef: MatDialogRef<SolCodeLookupComponent>,
     public formBuilder:UntypedFormBuilder,
     private productService: ProductService,
     private branchesService: BranchesService) {

     }

   ngOnInit(): void {
     this.getData();
   }

   getData() {
    this.subscription = this.branchesService.find().subscribe(res => {
     this.data = res;
     console.log(this.data);

      // Binding with the datasource
      this.dataSource = new MatTableDataSource(this.data.entity);
      this.dataSource.paginator = this.paginator;
      this.dataSource.sort = this.sort;
    })
  }


   applyFilter(event:Event){
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
