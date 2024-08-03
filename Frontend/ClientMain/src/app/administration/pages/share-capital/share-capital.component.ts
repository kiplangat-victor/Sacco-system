import { Component, OnInit, ViewChild } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSnackBar, MatSnackBarHorizontalPosition, MatSnackBarVerticalPosition } from '@angular/material/snack-bar';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { Subscription } from 'rxjs';
import { ShareCapitalParamsService } from '../SystemConfigurations/GlobalParams/share-capital-params/share-capital-params.service';
import { ShareCapitalService } from './share-capital.service';

@Component({
  selector: 'app-share-capital',
  templateUrl: './share-capital.component.html',
  styleUrls: ['./share-capital.component.scss']
})
export class ShareCapitalComponent implements OnInit {
  displayedColumns: string[] = [ 'index',
  'cust_code',
  'buySharesFrom',
  // 'custBuyerAc',
  'custSellerAc',
  'cust_name',
  // 'officeAc',
  'sharesAmount',
  'sharesQuantity',
  'postedTime',
]




  dataSource!: MatTableDataSource<any>;
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;
  subscription!: Subscription;
  data: any;
  error: any;
  employeeEmail: any;
  employee_id: any;
  creatingAccount = false;

 dialogData:any
 loading = false
 function_type:any
 cust_code:any
 cust_name:any
 message:any
 results:any
 horizontalPosition:MatSnackBarHorizontalPosition
 verticalPosition:MatSnackBarVerticalPosition
  requireMemberAc: boolean;
  requireSaccoAc: boolean;
  account_type: string;
  dtype: string;
  cust_account_caa: any;
  currentAcctiveShare: any;
  constructor(private fb:UntypedFormBuilder,
    private dialog:MatDialog,
    private shareService:ShareCapitalService,
    private ParamsService:ShareCapitalParamsService,
    private _snackbar:MatSnackBar) { }


  ngOnInit(): void {
    this.getCurrentShareValue()
    this.getPage()
  }

  getCurrentShareValue(){
    this.subscription = this.ParamsService.getLastEntry().subscribe(res=>{
      this.currentAcctiveShare = res;
    })
  }

   authuser  = "P"
 formData = this.fb.group({
  id: 0,
  cust_code:[''],
  deleteFlag:['N'],
  deletedBy:['N'],
  deletedTime:[new Date()],
  modifiedBy:['N'],
  modifiedTime:[new Date()],
  payment_means:[''],
  postedBy:[this.authuser],
  postedFlag:['Y'],
  postedTime:[new Date()],
  buySharesFrom:[''],
  officeAc:[''],
  custSellerAc:[''],
  sharesQuantity:[''],
  sharesAmount:[''],
  verifiedBy:['N'],
  verifiedFlag:['N'],
  verifiedTime:[new Date]
 })

//  customerLookup():void{
//   const dialogRef = this.dialog.open(RetailCustomerLookupComponent,{
//   });
//   dialogRef.afterClosed().subscribe(results =>{
//     this.dialogData = results.data;
//     this.formData.controls.cust_code.setValue(this.dialogData.customerCode)
//     this.formData.controls.cust_name.setValue(this.dialogData.middleName)
//   })
//  }
  disabledFormData() {
    return this.formData.disable
  }
  onBuyFrmSacco(event:any){
    this.requireMemberAc = false
    this.requireSaccoAc = true
  }
  onBuyFrmMember(event:any){
    this.requireSaccoAc = false
    this.requireMemberAc = true
  }
  onSharesKeypressEvent(event){
    let shares_qt = event.target.value;
    let min_share_qt = 2
    let current_share_value = (this.currentAcctiveShare.share_capital_amount_per_unit/this.currentAcctiveShare.share_capital_unit)
    if(shares_qt<min_share_qt){
      this._snackbar.open("Current Minimum Shares is "+min_share_qt, "Try Again",{
        horizontalPosition: 'end',
        verticalPosition: 'top',
        duration:3000,
        panelClass:['red-snackbar', 'login-snackbar']
      })
    }else{
      let share_cal_amount = shares_qt * 2000
      this.formData.controls.sharesAmount.setValue(share_cal_amount);
    }
  }

  officeAccountLookup(): void {
    // this.dtype="oa"
    // const dconfig= new MatDialogConfig()
    // dconfig.data={
    //   type:this.dtype
    // }
    // const cdialogRef = this.dialog.open(LoanAccountLookupComponent,dconfig);
    // cdialogRef.afterClosed().subscribe((result) => {
    //   this.formData.controls.officeAc.setValue(result.data.acid);
    // });
  }
  customerAccountLookup(): void {
    // this.dtype="ca"
    // const dconfig= new MatDialogConfig()
    // dconfig.data={
    //   type:this.dtype
    // }
    // const cdialogRef = this.dialog.open(LoanAccountLookupComponent,dconfig);
    // cdialogRef.afterClosed().subscribe((result) => {
    //   console.log(result.data);
    //   this.formData.controls.custSellerAc.setValue(result.data.acid);
    // });
  }



 getPage(){
   this.subscription = this.shareService.currentMessage.subscribe(
     message =>{
       this.message = message
       this.function_type = this.message.function_type
       this.cust_code = this.message.cust_code
       this.cust_name = this.message.cust_name
       this.cust_account_caa = this.message.cust_account_caa
      //  Get Table Data
        this.getData(this.cust_code);

      //  call to get account

       if(this.function_type == "ADD"){
        this.formData = this.fb.group({
        })
        this.formData = this.fb.group({
          id: 0,
          cust_code:[this.cust_code],
          cust_name:[this.cust_name],
          custBuyerAc:[this.cust_account_caa],
          deleteFlag:['N'],
          deletedBy:['N'],
          deletedTime:[new Date()],
          modifiedBy:['N'],
          modifiedTime:[new Date()],
          payment_means:[''],
          postedBy:[this.authuser],
          postedFlag:['Y'],
          postedTime:[new Date()],
          buySharesFrom:[''],
          officeAc:[''],
          custSellerAc:[''],
          sharesQuantity:[''],
          sharesAmount:[''],
          verifiedBy:['N'],
          verifiedFlag:['N'],
          verifiedTime:[new Date]
        })
       }else if(this.function_type == 'INQUIRE'){
         this.disabledFormData()
      this.subscription = this.shareService.getShareCapitalByCode(this.cust_code).subscribe(
        res =>{
            this.results = res
            this.formData = this.fb.group({
              id:[this.results.id],
              custBuyerAc:[this.cust_account_caa],
              payment_means:[this.results.payment_means],
              buySharesFrom:[this.results.buySharesFrom],
              officeAc:[this.results.officeAc],
              custSellerAc:[this.results.custSellerAc],
              sharesQuantity:[this.results.sharesQuantity],
              sharesAmount:[this.results.sharesAmount],
              modifiedBy: [this.results.modifiedBy],
              modifiedTime: [this.results.modifiedTime],
              postedBy: [this.results.postedBy],
              postedFlag: [this.results.postedFlag],
              postedTime: [this.results.postedTime],
              verifiedBy: [this.results.verifiedBy],
              verifiedFlag: [this.results.verifiedFlag],
              verifiedTime: [this.results.verifiedTime],
              deleteFlag: [this.results.deleteFlag],
              deletedBy: [this.results.deletedBy],
              deletedTime: [this.results.deletedTime],
            })
        }
      )

       }else if(this.function_type == 'MODIFY'){
         this.subscription = this.shareService.getShareCapitalByCode(this.cust_code).subscribe(
           res=>{
            this.results = res
            this.formData = this.fb.group({
              id:[this.results.id],
              custBuyerAc:[this.cust_account_caa],
              payment_means:[this.results.payment_means],
              buySharesFrom:[this.results.buySharesFrom],
              officeAc:[this.results.officeAc],
              custSellerAc:[this.results.custSellerAc],
              sharesQuantity:[this.results.sharesQuantity],
              sharesAmount:[this.results.sharesAmount],
              modifiedBy: [this.results.modifiedBy],
              modifiedTime: [this.results.modifiedTime],
              postedBy: [this.results.postedBy],
              postedFlag: [this.results.postedFlag],
              postedTime: [this.results.postedTime],
              verifiedBy: [this.results.verifiedBy],
              verifiedFlag: [this.results.verifiedFlag],
              verifiedTime: [this.results.verifiedTime],
              deleteFlag: [this.results.deleteFlag],
              deletedBy: [this.results.deletedBy],
              deletedTime: [this.results.deletedTime],
            })
           }
         )
       }else if(this.function_type == 'DELETE'){
        this.subscription = this.shareService.getShareCapitalByCode(this.cust_code).subscribe(
          res=>{
           this.results = res
           this.formData = this.fb.group({
            id:[this.results.id],
            custBuyerAc:[this.cust_account_caa],
            payment_means:[this.results.payment_means],
            buySharesFrom:[this.results.buySharesFrom],
            officeAc:[this.results.officeAc],
            custSellerAc:[this.results.custSellerAc],
            sharesQuantity:[this.results.sharesQuantity],
            sharesAmount:[this.results.sharesAmount],
            modifiedBy: [this.results.modifiedBy],
            modifiedTime: [this.results.modifiedTime],
            postedBy: [this.results.postedBy],
            postedFlag: [this.results.postedFlag],
            postedTime: [this.results.postedTime],
            verifiedBy: [this.results.verifiedBy],
            verifiedFlag: [this.results.verifiedFlag],
            verifiedTime: [this.results.verifiedTime],
            deleteFlag: [this.results.deleteFlag],
            deletedBy: [this.results.deletedBy],
            deletedTime: [this.results.deletedTime],
           })
          } ) }

     }) }
 onSubmit(){
   console.log("This is form data", this.formData.value);
  if(this.formData.valid){
    console.log(this.formData.value);

    if(this.function_type == "ADD"){
      this.subscription = this.shareService.createShareCapital(this.formData.value).subscribe(
        res =>{ this.results = res
          this._snackbar.open("Executed Successfully", "X",{
              horizontalPosition:this.horizontalPosition,
              verticalPosition:this.verticalPosition,
              duration:3000,
              panelClass:['green-snackbar', 'login-snackbar']
          })},
          err=>{
              this.error = err
              this._snackbar.open("Invalid Form Data Value", "Try Again",{
                horizontalPosition:this.horizontalPosition,
                verticalPosition:this.verticalPosition,
                duration:3000,
                panelClass:['red-snackbar', 'login-snackbar']
              })
          }
      )
    }else if(this.function_type != "ADD"){
      this.subscription = this.shareService.updateShareCapital(this.formData.value).subscribe(
        res =>{
          this.results
          this._snackbar.open("Updated Successfully", "X",{
            horizontalPosition:this.horizontalPosition,
            verticalPosition:this.verticalPosition,
            duration:3000,
            panelClass:['red-snackbar', 'login-snackbar']
          })
        }
      )
    }
  }
 }




//  Get Data for Table
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
getData(data:any) {
  this.subscription = this.shareService.getShareCapitalByCustomerCode(data).subscribe(res => {
   this.data = res;
   console.log("These are data from the res", res);

    // Binding with the datasource
    this.dataSource = new MatTableDataSource(this.data);
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
  })
}
onSelect(data:any){
  // this.dialogRef.close({ event: 'close', data:data });
}
}
