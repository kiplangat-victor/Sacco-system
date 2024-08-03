import { Component, Inject, OnInit, ViewChild, OnDestroy } from '@angular/core';
import { FormArray, FormBuilder, FormGroup } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';
import { AccountsService } from 'src/app/administration/Service/AccountsService/accounts/accounts.service';
import { LoanScheduleService } from 'src/app/administration/Service/AccountsService/Loans/LoanSchedule/loan-schedule.service';
import { AccountStatementService } from 'src/app/administration/Service/transaction/account-statement.service';

@Component({
  selector: 'app-general-account-details',
  templateUrl: './general-account-details.component.html',
  styleUrls: ['./general-account-details.component.scss']
})
export class GeneralAccountDetailsComponent implements OnInit, OnDestroy {
  guarantoColumns: string[] = [
    'index',
    'guarantorType',
    'guarantorCustomerCode',
    'loanSeries',
    'guaranteeAmount'
  ];
  collateralColumns: string[] = [
    'index',
    'collateralSerial',
    'collateralName',
    'collateralType',
    'collateralValue'
  ];
  loanFeesColumns: string[] = [
    'index',
    'chargeCollectionAccount',
    'eventIdCode',
    'eventTypeDesc',
    'initialAmt',
    'monthlyAmount',
    'nextCollectionDate'
  ];
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
    'occupation'
  ];
  relatedPartiesColumns: string[] = [
    "index",
    "relPartyCustomerCode",
    "relPartyCustomerName",
    'relPartyPostalAddress',
    'relPartyRelationType'
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
  nomineesDataSource: MatTableDataSource<any>;
  @ViewChild("nomineesPaginator") nomineesPaginator!: MatPaginator;
  @ViewChild(MatSort) nomineesSort!: MatSort;
  relatedPartiesDataSource: MatTableDataSource<any>;
  @ViewChild("relatedPartiesPaginator") relatedPartiesPaginator!: MatPaginator;
  @ViewChild(MatSort) relatedPartiesSort!: MatSort;
  guarantorsArray: any[] = [];
  loanFeesArray: any[] = [];
  collateralsArray: any[] = [];
  nomineesArray: any[] = [];
  relatedPartiesArray: any[] = [];
  accountDocumentsArray = new Array();
  onCheckedAccountStatements = false;
  disableActions: boolean = false;
  onShowNomineesTab: boolean = false;
  showLoanAccountTab: boolean = false;
  showOfficeAccountTab: boolean = false;
  showSavingsAccountTab: boolean = false;
  showFixedDepositAccountTab: boolean = false;
  showAccountDocumentsTab: boolean = false;
  destroy$: Subject<boolean> = new Subject<boolean>();

  loading: boolean = false;
  loanDetails: any;
  results: any;
  loanSchedules: any;
  loanDemands: any;
  officeDetails: any;
  savingsDetails: any;
  submitted: boolean= false;
  onShoWarning: boolean = false;
  onShowWarning: boolean = false;
  onShowRelatedPartiesTab: boolean = false;
  onShowAccountStatement: boolean = false;
  termDepositDetails: any;
  constructor(
    private fb: FormBuilder,
    private accountsAPI: AccountsService,
    private notificationAPI: NotificationService,
    @Inject(MAT_DIALOG_DATA) public account_acid_id: any,
    public dialogRef: MatDialogRef<GeneralAccountDetailsComponent>
  ) {
   }
  ngOnDestroy(): void {
    this.destroy$.next(true);
    this.destroy$.complete();
  }
  ngOnInit(): void {
    this.getAccountData();
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
    accountStatus: [''],
    operationMode: [''],
    accountType: [''],
    cashExceptionCr: [''],
    cashExceptionLimitDr: [''],
    currency: ['KES'],
    customerCode: [''],
    managerCode: [''],
    dispatchMode: [''],
    glCode: [''],
    glSubhead: [''],
    productCode: [''],
    misSectorCode: [''],
    misSubSectorCode: [''],
    lienAmount: [''],
    postedTime: [new Date()],
    openingDate: [new Date()],
    solCode: [''],
    transferExceptionLimitCr: [''],
    transferExceptionLimitDr: [''],
    accountStatement: [''],
    statementFreq: [''],
    transactionPhone: [''],
    officeAccount: [''],
    loan: [''],
    savings: [''],
    termDeposit: [''],
    nominees: [[]],
    relatedParties: [[]],
    accountDocuments: new FormArray([]),
  });
  officeAccountsForm: FormGroup = this.fb.group({
    id: [''],
    accountHeadName: [''],
    accountSupervisorId: [''],
    accountSupervisorName: [''],
    cashLimitDr: [''],
    cashLimitCr: [''],
    transferLimitDr: [''],
    transferLimitCr: [''],
    clearingLimitCrExce: ['']
  });
  savingsForm: FormGroup = this.fb.group({
    id: [''],
    sba_maturedDate: [''],
    sba_maturedValue: [''],
    sba_monthlyValue: [''],
    sba_savingPeriod: [''],
    sba_startDate: [''],
  });
  termDepositForm: FormGroup = this.fb.group({
    accrualLastDate: [''],
    id: [''],
    interestAmount: [''],
    interestCrAccountId: [''],
    interestPreferential: [''],
    interestRate: [''],
    maturityDate: [new Date()],
    maturityValue: [''],
    periodInMonths: [''],
    principalCrAccountId: [''],
    principalDrAccountId: [''],
    sumAccruedAmount: [''],
    termDepositAmount: [''],
    termDepositStatus: [''],
    valueDate: [new Date()],
    withholdingTax: ['']
  });
  loansForm: FormGroup = this.fb.group({
    sn: [''],
    collectInterest: ['TRUE'],
    collateral: [''],
    collateralsTotal: [''],
    currentRate: [''],
    loanType: [''],
    interestCalculationMethod: [''],
    loanStatus: [''],
    feesAmount: [''],
    guarantorsTotal: [''],
    principalAmount: [''],
    netAmount: [''],
    disbursementAmount: [''],
    interestDemandAmount: [''],
    principalDemandAmount: [''],
    sumPrincipalDemand: [''],
    repaymentPeriodId: [''],
    repaymentPeriod: [''],
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
    numberOfInstallments: [''],
    frequencyId: ['MONTHS'],
    frequencyPeriod: ['1'],
    installmentStartDate: [new Date()],
    installmentAmount: [''],
    outStandingPrincipal: [''],
    outStandingInterest: [''],
    totalCollateralsAndGuarantors: [''],
    totalLoanBalance: [''],
    loanCollaterals: [[]],
    loanFees: [[]],
    loanGuarantors: [[]]
  });
  get f() {
    return this.formData.controls;
  }
  get loan() {
    return this.loansForm.controls;
  }
  get s() {
    return this.savingsForm.controls;
  }
  get td() {
    return this.termDepositForm.controls;
  }
  get officeAccount() {
    return this.officeAccountsForm.controls;
  }
  getGuarantors() {
    this.guarantorDataSource = new MatTableDataSource(this.guarantorsArray);
    this.guarantorDataSource.paginator = this.guarantorPaginator;
    this.guarantorDataSource.sort = this.guarantorSort;
  }
  getCollateral() {
    this.collateralDataSource = new MatTableDataSource(this.collateralsArray);
    this.collateralDataSource.paginator = this.collateralPaginator;
    this.collateralDataSource.sort = this.collateralSort;
  }
  getLoanFees() {
    this.loanFeesDataSource = new MatTableDataSource(this.loanFeesArray);
    this.loanFeesDataSource.paginator = this.loanFeesPaginator;
    this.loanFeesDataSource.sort = this.loanFeesSort;
  }
  getrelatedParties() {
    this.relatedPartiesDataSource = new MatTableDataSource(this.relatedPartiesArray);
    this.relatedPartiesDataSource.paginator = this.relatedPartiesPaginator;
    this.relatedPartiesDataSource.sort = this.relatedPartiesSort;
  }
  getNominee() {
    this.nomineesDataSource = new MatTableDataSource(this.nomineesArray);
    this.nomineesDataSource.paginator = this.nomineesPaginator;
    this.nomineesDataSource.sort = this.nomineesSort;
  }
  getAccountData() {
    this.loading = true;
    this.accountsAPI.retrieveAccount(this.account_acid_id).pipe(takeUntil(this.destroy$)).subscribe(
      {
        next: (
          (res) => {
            if (res.statusCode === 302) {
              this.loading = false;
              this.results = res.entity;
              this.accountDocumentsArray = this.results.accountDocuments;
              if (this.results.accountType == 'OAB') {
                this.showOfficeAccountTab = true;
                this.officeDetails = this.results.officeAccount;
                this.officeAccountsForm = this.fb.group({
                  id: [this.officeDetails.id],
                  accountHeadName: [this.officeDetails.accountHeadName],
                  accountSupervisorId: [this.officeDetails.accountSupervisorId],
                  accountSupervisorName: [this.officeDetails.accountSupervisorName],
                  cashLimitDr: [this.officeDetails.cashLimitDr],
                  cashLimitCr: [this.officeDetails.cashLimitCr],
                  transferLimitDr: [this.officeDetails.transferLimitDr],
                  transferLimitCr: [this.officeDetails.transferLimitCr],
                  clearingLimitCrExce: [this.officeDetails.clearingLimitCrExce],
                });
              }
              if (this.results.accountType == 'SBA') {
                this.showSavingsAccountTab = true;
                this.showAccountDocumentsTab = true;
                this.savingsDetails = this.results.savings;
                this.nomineesArray = this.results.nominees;
                this.relatedPartiesArray = this.results.relatedParties;
                this.savingsForm = this.fb.group({
                  id: [this.savingsDetails.id],
                  sba_maturedDate: [this.savingsDetails.sba_maturedDate],
                  sba_maturedValue: [this.savingsDetails.sba_maturedValue],
                  sba_monthlyValue: [this.savingsDetails.sba_monthlyValue],
                  sba_savingPeriod: [this.savingsDetails.sba_savingPeriod],
                  sba_startDate: [this.savingsDetails.sba_startDate],
                });
              }
              if (this.results.accountType == 'TDA') {
                this.showFixedDepositAccountTab = true;
                this.termDepositDetails = this.results.termDeposit;
                this.nomineesArray = this.results.nominees;
                this.relatedPartiesArray = this.results.relatedParties;
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
              }
              if (this.results.accountType == 'LAA') {
                this.showLoanAccountTab = true;
                this.showAccountDocumentsTab = true;
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
                  loanGuarantors: [[]]
                });
              }
              if (this.results.checkedJointAccount == true) {
                this.onShowRelatedPartiesTab = true;
              } else if (this.results.checkedJointAccount == false) {
                this.onShowRelatedPartiesTab = false;
              }
              if (this.results.accountStatement == true) {
                this.onShowAccountStatement = true;
              } else if (this.results.accountStatement == false) {
                this.onShowAccountStatement = true;
              }
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
                withholdingTax: [this.results.withholdingTax]
              });

              this.results.loan.loanGuarantors.forEach((element: any) => {
                this.guarantorsArray.push(element);
              });
              this.getGuarantors();
              this.results.loan.loanCollaterals.forEach((element: any) => {
                this.collateralsArray.push(element);
              });
              this.getCollateral();
              this.results.loan.loanFees.forEach((element: any) => {
                this.loanFeesArray.push(element);
              });
              this.getLoanFees();

              this.results.nominees.forEach((element: any) => {
                this.nomineesArray.push(element);
              });
              this.getNominee();
              this.results.relatedParties.forEach((element: any) => {
                this.relatedPartiesArray.push(element);
              });
              this.getrelatedParties();
            } else {
              this.loading = false;
              this.notificationAPI.alertWarning("Account Details For " + this.account_acid_id + " Not Found");
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
    );
  }
}
