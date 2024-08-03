import { DatePipe } from '@angular/common';
import { HttpParams } from '@angular/common/http';
import { Component, ElementRef, Inject, OnInit, ViewChild } from '@angular/core';
import { FormGroup, UntypedFormBuilder } from '@angular/forms';
import { MatDialog, MatDialogConfig, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatSnackBarHorizontalPosition, MatSnackBarVerticalPosition } from '@angular/material/snack-bar';
import { Subscription } from 'rxjs';
import { AuthService } from 'src/@core/AuthService/auth.service';
import { AccountsLookupComponent } from '../../reports/accounts-lookup/accounts-lookup.component';
import { ViewReportComponent } from '../../reports/view-report/view-report.component';
import { TransactionExecutionService } from '../../transaction-execution/transaction-execution.service';


@Component({
  selector: 'app-customer-shares-lookup',
  templateUrl: './customer-shares-lookup.component.html',
  styleUrls: ['./customer-shares-lookup.component.scss']
})
export class CustomerSharesLookupComponent implements OnInit {
  formData: FormGroup;
  action: string;
  dialogTitle: string;
  schemeTypes: any = [
    { code: 'LAA', description: 'Loan Accounts' },
    { code: 'ODA', description: 'Overdraft Accounts' },
    { code: 'SBA', description: 'Savings Accounts' },
    { code: 'TDA', description: 'Term Deposits' },
    { code: 'CAA', description: 'Current Accounts' },
  ];
  types: any = ["Retail Accounts", "Cooperate Accounts"];
  scope: any = ["All Accounts", "Branch Accounts"];
  users: any [] = [];

  subscription!: Subscription;
  error: any;
  loading: boolean;
  accountTypesReports: boolean = true;
  accountStatetement: boolean = false;
  horizontalPosition: MatSnackBarHorizontalPosition = 'end';
  verticalPosition: MatSnackBarVerticalPosition = 'top';
  years: number[] = [];

  constructor(
    private fb: UntypedFormBuilder,
    public dialogRef: MatDialogRef<ViewReportComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private authService: AuthService,
    private dialog:MatDialog,
    private transactionService: TransactionExecutionService
  ) {}

  ngOnInit(): void {
    this.getYears(2020);
  
    this.formData = this.initFormData();

    this.formData.patchValue({
      reportType: this.data.action
    })
  } 
  
  getYears(year){
    for(let i = year; i < 2099; i++){
      this.years.push(i)
    }
  }

  accountLookup() {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.disableClose = false;
    dialogConfig.autoFocus = true;
    dialogConfig.width = '600px';
    dialogConfig.data = {
      data: '',
    };
    const dialogRef = this.dialog.open(AccountsLookupComponent, dialogConfig);
    dialogRef.afterClosed().subscribe((result) => {
      console.log(result);

      console.log(result.data.acid);
      this.formData.patchValue({ acid: result.data.acid });
    });
  }

  

  getData() {
    this.subscription =  this.authService.allUsers().subscribe(res => {
      this.users = res;

      console.log("Users : ", this.users)
     }, err => {
      console.log(err);
     })
   }


  initFormData(): FormGroup {
    return this.fb.group({
      acid: [],
      year: [],
    });
  }



  submit() {
    const params = new HttpParams()
    .set('acid', this.formData.value.acid)
    .set('year', this.formData.value.year)
  
    this.transactionService.fetchtransaction(params).subscribe(res => {
      console.log(res);

      this.dialogRef.close({ event: 'close', data:res });
    }, err => {
      console.log(err);

      this.dialogRef.close();
    })
  }


  cancel(){
    this.dialogRef.close();
  }
  
}
