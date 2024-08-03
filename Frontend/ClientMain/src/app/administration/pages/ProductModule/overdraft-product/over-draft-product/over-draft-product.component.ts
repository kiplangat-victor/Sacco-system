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
  selector: 'app-over-draft-product',
  templateUrl: './over-draft-product.component.html',
  styleUrls: ['./over-draft-product.component.scss']
})
export class OverDraftProductComponent implements OnInit, OnDestroy {
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
  index1: number;
  glsubheadsForm!: FormGroup;
  glsubheadsArray: any[] = [];
  editButton: boolean = false;
  addButton: boolean = true;
  disableAddButton: boolean = false;
  disableActions: boolean = false;
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
  showAcidStructure: boolean = false;
  onShowDate: boolean = true;
  submitted: boolean = false;
  submittedOda: boolean = false;
  onShowWarning: boolean = true;
  glSubheadData: any;
  lookupdata: any;
  results: any;
  odaDetails: any;
  params: any;
  showglsubheadsForm: boolean = true;
  showledgerFeeEventIdCode: boolean = false;
  runningNo: any;
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
    odaDetails: ['']
  });
  odaDetailsForm: FormGroup = this.fb.group({
    ac_statement_charged_by: [''],
    ac_stmt_chrg_fixed_amt: [''],
    ac_stmt_chrg_per_page: [''],
    crLimit: [''],
    dbLimit: [''],
    dormant_ac_abnormal_trans_limit: [''],
    dormant_fee: [''],
    dr_bal_limit: [''],
    duration_from_inactive_to_dormant: [''],
    duration_to_mark_ac_inactive: [''],
    id: [''],
    inactive_ac_abnormal_trans_limit: [''],
    max_penal_int: [''],
    max_sanction_limit: [''],
    norm_int_product_method: [''],
    penal_int_rate_method: [''],
  });
  get f() {
    return this.formData.controls;
  }
  get oda() {
    return this.odaDetailsForm.controls;
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
  editglsubheads(data1: any) {
    this.editButton = true;
    this.addButton = false;
    this.submitted = false;
    this.submittedOda = false;
    this.index1 = this.glsubheadsArray.indexOf(data1);
    this.glsubheadsForm.patchValue({
      gl_code: data1.gl_code,
      gl_subhead: data1.gl_subhead,
      gl_subhead_deafault: data1.gl_subhead_deafault,
      gl_subhead_description: data1.gl_subhead_description,
      id: data1.id
    });
  }
  updateglsubheads() {
    this.editButton = false;
    this.addButton = true;
    this.glsubheadsArray[this.index1] = this.glsubheadsForm.value;
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
  disableControls() {
    this.formData.disable();
    this.odaDetailsForm.disable();
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
        odaDetails: ['']
      });
      this.odaDetailsForm = this.fb.group({
        ac_statement_charged_by: [''],
        ac_stmt_chrg_fixed_amt: [''],
        ac_stmt_chrg_per_page: [''],
        crLimit: [''],
        dbLimit: [''],
        dormant_ac_abnormal_trans_limit: [''],
        dormant_fee: [''],
        dr_bal_limit: [''],
        duration_from_inactive_to_dormant: [''],
        duration_to_mark_ac_inactive: [''],
        id: [''],
        inactive_ac_abnormal_trans_limit: [''],
        max_penal_int: [''],
        max_sanction_limit: [''],
        norm_int_product_method: [''],
        penal_int_rate_method: [''],
      });
      this.btnColor = 'primary';
      this.btnText = 'SUBMIT';
    } else if (this.fmData.function_type == 'INQUIRE') {
      this.getData();
      this.getFunctions();
      this.disableControls();

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
    this.showglsubheadsForm = false;
    this.showButtons = false
    this.showSearch = false;
    this.onShowDate = false;
    this.onShowResults = true;
    this.onShowWarning = false;
    this.disableActions = true;
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
              this.odaDetails = this.results.odaDetails;
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
                entityId: [this.results.entityId],
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
                odaDetails: ['']
              });
              this.odaDetailsForm = this.fb.group({
                ac_statement_charged_by: [this.odaDetails.ac_statement_charged_by],
                ac_stmt_chrg_fixed_amt: [this.odaDetails.ac_stmt_chrg_fixed_amt],
                ac_stmt_chrg_per_page: [this.odaDetails.ac_stmt_chrg_per_page],
                crLimit: [this.odaDetails.crLimit],
                dbLimit: [this.odaDetails.dbLimit],
                dormant_ac_abnormal_trans_limit: [this.odaDetails.dormant_ac_abnormal_trans_limit],
                dormant_fee: [this.odaDetails.dormant_fee],
                dr_bal_limit: [this.odaDetails.dr_bal_limit],
                duration_from_inactive_to_dormant: [this.odaDetails.duration_from_inactive_to_dormant],
                duration_to_mark_ac_inactive: [this.odaDetails.duration_to_mark_ac_inactive],
                id: [this.odaDetails.id],
                inactive_ac_abnormal_trans_limit: [this.odaDetails.inactive_ac_abnormal_trans_limit],
                max_penal_int: [this.odaDetails.max_penal_int],
                max_sanction_limit: [this.odaDetails.max_sanction_limit],
                norm_int_product_method: [this.odaDetails.norm_int_product_method],
                penal_int_rate_method: [this.odaDetails.penal_int_rate_method],
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
              this.notificationAPI.alertWarning("Over-DrAft Product Details Not Available !!");
              this.router.navigate(['/system/product/overdrafts/maintenance'], { skipLocationChange: true });
            }
          }
        ),
        error: (
          (err) => {
            this.loading = false;
            this.notificationAPI.alertWarning("Server Error: !!");
            this.router.navigate(['/system/product/overdrafts/maintenance'], { skipLocationChange: true });
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
    this.submittedOda = true;
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
    this.formData.controls.odaDetails.setValue(this.odaDetailsForm.value);
    if (this.fmData.function_type == 'ADD') {
      if (this.formData.valid) {
        this.productsAPI.addOda(this.formData.value).pipe(takeUntil(this.destroy$)).subscribe(
          {
            next: (
              (res) => {
                if (res.statusCode === 201) {
                  this.loading = false;
                  this.notificationAPI.alertSuccess(res.message);
                  this.router.navigate(['/system/product/overdrafts/maintenance'], { skipLocationChange: true });
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
        this.notificationAPI.alertWarning("OVER-DRAFT PRODUCT FORM DATA IS INVALID!!");
      }
    }
    else if (this.fmData.function_type == 'MODIFY') {
      if (this.formData.valid) {
        this.formData.controls.odaDetails.setValue(this.odaDetailsForm.value);
        this.productsAPI.modifyOda(this.formData.value).pipe(takeUntil(this.destroy$)).subscribe(
          {
            next: (
              (res) => {
                if (res.statusCode === 200) {
                  this.loading = false;
                  this.notificationAPI.alertSuccess(res.message);
                  this.router.navigate(['/system/product/overdrafts/maintenance'], { skipLocationChange: true });
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
        this.notificationAPI.alertWarning("OVER-DRAFT PRODUCT FORM DATA IS INVALID!!");
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
                this.router.navigate(['/system/product/overdrafts/maintenance'], { skipLocationChange: true });
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
                this.router.navigate(['/system/product/overdrafts/maintenance'], { skipLocationChange: true });
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
