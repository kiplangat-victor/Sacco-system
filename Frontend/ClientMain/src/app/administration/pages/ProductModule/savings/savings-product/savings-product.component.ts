import { HttpParams } from '@angular/common/http';
import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatRadioChange } from '@angular/material/radio';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { Router } from '@angular/router';
import { Subject, Subscription } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';
import { ProductsService } from 'src/app/administration/Service/products/products.service';
import { OfficeAccountsLookUpsComponent } from '../../../Account-Component/office-account/office-accounts-look-ups/office-accounts-look-ups.component';
import { EventIdLookupComponent } from '../../../SystemConfigurations/ChargesParams/event-id/event-id-lookup/event-id-lookup.component';
import { GlSubheadLookupComponent } from '../../../SystemConfigurations/GlobalParams/gl-subhead/gl-subhead-lookup/gl-subhead-lookup.component';
import { InterestcodelookupComponent } from '../../../SystemConfigurations/interestcode/interestcodelookup/interestcodelookup.component';
import { GeneralProductLookUpComponent } from '../../general-product-look-up/general-product-look-up.component';
import { DatePipe } from '@angular/common';

@Component({
  selector: 'app-savings-product',
  templateUrl: './savings-product.component.html',
  styleUrls: ['./savings-product.component.scss']
})
export class SavingsProductComponent implements OnInit, OnDestroy {
  autoAddedAccountsColumns: string[] = [
    "index",
    "productCode",
    "actions",
  ];
  glsubheadsColumns: string[] = [
    "index",
    "gl_code",
    'gl_subhead',
    'gl_subhead_description',
    "actions",
  ];
  autoAddedAccountsDataSource: MatTableDataSource<any>;
  @ViewChild("autoAddedAccountsPaginator") autoAddedAccountsPaginator!: MatPaginator;
  @ViewChild(MatSort) autoAddedAccountsSort!: MatSort;
  glsubheadsDataSource: MatTableDataSource<any>;
  @ViewChild("glsubheadsPaginator") glsubheadsPaginator!: MatPaginator;
  @ViewChild(MatSort) glsubheadsSort!: MatSort;
  index: number;
  autoAddedAccountsForm!: FormGroup;
  autoAddedAccountsArray: any[] = [];
  glsubheadsForm!: FormGroup;
  glsubheadsArray: any[] = [];
  editButton: boolean = false;
  addButton: boolean = true;
  disableAddButton: boolean = false;
  disableActions: boolean = false;
  submittedSba: boolean = false;
  hideBtn: boolean = false;
  btnColor: any;
  btnText: any;
  fmData: any;
  function_type: any;
  productCode: any;
  loading: boolean = false;
  onShowResults: boolean = false;
  destroy$: Subject<boolean> = new Subject<boolean>();
  showAcidStructure: boolean = false;
  onShowDate: boolean = true;
  submitted: boolean = false;
  showSearch: boolean = true;
  showButtons: boolean = true;
  onShowWarning: boolean = true;
  lookupdata: any;
  results: any;
  params: any;
  sbaDetails: any;
  showautoAddedAccountsForm: boolean = true;
  showglsubheadsForm: boolean = true;
  runningNo: any;
  showEventIdCharges: boolean = false;
  showledgerFeeEventIdCode: boolean = false;
  constructor(
    private router: Router,
    private fb: FormBuilder,
    private dialog: MatDialog,
    private datepipe: DatePipe,
    private productsAPI: ProductsService,
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
    this.runningNo = this.productCode + Math.floor(Math.random() * (1000));
    this.initialisAutoAddedAccountsForm();
    this.initialiseglsubheadsForm();
  }
  formData: FormGroup = this.fb.group({
    acid_generation: ['', Validators.required],
    acid_structure: [''],
    advance_int_ac: ['', Validators.required],
    autoAddedAccounts: [[]],
    collectLedgerFee: [""],
    effective_from_date: [new Date, Validators.required],
    effective_to_date: [new Date(), Validators.required],
    id: [''],
    glsubheads: [[]],
    increment: ['', Validators.required],
    int_receivable_ac: ['', Validators.required],
    interest_table_code: ['', Validators.required],
    ledgerFeeEventIdCode: [""],
    penal_int_receivable_ac: ['', Validators.required],
    penal_receivable_ac: ['', Validators.required],
    pl_ac: ['', Validators.required],
    pl_ac_ccy: ['', Validators.required],
    princ_receivable_ac: ['', Validators.required],
    productCode: ['', Validators.required],
    productCodeDesc: ['', Validators.required],
    productType: ['', Validators.required],
    running_no_size: [''],
    sbaDetails: ['']
  });

  sbaDetailsForm: FormGroup = this.fb.group({
    allow_mobile_transactions: ['', Validators.required],
    eventIdCode: [''],
    eventTypeCode: [''],
    id: [''],
    min_account_balance: ['', Validators.required],
    no_of_withdrawals: ['', Validators.required],
    withdrawalCharge: ['', Validators.required],
    withdrawalsAllowed: ['', Validators.required]
  });
  get f() {
    return this.formData.controls;
  }
  get sba() {
    return this.sbaDetailsForm.controls;
  }
  eventIdChange(event: MatRadioChange) {
    if (event.value == 'Y') {
      this.showEventIdCharges = true;
    }
    else if (event.value == 'N') {
      this.showEventIdCharges = false;
    }
  }
  eventIdCodeLookup(): void {
    const dialogRef = this.dialog.open(EventIdLookupComponent, {
      width: '40%'
    });
    dialogRef.afterClosed().subscribe(result => {
      this.lookupdata = result.data;
      this.sbaDetailsForm.controls.eventIdCode.setValue(this.lookupdata.eventIdCode);
      this.sbaDetailsForm.controls.eventTypeCode.setValue(this.lookupdata.eventTypeCode);
    });
  }
  handleChange(event: MatRadioChange) {
    if (event.value == 'Y') {
      this.showAcidStructure = true;
      this.formData.controls.acid_structure.setValue("PRODUCT-MCODE");
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
  initialisAutoAddedAccountsForm() {
    this.initAutoAddedAccountsForm();
    this.autoAddedAccountsForm.controls.id.setValidators([]);
    this.autoAddedAccountsForm.controls.productCode.setValidators([Validators.required]);
  }
  initAutoAddedAccountsForm() {
    this.autoAddedAccountsForm = this.fb.group({
      id: [''],
      productCode: ['']
    })
  }
  addgAutoAddedAccounts() {
    if (this.autoAddedAccountsForm.valid) {
      this.autoAddedAccountsArray.push(this.autoAddedAccountsForm.value);
      this.resetAutoAddedAccountsForm();
    }
  }
  getAutoAddedAccounts() {
    this.autoAddedAccountsDataSource = new MatTableDataSource(this.autoAddedAccountsArray);
    this.autoAddedAccountsDataSource.paginator = this.autoAddedAccountsPaginator;
    this.autoAddedAccountsDataSource.sort = this.autoAddedAccountsSort;
  }
  resetAutoAddedAccountsForm() {
    this.formData.patchValue({
      autoAddedAccounts: this.autoAddedAccountsArray,
    });
    this.getAutoAddedAccounts();
    this.initAutoAddedAccountsForm();
  }
  editAutoAddedAccounts(data: any) {
    this.index = this.autoAddedAccountsArray.indexOf(data);
    this.editButton = true;
    this.addButton = false;
    this.autoAddedAccountsForm.patchValue({
      id: data.id,
      productCode: data.productCode
    });
  }
  updateAutoAddedAccounts() {
    this.editButton = false;
    this.addButton = true;
    this.autoAddedAccountsArray[this.index] = this.autoAddedAccountsForm.value;
    this.formData.patchValue({
      autoAddedAccounts: this.autoAddedAccountsArray
    });
    this.resetAutoAddedAccountsForm();
  }
  deleteAutoAddedAccounts(data: any) {
    let deleteIndex = this.autoAddedAccountsArray.indexOf(data);
    this.autoAddedAccountsArray.splice(deleteIndex, 1);
    this.resetAutoAddedAccountsForm();
  }
  applyAutoAddedAccountsFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.autoAddedAccountsDataSource.filter = filterValue.trim().toLowerCase();
    if (this.autoAddedAccountsDataSource.paginator) {
      this.autoAddedAccountsDataSource.paginator.firstPage();
    }
  }
  initialiseglsubheadsForm() {
    this.initglsubheadsForm();
    this.glsubheadsForm.controls.gl_code.setValidators([Validators.required]);
    this.glsubheadsForm.controls.gl_subhead.setValidators([Validators.required]);
    this.glsubheadsForm.controls.gl_subhead_deafault.setValidators([]);
    this.glsubheadsForm.controls.gl_subhead_description.setValidators([Validators.required]);
    this.glsubheadsForm.controls.id.setValidators([]);
  }
  initglsubheadsForm() {
    this.glsubheadsForm = this.fb.group({
      gl_code: [''],
      gl_subhead: [''],
      gl_subhead_deafault: [''],
      gl_subhead_description: [''],
      id: ['']
    })
  }
  addglsubheads() {
    if (this.glsubheadsForm.valid) {
      this.glsubheadsArray.push(this.glsubheadsForm.value);
      this.resetglsubheadsForm();
    }
  }
  getglsubheads() {
    this.glsubheadsDataSource = new MatTableDataSource(this.glsubheadsArray);
    this.glsubheadsDataSource.paginator = this.glsubheadsPaginator;
    this.glsubheadsDataSource.sort = this.glsubheadsSort;
  }
  resetglsubheadsForm() {
    this.formData.patchValue({
      glsubheads: this.glsubheadsArray,
    });
    this.getglsubheads();
    this.initglsubheadsForm();

  }
  editglsubheads(data: any) {
    this.index = this.glsubheadsArray.indexOf(data);
    this.editButton = true;
    this.addButton = false;
    this.glsubheadsForm.patchValue({
      gl_code: data.gl_code,
      gl_subhead: data.gl_subhead,
      gl_subhead_deafault: data.gl_subhead_deafault,
      gl_subhead_description: data.gl_subhead_description,
      id: data.id
    });
  }
  updateglsubheads() {
    this.editButton = false;
    this.addButton = true;
    this.glsubheadsArray[this.index] = this.glsubheadsForm.value;
    this.formData.patchValue({
      glsubheads: this.glsubheadsArray
    });
    this.resetglsubheadsForm();
  }
  deleteglsubheads(docdata: any) {
    let deleteIndex = this.glsubheadsArray.indexOf(docdata);
    this.glsubheadsArray.splice(deleteIndex, 1);
    this.resetglsubheadsForm();
  }
  applyglsubheadsFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.glsubheadsDataSource.filter = filterValue.trim().toLowerCase();
    if (this.glsubheadsDataSource.paginator) {
      this.glsubheadsDataSource.paginator.firstPage();
    }
  }

  glSubheadLookup(): void {
    const dialogRef = this.dialog.open(GlSubheadLookupComponent, {
      width: '35%',
      autoFocus: false,
      maxHeight: '90vh'
    });
    dialogRef.afterClosed().subscribe(result => {
      this.lookupdata = result.data;
      this.glsubheadsForm.controls.gl_code.setValue(this.lookupdata.glCode);
      this.glsubheadsForm.controls.gl_subhead.setValue(this.lookupdata.glSubheadCode);
      this.glsubheadsForm.controls.gl_subhead_description.setValue(this.lookupdata.glSubheadDescription);
    });
  }
  intTableCodeLookup(): void {
    const dialogRef = this.dialog.open(InterestcodelookupComponent, {
      width: '35%',
      autoFocus: false,
      maxHeight: '90vh'
    });
    dialogRef.afterClosed().subscribe(result => {
      this.lookupdata = result.data;
      this.formData.controls.interest_table_code.setValue(this.lookupdata.interestCode);
    });
  }
  intAccountLookup(): void {
    const dialogRef = this.dialog.open(OfficeAccountsLookUpsComponent, {
      width: '45%',
      autoFocus: false,
      maxHeight: '90vh',
    });
    dialogRef.afterClosed().subscribe(result => {
      this.lookupdata = result.data;
      this.formData.controls.int_receivable_ac.setValue(this.lookupdata.acid);
    });
  }
  advanceAccountLookup(): void {
    const dialogRef = this.dialog.open(OfficeAccountsLookUpsComponent, {
      width: '45%',
      autoFocus: false,
      maxHeight: '90vh',
    });
    dialogRef.afterClosed().subscribe(result => {
      this.lookupdata = result.data;
      this.formData.controls.advance_int_ac.setValue(this.lookupdata.acid);
    });
  }
  penalIntAccountLookup(): void {
    const dialogRef = this.dialog.open(OfficeAccountsLookUpsComponent, {
      width: '45%',
      autoFocus: false,
      maxHeight: '90vh',
    });
    dialogRef.afterClosed().subscribe(result => {
      this.lookupdata = result.data;
      this.formData.controls.penal_int_receivable_ac.setValue(this.lookupdata.acid);
    });
  }
  penalAccountLookup(): void {
    const dialogRef = this.dialog.open(OfficeAccountsLookUpsComponent, {
      width: '45%',
      autoFocus: false,
      maxHeight: '90vh',
    });
    dialogRef.afterClosed().subscribe(result => {
      this.lookupdata = result.data;
      this.formData.controls.penal_receivable_ac.setValue(this.lookupdata.acid);
    });
  }
  plAccountLookup(): void {
    const dialogRef = this.dialog.open(OfficeAccountsLookUpsComponent, {
      width: '45%',
      autoFocus: false,
      maxHeight: '90vh',
    });
    dialogRef.afterClosed().subscribe(result => {
      this.lookupdata = result.data;
      this.formData.controls.pl_ac.setValue(this.lookupdata.acid);
    });
  }
  princAccountLookup(): void {
    const dialogRef = this.dialog.open(OfficeAccountsLookUpsComponent, {
      width: '45%',
      autoFocus: false,
      maxHeight: '90vh',
    });
    dialogRef.afterClosed().subscribe(result => {
      this.lookupdata = result.data;
      this.formData.controls.princ_receivable_ac.setValue(this.lookupdata.acid);
    });
  }
  productLookup(): void {
    const dialogRef = this.dialog.open(GeneralProductLookUpComponent, {
      width: '40%',
      autoFocus: false,
      maxHeight: '90vh',
    });
    dialogRef.afterClosed().subscribe(result => {
      this.lookupdata = result.data;
      this.autoAddedAccountsForm.controls.productCode.setValue(this.lookupdata.productCode);
    });
  }
  getPage() {
    if (this.fmData.function_type == 'ADD') {
      this.formData = this.fb.group({
        acid_generation: ['', Validators.required],
        acid_structure: [''],
        advance_int_ac: ['', Validators.required],
        autoAddedAccounts: [[]],
        collectLedgerFee: [""],
        effective_from_date: [new Date, Validators.required],
        effective_to_date: [new Date(), Validators.required],
        id: [''],
        glsubheads: [[]],
        increment: ['', Validators.required],
        int_receivable_ac: ['', Validators.required],
        interest_table_code: ['', Validators.required],
        ledgerFeeEventIdCode: [""],
        penal_int_receivable_ac: ['', Validators.required],
        penal_receivable_ac: ['', Validators.required],
        pl_ac: ['', Validators.required],
        pl_ac_ccy: ['', Validators.required],
        princ_receivable_ac: ['', Validators.required],
        productCode: [this.fmData.productCode],
        productCodeDesc: [this.fmData.productCodeDesc],
        productType: [this.fmData.productType],
        running_no_size: [''],
        sbaDetails: ['']
      });
      this.sbaDetailsForm = this.fb.group({
        allow_mobile_transactions: ['', Validators.required],
        eventIdCode: [''],
        eventTypeCode: [''],
        id: [''],
        min_account_balance: ['', Validators.required],
        no_of_withdrawals: ['', Validators.required],
        withdrawalCharge: ['', Validators.required],
        withdrawalsAllowed: ['', Validators.required]
      });
      this.btnColor = 'primary';
      this.btnText = 'SUBMIT';
    } else if (this.fmData.function_type == 'INQUIRE') {
      this.getData();
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
      this.btnColor = 'primary';
      this.btnText = 'VERIFFY';
    }
    else if (this.fmData.function_type == 'DELETE') {
      this.getData();
      this.getFunctions();
      this.btnColor = 'accent';
      this.btnText = 'DELETE';
    }
  }
  getFunctions() {
    this.showautoAddedAccountsForm = false;
    this.showglsubheadsForm = false;
    this.showButtons = false
    this.showSearch = false;
    this.onShowDate = false;
    this.onShowResults = true;
    this.onShowWarning = false;
    this.disableActions = true;
    this.formData.disable();
    this.sbaDetailsForm.disable()
  }
  getData() {
    this.loading = true;
    this.params = new HttpParams()
      .set('productCode', this.fmData.productCode);
    this.productsAPI.odaCode(this.params).pipe(takeUntil(this.destroy$)).subscribe(
      {
        next: (
          (res) => {
            if (res.statusCode === 200) {
              this.loading = false;
              this.results = res.entity;
              this.sbaDetails = this.results.sbaDetails;
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
                acid_generation: [this.results.acid_generation],
                acid_structure: [this.results.acid_structure],
                advance_int_ac: [this.results.advance_int_ac],
                autoAddedAccounts: [[]],
                collectLedgerFee: [this.results.collectLedgerFee],
                effective_from_date: [this.results.effective_from_date],
                effective_to_date: [this.results.effective_to_date],
                id: [this.results.id],
                glsubheads: [[]],
                increment: [this.results.increment],
                int_receivable_ac: [this.results.int_receivable_ac],
                interest_table_code: [this.results.interest_table_code],
                ledgerFeeEventIdCode: [this.results.ledgerFeeEventIdCode],
                penal_int_receivable_ac: [this.results.penal_int_receivable_ac],
                penal_receivable_ac: [this.results.penal_receivable_ac],
                pl_ac: [this.results.pl_ac],
                pl_ac_ccy: [this.results.pl_ac_ccy],
                princ_receivable_ac: [this.results.princ_receivable_ac],
                productCode: [this.results.productCode],
                productCodeDesc: [this.results.productCodeDesc],
                productType: [this.results.productType],
                running_no_size: [this.results.running_no_size],
                sbaDetails: ['']
              });
              this.sbaDetailsForm = this.fb.group({
                allow_mobile_transactions: [this.sbaDetails.allow_mobile_transactions],
                id: [this.sbaDetails.id],
                min_account_balance: [this.sbaDetails.min_account_balance],
                no_of_withdrawals: [this.sbaDetails.no_of_withdrawals],
                withdrawalCharge: [this.sbaDetails.withdrawalCharge],
                withdrawalsAllowed: [this.sbaDetails.withdrawalsAllowed]
              });
              this.results.autoAddedAccounts.forEach((element: any) => {
                this.autoAddedAccountsArray.push(element);
              });
              this.getAutoAddedAccounts();
              this.formData.patchValue({
                autoAddedAccounts: this.autoAddedAccountsArray
              });
              this.results.glsubheads.forEach((element: any) => {
                this.glsubheadsArray.push(element);
              });
              this.getglsubheads();
              this.formData.patchValue({
                glsubheads: this.glsubheadsArray
              });
            } else {
              this.loading = false;
              this.notificationAPI.alertWarning("Savings Product Details Not Available!!");
              this.router.navigate(['/system/product/savings/maintenance'], { skipLocationChange: true });
            }
          }
        ),
        error: (
          (err) => {
            this.loading = false;
            this.notificationAPI.alertWarning("Server Error: !!");
            this.router.navigate(['/system/product/savings/maintenance'], { skipLocationChange: true });
          }
        ),
        complete: (
          () => {

          }
        )
      }
    )
  }

  onSubmit() {
    this.submitted = true;
    this.submittedSba = true;
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
    this.formData.controls.sbaDetails.setValue(this.sbaDetailsForm.value);
    if (this.fmData.function_type == 'ADD') {
      if (this.formData.valid) {
        this.productsAPI.addSba(this.formData.value).pipe(takeUntil(this.destroy$)).subscribe(
          {
            next: (
              (res) => {
                if (res.statusCode === 201) {
                  this.loading = false;
                  this.notificationAPI.alertSuccess(res.message);
                  this.router.navigate(['/system/product/savings/maintenance'], { skipLocationChange: true });
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
        this.notificationAPI.alertWarning("SAVINGS PRODUCT FORM DATA IS INVALID!!");
      }
    }
    else if (this.fmData.function_type == 'MODIFY') {
      if (this.formData.valid) {
        this.productsAPI.modifySba(this.formData.value).pipe(takeUntil(this.destroy$)).subscribe(
          {
            next: (
              (res) => {
                if (res.statusCode === 200) {
                  this.loading = false;
                  this.notificationAPI.alertSuccess(res.message);
                  this.router.navigate(['/system/product/savings/maintenance'], { skipLocationChange: true });
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
        this.notificationAPI.alertWarning("SAVINGS PRODUCT FORM DATA IS INVALID!!");
      }
    }
    else if (this.fmData.function_type == 'VERIFY') {
      this.productsAPI.verify(this.results.productCode).pipe(takeUntil(this.destroy$)).subscribe(
        {
          next: (
            (res) => {
              if (res.statusCode === 200) {
                this.loading = false;
                this.notificationAPI.alertSuccess(res.message);
                this.router.navigate(['/system/product/savings/maintenance'], { skipLocationChange: true });
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
      this.productsAPI.delete(this.results.productCode).pipe(takeUntil(this.destroy$)).subscribe(
        {
          next: (
            (res) => {
              if (res.statusCode === 200) {
                this.loading = false;
                this.notificationAPI.alertSuccess(res.message);
                this.router.navigate(['/system/product/savings/maintenance'], { skipLocationChange: true });
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
