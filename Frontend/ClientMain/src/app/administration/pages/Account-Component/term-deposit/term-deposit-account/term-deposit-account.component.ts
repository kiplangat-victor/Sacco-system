import { DatePipe } from '@angular/common';
import { HttpParams } from '@angular/common/http';
import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatCheckboxChange } from '@angular/material/checkbox';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { Router } from '@angular/router';
import html2canvas from 'html2canvas';
import jspdf from 'jspdf';
import { Subject, Subscription } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';
import { AccountsNotificationService } from 'src/@core/helpers/accounts/accounts-notification.service';
import { occupations } from 'src/@core/helpers/occupation';
import { AccountsService } from 'src/app/administration/Service/AccountsService/accounts/accounts.service';
import { ProductService } from 'src/app/administration/Service/product-maintainance/product.service';
import { AccountStatementService } from 'src/app/administration/Service/transaction/account-statement.service';
import { environment } from 'src/environments/environment';
import { GroupMembershipDetailsComponent } from '../../../MembershipComponent/GroupMembership/group-membership-details/group-membership-details.component';
// import { MemberDetailsComponent } from '../../../MembershipComponent/Membership/member-details/member-details.component';
import { NewMembershipComponent } from '../../../MembershipComponent/Membership/new-membership/new-membership.component';
import { UniversalMembershipLookUpComponent } from '../../../MembershipComponent/universal-membership-look-up/universal-membership-look-up.component';
import { TermDepositLookupComponent } from '../../../ProductModule/term-deposit/term-deposit-lookup/term-deposit-lookup.component';
import { MisSectorService } from '../../../SystemConfigurations/GlobalParams/mis-sector/mis-sector.service';
import { ReportService } from '../../../reports/report.service';
import { AccountUsersComponent } from '../../account-users/account-users.component';
import { relationType } from '../../savings-account/relation-types';
import { SavingsLookupComponent } from '../../savings-account/savings-lookup/savings-lookup.component';
import { relationships } from './../../savings-account/relationship';
import { AccountsApprovalComponent } from '../../accounts-approval/accounts-approval.component';
import { CurrencyLookupComponent } from '../../../SystemConfigurations/GlobalParams/currency-config/currency-lookup/currency-lookup.component';

@Component({
  selector: 'app-term-deposit-account',
  templateUrl: './term-deposit-account.component.html',
  styleUrls: ['./term-deposit-account.component.scss']
})
export class TermDepositAccountComponent implements OnInit, OnDestroy {
  nomineesColumns: string[] = [
    "index",
    "firstName",
    "middleName",
    "lastName",
    'identificationNo',
    'dob',
    'phone',
    'emailAddress',
    'relationship',
    'occupation',
    "actions",
  ];
  relatedPartiesColumns: string[] = [
    "index",
    "relPartyCustomerCode",
    "relPartyCustomerName",
    'relPartyPostalAddress',
    'relPartyRelationType',
    "actions",
  ];
  accountDocumentsColumns: string[] = [
    "index",
    "documentTitle",
    'view',
    "actions",
  ];

  nomineesDataSource: MatTableDataSource<any>;
  @ViewChild("nomineesPaginator") nomineesPaginator!: MatPaginator;
  @ViewChild(MatSort) nomineesSort!: MatSort;

  relatedPartiesDataSource: MatTableDataSource<any>;
  @ViewChild("relatedPartiesPaginator") relatedPartiesPaginator!: MatPaginator;
  @ViewChild(MatSort) relatedPartiesSort!: MatSort;

  accountDocumentsDataSource: MatTableDataSource<any>;
  @ViewChild("accountDocumentsPaginator") accountDocumentsPaginator!: MatPaginator;
  @ViewChild(MatSort) accountDocumentsSort!: MatSort;
  index: number;
  nomineesForm!: FormGroup;
  nomineesArray: any[] = [];
  relatedPartiesForm!: FormGroup;
  relatedPartiesArray: any[] = [];
  accountDocumentsForm!: FormGroup;
  accountDocumentsArray: any[] = [];
  editButton: boolean = false;
  addButton: boolean = true;
  disableAddButton: boolean = false;
  disableActions: boolean = false;
  submittedTda: boolean = false;
  dtaParams: HttpParams;
  despatch_mode_array: any = [
    'POST', 'E-MAIL', 'COURIER', 'COLLECT IN PERSON'
  ];
  statementFreqArray: any = [
    'DAILY', 'WEEKLY', 'MONTLY', 'YEARLY'
  ];
  accountStatusArray: any = [
    'ACTIVE', 'DORMANT', 'NOT-ACTIVE'
  ];
  aaplicationStatusAr: any = [
    'PENDING', 'VERIFIED'
  ];
  relationshipArray: any = [
    'FATHER', 'MOTHER', 'WIFE', 'HUSBAND', 'SIBLING', 'DAUGHTER', 'SON'
  ];
  guardianArray: any = [
    'COURT APPOINTED', 'NATURAL GUARDIAN'
  ];
  imageTypeArray: any = [
    'PASSPORT', 'SIGNATURE'
  ]
  operationsArray: any = ['ANY TO SIGN', 'ALL TO SIGN', 'EITHER TO SIGN', 'INDIVIDUAL TO SIGN'];
  accountOwnershipArray: any = ['SINGLE-ACCOUNT', 'JOINT-ACCOUNT'];
  subSectorData: any;
  currencyData: any;
  customer_lookup: any;
  docIndex: any;
  onShowSelect = true;
  isHiddenInput = false;
  customer_type: any;
  documentCaptured: any;
  loading: boolean = false;
  lookupdata: any;
  glCode: any;
  results: any;
  relationship: any;
  relationtypes: any;
  branchCode: any;
  fmData: any;
  showAccountCode = false;
  showSearch = true;
  showCustomerType = false;
  showVerification = false;
  userName: any;
  id: any;
  deletedBy: any;
  params: any;
  acid: any;
  function_type: any;
  account_code: any;
  account_type: any;
  onShowUpdateButton = false;
  showTableOperation = true;
  showButtons = true;
  onShowAddButton = true;
  onShowNomineesForm = true;
  onShowRelatePartiesForm = true;
  termDepositDetails: any;
  onShowAccountStatement: boolean;
  occupations: any;
  mainSectorData: any;
  code: any;
  sba_startDate: any;
  onShowDatePicker = false;
  onShowOpeningDate = false;
  selectedDate = new Date();
  submitted = false;
  onShowResults = false;
  subHeadLookupdata: any;
  subHeadLookupFound: any;
  tdaWithdrawals: any;
  onShowAccountOwnership = true;
  onShowRelatedPartiesTab = false;
  onHideSpecificDetails: boolean = false;
  onShowDate = true;
  onShowWarning = true;
  onShowAccountStatus = false;
  exceptions: any;
  today = new Date();
  maturityDate = new Date(new Date().setDate(this.today.getDate() + 365));
  exceptionData: any;
  onShowAccountBalance = false;
  subSectorFound: any;
  messageRes: any;
  accountBalance: any
  document: any;
  documentElement: any;
  imageFile: any;
  imageSrc: string;
  onShowImagesForm = true;
  onShowImageDivider = false;
  onShowUploadedImages = false;
  onShowAccountImages = true;
  displayBackButton = true;
  displayApprovalBtn: boolean = false;
  imageResults: any[];
  account_statement: any;
  onShowAccountStatementTab = false;
  onShowAccountDocumentsTab = true;
  onShowNomineesTab = true;
  onShowSpecificDetailsTab = true;
  onShowGeneralDetailsTab = true;
  onShowMainMenu = true;
  fcData: any;
  onShowAccountStatementDateParams = false
  fromdate: any;
  todate: any;
  hideBtn = false;
  btnColor: any;
  btnText: any;
  destroy$: Subject<boolean> = new Subject<boolean>();
  logolink: string = `${environment.reportsAPI}/api/v1/dynamic/saccologo`;
  saccoNamelink: any;
  currentUser: any;
  customer_account_id: any;
  customer_account_code: any;
  onMemberDetailsLookup: boolean = false;
  showApprovalbtn: boolean = false;
  accountsdata: any;
  hideRejectBtn: boolean = false;
  btnRejectColor: any;
  rejectBtnText: any;
  rejectionData: any;
  dialogConfig: any;
  isEnabled = true;
  chrg_calc_crncy: any;
  constructor(
    private router: Router,
    private fb: FormBuilder,
    private dialog: MatDialog,
    private datepipe: DatePipe,
    private reportsAPI: ReportService,
    private sectorAPI: MisSectorService,
    private accountsAPI: AccountsService,
    private productService: ProductService,
    private notificationAPI: NotificationService,
    private statementAPI: AccountStatementService,
    private accountsNotification: AccountsNotificationService
  ) {
    this.currentUser = JSON.parse(localStorage.getItem('auth-user'))
    this.fmData = this.router.getCurrentNavigation().extras.queryParams.formData;
    this.function_type = this.fmData.function_type;
    this.account_code = this.fmData.account_code;
    this.account_type = this.fmData.account_type;
    if (this.fmData.backBtn == 'APPROVAL') {
      this.displayBackButton = false;
      this.displayApprovalBtn = true;
    }
  }
  ngOnDestroy(): void {
    this.destroy$.next(true);
    this.destroy$.complete();
  }
  ngOnInit(): void {
    this.getPage();
    this.getSaccoName();
    this.initialiserelatedPartiesForm();
    this.initialisenomineesForm();
    this.initialiseAccountDocumentsForm();
    this.getRelationships();
    this.getRelationTypes();
    this.getOccupations();
    this.getSectorCode();
  }
  getSaccoName() {
    this.loading = true;
    this.reportsAPI.sacconame().pipe(takeUntil(this.destroy$)).subscribe(
      {
        next: (
          (res) => {
            if (res.statusCode === 302) {
              this.loading = false;
              this.saccoNamelink = res.entity;
            } else {
              this.loading = false;
            }
          }
        ),
        error: (
          (err) => {
            this.loading = false;
          }
        ),
        complete: (
          () => {

          }
        )
      }
    )
  }
  formData: FormGroup = this.fb.group({
    id: [''],
    acid: [''],
    entityId: [''],
    customerType: [''],
    accountBalance: [''],
    accountManager: [''],
    accountName: [''],
    accountOwnership: ['SINGLE ACCOUNT'],
    checkedJointAccount: [''],
    isWithdrawalAllowed: ['TRUE'],
    operationMode: [''],
    accountStatus: [''],
    accountType: [''],
    cashExceptionCr: [''],
    cashExceptionLimitDr: [''],
    currency: ['UGX'],
    customerCode: ['', Validators.required],
    managerCode: ['', Validators.required],
    dispatchMode: [''],
    glCode: [''],
    glSubhead: ['', Validators.required],
    misSectorCode: ['', Validators.required],
    misSubSectorCode: ['', Validators.required],
    lienAmount: [''],
    openingDate: [new Date()],
    productCode: ['', Validators.required],
    solCode: [''],
    transferExceptionLimitCr: [''],
    transferExceptionLimitDr: [''],
    accountStatement: [''],
    statementFreq: [''],
    transactionPhone: [''],
    termDeposit: [''],
    accountDocuments: [[]],
    nominees: [[]],
    relatedParties: [[]],
    chrg_calc_crncy: ['', Validators.required]
  });

  termDepositForm: FormGroup = this.fb.group({
    chrg_calc_crncy: ['', Validators.required],
    accrualLastDate: [''],
    id: [''],
    interestAmount: [''],
    interestCrAccountId: [''],
    interestPreferential: ['', Validators.required],
    interestRate: [''],
    maturityDate: [new Date()],
    maturityValue: [''],
    periodInMonths: ['', Validators.required],
    principalCrAccountId: ['', Validators.required],
    principalDrAccountId: ['', Validators.required],
    sumAccruedAmount: [''],
    termDepositAmount: ['', Validators.required],
    termDepositStatus: [''],
    valueDate: [new Date()],
    withholdingTax: []
  });
  get f() {
    return this.formData.controls;
  }
  get td() {
    return this.termDepositForm.controls;
  }
  initialisenomineesForm() {
    this.initnomineesForm();
    this.nomineesForm.controls.dob.setValidators([Validators.required]);
    this.nomineesForm.controls.emailAddress.setValidators([Validators.required]);
    this.nomineesForm.controls.firstName.setValidators([Validators.required]);
    this.nomineesForm.controls.id.setValidators([]);
    this.nomineesForm.controls.identificationNo.setValidators([Validators.required]);
    this.nomineesForm.controls.lastName.setValidators([Validators.required]);
    this.nomineesForm.controls.middleName.setValidators([Validators.required]);
    this.nomineesForm.controls.occupation.setValidators([Validators.required]);
    this.nomineesForm.controls.phone.setValidators([Validators.required]);
    this.nomineesForm.controls.relationship.setValidators([Validators.required]);
    chrg_calc_crncy: ['', Validators.required];
  }
  initnomineesForm() {
    this.nomineesForm = this.fb.group({
      dob: [new Date()],
      emailAddress: [''],
      firstName: [''],
      id: [''],
      identificationNo: [''],
      lastName: [''],
      middleName: [''],
      occupation: [''],
      phone: [''],
      relationship: ['']
    });
  }
  addNominee() {
    if (this.nomineesForm.valid) {
      this.nomineesArray.push(this.nomineesForm.value);
      this.resetNomineeForm();
    }
  }
  getNominee() {
    this.nomineesDataSource = new MatTableDataSource(this.nomineesArray);
    this.nomineesDataSource.paginator = this.nomineesPaginator;
    this.nomineesDataSource.sort = this.nomineesSort;
  }
  resetNomineeForm() {
    this.formData.patchValue({
      nominees: this.nomineesArray,
    });
    this.getNominee();
    this.initialisenomineesForm();
  }
  editItem(nomineedata: any) {
    this.index = this.nomineesArray.indexOf(nomineedata);
    this.editButton = true;
    this.addButton = false;
    this.nomineesForm.patchValue({
      dob: nomineedata.dob,
      emailAddress: nomineedata.emailAddress,
      firstName: nomineedata.firstName,
      id: nomineedata.id,
      identificationNo: nomineedata.identificationNo,
      lastName: nomineedata.lastName,
      middleName: nomineedata.middleName,
      occupation: nomineedata.occupation,
      phone: nomineedata.phone,
      relationship: nomineedata.relationship
    });
  }
  updateNominee() {
    this.editButton = false;
    this.addButton = true;
    this.nomineesArray[this.index] = this.nomineesForm.value;
    this.formData.patchValue({
      nominees: this.nomineesArray,
    });
    this.resetNomineeForm();
  }
  deleteItem(data: any) {
    let deleteIndex = this.nomineesArray.indexOf(data);
    this.nomineesArray.splice(deleteIndex, 1);
    this.resetNomineeForm();
  }
  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.nomineesDataSource.filter = filterValue.trim().toLowerCase();
    if (this.nomineesDataSource.paginator) {
      this.nomineesDataSource.paginator.firstPage();
    }
  }
  initialiserelatedPartiesForm() {
    this.initrelatedPartiesForm();
    this.relatedPartiesForm.controls.id.setValidators([]);
    this.relatedPartiesForm.controls.relPartyCustomerCode.setValidators([Validators.required]);
    this.relatedPartiesForm.controls.relPartyCustomerName.setValidators([Validators.required]);
    this.relatedPartiesForm.controls.relPartyPostalAddress.setValidators([Validators.required]);
    this.relatedPartiesForm.controls.relPartyRelationType.setValidators([Validators.required]);
    chrg_calc_crncy: ['', Validators.required];
  }
  initrelatedPartiesForm() {
    this.relatedPartiesForm = this.fb.group({
      id: [''],
      relPartyCustomerCode: [''],
      relPartyCustomerName: [''],
      relPartyPostalAddress: [''],
      relPartyRelationType: ['']
    })
  }
  addrelatedParties() {
    if (this.relatedPartiesForm.valid) {
      this.relatedPartiesArray.push(this.relatedPartiesForm.value);
      this.resetrelatedPartiesForm();
    }
  }
  getrelatedParties() {
    this.relatedPartiesDataSource = new MatTableDataSource(this.relatedPartiesArray);
    this.relatedPartiesDataSource.paginator = this.relatedPartiesPaginator;
    this.relatedPartiesDataSource.sort = this.relatedPartiesSort;
  }
  resetrelatedPartiesForm() {
    this.formData.patchValue({
      relatedParties: this.relatedPartiesArray,
    });
    this.getrelatedParties();
    this.initrelatedPartiesForm();
  }
  editrelatedParties(data: any) {
    this.index = this.relatedPartiesArray.indexOf(data);
    this.editButton = true;
    this.addButton = false;
    this.relatedPartiesForm.patchValue({
      id: data.id,
      relPartyCustomerCode: data.relPartyCustomerCode,
      relPartyCustomerName: data.relPartyCustomerName,
      relPartyPostalAddress: data.relPartyPostalAddress,
      relPartyRelationType: data.relPartyRelationType
    });
  }
  updaterelatedParties() {
    this.editButton = false;
    this.addButton = true;
    this.relatedPartiesArray[this.index] = this.relatedPartiesForm.value;
    this.formData.patchValue({
      relatedParties: this.relatedPartiesArray
    });
    this.resetrelatedPartiesForm();
  }


  deleterelatedParties(relatedPartydata: any) {
    let deleteIndex = this.relatedPartiesArray.indexOf(relatedPartydata);
    this.relatedPartiesArray.splice(deleteIndex, 1);
    this.resetrelatedPartiesForm();
  }
  applyrelatedPartiesFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.relatedPartiesDataSource.filter = filterValue.trim().toLowerCase();
    if (this.relatedPartiesDataSource.paginator) {
      this.relatedPartiesDataSource.paginator.firstPage();
    }
  }

  initialiseAccountDocumentsForm() {
    this.initAccountDocumentsForm();
    this.accountDocumentsForm.controls.documentImage.setValidators([Validators.required]);
    this.accountDocumentsForm.controls.documentTitle.setValidators([Validators.required]);
    this.accountDocumentsForm.controls.sn.setValidators([]);
  }
  initAccountDocumentsForm() {
    this.accountDocumentsForm = this.fb.group({
      documentImage: [''],
      documentTitle: [''],
      sn: ['']
    })
  }
  addAccountDocuments() {
    if (this.accountDocumentsForm.valid) {
      this.accountDocumentsArray.push(this.accountDocumentsForm.value);
      this.resetAccountDocumentsForm();
    }
  }
  getAccountDocuments() {
    this.accountDocumentsDataSource = new MatTableDataSource(this.accountDocumentsArray);
    this.accountDocumentsDataSource.paginator = this.accountDocumentsPaginator;
    this.accountDocumentsDataSource.sort = this.accountDocumentsSort;
  }
  resetAccountDocumentsForm() {
    this.formData.patchValue({
      accountDocuments: this.accountDocumentsArray,
    });
    this.getAccountDocuments();
    this.initAccountDocumentsForm();
    this.accountDocumentsForm.controls.documentImage.setValue("");
    this.imageSrc = "";

  }
  editAccountDocuments(data: any) {
    this.index = this.accountDocumentsArray.indexOf(data);
    this.editButton = true;
    this.addButton = false;
    this.accountDocumentsForm.patchValue({
      documentImage: data.documentImage,
      documentTitle: data.documentTitle,
      sn: data.sn,
    });
  }
  updateAccountDocuments() {
    this.editButton = false;
    this.addButton = true;
    this.accountDocumentsArray[this.index] = this.accountDocumentsForm.value;
    this.formData.patchValue({
      accountDocuments: this.accountDocumentsArray
    });
    this.resetAccountDocumentsForm();
  }
  deleteAccountDocuments(docdata: any) {
    let deleteIndex = this.accountDocumentsArray.indexOf(docdata);
    this.accountDocumentsArray.splice(deleteIndex, 1);
    this.resetAccountDocumentsForm();
  }
  applyAccountDocumentsFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.accountDocumentsDataSource.filter = filterValue.trim().toLowerCase();
    if (this.accountDocumentsDataSource.paginator) {
      this.accountDocumentsDataSource.paginator.firstPage();
    }
  }
  onImageFileChange(event: any) {
    this.imageFile = event.target.files[0];
    if (event.target.files && event.target.files[0]) {
      var reader = new FileReader();
      reader.readAsDataURL(event.target.files[0]);
      reader.onload = () => {
        this.documentCaptured = reader.result;
        this.accountDocumentsForm.controls.documentImage.setValue(this.documentCaptured);
        this.imageSrc = reader.result as string;
      }
      reader.onerror = function (error) {
      };
    }
  }
  onView() {

  }
  openDialog(action: any) {
    const dialogRef = this.dialog.open(NewMembershipComponent, {
      maxWidth: '100vw',
      maxHeight: '100vh',
      height: '100%',
      width: '100%',
      panelClass: 'full-screen-modal',
      data: action
    });
  }

  getRelationships() {
    this.relationship = relationships;
  }
  getRelationTypes() {
    this.relationtypes = relationType;
  }
  getOccupations() {
    this.occupations = occupations;
  }
  getSectorCode() {
    this.sectorAPI.find().subscribe(
      (res) => {
        this.mainSectorData = res.entity;
      }
    )
  }
  onSectorCodeChange(event: any) {
    this.code = event.target.value;
    this.sectorAPI.findByCode(event.target.value).pipe(takeUntil(this.destroy$)).subscribe(
      (res) => {
        this.subSectorData = this.mainSectorData.filter(ev => ev.misCode == event.target.value)[0].missubsectors;
      }
    )
  }
  onCheckedAccountStatement(event: MatCheckboxChange): void {
    if (event.checked) {
      this.onShowAccountStatement = true;
    } else if (!event.checked) {
      this.onShowAccountStatement = false;
    }
  }

  tdaProductCodeLookUp(): void {
    const dialogRef = this.dialog.open(TermDepositLookupComponent, {
      width: '35%',
      autoFocus: false,
      maxHeight: '90vh'
    });
    dialogRef.afterClosed().subscribe(result => {
      this.lookupdata = result.data;
      this.subHeadLookupdata = this.lookupdata.glsubheads;
      this.subHeadLookupFound = this.subHeadLookupdata[0].gl_subhead;
      this.formData.controls.productCode.setValue(this.lookupdata.productCode);
      this.formData.controls.glSubhead.setValue(this.subHeadLookupFound);
      this.formData.controls.transferExceptionLimitDr.setValue(99999999);
      this.formData.controls.transferExceptionLimitCr.setValue(99999999);
      this.formData.controls.cashExceptionLimitDr.setValue(99999999);
      this.formData.controls.cashExceptionCr.setValue(99999999);
      this.tdaWithdrawals = this.lookupdata.tdaDetails;
    });
  }
  customerKeyUp() {

  }

  onKeyUp(event: any) {
    this.loading = true;
    this.dtaParams = new HttpParams()
      .set('productCode', event.target.value)
    this.productService.getTDAProduct(this.dtaParams).pipe(takeUntil(this.destroy$)).subscribe(
      {
        next: (
          (res) => {
            if (res.statusCode === 200) {
              this.loading = false;
              this.lookupdata = res.entity;
              this.subHeadLookupdata = this.lookupdata.glsubheads;
              this.subHeadLookupFound = this.subHeadLookupdata[0].gl_subhead;
              this.formData.controls.productCode.setValue(this.lookupdata.productCode);
              this.formData.controls.glSubhead.setValue(this.subHeadLookupFound);
              this.formData.controls.transferExceptionLimitDr.setValue(99999999);
              this.formData.controls.transferExceptionLimitCr.setValue(99999999);
              this.formData.controls.cashExceptionLimitDr.setValue(99999999);
              this.formData.controls.cashExceptionCr.setValue(99999999);
              this.tdaWithdrawals = this.lookupdata.tdaDetails;
            } else {
              this.loading = false;
              this.notificationAPI.alertWarning("TDA Product Code is Invalid!!");
            }
          }
        ),
        error: (
          (err) => {
            this.loading = false;
            this.notificationAPI.alertWarning("Server Error: !!");
          }
        ), complete: (
          () => {

          }
        )
      }

    ), Subscription
  }
  savingsCodeLookUp(): void {
    const dialogRef = this.dialog.open(SavingsLookupComponent, {
      width: '50%',
      autoFocus: false,
      maxHeight: '90vh'
    });
    dialogRef.afterClosed().pipe(takeUntil(this.destroy$)).subscribe(result => {
      this.lookupdata = result.data;
      this.termDepositForm.controls.interestCrAccountId.setValue(this.lookupdata.acid);
    });
  }
  principalDrAccountIdLookUp(): void {
    const dialogRef = this.dialog.open(SavingsLookupComponent, {
      width: '50%',
      autoFocus: false,
      maxHeight: '90vh'
    });
    dialogRef.afterClosed().pipe(takeUntil(this.destroy$)).subscribe(result => {
      this.lookupdata = result.data;
      this.termDepositForm.controls.principalDrAccountId.setValue(this.lookupdata.acid);
    });
  }
  principalCrAccountIdLookUp(): void {
    const dialogRef = this.dialog.open(SavingsLookupComponent, {
      width: '50%',
      autoFocus: false,
      maxHeight: '90vh'
    });
    dialogRef.afterClosed().pipe(takeUntil(this.destroy$)).subscribe(result => {
      this.lookupdata = result.data;
      this.termDepositForm.controls.principalCrAccountId.setValue(this.lookupdata.acid);
    });
  }
  relatedPartyCustomerLookup(): void {
    const dialogRef = this.dialog.open(UniversalMembershipLookUpComponent, {
      width: '50%',
      autoFocus: false,
      maxHeight: '90vh'
    });
    dialogRef.afterClosed().pipe(takeUntil(this.destroy$)).subscribe(result => {
      this.customer_lookup = result.data;
      this.relatedPartiesForm.controls.relPartyCustomerName.setValue(this.customer_lookup.customerName);
      this.relatedPartiesForm.controls.relPartyCustomerCode.setValue(this.customer_lookup.customerCode)
    });
  }
  customerLookup(): void {
    const dialogRef = this.dialog.open(UniversalMembershipLookUpComponent, {
      width: '60%',
      autoFocus: false,
      maxHeight: '90vh'
    });
    dialogRef.afterClosed().subscribe(result => {
      this.customer_lookup = result.data;
      this.customer_account_id = this.customer_lookup.id;
      this.customer_account_code = this.customer_lookup.customerCode;
      this.formData.controls.customerCode.setValue(this.customer_account_code);
      this.formData.controls.accountName.setValue(this.customer_lookup.customerName);
      this.formData.controls.solCode.setValue(this.customer_lookup.branchCode);
      this.formData.controls.customerType.setValue(this.customer_lookup.customerType);
      if (this.customer_account_code === null || this.customer_account_code === undefined || this.customer_account_code === '-') {
        this.onMemberDetailsLookup = false;
      } else {
        this.onMemberDetailsLookup = true;
      }
    });
  }

  // memberDetailsLookup(data: number) {
  //   if (this.customer_lookup.customerType == '12') {
  //     this.dialog.open(GroupMembershipDetailsComponent, {
  //       maxWidth: '100vw',
  //       maxHeight: '100vh',
  //       height: '100%',
  //       width: '100%',
  //       panelClass: 'full-screen-modal',
  //       data: this.customer_account_id
  //     });
  //   } else if (this.customer_lookup.customerType !== '12') {
  //     this.dialog.open(MemberDetailsComponent, {
  //       maxWidth: '100vw',
  //       maxHeight: '100vh',
  //       height: '100%',
  //       width: '100%',
  //       panelClass: 'full-screen-modal',
  //       data: this.customer_account_id
  //     });
  //   }
  // }

  accountManagerLookup(): void {
    const dialogRef = this.dialog.open(AccountUsersComponent, {
      width: '50%',
      autoFocus: false,
      maxHeight: '90vh'
    });
    dialogRef.afterClosed().pipe(takeUntil(this.destroy$)).subscribe(result => {
      this.customer_lookup = result.data;
      this.formData.controls.accountManager.setValue(this.customer_lookup.firstName + " " + this.customer_lookup.lastName);
      this.formData.controls.managerCode.setValue(this.customer_lookup.entityId + this.customer_lookup.solCode);
    });
  }
  onCheckedJointAccount(event: MatCheckboxChange): void {
    if (event.checked) {
      this.onShowRelatedPartiesTab = true;
    } else if (!event.checked) {
      this.onShowRelatedPartiesTab = false;
    }
  }
  getFunctions() {
    this.loading = true;
    this.showAccountCode = true;
    this.showSearch = false;
    this.showCustomerType = true;
    this.showVerification = true;
    this.onShowUpdateButton = false;
    this.showTableOperation = false;
    this.showButtons = false;
    this.onShowResults = true;
    this.onShowDate = false;
    this.onShowDatePicker = false;
    this.onShowNomineesForm = false;
    this.onShowRelatePartiesForm = false;
    this.onShowAccountStatement = true;
    this.onShowRelatedPartiesTab = false;
    this.onShowAccountBalance = true;
    this.onShowWarning = false;
    this.onShowAccountImages = false;
    this.onShowImagesForm = false;
    this.onShowImageDivider = true;
    this.onShowUploadedImages = true;
    this.onShowSelect = false;
    this.isHiddenInput = true;
    this.onHideSpecificDetails = true;
    this.formData.disable();
    this.termDepositForm.disable();
  }
  getModifyFunctions() {
    this.loading = true;
    this.showAccountCode = true;
    this.showCustomerType = true;
    this.onShowAddButton = false;
    this.onShowUpdateButton = true;
    this.showSearch = true;
    this.showVerification = true;
    this.showTableOperation = true;
    this.showButtons = true;
    this.onShowResults = true;
    this.onShowAccountBalance = true;
    this.onShowSelect = false;
    this.isHiddenInput = true;
    this.onHideSpecificDetails = true;
    this.gettermDepositDetailsData();
  }
  getAccountStatementFunctions() {
    this.loading = false;
    this.onShowAccountStatementTab = true;
    this.onShowAccountDocumentsTab = false;
    this.onShowNomineesTab = false;
    this.onShowSpecificDetailsTab = false;
    this.onShowGeneralDetailsTab = false;
    this.onShowMainMenu = true;
  }
  statementForm = this.fb.group({
    acid: [''],
    fromdate: [new Date()],
    todate: [new Date()]
  })
  eventStart(event: any) {
    this.fromdate = event.target.value;
  }
  eventEnd(event: any) {
    this.todate = event.target.value;
  }

  getAccounStatementRecords() {
    this.loading = true;
    this.params = new HttpParams()
      .set("acid", this.fmData.account_code)
      .set('fromdate', this.datepipe.transform(this.statementForm.controls.fromdate.value, "yyyy-MM-dd"))
      .set('todate', this.datepipe.transform(this.statementForm.controls.todate.value, "yyyy-MM-dd"));
    this.statementAPI.getStatement(this.params).pipe(takeUntil(this.destroy$)).subscribe(
      (res) => {
        this.loading = false;
        this.results = res;
        this.account_statement = this.results.entity;
        this.gettermDepositDetailsData();
        this.notificationAPI.alertSuccess(this.results.message);
      }, err => {
        this.loading = false;
        this.notificationAPI.alertWarning("Server Errror: !!");
      }
    )
  }
  gettermDepositDetailsData() {
    this.loading = true;
    this.accountsAPI.retrieveAccount(this.fmData.account_code).pipe(takeUntil(this.destroy$)).subscribe(
      data => {
        this.loading = false;
        console.log(data);
        if (data.statusCode === 302) {
          this.loading = false;
          this.accountsdata = data.entity;
          if (this.fmData.function_type == 'VERIFY') {
            if (this.accountsdata.verifiedFlag == 'Y') {
              this.hideRejectBtn = false;
            }
            if (this.accountsdata.verifiedFlag == 'N') {
              this.btnRejectColor = 'accent';
              this.rejectBtnText = 'REJECT';
              this.hideRejectBtn = true;
            }
          }
          this.results = data.entity;
          this.termDepositDetails = this.results.termDeposit;
          this.termDepositForm = this.fb.group({
            accrualLastDate: [this.termDepositDetails.accrualLastDate],
            id: [this.termDepositDetails.id],
            interestAmount: [this.termDepositDetails.interestAmount],
            interestCrAccountId: [this.termDepositDetails.interestCrAccountId],
            interestPreferential: [this.termDepositDetails.interestPreferential],
            interestRate: [this.termDepositDetails.interestRate],
            maturityDate: [this.termDepositDetails.maturityDate],
            maturityValue: [this.termDepositDetails.maturityValue],
            periodInMonths: [this.termDepositDetails.periodInMonths],
            principalCrAccountId: [this.termDepositDetails.principalCrAccountId],
            principalDrAccountId: [this.termDepositDetails.principalDrAccountId],
            sumAccruedAmount: [this.termDepositDetails.sumAccruedAmount],
            termDepositAmount: [this.termDepositDetails.termDepositAmount],
            termDepositStatus: [this.termDepositDetails.termDepositStatus],
            valueDate: [this.termDepositDetails.valueDate],
            withholdingTax: [this.termDepositDetails.withholdingTax]
          });
          if (this.results.checkedJointAccount == true) {
            this.onShowRelatedPartiesTab = true;
          } else if (this.results.checkedJointAccount == false) {
            this.onShowRelatedPartiesTab = false;
          }
          this.formData = this.fb.group({
            id: [this.results.id],
            acid: [this.results.acid],
            customerType: [this.results.customerType],
            accountType: [this.results.accountType],
            entityId: [this.results.entity],
            accountBalance: [this.results.accountBalance],
            accountManager: [this.results.accountManager],
            accountName: [this.results.accountName],
            accountOwnership: [this.results.accountOwnership],
            operationMode: [this.results.operationMode],
            checkedJointAccount: [this.results.checkedJointAccount],
            isWithdrawalAllowed: [this.results.isWithdrawalAllowed],
            accountStatus: [this.results.accountStatus],
            currency: [this.results.currency],
            productCode: [this.results.productCode],
            customerCode: [this.results.customerCode],
            managerCode: [this.results.managerCode],
            lienAmount: [this.results.lienAmount],
            referredBy: [this.results.referredBy],
            withholdingTax: [this.results.withholdingTax],
            misSectorCode: [this.results.misSectorCode],
            misSubSectorCode: [this.results.misSubSectorCode],
            glSubhead: [this.results.glSubhead],
            glCode: [this.results.glCode],
            solCode: [this.results.solCode],
            openingDate: [this.results.openingDate],
            cashExceptionCr: [this.results.cashExceptionCr],
            cashExceptionLimitCr: [this.results.cashExceptionLimitCr],
            cashExceptionLimitDr: [this.results.cashExceptionLimitDr],
            transferExceptionLimitCr: [this.results.transferExceptionLimitCr],
            transferExceptionLimitDr: [this.results.transferExceptionLimitDr],
            accountStatement: [this.results.accountStatement],
            statementFreq: [this.results.statementFreq],
            dispatchMode: [this.results.dispatchMode],
            postedBy: [this.results.postedBy],
            postedFlag: [this.results.postedFlag],
            postedTime: [this.results.postedTime],
            modifiedBy: [this.results.modifiedBy],
            modifiedTime: [this.results.modifiedTime],
            verifiedBy: [this.results.verifiedBy],
            verifiedFlag: [this.results.verifiedFlag],
            verifiedTime: [this.results.verifiedTime],
            deleteFlag: [this.results.deleteFlag],
            deleteTime: [this.results.deleteTime],
            deletedBy: [this.results.deletedBy],
            termDeposit: [''],
            accountDocuments: [[]],
            nominees: [[]],
            relatedParties: [[]],
            chrg_calc_crncy: [this.results.currency]
          });
          this.results.accountDocuments.forEach((element: any) => {
            this.accountDocumentsArray.push(element);
          });
          this.formData.patchValue({
            accountDocuments: this.accountDocumentsArray
          });
          this.getAccountDocuments();
          this.results.nominees.forEach((element: any) => {
            this.nomineesArray.push(element);
          });
          this.formData.patchValue({
            nominees: this.nomineesArray,
          });
          this.getNominee();
          this.results.relatedParties.forEach((element: any) => {
            this.relatedPartiesArray.push(element);
          });
          this.formData.patchValue({
            relatedParties: this.relatedPartiesArray,
          });
          this.getrelatedParties();
        } else {
          this.loading = false;
          this.notificationAPI.alertWarning(data.message);
          this.router.navigate([`system/term-deposit/account/maintenance`], { skipLocationChange: true });
        }
      }, err => {
        this.loading = false;
        this.notificationAPI.alertWarning("SERVER ERRPR: TRY AGAIN LATER");
        this.router.navigate([`system/term-deposit/account/maintenance`], { skipLocationChange: true });
      }
    );
  }
  getPage() {
    this.loading = true;
    if (this.fmData.function_type == 'ADD') {
      this.loading = false;
      this.showAccountCode = false;
      this.showCustomerType = true;
      this.showCustomerType = false;
      this.formData = this.fb.group({
        id: [''],
        acid: [''],
        entityId: [''],
        customerType: [''],
        accountType: [this.account_type],
        accountBalance: [''],
        accountManager: [this.currentUser.firstName + " " + this.currentUser.lastName],
        accountName: [''],
        accountOwnership: ['SINGLE ACCOUNT'],
        checkedJointAccount: [''],
        isWithdrawalAllowed: ['TRUE'],
        operationMode: [''],
        accountStatus: [''],
        cashExceptionCr: [''],
        cashExceptionLimitDr: [''],
        currency: ['UGX'],
        customerCode: ['', Validators.required],
        managerCode: [this.currentUser.memberCode],
        dispatchMode: [''],
        glSubhead: [''],
        glCode: [''],
        lienAmount: [''],
        openingDate: [new Date()],
        productCode: ['', Validators.required],
        solCode: [''],
        misSectorCode: ['', Validators.required],
        misSubSectorCode: ['', Validators.required],
        transferExceptionLimitCr: [''],
        transferExceptionLimitDr: [''],
        withholdingTax: [''],
        accountStatement: [''],
        statementFreq: [''],
        transactionPhone: [''],
        termDeposit: [''],
        accountDocuments: [[]],
        nominees: [[]],
        relatedParties: [[]],
        chrg_calc_crncy: ['', Validators.required]
      });
      this.termDepositForm = this.fb.group({
        accrualLastDate: [''],
        id: [''],
        interestAmount: [''],
        interestCrAccountId: [''],
        interestPreferential: ['', Validators.required],
        interestRate: [''],
        maturityDate: [new Date()],
        maturityValue: [''],
        periodInMonths: ['', Validators.required],
        principalCrAccountId: ['', Validators.required],
        principalDrAccountId: ['', Validators.required],
        sumAccruedAmount: [''],
        termDepositAmount: ['', Validators.required],
        termDepositStatus: [''],
        valueDate: [new Date()],
        withholdingTax: ['', Validators.required]
      });
      this.btnColor = 'primary';
      this.btnText = 'SUBMIT';
    } else if (this.fmData.function_type == 'INQUIRE') {
      this.getFunctions();
      this.gettermDepositDetailsData();
    }
    else if (this.fmData.function_type == 'STATEMENT') {
      this.onShowAccountStatementDateParams = true;
      this.getAccountStatementFunctions();
    } else if (this.fmData.function_type == 'MODIFY') {
      this.getModifyFunctions();
      this.btnColor = 'primary';
      this.btnText = 'MODIFY';
    }
    else if (this.fmData.function_type == 'REJECT') {
      this.gettermDepositDetailsData();
      this.getFunctions();
      this.btnColor = 'accent';
      this.btnText = 'REJECT';
    }
    else if (this.fmData.function_type == 'VERIFY') {
      this.gettermDepositDetailsData();
      this.getFunctions();
      this.showApprovalbtn = true;
      this.btnColor = 'primary';
      this.btnText = 'VERIFFY';
    }
    else if (this.fmData.function_type == 'CLOSE TDA') {
      this.gettermDepositDetailsData();
      this.getFunctions();
      this.showApprovalbtn = true;
      this.btnColor = 'primary';
      this.btnText = 'CLOSE TDA';
    }
    else if (this.fmData.function_type == 'DELETE') {
      this.gettermDepositDetailsData();
      this.getFunctions();
      this.btnColor = 'accent';
      this.btnText = 'DELETE';
    }
  }
  chrgCalcCrncyLookup(): void {
    const dialogRef = this.dialog.open(CurrencyLookupComponent, {});
    dialogRef.afterClosed().subscribe((result) => {
       this.chrg_calc_crncy = result.data.ccy;
      // this.chrg_calc_crncy = result.data.ccy_name;
      this.formData.controls.chrg_calc_crncy.setValue(result.data.ccy);
    });
  }
  onSubmit() {
    this.submitted = true;
    this.submittedTda = true;
    this.loading = true;
    if (this.fmData.function_type == 'ADD') {
      if (this.formData.valid) {
        this.formData.controls.termDeposit.setValue(this.termDepositForm.value);
        this.accountsAPI.createAccount(this.formData.value).pipe(takeUntil(this.destroy$)).subscribe(
          res => {
            this.loading = false;
            this.results = res;
            if (res.statusCode == 201) {
              this.accountsNotification.alertSuccess(this.results.message);
              this.router.navigate([`system/term-deposit/account/maintenance`], { skipLocationChange: true });
            } else {
              this.loading = false;
              this.notificationAPI.alertWarning(this.results.message);
            }
          },
          err => {
            this.loading = false;
            this.notificationAPI.alertWarning("Server Errror: !!");
          }
        )
      }
      else if (!this.formData.valid) {
        this.loading = false;
        this.notificationAPI.alertWarning("FIXED DEPOSIT FORM DATA INVLID");
      }
    } else if (this.fmData.function_type == 'VERIFY') {
      this.loading = true;
      this.showSearch = false;
      this.acid = this.fmData.account_code;
      this.params = new HttpParams().set('Acid', this.acid);
      this.accountsAPI.verifyAccount(this.params).pipe(takeUntil(this.destroy$)).subscribe(
        res => {
          console.log(res);
          if (res.statusCode == 200) {
            this.results = res;
            this.loading = false;
            this.accountsNotification.alertSuccess(this.results.message);
            this.router.navigate([`system/term-deposit/account/maintenance`], { skipLocationChange: true });
          } else {
            this.loading = false;
            //this.gettermDepositDetailsData();
            this.notificationAPI.alertWarning(res.message);
          }
        },
        err => {
          console.log("In error");
          console.log(err);
          this.loading = false;
          //this.gettermDepositDetailsData();
          this.notificationAPI.alertWarning("Server Errror: !!");
        }
      )
    }
    else if (this.fmData.function_type == 'CLOSE TDA') {
      this.loading = true;
      this.showSearch = false;
      this.acid = this.fmData.account_code;
      this.params = new HttpParams().set('acid', this.acid);
      this.accountsAPI.payTermDeposit(this.params).pipe(takeUntil(this.destroy$)).subscribe(
        res => {
          if (res.statusCode == 200) {
            this.results = res;
            this.loading = false;
            this.accountsNotification.alertSuccess(this.results.message);
            console.log("message",this.results.message);
            this.router.navigate([`system/term-deposit/account/maintenance`], { skipLocationChange: true });
          } else {
            this.loading = false;
            this.gettermDepositDetailsData();
            this.notificationAPI.alertWarning(this.results.message);
          }
        },
        err => {
          this.loading = false;
          this.gettermDepositDetailsData();
          this.notificationAPI.alertWarning("Server Errror: !!");
        }
      )
    }
    if (this.fmData.function_type == 'MODIFY') {
      this.loading = true;
      this.formData.controls.termDeposit.setValue(this.termDepositForm.value);
      this.accountsAPI.updateAccounts(this.formData.value).pipe(takeUntil(this.destroy$)).subscribe(
        res => {
          if (res.statusCode == 200) {
            this.results = res;
            this.loading = false
            this.accountsNotification.alertSuccess(this.results.message);
            this.router.navigate([`system/term-deposit/account/maintenance`], { skipLocationChange: true });
          } else {
            this.loading = false
            this.gettermDepositDetailsData();
            this.notificationAPI.alertWarning(this.results.message);
          }
        },
        err => {
          this.loading = false;
          this.gettermDepositDetailsData();
          this.notificationAPI.alertWarning("Server Errror: !!");
        }
      )
    }
    else if (this.fmData.function_type == 'REJECT') {
      this.loading = true;
      this.showSearch = false;
      this.params = new HttpParams().set('acid', this.fmData.account_code);
      this.accountsAPI.rejectAccount(this.params).pipe(takeUntil(this.destroy$)).subscribe(
        res => {
          if (res.statusCode == 200) {
            this.loading = false;
            this.accountsNotification.alertSuccess(res.message);
            this.router.navigate([`system/loan-account/maintenance`], { skipLocationChange: true });
          } else {
            this.loading = false;
            this.gettermDepositDetailsData();
            this.notificationAPI.alertWarning(res.message);
          }
        },
        err => {
          this.loading = false;
          this.gettermDepositDetailsData();
          this.notificationAPI.alertWarning("Server Error: !!!");
        }
      )
    }
    else if (this.fmData.function_type == 'DELETE') {
      if (window.confirm("ARE YOU SURE YOU WANT TO REMOVE THE FIXED DEPOSIT? ALL RECORDS WILL BE DELETED!!")) {
        this.loading = true;
        this.id = this.results.id;
        this.deletedBy = this.userName;
        this.params = new HttpParams().set("id", this.id).set("deletedBy", this.deletedBy);
        this.accountsAPI.temporaryDeleteAccount(this.params).pipe(takeUntil(this.destroy$)).subscribe(
          res => {
            this.loading = false;
            this.results = res
            this.notificationAPI.alertSuccess(this.results.message);
            this.router.navigate([`system/term-deposit/account/maintenance`], { skipLocationChange: true });
          },
          err => {
            this.loading = false;
            this.gettermDepositDetailsData();
            this.notificationAPI.alertWarning("Server Errror: !!");
          }
        )
      } else {
        this.loading = false;
        this.router.navigate([`system/term-deposit/account/data-view`], { skipLocationChange: true });
      }

    }
  }
  printAccountStatement(): void {
    let printContents: any, popupWin: any;
    printContents = document.getElementById('print-section').innerHTML;
    popupWin = window.open('', '_blank', 'top=0,left=0,height=100%,width=auto');
    popupWin.document.open();
    popupWin.document.write(`
      <html>
        <head>
          <title>ACCOUNT STATEMENT</title>
          <hr/>
           <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
        </head>
    <body onload="window.print();window.close()">${printContents}</body>
      </html>`
    );
    printContents = setTimeout(function () { popupWin.close(); }, 100000);
    popupWin.print();
  }

  onExportAccountStatement() {
    var data = document.getElementById('print-section');
    html2canvas(data).then(canvas => {
      let imgWidth = 208;
      let pageHeight = 295;
      let imgHeight = canvas.height * imgWidth / canvas.width;
      let heightLeft = imgHeight;

      const contentDataURL = canvas.toDataURL('image/png')
      let pdf = new jspdf('p', 'mm', 'a4');
      let position = 0;
      pdf.addImage(contentDataURL, 'PNG', 0, position, imgWidth, heightLeft)
      pdf.save('account-statement.pdf');
    });
  }
  onApproval() {
    this.dialogConfig = new MatDialogConfig();
    this.dialogConfig.disableClose = true;
    this.dialogConfig.autoFocus = true;
    this.dialogConfig.width = "600px";
    this.dialogConfig.data = this.accountsdata
    const dialogRef = this.dialog.open(AccountsApprovalComponent, this.dialogConfig);
    dialogRef.afterClosed().subscribe(
      (_res: any) => {
        this.loading = false;
      });
  }
  onRejectAccount() {
    if (window.confirm("ARE YOU SURE YOU WANT TO REJECT ACCOUNT " + this.results.accountType + " WITH ACCOUNT NO: " + this.results.acid + " ?")) {
      this.loading = true;
      this.params = new HttpParams()
        .set('acid', this.accountsdata.acid);
      this.accountsAPI.rejectAccount(this.params).pipe(takeUntil(this.destroy$)).subscribe(
        {
          next: (
            (res) => {
              if (res.statusCode === 200) {
                this.loading = false;
                this.accountsNotification.alertSuccess(res.message);
                if (this.fmData.backBtn == 'APPROVAL') {
                  this.router.navigate([`/system/new/notification/allerts`], { skipLocationChange: true });
                } else {
                  this.router.navigate([`/system/term-deposit/account/maintenance`], { skipLocationChange: true });
                }
              } else {
                this.loading = false;
                this.notificationAPI.alertWarning(res.message);
              }
            }
          ),
          error: (
            (err) => {
              this.loading = false;
              this.notificationAPI.alertWarning("Service Error: !!!");
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
