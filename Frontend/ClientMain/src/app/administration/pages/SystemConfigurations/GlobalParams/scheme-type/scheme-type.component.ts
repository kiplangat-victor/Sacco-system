import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder, Validators } from '@angular/forms';
import { MatSnackBar, MatSnackBarHorizontalPosition, MatSnackBarVerticalPosition } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { SchemeTypeService } from './scheme-type.service';

@Component({
  selector: 'app-scheme-type',
  templateUrl: './scheme-type.component.html',
  styleUrls: ['./scheme-type.component.scss']
})
export class SchemeTypeComponent implements OnInit {
  horizontalPosition: MatSnackBarHorizontalPosition = 'end';
  verticalPosition: MatSnackBarVerticalPosition = 'top';
  loading = false;
  isDisabled = false;
  isEnabled = true;
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
  organization_id: any;
  submitted = false;
  message: any;
  organization_name: any;
  showPercentageField = false;
  showFixedAmtField = false;
  org_chrg_crncy: any;
  formn: any;
  formcontrOrg: any;
  infosecdes: any;

  prioritizationArray: any = [
    'Customer Level','Account Level','Charge Level','Contract Level'
  ]
  scheme_id: any;
  constructor(
    public fb: UntypedFormBuilder,
    private router: Router,
    private _snackBar: MatSnackBar,
    private schemeTypeAPI:  SchemeTypeService,
    ) { }
    ngOnInit() {
      this.redirectToMaintenancePage();
      this.getPage();
    }
    redirectToMaintenancePage(){
      this.subscription = this.schemeTypeAPI.currentMessage.subscribe(message =>{
        this.message = message;
        if( this.message == "default message"){
          this.router.navigate(['system/configurations/global/scheme-type/maintenance'], {skipLocationChange:true});
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
        scheme_type:['', [Validators.required]],
        scheme_abbreviation:[''],
        scheme_category:[''],
        scheme_description:[''],
        is_verified:[''],
        is_deleted:['']
      });
    get f() { return this.formData.controls; }
      disabledFormControll(){
        this.formData.controls.scheme_type.disable();
        this.formData.controls.scheme_abbreviation.disable();
        this.formData.controls.scheme_category.disable();
        this.formData.controls.scheme_description.disable();
        this.formData.controls.is_verified.disable();
        this.formData.controls.is_deleted.disable();
      }
      getPage(){
        this.subscription = this.schemeTypeAPI.currentMessage.subscribe(message =>{
          this.message = message;
          this.function_type = this.message.function_type
          this.scheme_id = this.message.lookupData.id
          this.organization_id = this.message.organization_id
        if(this.function_type == "ADD"){
          this.formData = this.fb.group({
            scheme_type:['', [Validators.required]],
            scheme_abbreviation:[''],
            scheme_category:[''],
            scheme_description:[''],
            is_verified:[''],
            is_deleted:['']
          });
        this.formData.controls.is_verified.disable();
        this.formData.controls.is_deleted.disable();
        }
        else if(this.function_type == "INQUIRE"){
          this.disabledFormControll();
          this.isEnabled = false;
          this.subscription = this.schemeTypeAPI.getSchemetypeId(this.scheme_id).subscribe(res=>{
            this.results = res;
            this.formData = this.fb.group({
              scheme_type:[this.results.scheme_type, [Validators.required]],
              scheme_abbreviation:[this.results.scheme_abbreviation],
              scheme_category:[this.results.scheme_category],
              scheme_description:[this.results.scheme_description],
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
          })
        }
        else if(this.function_type == "MODIFY"){

        this.formData.controls.is_verified.disable();
        this.formData.controls.is_deleted.disable();

          this.subscription = this.schemeTypeAPI.getSchemetypeId(this.scheme_id).subscribe(res=>{
            this.results = res;
            this.formData = this.fb.group({
              scheme_type:[this.results.scheme_type, [Validators.required]],
              scheme_abbreviation:[this.results.scheme_abbreviation],
              scheme_category:[this.results.scheme_category],
              scheme_description:[this.results.scheme_description],
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
          })
        }
        else if(this.function_type == "VERIFY"){

          this.disabledFormControll();
          this.formData.controls.is_verified.enable();

            this.subscription = this.schemeTypeAPI.getSchemetypeId(this.scheme_id).subscribe(res=>{
              this.results = res;
              this.formData = this.fb.group({
                scheme_type:[this.results.scheme_type, [Validators.required]],
                scheme_abbreviation:[this.results.scheme_abbreviation],
                scheme_category:[this.results.scheme_category],
                scheme_description:[this.results.scheme_description],
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
            })
        }
        else if(this.function_type == "DELETE"){

          this.disabledFormControll();
          this.formData.controls.is_deleted.enable();

            this.subscription = this.schemeTypeAPI.getSchemetypeId(this.scheme_id).subscribe(res=>{
              this.results = res;
              this.formData = this.fb.group({
                scheme_type:[this.results.scheme_type, [Validators.required]],
                scheme_abbreviation:[this.results.scheme_abbreviation],
                scheme_category:[this.results.scheme_category],
                scheme_description:[this.results.scheme_description],
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
            })
        }
      })
      }
      onSubmit() {
          this.submitted = true;
          if (this.formData.valid) {
            if(this.function_type == "ADD"){
            this.subscription = this.schemeTypeAPI.createSchemetype(this.formData.value).subscribe(res=>{
              this.results = res;
                this._snackBar.open("Success!", "X", {
                  horizontalPosition: this.horizontalPosition,
                  verticalPosition: this.verticalPosition,
                  duration: 3000,
                  panelClass: ['green-snackbar','login-snackbar'],
                });
              this.router.navigate(['system/configurations/global/scheme-type/maintenance'], {skipLocationChange:true});
            },err=>{
              this.error = err;
              this._snackBar.open(this.error, "X", {
                horizontalPosition: this.horizontalPosition,
                verticalPosition: this.verticalPosition,
                duration: 3000,
                panelClass: ['red-snackbar','login-snackbar'],
              });
            })
            }else if(this.function_type != "ADD"){
              this.subscription = this.schemeTypeAPI.updateSchemetype(this.scheme_id, this.formData.value).subscribe(res=>{
                this.results = res;
                  this._snackBar.open("Success!", "X", {
                    horizontalPosition: this.horizontalPosition,
                    verticalPosition: this.verticalPosition,
                    duration: 3000,
                    panelClass: ['green-snackbar','login-snackbar'],
                  });
                  this.router.navigate(['system/configurations/global/scheme-type/maintenance'], {skipLocationChange:true});
                },err=>{
                this.error = err;
                this._snackBar.open(this.error, "X", {
                  horizontalPosition: this.horizontalPosition,
                  verticalPosition: this.verticalPosition,
                  duration: 3000,
                  panelClass: ['red-snackbar','login-snackbar'],
                });
              })
            }
          }else{
            this._snackBar.open("Invalid Form Data", "X", {
              horizontalPosition: this.horizontalPosition,
              verticalPosition: this.verticalPosition,
              duration: 3000,
              panelClass: ['red-snackbar','login-snackbar'],
            });
          }
      }
  }
