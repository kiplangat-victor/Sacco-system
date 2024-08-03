import { HttpClient, HttpParams } from '@angular/common/http';
import { Component, Inject, NgZone, OnInit, Optional } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { MatDialog, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatSnackBar, MatSnackBarHorizontalPosition, MatSnackBarVerticalPosition } from '@angular/material/snack-bar';
import { Router, ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs';
import { TokenStorageService } from 'src/@core/AuthService/token-storage.service';
import { CurrencyLookupComponent } from '../../GlobalParams/currency-config/currency-lookup/currency-lookup.component';
import { ChrgPreferentialServiceService } from './chrg-preferential-service.service';
import { DatePipe } from '@angular/common';

@Component({
  selector: 'app-chrg-preferential',
  templateUrl: './chrg-preferential.component.html',
  styleUrls: ['./chrg-preferential.component.scss']
})
export class ChrgPreferentialComponent implements OnInit {
  horizontalPosition: MatSnackBarHorizontalPosition = 'end';
  verticalPosition: MatSnackBarVerticalPosition = 'top';
  loading = false;
  isDisabled = false;
  isEnabled = true;
  flagArray: any = [

    'Y','N'
  ]
  amt_derivation_Array: any = [
    {code:'CHRG', description:'Free Code'},
    {code:'FIXED', description:'Fixed Amt'},
    {code:'MRT', description:'Formula Based'},
    {code:'PCNT', description:'Percentage'},
    {code:'SCRPT', description:'Script Based'},
    {code:'USTM', description:'Unit Charge Code'},
  ]
  chargePreferetialsArrays: any = [
    'Custmer Level','Account Level','Charge Level','Contract Level'
  ]

  dialogValue: any;
  dialogData: any;
  function_type: any;
  event_type: any;
  event_id: any;
  subscription!:Subscription;
  error: any;
  results: any;
  showContractInput = false;
  showDerivationInput = false;
  showAmtDerivationInput= false;
  showPercentageDerivationInput= false;
  showFilenameDerivationInput= false;
  showChargecodeDerivationInput= false;
  showMrtFilenameDerivationInput= false;
  chrg_calc_crncy: any;
  chrg_coll_crncy: any;
  showExerciseDutyPercentageInput = false;
  params: any;
  eventId: any;
  account_id: any;
  cif_id:any;
  organization_id: any;
  chrg_preferential: any;

  message: any;
  // subscription!: Subscription;
  showPercentageField = false;
  showFixedAmtField = false;
  selectedStartdate: any;
  selectedEnddate: any;
  fomartedStartDate: any;
  fomartedEndDate: any;
  requestParam!: UntypedFormGroup;
  id: any;
  organization_name: any;
  event_type_description: any;


  constructor(
    private chrgPreferentialAPI: ChrgPreferentialServiceService,
    public fb: UntypedFormBuilder,
    private router: Router,
    private ngZone: NgZone,
    private _snackBar: MatSnackBar,
    private http: HttpClient,
    private actRoute: ActivatedRoute,
    private dialog: MatDialog,
    private tokenStorage: TokenStorageService,
    public datepipe: DatePipe
    ) { }
    amtDerivationArrays: any = [
      'Percentage','Fixed'
    ]
    onSelectAmountDerv(event:any){
      if(event.target.value == "Percentage"){
        this.showPercentageField = true;
        this.formData.controls.percentage_val.setValidators([Validators.required])
        this.showFixedAmtField = false;
        this.formData.controls.fixed_amt.setValue("")
        this.formData.controls.fixed_amt.setValidators([])
      }else if(event.target.value == "Fixed"){
        this.showPercentageField = false;
        this.formData.controls.percentage_val.setValue("")
        this.formData.controls.percentage_val.setValidators([])
        this.showFixedAmtField = true;
        this.formData.controls.ixed_amt.setValidators([Validators.required])     }
    }
    onYesExerciseDuty(event:any){
      this.showExerciseDutyPercentageInput = true;
      this.formData.controls.exercise_duty_percentage.setValidators([Validators.required])
    }
    onNoExerciseDuty(event:any){
      this.showExerciseDutyPercentageInput = false;
      this.formData.controls.exercise_duty_percentage.setValue("0")
    }
    onSelectAmtDerivationType(event:any){
      console.log("this is selected", event.target.value)
      if(event.target.value != "FIXED"){
        this.showAmtDerivationInput = false;
        this.formData.controls.amt.setValue(0)
        this.formData.controls.amt.setValidators([])
      }else if(event.target.value == "FIXED"){
        this.showAmtDerivationInput = true;
        this.formData.controls.amt.setValidators([Validators.required])
      }
      if(event.target.value != "PCNT"){
        this.showPercentageDerivationInput = false;
        this.formData.controls.percentage.setValue(0)
        this.formData.controls.percentage.setValidators([])
      }else if(event.target.value == "PCNT"){
        this.showPercentageDerivationInput = true;
        this.formData.controls.percentage.setValidators([Validators.required])
      }
      if(event.target.value != "CHRG"){
        this.showChargecodeDerivationInput = false;
        this.formData.controls.chrg_code.setValue(0)
        this.formData.controls.chrg_code.setValidators([])
      }else if(event.target.value == "CHRG"){
        this.showChargecodeDerivationInput = true;
        this.formData.controls.chrg_code.setValidators([Validators.required])
      }
      if(event.target.value != "MRT"){
        this.showMrtFilenameDerivationInput = false;
        this.formData.controls.file_name.setValue("NULL")
        this.formData.controls.file_name.setValidators([])
      }else if(event.target.value == "MRT"){
        this.showMrtFilenameDerivationInput = true;
        this.formData.controls.file_name.setValidators([Validators.required])
      }
      if(event.target.value != "SCRPT" ){
        this.showFilenameDerivationInput = false;
        this.formData.controls.file_name.setValue("NULL")
        this.formData.controls.file_name.setValidators([])
      }else if(event.target.value == "SCRPT"){
        this.showFilenameDerivationInput = true;
        this.formData.controls.file_name.setValidators([Validators.required])
      }
    }
    // fixed -> amt
    // PCNT‟/‟MRT  -> percent
    // „CHRG‟->charge-code
    // MRT or SCRIPT =>filename
  subscribeChargeMessage(){
    this.subscription = this.chrgPreferentialAPI.currentMessage.subscribe(message =>{
      this.message = message
      console.log("hey message communicated",this.message )
    })
  }

  // ngOnInit() {
  //   this.subscription = this.data.currentMessage.subscribe(message => this.message = message)
  // }

  ngOnDestroy() {
    this.subscription.unsubscribe();
  }

    submitted = false;
    ngOnInit() {
      this.redirectToMaintenancePage();
      this.getPage();
    }
    redirectToMaintenancePage(){
      this.subscription = this.chrgPreferentialAPI.currentMessage.subscribe(message =>{
        this.message = message;
        if( this.message == "default message"){
          // Redirect to maintenace if no action header
          this.ngZone.run(() => this.router.navigateByUrl('system/configurations/charge/preferentials/maintenance'));
        }else{
          null;
        }
      })
    }
      ac_placeholder = "";
      min_amt_ccy = "";
      max_amt_ccy = "";
      linked_event_id = "";
      formData = this.fb.group({
        function_type: [''],
        chrg_preferential: [''],
        event_type: [''],
        event_id: [''],
        account_id:[''],
        cif_id:[''],
        organization_id:[''],

        start_date: ['', [Validators.required]],
        end_date:['', [Validators.required]],
        chrg_derivation:['', [Validators.required]],
        percentage_val: [''],
        fixed_amt: [''],
        min_amt_ccy: ['', [Validators.required]],
        min_amt: ['', [Validators.required]],
        max_amt_ccy: ['', [Validators.required]],
        max_amt: ['', [Validators.required]],
        is_verified:[''],
        is_deleted:['']
      });
      disabledFormControll(){
        this.formData.controls.function_type.disable();
        this.formData.controls.chrg_preferential.disable();
        this.formData.controls.event_type.disable();
        this.formData.controls.event_id.disable();
        this.formData.controls.account_id.disable();
        this.formData.controls.cif_id.disable();
        this.formData.controls.organization_id.disable();

        this.formData.controls.start_date.disable();
        this.formData.controls.end_date.disable();
        this.formData.controls.chrg_derivation.disable();
        this.formData.controls.percentage_val.disable();
        this.formData.controls.fixed_amt.disable();
        this.formData.controls.min_amt_ccy.disable();
        this.formData.controls.min_amt.disable();
        this.formData.controls.max_amt_ccy.disable();
        this.formData.controls.max_amt.disable();
        this.formData.controls.is_verified.disable();
        this.formData.controls.is_deleted.disable();
      }

      getPage(){
        this.subscription = this.chrgPreferentialAPI.currentMessage.subscribe(message =>{
        this.message = message;
        this.function_type =  this.message.function_type;
        this.event_type_description = this.message.event_type_data.description;
        this.event_type = this.message.event_type_data.code
        this.organization_name = this.message.organization_data.organization_name;
      // this.event_type = result.data.description;

        this.event_id = this.message.event_id;
        this.account_id = this.message.account_id;
        this.cif_id  = this.message.cif_id;
        this.organization_id = this.message.organization_id;
        this.chrg_preferential = this.message.chrg_preferential;

        if(this.function_type == "ADD"){

        this.formData.controls.is_verified.disable();
        this.formData.controls.is_deleted.disable();
          // open empty forms
          this.formData = this.fb.group({
            function_type: [this.function_type],
            chrg_preferential: [this.chrg_preferential],
            event_type: [this.event_type],
            event_id: [this.event_id],
            account_id:[this.account_id],
            cif_id:[this.cif_id],
            organization_id:[this.organization_id],

            start_date: ['', [Validators.required]],
            end_date:['', [Validators.required]],
            chrg_derivation:['', [Validators.required]],
            percentage_val: [''],
            fixed_amt: [''],
            min_amt_ccy: ['', [Validators.required]],
            min_amt: ['', [Validators.required]],
            max_amt_ccy: ['', [Validators.required]],
            max_amt: ['', [Validators.required]],
            is_verified:[''],
            is_deleted:['']

          });
        }
        else if(this.function_type == "INQUIRE"){
          this.showContractInput = true;
          this.disabledFormControll();
          this.params = new HttpParams()
          .set('function_type',this.function_type)
          .set('chrg_preferential', this.chrg_preferential)
          .set('event_type', this.event_type)
          .set('event_id', this.event_id)
          .set('account_id', this.account_id)
          .set('cif_id', this.cif_id)
          .set('organization_id', this.organization_id);
          // hide Buttons
          this.isEnabled = false;
          this.subscription = this.chrgPreferentialAPI.getActualChrgPreferential(this.params).subscribe(res=>{
            this.results = res;
            if(this.results.percentage_val != null){
              this.showPercentageField = true;
              this.formData.controls.percentage_val.disable();
            }else if(this.results.fixed_amt != null){
              this.showFixedAmtField = true;
              this.formData.controls.fixed_amt.disable();
            }

            this.formData = this.fb.group({
              function_type: [this.function_type],
              chrg_preferential: [this.chrg_preferential],
              event_type: [this.event_type],
              event_id: [this.event_id],
              account_id:[this.account_id],
              cif_id:[this.cif_id],
              organization_id:[this.organization_id],

              start_date: [this.results.start_date],
              end_date:[this.results.end_date],
              chrg_derivation:[this.results.chrg_derivation],
              percentage_val: [this.results.percentage_val],
              fixed_amt: [this.results.fixed_amt],
              min_amt_ccy: [this.results.min_amt_ccy],
              min_amt: [this.results.min_amt],
              max_amt_ccy: [this.results.max_amt_ccy],
              max_amt: [this.results.max_amt],
              is_verified:[this.results.is_verified],
              is_deleted:[this.results.is_deleted]
            });
          }, err=>{
            this.error = err;
            this._snackBar.open(this.error, "X", {
              horizontalPosition: this.horizontalPosition,
              verticalPosition: this.verticalPosition,
              duration: 3000,
              panelClass: ['red-snackbar','login-snackbar'],
            });
            this.ngZone.run(() => this.router.navigateByUrl('system/configurations/charge/preferentials/maintenance'));

          })

        }
        else if(this.function_type == "MODIFY"){
        this.formData.controls.is_verified.disable();
        this.formData.controls.is_deleted.disable();

          this.showContractInput = true;
          this.params = new HttpParams()
          .set('function_type',this.function_type)
          .set('chrg_preferential', this.chrg_preferential)
          .set('event_type', this.event_type)
          .set('event_id', this.event_id)
          .set('account_id', this.account_id)
          .set('cif_id', this.cif_id)
          .set('organization_id', this.organization_id);
          this.subscription = this.chrgPreferentialAPI.getActualChrgPreferential(this.params).subscribe(res=>{
            this.results = res;
            console.log("this are data", res)
            this.formData = this.fb.group({
              start_date: [this.results.start_date, [Validators.required]],
              end_date:[this.results.end_date, [Validators.required]],
              chrg_derivation:[this.results.chrg_derivation, [Validators.required]],
              percentage_val: [this.results.percentage_val],
              fixed_amt: [this.results.fixed_amt],
              min_amt_ccy: [this.results.min_amt_ccy, [Validators.required]],
              min_amt: [this.results.min_amt, [Validators.required]],
              max_amt_ccy: [this.results.max_amt_ccy, [Validators.required]],
              max_amt: [this.results.max_amt, [Validators.required]],
              is_verified:[this.results.is_verified],
              is_deleted:[this.results.is_deleted]
            });
          }, err=>{
            this.error = err;
              this.ngZone.run(() => this.router.navigateByUrl('system/configurations/charge/preferentials/maintenance'));
              this._snackBar.open(this.error, "X", {
                horizontalPosition: this.horizontalPosition,
                verticalPosition: this.verticalPosition,
                duration: 3000,
                panelClass: ['red-snackbar','login-snackbar'],
              });
          })

        }
        else if(this.function_type == "VERIFY"){
          this.disabledFormControll();
        this.formData.controls.is_verified.enable();
          // Populate data with rotected fileds only verification is enabled
        this.formData.controls.is_deleted.disable();

          this.showContractInput = true;
          this.params = new HttpParams()
          .set('function_type',this.function_type)
          .set('chrg_preferential', this.chrg_preferential)
          .set('event_type', this.event_type)
          .set('event_id', this.event_id)
          .set('account_id', this.account_id)
          .set('cif_id', this.cif_id)
          .set('organization_id', this.organization_id);
          this.subscription = this.chrgPreferentialAPI.getActualChrgPreferential(this.params).subscribe(res=>{
            this.results = res;
            console.log("this are data", res)
            this.formData = this.fb.group({
              start_date: [this.results.start_date, [Validators.required]],
              end_date:[this.results.end_date, [Validators.required]],
              chrg_derivation:[this.results.chrg_derivation, [Validators.required]],
              percentage_val: [this.results.percentage_val],
              fixed_amt: [this.results.fixed_amt],
              min_amt_ccy: [this.results.min_amt_ccy, [Validators.required]],
              min_amt: [this.results.min_amt, [Validators.required]],
              max_amt_ccy: [this.results.max_amt_ccy, [Validators.required]],
              max_amt: [this.results.max_amt, [Validators.required]],
              is_verified:[this.results.is_verified],
              is_deleted:[this.results.is_deleted]
            });
          }, err=>{
            this.error = err;
              this.ngZone.run(() => this.router.navigateByUrl('system/configurations/charge/preferentials/maintenance'));
              this._snackBar.open(this.error, "X", {
                horizontalPosition: this.horizontalPosition,
                verticalPosition: this.verticalPosition,
                duration: 3000,
                panelClass: ['red-snackbar','login-snackbar'],
              });
          })
        }
        else if(this.function_type == "DELETE"){
          this.disabledFormControll();
        this.formData.controls.is_deleted.enable();
          // should open a page with data and show remove button

          this.showContractInput = true;
          this.params = new HttpParams()
          .set('function_type',this.function_type)
          .set('chrg_preferential', this.chrg_preferential)
          .set('event_type', this.event_type)
          .set('event_id', this.event_id)
          .set('account_id', this.account_id)
          .set('cif_id', this.cif_id)
          .set('organization_id', this.organization_id);
          this.subscription = this.chrgPreferentialAPI.getActualChrgPreferential(this.params).subscribe(res=>{
            this.results = res;
            console.log("this are data", res)
            this.formData = this.fb.group({
              start_date: [this.results.start_date, [Validators.required]],
              end_date:[this.results.end_date, [Validators.required]],
              chrg_derivation:[this.results.chrg_derivation, [Validators.required]],
              percentage_val: [this.results.percentage_val],
              fixed_amt: [this.results.fixed_amt],
              min_amt_ccy: [this.results.min_amt_ccy, [Validators.required]],
              min_amt: [this.results.min_amt, [Validators.required]],
              max_amt_ccy: [this.results.max_amt_ccy, [Validators.required]],
              max_amt: [this.results.max_amt, [Validators.required]],
              is_verified:[this.results.is_verified],
              is_deleted:[this.results.is_deleted]
            });
          }, err=>{
            this.error = err;
              this.ngZone.run(() => this.router.navigateByUrl('system/configurations/charge/preferentials/maintenance'));
              this._snackBar.open(this.error, "X", {
                horizontalPosition: this.horizontalPosition,
                verticalPosition: this.verticalPosition,
                duration: 3000,
                panelClass: ['red-snackbar','login-snackbar'],
              });
          })
        }
      })
      }





      accountLookup(): void {
        // const dialogRef = this.dialog.open(AcPlaceholderLookupComponent, {
        //   height: '400px',
        //   width: '600px',
        // });
        // dialogRef.afterClosed().subscribe(result => {
        //   this.ac_placeholder = result.data;
        //   this.dialogValue = result.data;
        //   this.formData.controls.ac_placeholder.setValue(result.data);
        // });
      }
      minAmtCurrencyLookup(): void {
        const dialogRef = this.dialog.open(CurrencyLookupComponent, {
          height: '400px',
          width: '600px',
        });
        dialogRef.afterClosed().subscribe(result => {
          this.min_amt_ccy = result.data.ccy;
          this.formData.controls.min_amt_ccy.setValue(result.data.ccy);
        });
      }
      maxAmtCurrencyLookup(): void {
        const dialogRef = this.dialog.open(CurrencyLookupComponent, {
          height: '400px',
          width: '600px',
        });
        dialogRef.afterClosed().subscribe(result => {
          this.max_amt_ccy = result.data.ccy;
          this.formData.controls.max_amt_ccy.setValue(result.data.ccy);
        });
      }
      chrgCalcCrncyLookup(): void {
        const dialogRef = this.dialog.open(CurrencyLookupComponent, {
          height: '400px',
          width: '600px',
        });
        dialogRef.afterClosed().subscribe(result => {
          this.chrg_calc_crncy = result.data;
          this.formData.controls.chrg_calc_crncy.setValue(result.data);
        });
      }
      chrgCollCrncyLookup(): void {
        const dialogRef = this.dialog.open(CurrencyLookupComponent, {
          height: '400px',
          width: '600px',
        });
        dialogRef.afterClosed().subscribe(result => {
          this.chrg_coll_crncy= result.data;
          this.formData.controls.chrg_coll_crncy.setValue(result.data);
        });
      }
      // convenience getter for easy access to form fields
      get f() { return this.formData.controls; }
      onSubmit() {
          this.submitted = true;
          console.log("form data before", this.formData.value)
          // stop here if form is invalid
          if (this.formData.valid) {
            if(this.function_type == "ADD"){
              this.selectedStartdate =  this.f.start_date.value.toLocaleDateString(),
              this.selectedEnddate =  this.f.end_date.value.toLocaleDateString(),
              this.fomartedStartDate  =this.datepipe.transform(this.selectedStartdate, 'yyyy-MM-ddTHH:mm:ss');
              this.fomartedEndDate  =this.datepipe.transform(this.selectedEnddate, 'yyyy-MM-ddTHH:mm:ss');

              this.requestParam = this.fb.group({
                    function_type: this.f.function_type.value,
                    chrg_preferential: this.f.chrg_preferential.value,
                    event_type: this.f.event_type.value,
                    event_id: this.f.event_id.value,
                    account_id: this.f.account_id.value,
                    cif_id: this.f.cif_id.value,
                    organization_id: this.f.organization_id.value,
                    start_date: this.fomartedStartDate,
                    end_date: this.fomartedEndDate ,
                    chrg_derivation: this.f.chrg_derivation.value,
                    percentage_val: this.f.percentage_val.value,
                    fixed_amt:  this.f.fixed_amt.value,
                    min_amt_ccy: this.f.min_amt_ccy.value,
                    min_amt: this.f.min_amt.value,
                    max_amt_ccy: this.f.max_amt_ccy.value,
                    max_amt: this.f.max_amt.value,
                });
            this.subscription = this.chrgPreferentialAPI.createChrgPreferential(this.requestParam.value).subscribe(res=>{
              this.results = res;
                this._snackBar.open("Success!", "X", {
                  horizontalPosition: this.horizontalPosition,
                  verticalPosition: this.verticalPosition,
                  duration: 3000,
                  panelClass: ['green-snackbar','login-snackbar'],
                });
          this.ngZone.run(() => this.router.navigateByUrl('system/configurations/charge/preferentials/maintenance'));
          // http://localhost:4200/app_ui/system/configurations/charge/preferentials/maintenance
            },err=>{
              this.error = err;
              this._snackBar.open(this.error, "X", {
                horizontalPosition: this.horizontalPosition,
                verticalPosition: this.verticalPosition,
                duration: 3000,
                panelClass: ['red-snackbar','login-snackbar'],
              });
          this.ngZone.run(() => this.router.navigateByUrl('system/configurations/charge/preferentials/maintenance'));
            })
            }else if(this.function_type != "ADD"){

          this.params = new HttpParams()
          .set('function_type',this.function_type)
          .set('chrg_preferential', this.chrg_preferential)
          .set('event_type', this.event_type)
          .set('event_id', this.event_id)
          .set('account_id', this.account_id)
          .set('cif_id', this.cif_id)
          .set('organization_id', this.organization_id);
          this.subscription = this.chrgPreferentialAPI.getActualChrgPreferential(this.params).subscribe(res=>{
            this.id = res.id;
            console.log("id is", this.id)
            this.subscription = this.chrgPreferentialAPI.updateChrgPreferential(this.id, this.formData.value).subscribe(res=>{
              this.results = res;
                this._snackBar.open("Success!", "X", {
                  horizontalPosition: this.horizontalPosition,
                  verticalPosition: this.verticalPosition,
                  duration: 3000,
                  panelClass: ['green-snackbar','login-snackbar'],
                });
          this.ngZone.run(() => this.router.navigateByUrl('system/configurations/charge/preferentials/maintenance'));
            },err=>{
              this.error = err;
              this._snackBar.open(this.error, "X", {
                horizontalPosition: this.horizontalPosition,
                verticalPosition: this.verticalPosition,
                duration: 3000,
                panelClass: ['red-snackbar','login-snackbar'],
              });
          this.ngZone.run(() => this.router.navigateByUrl('system/configurations/charge/preferentials/maintenance'));
            })
          })
            }

          }else{
            this._snackBar.open("Invalid Form Data", "X", {
              horizontalPosition: this.horizontalPosition,
              verticalPosition: this.verticalPosition,
              duration: 3000,
              panelClass: ['red-snackbar','login-snackbar'],
            });
          this.ngZone.run(() => this.router.navigateByUrl('system/configurations/charge/preferentials/maintenance'));

          }

      }
  }