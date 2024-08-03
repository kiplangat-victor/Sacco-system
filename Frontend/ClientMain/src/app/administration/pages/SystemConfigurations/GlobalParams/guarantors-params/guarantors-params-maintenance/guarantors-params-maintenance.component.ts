import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder, Validators } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
import { DataStoreService } from 'src/@core/helpers/data-store.service';
import { GuarantorsParamsService } from '../guarantors-params.service';

@Component({
  selector: 'app-guarantors-params-maintenance',
  templateUrl: './guarantors-params-maintenance.component.html',
  styleUrls: ['./guarantors-params-maintenance.component.scss']
})
export class GuarantorsParamsMaintenanceComponent implements OnInit {
  existingData: boolean;

  constructor(
    public fb: UntypedFormBuilder,
    private router: Router,
    private _snackBar: MatSnackBar,
    private guarantorsConfigAPI: GuarantorsParamsService,
    private dataStoreApi: DataStoreService
  ) {
    this.functionArray = this.dataStoreApi.getActionsByPrivilege("CONFIGURATIONS");
    this.functionArray = this.functionArray.filter(
      arr => arr === 'ADD' ||
        arr === 'INQUIRE' ||
        arr === 'MODIFY' ||
        arr === 'VERIFY' ||
        arr === 'DELETE');
  }
  ngOnInit(): void {
  }
  loading = false;
  submitted = false;
  functionArray: any

  formData = this.fb.group({
    function_type: ['', [Validators.required]],
    id: [''],
  });
  onSelectFunction(event: any) {
    if (event.target.value != "ADD") {
      this.existingData = true;
    } else if (event.target.value == "ADD") {
      this.existingData = false;
    }
  }
  get f() { return this.formData.controls; }
  onSubmit() {
    this.loading = true;
    this.submitted = true;
    if (this.formData.valid) {
      this.guarantorsConfigAPI.changeMessage(this.formData.value)
      this.router.navigate(['/system/configurations/global/guarantors/data/view'], { skipLocationChange: true });
    } else {
      this.loading = false;
      this._snackBar.open("Invalid Form Data", "X", {
        horizontalPosition: 'end',
        verticalPosition: 'top',
        duration: 3000,
        panelClass: ['red-snackbar', 'login-snackbar'],
      });
    }
  }

}
