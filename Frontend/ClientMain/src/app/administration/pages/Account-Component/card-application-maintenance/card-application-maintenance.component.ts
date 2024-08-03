import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { Subject } from 'rxjs';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';
import { DataStoreService } from 'src/@core/helpers/data-store.service';
import { CardnumberLookUpComponent } from '../cardnumber-look-up/cardnumber-look-up.component';
import { takeUntil } from 'rxjs/operators';

@Component({
  selector: 'app-card-application-maintenance',
  templateUrl: './card-application-maintenance.component.html',
  styleUrls: ['./card-application-maintenance.component.scss']
})
export class CardApplicationMaintenanceComponent implements OnInit {

  constructor(private fb:FormBuilder, private router:Router, private dialog: MatDialog,private dataStoreAPI:
    DataStoreService, private notificationAPI: NotificationService) {
      this.functionArray = this.dataStoreAPI.getActionsByPrivilege("ACCOUNTS MANAGEMENT");
      this.functionArray=this.functionArray.filter(
        (arr: string) =>
        arr === 'ADD' ||
        arr === 'VERIFY' ||
        arr === 'INQUIRE' ||
        arr === 'MODIFY' ||
        arr === 'DELETE' );
        //arr === 'ACKNOWLEDGE' ||
        //arr === 'ACTIVATE' ||
       // arr === 'BLOCK' ||
      
      
     }

    

  loading = false;
  submitted = false;
  account_code: any;
  functionArray: any;
  function_type: any;
  account_type: any;
  lookupData: any;
  acid: any;
  id: any;
  showFunctionType = false;
  showCustomerType = false;
  showAccountCode = false;
  destroy$: Subject<boolean> = new Subject<boolean>();

  ngOnInit(): void {
  }
// formData : FormGroup;
formData = this.fb.group({
  id: [''],
  function_type: ['', Validators.required],
  account_code: ['', Validators.required],
  account_type: ['']
});


onChange(event: any) {
  this.function_type = event.target.value;
  if (event.target.value == "ADD") {
    this.showAccountCode = false;
    this.showCustomerType = true;
    this.formData.controls.id.setValue("0000000000");
    this.formData.controls.account_type.setValue("SBA");
  }
  else if (event.target.value !== "ADD") {
    this.showAccountCode = true;
    this.showCustomerType = true;
    this.formData.controls.id.setValue("");
  }
}
get f() {
  return this.formData.controls;
}

atmCardLookUp(): void {
  const dialogRef = this.dialog.open(CardnumberLookUpComponent, {
    width: '50%',
    autoFocus: false,
    maxHeight: '90vh'
  });
  dialogRef.afterClosed().pipe(takeUntil(this.destroy$)).subscribe(result => {
    this.lookupData = result.data;
    this.id = this.lookupData.cardNumber;
    this.formData.controls.account_code.setValue(this.id);
    console.log(this.id)
  });
}

onSubmit(){
  this.loading = true;
  if (this.formData.valid || this.function_type =="ADD") {
    this.loading = false;
    this.router.navigate(['system/application'],  { skipLocationChange: true, queryParams: { formData: this.formData.value, fetchedData: this.lookupData } });
  }

else if (!this.formData.valid) {
  if (this.formData.controls.function_type.value == "") {
    this.loading = false;
    this.submitted = true;
    this.notificationAPI.alertWarning("CHOOSE ATM FUNCTION")

}
else if (this.formData.controls.id.value == "") {
  this.loading = false;
  this.submitted = true;
  this.notificationAPI.alertWarning("ATM APPLICATION DETAIL ID IS EMPTY")
} else {
  this.loading = false;
          this.submitted = true;
          this.notificationAPI.alertWarning("ATM APPLICATION DETAILS NOT ALLOWED");
        }


}
}
}
