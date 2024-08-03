import { SelectionModel } from '@angular/cdk/collections';
import { Component, OnInit, ViewChild } from '@angular/core';
import { UntypedFormBuilder, Validators } from '@angular/forms';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { MatMenuTrigger } from '@angular/material/menu';
import { MatPaginator } from '@angular/material/paginator';
import {
  MatSnackBar,
  MatSnackBarHorizontalPosition,
  MatSnackBarVerticalPosition,
} from '@angular/material/snack-bar';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { CustomerSharesLookupComponent } from '../customer-shares-lookup/customer-shares-lookup.component';
import { ShareCapitalService } from '../share-capital.service';
@Component({
  selector: 'app-share-capital-maintenance',
  templateUrl: './share-capital-maintenance.component.html',
  styleUrls: ['./share-capital-maintenance.component.scss'],
})
export class ShareCapitalMaintenanceComponent implements OnInit {
  function_type: any;
  dialogData: any;
  submitted = false;
  resAccountData: any;
  subscription: Subscription;
  horizontalPosition: MatSnackBarHorizontalPosition;
  verticalPosition: MatSnackBarVerticalPosition;
  constructor(
    private shareService: ShareCapitalService,
    private fb: UntypedFormBuilder,
    private _snackbar: MatSnackBar,
    private dialog: MatDialog,
    private router: Router
  ) {}

  ngOnInit(): void {}

  functionArray = [
    'INQUIRE Share Capital Account Details',
  ];
  displayedColumns: string[] = [
    'id',
    'year',
    'month',
    'transactionCode',
    'account',
    'tranDate',
    'parttranType',
    'tranAmount',
    'serviceBy'
  ];

  dataSource!: MatTableDataSource<any>;
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;
  contextMenu: MatMenuTrigger;
  contextMenuPosition = { x: '0px', y: '0px' };

  selection = new SelectionModel<any>(true, []);
  data: any;
  error: any;
  isLoading: boolean = true;
  loading = false;
  retryPosting: boolean = false;
  postToUraFailed: boolean = false;
  customerDetailsFound: boolean = false;
  transferCustomerShares: boolean = false;
  inquireShareCapitalSelected: boolean = false;

  formData = this.fb.group({
    function_type: ['', Validators.required],
    customerCode: [''],
    customerName: [''],
    cust_account_caa: [''],
  });
  get f() {
    return this.formData.controls;
  }

  onSelectFunction(event: any) {
    if (event.target.value == 'INQUIRE Share Capital Account Details') {
     this.inquireShareCapitalSelected = true
    } else {
      this.inquireShareCapitalSelected = false;
    }
  }


  sharesLookup(): void {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.disableClose = false;
    dialogConfig.autoFocus = true;
    dialogConfig.width = '800px';
    dialogConfig.data = {
      data: '',
    };

    const dialogRef = this.dialog.open(
      CustomerSharesLookupComponent,
      dialogConfig
    );
    dialogRef.afterClosed().subscribe((results) => {
      console.log(results);

      if (results.data.message == 'Not Found') {
        this._snackbar.open('Sorry! ', 'Data not available for this account', {
          horizontalPosition: this.horizontalPosition,
          verticalPosition: this.verticalPosition,
          duration: 3000,
          panelClass: ['red-snackbar', 'login-snackbar'],
        });
      } else  {

        this.dataSource = new MatTableDataSource(results.data.entity);
        this.dataSource.paginator = this.paginator;
        this.dataSource.sort = this.sort;

        this.customerDetailsFound = true;
      }
    });
  }

  onSubmit() {
    this.submitted = true;
    if (this.formData.valid) {
      this.shareService.changeMessage(this.formData.value);
      if (this.function_type == 'ADD') {
        this.router.navigateByUrl('system/share-capital/data/view');
      } else if (this.function_type != 'ADD') {
        this.router.navigateByUrl('system/share-capital/data/view');
      }
    } else {
      this._snackbar.open('Invalid form Data value', 'Try Again', {
        horizontalPosition: this.horizontalPosition,
        verticalPosition: this.verticalPosition,
        duration: 3000,
        panelClass: ['red-snackbar', 'login-snackbar'],
      });
    }
  }
}
