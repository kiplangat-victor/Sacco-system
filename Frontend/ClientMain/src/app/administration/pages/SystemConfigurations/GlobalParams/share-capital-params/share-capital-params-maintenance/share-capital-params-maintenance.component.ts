import { Component, OnInit} from '@angular/core';
import { UntypedFormBuilder, Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar, MatSnackBarHorizontalPosition, MatSnackBarVerticalPosition } from '@angular/material/snack-bar';
import { Router} from '@angular/router';
import { DataStoreService } from 'src/@core/helpers/data-store.service';
import { ShareCapitalParamsLookupComponent } from '../share-capital-params-lookup/share-capital-params-lookup.component';

@Component({
  selector: 'app-share-capital-params-maintenance',
  templateUrl: './share-capital-params-maintenance.component.html',
  styleUrls: ['./share-capital-params-maintenance.component.scss']
})
export class ShareCapitalParamsMaintenanceComponent implements OnInit {
  horizontalPosition: MatSnackBarHorizontalPosition = 'end';
  verticalPosition: MatSnackBarVerticalPosition = 'top';
  lookupData: any;
  shareCapitalCode: any;
  existingData: boolean;
  shareCapitalDescription: any;
  constructor(
    public fb: UntypedFormBuilder,
    private router: Router,
    private _snackBar: MatSnackBar,
    private dialog: MatDialog,
    private dataStoreApi: DataStoreService
  ) {
    this.functionArray = this.dataStoreApi.getActionsByPrivilege("CONFIGURATIONS");
    this.functionArray = this.functionArray.filter(
      arr => arr ===  'ADD' ||
      arr ===  'INQUIRE' ||
      arr ===  'MODIFY' ||
      arr ===  'VERIFY' ||
      arr ===  'DELETE');
     }
  ngOnInit(): void {
    this.lookupData = {}
  }
  loading = false;
  submitted = false;
  functionArray: any;

  formData = this.fb.group({
    function_type: ['', [Validators.required]],
    shareCapitalCode: ['', [Validators.required]],
  });
 shareCapitalLookup(): void {
    const dialogRef = this.dialog.open(ShareCapitalParamsLookupComponent, {
      height: '400px',
      autoFocus: false,
      maxHeight: '90vh'
    });
    dialogRef.afterClosed().subscribe(result => {
      this.lookupData = result.data;
      this.shareCapitalCode = this.lookupData.shareCapitalCode;
      this.shareCapitalDescription = this.lookupData.shareCapitalDescription;
      this.formData.controls.shareCapitalCode.setValue(this.shareCapitalCode);
    });
  }
  onSelectFunction(event:any){
    this.shareCapitalCode;
    if(event.target.value != "ADD"){
      this.existingData = true;
    }else if(event.target.value == "ADD"){
      this.existingData = false;
    }
  }
      get f() { return this.formData.controls; }
  onSubmit(){
    this.loading = true;
    this.submitted = true;
    console.log(this.formData.value);
    if(this.formData.valid){
    this.router.navigate([`/system/configurations/global/share-capital/params/data/view`], {skipLocationChange: true, queryParams: {formData: this.formData.value, fetchData: this.lookupData }});
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
