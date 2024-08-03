import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { AccountsService } from 'src/app/administration/Service/AccountsService/accounts/accounts.service';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';
import { SavingsProductLookupComponent } from '../../ProductModule/savings/savings-product-lookup/savings-product-lookup.component';
import { MatTabGroup } from '@angular/material/tabs';
import { MembershipLookUpComponent } from '../../MembershipComponent/Membership/membership-look-up/membership-look-up.component';
import { GeneralAccountsLookupComponent } from '../general-accounts-lookup/general-accounts-lookup.component';

@Component({
  selector: 'app-card-application',
  templateUrl: './card-application.component.html',
  styleUrls: ['./card-application.component.scss']
})
export class CardApplicationComponent implements OnInit {
  function_type: any;
  //atmForm:FormGroup;
  loading: boolean = false;
  public data =[]
  customerCode:any;
  accountNumber:any;
  accountName:any;
  accountType:any;
  atmIncomeAccount:any;
  currency:any;
  accountStatus:any;
  cardNumber:any;
  cardStatus:any;
 
  cardLimit:any;
  cardExpiryDate:any;
  pin:any;
  //branchCode:any;
  manager:any;
  //ATMDetailsTab=true;
  onShowSubmitButton = true;
  onShowModifyButton = false;
  onShowVerifyButton = false;
  onShowDeleteButton = false;
  fmData: any;
  fetchData: any;
  onShowResults = false;
  onShowAddButton = true;
  results: any;
  submitted: boolean;
  error: any;
  isEnabled = true;
  showButtons = true;
  onShowSearchIcon = true;
  accountData: any;
  form: FormGroup<{ cardType: FormControl<string>; }>;
  cardTypes:any=['VISA', 'MASTERCARD','LOCAL CARD'];

  constructor(private router:Router , 
    public dialog: MatDialog ,
    private apiService:AccountsService,
    private notificationAPI: NotificationService,
    private fb:FormBuilder){
      this.fmData = this.router.getCurrentNavigation().extras.queryParams.formData;
    this.fetchData = this.router.getCurrentNavigation().extras.queryParams.fetchedData;
    this.function_type = this.fmData.function_type;
    
  }
  
  
  ngOnInit() {
    this.getPage()
    //this.form = new FormGroup({
    //  cardType: new FormControl('')
   // });
    //this.fetchCardTypes();
   
  
  
    
   
    
   
  }
  fetchCardTypes() {
  this.apiService.getCardTypes().subscribe(data=>{
    this.cardTypes = data;
  }
    )
  }



navigateToLookup() {
  this.router.navigate(['lookup']);
}

formData:FormGroup=this.fb.group({
  
  accountName:[''],
  accountStatus:[''],
  accountType:[''],
  acid:[''],
  solCode:[''],
  cardActiveFlag:[''],
  cardLimit:[''],
  cardNumber:[''],
  cardStatus:[''],
  cardType:[''],
  currency:[''],
  entityId:[''],
  expiryDate:[''],
  manager:[''],
  pin:[''],
  //id:['']


})

getPage() {
  if (this.function_type == "ADD") {
    

    this.formData = this.fb.group({
     
      acid:['',Validators.required],
      accountName:['',Validators.required],
      accountType:['',Validators.required],
      solCode:['',Validators.required],
      currency:['',Validators.required],
      accountStatus:['',Validators.required],
      cardNumber:['',Validators.required],
      cardStatus:['',Validators.required],
      cardType:['',Validators.required],
      cardLimit:['',Validators.required],
      cardExpiryDate:['',Validators.required],
      pin:['',Validators.required],
      //branchCode:['',Validators.required],
     
      manager:['',Validators.required],
      //id:['',Validators.required]
    });
    
  }
  else if (this.function_type == "INQUIRE") {
    this.getFunctions();
    this.getCardData();
    this.formData.disable();
  
  }
  else if (this.function_type == "MODIFY") {
    this.loading = false;
    this.onShowResults = true;
    this.onShowAddButton = true;
    this.onShowModifyButton = true;
    this.onShowSubmitButton = false;
    this.getCardData();
    
    
  }
  else if (this.function_type == "VERIFY") {
    this.getFunctions();
    this.onShowVerifyButton = true;
    this.getCardData();
    this.disableForm()
  } else if (this.function_type == "DELETE") {
    this.getFunctions();
    this.onShowDeleteButton = true;
    this.getCardData();
    
  }
}
getCardData(){
  this.formData = this.fb.group({
        
    acid:[this.fetchData.acid],
    accountName:[this.fetchData.accountName],
    accountType:[this.fetchData.accountType],
    currency:[this.fetchData.currency],
    accountStatus:[this.fetchData.accountStatus],
    cardNumber:[this.fetchData.cardNumber],
    cardStatus:[this.fetchData.cardStatus],
    cardType:[this.fetchData.cardType],
    cardLimit:[this.fetchData.cardLimit],
    cardExpiryDate:[this.fetchData.cardExpiryDate],
    //branchCode:[this.fetchData.branchCode],
    pin:[this.fetchData.pin],
    //id:[res.entity.id],
    manager:[this.fetchData.manager],
    solCode:[this.fetchData.solCode]
    
  });

}



disableForm() {
  this.formData.disable();

}

getFunctions() {
  this.loading = false;
  this.isEnabled = false;
  this.onShowAddButton = false;
  this.showButtons = false;
  this.onShowSubmitButton = false;
  this.onShowResults = true;
  this.formData.disable();
  this.onShowSearchIcon = false;

}

openLookUpDialog():void {
    const dialogRef = this.dialog.open(GeneralAccountsLookupComponent,{
    width:'700px',height:'500px'});
     dialogRef.afterClosed().subscribe(result => {
       this.accountData = result.data
       this.formData.controls.accountName.setValue(result.data.accountName)
       this.formData.controls.acid.setValue(result.data.acid)
       this.formData.controls.accountType.setValue(result.data.accountType)
       this.formData.controls.currency.setValue(result.data.currency)
       this.formData.controls.solCode.setValue(result.data.solCode)
       this.formData.controls.accountStatus.setValue(result.data.accountStatus)
       this.formData.controls.manager.setValue(result.data.accountManager)





       




        
      });
  }
  nextTab(tabGroup:MatTabGroup){
    if(this.isFormValid()){
      tabGroup.selectedIndex=tabGroup.selectedIndex +1;
    }
  }
  previousTab(tabGroup:MatTabGroup){
    tabGroup.selectedIndex=tabGroup.selectedIndex -1;
  }
  isFormValid():boolean{
    return true
  }
  
goBack(){
  this.router.navigate(['/system/card-maintenance'])

}



onSubmit() {
  this.loading = true;
  this.submitted = true;
  if (this.function_type == "ADD") {
    console.log(this.formData)
    if (this.formData.valid) {
      this.apiService.addCard(this.formData.value).subscribe(
        res => {
          if (res.statusCode === 406) {
            this.results = res;
            this.loading = false;
            this.router.navigate([`/system/card-maintenance`], { skipLocationChange: true });
          } else {
            this.results = res;
            this.loading = false;
            this.router.navigate([`/system/card-maintenance`], { skipLocationChange: true });
          }
        }, err => {
          this.loading = false;
          this.error = err;
          
        }
      )
    } else if (this.formData.invalid) {
      this.loading = false;
      
    }

  } else if (this.function_type == "MODIFY") {
    this.apiService.modifyCard(this.fetchData.id).subscribe(
      (res) => {
        if (res.statusCode === 200 || res.statusCode === 201) {
          this.results = res;
          this.loading = false;
          this.notificationAPI.alertWarning(this.results.message);
          this.router.navigate([`/system/card-maintenance`], { skipLocationChange: true });
        } else {
          this.results = res;
          this.loading = false;
          this.notificationAPI.alertWarning(this.results.message);
          this.router.navigate([`/system/card-maintenance`], { skipLocationChange: true });
        }

      }, (err) => {
        this.loading = false;
        this.error = err;
        
      }
    )
  }
  else if (this.function_type == "VERIFY") {
    this.apiService.verifyCard(this.fetchData.id).subscribe(
      res => {
        if (res.statusCode === 200) {
          this.loading = false;
          this.results = res;
          this.notificationAPI.alertWarning(this.results.message);
          this.router.navigate([`admin/AtmMaintenance`], { skipLocationChange: true });
        } else {
          this.loading = false;
          this.results = res;
          this.notificationAPI.alertWarning(this.results.message);
          this.router.navigate([`admin/AtmMachineOnboardingComponent`], { skipLocationChange: true });
        }
      }, err => {
        this.loading = false;
        this.error = err;
        this.notificationAPI.alertWarning(this.error);
      }
    )

  } else if (this.function_type == "DELETE") {
    this.apiService.deleteCard(this.fetchData.id).subscribe(
      res => {
        if (res.statusCode === 200) {
          this.loading = false;
          this.results = res;
          this.notificationAPI.alertWarning(this.results.message);
          this.router.navigate([`/system/card-maintenance`], { skipLocationChange: true });
        } else {
          this.loading = false;
          this.results = res;
          this.notificationAPI.alertWarning(this.results.message);
          this.router.navigate([`/system/card-maintenance`], { skipLocationChange: true });
        }
      }, err => {
        this.loading = false;
        this.error = err;
      }
    )
  }
}
}

















function getcardTypes() {
  throw new Error('Function not implemented.');
}

