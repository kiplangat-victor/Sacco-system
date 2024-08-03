import { HttpClient } from '@angular/common/http';
import { Component, Inject, NgZone, OnInit, Optional } from '@angular/core';
import { UntypedFormBuilder, Validators } from '@angular/forms';
import { MatDialog, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatSnackBar, MatSnackBarHorizontalPosition, MatSnackBarVerticalPosition } from '@angular/material/snack-bar';
import { Router, ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs';
import { SchemeTypeService } from '../scheme-type.service';
import { SchemeTypeLookupComponent } from '../scheme-type-lookup/scheme-type-lookup.component';
import { DataStoreService } from 'src/@core/helpers/data-store.service';

@Component({
  selector: 'app-scheme-type-maintenance',
  templateUrl: './scheme-type-maintenance.component.html',
  styleUrls: ['./scheme-type-maintenance.component.scss']
})
export class SchemeTypeMaintenanceComponent implements OnInit {
  horizontalPosition: MatSnackBarHorizontalPosition = 'end';
  verticalPosition: MatSnackBarVerticalPosition = 'top';
  function_type: any;
  isRequired = false;
  function_type_data: any;
  subscription!: Subscription;
  showOrganizationId = true;
  organization_id: any;
  organization_name: any;
  scheme_type_id: any;
  lookupData: any;
  scheme_type: any;
  showFormGroup = false;
  constructor(
    public fb: UntypedFormBuilder,
    private router: Router,
    private _snackBar: MatSnackBar,
    private dialog: MatDialog,
    private schemeTypeAPI: SchemeTypeService,
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
  }
  loading = false;
  submitted = false;
  functionArray: any;
  formData = this.fb.group({
    function_type: ['', [Validators.required]],
    scheme_type: ['', [Validators.required]],
    lookupData: ['']
  });

  schemeTypeLookup(): void {
    const dialogRef = this.dialog.open(SchemeTypeLookupComponent, {
    });
    dialogRef.afterClosed().subscribe(result => {
      this.lookupData = result.data;
      this.scheme_type = this.lookupData.scheme_type;
      this.scheme_type_id = this.lookupData.id;
      this.formData.controls.scheme_type.setValue(this.scheme_type);
      this.formData.controls.lookupData.setValue(this.lookupData);
    });
  }
  onSelectFunction(event: any) {
    if (event.target.value != "ADD") {
      this.showFormGroup = true;
      this.formData.controls.scheme_type.setValue("")
      this.formData.controls.scheme_type.setValidators([Validators.required])
    } else if (event.target.value == "ADD") {
      this.showFormGroup = false;
      this.formData.controls.scheme_type.setValidators([])
      this.formData.controls.scheme_type.setValue("");
    }
  }
  addEventId() {
    this.router.navigate(['system/event_id'], { skipLocationChange: true });
  }
  get f() { return this.formData.controls; }
  onSubmit() {
    this.loading = true;
    this.submitted = true;
    if (this.formData.valid) {
      this.schemeTypeAPI.changeMessage(this.formData.value)
      this.router.navigate(['system/configurations/global/scheme-type/data/view'], { skipLocationChange: true });
    } else {
      this.loading = false;
      this._snackBar.open("Invalid Form Data", "X", {
        horizontalPosition: this.horizontalPosition,
        verticalPosition: this.verticalPosition,
        duration: 3000,
        panelClass: ['red-snackbar', 'login-snackbar'],
      });
    }
  }

}
