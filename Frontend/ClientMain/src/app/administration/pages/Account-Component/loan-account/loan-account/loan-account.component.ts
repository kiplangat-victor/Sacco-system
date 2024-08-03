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
import { LoanRepricingPlans } from 'src/@core/helpers/RepricingPlans';
import { AccountsNotificationService } from 'src/@core/helpers/accounts/accounts-notification.service';
import { occupations } from 'src/@core/helpers/occupation';
import { LoanAccountService } from 'src/app/administration/Service/AccountsService/Loans/LoanAccount/loan-account.service';
import { LoanScheduleService } from 'src/app/administration/Service/AccountsService/Loans/LoanSchedule/loan-schedule.service';
import { AccountsService } from 'src/app/administration/Service/AccountsService/accounts/accounts.service';
import { AccountStatementService } from 'src/app/administration/Service/transaction/account-statement.service';
import { environment } from 'src/environments/environment';
import { GroupMembershipDetailsComponent } from '../../../MembershipComponent/GroupMembership/group-membership-details/group-membership-details.component';
// import { MemberDetailsComponent } from '../../../MembershipComponent/Membership/member-details/member-details.component';
import { UniversalMembershipLookUpComponent } from '../../../MembershipComponent/universal-membership-look-up/universal-membership-look-up.component';
import { EventIdLookupComponent } from '../../../SystemConfigurations/ChargesParams/event-id/event-id-lookup/event-id-lookup.component';
import { MisSectorService } from '../../../SystemConfigurations/GlobalParams/mis-sector/mis-sector.service';
import { CollateralLookupComponent } from '../../../collateral-limits/collateral/collateral-lookup/collateral-lookup.component';
import { ReportService } from '../../../reports/report.service';
import { AccountDoumentsComponent } from '../../account-douments/account-douments.component';
import { AccountUsersComponent } from '../../account-users/account-users.component';
import { AccountsApprovalComponent } from '../../accounts-approval/accounts-approval.component';
import { OfficeAccountsLookUpsComponent } from '../../office-account/office-accounts-look-ups/office-accounts-look-ups.component';
import { RepaymentAccountsComponent } from '../../repayment-accounts/repayment-accounts.component';
import { relationType } from '../../savings-account/relation-types';
import { relationships } from '../../savings-account/relationship';
import { LoansProductLookUpComponent } from './../../../ProductModule/loans-product/loans-product-look-up/loans-product-look-up.component';
import { CollateralDialogComponent } from './collateral-dialog/collateral-dialog.component';
import { CurrencyLookupComponent } from '../../../SystemConfigurations/GlobalParams/currency-config/currency-lookup/currency-lookup.component';

@Component({
  selector: 'app-loan-account',
  templateUrl: './loan-account.component.html',
  styleUrls: ['./loan-account.component.scss']
})
export class LoanAccountComponent implements OnInit, OnDestroy {
  accountDocumentsColumns: string[] = [
    "index",
    "documentTitle",
    'view',
    "actions",
  ];

  guarantoColumns: string[] = [
    'index',
    'guarantorType',
    'guarantorCustomerCode',
    'loanSeries',
    'guaranteeAmount',
    'actions'
  ];
  collateralColumns: string[] = [
    'index',
    'collateralSerial',
    'collateralName',
    'collateralType',
    'collateralValue',
    'actions'
  ];
  loanFeesColumns: string[] = [
    'index',
    'chargeCollectionAccount',
    'eventIdCode',
    'eventTypeDesc',
    'initialAmt',
    'monthlyAmount',
    'nextCollectionDate',
    'actions'
  ];

  guarantorDataSource: MatTableDataSource<any>;
  @ViewChild("guarantorPaginator") guarantorPaginator!: MatPaginator;
  @ViewChild(MatSort) guarantorSort!: MatSort;
  collateralDataSource: MatTableDataSource<any>;
  @ViewChild("collateralPaginator") collateralPaginator!: MatPaginator;
  @ViewChild(MatSort) collateralSort!: MatSort;
  loanFeesDataSource: MatTableDataSource<any>;
  @ViewChild("loanFeesPaginator") loanFeesPaginator!: MatPaginator;
  @ViewChild(MatSort) loanFeesSort!: MatSort;
  accountDocumentsDataSource: MatTableDataSource<any>;
  @ViewChild("accountDocumentsPaginator") accountDocumentsPaginator!: MatPaginator;
  @ViewChild(MatSort) accountDocumentsSort!: MatSort;
  accountDocumentsForm!: FormGroup;
  accountDocumentsArray: any[] = [];
  index: number;
  guarantorsForm!: FormGroup;
  guarantorsArray: any[] = [];
  collateralsForm!: FormGroup;
  collateralsArray: any[] = [];
  loanFeesForm!: FormGroup;
  loanFeesArray: any[] = [];
  editButton: boolean = false;
  addButton: boolean = true;
  disableAddButton: boolean = false;
  disableActions: boolean = false;
  guaranteeAmount = 0.00;

  loanInterestMethodArray: any = [
    {
      value: "01",
      name: "FLAT/FIXED RATE"
    },
    {
      value: "02",
      name: "REDUCING BALANCE"
    }
  ];
  imageTypeArray: any = [
    'PASSPORT', 'SIGNATURE'
  ];
  documentsTypeArray: any=['LOAN FORM', 'ID','GUARANTORS','TAX CERTIFICATE'

  ]
  guarantorTypesArray: any[] = ["DEPOSITS", "SALARY"];
  despatch_mode_array: any = [
    'POST', 'E-MAIL', 'COURIER', 'COLLECT IN PERSON'
  ];
  documentTypeArray: any = [
    'LOAN APPLICATION FORM','INDEMNITY FORM'
  ];
  statementFreqArray: any = [
    'DAILY', 'WEEKLY', 'MONTHLY', 'YEARLY'
  ];
  loanFrequency: any = ['MONTHS', 'YEARS'];
  accountStatusArray: any = [
    'ACTIVE', 'DORMANT', 'NOT-ACTIVE'
  ];
  aaplicationStatusAr: any = [
    'PENDING', 'VERIFIED'
  ];
  demandTypes: any = [
    'PERSONAL LOANS', 'STUDENT LOANS', 'MORTGAGE LOANS', 'AUTO LOANS', 'PAYDAY LOANS'
  ];
  loanTypes: any = ['NORMAL LOAN', 'NORMAL LOAN PLUS', 'NORMAL LOAN SELF GUARANTEE', 'SALARY ADVANCE LOAN', 'SALARY OVERDRAFT', 'EMPLOYEES STAFF LOAN'];
  loanStatus: any = ['PAID', 'NOT-PAID', 'PERFORMING']
  interestMethodArray: any = ['FIXED RATE', 'REDUCING BALANCE'];
  loan_operations: any = ['ANY TO SIGN', 'ALL TO SIGN', 'EITHER TO SIGN', 'INDIVIDUAL TO SIGN'];
  accountOwnershipArray: any = ['SINGLE-ACCOUNT', 'JOINT-ACCOUNT'];
  sectorData: any;
  subSectorData: any;
  subSectors: any;
  currencyData: any;
  customer_lookup: any;
  results: any;
  error: any;
  deletedBy: any;
  id: any;
  loanCollateralsDetails: any;
  signatureImage: any;
  // isEnabled = false;
  isEnabled = true;
  loanDemands: any;
  arrayIndex: any;
  lookupdata: any;
  glSubheads: any;
  interestData: any;
  interestRes: any;
  loading = false;
  fmData: any;
  fetchData: any;
  sba_scheme_code: any;
  lookupData: any;
  glCode: any;
  branchCode: any;
  loanFees: any;
  showAccountCode = false;
  showSearch = true;
  Acid: any;
  onHiden = false;
  params: any;
  branchSolCode: any;
  showPostedBy = false;
  showFunctionType = true;
  showCustomerType = true;
  currentUser: any;
  userName: any;
  loanDetails: any;
  loanDocuments: any;
  onShowUpdateButton = false;
  showTableOperation = true;
  showButtons = true;
  onShowAddButton = true;
  onShowLoanDemanForm = true;
  showloanFeesForm = true;
  onShowGuarantors = true;
  onShowDocumentForm = true;
  demandDocument: any;
  isSubmitting = false;
  documentElement: any;
  mainSectorData: any;
  laa_scheme_code: any;
  onShowAccountStatement = false;
  relationship: any;
  relationtypes: any;
  occupations: any;
  onShowResults = false;
  repricing_plans: any;
  acid: any;
  daysAhead: any;
  submittedCtrl = false;
  customerType: any;
  loanSchedules: any;
  showGeneralDetalsTab = true;
  showSpecificDetailsTab = true;
  showDetailsTab = true;
  showGurantorsTab = true;
  showGuarantorsForm = true;
  showCollateralsTab = true;
  showProductFeesTab = false;
  showCollateralsForm = true;
  showLoanDocumentsTab = true;
  showLoanSchedularTab = true;
  showScheduleAmortization = false
  showLoanDemandsTab = false;
  onCheckedAccountStatements = false;
  onShowAccountOwnership = false;
  onShowAccountOwneshipType = false;
  showLoanBookingsTab = false;
  showLoanAccrualTab = false;
  showLoanDemandEvents = false;
  accrualInfor: any;
  code: any;
  freqId: any;
  freqPeriod: any;
  interestRate: any;
  numberOfInstallments: any;
  principalAmount: any;
  startDate: any;
  interestSchedule: any;
  demandInfor: any;
  bookingInfor: any;
  interestScheduleCalculated: any;
  showTriggeredAccrual = false;
  showAllTriggeredAccrual = false;
  showTriggeredLoanDemands = false;
  showAllTriggeredLoanDemands = false;
  showTriggeredBookings = false;
  showAllNotBookedLoans = false;
  showAllTriggeredBookings = false;
  onShowInterestTable = false;
  showProductFeeTab = true;
  showLoanFeesTab = true;
  onShowCloseButton = false;
  showDemandSatisfaction = false;
  showSatisfiedLoanDemands = false;
  showAllSatisfiedLoanDemands = false;
  accrualParams: any;
  bookingsParams: any;
  loanDemandsParams: any;
  demandRes: any;
  bookingRes: any;
  principalValue: any
  accrualRes: any;
  onShowDatePicker = true;
  onShowOpeningDate = false;
  loanParams: HttpParams;
  resdata: any;
  imageSrc: string;
  productCode: any;
  feeParams: any;
  demandsparams: HttpParams;
  dataRes: any;
  sdata: any;
  fees: any;
  onShowNextRepaymentDate = false;
  today = new Date();
  nextDate = new Date(new Date().setDate(this.today.getDate() + 30));
  submitted = false;
  subHeadLookupdata: any;
  subHeadLookupFound: any;
  laaWithdrawals: any;
  onShowLoanInterestCalculator = false;
  allDemands: any;
  onShowLoanInterestTable = false;
  vrdata: any;
  submittedDocs = false;
  onShowWarning = true;
  onShowAccountStatus = false;
  onShowAccountBalance = false;
  allAccrualRes: any;
  accrualData: any;
  allBookingRes: any;
  bookingData: any;
  allDemandsRes: any;
  demandsData: any;
  amount: any;
  productFees: any;
  feeRes: any;
  satisfiedRes: any;
  satisfiedData: any;
  interstRate: any;
  no_installment: any;
  frequency: any;
  freq_period: any;
  loan_interestRate: any;
  frequency_id: any;
  submittedGntr = false;
  frequencyId_format: any;
  onShowUploadedDocuments = false;
  onShowLonsDocuments = true;
  onShowEditIcon = false;
  documentFile: any;
  document_file: any;
  collateralElement: any;
  uploadedDocuments: any[];
  redParams: HttpParams;
  loanCategory: any;
  loanSpecificDetails: any;
  onShowFixedInterestBtn = false;
  onShowReducingRateBtn = false;
  onShowLoanDocumentsTab = true;
  onShowAccountImages = true;
  onShowUploadedImages = false;
  onShowImageDivider = false;
  displayBackButton = true;
  onShowImagesForm = true;
  imageFile: any;
  account_statement: any;
  onShowAccountStatementTab = false;
  onShowAccountStatementDateParams = false;
  fromdate: any;
  todate: any;
  interestValue: any;
  notBooked: any;
  total_bookings: any;
  accountDocuments: any;
  onShowSelect = true;
  isHiddenInput = false;
  hideBtn = false;
  btnColor: any;
  btnText: any;
  destroy$: Subject<boolean> = new Subject<boolean>();
  collateralsTotal = 0.00;
  collateralValue = 0.00;
  guaranteedAmount = 0.00;
  totaGuaranteeAmount = 0.00;
  totalCollateralsAndGuarantors = 0.00;
  dialogConfig: any;
  collateralCode: any;
  showNextCollectionDate: boolean = false;
  monthlyAmount: 0.00;
  logolink: string = `${environment.reportsAPI}/api/v1/dynamic/saccologo`;
  saccoNamelink: any;
  customer_account_code: any;
  showCurrentRate: boolean = false;
  customer_account_id: any;
  onMemberDetailsLookup: boolean =false;
  function_type: any;
  account_code: any;
  account_type: any;
  guarantorType: any;
  showApprovalbtn: boolean = false;
  accountsdata: any;
  displayApprovalBtn: boolean = false;
  hideRejectBtn: boolean = false;
  btnRejectColor: any;
  rejectBtnText: any;
  rejectionData: any;
  disableViewAction: boolean = true;
  pausedDemandsFlag: boolean;
  pausedSatisfactionFlag: boolean;
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
    private loanInterestApi: LoanScheduleService,
    private statementAPI: AccountStatementService,
    private productInterestAPI: LoanAccountService,
    private accountsNotification: AccountsNotificationService
  ) {
    this.currentUser = JSON.parse(localStorage.getItem('auth-user'));
    this.fmData = this.router.getCurrentNavigation().extras.queryParams;
    this.function_type = this.fmData.formData.function_type;
    this.account_code = this.fmData.formData.account_code;
    this.account_type = this.fmData.formData.account_type;
    if (this.fmData.formData.backBtn == 'APPROVAL') {
      this.displayBackButton = false;
      this.displayApprovalBtn = true;
    }
  }
  ngOnDestroy(): void {
    this.destroy$.next(true);
    this.destroy$.complete();
  }
  ngOnInit(): void {
    this.getSaccoName();
    this.getPage();
    this.initialiseAccountDocumentsForm();
    this.initialiseguarantorsForm();
    this.initialiseCollateralForm();
    this.initialiseLoanFeesForm();
    this.getRelationships();
    this.getRelationTypes();
    this.getOccupations();
    this.getLoanRepricingPlans();
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
    chrg_calc_crncy: ['', Validators.required],
    id: [''],
    acid: [''],
    entityId: [''],
    customerType: [''],
    accountBalance: [''],
    accountManager: ['', Validators.required],
    accountName: [''],
    accountOwnership: ['SINGLE ACCOUNT'],
    checkedJointAccount: [''],
    isWithdrawalAllowed: ['TRUE'],
    accountStatus: [''],
    operationMode: [''],
    accountType: [''],
    cashExceptionCr: [''],
    cashExceptionLimitDr: [''],
    currency: ['UGX'],
    customerCode: ['', Validators.required],
    managerCode: ['', Validators.required],
    dispatchMode: [''],
    glCode: [''],
    glSubhead: ['', Validators.required],
    productCode: ['', Validators.required],
    misSectorCode: ['', Validators.required],
    misSubSectorCode: ['', Validators.required],
    lienAmount: [''],
    postedTime: [new Date()],
    openingDate: [new Date()],
    solCode: [''],
    transferExceptionLimitCr: [''],
    transferExceptionLimitDr: [''],
    accountStatement: [''],
    statementFreq: [''],
    transactionPhone: [''],
    loan: [''],
    accountDocuments: [[]]    
  });
  installmentStartDate = new Date(new Date().setMonth(new Date().getMonth()+1));
  loansForm: FormGroup = this.fb.group({
    sn: [''],
    collectInterest: ['TRUE'],
    collateral: [''],
    collateralsTotal: [''],
    currentRate: [''],
    loanType: ['', Validators.required],
    interestCalculationMethod: [''],
    loanStatus: [''],
    feesAmount: [''],
    guarantorsTotal: [''],
    principalAmount: ['', Validators.required],
    netAmount: [''],
    disbursementAmount: ['', Validators.required],
    interestDemandAmount: [''],
    principalDemandAmount: [''],
    sumPrincipalDemand: [''],
    repaymentPeriodId: [''],
    repaymentPeriod: ['', Validators.required],
    nextRepaymentDate: [new Date()],
    maturityDate: [new Date()],
    disbursementDate: [new Date()],
    overFlowAmount: [''],
    demandCarryForward: [''],
    operativeAcountId: ['',  Validators.required],
    disbursmentAccount: ['',  Validators.required],
    interestRate: [''],
    interestPreferential: [0],
    displayPeriod: [''],
    numberOfInstallments: ['', Validators.required],
    frequencyId: ['', Validators.required],
    frequencyPeriod: ['1', Validators.required],
    installmentStartDate: [this.installmentStartDate, Validators.required],
    installmentAmount: [''],
    outStandingPrincipal: [''],
    outStandingInterest: [''],
    totalCollateralsAndGuarantors: [''],
    totalLoanBalance: [''],
    loanCollaterals: [[]],
    loanFees: [[]],
    loanGuarantors: [[]],
    chrg_calc_crncy: [''],
  });

  loanSchedulesForm = this.fb.group({
    id: [''],
    startDate: [new Date()],
    installmentNumber: [''],
    installmentDescription: [''],
    installmentAmount: [''],
    principleAmount: [''],
    interestAmount: [''],
    principalOutstanding: [''],
    status: [''],
    chrg_calc_crncy: [''],
  });
  loanInterestCalulatorForm = this.fb.group({
    principalAmount: [''],
    numberOfInstallments: [''],
    freqPeriod: [''],
    freqId: [''],
    interestRate: [''],
    startDate: [this.nextDate],
    chrg_calc_crncy: [''],
  })
  loanProductFeesForm = this.fb.group({
    amount: [''],
    productCode: [''],
    chrg_calc_crncy: [''],
  })


  get f() {
    return this.formData.controls;
  }
  get loan() {
    return this.loansForm.controls;
  }

  get schedule() {
    return this.loan.loanSchedules as FormArray;
  }

  attachCollateral() {
    this.collateralsArray.push(this.collateralsForm.value);
    this.collateralValue = 0.0;
    this.collateralsTotal = 0.00
    for (let i = 0; i < this.collateralsArray.length; i++) {
      this.collateralValue = parseFloat(this.collateralsArray[i].collateralValue);
      this.collateralsTotal += this.collateralValue;
    }
    this.loansForm.controls.collateralsTotal.setValue(this.collateralsTotal);
    this.totalCollateralsAndGuarantors = this.collateralsTotal + this.totaGuaranteeAmount;
    this.loansForm.controls.totalCollateralsAndGuarantors.setValue(this.totalCollateralsAndGuarantors);
    this.resetCollateralForm();
  }

  onOtpDialog(collateralCode: number) {
    this.dialogConfig = new MatDialogConfig();
    this.dialogConfig.disableClose = true;
    this.dialogConfig.autoFocus = true;
    this.dialogConfig.data = this.collateralCode;
    const dialogRef = this.dialog.open(CollateralDialogComponent, this.dialogConfig);
    dialogRef.afterClosed().subscribe(
      {
        next: (
          (res) => {
            if (res.statusCode === 200) {
              this.collateralsArray.push(this.collateralsForm.value);
              this.collateralValue = 0.0;
              this.collateralsTotal = 0.00
              for (let i = 0; i < this.collateralsArray.length; i++) {
                this.collateralValue = parseFloat(this.collateralsArray[i].collateralValue);
                this.collateralsTotal += this.collateralValue;
              }
              this.loansForm.controls.collateralsTotal.setValue(this.collateralsTotal);
              this.totalCollateralsAndGuarantors = this.collateralsTotal + this.totaGuaranteeAmount;
              this.loansForm.controls.totalCollateralsAndGuarantors.setValue(this.totalCollateralsAndGuarantors);
              this.resetCollateralForm();
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
    )
  }
  updateOtpDialog(collateralCode: number) {
    this.dialogConfig = new MatDialogConfig();
    this.dialogConfig.disableClose = true;
    this.dialogConfig.autoFocus = true;
    this.dialogConfig.data = this.collateralCode;
    const dialogRef = this.dialog.open(CollateralDialogComponent, this.dialogConfig);
    dialogRef.afterClosed().subscribe(
      {
        next: (
          (res) => {
            if (res.statusCode === 200) {
              this.collateralsArray[this.index] = this.collateralsForm.value;
              this.loansForm.patchValue({
                loanCollaterals: this.collateralsArray,
              });
              this.resetCollateralForm();

              this.collateralValue = 0.0;
              this.collateralsTotal = 0.00

              for (let i = 0; i < this.collateralsArray.length; i++) {
                this.collateralValue = parseFloat(this.collateralsArray[i].collateralValue);
                this.collateralsTotal += this.collateralValue;
              }
              this.loansForm.controls.collateralsTotal.setValue(this.collateralsTotal);
              this.totalCollateralsAndGuarantors = this.collateralsTotal + this.totaGuaranteeAmount;
              this.loansForm.controls.totalCollateralsAndGuarantors.setValue(this.totalCollateralsAndGuarantors);
              this.resetCollateralForm();


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
    )
  }

  initialiseCollateralForm() {
    this.initCollateralForm();
    this.collateralsForm.controls.collateralName.setValidators([Validators.required]);
    this.collateralsForm.controls.collateralSerial.setValidators([Validators.required]);
    this.collateralsForm.controls.collateralType.setValidators([Validators.required]);
    this.collateralsForm.controls.collateralValue.setValidators([]);
    this.collateralsForm.controls.id.setValidators([]);
    chrg_calc_crncy: [''];
  }
  initCollateralForm() {
    this.collateralsForm = this.fb.group({
      collateralName: [''],
      collateralSerial: [''],
      collateralType: [''],
      collateralValue: [''],
      id: [''],
      chrg_calc_crncy: ['']
    });
  }
  addCollateral() {
    if (this.collateralsForm.valid) {
      this.collateralCode = this.collateralsForm.controls.collateralSerial.value;
      this.attachCollateral();
      // this.onOtpDialog(this.collateralCode);
      // this.params = new HttpParams()
      //   .set('colateralCode', this.collateralsForm.controls.collateralSerial.value);
      // this.loading = true;
      // this.accountsAPI.sendOtp(this.params).pipe(takeUntil(this.destroy$)).subscribe(
      //   {
      //     next: (
      //       (res) => {
      //         if (res.statusCode === 200) {
      //           this.loading = false;
      //           this.onOtpDialog(this.collateralCode);
      //         } else {
      //           this.loading = false;
      //           this.notificationAPI.alertWarning(res.message);
      //         }
      //       }
      //     ),
      //     error: (
      //       (err) => {
      //         this.loading = false;
      //         this.notificationAPI.alertWarning("Server Error: !!");
      //       }
      //     ),
      //     complete: (
      //       () => {

      //       }
      //     )
      //   }
      // )
    } else {
      this.loading = false;
      this.notificationAPI.alertWarning("Guarantor's Form Data is Invalid");
    }
  }
  getCollateral() {
    this.collateralDataSource = new MatTableDataSource(this.collateralsArray);
    this.collateralDataSource.paginator = this.collateralPaginator;
    this.collateralDataSource.sort = this.collateralSort;
  }

  resetCollateralForm() {
    this.loansForm.patchValue({
      loanCollaterals: this.collateralsArray
    });
    this.getCollateral();
    this.initialiseCollateralForm();
  }
  editCollateral(data: any) {
    this.index = this.collateralsArray.indexOf(data);
    this.editButton = true;
    this.addButton = false;
    this.collateralsForm.patchValue({
      collateralName: data.collateralName,
      collateralSerial: data.collateralSerial,
      collateralType: data.collateralType,
      collateralValue: data.collateralValue,
      id: data.id
    });
  }
  updateCollateral() {
    this.editButton = false;
    this.addButton = true;
    if (this.collateralsForm.valid) {
      this.collateralCode = this.collateralsForm.controls.collateralSerial.value;
      this.params = new HttpParams()
        .set('colateralCode', this.collateralsForm.controls.collateralSerial.value);
      this.loading = true;
      this.accountsAPI.sendOtp(this.params).pipe(takeUntil(this.destroy$)).subscribe(
        {
          next: (
            (res) => {
              if (res.statusCode === 200) {
                this.loading = false;
                this.updateOtpDialog(this.collateralCode);
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
      )
    } else {
      this.loading = false;
      this.notificationAPI.alertWarning("Guarantor's Form Data is Invalid");
    }
  }
  deleteCollateral(data: any) {
    let deleteIndex = this.collateralsArray.indexOf(data);
    this.collateralsArray.splice(deleteIndex, 1);
    this.resetCollateralForm();
  }
  collateralFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.collateralDataSource.filter = filterValue.trim().toLowerCase();
    if (this.collateralDataSource.paginator) {
      this.collateralDataSource.paginator.firstPage();
    }
  }
  monthlyAmountKeyUp(event: any) {
    this.monthlyAmount = event.target.value;
    this.monthlyAmount = this.loanFeesForm.controls.monthlyAmount.value;
    if (this.monthlyAmount > 1) {
      this.showNextCollectionDate = true;
    } else if (this.monthlyAmount < 1) {
      this.showNextCollectionDate = false;
    }
  }
  initialiseLoanFeesForm() {
    this.initLoanFeesForm();
    this.loanFeesForm.controls.chargeCollectionAccount.setValidators([Validators.required]);
    this.loanFeesForm.controls.closedFlag.setValidators([]);
    this.loanFeesForm.controls.eventIdCode.setValidators([Validators.required]);
    this.loanFeesForm.controls.eventTypeDesc.setValidators([Validators.required]);
    this.loanFeesForm.controls.id.setValidators([]);
    this.loanFeesForm.controls.initialAmt.setValidators([Validators.required]);
    this.loanFeesForm.controls.monthlyAmount.setValidators([]);
    this.loanFeesForm.controls.nextCollectionDate.setValidators([]);
    this.loanFeesForm.controls.paidFlag.setValidators([]);
    this.loanFeesForm.controls.recurEventIdCode.setValidators([]);
    chrg_calc_crncy: [''];
  }
  initLoanFeesForm() {
    this.loanFeesForm = this.fb.group({
      chargeCollectionAccount: [''],
      closedFlag: [''],
      eventIdCode: [''],
      eventTypeDesc: [''],
      id: [''],
      initialAmt: [''],
      monthlyAmount: [''],
      nextCollectionDate: [new Date()],
      paidFlag: [''],
      recurEventIdCode: [''],
      chrg_calc_crncy: ['']
    });
  }
  addLoanFees() {
    if (this.loanFeesForm.valid) {
      this.loanFeesArray.push(this.loanFeesForm.value);
      this.resetLoanFeesForm();
    }
  }
  getLoanFees() {
    this.loanFeesDataSource = new MatTableDataSource(this.loanFeesArray);
    this.loanFeesDataSource.paginator = this.loanFeesPaginator;
    this.loanFeesDataSource.sort = this.loanFeesSort;
  }

  resetLoanFeesForm() {
    this.loansForm.patchValue({
      loanFees: this.loanFeesArray
    });
    this.getLoanFees();
    this.initialiseLoanFeesForm();
  }
  editLoanFees(data: any) {
    this.index = this.loanFeesArray.indexOf(data);
    this.editButton = true;
    this.addButton = false;
    this.loanFeesForm.patchValue({
      chargeCollectionAccount: data.chargeCollectionAccount,
      closedFlag: data.closedFlag,
      eventIdCode: data.eventIdCode,
      eventTypeDesc: data.eventTypeDesc,
      id: data.id,
      initialAmt: data.initialAmt,
      monthlyAmount: data.monthlyAmount,
      nextCollectionDate: data.nextCollectionDate,
      paidFlag: data.paidFlag,
      recurEventIdCode: data.recurEventIdCode
    });
  }
  updateLoanFees() {
    this.editButton = false;
    this.addButton = true;
    this.loanFeesArray[this.index] = this.loanFeesForm.value;
    this.loansForm.patchValue({
      loanFees: this.loanFeesArray
    });
    this.resetLoanFeesForm();
  }
  deleteLoanFees(data: any) {
    let deleteIndex = this.loanFeesArray.indexOf(data);
    this.loanFeesArray.splice(deleteIndex, 1);
    this.loansForm.patchValue({
      loanFees: this.loanFeesArray
    });
    this.resetLoanFeesForm();
  }
  loanFeesFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.loanFeesDataSource.filter = filterValue.trim().toLowerCase();
    if (this.loanFeesDataSource.paginator) {
      this.loanFeesDataSource.paginator.firstPage();
    }
  }
  eventIdCodeLookup(): void {
    const dialogRef = this.dialog.open(EventIdLookupComponent, {
      width: '40%'
    });
    dialogRef.afterClosed().subscribe(result => {
      this.lookupdata = result.data;
      this.loanFeesForm.controls.eventIdCode.setValue(this.lookupdata.eventIdCode);
      this.loanFeesForm.controls.eventTypeDesc.setValue(this.lookupdata.event_type_desc);
      this.loanFeesForm.controls.recurEventIdCode.setValue(this.lookupdata.eventIdCode);
    });
  }
  chargeCollectionAccount(): void {
    const dialogRef = this.dialog.open(OfficeAccountsLookUpsComponent, {
      width: '45%',
      autoFocus: false,
      maxHeight: '90vh',
    });
    dialogRef.afterClosed().subscribe(result => {
      this.lookupdata = result.data;
      this.loanFeesForm.controls.chargeCollectionAccount.setValue(this.lookupdata.acid);
    });
  }
  initialiseAccountDocumentsForm() {
    this.initAccountDocumentsForm();
    this.accountDocumentsForm.controls.documentImage.setValidators([Validators.required]);
    this.accountDocumentsForm.controls.documentTitle.setValidators([Validators.required]);
    this.accountDocumentsForm.controls.sn.setValidators([]);
    chrg_calc_crncy: [''];
  }
  initAccountDocumentsForm() {
    this.accountDocumentsForm = this.fb.group({
      documentImage: [""],
      documentTitle: [""],
      sn: [""],
      chrg_calc_crncy: [''],
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

  initguarantorsForm() {
    this.guarantorsForm = this.fb.group({
      id: [''],
      guarantorCustomerCode: [''],
      guarantorType: [''],
      loanSeries: [''],
      guaranteeAmount: [''],
      chrg_calc_crncy: ['']
    });
  }

  initialiseguarantorsForm() {
    this.initguarantorsForm();
    this.guarantorsForm.controls.guarantorCustomerCode.setValidators([Validators.required]);
    this.guarantorsForm.controls.guarantorType.setValidators([Validators.required]);
    this.guarantorsForm.controls.loanSeries.setValidators([Validators.required]);
    this.guarantorsForm.controls.guaranteeAmount.setValidators([Validators.required]);
    chrg_calc_crncy: [''];
  }
  addGuarantor() {
    if (this.guarantorsForm.valid) {
      this.guarantorType = this.guarantorsForm.controls.guarantorType.value;
      if (this.guarantorType == 'SALARY') {
        this.guarantorsArray.push(this.guarantorsForm.value);
        for (let i = 0; i < this.guarantorsArray.length; i++) {
          this.guaranteedAmount = parseFloat(this.guarantorsArray[i].guaranteeAmount);
          this.totaGuaranteeAmount += this.guaranteedAmount;
          this.loansForm.controls.guarantorsTotal.setValue(this.totaGuaranteeAmount);
        }
        this.totalCollateralsAndGuarantors = this.collateralsTotal + this.totaGuaranteeAmount;
        this.loansForm.controls.totalCollateralsAndGuarantors.setValue(this.totalCollateralsAndGuarantors);
        this.resetguarantorsForm();
      } else if (this.guarantorType == 'DEPOSITS') {
        this.params = new HttpParams()
          .set('amount', this.guarantorsForm.controls.guaranteeAmount.value)
          .set('customerCode', this.guarantorsForm.controls.guarantorCustomerCode.value);
        this.loading = true;
        this.loanInterestApi.validateGuarantor(this.params).pipe(takeUntil(this.destroy$)).subscribe(
          {
            next: (
              (res) => {
                if (res.statusCode === 200) {
                  this.loading = false;
                  this.guarantorsArray.push(this.guarantorsForm.value);
                  for (let i = 0; i < this.guarantorsArray.length; i++) {
                    this.guaranteedAmount = parseFloat(this.guarantorsArray[i].guaranteeAmount);
                    this.totaGuaranteeAmount += this.guaranteedAmount;
                    this.loansForm.controls.guarantorsTotal.setValue(this.totaGuaranteeAmount);
                  }
                  this.totalCollateralsAndGuarantors = this.collateralsTotal + this.totaGuaranteeAmount;
                  this.loansForm.controls.totalCollateralsAndGuarantors.setValue(this.totalCollateralsAndGuarantors);
                  this.resetguarantorsForm();
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
        )
      }

    } else {
      this.loading = false;
      this.notificationAPI.alertWarning("Guarantor's Form Data is Invalid");
    }
  }
  getGuarantors() {
    this.guarantorDataSource = new MatTableDataSource(this.guarantorsArray);
    this.guarantorDataSource.paginator = this.guarantorPaginator;
    this.guarantorDataSource.sort = this.guarantorSort;
  }

  resetguarantorsForm() {
    this.loansForm.patchValue({
      loanGuarantors: this.guarantorsArray
    });
    this.getGuarantors();
    this.initialiseguarantorsForm();
  }
  editItem(data: any) {
    this.index = this.guarantorsArray.indexOf(data);
    this.editButton = true;
    this.addButton = false;
    this.guarantorsForm.patchValue({
      id: data.id,
      guarantorCustomerCode: data.guarantorCustomerCode,
      guarantorType: data.guarantorType,
      loanSeries: data.loanSeries,
      guaranteeAmount: data.guaranteeAmount
    });
  }
  updateGuarantor() {
    this.editButton = false;
    this.addButton = true;
    this.guarantorType = this.guarantorsForm.controls.guarantorType.value;
    if (this.guarantorType == 'SALARY') {
      this.guarantorsArray[this.index] = this.guarantorsForm.value;
      this.loansForm.patchValue({
        loanGuarantors: this.guarantorsArray
      });
      this.resetguarantorsForm();
    } else if (this.guarantorType == 'DEPOSITS') {
      this.params = new HttpParams()
        .set('amount', this.guarantorsForm.controls.guaranteeAmount.value)
        .set('customerCode', this.guarantorsForm.controls.guarantorCustomerCode.value);
      this.loading = true;
      this.loanInterestApi.validateGuarantor(this.params).pipe(takeUntil(this.destroy$)).subscribe(
        {
          next: (
            (res) => {
              if (res.statusCode === 200) {
                this.loading = false;
                this.guarantorsArray[this.index] = this.guarantorsForm.value;
                this.loansForm.patchValue({
                  loanGuarantors: this.guarantorsArray
                });
                this.resetguarantorsForm();
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
      )
    }
  }
  deleteItem(data: any) {
    let deleteIndex = this.guarantorsArray.indexOf(data);
    this.guarantorsArray.splice(deleteIndex, 1);
    this.resetguarantorsForm();
  }
  guarantorsFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.guarantorDataSource.filter = filterValue.trim().toLowerCase();
    if (this.guarantorDataSource.paginator) {
      this.guarantorDataSource.paginator.firstPage();
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
  getLoanRepricingPlans() {
    this.repricing_plans = LoanRepricingPlans;
  }
  onCheckedAccountStatement(event: MatCheckboxChange): void {
    if (event.checked) {
      this.onShowAccountStatement = true;
    } else if (!event.checked) {
      this.onShowAccountStatement = false;
    }
  }
  getSectorCode() {
    this.sectorAPI.find().subscribe(
      (res) => {
        this.mainSectorData = res.entity;
      }
    )
  }
  onSectorCodeChange(event: any) {
    this.sectorAPI.findByCode(event.target.value).pipe(takeUntil(this.destroy$)).subscribe(
      (res) => {
        this.subSectorData = this.mainSectorData.filter(ev => ev.misCode == event.target.value)[0].missubsectors;
      }
    )
  }

  onKeyUp(event: any) {
    this.principalValue = event.target.value;
    this.loanProductFeesForm.controls.amount.setValue(this.principalValue);
    this.loanProductFeesForm.controls.productCode.setValue(this.formData.get("productCode").value);
    this.loanInterestCalulatorForm.controls.principalAmount.setValue(this.principalValue);
    this.getInteresRate();
    this.calculateProductFees();
  }

  getInteresRate() {
    this.params = new HttpParams()
      .set("amount", this.loansForm.controls.principalAmount.value)
      .set("productCode", this.formData.controls.productCode.value);
    this.productInterestAPI.getInterest(this.params).pipe(takeUntil(this.destroy$)).subscribe(
      (res) => {
        this.results = res;
        this.interestRes = this.results;
        this.interestData = this.interestRes.entity;
        this.interestValue = this.interestData;
        this.loansForm.controls.interestRate.setValue(this.interestValue.rate);
        this.loansForm.controls.interestCalculationMethod.setValue(this.interestValue.calculationMethod);
        this.loanInterestCalulatorForm.controls.interestRate.setValue(this.interestValue.rate);
        this.loansForm.controls.displayPeriod.setValue(this.interestValue.interestPeriod);
        if (this.interestValue.calculationMethod == 'Flat_Rate') {
          this.onShowLoanInterestCalculator = true;
          this.onShowFixedInterestBtn = true;
          this.onShowReducingRateBtn = false;
          this.loanInterestCalulatorForm.disable();
        } else if (this.interestValue.calculationMethod == 'Reducing_Balance') {
          this.onShowLoanInterestCalculator = true;
          this.onShowFixedInterestBtn = false;
          this.onShowReducingRateBtn = true;
          this.loanInterestCalulatorForm.disable();
          chrg_calc_crncy: [''];
        }
      }
    )
  }
  onInterestKeyUp(event: any) {
    this.loan_interestRate = event.target.value;
    this.loanInterestCalulatorForm.controls.interestRate.setValue(this.loan_interestRate)
  }
  // chrgCalcCrncyLookup(): void {
  //   const dialogRef = this.dialog.open(CurrencyLookupComponent, {});
  //   dialogRef.afterClosed().subscribe((result) => {
  //      this.chrg_calc_crncy = result.data.ccy;
  //      //this.chrg_calc_crncy = result.data.ccy_name;
  //     this.formData.controls.chrg_calc_crncy.setValue(result.data.ccy);
  //   });
  // }
  onInstallmentsKeyUp(event: any) {
    this.no_installment = event.target.value;
    this.loanInterestCalulatorForm.controls.numberOfInstallments.setValue(this.no_installment);
  }
  onFrequencyIdChange(event: any) {
    this.frequencyId_format = event.target.value;
    this.loanInterestCalulatorForm.controls.freqId.setValue(this.frequencyId_format);
  }
  onFreqPeriodKeyUp(event: any) {
    this.freq_period = event.target.value;
    this.loanInterestCalulatorForm.controls.freqPeriod.setValue(this.freq_period);
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
      width: '60%',
      autoFocus: false,
      maxHeight: '90vh'
    });
    dialogRef.afterClosed().pipe(takeUntil(this.destroy$)).subscribe(result => {
      this.customer_lookup = result.data;
      this.formData.controls.accountManager.setValue(this.customer_lookup.firstName + " " + this.customer_lookup.lastName);
      this.formData.controls.managerCode.setValue(this.customer_lookup.entityId + this.customer_lookup.solCode);
    });
  }
  guarantorCodeLookup(): void {
    const dialogRef = this.dialog.open(UniversalMembershipLookUpComponent, {
      width: '60%'
    });
    dialogRef.afterClosed().pipe(takeUntil(this.destroy$)).subscribe(result => {
      this.customer_lookup = result.data;
      this.guarantorsForm.controls.guarantorCustomerCode.setValue(this.customer_lookup.customerCode);
      this.guarantorsForm.controls.loanSeries.setValue(this.customer_lookup.customerName);
    });
  }
  collateralCodeLookup(): void {
    const dialogRef = this.dialog.open(CollateralLookupComponent, {
      width: '45%',
      autoFocus: false,
      maxHeight: '90vh'
    });
    dialogRef.afterClosed().pipe(takeUntil(this.destroy$)).subscribe(result => {
      this.lookupData = result.data;
      this.collateralsForm.controls.collateralSerial.setValue(this.lookupData.collateralCode);
      this.collateralsForm.controls.collateralValue.setValue(this.lookupData.collateralValue);
      this.collateralsForm.controls.collateralName.setValue(this.lookupData.description);
      this.collateralsForm.controls.collateralType.setValue(this.lookupData.collateralType);
    });
  }
  LoanProductCodeLookUp(): void {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.disableClose = false;
    dialogConfig.autoFocus = true;
    dialogConfig.width = "600px";
    dialogConfig.data = {
      data: "",
    };
    const dialogRef = this.dialog.open(LoansProductLookUpComponent, dialogConfig);
    dialogRef.afterClosed().pipe(takeUntil(this.destroy$)).subscribe((result) => {
      this.lookupdata = result.data;
      this.subHeadLookupdata = this.lookupdata.glsubheads;
      this.subHeadLookupFound = this.subHeadLookupdata[0].gl_subhead;
      this.laaWithdrawals = this.lookupdata.laaDetails;
      this.sba_scheme_code = this.lookupdata.productCode;
      this.loanCategory = this.lookupdata.productCodeDesc;
      this.loansForm.controls.loanType.setValue(this.loanCategory);
      this.formData.controls.productCode.setValue(this.sba_scheme_code);
      this.loanProductFeesForm.controls.productCode.setValue(this.sba_scheme_code);
      this.formData.controls.glSubhead.setValue(this.subHeadLookupFound);
      this.formData.controls.transferExceptionLimitDr.setValue(999999999);
      this.formData.controls.transferExceptionLimitCr.setValue(999999999);
      this.formData.controls.cashExceptionLimitDr.setValue(999999999);
      this.formData.controls.cashExceptionCr.setValue(999999999);
      chrg_calc_crncy: [''];
    });
  }

  operativeAcountIdLookUp(customer_account_code: any) {
    if (this.customer_account_code == null) {
      this.loading = false;
      this.notificationAPI.alertWarning("Member Code Must Be Selected !!");
    } else {
      this.dialogConfig = new MatDialogConfig();
      this.dialogConfig.disableClose = true;
      this.dialogConfig.autoFocus = true;
      this.dialogConfig.data = this.customer_account_code;
      const dialogRef = this.dialog.open(RepaymentAccountsComponent, this.dialogConfig);
      dialogRef.afterClosed().subscribe(
        {
          next: (
            (res) => {
              this.lookupData = res.data;
              this.loansForm.controls.operativeAcountId.setValue(this.lookupData.acid);
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
  disbursmentAccountLookUp(customer_account_code: any) {
    if (this.customer_account_code == null) {
      this.loading = false;
      this.notificationAPI.alertWarning("Member Code Must Be Selected !!");
    } else {
      this.dialogConfig = new MatDialogConfig();
      this.dialogConfig.disableClose = true;
      this.dialogConfig.autoFocus = true;
      this.dialogConfig.data = this.customer_account_code;
      const dialogRef = this.dialog.open(RepaymentAccountsComponent, this.dialogConfig);
      dialogRef.afterClosed().subscribe(
        {
          next: (
            (res) => {
              this.lookupData = res.data;
              this.loansForm.controls.disbursmentAccount.setValue(this.lookupData.acid);
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
      ), Subscription
    }
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
      .set("acid", this.fmData.formData.account_code)
      .set('fromdate', this.datepipe.transform(this.statementForm.controls.fromdate.value, "yyyy-MM-dd"))
      .set('todate', this.datepipe.transform(this.statementForm.controls.todate.value, "yyyy-MM-dd"));
    this.statementAPI.getStatement(this.params).pipe(takeUntil(this.destroy$)).subscribe(
      (res) => {
        this.loading = false;
        this.results = res;
        this.account_statement = this.results.entity;
        this.notificationAPI.alertSuccess(this.results.message);
        this.getLoanDataDetails();
      }, err => {
        this.loading = false;
        this.notificationAPI.alertWarning("Server Error: !!");
      }
    )
  }

  getLoanDataDetails() {
    this.loading = true;
    this.accountsAPI.retrieveAccount(this.fmData.formData.account_code).pipe(takeUntil(this.destroy$)).subscribe(
      {
        next: (
          (res) => {
            if (res.statusCode === 302) {
              this.loading = false;
              this.accountsdata = res.entity;
              if (this.fmData.formData.function_type == 'VERIFY') {
                if (this.accountsdata.verifiedFlag == 'Y') {
                  this.hideRejectBtn = false;
                }
                if (this.accountsdata.verifiedFlag == 'N') {
                  this.btnRejectColor = 'accent';
                  this.rejectBtnText = 'REJECT';
                  this.hideRejectBtn = true;
                }
              }
              this.results = res.entity;
              this.loanDetails = this.results.loan;
              this.loanDemands = this.loanDetails.loanDemands;
              this.loanSchedules = this.loanDetails.loanSchedules;
              this.loansForm = this.fb.group({
                sn: [this.loanDetails.sn],
                collectInterest: [this.loanDetails.collectInterest],
                collateralsTotal: [this.loanDetails.collateralsTotal],
                currentRate: [this.loanDetails.currentRate],
                loanType: [this.loanDetails.loanType],
                loanStatus: [this.loanDetails.loanStatus],
                principalAmount: [this.loanDetails.principalAmount],
                disbursementAmount: [this.loanDetails.disbursementAmount],
                netAmount: [this.loanDetails.netAmount],
                interestCalculationMethod: [this.loanDetails.interestCalculationMethod],
                interestDemandAmount: [this.loanDetails.interestDemandAmount],
                guarantorsTotal: [this.loanDetails.guarantorsTotal],
                principalDemandAmount: [this.loanDetails.principalDemandAmount],
                sumPrincipalDemand: [this.loanDetails.sumPrincipalDemand],
                repaymentPeriodId: [this.loanDetails.repaymentPeriodId],
                repaymentPeriod: [this.loanDetails.repaymentPeriod],
                maturityDate: [this.loanDetails.maturityDate],
                nextRepaymentDate: [this.loanDetails.nextRepaymentDate],
                disbursementDate: [this.loanDetails.disbursementDate],
                overFlowAmount: [this.loanDetails.overFlowAmount],
                demandCarryForward: [this.loanDetails.demandCarryForward],
                operativeAcountId: [this.loanDetails.operativeAcountId],
                disbursmentAccount: [this.loanDetails.disbursmentAccount],
                interestRate: [this.loanDetails.interestRate],
                interestPreferential: [this.loanDetails.interestPreferential],
                numberOfInstallments: [this.loanDetails.numberOfInstallments],
                frequencyId: [this.loanDetails.frequencyId],
                frequencyPeriod: [this.loanDetails.frequencyPeriod],
                installmentStartDate: [this.loanDetails.installmentStartDate],
                installmentAmount: [this.loanDetails.installmentAmount],
                outStandingPrincipal: [this.loanDetails.outStandingPrincipal],
                outStandingInterest: [this.loanDetails.outStandingInterest],
                totalCollateralsAndGuarantors: [this.loanDetails.totalCollateralsAndGuarantors],
                totalLoanBalance: [this.loanDetails.totalLoanBalance],
                loanCollaterals: [[]],
                loanFees: [[]],
                loanGuarantors: [[]],
                chrg_calc_crncy: [this.loanDetails.currency]

              });

              this.formData = this.fb.group({
                id: [this.results.id],
                acid: [this.results.acid],
                accountBalance: [this.results.accountBalance],
                accountManager: [this.results.accountManager],
                accountName: [this.results.accountName],
                accountOwnership: [this.results.accountOwnership],
                accountStatus: [this.results.accountStatus],
                operationMode: [this.results.operationMode],
                checkedJointAccount: [this.results.checkedJointAccount],
                isWithdrawalAllowed: [this.results.isWithdrawalAllowed],
                accountType: [this.results.accountType],
                customerType: [this.results.customerType],
                cashExceptionCr: [this.results.cashExceptionCr],
                cashExceptionLimitDr: [this.results.cashExceptionLimitDr],
                currency: [this.results.currency],
                customerCode: [this.results.customerCode],
                managerCode: [this.results.managerCode],
                lienAmount: [this.results.lienAmount],
                openingDate: [this.results.openingDate],
                referredBy: [this.results.referredBy],
                loan: [''],
                glCode: [this.results.glCode],
                glSubhead: [this.results.glSubhead],
                solCode: [this.results.solCode],
                productCode: [this.results.productCode],
                misSectorCode: [this.results.misSectorCode],
                misSubSectorCode: [this.results.misSubSectorCode],
                transferExceptionLimitCr: [this.results.transferExceptionLimitCr],
                transferExceptionLimitDr: [this.results.transferExceptionLimitDr],
                accountStatement: [this.results.accountStatement],
                statementFreq: [this.results.statementFreq],
                dispatchMode: [this.results.dispatchMode],
                withholdingTax: [this.results.withholdingTax],
                accountDocuments: [[]],
                chrg_calc_crncy: [this.results.currency]

              });
              this.results.loan.loanGuarantors.forEach((element: any) => {
                this.guarantorsArray.push(element);
              });
              this.getGuarantors();
              this.loansForm.patchValue({
                loanGuarantors: this.guarantorsArray
              });
              this.results.loan.loanCollaterals.forEach((element: any) => {
                this.collateralsArray.push(element);
              });
              this.getCollateral();
              this.loansForm.patchValue({
                loanCollaterals: this.collateralsArray
              });
              this.results.loan.loanFees.forEach((element: any) => {
                this.loanFeesArray.push(element);
              });
              this.getLoanFees();
              this.loansForm.patchValue({
                loanFees: this.loanFeesArray
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
              this.notificationAPI.alertWarning("No Loan Data for Account " + this.fmData.formData.account_code + " Found: !!");
              this.router.navigate([`system/loan-account/maintenance`], { skipLocationChange: true });
            }
          }
        ),
        error: (
          (err) => {
            this.loading = false;
            this.notificationAPI.alertWarning("Server Error: !!!")
          }
        ),
        complete: (
          () => {

          }
        )
      }
    ), Subscription;
  }

  getStatementFunctions() {
    this.onShowAccountStatementTab = true;
    this.showLoanDemandsTab = false;
    this.showLoanBookingsTab = false;
    this.showScheduleAmortization = false;
    this.onShowLoanDocumentsTab = false;
    this.showLoanSchedularTab = false;
    this.showLoanFeesTab = false;
    this.showProductFeesTab = false;
    this.showCollateralsTab = false;
    this.showLoanFeesTab = false;
    this.showGurantorsTab = false;
    this.showSpecificDetailsTab = false;
    this.showGeneralDetalsTab = false;
    this.onShowAccountStatementDateParams = true;
    chrg_calc_crncy: [''];
  }

  getFunctions() {
    this.showGeneralDetalsTab = false;
    this.showSpecificDetailsTab = false;
    this.showCollateralsTab = false;
    this.showLoanFeesTab = false;
    this.showLoanDocumentsTab = false;
    this.showLoanDemandsTab = false;
    this.showLoanFeesTab = false;
    this.onShowLoanDocumentsTab = false;
  }
  getModifyFunctions() {
    this.loading = true;
    this.showAccountCode = true;
    this.showPostedBy = true;
    this.showTableOperation = true;
    this.showButtons = true;
    this.isEnabled = true;
    this.onShowEditIcon = true;
    this.onShowResults = true;
    this.onShowLoanDemanForm = true;
    this.onShowGuarantors = true;
    this.showCollateralsForm = true;
    this.onShowDocumentForm = true;
    this.onShowAddButton = false;
    this.showProductFeesTab = false;
    this.onShowUpdateButton = true;
    this.onShowAccountBalance = true;
    this.showLoanSchedularTab = false;
    this.onShowUploadedDocuments = true;
    this.onShowSelect = true;
    this.isHiddenInput = false;
    this.onShowAccountStatus = true;
    this.showCurrentRate = true;
    this.disableViewAction = false;
    chrg_calc_crncy: [''];
  }
  getLoanFunctions() {
    this.loading = true;
    this.showAccountCode = true;
    this.showPostedBy = true;
    this.showCustomerType = false;
    this.onShowResults = true;
    this.showSearch = false;
    this.showTableOperation = false;
    this.showButtons = false;
    this.onShowGuarantors = false;
    this.showCollateralsForm = false;
    this.onShowDocumentForm = false;
    this.onShowLoanDemanForm = false;
    this.onShowAddButton = false;
    this.onShowDatePicker = false;
    this.onShowWarning = false;
    this.onShowAccountImages = false;
    this.onShowImagesForm = false;
    this.onShowImageDivider = true;
    this.onShowUploadedImages = true;
    this.disableActions = true;
    this.disableViewAction = false;
    this.formData.disable();
    this.loansForm.disable();
    this.onShowAccountBalance = true;
    this.showProductFeesTab = false;
    this.showLoanFeesTab = true;
    this.showLoanSchedularTab = false;
    this.onShowNextRepaymentDate = true;
    this.onShowUploadedDocuments = true;
    this.onShowLonsDocuments = false;
    this.onShowLoanDocumentsTab = true;
    this.onShowSelect = true;
    this.isHiddenInput = false;
    this.showGuarantorsForm = false;
    this.showloanFeesForm = false;
    this.onShowAccountStatus = true;
    this.showCurrentRate = true;
    chrg_calc_crncy: [''];
  }
  getPage() {
    if (this.fmData.formData.function_type == 'ADD') {
      this.loading = false;
      this.showSearch = true;
      this.isEnabled = true;
      this.showAccountCode = false;
      this.showDetailsTab = false;
      this.showCustomerType = true;
      this.showFunctionType = true;
      this.formData = this.fb.group({
        id: [''],
        acid: [''],
        entityId: [''],
        customerType: [''],
        accountType: [this.fmData.formData.account_type],
        accountBalance: [''],
        accountManager: [this.currentUser.firstName + " " + this.currentUser.lastName],
        accountName: [''],
        accountOwnership: ['SINGLE ACCOUNT'],
        checkedJointAccount: [''],
        isWithdrawalAllowed: ['TRUE'],
        openingDate: [new Date()],
        solCode: [''],
        accountStatus: [''],
        operationMode: [''],
        cashExceptionCr: [''],
        cashExceptionLimitDr: [''],
        currency: ['UGX'],
        customerCode: ['', Validators.required],
        managerCode: [this.currentUser.memberCode],
        dispatchMode: [''],
        glSubhead: [''],
        glCode: [''],
        productCode: ['', Validators.required],
        misSubSectorCode: ['', Validators.required],
        misSectorCode: ['', Validators.required],
        lienAmount: [''],
        transferExceptionLimitCr: [''],
        transferExceptionLimitDr: [''],
        accountStatement: [''],
        statementFreq: [''],
        transactionPhone: [''],
        loan: [''],
        accountDocuments: [[]],
        chrg_calc_crncy: ['']
      });
      this.loansForm = this.fb.group({
        sn: [''],
        collectInterest: ['TRUE'],
        collateral: [''],
        collateralsTotal: [''],
        currentRate: [''],
        loanType: ['', Validators.required],
        interestCalculationMethod: [''],
        loanStatus: [''],
        feesAmount: [''],
        guarantorsTotal: [''],
        principalAmount: ['', Validators.required],
        netAmount: [''],
        disbursementAmount: ['', Validators.required],
        interestDemandAmount: [''],
        principalDemandAmount: [''],
        sumPrincipalDemand: [''],
        repaymentPeriodId: [''],
        repaymentPeriod: ['', Validators.required],
        nextRepaymentDate: [new Date()],
        maturityDate: [new Date()],
        disbursementDate: [new Date()],
        overFlowAmount: [''],
        demandCarryForward: [''],
        operativeAcountId: [''],
        disbursmentAccount: [''],
        interestRate: [''],
        interestPreferential: [0],
        displayPeriod: [''],
        numberOfInstallments: ['', Validators.required],
        frequencyId: ['', Validators.required],
        frequencyPeriod: ['', Validators.required],
        installmentStartDate: [this.installmentStartDate],
        installmentAmount: [''],
        outStandingPrincipal: [''],
        outStandingInterest: [''],
        totalCollateralsAndGuarantors: [''],
        totalLoanBalance: [''],
        loanCollaterals: [[]],
        loanFees: [[]],
        loanGuarantors: [[]],
        chrg_calc_crncy: ['']
      });
      this.btnColor = 'primary';
      this.btnText = 'SUBMIT';

    } else if (this.fmData.formData.function_type == 'INQUIRE') {
      this.getLoanDataDetails();
      this.getLoanFunctions();
      this.loading = true;
      this.onShowUploadedDocuments = true;
      this.showScheduleAmortization = true;

    }

    else if (this.fmData.formData.function_type == 'ACCRUAL') {
      this.loading = true;
      this.getLoanFunctions();
      this.getLoanDataDetails();;
      this.getFunctions();
      this.showLoanAccrualTab = true;
    }
    else if (this.fmData.formData.function_type == 'BOOKING') {
      this.loading = true;
      this.getLoanFunctions();
      this.getLoanDataDetails();;
      this.getFunctions();
      this.showLoanBookingsTab = true;
    }
    else if (this.fmData.formData.function_type == 'DEMANDS') {
      this.loading = true;
      this.getLoanFunctions();
      this.getLoanDataDetails();;
      this.getFunctions();
      this.showDemandSatisfaction = true;
      this.showLoanDemandsTab = true;
    }
    else if (this.fmData.formData.function_type == 'REJECT') {
      this.getLoanFunctions();
      this.getLoanDataDetails();
      this.btnColor = 'accent';
      this.btnText = 'REJECT';
    }
    else if (this.fmData.formData.function_type == 'VERIFY') {
      this.getLoanFunctions();
      this.getLoanDataDetails();;
      this.btnColor = 'primary';
      this.btnText = 'VERIFY';
    }
    else if (this.fmData.formData.function_type == 'VERIFY') {
      this.getLoanFunctions();
      this.getLoanDataDetails();;
      this.btnColor = 'primary';
      this.btnText = 'VERIFY';
    }
    else if (this.fmData.formData.function_type == 'MODIFY') {
      this.getModifyFunctions();
      this.getLoanDataDetails();;
      this.btnColor = 'primary';
      this.btnText = 'MODIFY';

    } else if (this.fmData.formData.function_type == 'DELETE') {
      this.loading = true;
      this.getLoanFunctions();
      this.getLoanDataDetails();;
      this.btnColor = 'primary';
      this.btnText = 'DELETE';
    }
    else if (this.fmData.formData.function_type == 'DISBURSE') {
      this.getLoanFunctions();
      this.getLoanDataDetails();;
      this.btnColor = 'primary';
      this.btnText = 'DISBURSE';
    }
    else if (this.fmData.formData.function_type == 'DISBURSEMENT') {
      this.getLoanFunctions();
      this.getLoanDataDetails();;
      this.btnColor = 'primary';
      this.btnText = 'DISBURSE';

    }
    else if (this.fmData.formData.function_type == 'STATEMENT') {
      this.loading = false;
      this.getStatementFunctions();
    }
    else if (this.fmData.formData.function_type == 'VERIFICATION') {
      this.getLoanFunctions();
      this.getLoanDataDetails();;
      this.btnColor = 'primary';
      this.btnText = 'VERIFY';
    }
  }
  chrgCalcCrncyLookup(): void {
    const dialogRef = this.dialog.open(CurrencyLookupComponent, {});
    dialogRef.afterClosed().subscribe((result) => {
      //  this.chrg_calc_crncy = result.data.ccy;
      // this.chrg_calc_crncy = result.data.ccy_name;
      this.formData.controls.chrg_calc_crncy.setValue(result.data.ccy);
    });
  }
  onSubmit() {
    this.submitted = true;
    this.formData.controls.loan.setValue(this.loansForm.value);
    if (this.fmData.formData.function_type == 'ADD') {
      this.isSubmitting = true;
      if (this.formData.valid) {
        this.accountsAPI.createAccount(this.formData.value).subscribe(
          res => {
            if (res.statusCode === 201) {
              this.isSubmitting = false
              this.results = res;
              this.accountsNotification.alertSuccess(this.results.message);
              this.router.navigate([`system/loan-account/maintenance`], { skipLocationChange: true });
            } else {
              this.isSubmitting = false
              this.loading = false;
              this.accountsNotification.alertWarning(res.message);
            }
          },
          err => {
            this.error = err;
            this.notificationAPI.alertWarning("Server Error: !!");
          }
        )
      }
      else if (this.formData.invalid) {
        this.isSubmitting = false;
        this.notificationAPI.alertWarning("LOAN ACCOUNT FORM DATA INVALID");
      }
    } else if (this.fmData.formData.function_type == 'MODIFY') {
      this.loading = true;
      this.accountsAPI.updateAccounts(this.formData.value).subscribe(
        res => {
          if (res.statusCode === 200 || res.statusCode === 201) {
            this.loading = false
            this.results = res;
            this.notificationAPI.alertSuccess(this.results.message);
            this.router.navigate([`system/loan-account/maintenance`], { skipLocationChange: true });
          } else {
            this.loading = false
            this.results = res;
            this.notificationAPI.alertWarning(this.results.message);
          }

        },
        err => {
          this.error = err;
          this.notificationAPI.alertWarning("Server Error: !!");
        }
      )
    }
    else if (this.fmData.formData.function_type == 'REJECT') {
      this.loading = true;
      this.showSearch = false;
      this.params = new HttpParams().set('acid', this.fmData.formData.account_code);
      this.accountsAPI.rejectAccount(this.params).pipe(takeUntil(this.destroy$)).subscribe(
        res => {
          if (res.statusCode == 200) {
            this.loading = false;
            this.accountsNotification.alertSuccess(res.message);
            this.router.navigate([`system/loan-account/maintenance`], { skipLocationChange: true });
          } else {
            this.loading = false;
            this.getLoanDataDetails();
            this.notificationAPI.alertWarning(res.message);
          }
        },
        err => {
          this.loading = false;
          this.getLoanDataDetails();
          this.notificationAPI.alertWarning("Server Error: !!!");
        }
      )
    }
    else if (this.fmData.formData.function_type == 'VERIFY') {
      this.loading = true;
      this.showSearch = true;
      this.Acid = this.fmData.formData.account_code;
      this.params = new HttpParams().set('Acid', this.Acid);
      this.accountsAPI.verifyAccount(this.params).subscribe(
        res => {
          if (this.results.statusCode === 200) {
            this.loading = false;
            this.notificationAPI.alertWarning(this.results.message);
            this.router.navigate([`system/loan-account/maintenance`], { skipLocationChange: true });
          } else {
            this.loading = false;
            this.results = res;
            this.notificationAPI.alertSuccess(this.results.message);
            this.router.navigate([`system/loan-account/maintenance`], { skipLocationChange: true });
          }
        },
        err => {
          this.loading = false;
          this.notificationAPI.alertWarning("Server Error: !!");
        }
      )
    }
    else if (this.fmData.formData.function_type == 'DELETE') {
      if (window.confirm("ARE YOU SURE YOU WANT TO REMOVE THE LOAN ACCOUNT? ALL RECORDS WILL BE DELETED!!")) {
        this.loading = true;
        this.id = this.results.id;
        this.deletedBy = this.userName;
        this.params = new HttpParams().set("id", this.id).set("deletedBy", this.deletedBy);
        this.accountsAPI.temporaryDeleteAccount(this.params).subscribe(
          res => {
            this.loading = false;
            this.results = res;
            this.notificationAPI.alertSuccess(this.results.message);
            this.router.navigate([`system/loan-account/maintenance`], { skipLocationChange: true });
          },
          err => {
            this.loading = false;
            this.notificationAPI.alertWarning("Server Error: !!");
          }
        );
      } else {
        this.loading = false;
      }

    }
    else if (this.fmData.formData.function_type == 'DISBURSEMENT') {
      this.loading = true;
      this.acid = this.results.acid;
      this.params = new HttpParams().set("acid", this.acid);
      this.loanInterestApi.disburseLoans(this.params).pipe(takeUntil(this.destroy$)).subscribe(
        res => {
          this.loading = false;
          this.results = res;
          this.accountsNotification.alertSuccess(this.results.message);
          this.router.navigate([`system/loan-disbursement/maintenance`], { skipLocationChange: true });
        },
        err => {
          this.loading = false;
          this.error = err;
          this.notificationAPI.alertWarning("SERVER ERROR!!");
        }
      )
    }
    else if (this.fmData.formData.function_type == 'VERIFICATION') {
      this.triggerLoanDisbursmnent();
    }
  }

  calculateFixedInterest() {
    this.loading = true;
    this.params = new HttpParams()
      .set('freqId', this.loanInterestCalulatorForm.value.freqId)
      .set('freqPeriod', this.loanInterestCalulatorForm.value.freqPeriod)
      .set('interestRate', this.loanInterestCalulatorForm.value.interestRate)
      .set('numberOfInstallments', this.loanInterestCalulatorForm.value.numberOfInstallments)
      .set('principalAmount', this.loanInterestCalulatorForm.value.principalAmount)
      .set('startDate', this.datepipe.transform(this.loanInterestCalulatorForm.controls.startDate.value, "yyyy-MM-dd"));
    this.loanInterestApi.fixInterestBalance(this.params).pipe(takeUntil(this.destroy$)).subscribe(
      (res) => {
        this.loading = false;
        this.results = res;
        this.interestScheduleCalculated = this.results.entity;
        this.onShowResults = true;
        this.onShowLoanInterestTable = true;
        this.onShowCloseButton = true;
      }, (err) => {
        this.loading = false;
        this.notificationAPI.alertWarning("Server Error: !!");
      }
    );
  }
  calculateReducingInterest() {
    this.loading = true;
    this.redParams = new HttpParams()
      .set('freqId', this.loanInterestCalulatorForm.value.freqId)
      .set('freqPeriod', this.loanInterestCalulatorForm.value.freqPeriod)
      .set('interestRate', this.loanInterestCalulatorForm.value.interestRate)
      .set('numberOfInstallments', this.loanInterestCalulatorForm.value.numberOfInstallments)
      .set('principalAmount', this.loanInterestCalulatorForm.value.principalAmount)
      .set('startDate', this.datepipe.transform(this.loanInterestCalulatorForm.controls.startDate.value, "yyyy-MM-dd"));
    this.loanInterestApi.reducingInterestBalance(this.redParams).pipe(takeUntil(this.destroy$)).subscribe(
      (res) => {
        this.loading = false;
        this.results = res;
        this.interestScheduleCalculated = this.results.entity;
        this.onShowResults = true;
        this.onShowCloseButton = true;
        this.onShowLoanInterestTable = true;
      }, (err) => {
        this.loading = false;
        this.notificationAPI.alertWarning("Server Error: !!");
      }
    );
  }
  closeCalculatedInterest() {
    this.onShowLoanInterestTable = false;
  }
  calculateProductFees() {
    this.loading = true;
    this.amount = this.loansForm.controls.principalAmount.value;
    this.productCode = this.formData.controls.productCode.value;
    this.params = new HttpParams()
      .set('amount', this.amount)
      .set('productCode', this.productCode);
    this.loanInterestApi.getLoanFees(this.params).pipe(takeUntil(this.destroy$)).subscribe(
      (res) => {
        console.log("Received fees");
        console.log(res);
        this.loading = false;
        this.results = res;
        this.productFees = this.results.entity;
        this.loanFeesArray = this.productFees;
        this.getLoanFees();
        this.loansForm.patchValue({
          loanFees: this.loanFeesArray
        });
        this.feeRes = this.productFees;
        this.onShowResults = true;
        this.onShowCloseButton = true;
        console.log("After received fees");
        console.log(res);
      }, (err) => {
        this.loading = false;
        this.notificationAPI.alertWarning("Server Error: !!");;
      }
    );
  }

  triggerAccrual() {
    this.loading = true;
    this.acid = this.fmData.formData.account_code;
    this.accrualParams = new HttpParams().set("acid", this.acid);
    this.loanInterestApi.forceAccrual(this.accrualParams).pipe(takeUntil(this.destroy$)).subscribe(
      (res) => {
        this.loading = false;
        this.showTriggeredAccrual = true;
        this.showAllTriggeredAccrual = false;
        this.accrualRes = res;
        this.getAccrualInfo();
        this.onShowResults = true;
        this.notificationAPI.alertSuccess(this.accrualRes.message);
      }, (err) => {
        this.loading = false;
        this.notificationAPI.alertWarning("Server Error: !!");
      }
    )
  }
  triggerAllAccruals() {
    if (window.confirm('ALL LOANS ACCOUNTS WILL BE ACCRUED:- YOU CAN NOT REVERSE ONCE INITIATED!! ARE YOU SURE?')) {
      this.loading = true;
      this.loanInterestApi.accrueAll().pipe(takeUntil(this.destroy$)).subscribe(
        (res) => {
          this.loading = false;
          this.showTriggeredAccrual = false;
          this.showAllTriggeredAccrual = true;
          this.allAccrualRes = res;
          this.accrualData = this.allAccrualRes;
          this.getLoanDataDetails();;
          this.onShowResults = true;
          for (let i = 0; i < this.accrualData.length; i++) {
            this.notificationAPI.alertSuccess(this.accrualData[i].message);
          }
          this.router.navigate([`system/loan-account/data/view`], { skipLocationChange: true });
        }, (err) => {
          this.loading = false;
          this.notificationAPI.alertWarning("Server Error: !!");
          this.router.navigate([`system/loan-account/data/view`], { skipLocationChange: true });
        }
      )
    } else {
      this.router.navigate([`system/loan-account/data/view`], { skipLocationChange: true });
    }

  }
  triggerLoanBookings() {
    this.loading = true;
    this.acid = this.fmData.formData.account_code;
    this.bookingsParams = new HttpParams().set("acid", this.acid);
    this.loanInterestApi.forceLoanBooking(this.bookingsParams).pipe(takeUntil(this.destroy$)).subscribe(
      (res) => {
        this.loading = false;
        this.showTriggeredBookings = true;
        this.showAllTriggeredBookings = false;
        this.bookingRes = res;
        this.getBookingsInfo();
        this.onShowResults = true;
        this.notificationAPI.alertSuccess(this.bookingRes.message);
      }, (err) => {
        this.loading = false;
        this.notificationAPI.alertWarning("Server Error: !!");
      }
    );
  }
  triggerAllLoanBookings() {
    if (window.confirm('ALL LOANS ACCOUNTS WILL BE BOOKED:- YOU CAN NOT REVERSE ONCE INITIATED!! ARE YOU SURE?')) {
      this.loading = true;
      this.loanInterestApi.bookAll().pipe(takeUntil(this.destroy$)).subscribe(
        (res) => {
          this.loading = false;
          this.showTriggeredBookings = false;
          this.showAllTriggeredBookings = true;
          this.allBookingRes = res;
          this.bookingData = this.allBookingRes;
          this.onShowResults = true;
          for (let i = 0; i < this.bookingData.length; i++) {
            this.notificationAPI.alertSuccess(this.bookingData[i].message);
          }
          this.router.navigate([`system/loan-account/data/view`], { skipLocationChange: true });
        }, (err) => {
          this.loading = false;
          this.notificationAPI.alertWarning("Server Error: !!");
          this.router.navigate([`system/loan-account/data/view`], { skipLocationChange: true });
        }
      )
    } else {
      this.router.navigate([`system/loan-account/data/view`], { skipLocationChange: true });
    }
  }
  triggerLoanDemands() {
    this.loading = true;
    this.acid = this.fmData.formData.account_code;
    this.loanParams = new HttpParams()
      .set("acid", this.acid)
      .set("daysAhead", 10);    
    this.loanInterestApi.loanDemands(this.loanParams).pipe(takeUntil(this.destroy$)).subscribe(
      (res) => {
        this.loading = false;
        this.onShowResults = true;
        this.showLoanDemandEvents = true;
        this.showTriggeredLoanDemands = true;
        this.showAllTriggeredLoanDemands = false;
        this.demandRes = res;
        this.resdata = this.demandRes;
        for (let i = 0; i < this.resdata.length; i++) {
          this.notificationAPI.alertSuccess(this.resdata[i].message);
        }
        this.getDemandsInfo();
        this.getAllDemandsInfo(this.results.acid);
      }, (err) => {
        this.loading = false;
        this.notificationAPI.alertWarning("Server Error: !!");
      }
    );
  }
  triggerAllLoanDemands() {
    if (window.confirm('ALL LOANS ACCOUNTS WILL BE DEMANDED:- YOU CAN NOT REVERSE ONCE INITIATED!! ARE YOU SURE?')) {
      this.loading = true;
      this.loanInterestApi.demandAllLoans().pipe(takeUntil(this.destroy$)).subscribe(
        (res) => {
          this.loading = false;
          this.onShowResults = true;
          this.showLoanDemandEvents = false;
          this.showTriggeredLoanDemands = false;
          this.showAllTriggeredLoanDemands = true;
          this.allDemandsRes = res;
          this.demandsData = this.allDemandsRes;
          for (let i = 0; i < this.demandsData.length; i++) {
            this.notificationAPI.alertSuccess(this.demandsData[i].message);
          }
          this.router.navigate([`system/loan-account/data/view`], { skipLocationChange: true });
        }, (err) => {
          this.loading = false;
          this.error = err;
          this.notificationAPI.alertWarning(this.error);
          this.router.navigate([`system/loan-account/data/view`], { skipLocationChange: true });
        }
      )
    } else {
      this.router.navigate([`system/loan-account/data/view`], { skipLocationChange: true });
    }
  }
  triggerLoanDisbursmnent() {
    this.loading = true;
    this.acid = this.fmData.formData.account_code;
    this.params = new HttpParams().set("acid", this.acid);
    this.loanInterestApi.verifyLoanDisbursement(this.params).pipe(takeUntil(this.destroy$)).subscribe(
      (res) => {
        this.loading = false;
        this.demandRes = res;
        this.resdata = this.demandRes;
        for (let i = 0; i < this.resdata.length; i++) {
          this.notificationAPI.alertSuccess(this.resdata[i].message);
        }
        this.router.navigate([`system/loan-disbursement/maintenance`], { skipLocationChange: true, queryParams: { formData: this.formData.value } });
      },
      err => {
        this.loading = false;
        this.notificationAPI.alertWarning("Server Error: !!");
      }
    );
  }
  satisfyLoanDemands() {
    this.loading = true;
    this.acid = this.fmData.formData.account_code;
    this.demandsparams = new HttpParams().set("acid", this.acid);
    this.loanInterestApi.satisfyDemands(this.demandsparams).pipe(takeUntil(this.destroy$)).subscribe(
      (res) => {
        this.loading = false;
        this.dataRes = res;
        this.sdata = this.dataRes;
        for (let i = 0; i < this.sdata.length; i++) {
          this.notificationAPI.alertSuccess(this.sdata[i].message);
        }
        this.showSatisfiedLoanDemands = true;
        this.showAllSatisfiedLoanDemands = false;
        this.onShowResults = true;
      }, (err) => {
        this.loading = false;
        this.notificationAPI.alertWarning("Server Error: !!");
      }
    );
  }
 
 
  pauseLoanDemandsGeneration() {
    this.loading = true;
    this.acid = this.fmData.formData.account_code;
    this.demandsparams = new HttpParams().set("acid", this.acid);
    this.loanInterestApi.pauseDemandGeneration(this.demandsparams).pipe(takeUntil(this.destroy$)).subscribe(
      (res) => {
        this.loading = false;
        this.dataRes = res;
        this.sdata = this.dataRes;
        this.notificationAPI.alertSuccess(this.sdata.message);
        this.showSatisfiedLoanDemands = true;
        this.showAllSatisfiedLoanDemands = false;
        this.pausedDemandsFlag = true
        this.onShowResults = true;
      }, (err) => {
        this.loading = false;
        this.notificationAPI.alertWarning("Server Error: !!");
      }
    );
  }
  
  unpauseLoanDemandsGeneration() {
    this.loading = true;
    this.acid = this.fmData.formData.account_code;
    this.demandsparams = new HttpParams().set("acid", this.acid);
    this.loanInterestApi.unpauseDemandGeneration(this.demandsparams).pipe(takeUntil(this.destroy$)).subscribe(
      (res) => {
        this.loading = false;
        this.dataRes = res;
        this.sdata = this.dataRes;
        this.notificationAPI.alertSuccess(this.sdata.message);
        this.showSatisfiedLoanDemands = true;
        this.showAllSatisfiedLoanDemands = false;
        this.pausedDemandsFlag = false

        this.onShowResults = true;
      }, (err) => {
        this.loading = false;
        this.notificationAPI.alertWarning("Server Error: !!");
      }
    );
  }
   
  pauseLoanDemandsSatisfaction() {
    this.loading = true;
    this.acid = this.fmData.formData.account_code;
    this.demandsparams = new HttpParams().set("acid", this.acid);
    this.loanInterestApi.pauseDemandSatisfaction(this.demandsparams).pipe(takeUntil(this.destroy$)).subscribe(
      (res) => {
        this.loading = false;
        this.dataRes = res;
        this.sdata = this.dataRes;
        this.notificationAPI.alertSuccess(this.sdata.message);
        this.showSatisfiedLoanDemands = true;
        this.showAllSatisfiedLoanDemands = false;
        this.pausedSatisfactionFlag = true
        this.onShowResults = true;
      }, (err) => {
        this.loading = false;
        this.notificationAPI.alertWarning("Server Error: !!");
      }
    );
  }
  
  unpauseLoanDemandsSatisfaction() {
    this.loading = true;
    this.acid = this.fmData.formData.account_code;
    this.demandsparams = new HttpParams().set("acid", this.acid);
    this.loanInterestApi.unpauseDemandSatisfaction(this.demandsparams).pipe(takeUntil(this.destroy$)).subscribe(
      (res) => {
        this.loading = false;
        this.dataRes = res;
        this.sdata = this.dataRes;
        this.notificationAPI.alertSuccess(this.sdata.message);
        this.showSatisfiedLoanDemands = true;
        this.showAllSatisfiedLoanDemands = false;
        this.pausedDemandsFlag = false

        this.onShowResults = true;
      }, (err) => {
        this.loading = false;
        this.notificationAPI.alertWarning("Server Error: !!");
      }
    );
  }
  satisfyAllLoanDemands() {
    if (window.confirm('ALL LOANS ACCOUNTS WILL BE DEMANDED WILL BE SATISFIED:- YOU CAN NOT REVERSE THE PROCESS ONCE INITIATED!! ARE YOU SURE?')) {
      this.loading = true;
      this.loanInterestApi.satisfyAllDemands().pipe(takeUntil(this.destroy$)).subscribe(
        (res) => {
          this.loading = false;
          this.showSatisfiedLoanDemands = false;
          this.showAllSatisfiedLoanDemands = true;
          this.onShowResults = true;
          this.satisfiedRes = res;
          this.satisfiedData = this.satisfiedRes;
          for (let i = 0; i < this.satisfiedData.length; i++) {
            this.notificationAPI.alertSuccess(this.satisfiedData[i].message);
          }
          this.router.navigate([`system/loan-account/data/view`], { skipLocationChange: true });
        }, (err) => {
          this.loading = false;
          this.notificationAPI.alertWarning("Server Error: !!");
          this.router.navigate([`system/loan-account/data/view`], { skipLocationChange: true });
        }
      )
    } else {
      this.router.navigate([`system/loan-account/data/view`], { skipLocationChange: true });
    }
  }
  getAccrualInfo() {
    this.loanInterestApi.retrieveAccrual(this.fmData.formData.account_code).pipe(takeUntil(this.destroy$)).subscribe(
      (res) => {
        this.results = res;
        this.accrualInfor = this.results.entity;
        this.getLoanDataDetails();;
        this.onShowResults = true;
      }, (err) => {
        this.loading = false;
        this.notificationAPI.alertWarning("Server Error: !!");
      }
    )
  }
  getBookingsInfo() {
    this.loanInterestApi.retrieveBokings(this.fmData.formData.account_code).pipe(takeUntil(this.destroy$)).subscribe(
      (res) => {
        this.results = res;
        this.bookingInfor = this.results.entity;
        this.getLoanDataDetails();;
        this.onShowResults = true;
      }, (err) => {
        this.loading = false;
        this.notificationAPI.alertWarning("Server Error: !!");
      }
    )
  }

  getDemandsInfo() {
    this.loanInterestApi.retrieveDemands(this.results.acid).pipe(takeUntil(this.destroy$)).subscribe(
      (res) => {
        this.results = res;
        this.demandInfor = this.results.entity;
        this.getLoanDataDetails();;
        this.onShowResults = true;
      }, (err) => {
        this.loading = false;
        this.notificationAPI.alertWarning("Server Error: !!");
      }
    )
  }
  getAllDemandsInfo(acid: any) {
    this.loanInterestApi.retrieveAllDemands(this.results.acid).pipe(takeUntil(this.destroy$)).subscribe(
      (res) => {
        this.results = res;
        this.allDemands = this.results.entity;
        this.onShowResults = true;
      }, (err) => {
        this.loading = false;
        this.notificationAPI.alertWarning("Server Error: !!");
      }
    )
  }


  printAmortization(): void {
    let printContents: any, popupWin: any;
    printContents = document.getElementById('print-section').innerHTML;
    popupWin = window.open('', '_blank', 'top=0,left=0,height=100%,width=auto');
    popupWin.document.open();
    popupWin.document.write(`
      <html>
        <head>
          <title>
          </title>
        </head>
    <body onload="window.print();window.close()">
             <img src=${this.logolink}
             style="
               width: 10% !important;
                height: 10% !important;
                display: block;
    margin-left: auto;
    margin-right: auto;">
    ${printContents}
    </body>
      </html>`
    );
    printContents = setTimeout(function () { popupWin.close(); }, 90000);
    popupWin.print();
  }

  onExportAmortization() {
    var data = document.getElementById('print-section');  //Id of the table
    html2canvas(data).then(canvas => {
      let imgWidth = 208;
      let pageHeight = 295;
      let imgHeight = canvas.height * imgWidth / canvas.width;
      let heightLeft = imgHeight;

      const contentDataURL = canvas.toDataURL('image/png')
      let pdf = new jspdf('p', 'mm', 'a4'); // A4 size page of PDF
      let position = 0;
      pdf.addImage(contentDataURL, 'PNG', 0, position, imgWidth, heightLeft)
      pdf.save('Amortization.pdf'); // Generated PDF
    });
  }
  printAccountStatement(): void {
    let printContents: any, popupWin: any;
    printContents = document.getElementById('print-section').innerHTML;
    popupWin = window.open('', '_blank', 'top=0,left=0,height=100%,width=auto');
    popupWin.document.open();
    popupWin.document.write(`
      <html>
        <head>
          <title>${this.saccoNamelink} | ACCOUNT STATEMENT</title>
          <hr/>
           <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
        </head>
    <body onload="window.print();window.close()">
    ${printContents}</body>
      </html>`
    );
    printContents = setTimeout(function () { popupWin.close(); }, 100000);
    popupWin.print();
  }

  onExportAccountStatement() {
    var data = document.getElementById('print-section');  //Id of the table
    html2canvas(data).then(canvas => {
      let imgWidth = 208;
      let pageHeight = 295;
      let imgHeight = canvas.height * imgWidth / canvas.width;
      let heightLeft = imgHeight;

      const contentDataURL = canvas.toDataURL('image/png')
      let pdf = new jspdf('p', 'mm', 'a4'); // A4 size page of PDF
      let position = 0;
      pdf.addImage(contentDataURL, 'PNG', 0, position, imgWidth, heightLeft)
      pdf.save('account-statement.pdf'); // Generated PDF
    });
  }
  // onRejectAccount() {
  //   this.dialogConfig = new MatDialogConfig();
  //   this.dialogConfig.disableClose = true;
  //   this.dialogConfig.autoFocus = true;
  //   this.dialogConfig.width = "600px";
  //   this.dialogConfig.data = this.accountsdata
  //   const dialogRef = this.dialog.open(RejectAccountComponent, this.dialogConfig);
  //   dialogRef.afterClosed().subscribe(
  //     (_res: any) => {
  //       this.loading = false;
  //       this.getLoanDataDetails();
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
                  this.router.navigate([`/system/loan-account/maintenance`], { skipLocationChange: true });
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
