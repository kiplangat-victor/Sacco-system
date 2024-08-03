import { DatePipe } from '@angular/common';
import { HttpParams } from '@angular/common/http';
import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { FormArray, FormBuilder, FormGroup, Validators } from '@angular/forms';
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
import { AccountStatementService } from 'src/app/administration/Service/transaction/account-statement.service';
import { environment } from 'src/environments/environment';
import { GroupMembershipDetailsComponent } from '../../MembershipComponent/GroupMembership/group-membership-details/group-membership-details.component';
// import { MemberDetailsComponent } from '../../MembershipComponent/Membership/member-details/member-details.component';
import { UniversalMembershipLookUpComponent } from '../../MembershipComponent/universal-membership-look-up/universal-membership-look-up.component';
import { MisSectorService } from '../../SystemConfigurations/GlobalParams/mis-sector/mis-sector.service';
import { ReportService } from '../../reports/report.service';
import { AccountDoumentsComponent } from '../account-douments/account-douments.component';
import { AccountUsersComponent } from '../account-users/account-users.component';
import { AccountsApprovalComponent } from '../accounts-approval/accounts-approval.component';
import { SavingsProductLookupComponent } from './../../ProductModule/savings/savings-product-lookup/savings-product-lookup.component';
import { relationType } from './relation-types';
import { relationships } from './relationship';
import { CurrencyLookupComponent } from '../../SystemConfigurations/GlobalParams/currency-config/currency-lookup/currency-lookup.component';

@Component({
  selector: 'app-savings-account',
  templateUrl: './savings-account.component.html',
  styleUrls: ['./savings-account.component.scss']
})
export class SavingsAccountComponent implements OnInit, OnDestroy {
  accountDocumentsColumns: string[] = [
    "index",
    "documentTitle",
    'view',
    "actions",
  ];
  accountDocumentsDataSource: MatTableDataSource<any>;
  @ViewChild("accountDocumentsPaginator") accountDocumentsPaginator!: MatPaginator;
  @ViewChild(MatSort) accountDocumentsSort!: MatSort;
  accountDocumentsForm!: FormGroup;
  accountDocumentsArray: any[] = [];
  accountDocumentTypeArray = [
    { documentType: 'id', description: 'ID' },
    { documentType: 'taxDocument', description: 'Tax Document' },
    { documentType: 'signatureImage', description: 'Signature Image' },
    { documentType: 'customerPhoto', description: 'Customer Photo' },
  ];
  editButton: boolean = false;
  addButton: boolean = true;
  disableAddButton: boolean = false;
  disableActions: boolean = false;
  index: number;
  despatch_mode_array: any = [
    'POST', 'E-MAIL', 'COURIER', 'COLLECT IN PERSON'
  ];
  statementFreqArray: any = [
    'DAILY', 'WEEKLY', 'MONTLY', 'YEARLY'
  ];
  accountStatusArray: any = [
    'ACTIVE', 'DORMANT', 'NOT-ACTIVE'
  ];

  aaplicationStatusArray: any = [
    'PENDING', 'VERIFIED'
  ];
  relationshipArray: any = [
    'FATHER', 'MOTHER', 'WIFE', 'HUSBAND', 'SIBLING', 'DAUGHTER', 'SON'
  ];
  guardianArray: any = [
    'COURT APPOINTED', 'NATURAL GUARDIAN'
  ];
  documentTypeArray: any = [
    'IDENTIFICATION DOCUMENT','SIGNATURE IMAGE','PASSPORT-SIZED PHOTO','TAX DOCUMENT'
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
  signatureImage: any;
  loading: boolean = false;
  loadingRejection: boolean = false;
  lookupdata: any;
  glCode: any;
  error: any;
  results: any;
  i: number;
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
  Acid: any;
  function_type: any;
  account_code: any;
  onShowUpdateButton = false;
  showTableOperation = true;
  showButtons = true;
  onShowSubmitButton = true;
  onShowAddButton = true;
  onShowNomineesForm = true;
  onShowRelatePartiesForm = true;
  savingsDetails: any;
  submittedDocs = false;
  nomineeElement: any;
  arrayIndex: any;
  relPartyElement: any;
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
  sbaWithdrawals: any;
  onShowAccountOwnership = true;
  onShowRelatedPartiesTab = false;
  onShowDate = true;
  onShowWarning = true;
  onShowAccountStatus = false;
  exceptions: any;
  today = new Date();
  maturityDate = new Date(new Date().setDate(this.today.getDate() + 365));
  exceptionData: any;
  onShowAccountBalance = false;
  subSectorFound: any;
  accountBalance: any
  productCode: any;
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
  submittedSba = false;
  savingAMount: any;
  savingPeriod: any;
  totalSavings: number;
  nomineesArray = new Array();
  relatedPartiesArray = new Array();
  hideBtn = false;
  btnColor: any;
  btnText: any;
  destroy$: Subject<boolean> = new Subject<boolean>();
  logolink: string = `${environment.reportsAPI}/api/v1/dynamic/saccologo`;
  saccoNamelink: any;
  currentUser: any;
  onMemberDetailsLookup: boolean = false;
  customer_lookup_code: any;
  customer_account_code: any;
  customer_account_id: number;
  account_type: any;
  showApprovalbtn: boolean = false;
  accountsdata: any;
  hideRejectBtn: boolean = false;
  btnRejectColor: any;
  rejectBtnText: any;
  rejectionData: any;
  dialogConfig: MatDialogConfig<any>;
  disableViewAction: boolean = true;
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
    private notificationAPI: NotificationService,
    private statementAPI: AccountStatementService,
    private accountsNotification: AccountsNotificationService
  ) {
    this.currentUser = JSON.parse(localStorage.getItem('auth-user'));
    this.fmData = this.router.getCurrentNavigation().extras.queryParams.formData;
    this.function_type = this.fmData.function_type;
    this.customer_type = this.fmData.customer_type;

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
    this.initialiseAccountDocumentsForm();
    this.getSaccoName();
    this.getRelationships();
    this.getRelationTypes();
    this.getOccupations();
    this.getCardType();
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
    operationMode: [''],
    accountStatus: [''],
    accountType: [''],
    cashExceptionCr: [''],
    cashExceptionLimitDr: [''],
    currency: [''],
    customerCode: ['', Validators.required],
    managerCode: ['', Validators.required],
    dispatchMode: [''],
    glCode: [''],
    glSubhead: ['', Validators.required],
    isWithdrawalAllowed: [false, Validators.required],
    misSectorCode: ['', Validators.required],
    misSubSectorCode: ['', Validators.required],
    lienAmount: [''],
    openingDate: [''],
    productCode: ['', Validators.required],
    solCode: [''],
    transferExceptionLimitCr: [''],
    transferExceptionLimitDr: [''],
    accountStatement: [''],
    statementFreq: [''],
    transactionPhone: [''],
    nominees: new FormArray([]),
    accountDocuments: [[]],
    relatedParties: new FormArray([]),
    savings: [''],
    chrg_calc_crncy: ['']
  });

  relatedPartiesForm = this.fb.group({
    id: [''],
    relPartyCustomerCode: [''],
    relPartyCustomerName: [''],
    relPartyRelationType: [''],
    relPartyPostalAddress: ['']
  });
  onInitrelatedPartiesForm() {
    this.relatedPartiesForm = this.fb.group({
      id: [''],
      relPartyCustomerCode: [''],
      relPartyCustomerName: [''],
      relPartyRelationType: [''],
      relPartyPostalAddress: ['']
    })
  }
  imagesForm = this.fb.group({
    sn: [''],
    documentTitle: ['', Validators.required],
    documentImage: ['', Validators.required],
    documentSource: ['', Validators.required]
  });
  onInitImagesForm() {
    this.imagesForm = this.fb.group({
      sn: [''],
      documentTitle: [''],
      documentImage: [''],
      documentSource: ['']

    });
  }

  savingsForm: FormGroup = this.fb.group({
    id: [''],
    sba_maturedDate: [''],
    sba_maturedValue: [''],
    sba_monthlyValue: ['', Validators.required],
    sba_savingPeriod: ['', Validators.required],
    sba_startDate: [''],
  });
  nomineesForm = this.fb.group({
    id: [''],
    dob: [new Date()],
    emailAddress: [''],
    firstName: [''],
    lastName: [''],
    middleName: [''],
    identificationNo: [''],
    nomineeMinor: [''],
    occupation: [''],
    phone: [''],
    relationship: ['']
  });

  get f() {
    return this.formData.controls;
  }
  get n() {
    return this.f.nominees as FormArray;
  }
  get r() {
    return this.f.relatedParties as FormArray;
  }
  get dc() {
    return this.imagesForm.controls;
  }
  get d() {
    return this.f.accountDocuments as FormArray;
  }
  get s() {
    return this.savingsForm.controls;
  }
  onAddRelatedParty() {
    if (this.relatedPartiesForm.valid) {
      this.r.push(this.fb.group(this.relatedPartiesForm.value));
    }
    this.relatedPartiesArray.push(this.relatedPartiesForm.value);
    this.onInitrelatedPartiesForm();
  }
  onUpdateRelatedParty() {
    let i = this.relPartyElement;
    this.relatedPartiesArray[i] = this.relatedPartiesForm.value;
    this.onShowUpdateButton = false;
    this.onShowAddButton = true;
    this.onInitrelatedPartiesForm();
  }
  onEditRelatedParty(i: any) {
    this.relPartyElement = i;
    this.arrayIndex = this.relatedPartiesArray[i];
    this.relatedPartiesForm = this.fb.group({
      id: [this.relatedPartiesArray[i].id],
      relPartyCustomerCode: [this.relatedPartiesArray[i].relPartyCustomerCode],
      relPartyCustomerName: [this.relatedPartiesArray[i].relPartyCustomerName],
      relPartyPostalAddress: [this.relatedPartiesArray[i].relPartyPostalAddress],
      relPartyRelationType: [this.relatedPartiesArray[i].relPartyRelationType]
    });
    this.onShowUpdateButton = true;
    this.onShowAddButton = false;
  }
  onDeleteRelatedParty(i: any) {
    const index: any = this.relatedPartiesArray.indexOf(this.relatedPartiesArray.values);
    this.relatedPartiesArray.splice(i, 1);
    this.relatedPartiesArray = this.relatedPartiesArray;
  }
  onRelatedPartyClear() {
    this.onInitrelatedPartiesForm()
    this.relatedPartiesArray = new Array();
  }
  onInitNomineesForm() {
    this.nomineesForm = this.fb.group({
      id: [''],
      dob: [new Date()],
      emailAddress: [''],
      firstName: [''],
      lastName: [''],
      middleName: [''],
      identificationNo: [''],
      nomineeMinor: [''],
      occupation: [''],
      phone: [''],
      relationship: ['']
    });
  }
  onAddNominee() {
    if (this.nomineesForm.valid) {
      this.n.push(this.fb.group(this.nomineesForm.value));
    }
    this.nomineesArray.push(this.nomineesForm.value);
    this.onInitNomineesForm();
  }
  onUpdateNominee() {
    let i = this.nomineeElement;
    this.nomineesArray[i] = this.nomineesForm.value;
    this.onShowUpdateButton = false;
    this.onShowAddButton = true;
  }
  onEditNominee(i: any) {
    this.nomineeElement = i;
    this.arrayIndex = this.nomineesArray[i];
    this.nomineesForm = this.fb.group({
      id: [this.nomineesArray[i].id],
      dob: [this.nomineesArray[i].dob],
      emailAddress: [this.nomineesArray[i].emailAddress],
      firstName: [this.nomineesArray[i].firstName],
      lastName: [this.nomineesArray[i].lastName],
      middleName: [this.nomineesArray[i].middleName],
      identificationNo: [this.nomineesArray[i].identificationNo],
      nomineeMinor: [this.nomineesArray[i].nomineeMinor],
      occupation: [this.nomineesArray[i].occupation],
      phone: [this.nomineesArray[i].phone],
      relationship: [this.nomineesArray[i].relationship],
    });
    this.onShowUpdateButton = true;
    this.onShowAddButton = false;
  }
  onDeleteNominee(i: any) {
    const index: number = this.nomineesArray.indexOf(this.nomineesArray.values);
    this.nomineesArray.splice(i, 1);
    this.nomineesArray = this.nomineesArray
  }
  onNomineeClear() {
    this.nomineesArray = new Array();
    this.onInitNomineesForm()
  }

  initialiseAccountDocumentsForm() {
    this.initAccountDocumentsForm();

    this.accountDocumentsForm.controls.documentImage.setValidators([Validators.required]);
    this.accountDocumentsForm.controls.documentTitle.setValidators([Validators.required]);
    this.accountDocumentsForm.controls.sn.setValidators([]);
  }
  initAccountDocumentsForm() {
    this.accountDocumentsForm = this.fb.group({
      documentImage: [""],
      documentTitle: [""],
      sn: [""]
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
    this.accountDocumentsForm.controls.documentImage.setValue('');
    this.imageSrc = '';
  }
  editAccountDocuments(data: any) {
    this.index = this.accountDocumentsArray.indexOf(data);
    this.editButton = true;
    this.addButton = false;
    this.accountDocumentsForm.patchValue({
      documentImage: data.documentImage,
      documentTitle: data.documentTitle,
      sn: data.sn
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
  onViewDoument(sn: number) {
    this.dialogConfig = new MatDialogConfig();
    this.dialogConfig.disableClose = true;
    this.dialogConfig.autoFocus = true;
    this.dialogConfig.width = "30%";
    this.dialogConfig.data = sn;
    const dialogRef = this.dialog.open(AccountDoumentsComponent, this.dialogConfig);
    dialogRef.afterClosed().subscribe(
      (_res: any) => {
        this.loading = false;
      })
  }
  onImageFileChange(event: any) {
    this.imageFile = event.target.files[0];
    if (event.target.files && event.target.files[0]) {
      var reader = new FileReader();
      reader.readAsDataURL(event.target.files[0]);
      reader.onload = () => {
        this.signatureImage = reader.result;
        this.accountDocumentsForm.controls.documentImage.setValue(this.signatureImage);
        this.imageSrc = reader.result as string;
      }
      reader.onerror = function (error) {
      };
    }
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
  getCardType() {
    this.sectorAPI.find().subscribe(
      (res) => {
        this.mainSectorData = res.entity;
      }
    )
  }
  onCardTypeChange(event: any) {
    this.code = event.target.value;
    this.sectorAPI.findByCode(event.target.value).pipe(takeUntil(this.destroy$)).subscribe(
      // (res) => {
      //   this.subSectorData = this.mainSectorData.filter(ev => ev.misCode == event.target.value)[0].missubsectors;
      // }
    )
  }

  onCheckedAccountStatement(event: MatCheckboxChange): void {
    if (event.checked) {
      this.onShowAccountStatement = true;
    } else if (!event.checked) {
      this.onShowAccountStatement = false;
    }
  }
  onCheckedJointAccount(event: MatCheckboxChange): void {
    if (event.checked) {
      this.onShowRelatedPartiesTab = true;
    } else if (!event.checked) {
      this.onShowRelatedPartiesTab = false;
    }
  }
  savingsProductCodeLookUp(): void {
    const dialogRef = this.dialog.open(SavingsProductLookupComponent, {
      width: '35%',
      autoFocus: false,
      maxHeight: '90vh'
    });
    dialogRef.afterClosed().subscribe(result => {
      this.lookupdata = result.data;
      this.subHeadLookupdata = this.lookupdata.glsubheads;
      this.subHeadLookupFound = this.subHeadLookupdata[0].gl_subhead;
      this.sbaWithdrawals = this.lookupdata.sbaDetails;
      this.productCode = this.lookupdata.productCode;
      this.formData.controls.productCode.setValue(this.productCode);
      this.formData.controls.glSubhead.setValue(this.subHeadLookupFound);
      this.formData.controls.isWithdrawalAllowed.setValue(this.sbaWithdrawals.withdrawalsAllowed);
      this.formData.controls.transferExceptionLimitDr.setValue(99999999);
      this.formData.controls.transferExceptionLimitCr.setValue(99999999);
      this.formData.controls.cashExceptionLimitDr.setValue(99999999);
      this.formData.controls.cashExceptionCr.setValue(99999999);
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
  relatedPartyCustomerLookup(): void {
    const dialogRef = this.dialog.open(UniversalMembershipLookUpComponent, {
      width: '60%',
      autoFocus: false,
      maxHeight: '90vh'
    });
    dialogRef.afterClosed().subscribe(result => {
      this.customer_lookup = result.data;
      this.relatedPartiesForm.controls.relPartyCustomerName.setValue(this.customer_lookup.customerName);
      this.relatedPartiesForm.controls.relPartyCustomerCode.setValue(this.customer_lookup.customerCode)
    });
  }
  accountManagerLookup(): void {
    const dialogRef = this.dialog.open(AccountUsersComponent, {
      width: '60%',
      autoFocus: false,
      maxHeight: '90vh'
    });
    dialogRef.afterClosed().subscribe(result => {
      this.customer_lookup = result.data;
      this.formData.controls.accountManager.setValue(this.customer_lookup.firstName + " " + this.customer_lookup.lastName);
      this.formData.controls.managerCode.setValue(this.customer_lookup.entityId + this.customer_lookup.solCode);
    });
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
    this.onShowRelatedPartiesTab = true
    this.onShowAccountBalance = true;
    this.onShowWarning = false;
    this.onShowAccountImages = false;
    this.onShowImagesForm = false;
    this.onShowImageDivider = true;
    this.onShowUploadedImages = true;
    this.onShowSubmitButton = false;
    this.onShowSelect = false;
    this.isHiddenInput = true;
    this.onShowAccountStatus = true;
    this.disableViewAction = false;
    this.disableActions = true;
    this.formData.disable();
    this.savingsForm.disable();
    this.nomineesForm.disable();
    this.relatedPartiesForm.disable();
  }
  getModifyFunctions() {
    this.loading = true;
    this.showAccountCode = true;
    this.showCustomerType = true;
    this.onShowAddButton = false;
    this.onShowUpdateButton = true;
    this.showSearch = true;
    this.onShowAccountStatus = true;
    this.showVerification = true;
    this.showTableOperation = true;
    this.showButtons = true;
    this.onShowResults = true;
    this.onShowSubmitButton = false;
    this.onShowNomineesForm = true;
    this.onShowRelatePartiesForm = true;
    this.onShowAccountBalance = true;
    this.onShowSelect = false;
    this.isHiddenInput = true;
    this.getSavingsDetailsData();
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
  onKeyUp(event: any) {
    this.getSavingData();
  }
  getSavingData() {
    this.savingAMount = this.savingsForm.controls.sba_monthlyValue.value;
    this.savingPeriod = this.savingsForm.controls.sba_savingPeriod.value;
    this.totalSavings = this.savingAMount * this.savingPeriod;
    this.savingsForm.controls.sba_maturedValue.setValue(this.totalSavings);
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
        this.getSavingsDetailsData();
        this.notificationAPI.alertSuccess(this.results.message);
      }, err => {
        this.loading = false;
        this.error = err;
        this.notificationAPI.alertWarning("Server Error: !!!");
      }
    )
  }
  getSavingsDetailsData() {
    this.loading = true;
    this.accountsAPI.retrieveAccountById(this.fmData.id).pipe(takeUntil(this.destroy$)).subscribe(
      data => {
        this.loading = false;
        if (data.statusCode === 302) {
          this.loading = false;
          this.accountsdata = data.entity;

          this.account_code = this.accountsdata.acid

          console.log("this.accountsdata:: ", this.accountsdata)

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
          this.nomineesArray = this.results.nominees;
          this.relatedPartiesArray = this.results.relatedParties;
          this.savingsDetails = this.results.savings;
          this.savingsForm = this.fb.group({
            id: [this.savingsDetails.id],
            sba_maturedDate: [this.savingsDetails.sba_maturedDate],
            sba_maturedValue: [this.savingsDetails.sba_maturedValue],
            sba_monthlyValue: [this.savingsDetails.sba_monthlyValue],
            sba_savingPeriod: [this.savingsDetails.sba_savingPeriod],
            sba_startDate: [this.savingsDetails.sba_startDate],
          })
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
            savings: [''],
            nominees: new FormArray([]),
            relatedParties: new FormArray([]),
            accountDocuments: [[]],
            chrg_calc_crncy: [this.results.currency]
          });

          this.relatedPartiesArray.forEach((rel) => {
            this.relatedPartiesForm.patchValue({
              id: rel.id,
              relPartyCustomerCode: rel.relPartyCustomerCode,
              relPartyCustomerName: rel.relPartyCustomerName,
              relPartyRelationType: rel.relPartyRelationType,
              relPartyPostalAddress: rel.relPartyPostalAddress
            });
            this.r.push(this.fb.group(
              this.relatedPartiesForm.value
            ));
          });

          this.nomineesArray.forEach((noms) => {
            this.nomineesForm.patchValue({
              id: noms.id,
              dob: noms.dob,
              emailAddress: noms.emailAddress,
              firstName: noms.firstName,
              lastName: noms.lastName,
              middleName: noms.middleName,
              identificationNo: noms.identificationNo,
              nomineeMinor: noms.nomineeMinor,
              occupation: noms.occupation,
              phone: noms.phone,
              relationship: noms.relationship,
            });
            this.n.push(this.fb.group(
              this.nomineesForm.value
            ));
          });
          this.results.accountDocuments.forEach((element: any) => {
            this.accountDocumentsArray.push(element);
          });
          this.formData.patchValue({
            accountDocuments: this.accountDocumentsArray
          });
          this.getAccountDocuments();
        } else {
          this.loading = false;
          this.notificationAPI.alertWarning(data.message);
          this.router.navigate([`system/savings-account/maintenance`], { skipLocationChange: true });
        }
      }, err => {
        this.error = err;
        this.loading = false;
        this.notificationAPI.alertWarning("SERVER ERRPR: TRY AGAIN LATER");
        this.router.navigate([`system/savings-account/maintenance`], { skipLocationChange: true });
      }
    );
  }
  getPage() {
    this.loading = true;
    if (this.fmData.function_type == 'ADD') {
      this.loading = false;
      this.showAccountCode = false;
      this.showCustomerType = true;
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
        isWithdrawalAllowed: [false, Validators.required],
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
        nominees: new FormArray([]),
        relatedParties: new FormArray([]),
        accountDocuments: [[]],
        savings: [''],
        chrg_calc_crncy: ['', Validators.required]
      });
      this.savingsForm = this.fb.group({
        id: [''],
        sba_maturedDate: [this.maturityDate],
        sba_maturedValue: ['0'],
        sba_monthlyValue: ['0', Validators.required],
        sba_savingPeriod: ['0', Validators.required],
        sba_startDate: [new Date()],
      });
      this.btnColor = 'primary';
      this.btnText = 'SUBMIT';
    } else if (this.fmData.function_type == 'INQUIRE') {
      this.getFunctions();
      this.getSavingsDetailsData();
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
      this.getSavingsDetailsData();
      this.getFunctions();
      this.btnColor = 'accent';
      this.btnText = 'REJECT';
    }
    else if (this.fmData.function_type == 'VERIFY') {
      this.getSavingsDetailsData();
      this.getFunctions();
      this.btnColor = 'primary';
      this.btnText = 'VERIFY';
    }
    else if (this.fmData.function_type == 'ACTIVATE') {
      this.getSavingsDetailsData();
      this.getFunctions();
      this.btnColor = 'primary';
      this.btnText = 'ACTIVATE';
    }
    else if (this.fmData.function_type == 'DELETE') {
      this.getSavingsDetailsData();
      this.getFunctions();
      this.btnColor = 'accent';
      this.btnText = 'DELETE';
    }
  }
  public findInvalidControls() {
    const invalid = [];
    const controls = this.formData.controls;
    for (const name in controls) {
      if (controls[name].invalid) {
        invalid.push(name);
      }
    }
    console.log(invalid);
    // return invalid;
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

    console.log(this.formData.value);
    
    this.submitted = true;
    this.submittedSba = true;
    this.loading = true;
    if (this.fmData.function_type == 'ADD') {
      console.log(this.formData.value);
      if (this.formData.valid) {
        this.formData.controls.savings.setValue(this.savingsForm.value);
        this.accountsAPI.createAccount(this.formData.value).pipe(takeUntil(this.destroy$)).subscribe(
          res => {
            this.loading = false;
            this.results = res;
            if (res.statusCode == 201) {
              this.accountsNotification.alertSuccess(this.results.message);
              this.router.navigate([`system/savings-account/maintenance`], { skipLocationChange: true });
            } else {
              this.notificationAPI.alertWarning(this.results.message);
            }
          },
          err => {
            this.error = err;
            this.loading = false;
            this.notificationAPI.alertWarning("Server Error: !!!");
          }
        )
      }
      else if (!this.formData.valid) {
        this.loading = false;
        this.notificationAPI.alertWarning("SAVINGS FORM DATA INVLID");
      }
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
            this.router.navigate([`system/savings-account/maintenance`], { skipLocationChange: true });
          } else {
            this.loading = false;
            this.getSavingsDetailsData();
            this.notificationAPI.alertWarning(res.message);
          }
        },
        err => {
          this.loading = false;
          this.getSavingsDetailsData();
          this.notificationAPI.alertWarning("Server Error: !!!");
        }
      )
    }
    else if (this.fmData.function_type == 'VERIFY') {
      this.loading = true;
      this.showSearch = false;
      this.Acid = this.accountsdata.acid
      this.params = new HttpParams().set('Acid', this.Acid);

      console.log("this.Acid:: ", this.Acid)

      this.accountsAPI.verifyAccount(this.params).pipe(takeUntil(this.destroy$)).subscribe(
        res => {
          if (res.statusCode == 200) {
            this.loading = false;
            this.accountsNotification.alertSuccess(res.message);
            this.router.navigate([`system/savings-account/maintenance`], { skipLocationChange: true });
          } else {
            this.loading = false;
            this.getSavingsDetailsData();
            this.notificationAPI.alertWarning(res.message);
          }
        },
        err => {
          this.loading = false;
          this.getSavingsDetailsData();
          this.notificationAPI.alertWarning("Server Error: !!!");
        }
      )
    }
    else if (this.fmData.function_type == 'ACTIVATE') {
      this.loading = true;
      this.showSearch = false;
      this.Acid = this.accountsdata.acid
      this.params = new HttpParams().set('Acid', this.Acid);
      this.accountsAPI.activateAccount(this.params).pipe(takeUntil(this.destroy$)).subscribe(
        res => {
          if (res.statusCode == 200) {
            this.accountsNotification.alertSuccess(res.message);
            this.loading = false;
            this.router.navigate([`system/savings-account/maintenance`], { skipLocationChange: true });
          } else {
            this.loading = false;
            this.getSavingsDetailsData();
            this.notificationAPI.alertWarning(res.message);
          }
        },
        err => {
          this.error = err;
          this.loading = false;
          this.getSavingsDetailsData();
          this.notificationAPI.alertWarning("Server Error: !!!");
        }
      )
    }
    if (this.fmData.function_type == 'MODIFY') {
      this.loading = true;
      console.log(this.formData);
      if (this.formData.valid) {
        this.formData.controls.savings.setValue(this.savingsForm.value);
        this.accountsAPI.updateAccounts(this.formData.value).pipe(takeUntil(this.destroy$)).subscribe(
          res => {
            if (res.statusCode == 200) {
              this.loading = false;
              this.accountsNotification.alertSuccess(res.message);
              this.router.navigate([`system/savings-account/maintenance`], { skipLocationChange: true });
            } else {
              this.loading = false;
              this.getSavingsDetailsData();
              this.notificationAPI.alertWarning(res.message);
            }
          },
          err => {
            this.error = err;
            this.loading = false;
            this.getSavingsDetailsData();
            this.notificationAPI.alertWarning("Server Error: !!!");
          }
        )
      }
      else if (this.formData.invalid) {
        this.loading = false;
        this.findInvalidControls();
        this.getSavingsDetailsData();
        this.notificationAPI.alertWarning("SAVINGS FORM DATA INVLID");
      }
    }
    else if (this.fmData.function_type == 'DELETE') {
      if (window.confirm("ARE YOU SURE YOU WANT TO REMOVE THE SAVINGS ACCOUNT? ALL RECORDS WILL BE DELETED!!")) {
        this.loading = true;
        this.id = this.results.id;
        this.deletedBy = this.userName;
        this.params = new HttpParams().set("id", this.id).set("deletedBy", this.deletedBy);
        this.accountsAPI.temporaryDeleteAccount(this.params).pipe(takeUntil(this.destroy$)).subscribe(
          res => {
            this.loading = false;
            this.results = res
            this.notificationAPI.alertSuccess(this.results.message);
            this.router.navigate([`system/savings-account/maintenance`], { skipLocationChange: true });
          },
          err => {
            this.loading = false;
            this.error = err;
            this.getSavingsDetailsData();
            this.notificationAPI.alertWarning("Server Error: !!!");
          }
        )
      } else {
        this.loading = false;
        this.getSavingsDetailsData();
        this.router.navigate([`system/savings-account/data/view`], { skipLocationChange: true });
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
          <title>MWAMBA IMARA | ACCOUNT STATEMENT</title>
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
  // onRejectAccount() {
  //   this.loading = false;
  //   this.dialogConfig = new MatDialogConfig();
  //   this.dialogConfig.disableClose = true;
  //   this.dialogConfig.autoFocus = true;
  //   this.dialogConfig.width = "450px";
  //   this.dialogConfig.data = this.accountsdata
  //   const dialogRef = this.dialog.open(RejectAccountComponent, this.dialogConfig);
  //   dialogRef.afterClosed().subscribe(
  //     (_res: any) => {
  //       this.loading = false;
  //       this.getSavingsDetailsData();
  //     });
  // }
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
                  this.router.navigate([`/system/savings-account/maintenance`], { skipLocationChange: true });
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
