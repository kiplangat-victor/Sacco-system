import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder, Validators } from '@angular/forms';
import { MatSnackBar, MatSnackBarHorizontalPosition, MatSnackBarVerticalPosition } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';
import { ShareCapitalParamsService } from './share-capital-params.service';

@Component({
  selector: 'app-share-capital-params',
  templateUrl: './share-capital-params.component.html',
  styleUrls: ['./share-capital-params.component.scss']
})
export class ShareCapitalParamsComponent implements OnInit {
  horizontalPosition: MatSnackBarHorizontalPosition = 'end';
  verticalPosition: MatSnackBarVerticalPosition = 'top';
  loading = false;
  function_type: string;
  shareCapitalCode: any;
  error: any;
  results: any;
  fetchData: any;
  fmData: any;
  isDisabled: boolean;
  postedBy: any;
  verifiedFlag: any;
  verifiedBy: any;
  isEnabled = true;
  constructor(
    public fb: UntypedFormBuilder,
    private router: Router,
    private _snackBar: MatSnackBar,
    private shareCapitalParams: ShareCapitalParamsService,
    private notificationAPI: NotificationService
  ) {
    this.fmData = this.router.getCurrentNavigation().extras.queryParams.formData;
    this.fetchData = this.router.getCurrentNavigation().extras.queryParams.fetchData;
    this.function_type = this.fmData.function_type;
    this.shareCapitalCode = this.fmData.shareCapitalCode;
    this.postedBy = this.fetchData.postedBy
    this.verifiedFlag = this.fetchData.verifiedFlag
    this.verifiedBy = this.fetchData.verifiedBy
  }
  ngOnInit() {
    this.getPage();
  }
  formData = this.fb.group({
    id: [''],
    share_capital_unit:['',[Validators.required]],
    share_capital_amount_per_unit:['',[Validators.required]],
    share_min_unit:['',[Validators.required]],
    shares_office_ac:['',[Validators.required]]
  });
  disabledFormControll() {
    this.formData.controls.id.disable();
    this.formData.controls.share_capital_unit.disable();
    this.formData.controls.share_capital_amount_per_unit.disable();
    this.formData.controls.share_min_unit.disable();
    this.formData.controls.shares_office_ac.disable();
  }
  get f() { return this.formData.controls; }
  getData(){
    this.formData = this.fb.group({
      shareCapitalCode: [this.shareCapitalCode],
      id: [this.fetchData.id],
      share_capital_unit:[this.fetchData.share_capital_unit],
      share_capital_amount_per_unit:[this.fetchData.share_capital_amount_per_unit],
      share_min_unit:[this.fetchData.share_min_unit],
      shares_office_ac:[this.fetchData.shares_office_ac]
    });
  }
  getPage() {
    if (this.function_type == "ADD") {
      this.formData = this.fb.group({
        shareCapitalCode: [this.shareCapitalCode],
        id: [''],
        share_capital_unit:[''],
        share_capital_amount_per_unit:[''],
        share_min_unit:[''],
        shares_office_ac:['']
      });
    }
    else if (this.function_type == "INQUIRE") {
      this.disabledFormControll()
      this.isEnabled = false;
      this.getData();
    }
    else if (this.function_type == "MODIFY") {
      this.getData();
    }
    else if (this.function_type == "VERIFY") {
      this.disabledFormControll();
      this.getData();
    }
    else if (this.function_type == "DELETE") {
      this.disabledFormControll();
      this.getData();
    }
  }
  onSubmit() {
    this.loading = true;
    if (this.function_type == "ADD") {
      if (this.formData.valid) {
        this.shareCapitalParams.create(this.formData.value).subscribe(res => {
          if (res.statusCode == 201) {
            this._snackBar.open(res.message, "X", {
              horizontalPosition: this.horizontalPosition,
              verticalPosition: this.verticalPosition,
              duration: 3000,
              panelClass: ['green-snackbar', 'login-snackbar'],
            });
          } else {
            this._snackBar.open(res.message, "X", {
              horizontalPosition: this.horizontalPosition,
              verticalPosition: this.verticalPosition,
              duration: 3000,
              panelClass: ['red-snackbar', 'login-snackbar'],
            });
          }
          this.loading = false;
          this.router.navigate([`/system/configurations/global/share-capital/params/maintenance`], { skipLocationChange: true });

        }, err => {
          this.error = err;
          this.loading = false;
          this._snackBar.open(this.error, "X", {
            horizontalPosition: this.horizontalPosition,
            verticalPosition: this.verticalPosition,
            duration: 3000,
            panelClass: ['red-snackbar', 'login-snackbar'],
          });
          this.loading = false;
        })

      } else  {
        this.loading = false;
        this._snackBar.open("Invalid Form Data", "X", {
          horizontalPosition: this.horizontalPosition,
          verticalPosition: this.verticalPosition,
          duration: 3000,
          panelClass: ['red-snackbar', 'login-snackbar'],
        });
      }
    }
    if (this.function_type  ==  "MODIFY") {
      if (this.formData.valid) {
        this.shareCapitalParams.modify(this.formData.value).subscribe(res => {
          if (res.statusCode == 200) {
          this.notificationAPI.alertSuccess(res.message);
          } else {
          this.notificationAPI.alertWarning(res.message);
          }
          this.loading = false;
          this.router.navigate([`/system/configurations/global/share-capital/params/maintenance`], { skipLocationChange: true });
        }, err => {
          this.loading = false;
          this.error = err;
          this._snackBar.open(this.error, "X", {
            horizontalPosition: this.horizontalPosition,
            verticalPosition: this.verticalPosition,
            duration: 3000,
            panelClass: ['red-snackbar', 'login-snackbar'],
          });
        })
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

    if (this.function_type  ==  "VERIFY") {
      this.shareCapitalParams.verify(this.fetchData.id).subscribe(res => {
        if (res.statusCode == 200) {
        this.notificationAPI.alertSuccess(res.message);
        } else {
        this.notificationAPI.alertWarning(res.message);
        }
        this.loading = false;
        this.router.navigate([`/system/configurations/global/share-capital/params/maintenance`], { skipLocationChange: true });
      }, err => {
        this.loading = false;
        this.error = err;
        this._snackBar.open(this.error, "X", {
          horizontalPosition: this.horizontalPosition,
          verticalPosition: this.verticalPosition,
          duration: 3000,
          panelClass: ['red-snackbar', 'login-snackbar'],
        });
      })
    }

    if (this.function_type  ==  "DELETE") {
      this.shareCapitalParams.delete(this.fetchData.id).subscribe(res => {
        if (res.statusCode == 200) {
        this.notificationAPI.alertSuccess(res.message);
        } else {
        this.notificationAPI.alertWarning(res.message);
        }
        this.loading = false;
        this.router.navigate([`/system/configurations/global/share-capital/params/maintenance`], { skipLocationChange: true });
      }, err => {
        this.loading = false;
        this.error = err;
        this._snackBar.open(this.error, "X", {
          horizontalPosition: this.horizontalPosition,
          verticalPosition: this.verticalPosition,
          duration: 3000,
          panelClass: ['red-snackbar', 'login-snackbar'],
        });
      })
    }
  }
}
