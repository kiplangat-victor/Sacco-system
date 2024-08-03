import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MatRadioChange } from '@angular/material/radio';
import { Router } from '@angular/router';
import { Subject, Subscription } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';
import { AccountUsersComponent } from '../../Account-Component/account-users/account-users.component';
import { MainClassificationLookupComponent } from '../../SystemConfigurations/GlobalParams/main-classifications/main-classification-lookup/main-classification-lookup.component';
import { OfficeProductService } from './office-product.service';
import { EventIdLookupComponent } from '../../SystemConfigurations/ChargesParams/event-id/event-id-lookup/event-id-lookup.component';
import { DatePipe } from '@angular/common';
import { CurrencyLookupComponent } from '../../SystemConfigurations/GlobalParams/currency-config/currency-lookup/currency-lookup.component';

@Component({
  selector: 'app-office-product',
  templateUrl: './office-product.component.html',
  styleUrls: ['./office-product.component.scss']
})
export class OfficeProductComponent implements OnInit, OnDestroy {
  tranRestrictionArray: any[] = ['Credit Not Allowed', 'Debit not Allowed', 'Both Br_Cr Allowed', 'Both Br_Cr not Allowed']
  hideBtn: boolean = false;
  btnColor: any;
  btnText: any;
  fmData: any;
  function_type: any;
  productCode: any;
  showButtons: boolean = true;
  loading: boolean = false;
  showSearch: boolean = true;
  onShowResults: boolean = false;
  destroy$: Subject<boolean> = new Subject<boolean>();
  showledgerFeeEventIdCode: boolean = false;
  showAcidStructure: boolean = false;
  onShowDate: boolean = true;
  submitted: boolean = false;
  onShowWarning: boolean = true;
  lookupdata: any;
  chrg_calc_crncy: any;
  chrg_calc_crncy_name: any;
  isEnabled = true;
  results: any;
  runningNo: any;
  constructor(
    private router: Router,
    private fb: FormBuilder,
    private dialog: MatDialog,
    private datepipe: DatePipe,
    private productsAPI: OfficeProductService,
    private notificationAPI: NotificationService
  ) {
    this.fmData = this.router.getCurrentNavigation().extras.queryParams.formData;
    this.function_type = this.fmData.function_type;
    this.productCode = this.fmData.productCode;
  }
  ngOnDestroy(): void {
    this.destroy$.next(true);
    this.destroy$.complete();
  }
  ngOnInit(): void {
    this.getPage();
    this.runningNo = this.productCode + Math.floor(Math.random() * (10000));
  }
  formData: FormGroup = this.fb.group({
    acCurrency: ['UGX', Validators.required],
    acidGeneration: ['', Validators.required],
    acidStructure: [''],
    acPrefix: ['', Validators.required],
    backDatedTransactionExce: ['Y', Validators.required],
    cashLimitCr: ['', Validators.required],
    cashLimitDr: ['', Validators.required],
    collectLedgerFee: [""],
    clearingLimitCrExce: ['', Validators.required],
    clearingLimitDrExce: ['', Validators.required],
    crTranRestrictedExce: ['Y', Validators.required],
    effective_from_date: [new Date, Validators.required],
    effective_to_date: [new Date(), Validators.required],
    eodBalanceCheckExce: ['Y', Validators.required],
    eodMaxBalExce: ['', Validators.required],
    eodMinBalExce: ['', Validators.required],
    id: [''],
    increment: [''],
    ledgerFeeEventIdCode: [""],
    productCode: ['', Validators.required],
    productCodeDesc: ['', Validators.required],
    productType: ['', Validators.required],
    //schemeName: ['', Validators.required],
    schemeSupervisorID: ['', Validators.required],
    tranRestriction: ['', Validators.required],
    transferLimitCr: ['', Validators.required],
    transferLimitDr: ['', Validators.required],
    chrg_calc_crncy: ['', Validators.required]
  });
  get f() {
    return this.formData.controls;
  }
  handleChange(event: MatRadioChange) {
    if (event.value == 'Y') {
      this.showAcidStructure = true;
      this.formData.controls.acidStructure.setValue("PRODUCT-MCODE");
    }
    else if (event.value == 'N') {
      this.showAcidStructure = false;
    }
  }
  handleFeeChange(event: MatRadioChange) {
    if (event.value == true) {
      this.showledgerFeeEventIdCode = true;
    }
    else if (event.value == false) {
      this.showledgerFeeEventIdCode = false;
    }
  }
  ledgerFeeEventIdCodeLookup(): void {
    const dialogRef = this.dialog.open(EventIdLookupComponent, {
      width: '40%'
    });
    dialogRef.afterClosed().subscribe(result => {
      this.lookupdata = result.data;
      this.formData.controls.ledgerFeeEventIdCode.setValue(this.lookupdata.eventIdCode);
    });
  }
  // mainAssetsCodeLookup(): void {
  //   const dialogRef = this.dialog.open(MainClassificationLookupComponent, {
  //     width: "40%",
  //     autoFocus: false,
  //     maxHeight: '90vh'
  //   });
  //   dialogRef.afterClosed().subscribe(result => {
  //     this.lookupdata = result.data;
  //     this.formData.controls.schemeName.setValue(this.lookupdata.assetClassificationName);
  //   });
  // }
  accountManagerLookup(): void {
    const dialogRef = this.dialog.open(AccountUsersComponent, {
      width: '60%',
      autoFocus: false,
      maxHeight: '90vh'
    });
    dialogRef.afterClosed().pipe(takeUntil(this.destroy$)).subscribe(result => {
      this.lookupdata = result.data;
      this.formData.controls.schemeSupervisorID.setValue(this.lookupdata.entityId + this.lookupdata.solCode);
    });
  }
  disableControls() {
    this.formData.disable();
  }
  getPage() {
    if (this.fmData.function_type == 'ADD') {
      this.formData = this.fb.group({
        acCurrency: ['UGX', Validators.required],
        acidGeneration: ['', Validators.required],
        acidStructure: [''],
        acPrefix: ['', Validators.required],
        backDatedTransactionExce: ['Y', Validators.required],
        cashLimitCr: ['', Validators.required],
        cashLimitDr: ['', Validators.required],
        collectLedgerFee: [""],
        clearingLimitCrExce: ['', Validators.required],
        clearingLimitDrExce: ['', Validators.required],
        crTranRestrictedExce: ['Y', Validators.required],
        effective_from_date: [new Date, Validators.required],
        effective_to_date: [new Date(), Validators.required],
        eodBalanceCheckExce: ['Y', Validators.required],
        eodMaxBalExce: ['', Validators.required],
        eodMinBalExce: ['', Validators.required],
        id: [''],
        increment: [''],
        ledgerFeeEventIdCode: [""],
        productCode: [this.fmData.productCode],
        productCodeDesc: [this.fmData.productCodeDesc],
        productType: [this.fmData.productType],
        //schemeName: ['', Validators.required],
        schemeSupervisorID: ['', Validators.required],
        tranRestriction: ['', Validators.required],
        transferLimitCr: ['', Validators.required],
        transferLimitDr: ['', Validators.required],
        chrg_calc_crncy: ['', Validators.required]
      });
      this.btnColor = 'primary';
      this.btnText = 'SUBMIT';
    } else if (this.fmData.function_type == 'INQUIRE') {
      this.getData();
      this.disableControls();
      this.getFunctions();
    }
    else if (this.fmData.function_type == 'MODIFY') {
      this.getData();
      this.btnColor = 'primary';
      this.btnText = 'MODIFY';
    }
    else if (this.fmData.function_type == 'VERIFY') {
      this.getData();
      this.getFunctions();
      this.disableControls();
      this.btnColor = 'primary';
      this.btnText = 'VERIFFY';
    }
    else if (this.fmData.function_type == 'DELETE') {
      this.getData();
      this.getFunctions();
      this.disableControls();
      this.btnColor = 'accent';
      this.btnText = 'DELETE';
    }
  }

  getFunctions() {
    this.showButtons = false
    this.showSearch = false;
    this.onShowDate = false;
    this.onShowResults = true;
    this.onShowWarning = false;
  }
  getData() {
    this.loading = true;
    this.productsAPI.oabCode(this.fmData.productCode).pipe(takeUntil(this.destroy$)).subscribe(
      {
        next: (
          (res) => {
            if (res.statusCode === 200) {
              this.loading = false;
              this.results = res.entity;
              if (this.results.acid_generation == 'Y') {
                this.showAcidStructure = true;
              }
              else if (this.results.acid_generation == 'N') {
                this.showAcidStructure = false;
              }
              if (this.results.collectLedgerFee == true) {
                this.showledgerFeeEventIdCode = true;
              }
              else if (this.results.collectLedgerFee == false) {
                this.showledgerFeeEventIdCode = false;
              }
              this.formData = this.fb.group({
                acCurrency: [this.results.acCurrency],
                acidGeneration: [this.results.acidGeneration],
                acidStructure: [this.results.acidStructure],
                acPrefix: [this.results.acPrefix],
                backDatedTransactionExce: [this.results.backDatedTransactionExce],
                cashLimitCr: [this.results.cashLimitCr],
                cashLimitDr: [this.results.cashLimitDr],
                collectLedgerFee: [this.results.collectLedgerFee],
                clearingLimitCrExce: [this.results.clearingLimitCrExce],
                clearingLimitDrExce: [this.results.clearingLimitDrExce],
                crTranRestrictedExce: [this.results.crTranRestrictedExce],
                effective_from_date: [this.results.effective_from_date],
                effective_to_date: [this.results.effective_to_date],
                eodBalanceCheckExce: [this.results.eodBalanceCheckExce],
                eodMaxBalExce: [this.results.eodMaxBalExce],
                eodMinBalExce: [this.results.eodMinBalExce],
                id: [this.results.id],
                increment: [this.results.increment],
                ledgerFeeEventIdCode: [this.results.ledgerFeeEventIdCode],
                productCode: [this.results.productCode],
                productCodeDesc: [this.results.productCodeDesc],
                productType: [this.results.productType],
                //schemeName: [this.results.schemeName],
                schemeSupervisorID: [this.results.schemeSupervisorID],
                shortName: [this.results.shortName],
                tranRestriction: [this.results.tranRestriction],
                transferLimitCr: [this.results.transferLimitCr],
                transferLimitDr: [this.results.transferLimitDr],
                chrg_calc_crncy: [this.results.acCurrency]
              });

            } else {
              this.loading = false;
              this.notificationAPI.alertWarning("Office Product Details No Available !!");
              this.router.navigate(['/system/product/office-product/maintenance'], { skipLocationChange: true });
            }
          }
        ),
        error: (
          (err) => {
            this.loading = false;
            this.notificationAPI.alertWarning("Server Error: !!");
            this.router.navigate(['/system/product/office-product/maintenance'], { skipLocationChange: true });
          }
        ),
        complete: (
          () => {

          }
        )
      }
    )
  }
  chrgCalcCrncyLookup(): void {
    const dialogRef = this.dialog.open(CurrencyLookupComponent, {});
    dialogRef.afterClosed().subscribe((result) => {
       this.chrg_calc_crncy = result.data.ccy;
      // this.chrg_calc_crncy_name = result.data.ccy_name;
      this.formData.controls.chrg_calc_crncy.setValue(result.data.ccy);
    });
  }
  onSubmit() {
    this.submitted = true;
    this.loading = true;
    this.formData.controls.effective_from_date.setValue(
      this.datepipe.transform(
        this.f.effective_from_date.value,
        'yyyy-MM-ddTHH:mm:ss'
      )
    );
    this.formData.controls.effective_to_date.setValue(
      this.datepipe.transform(
        this.f.effective_to_date.value,
        'yyyy-MM-ddTHH:mm:ss'
      )
    );
    if (this.fmData.function_type == 'ADD') {
      if (this.formData.valid) {
        this.productsAPI.addOab(this.formData.value).pipe(takeUntil(this.destroy$)).subscribe(
          {
            next: (
              (res) => {
                if (res.statusCode === 201) {
                  this.loading = false;
                  this.notificationAPI.alertSuccess(res.message);
                  this.router.navigate(['/system/product/office-product/maintenance'], { skipLocationChange: true });
                } else {
                  this.loading = false;
                  this.notificationAPI.alertWarning(res.message);
                }
              }
            ),
            error: (
              (err) => {
                this.loading = false;
                this.notificationAPI.alertWarning("Server Error: !!");
              }
            ),
            complete: (
              () => {

              }
            )
          }
        ), Subscription;
      } else {
        this.loading = false;
        this.notificationAPI.alertWarning("OFFICE PRODUCT FORM DATA IS INVALID!!");
      }
    }
    else if (this.fmData.function_type == 'MODIFY') {
      if (this.formData.valid) {
        this.productsAPI.modifyOab(this.formData.value).pipe(takeUntil(this.destroy$)).subscribe(
          {
            next: (
              (res) => {
                if (res.statusCode === 200) {
                  this.loading = false;
                  this.notificationAPI.alertSuccess(res.message);
                  this.router.navigate(['/system/product/office-product/maintenance'], { skipLocationChange: true });
                } else {
                  this.loading = false;
                  this.notificationAPI.alertWarning(res.message);
                }
              }
            ),
            error: (
              (err) => {
                this.loading = false;
                this.notificationAPI.alertWarning("Server Error: !!");
              }
            ),
            complete: (
              () => {

              }
            )
          }
        ), Subscription;
      } else {
        this.loading = false;
        this.notificationAPI.alertWarning("OFFICE PRODUCT FORM DATA IS INVALID!!");
      }
    }
    else if (this.fmData.function_type == 'VERIFY') {
      this.loading = true;
      this.productsAPI.verify(this.results.productCode).pipe(takeUntil(this.destroy$)).subscribe(
        {
          next: (
            (res) => {
              if (res.statusCode === 200) {
                this.loading = false;
                this.notificationAPI.alertSuccess(res.message);
                this.router.navigate(['/system/product/office-product/maintenance'], { skipLocationChange: true });
              } else {
                this.loading = false;
                this.notificationAPI.alertWarning(res.message);
              }
            }
          ),
          error: (
            (err) => {
              this.loading = false;
              this.notificationAPI.alertWarning("Server Error: !!");
            }
          ),
          complete: (
            () => {

            }
          )
        }
      ), Subscription;
    }
    else if (this.fmData.function_type == 'DELETE') {
      this.loading = true;
      this.productsAPI.delete(this.results.productCode).pipe(takeUntil(this.destroy$)).subscribe(
        {
          next: (
            (res) => {
              if (res.statusCode === 200) {
                this.loading = false;
                this.notificationAPI.alertSuccess(res.message);
                this.router.navigate(['/system/product/office-product/maintenance'], { skipLocationChange: true });
              } else {
                this.loading = false;
                this.notificationAPI.alertWarning(res.message);
              }
            }
          ),
          error: (
            (err) => {
              this.loading = false;
              this.notificationAPI.alertWarning("Server Error: !!");
            }
          ),
          complete: (
            () => {

            }
          )
        }
      ), Subscription;
    }
  }
}
