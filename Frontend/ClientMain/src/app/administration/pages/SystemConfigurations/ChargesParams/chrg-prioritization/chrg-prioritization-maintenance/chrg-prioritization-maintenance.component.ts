import { HttpClient, HttpParams } from '@angular/common/http';
import { Component, Inject, NgZone, OnInit, Optional } from '@angular/core';
import { UntypedFormBuilder, Validators } from '@angular/forms';
import { MatDialog, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatSnackBar, MatSnackBarHorizontalPosition, MatSnackBarVerticalPosition } from '@angular/material/snack-bar';
import { Router, ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs';
import { EventIdService } from 'src/app/administration/pages/SystemConfigurations/ChargesParams/event-id/event-id.service';
import { TokenStorageService } from 'src/@core/AuthService/token-storage.service';
import { ChrgPrioritizationService } from '../chrg-prioritization.service';


@Component({
  selector: 'app-chrg-prioritization-maintenance',
  templateUrl: './chrg-prioritization-maintenance.component.html',
  styleUrls: ['./chrg-prioritization-maintenance.component.scss']
})
export class ChrgPrioritizationMaintenanceComponent implements OnInit {
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
  cif: any;

  constructor(
    private chrgPrioritizationServiceAPI: ChrgPrioritizationService,
    public fb: UntypedFormBuilder,
    private router: Router,
    private ngZone: NgZone,
    private _snackBar: MatSnackBar,
    private http: HttpClient,
    private actRoute: ActivatedRoute,
    private dialog: MatDialog,
    private tokenStorage: TokenStorageService,
    private eventIdAPI:EventIdService,
    // public dialogRef: MatDialogRef<EventIdMaintenanceComponent>,
    // @Optional() @Inject(MAT_DIALOG_DATA) public data: any

    ) { }
    chargePreferetialsArrays: any = [
      'Customer Level','Account Level','Charge Level','Contract Level'
    ]
    functionArray: any = [
      'ADD','INQUIRE','MODIFY','VERIFY','X-Cancel'
    ]
  ngOnInit(): void {
    this.subscribeChargeMessage();
  }

  subscribeChargeMessage(){
    this.subscription = this.chrgPrioritizationServiceAPI.currentMessage.subscribe(message => this.message = message)
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
    cif: ['', [Validators.required]],
  });

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
      // this.chrg_preferential = this.f.chrg_preferential.value;
      this.cif = this.f.cif.value;


      this.params = new HttpParams()
      .set('function_type',this.function_type)
      .set('cif', this.cif);

      if(this.function_type == "ADD"){
        console.log("called add!")
        // check if code exists
        this.subscription = this.chrgPrioritizationServiceAPI.checkIfChrgPrioritizationExists(this.params).subscribe(res=>{
          // not available else proceed
          this.chrgPrioritizationServiceAPI.changeMessage(this.formData.value)
          this.ngZone.run(() => this.router.navigateByUrl('system/configurations/charge/prioritization/data/view'));
        }, err=>{
          this.ngZone.run(() => this.router.navigateByUrl('system/configurations/charge/prioritization/data/view'));

          // exist else show error
          // this.error = err;
          //   this.loading = false;
          //   this._snackBar.open(this.error, "X", {
          //     horizontalPosition: this.horizontalPosition,
          //     verticalPosition: this.verticalPosition,
          //     duration: 3000,
          //     panelClass: ['red-snackbar','login-snackbar'],
          //   });
        })
      }else if(this.function_type == "INQUIRE"){
        // check if code exists
        this.subscription = this.chrgPrioritizationServiceAPI.checkIfChrgPrioritizationExists(this.params).subscribe(res=>{
          // not available else proceed
          this.chrgPrioritizationServiceAPI.changeMessage(this.formData.value)
          this.ngZone.run(() => this.router.navigateByUrl('system/configurations/charge/prioritization/data/view'));
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
      }else if(this.function_type == "MODIFY"){
        // check if code exists
        this.subscription = this.chrgPrioritizationServiceAPI.checkIfChrgPrioritizationExists(this.params).subscribe(res=>{
          // not available else proceed
          this.chrgPrioritizationServiceAPI.changeMessage(this.formData.value)
          this.ngZone.run(() => this.router.navigateByUrl('system/configurations/charge/prioritization/data/view'));
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
      else{
      }

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
