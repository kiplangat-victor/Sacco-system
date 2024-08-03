import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { MatSnackBar, MatSnackBarHorizontalPosition, MatSnackBarVerticalPosition } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { GuarantorsParamsService } from './guarantors-params.service';

@Component({
  selector: 'app-guarantors-params',
  templateUrl: './guarantors-params.component.html',
  styleUrls: ['./guarantors-params.component.scss']
})
export class GuarantorsParamsComponent implements OnInit {
  auth_user = "SUPERUSER"
  subscription:Subscription
  results:any
  error:any
  horizontalPosition:MatSnackBarHorizontalPosition
  verticalPosition:MatSnackBarVerticalPosition
   message: any;
   function_type: any;
   organization_id: any;
   isEnabled: boolean;
   _snackBar: any;
   id: number;
   ngZone: any;
  fmData: any;
  fetchData: any;
   constructor(
     private guarantorsConfigAPI: GuarantorsParamsService,
     private fb:UntypedFormBuilder,
     private _snackbar:MatSnackBar,
     private router:Router) {
    this.fmData = this.router.getCurrentNavigation().extras.queryParams.formData;
    this.fetchData = this.router.getCurrentNavigation().extras.queryParams.fetchData;
    console.log(this.fetchData);
    this.function_type = this.fmData.function_type;
    this.id = this.fmData.id;
     }
   ngOnInit(){
     this.getPage();
   }
   formData = this.fb.group({
     id: [''],
     checkCustomerActiveness:[''],
     checkCustomerSubsequentGuaranteStatus:[''],
     checkCustomerLoanStatus:[''],
     checkCustomerSharesQualification:[''],
     checkCustomerMaximumActiveGuaranteedNo:[''],
     customerMaximumGuaranteed:[''],
     modifiedBy: ['N'],
     modifiedFlag:['N'],
     modifiedTime: [new Date()],
     postedBy: [this.auth_user],
     postedFlag: ['Y'],
     postedTime:[new Date()],
     verifiedBy:['N'],
     verifiedFlag: ['N'],
     verifiedTime:[new Date()],
     deletedFlag: ['N'],
     deletedBy: ['N'],
     deletedTime: [new Date()],
   })
   get f() { return this.formData.controls; }

   getData() {
    this.results = this.fetchData;
    this.formData = this.fb.group({
     id: [this.results.id],
     checkCustomerActiveness:[this.results.checkCustomerActiveness],
     checkCustomerSubsequentGuaranteStatus:[this.results.checkCustomerSubsequentGuaranteStatus],
     checkCustomerLoanStatus:[this.results.checkCustomerLoanStatus],
     checkCustomerSharesQualification:[this.results.checkCustomerSharesQualification],
     checkCustomerMaximumActiveGuaranteedNo:[this.results.checkCustomerMaximumActiveGuaranteedNo],
     customerMaximumGuaranteed:[this.results.customerMaximumGuaranteed],
     modifiedFlag:[this.results.modifiedFlag],
     modifiedBy: [this.results.modifiedBy],
     modifiedTime: [this.results.modifiedTime],
     postedBy: [this.results.postedBy],
     postedFlag: [this.results.postedFlag],
     postedTime:[this.results.postedTime],
     verifiedBy:[this.results.verifiedBy],
     verifiedFlag: [this.results.verifiedFlag],
     verifiedTime:[this.results.verifiedTime],
     deletedFlag: [this.results.deletedFlag],
     deletedBy: [this.results.deletedBy],
     deletedTime: [this.results.deletedTime],
    });
   }
 getPage(){
   if(this.function_type == "ADD") {
     this.isEnabled = true;
     this.formData = this.fb.group({
      id: [''],
      checkCustomerActiveness:[''],
      checkCustomerSubsequentGuaranteStatus:[''],
      checkCustomerLoanStatus:[''],
      checkCustomerSharesQualification:[''],
      checkCustomerMaximumActiveGuaranteedNo:[''],
      customerMaximumGuaranteed:[''],
      modifiedFlag:['N'],
      modifiedBy: ['N'],
      modifiedTime: [new Date()],
      postedBy: [this.auth_user],
      postedFlag: ['Y'],
      postedTime:[new Date()],
      verifiedBy:['N'],
      verifiedFlag: ['N'],
      verifiedTime:[new Date()],
      deletedFlag: ['N'],
      deletedBy: ['N'],
      deletedTime: [new Date()],
     });
   this.formData.controls.is_verified.disable();
   this.formData.controls.is_deleted.disable();
   }
   else if(this.function_type == "INQUIRE"){
     this.isEnabled = false;
   }
   else if(this.function_type == "VERIFY"){
     this.formData.disable()
     this.isEnabled = true;
   }
 }
   disabledFormControll() {
     throw new Error('Method not implemented.');
   }
  onSubmit() {
     if(this.formData.valid){
       if(this.function_type == "ADD"){
        this.subscription = this.guarantorsConfigAPI.createGuarantorsConfig(this.formData.value).subscribe(
          res =>{
            this.results = res
            this._snackbar.open("Executed Successfully","X",{
              horizontalPosition:this.horizontalPosition,
              verticalPosition:this.verticalPosition,
              duration:3000,
              panelClass:['green-snackbar', 'login-snackbar']
            });
            this.router.navigate([`/system/Configurations/Global/Guarantors-Params/maintenance`], { skipLocationChange: true });
          },
          err =>{
            this.error = err
            this._snackbar.open(this.error,"Try Again",{
              horizontalPosition:this.horizontalPosition,
              verticalPosition:this.verticalPosition,
              duration:3000,
              panelClass:['red-snackbar', 'login-snackbar']
            })
          }
        )
       }else{
        this.subscription = this.guarantorsConfigAPI.updateGuarantorsConfig(this.formData.value).subscribe(
          res =>{
            this.results = res
            this._snackbar.open("Executed Successfully","X",{
              horizontalPosition:this.horizontalPosition,
              verticalPosition:this.verticalPosition,
              duration:3000,
              panelClass:['green-snackbar', 'login-snackbar']
            });
            this.router.navigate([`/system/Configurations/Global/Guarantors-Params/maintenance`], { skipLocationChange: true });
          },
          err =>{
            this.error = err
            this._snackbar.open(this.error,"Try Again",{
              horizontalPosition:this.horizontalPosition,
              verticalPosition:this.verticalPosition,
              duration:3000,
              panelClass:['red-snackbar', 'login-snackbar']
            })
          }
        )
       }
     }else {
       this._snackbar.open("Invalid Form Data Valua", "Try Again",{
         horizontalPosition: this.horizontalPosition,
         verticalPosition: this.verticalPosition,
         duration:3000,
         panelClass:['red-snackbar','login-snackbar']
       })
     }
   }
 }

