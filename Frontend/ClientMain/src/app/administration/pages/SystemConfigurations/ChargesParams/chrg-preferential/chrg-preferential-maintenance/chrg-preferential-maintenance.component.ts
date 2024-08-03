import { HttpClient, HttpParams } from '@angular/common/http';
import { Component,NgZone, OnInit} from '@angular/core';
import { UntypedFormBuilder, Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar, MatSnackBarHorizontalPosition, MatSnackBarVerticalPosition } from '@angular/material/snack-bar';
import { Router, ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs';
import { EventIdService } from 'src/app/administration/pages/SystemConfigurations/ChargesParams/event-id/event-id.service';
import { TokenStorageService } from 'src/@core/AuthService/token-storage.service';
import { EventTypeLookupComponent } from '../../event-type/event-type-lookup/event-type-lookup.component';
import { ChrgPreferentialServiceService } from '../chrg-preferential-service.service';
import { ChrgPreferentialLookupComponent } from '../chrg-preferential-lookup/chrg-preferential-lookup.component';
import { LinkedOrganizationLookupComponent } from '../../../GlobalParams/linked-organization/linked-organization-lookup/linked-organization-lookup.component';
import { EventIdLookupComponent } from '../../event-id/event-id-lookup/event-id-lookup.component';
import { DataStoreService } from 'src/@core/helpers/data-store.service';

@Component({
  selector: 'app-chrg-preferential-maintenance',
  templateUrl: './chrg-preferential-maintenance.component.html',
  styleUrls: ['./chrg-preferential-maintenance.component.scss']
})
export class ChrgPreferentialMaintenanceComponent implements OnInit {
  horizontalPosition: MatSnackBarHorizontalPosition = 'end';
  verticalPosition: MatSnackBarVerticalPosition = 'top';
  function_type: any;
  isRequired = false;
  function_type_data: any;
  chrg_preferential: any;
  entry_identity = "";
  showAccountInput = false;
  showCifInput = false;
  showContractInput = false;
  newData = true;
  account_id = false;
  cif_id = false;
  organization_id = false;
  message!:string;
  subscription!: Subscription;
  params: any;
  error: any;
  organization_name: any;
  event_type_data: any;
  showLokup = true;
  showChargeInput = false;
  lookupdata: any;
  event_id_desc: any;
  event_type_desc: any;

  constructor(
    private chrgPreferentialAPI: ChrgPreferentialServiceService,
    public fb: UntypedFormBuilder,
    private router: Router,
    private ngZone: NgZone,
    private _snackBar: MatSnackBar,
    private dialog: MatDialog,
    private datastoreApi: DataStoreService
    ) {
      this.functionArray = this.datastoreApi.getActionsByPrivilege("CONFIGURATIONS");
      this.functionArray = this.functionArray.filter(
        arr => arr ===  'ADD' ||
        arr ===  'INQUIRE' ||
        arr ===  'MODIFY' ||
        arr ===  'VERIFY' ||
        arr ===  'DELETE');
     }
    chargePreferetialsArrays: any = [
      'Customer Level','Account Level','Charge Level','Contract Level'
    ]
    functionArray: any = [
      'ADD','INQUIRE','MODIFY','VERIFY','DELETE'
    ]
  ngOnInit(): void {
    this.subscribeChargeMessage();
  }

  subscribeChargeMessage(){
    this.subscription = this.chrgPreferentialAPI.currentMessage.subscribe(message => this.message = message)
  }
  ngOnDestroy() {
    this.subscription.unsubscribe();
  }

  loading = false;
  submitted = false;

  event_type = "";
  event_id = "";
  formData = this.fb.group({
    function_type: ['', [Validators.required]],
    chrg_preferential: ['', [Validators.required]],
    event_type: [this.event_type],
    event_id: [''],
    account_id:[''],
    cif_id:[''],
    organization_id:[''],
    organization_data:[''],
    event_type_data:['']
  });
  eventType(): void {
    const dialogRef = this.dialog.open(EventTypeLookupComponent, {

    });
    dialogRef.afterClosed().subscribe(result => {
      this.event_type = result.data.description;
      this.formData.controls.event_type.setValue(result.data.code);
      this.formData.controls.event_type_data.setValue(result.data);
    });
  }
  eventId(): void {
    const dialogRef = this.dialog.open(EventIdLookupComponent, {

    });
    dialogRef.afterClosed().subscribe(result => {
      this.lookupdata = result.data;
      this.event_id = result.data.event_id;
      this.event_id_desc = result.data.event_id_desc;
      this.event_type = result.data.event_type;
      this.event_type_desc = result.data.event_type_desc;
      this.formData.controls.event_id.setValue(result.data);
    });
  }

  organizationIdLookup(): void {
    const dialogRef = this.dialog.open(LinkedOrganizationLookupComponent, {

    });
    dialogRef.afterClosed().subscribe(result => {
      this.organization_id = result.data;
      this.organization_name = result.data.organization_name;
      this.formData.controls.organization_id.setValue(result.data.id);
      this.formData.controls.organization_data.setValue(result.data);
    });
  }

  chrgPref(): void {
    const dialogRef = this.dialog.open(ChrgPreferentialLookupComponent, {

    });
    dialogRef.afterClosed().subscribe(result => {
      this.event_id = result.data;
      this.formData.controls.event_id.setValue(result.data);
    });
  }

  onSelectPreferential(event:any){
    if(event.target.value == "Customer Level"){
      this.showAccountInput = false;
      this.showCifInput = true;
      this.showContractInput = false;
      this.showChargeInput = false;
    }else if(event.target.value == "Account Level"){
      this.showAccountInput = true;
      this.showCifInput = false;
      this.showChargeInput = false;
      this.showContractInput = false;
    }else if(event.target.value == "Charge Level"){
      this.showChargeInput = true;
      this.showAccountInput = false;
      this.showCifInput = false;
      this.showContractInput = false;
      this.formData.controls.event_type.disable()
      // this.formData.controls.event_id.setValidators(Validators.required);
    }else if(event.target.value == "Contract Level"){
      this.formData.controls.organization_id.setValidators([Validators.required])
      this.showLokup = false;
      this.showAccountInput = false;
      this.showChargeInput = false;
      this.showCifInput = false;
      this.showContractInput = true;
      // this.formData.controls.linked_organization.setValidators([Validators.required])
    }
  }
  addEventId(){
    this.ngZone.run(() => this.router.navigateByUrl('system/event_id'));
  }
      // convenience getter for easy access to form fields
      get f() { return this.formData.controls; }
  onSubmit(){
    // console.log(this.formData.value)
    this.loading = true;
    this.submitted = true;
    if(this.formData.valid){
      // this.int_tbl_code = this.f.int_tbl_code.value;
      // this.function_type =  this.f.function_type.value;
      this.function_type = this.f.function_type.value;
      this.function_type_data = this.f.function_type.value;
      // this.chrg_preferential = this.f.chrg_preferential.value;

      this.chrg_preferential = this.f.chrg_preferential.value;
      this.event_type = this.f.event_type.value;
      this.event_id = this.f.event_id.value;
      this.account_id = this.f.account_id.value;
      this.cif_id = this.f.cif_id.value;
      this.organization_id = this.f.organization_id.value;

      this.event_type =  this.f.event_type.value;
      this.event_id =  this.f.event_id.value;
      if(this.chrg_preferential  == "Customer Level"){
        this.formData.controls.organization_id.setValidators([])
        this.formData.controls.organization_id.setValue("")
        this.formData.controls.account_id.setValidators([])
        this.formData.controls.account_id.setValue("")
        this.formData.controls.cif_id.setValidators([Validators.required])
        this.entry_identity = this.f.cif_id.value;
      }else if(this.chrg_preferential  == "Account Level"){
        this.formData.controls.cif_id.setValidators([])
        this.formData.controls.cif_id.setValue("")
        this.formData.controls.organization_id.setValidators([])
        this.formData.controls.organization_id.setValue("")
        this.formData.controls.account_id.setValidators([Validators.required])
        this.entry_identity = this.f.account_id.value;
      }else if(this.chrg_preferential  == "Charge Level"){
        this.formData.controls.account_id.setValidators([])
        this.formData.controls.account_id.setValue("")
        this.formData.controls.cif_id.setValidators([])
        this.formData.controls.cif_id.setValue("")
        this.formData.controls.organization_id.setValidators([])
        this.formData.controls.organization_id.setValue("")
        this.entry_identity = this.f.event_id.value;
      }else if(this.chrg_preferential  == "Contract Level"){
        this.formData.controls.account_id.setValidators([])
        this.formData.controls.account_id.setValue("")
        this.formData.controls.cif_id.setValidators([])
        this.formData.controls.cif_id.setValue("")
        this.formData.controls.organization_id.setValidators([Validators.required])
        this.entry_identity = this.f.organization_id.value;
      }

      this.params = new HttpParams()
      .set('function_type',this.function_type)
      .set('chrg_preferential', this.chrg_preferential)
      .set('event_type', this.event_type)
      .set('event_id', this.event_id)
      .set('account_id', this.account_id)
      .set('cif_id', this.cif_id)
      .set('organization_id', this.organization_id);

      if(this.function_type == "ADD"){
        console.log("called add!")
        // check if code exists
        this.subscription = this.chrgPreferentialAPI.checkIfChrgPreferentialExists(this.params).subscribe(res=>{
          // not available else proceed
          this.chrgPreferentialAPI.changeMessage(this.formData.value)
          this.ngZone.run(() => this.router.navigateByUrl('system/configurations/charge/preferentials/data/view'));
        }, err=>{
          // exist else show error
          this.error = err;
            this.loading = false;
            this._snackBar.open(this.error, "X", {
              horizontalPosition: this.horizontalPosition,
              verticalPosition: this.verticalPosition,
              duration: 3000,
              panelClass: ['red-snackbar','login-snackbar'],
            });

        })
      }else if(this.function_type != "ADD"){
        // check if code exists
        this.subscription = this.chrgPreferentialAPI.checkIfChrgPreferentialExists(this.params).subscribe(res=>{
          // not available else proceed
          this.chrgPreferentialAPI.changeMessage(this.formData.value)
          this.ngZone.run(() => this.router.navigateByUrl('system/configurations/charge/preferentials/data/view'));
        }, err=>{
          // exist else show error
          this.error = err;
            this.loading = false;
            this._snackBar.open(this.error, "X", {
              horizontalPosition: this.horizontalPosition,
              verticalPosition: this.verticalPosition,
              duration: 3000,
              panelClass: ['red-snackbar','login-snackbar'],
            });

        })
      }

      // checkHitcm

      // check if adding
  }else{
    this.loading = false;
    this._snackBar.open("Invalid Form Data", "X", {
      horizontalPosition: this.horizontalPosition,
      verticalPosition: this.verticalPosition,
      duration: 3000,
      panelClass: ['red-snackbar','login-snackbar'],
    });

  }


  }
}
