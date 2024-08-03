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
import { GlSubheadLookupComponent } from '../../../SystemConfigurations/GlobalParams/gl-subhead/gl-subhead-lookup/gl-subhead-lookup.component';
import { InterestcodelookupComponent } from '../../../SystemConfigurations/interestcode/interestcodelookup/interestcodelookup.component';
import { EventIdLookupComponent } from '../../../SystemConfigurations/ChargesParams/event-id/event-id-lookup/event-id-lookup.component';
import { DatePipe } from '@angular/common';

@Component({
  selector: 'app-term-deposit-product',
  templateUrl: './term-deposit-product.component.html',
  styleUrls: ['./term-deposit-product.component.scss']
})
export class TermDepositProductComponent implements OnInit, OnDestroy {
  glsubheadsColumns: string[] = [
    "index",
    "gl_code",
    'gl_subhead',
    'gl_subhead_description',
    "actions",
  ];
  glsubheadsDataSource: MatTableDataSource<any>;
  @ViewChild("glsubheadsPaginator") glsubheadsPaginator!: MatPaginator;
  @ViewChild(MatSort) glsubheadsSort!: MatSort;
  index: number;
  glsubheadsForm!: FormGroup;
  glsubheadsArray: any[] = [];
  editButton: boolean = false;
  addButton: boolean = true;
  disableAddButton: boolean = false;
  disableActions: boolean = false;
  submittedTda: boolean = false;
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
  interestRate: number = 0;
  results: any;
  params: any;
  tdaDetails: any;
  showglsubheadsForm: boolean = true;
  showledgerFeeEventIdCode: boolean = false;
  runningNo: string;
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
    this.runningNo = this.productCode + Math.floor(Math.random() * (100));
    this.initialiseglsubheadsForm();
  }
  formData: FormGroup = this.fb.group({
    acid_generation: ['', Validators.required],
    acid_structure: [''],
    advance_int_ac: ['', Validators.required],
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
    taxCollectionAccount: ['', Validators.required],
    tdaDetails: ['']
  });

  tdaDetailsForm: FormGroup = this.fb.group({
    id: [''],
    tda_deposit_amt_max: ['', Validators.required],
    tda_deposit_amt_min: ['', Validators.required],
    tda_period_dd_max: ['', Validators.required],
    tda_period_dd_min: ['', Validators.required],
    tda_period_mm_max: ['', Validators.required],
    tda_period_mm_min: ['', Validators.required],
    interestRate: ['', Validators.required],
  });
  get f() {
    return this.formData.controls;
  }
  get tda() {
    return this.tdaDetailsForm.controls;
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
  taxAccountLookup(): void {
    const dialogRef = this.dialog.open(OfficeAccountsLookUpsComponent, {
      width: '45%',
      autoFocus: false,
      maxHeight: '90vh',
    });
    dialogRef.afterClosed().subscribe(result => {
      this.lookupdata = result.data;
      this.formData.controls.taxCollectionAccount.setValue(this.lookupdata.acid);
    });
  }

  getPage() {
    if (this.fmData.function_type == 'ADD') {
      this.formData = this.fb.group({
        acid_generation: ['', Validators.required],
        acid_structure: [''],
        advance_int_ac: ['', Validators.required],
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
        taxCollectionAccount: ['', Validators.required],
        tdaDetails: ['']
      });

      this.tdaDetailsForm = this.fb.group({
        id: [''],
        tda_deposit_amt_max: ['', Validators.required],
        tda_deposit_amt_min: ['', Validators.required],
        tda_period_dd_max: ['', Validators.required],
        tda_period_dd_min: ['', Validators.required],
        tda_period_mm_max: ['', Validators.required],
        tda_period_mm_min: ['', Validators.required],
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
    this.showglsubheadsForm = false;
    this.showButtons = false
    this.showSearch = false;
    this.onShowDate = false;
    this.onShowResults = true;
    this.onShowWarning = false;
    this.disableActions = true;
    this.formData.disable();
    this.tdaDetailsForm.disable();
  }
  getData() {
    this.loading = true;
    this.params = new HttpParams()
      .set('productCode', this.fmData.productCode);
    this.productsAPI.tdaCode(this.params).pipe(takeUntil(this.destroy$)).subscribe(
      {
        next: (
          (res) => {
            if (res.statusCode === 200) {
              this.loading = false;
              this.results = res.entity;
              this.tdaDetails = this.results.tdaDetails;
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
                taxCollectionAccount: [this.results.taxCollectionAccount],
                tdaDetails: ['']
              });
              this.tdaDetailsForm = this.fb.group({
                id: [this.tdaDetails.id],
                tda_deposit_amt_max: [this.tdaDetails.tda_deposit_amt_max],
                tda_deposit_amt_min: [this.tdaDetails.tda_deposit_amt_min],
                tda_period_dd_max: [this.tdaDetails.tda_period_dd_max],
                tda_period_dd_min: [this.tdaDetails.tda_period_dd_min],
                tda_period_mm_max: [this.tdaDetails.tda_period_mm_max],
                tda_period_mm_min: [this.tdaDetails.tda_period_mm_min],
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
              this.notificationAPI.alertWarning("Term Deposit Product Details Not Available !!");
              this.router.navigate(['/system/product/term-deposit/maintenance'], { skipLocationChange: true });
            }
          }
        ),
        error: (
          (err) => {
            this.loading = false;
            this.notificationAPI.alertWarning("Server Error: !!");
            this.router.navigate(['/system/product/term-deposit/maintenance'], { skipLocationChange: true });
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
    this.submittedTda = true;
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
    this.formData.controls.tdaDetails.setValue(this.tdaDetailsForm.value);
    if (this.fmData.function_type == 'ADD') {
      if (this.formData.valid) {
        this.productsAPI.addTda(this.formData.value).pipe(takeUntil(this.destroy$)).subscribe(
          {
            next: (
              (res) => {
                if (res.statusCode === 201) {
                  this.loading = false;
                  this.notificationAPI.alertSuccess(res.message);
                  this.router.navigate(['/system/product/term-deposit/maintenance'], { skipLocationChange: true });
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
        this.notificationAPI.alertWarning("TERM DEPOSIT PRODUCT FORM DATA IS INVALID!!");
      }
    }
    else if (this.fmData.function_type == 'MODIFY') {
      if (this.formData.valid) {
        this.productsAPI.modifyTda(this.formData.value).pipe(takeUntil(this.destroy$)).subscribe(
          {
            next: (
              (res) => {
                if (res.statusCode === 200) {
                  this.loading = false;
                  this.notificationAPI.alertSuccess(res.message);
                  this.router.navigate(['/system/product/term-deposit/maintenance'], { skipLocationChange: true });
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
        this.notificationAPI.alertWarning("TERM DEPOSIT PRODUCT FORM DATA IS INVALID!!");
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
                this.router.navigate(['/system/product/term-deposit/maintenance'], { skipLocationChange: true });
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
                this.router.navigate(['/system/product/term-deposit/maintenance'], { skipLocationChange: true });
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
