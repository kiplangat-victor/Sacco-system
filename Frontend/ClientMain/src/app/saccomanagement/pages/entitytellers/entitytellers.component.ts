import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';
import { EntityuserlookupComponent } from '../entityuser/entityuserlookup/entityuserlookup.component';
import { TellersService } from 'src/app/administration/pages/AccessManagement/tellers-management/tellers.service';
import { OfficeTellerAccountsLookupComponent } from 'src/app/administration/pages/Account-Component/office-account/office-teller-accounts-lookup/office-teller-accounts-lookup.component';

@Component({
  selector: 'app-entitytellers',
  templateUrl: './entitytellers.component.html',
  styleUrls: ['./entitytellers.component.scss']
})
export class EntitytellersComponent implements OnInit {
  function_type: string;
  branchCode: any;
  error: any;
  results: any;
  fetchData: any;
  fmData: any;
  isDisabled: boolean;
  postedBy: any;
  verifiedFlag: any;
  verifiedBy: any;
  lookupData: any;
  submitted: boolean;
  tellAccounName: any;
  tellName: any;
  acid: any;
  customerType: any;
  formData: FormGroup;
  loading = false;
  onShowResults = false;
  hideSubmit = false;
  btnColor = 'primary';
  submitData = 'SUBMIT'
  postedTime: any;

  constructor(
    public fb: FormBuilder,
    private router: Router,
    private tellersService: TellersService,
    private dialog: MatDialog,
    private notificationService: NotificationService
  ) {
    this.fmData =
      this.router.getCurrentNavigation().extras.queryParams.formData;
    this.fetchData =
      this.router.getCurrentNavigation().extras.queryParams.fetchData.entity;
    if (this.router.getCurrentNavigation().extras.queryParams == null) {
      this.router.navigate([`/saccomanagement/`], { skipLocationChange: true });
    }
    this.function_type = this.fmData.function_type;
    if (this.fetchData != null) {
      this.postedBy = this.fetchData.postedBy;
      this.postedTime = this.fetchData.postedTime;
      this.verifiedFlag = this.fetchData.verifiedFlag;
      this.verifiedBy = this.fetchData.verifiedBy;
    }
  }
  ngOnInit() {
    this.getPage();
  }

  onInitEmptyForm() {
    this.formData = this.fb.group({
      tellerId: ['', [Validators.required]],
      tellerUserName: ['', [Validators.required]],
      tellerAc: ['', [Validators.required]],
      shortageAc: ['', [Validators.required]],
      excessAc: ['', [Validators.required]],
    });
  }
  onInitPrefilledForm() {
    this.formData = this.fb.group({
      id: [this.fetchData.id, [Validators.required]],
      tellerId: [this.fetchData.tellerId, [Validators.required]],
      tellerUserName: [this.fetchData.tellerUserName, [Validators.required]],
      tellerAc: [this.fetchData.tellerAc, [Validators.required]],
      shortageAc: [this.fetchData.shortageAc, [Validators.required]],
      excessAc: [this.fetchData.excessAc, [Validators.required]],
    });
  }
  disabledFormControl() {
    this.formData.disable();
  }
  get f() {
    return this.formData.controls;
  }


  tellerAccountIdLookup(): void {
    const dialogRef = this.dialog.open(OfficeTellerAccountsLookupComponent, {
      width: '45%',
      autoFocus: false,
      maxHeight: '90vh'
    });
    dialogRef.afterClosed().subscribe((result) => {
      this.lookupData = result.data;
      this.acid = this.lookupData.acid;
      this.customerType = this.lookupData.customerType;
      this.formData.patchValue({
        tellerAc: this.acid,
      });
    });
  }
  shortageAccountIdLookup(): void {
    const dialogRef = this.dialog.open(OfficeTellerAccountsLookupComponent, {
      width: '45%',
      autoFocus: false,
      maxHeight: '90vh'
    });
    dialogRef.afterClosed().subscribe((result) => {
      this.lookupData = result.data;
      this.acid = this.lookupData.acid;
      this.formData.patchValue({
        shortageAc: this.acid,
      });
    });
  }
  excessAccountIdLookup(): void {
    const dialogRef = this.dialog.open(OfficeTellerAccountsLookupComponent, {
      width: '45%',
      autoFocus: false,
      maxHeight: '90vh'
    });
    dialogRef.afterClosed().subscribe((result) => {
      this.lookupData = result.data;
      this.acid = this.lookupData.acid;
      this.formData.patchValue({
        excessAc: this.acid,
      });
    });
  }
  userLookup(): void {
    const dialogRef = this.dialog.open(EntityuserlookupComponent, {
      width: '60%',
      autoFocus: false,
      maxHeight: '90vh'
    });
    dialogRef.afterClosed().subscribe(result => {
      this.lookupData = result.data;
      this.branchCode = this.lookupData.branchCode;
      this.formData.patchValue({
        tellerId: this.lookupData.sn,
        tellerUserName: this.lookupData.username,
      });
    });
  }
  getPage() {
    if (this.function_type == 'ADD') {
      this.onInitEmptyForm();
    } else if (this.function_type == 'INQUIRE') {
      this.hideSubmit = true;
      this.onInitPrefilledForm();
      this.disabledFormControl();
      this.isDisabled = true;
      this.onShowResults = true;
    } else if (this.function_type == 'MODIFY') {
      this.submitData = 'MODIFY'
      this.onInitPrefilledForm();
      this.onShowResults = true;
    } else if (this.function_type == 'VERIFY') {
      this.submitData = 'VERIFY'
      this.onInitPrefilledForm();
      this.disabledFormControl();
      this.isDisabled = true;
      this.onShowResults = true;
    } else if (this.function_type == 'DELETE') {
      this.btnColor = 'warn';
      this.submitData = 'DELETE'
      this.onInitPrefilledForm();
      this.disabledFormControl();
      this.isDisabled = true;
      this.onShowResults = true;
    }
  }
  onSubmit() {
    this.submitted = true;
    if (this.function_type == 'ADD') {
      this.loading = true;
      if (this.formData.valid) {
        console.log("TELLER FORM DATA", this.formData.value);
        this.tellersService.create(this.formData.value).subscribe(
          (res) => {
            if (res.statusCode == 201) {
              this.loading = false;
              this.notificationService.alertSuccess(res.message);
              this.router.navigate([`/saccomanagement/entity-tellers/maintenance`], {
                skipLocationChange: true,
              });
            } else {
              this.loading = false;
              this.notificationService.alertWarning(res.message);
            }
          },
          (err) => {
            this.error = err;
            this.loading = false;
            this.notificationService.alertWarning("Server Error: ");
          }
        );
      } else {
        this.loading = false;
        this.notificationService.alertWarning("TELLER FORM DATA IS INVALID");
      }
    }
    if (this.function_type == 'MODIFY') {
      if (this.formData.valid) {
        this.tellersService.modify(this.formData.value).subscribe(
          (res) => {
            this.results = res;
            console.log(res);
            this.notificationService.alertSuccess(this.results.message);
            this.router.navigate([`/saccomanagement/entity-tellers/maintenance`], {
              skipLocationChange: true,
            });
          },
          (err) => {
            this.loading = false;
            this.error = err;
            this.notificationService.alertWarning(this.error);
          }
        );
      } else {
        this.loading = false;
        this.notificationService.alertWarning("TELLER FORM DATA IS INVALID");
      }
    }
    if (this.function_type == 'VERIFY') {
      this.tellersService.verify(this.fetchData.id).subscribe(
        (res) => {
          this.results = res;
          this.notificationService.alertSuccess(this.results.message);
          this.router.navigate([`/saccomanagement/entity-tellers/maintenance`], {
            skipLocationChange: true,
          });
        },
        (err) => {
          this.loading = false;
          this.error = err;
          this.notificationService.alertWarning(this.error);
        }
      );
    }
    if (this.function_type == 'DELETE') {
      this.tellersService.delete(this.fetchData.id).subscribe(
        (res) => {
          this.results = res;
          this.notificationService.alertSuccess(this.results.message);
          this.router.navigate([`/saccomanagement/entity-tellers/maintenance`], {
            skipLocationChange: true,
          });
        },
        (err) => {
          this.loading = false;
          this.error = err;
          this.notificationService.alertWarning(this.results.message);
        }
      );
    }
  }
}
