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
import { ExceptionsCodesLookupComponent } from '../../../SystemConfigurations/GlobalParams/exceptions-codes/exceptions-codes-lookup/exceptions-codes-lookup.component';
import { GlSubheadLookupComponent } from '../../../SystemConfigurations/GlobalParams/gl-subhead/gl-subhead-lookup/gl-subhead-lookup.component';
import { InterestcodelookupComponent } from '../../../SystemConfigurations/interestcode/interestcodelookup/interestcodelookup.component';
import { LoansProductLookUpComponent } from '../loans-product-look-up/loans-product-look-up.component';
import { DatePipe } from '@angular/common';

@Component({
  selector: 'app-loans-product',
  templateUrl: './loans-product.component.html',
  styleUrls: ['./loans-product.component.scss']
})
export class LoansProductComponent implements OnInit, OnDestroy {
  exceptionsColumns: string[] = [
    "index",
    "exception_code",
    'exception_description',
    "actions",
  ];
  feesColumns: string[] = [
    "index",
    "eventIdCode",
    'eventTypeCode',
    "actions",
  ];
  glsubheadsColumns: string[] = [
    "index",
    "gl_code",
    'gl_subhead',
    'gl_subhead_description',
    "actions",
  ];
  loanLimitColumns: string[] = [
    'index',
    'conditionType',
    'accMultiplier',
    'activeMonths',
    'historyMultiplier',
    'privTranPeriodMonth',
    'productCode',
    'actions',
  ];
  exceptionsDataSource: MatTableDataSource<any>;
  @ViewChild("exceptionsPaginator") exceptionsPaginator!: MatPaginator;
  @ViewChild(MatSort) exceptionsSort!: MatSort;
  feesDataSource: MatTableDataSource<any>;
  @ViewChild("feesPaginator") feesPaginator!: MatPaginator;
  @ViewChild(MatSort) feesSort!: MatSort;
  glsubheadsDataSource: MatTableDataSource<any>;
  @ViewChild("glsubheadsPaginator") glsubheadsPaginator!: MatPaginator;
  @ViewChild(MatSort) glsubheadsSort!: MatSort;
  loanLimitDataSource: MatTableDataSource<any>;
  @ViewChild("loanLimitPaginator") loanLimitPaginator!: MatPaginator;
  @ViewChild(MatSort) loanLimitSort!: MatSort;

  index: number;
  exceptionsForm!: FormGroup;
  exceptionsArray: any[] = [];
  feesForm!: FormGroup;
  feesArray: any[] = [];
  glsubheadsForm!: FormGroup;
  glsubheadsArray: any[] = [];
  loanLimitsByProductForm!: FormGroup;
  loanLimitsByProductArray: any[] = [];
  editButton: boolean = false;
  addButton: boolean = true;
  disableAddButton: boolean = false;
  disableActions: boolean = false;
  submittedLaa: boolean = false;
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
  showfeesForm: boolean = true;
  showexceptionsForm: boolean = true;
  showglsubheadsForm: boolean = true;
  laaDetails: any;
  showAccMultiplier: boolean = true;
  onshowLimitProductCode: boolean = true;
  showActiveMonths: boolean = true;
  showHistoryMultiplier: boolean = true;
  showLoanLimitsByProductForm: boolean = true;
  showledgerFeeEventIdCode: boolean = false;
  loanLimitsByProduct: any;
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
    this.runningNo = this.productCode + Math.floor(Math.random() * (10000));
    this.initialisefeesForm();
    this.initialiseexceptionsForm();
    this.initialiseloanLimitsByProduct();
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
    exceptions: [[]],
    fees: [[]],
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
    laaDetails: ['']
  });

  laaDetailsForm: FormGroup = this.fb.group({
    id: [''],
    loan_amt_min: ['', Validators.required],
    loan_max_amt: ['', Validators.required],
    loan_period_min_mm: ['', Validators.required],
    loan_period_max_mm: ['', Validators.required],
    grace_period_for_late_fee_mm: ['', Validators.required],
    apply_late_fee_for_delayed_payment: ['', Validators.required],
    loanLimitsByProduct: [[]],
    maxGuarantorCount: ['', Validators.required],
    minGuarantorCount: ['', Validators.required],
    penal_int: ['', Validators.required]
  });
  get f() {
    return this.formData.controls;
  }
  get laa() {
    return this.laaDetailsForm.controls;
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
  initialiseexceptionsForm() {
    this.initexceptionsForm();
    this.exceptionsForm.controls.exception_code.setValidators([Validators.required]);
    this.exceptionsForm.controls.exception_description.setValidators([]);
    this.exceptionsForm.controls.id.setValidators([]);
  }
  initexceptionsForm() {
    this.exceptionsForm = this.fb.group({
      exception_code: [''],
      exception_description: [''],
      id: ['']
    })
  }
  addexceptions() {
    if (this.exceptionsForm.valid) {
      this.exceptionsArray.push(this.exceptionsForm.value);
      this.resetexceptionsForm();
    }
  }
  getexceptions() {
    this.exceptionsDataSource = new MatTableDataSource(this.exceptionsArray);
    this.exceptionsDataSource.paginator = this.exceptionsPaginator;
    this.exceptionsDataSource.sort = this.exceptionsSort;
  }
  resetexceptionsForm() {
    this.formData.patchValue({
      exceptions: this.exceptionsArray,
    });
    this.getexceptions();
    this.initexceptionsForm();

  }
  editexceptions(data: any) {
    this.index = this.exceptionsArray.indexOf(data);
    this.editButton = true;
    this.addButton = false;
    this.exceptionsForm.patchValue({
      exception_code: data.exception_code,
      exception_description: data.exception_description,
      id: data.id
    });
  }
  updateexceptions() {
    this.editButton = false;
    this.addButton = true;
    this.exceptionsArray[this.index] = this.exceptionsForm.value;
    this.formData.patchValue({
      exceptions: this.exceptionsArray
    });
    this.resetexceptionsForm();
  }
  deleteexceptions(data: any) {
    let deleteIndex = this.exceptionsArray.indexOf(data);
    this.exceptionsArray.splice(deleteIndex, 1);
    this.resetexceptionsForm();
  }
  applyexceptionsFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.exceptionsDataSource.filter = filterValue.trim().toLowerCase();
    if (this.exceptionsDataSource.paginator) {
      this.exceptionsDataSource.paginator.firstPage();
    }
  }
  exceptionLookup(): void {
    const dialogRef = this.dialog.open(ExceptionsCodesLookupComponent, {
      width: '35%',
      autoFocus: false,
      maxHeight: '90vh'
    });
    dialogRef.afterClosed().subscribe(result => {
      this.lookupdata = result.data;
      this.exceptionsForm.controls.exception_code.setValue(
        this.lookupdata.exceptionCode
      );
      this.exceptionsForm.controls.exception_description.setValue(
        this.lookupdata.exce_description
      );
    });
  }
  initialisefeesForm() {
    this.initfeesForm();
    this.feesForm.controls.eventIdCode.setValidators([Validators.required]);
    this.feesForm.controls.eventTypeCode.setValidators([Validators.required]);
    this.feesForm.controls.id.setValidators([]);
  }
  initfeesForm() {
    this.feesForm = this.fb.group({
      eventIdCode: [''],
      eventTypeCode: [''],
      id: ['']
    })
  }
  addfees() {
    if (this.feesForm.valid) {
      this.feesArray.push(this.feesForm.value);
      this.resetfeesForm();
    }
  }
  getfees() {
    this.feesDataSource = new MatTableDataSource(this.feesArray);
    this.feesDataSource.paginator = this.feesPaginator;
    this.feesDataSource.sort = this.feesSort;
  }
  resetfeesForm() {
    this.formData.patchValue({
      fees: this.feesArray,
    });
    this.getfees();
    this.initfeesForm();

  }
  editfees(data: any) {
    this.index = this.feesArray.indexOf(data);
    this.editButton = true;
    this.addButton = false;
    this.feesForm.patchValue({
      eventIdCode: data.eventIdCode,
      eventTypeCode: data.eventTypeCode,
      id: data.id
    });
  }
  updatefees() {
    this.editButton = false;
    this.addButton = true;
    this.feesArray[this.index] = this.feesForm.value;
    this.formData.patchValue({
      fees: this.feesArray
    });
    this.resetfeesForm();
  }
  deletefees(data: any) {
    let deleteIndex = this.feesArray.indexOf(data);
    this.feesArray.splice(deleteIndex, 1);
    this.resetfeesForm();
  }
  applyfeesFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.feesDataSource.filter = filterValue.trim().toLowerCase();
    if (this.feesDataSource.paginator) {
      this.feesDataSource.paginator.firstPage();
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
  conditionTypeChange(event: MatRadioChange) {
    if (event.value == 'accMultiplier') {
      this.showAccMultiplier = true;
      this.onshowLimitProductCode = true;
      this.showActiveMonths = false;
      this.showHistoryMultiplier = false;
    }
    else if (event.value == 'activeMonths') {
      this.showAccMultiplier = false;
      this.onshowLimitProductCode = false;
      this.showActiveMonths = true;
      this.showHistoryMultiplier = false;
    }
    else if (event.value == 'historyMultiplier') {
      this.showAccMultiplier = false;
      this.onshowLimitProductCode = false;
      this.showActiveMonths = false;
      this.showHistoryMultiplier = true;
    }
  }

  initloanLimitsByProduct() {
    this.loanLimitsByProductForm = this.fb.group({
      id: [''],
      accMultiplier: [''],
      activeMonths: [''],
      conditionType: [''],
      historyMultiplier: [''],
      limitStatementInWords: [''],
      privTranPeriodMonth: [''],
      productCode: ['']
    });
  }
  initialiseloanLimitsByProduct() {
    this.initloanLimitsByProduct();
    this.loanLimitsByProductForm.controls.conditionType.setValidators([Validators.required]);
    this.loanLimitsByProductForm.controls.limitStatementInWords.setValidators([Validators.required]);
  }
  onAddLoanLimit() {
    if (this.loanLimitsByProductForm.valid) {
      this.loanLimitsByProductArray.push(this.loanLimitsByProductForm.value);
      this.resetloanLimitsByProduct();
    }
  }
  getLoanLimits() {
    this.loanLimitDataSource = new MatTableDataSource(this.loanLimitsByProductArray);
    this.loanLimitDataSource.paginator = this.loanLimitPaginator;
    this.loanLimitDataSource.sort = this.loanLimitSort;
  }

  resetloanLimitsByProduct() {
    this.laaDetailsForm.patchValue({
      loanLimitsByProduct: this.loanLimitsByProductArray,
    });
    this.getLoanLimits();
    this.initialiseloanLimitsByProduct();
  }
  editLoanItem(data: any) {
    this.index = this.loanLimitsByProductArray.indexOf(data);
    this.editButton = true;
    this.addButton = false;
    this.loanLimitsByProductForm.patchValue({
      id: [data.id],
      accMultiplier: [data.accMultiplier],
      activeMonths: [data.activeMonths],
      conditionType: [data.conditionType],
      historyMultiplier: [data.historyMultiplier],
      limitStatementInWords: [data.limitStatementInWords],
      privTranPeriodMonth: [data.privTranPeriodMonth],
      productCode: [data.productCode]
    });
  }
  onUpdateLoanLimit() {
    this.editButton = false;
    this.addButton = true;
    this.loanLimitsByProductArray[this.index] = this.loanLimitsByProductForm.value;
    this.loanLimitsByProductForm.patchValue({
      loanLimitsByProduct: this.loanLimitsByProductArray,
    });
    this.resetloanLimitsByProduct();
  }
  deleteLoanItem(data: any) {
    let deleteIndex = this.loanLimitsByProductArray.indexOf(data);
    this.loanLimitsByProductArray.splice(deleteIndex, 1);
    this.resetloanLimitsByProduct();
  }
  loanLimitFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.loanLimitDataSource.filter = filterValue.trim().toLowerCase();
    if (this.loanLimitDataSource.paginator) {
      this.loanLimitDataSource.paginator.firstPage();
    }
  }
  eventIdCodeLookup(): void {
    const dialogRef = this.dialog.open(EventIdLookupComponent, {
      width: '40%'
    });
    dialogRef.afterClosed().subscribe(result => {
      this.lookupdata = result.data;
      this.feesForm.controls.eventIdCode.setValue(this.lookupdata.eventIdCode);
      this.feesForm.controls.eventTypeCode.setValue(this.lookupdata.eventTypeCode);
    });
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

  LoanProductCodeLookUp(): void {
    const dialogRef = this.dialog.open(LoansProductLookUpComponent, {
      width: '40%',
      autoFocus: false,
      maxHeight: '90vh'
    });
    dialogRef.afterClosed().subscribe(result => {
      this.lookupdata = result.data;
      this.loanLimitsByProductForm.controls.productCode.setValue(this.lookupdata.productCode);
    });
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
        exceptions: [[]],
        fees: [[]],
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
        laaDetails: ['']
      });

      this.laaDetailsForm = this.fb.group({
        id: [''],
        loan_amt_min: ['', Validators.required],
        loan_max_amt: ['', Validators.required],
        individualLimits: ['', Validators.required],
        individualLimitsProduct: ['', Validators.required],
        loan_period_min_mm: ['', Validators.required],
        loan_period_max_mm: ['', Validators.required],
        grace_period_for_late_fee_mm: ['', Validators.required],
        apply_late_fee_for_delayed_payment: ['', Validators.required],
        loanLimitsByProduct: [[]],
        maxGuarantorCount: ['', Validators.required],
        minGuarantorCount: ['', Validators.required],
        penal_int: ['', Validators.required]
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
    this.showexceptionsForm = false;
    this.showglsubheadsForm = false;
    this.showfeesForm = false
    this.showLoanLimitsByProductForm = false;
    this.showButtons = false
    this.showSearch = false;
    this.onShowDate = false;
    this.onShowResults = true;
    this.onShowWarning = false;
    this.disableActions = true;
    this.formData.disable();
    this.laaDetailsForm.disable()
  }
  getData() {
    this.loading = true;
    this.params = new HttpParams()
      .set('productCode', this.fmData.productCode);
    this.productsAPI.laaCode(this.params).pipe(takeUntil(this.destroy$)).subscribe(
      {
        next: (
          (res) => {
            if (res.statusCode === 200) {
              this.loading = false;
              this.results = res.entity;
              this.laaDetails = this.results.laaDetails;
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
              this.loanLimitsByProduct = this.laaDetails.loanLimitsByProduct;
              this.formData = this.fb.group({
                acid_generation: [this.results.acid_generation],
                acid_structure: [this.results.acid_structure],
                advance_int_ac: [this.results.advance_int_ac],
                collectLedgerFee: [this.results.collectLedgerFee],
                effective_from_date: [this.results.effective_from_date],
                effective_to_date: [this.results.effective_to_date],
                id: [this.results.id],
                exceptions: [[]],
                fees: [[]],
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
                laaDetails: ['']
              });
              this.laaDetailsForm = this.fb.group({
                id: [this.laaDetails.id],
                loan_amt_min: [this.laaDetails.loan_amt_min],
                loan_max_amt: [this.laaDetails.loan_max_amt],
                individualLimits: [this.laaDetails.individualLimits],
                individualLimitsProduct: [this.laaDetails.individualLimitsProduct],
                loan_period_min_mm: [this.laaDetails.loan_period_min_mm],
                loan_period_max_mm: [this.laaDetails.loan_period_max_mm],
                grace_period_for_late_fee_mm: [this.laaDetails.grace_period_for_late_fee_mm],
                apply_late_fee_for_delayed_payment: [this.laaDetails.apply_late_fee_for_delayed_payment],
                loanLimitsByProduct: [[]],
                maxGuarantorCount: [this.laaDetails.maxGuarantorCount],
                minGuarantorCount: [this.laaDetails.minGuarantorCount],
                penal_int: [this.laaDetails.penal_int]
              });
              this.results.exceptions.forEach((element: any) => {
                this.exceptionsArray.push(element);
              });
              this.getexceptions();
              this.formData.patchValue({
                exceptions: this.exceptionsArray
              });
              this.results.fees.forEach((element: any) => {
                this.feesArray.push(element);
              })
              this.getfees();
              this.formData.patchValue({
                fees: this.feesArray
              })
              this.results.glsubheads.forEach((element: any) => {
                this.glsubheadsArray.push(element);
              });
              this.getglsubheads();
              this.formData.patchValue({
                glsubheads: this.glsubheadsArray
              });
            } else {
              this.loading = false;
              this.notificationAPI.alertWarning("Loan Product Details Not Available !!");
              this.router.navigate(['/system/product/loans/maintenance'], { skipLocationChange: true });
            }
          }
        ),
        error: (
          (err) => {
            this.loading = false;
            this.notificationAPI.alertWarning("Server Error: !!");
            this.router.navigate(['/system/product/loans/maintenance'], { skipLocationChange: true });
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
    this.loading = true;
    this.submitted = true;
    this.submittedLaa = true;
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
    this.formData.controls.laaDetails.setValue(this.laaDetailsForm.value);
    if (this.fmData.function_type == 'ADD') {
      if (this.formData.valid) {
        this.productsAPI.addLaa(this.formData.value).pipe(takeUntil(this.destroy$)).subscribe(
          {
            next: (
              (res) => {
                if (res.statusCode === 201) {
                  this.loading = false;
                  this.notificationAPI.alertSuccess(res.message);
                  this.router.navigate(['/system/product/loans/maintenance'], { skipLocationChange: true });
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
        this.notificationAPI.alertWarning("LOAN PRODUCT FORM DATA IS INVALID!!");
      }
    }
    else if (this.fmData.function_type == 'MODIFY') {
      if (this.formData.valid) {
        this.productsAPI.modifyLaa(this.formData.value).pipe(takeUntil(this.destroy$)).subscribe(
          {
            next: (
              (res) => {
                if (res.statusCode === 200) {
                  this.loading = false;
                  this.notificationAPI.alertSuccess(res.message);
                  this.router.navigate(['/system/product/loans/maintenance'], { skipLocationChange: true });
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
        this.notificationAPI.alertWarning("LOAN PRODUCT FORM DATA IS INVALID!!");
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
                this.router.navigate(['/system/product/loans/maintenance'], { skipLocationChange: true });
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
                this.router.navigate(['/system/product/loans/maintenance'], { skipLocationChange: true });
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
