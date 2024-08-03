import { HttpParams } from '@angular/common/http';
import { Component, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup, UntypedFormBuilder } from '@angular/forms';
import { MatDialog, MatDialogConfig, MatDialogRef } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSnackBarHorizontalPosition, MatSnackBarVerticalPosition } from '@angular/material/snack-bar';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { Subscription } from 'rxjs';
import { ProductService } from 'src/app/administration/Service/product-maintainance/product.service';
import { ReportsService } from 'src/app/administration/Service/reports/reports.service';
import { SolCodeLookupComponent } from './sol-code-lookup/sol-code-lookup.component';

@Component({
  selector: 'app-accounts-lookup',
  templateUrl: './accounts-lookup.component.html',
  styleUrls: ['./accounts-lookup.component.scss']
})
export class AccountsLookupComponent implements OnInit {
  results:any;
  formData : any;
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;
  horizontalPosition: MatSnackBarHorizontalPosition = 'end';
  verticalPosition: MatSnackBarVerticalPosition = 'top';
  title = 'export-table-data-to-any-format';
  displayedColumns: string[] = [ 'index','acid','accountStatus', 'accountType', 'customerCode'];
  dataSource!: MatTableDataSource<any>;
  subscription!: Subscription;
  data: any;
  error: any;
  employeeEmail: any;
  employee_id: any;
  creatingAccount = false;
  parametersForm: FormGroup;

  schemeTypes: any = [
    { code: 'LAA', description: 'Loan Accounts' },
    { code: 'ODA', description: 'Overdraft Accounts' },
    { code: 'SBA', description: 'Savings Accounts' },
    { code: 'TDA', description: 'Term Deposits' },
    { code: 'CAA', description: 'Current Accounts' },
  ];


  constructor(
    private dialogRef: MatDialogRef<AccountsLookupComponent>,
     public formBuilder:UntypedFormBuilder,
     private productService: ProductService,
     private dialog: MatDialog,
     private fb: FormBuilder,
     private reportService: ReportsService) {

     }

   ngOnInit(): void {
    this.parametersForm = this.initParametersForm();
   }

   initParametersForm(): FormGroup{
    return this.fb.group({
      accountType: [''],
      solCode: ['']
     })
   }

  solCodeLookup(){
    const dialogConfig = new MatDialogConfig();
    dialogConfig.disableClose = false;
    dialogConfig.autoFocus = true;
    dialogConfig.width = '400px';
    dialogConfig.data = {
      data: '',
    };
    const dialogRef = this.dialog.open(SolCodeLookupComponent, dialogConfig);
    dialogRef.afterClosed().subscribe((result) => {

      this.parametersForm.patchValue({ solCode: result.data.branchCode })
    });
  }

   findAccount() {
    console.log(this.parametersForm.value.accountType);
    const params = new HttpParams()
    .set("accountType", this.parametersForm.value.accountType)
    .set("solCode", this.parametersForm.value.solCode)


    this.subscription = this.reportService.findAccountsLookups(params).subscribe(res => {
     this.data = res;
     console.log(this.data);

     console.log(this.data.entity.length)

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
