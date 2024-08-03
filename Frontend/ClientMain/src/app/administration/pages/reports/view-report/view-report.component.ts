import { HttpParams } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import {
  MatSnackBar,
  MatSnackBarHorizontalPosition,
  MatSnackBarVerticalPosition
} from '@angular/material/snack-bar';
import { Subscription } from 'rxjs';
import { DataStoreService } from 'src/@core/helpers/data-store.service';
import { ReportsService } from 'src/app/administration/Service/reports/reports.service';
import { UniversalInquiryService } from 'src/app/administration/Service/UniversalInquiry/universal-inquiry.service';
import Swal from 'sweetalert2';
import { DynamicReportLookupComponent } from '../dynamic-report-lookup/dynamic-report-lookup.component';
import { AccountStatementComponent } from './account-statement/account-statement.component';
import { ArrearsReportComponent } from './arrears-report/arrears-report.component';
import { BalanceSheetDialogComponent } from './balance-sheet-dialog/balance-sheet-dialog.component';
import { CustomerReportComponent } from './customer-report/customer-report.component';
import { DisbursementReportComponent } from './disbursement-report/disbursement-report.component';
import { ExpensesReportDialogueComponent } from './expenses-report-dialogue/expenses-report-dialogue.component';
import { FeeReportDialogueComponent } from './fee-report-dialogue/fee-report-dialogue.component';
import { LoanDemandsReportComponent } from './loan-demands-report/loan-demands-report.component';
import { LoanPortfolioReportsComponent } from './loan-portfolio-reports/loan-portfolio-reports.component';
import { RepaymentsReportComponent } from './repayments-report/repayments-report.component';
import { TransactionReportsComponent } from './transaction-reports/transaction-reports.component';
import { TrialBalanceReportDialogueComponent } from './trial-balance-report-dialogue/trial-balance-report-dialogue.component';

export interface ReportInterface {
  name: string,
  authName?: string,
  filename?: string,
  isDynamic?: boolean,
  requiredFields?: string[],
  displays?: string[],
  conversionsFrom?: string[],
  conversionsTo?: string[],
  alternatives?: any[],
  defaults?: any,
  display: boolean,
  function: () => void
}

export interface ReportCategory {
  title: string,
  display: boolean,
  specificReports: ReportInterface[]
}

@Component({
  selector: 'app-view-report',
  templateUrl: './view-report.component.html',
  styleUrls: ['./view-report.component.scss'],
})
export class ViewReportComponent implements OnInit {
  horizontalPosition: MatSnackBarHorizontalPosition = 'end';
  verticalPosition: MatSnackBarVerticalPosition = 'top';
  reports!: any;
  subscription!: Subscription;
  error: any;
  loading = false;
  displayReport = false;
  displayContents: string;
  AccountStatements = false;
  actionsArray: any[];
  RetailAccountReports = false;
  branchfeereport = false;
  organisationfeereport = false;
  branchexpensesreport = false;
  allexpensesreport = false;
  repaymentspermanagerreport = false;
  repaymentsperbranchreport = false;
  profitandlossreport = false;
  trialbalancereport = false;
  accountstatementsreport = false;
  retailaccountsreport = false;
  accountsbystatusreport = false;
  alldisbursementsreport = false;
  disbursementsperbranchreport = false;
  userdisbursementsreport = false;
  allloanportfoliosreport = false;
  branchloanportfoliosreport = false;
  userloanpotfoliosreport = false;
  retailcustomerreport = false;
  groupmembersreport = false;
  allarrearsreport = false;
  arrearsperbranchreport = false;
  arrearsonloanperissuerreport = false;
  allrepaymentsreport = false;
  alltransactionsreport = false;
  transactionsperbranchreport = false;
  accounttypetransactionsreport = false;
  memberAccountTransactionsreport = false;
  allloandemandsreport = false;
  loandemandperbranchreport = false;
  loandemandperbranchaspermanagerreport = false;
  guarantorsloansreport = false;
  balancesheetreport = false;
  searchTerm = ""
  reportDisplay: ReportCategory[]
  authReportDisplay: ReportCategory[]
  uiReports: ReportCategory[]
  corporateAccountsreport: boolean = true;
  groupAccountsreport: boolean = true;
  constructor(
    private dialog: MatDialog,
    private _snackBar: MatSnackBar,
    private universalInquiryService: UniversalInquiryService,
    private reportsService: ReportsService,
    private dataStoreApi: DataStoreService
  ) {
    this.iniAuthorization();
  }
  ngOnInit(): void { }

  iniAuthorization() {
    console.log("Init auth");
    this.actionsArray = this.dataStoreApi.getActions("REPORTS");
    for (let i = 0; i < this.actionsArray.length; i++) {
      let obj = this.actionsArray[i];
      if (!this.accountstatementsreport)
        this.accountstatementsreport = obj.name == "ACCOUNT STATEMENTS REPORT";
      if (!this.retailaccountsreport)
        this.retailaccountsreport = obj.name == "RETAIL ACCOUNTS REPORT";
      if (!this.accountsbystatusreport)
        this.accountsbystatusreport = obj.name == "ACCOUNTS BY STATUS REPORT";
      if (!this.alldisbursementsreport)
        this.alldisbursementsreport = obj.name == "ALL DISBURSEMENTS REPORT";
      if (!this.disbursementsperbranchreport)
        this.disbursementsperbranchreport = obj.name == "DISBURSEMENTS PER BRANCH REPORT";
      if (!this.userdisbursementsreport)
        this.userdisbursementsreport = obj.name == "USER DISBURSEMENTS REPORT";
      if (!this.allloanportfoliosreport)
        this.allloanportfoliosreport = obj.name == "ALL LOAN PORTFOLIOS REPORT";
      if (!this.branchloanportfoliosreport)
        this.branchloanportfoliosreport = obj.name == "BRANCH LOAN PORTFOLIOS REPORT";
      if (!this.userloanpotfoliosreport)
        this.userloanpotfoliosreport = obj.name == "USER LOAN POTFOLIOS REPORT";
      if (!this.retailcustomerreport)
        this.retailcustomerreport = obj.name == "RETAIL CUSTOMER REPORT";
      if (!this.groupmembersreport)
        this.groupmembersreport = obj.name == "GROUP MEMBERS REPORT";
      if (!this.allarrearsreport)
        this.allarrearsreport = obj.name == "ALL ARREARS REPORT";
      if (!this.arrearsperbranchreport)
        this.arrearsperbranchreport = obj.name == "ARREARS PER BRANCH REPORT";
      if (!this.arrearsonloanperissuerreport)
        this.arrearsonloanperissuerreport = obj.name == "ARREARS ON LOAN PER ISSUER REPORT";
      if (!this.allrepaymentsreport)
        this.allrepaymentsreport = obj.name == "ALL REPAYMENTS REPORT";
      if (!this.repaymentsperbranchreport)
        this.repaymentsperbranchreport = obj.name == "REPAYMENTS PER BRANCH REPORT";
      if (!this.repaymentspermanagerreport)
        this.repaymentspermanagerreport = obj.name == "REPAYMENTS PER MANAGER REPORT";
      if (!this.alltransactionsreport)
        this.alltransactionsreport = obj.name == "ALL TRANSACTIONS REPORT";
      if (!this.transactionsperbranchreport)
        this.transactionsperbranchreport = obj.name == "TRANSACTIONS PER BRANCH REPORT";
      if (!this.accounttypetransactionsreport)
        this.accounttypetransactionsreport = obj.name == "ACCOUNT TYPE TRANSACTIONS REPORT";
      if (!this.memberAccountTransactionsreport)
        this.memberAccountTransactionsreport = obj.name == "CUSTOMER ACCOUNT TYPE TRANSACTIONS REPORT";
      if (!this.allloandemandsreport)
        this.allloandemandsreport = obj.name == "ALL LOAN DEMANDS REPORT";
      if (!this.loandemandperbranchreport)
        this.loandemandperbranchreport = obj.name == "LOAN DEMAND PER BRANCH REPORT";
      if (!this.loandemandperbranchaspermanagerreport)
        this.loandemandperbranchaspermanagerreport = obj.name == "LOAN DEMAND PER BRANCH AS PER MANAGER REPORT";
      if (!this.guarantorsloansreport)
        this.guarantorsloansreport = obj.name == "GUARANTORS LOANS REPORT";
      if (!this.balancesheetreport)
        this.balancesheetreport = obj.name == "BALANCE SHEET REPORT";
      if (!this.trialbalancereport)
        this.trialbalancereport = obj.name == "TRIAL BALANCE REPORT";
      if (!this.profitandlossreport)
        this.profitandlossreport = obj.name == "PROFIT AND LOSS REPORT";
      if (!this.allexpensesreport)
        this.allexpensesreport = obj.name == "ALL EXPENSES REPORT";
      if (!this.branchexpensesreport)
        this.branchexpensesreport = obj.name == "BRANCH EXPENSES REPORT";
      if (!this.organisationfeereport)
        this.organisationfeereport = obj.name == "ORGANISATION FEE REPORT";
      if (!this.branchfeereport)
        this.branchfeereport = obj.name == "BRANCH FEE REPORT";
    }

    this.initDisplay();
    this.sortAuthReports();
    this.uiReports = this.authReportDisplay;
  }

  openUniversalInqury(key, entity) {
    this.universalInquiryService.open({ key: key, entity: entity });
  }

  sortAuthReports() {
    var tempDisplay: ReportCategory[] = []
    this.reportDisplay.forEach(element => {
      if (!element.display) {
      } else {
        var newElement: ReportCategory = JSON.parse(JSON.stringify(element))
        newElement.specificReports = []
        element.specificReports.forEach(report => {
          if (report.display) {
            newElement.specificReports.push(report);
          }
        });
        if (newElement.specificReports.length > 0) {
          tempDisplay.push(newElement);
        }
      }
    });
    this.authReportDisplay = tempDisplay;
  }

  onSearchChange() {
    this.search();
  }

  search() {
    var tempDisplay: ReportCategory[] = []
    this.authReportDisplay.forEach(element => {
      if (element.title.toUpperCase().indexOf(this.searchTerm.toUpperCase()) !== -1) {
        tempDisplay.push(element);
      } else {
        var newElement: ReportCategory = JSON.parse(JSON.stringify(element))
        newElement.specificReports = []
        element.specificReports.forEach(report => {
          if (report.name.toUpperCase().indexOf(this.searchTerm.toUpperCase()) !== -1) {
            newElement.specificReports.push(report);
          }
        });
        if (newElement.specificReports.length > 0) {
          tempDisplay.push(newElement);
        }
      }
    });
    this.uiReports = tempDisplay;
  }

  toDynamicReport = (report: ReportInterface): void => {
    console.log("To dynamic report");
    const dialogConfig = new MatDialogConfig();
    dialogConfig.disableClose = false;
    dialogConfig.autoFocus = true;
    dialogConfig.width = '800px';
    dialogConfig.data = {
      report: report,
      reportTitle: report.name,
    };
    const dialogRef = this.dialog.open(DynamicReportLookupComponent, dialogConfig);
    dialogRef.afterClosed().subscribe((result) => {
      console.log(result);
      if (result != null && result.event == 'display') {
        this.displayReport = true;
        this.displayContents = result;
      }
    });
  }

  initDisplay = (): void => {
    console.log(this.retailaccountsreport);
    this.reportDisplay =
      [
        {
          title: "Sacco Members Reports",
          display: true,
          specificReports: [
            {
              isDynamic: true,
              requiredFields: ['fromdate', 'todate'],
              filename: "all_members.jrxml",
              name: "All Members Report",
              display: this.retailcustomerreport,
              function: this.getAllMembersReport
            },
            {
              isDynamic: true,
              requiredFields: ['fromdate', 'todate'],
              filename: "real_members.jrxml",
              name: "Real Members Report",
              display: this.retailcustomerreport,
              function: this.getAllMembersReport
            },
            {
              isDynamic: true,
              requiredFields: ['fromdate', 'todate', 'memberType'],
              filename: "member_by_category.jrxml",
              name: "Members By Category Report",
              display: this.retailcustomerreport,
              function: this.getAllMembersReport
            },
            {             
              isDynamic: false,
              requiredFields: ['fromdate', 'todate'],
              filename: "all_members.jrxml",
              name: "RETAIL CUSTOMER REPORT",
              display: false,
              function: this.generateRetailCustomersReport
            },
            {
              name: "Corporate List REPORT",
              display: false,
              function: this.generateCorporateCustomersReport
            },
            {
              name: "HOUSES List REPORT",
              display: false,
              function: this.getListOfHousesReport
            },
            {
              name: "HOUSE MEMBERS REPORT",
              display: this.groupmembersreport,
              function: this.getMemberOfHouseReport
            }
          ]
        },
        {
          title: "Accounts reports",
          display: true,
          specificReports: [
            {
              isDynamic: true,
              requiredFields: ['fromdate', 'todate', 'customerCode', 'charge', 'showReversals'],
              filename: "all_member_account_statement.jrxml",
              name: "Full Member Statement Report",
              alternatives: [{filename: "all_member_account_statement_with_reversals.jrxml", conditions:[{name: "showReversals", value:"Y"}]}],
              display: this.alltransactionsreport,
              function: this.getAllMembersReport
            },
            {            
              isDynamic: true,
              requiredFields: ['fromdate', 'todate', 'accountType'],
              filename: "all_accounts.jrxml",
              name: "Accounts By Scheme REPORT",
              display: this.retailaccountsreport,
              function: this.accountsBySchemeReport
            },
            
            
            {
              isDynamic: true,
              requiredFields: ['fromdate', 'todate', 'acid', 'charge', 'showReversals'],
              filename: "account_statement.jrxml",
              name: "ACCOUNT STATEMENTS REPORT",
              alternatives: [{filename: "account_statement_with_reversals.jrxml", conditions:[{name: "showReversals", value:"Y"}]}],
              display: this.alltransactionsreport,
              function: this.getAllMembersReport
            },
            {
              isDynamic: true,
              requiredFields: ['fromdate', 'todate', 'acid', 'charge'],
              filename: "minimal_account_statement.jrxml",
              name: "MINIMAL ACCOUNT STATEMENTS REPORT",
              display: this.alltransactionsreport,
              function: this.getAllMembersReport
            },

            {
              isDynamic: true,
              requiredFields: ['fromdate', 'todate', 'productCode'],
              filename: "accounts_by_product_code.jrxml",
              name: "Accounts By Product REPORT",
              display: this.retailaccountsreport,
              function: this.accountsByProductReport
            },

            {
              isDynamic: true,
              requiredFields: ['fromdate', 'todate', 'productCode'],
              filename: "accounts_by_product_code_positive_book_balance.jrxml",
              name: "Accounts By Product(Positive) REPORT",
              display: this.retailaccountsreport,
              function: this.accountsByProductReport
            },

            {
              isDynamic: true,
              requiredFields: ['fromdate', 'todate', 'productCode'],
              filename: "deposits_against_loan_securities.jrxml",
              name: "Deposits Against Loans Guaranteed REPORT",
              display: this.retailaccountsreport,
              function: this.accountsByProductReport
            },
            {
              name: "All Accounts By member REPORT",
              display: this.retailaccountsreport,
              function: this.accountByMemberReport
            },
            {
              name: "Members of House Accounts REPORT",
              display: this.retailaccountsreport,
              function: this.accountsByGroupMembershipReport
            },
            {
              name: "RETAIL ACCOUNTS REPORT",
              display: false,
              function: this.retailsAccountsReport
            },
            {
              name: "CORPORATE ACCOUNTS REPORT",
              display: false,
              function: this.corporateAccountsReport
            },
            {
              name: "GROUP ACCOUNTS REPORT",
              display: false,
              function: this.groupAccountsReport
            },
            {
              isDynamic: true,
              requiredFields: ['fromdate', 'todate', 'productCode'],
              filename: "statement_for_all_accounts under_a_product.jrxml",
              name: "ALL  STATEMENT FOR ACCOUNTS IN PRODUCT Report",
              display: this.balancesheetreport,
              function: this.getAllMembersReport
            },
            {
              isDynamic: true,
              requiredFields: ['fromdate', 'todate', 'productCode'],
              filename: "statement_for_all_accounts under_a_product_minimal.jrxml",
              name: "ALL  STATEMENT FOR ACCOUNTS IN PRODUCT - Minimal Report",
              display: this.balancesheetreport,
              function: this.getAllMembersReport
            },
          ]
        },


        

        {
          title: "Teller Reports",
          display: true,
          specificReports: [
            {
              isDynamic: true,
              requiredFields: ['todaydate', 'username'],
              filename: "teller_transactions_grouped_for_date.jrxml",
              name: "Teller Transactions(Today) Report",
              display: this.alltransactionsreport,
              function: this.getAllMembersReport
            },
            {
              isDynamic: true,
              requiredFields: ['todaydate'],
              filename: "all_teller_transactions_grouped_for_date.jrxml",
              name: "All Teller Transactions(Today) Report",
              display: this.alltransactionsreport,
              function: this.getAllMembersReport
            },
            {
              isDynamic: true,
              requiredFields: ['fromdate', 'todate', 'username'],
              filename: "teller_transactions_grouped.jrxml",
              name: "Teller Transactions Report",
              display: this.alltransactionsreport,
              function: this.getAllMembersReport
            },
            {
              isDynamic: true,
              requiredFields: ['fromdate', 'todate'],
              filename: "all_teller_transactions_grouped.jrxml",
              name: "All Teller Transactions Report",
              display: this.alltransactionsreport,
              function: this.getAllMembersReport
            },
            {
              name: "ALL USER TRANSACTIONS REPORT",
              display: this.alltransactionsreport,
              function: this.generateAllTransactionsReport
            },
            {
              name: "TRANSACTIONS PER SYSTEM USER",
              display: this.transactionsperbranchreport,
              function: this.generateUserTransactionsReport
            },
          ]
        },

        
        {
          title: "Transaction Status Reports",
          display: true,
          specificReports: [
            {
              isDynamic: true,
              requiredFields: ['fromdate', 'todate', 'transactionType'],
              filename: "unposted_transactions.jrxml",
              name: "Unposted Transactions Report",
              display: this.alltransactionsreport,
              function: this.getAllMembersReport
            },
            {
              isDynamic: true,
              requiredFields: ['fromdate', 'todate', 'transactionType'],
              filename: "posted_transactions.jrxml",
              name: "Posted Transactions Report",
              display: this.alltransactionsreport,
              function: this.getAllMembersReport
            },
            {
              isDynamic: true,
              requiredFields: ['fromdate', 'todate', 'transactionType'],
              filename: "rejected_transactions.jrxml",
              name: "Rejected Transactions Report",
              display: this.alltransactionsreport,
              function: this.getAllMembersReport
            },
            {
              isDynamic: true,
              requiredFields: ['fromdate', 'todate'],
              filename: "unposted_transactions.jrxml",
              name: "Reversed Transactions Report",
              display: this.alltransactionsreport,
              function: this.getAllMembersReport
            },
          ]
        },

        {
          title: "GL and SubGl Reports",
          display: true,
          specificReports: [
            {
              isDynamic: true,
              requiredFields: ['fromdate', 'todate'],
              filename: "subgl_summed_transactions_all.jrxml",
              name: "All Subgl Periodic Summations Report",
              display: this.balancesheetreport,
              function: this.getAllMembersReport
            },
            {
              isDynamic: true,
              requiredFields: ['fromdate', 'todate', 'glCode'],
              filename: "subgl_summed_transactions_for_gl.jrxml",
              name: "Specific Subgl Periodic Summations Report",
              display: this.balancesheetreport,
              function: this.getAllMembersReport
            },
            {
              isDynamic: true,
              requiredFields: ['fromdate', 'todate', 'glSubheadCode'],
              filename: "sum_transactions_by_subgl.jrxml",
              name: "Analysis of Account Transactions on Sub GL Report",
              display: this.balancesheetreport,
              function: this.getAllMembersReport
            },
            {
              isDynamic: true,
              requiredFields: ['fromdate', 'todate', 'glCode'],
              filename: "sum_transactions_by_gl.jrxml",
              name: "Analysis of Account Transactions on GL Report",
              display: this.balancesheetreport,
              function: this.getAllMembersReport
            },
            {
              isDynamic: true,
              requiredFields: ['fromdate', 'todate', 'glCode'],
              filename: "statement_for_all_accounts under_a_gl.jrxml",
              name: "ALL  STATEMENT FOR ACCOUNTS IN GL Report",
              display: this.balancesheetreport,
              function: this.getAllMembersReport
            },
            {
              isDynamic: true,
              requiredFields: ['fromdate', 'todate', 'glCode'],
              filename: "statement_for_all_accounts under_a_gl_minimal.jrxml",
              name: "ALL  STATEMENT FOR ACCOUNTS IN GL - Minimal Report",
              display: this.balancesheetreport,
              function: this.getAllMembersReport
            },
          ]
        },

        {
          title: "Transactions Reports",
          display: true,
          specificReports: [
            {
              name: "Analysis of Account Transactions Member",
              display: this.transactionsperbranchreport,
              function: this.generateCustomerTransactionsReport
            },
            {
              isDynamic: true,
              requiredFields: ['fromdate', 'todate', 'productCode'],
              filename: "sum_transactions_by_product.jrxml",
              name: "Analysis of Transactions By Product",
              display:  this.transactionsperbranchreport,
              function: this.getAllMembersReport
            },
            {
              isDynamic: true,
              name: "Analysis of Transactions By Scheme",
              requiredFields: ['fromdate', 'todate', 'accountType'],
              display: this.transactionsperbranchreport,
              filename: "sum_transactions_by_scheme.jrxml",
              function: this.getAllMembersReport
            },
            {
              isDynamic: true,
              requiredFields: ['fromdate', 'todate'],
              name: "Analysis of All Account Transactions",
              display: this.transactionsperbranchreport,
              filename: "sum_transactions_all.jrxml",
              function:  this.getAllMembersReport
            },

            {
              name: "TRANSACTIONS PER BRANCH REPORT",
              display: this.transactionsperbranchreport,
              function: this.generateBranchTransactionsReport
            },
            {
              name: "TRANSACTIONS BY SCHEME REPORT",
              display: this.accounttypetransactionsreport,
              function: this.generateAccountTypeTransactionsReport
            },
            {
              name: "MEMBER ACCOUNTS TRANSACTIONS REPORT",
              display: this.memberAccountTransactionsreport,
              function: this.generateMemberAccountTransactionReport
            },
          ]
        },

        {
          title: "Loan Portfolio Reports",
          display: true,
          specificReports: [
            {
              isDynamic: true,
              requiredFields: ['fromdate', 'todate'],
              filename: "loan_portfolio.jrxml",
              name: "ALL LOAN PORTFOLIOS REPORT",
              display: this.allloanportfoliosreport,
              function: this.getAllMembersReport
            },
            {
              isDynamic: true,
              requiredFields: ['fromdate', 'todate', 'productCode', 'classification'],
              defaults: {productCode: "ALL"},
              filename: "loan_portfolio_advanced.jrxml",
              name: "ADVANCED LOAN PORTFOLIO REPORT",
              display: this.allloanportfoliosreport,
              function: this.getAllMembersReport
            },
            {
              isDynamic: true,
              requiredFields: ['fromdate', 'todate'],
              filename: "loan_deposits.jrxml",
              name: "LOAN VS SAVINGS REPORT",
              display: this.allloanportfoliosreport,
              function: this.getAllMembersReport
            },
            {
              isDynamic: true,
              requiredFields: ['fromdate', 'todate', 'productCode'],
              defaults: {productCode: "ALL"},
              filename: "loan_portfolio_advanced_inclusive.jrxml",
              name: "ADVANCED LOAN PORTFOLIO INCLUSIVE REPORT",
              display: this.allloanportfoliosreport,
              function: this.getAllMembersReport
            },
            {
              isDynamic: true,
              requiredFields: ['fromdate', 'todate', 'employerCode'],
              filename: "loan_portfolio_advanced_inclusive_employer.jrxml",
              name: "ADVANCED LOAN PORTFOLIO FOR EMPLOYEES REPORT",
              display: this.allloanportfoliosreport,
              function: this.getAllMembersReport
            },
            {
              isDynamic: true,
              requiredFields: ['fromdate', 'todate'],
              filename: "statement_for_all_loans.jrxml",
              name: "STATEMENT FOR ALL RUNNING LOANS REPORT",
              display: this.allloanportfoliosreport,
              function: this.getAllMembersReport
            },
            {
              isDynamic: true,
              requiredFields: ['fromdate', 'todate'],
              filename: "statement_for_all_closed_loans.jrxml",
              name: "STATEMENT FOR ALL CLOSED LOANS REPORT",
              display: this.allloanportfoliosreport,
              function: this.getAllMembersReport
            },
            {
              name: "BRANCH LOAN PORTFOLIOS REPORT",
              display: this.branchloanportfoliosreport,
              function: this.getBranchLoanPortfolios
            },
            {
              name: "USER LOAN PORTFOLIOS REPORT",
              display: this.userloanpotfoliosreport,
              function: this.getUserLoanPortfolios
            },
            
            {
              isDynamic: true,
              requiredFields: ['fromdate', 'todate', 'productCode'],
              defaults: {productCode: "ALL"},
              filename: "loan_arrears.jrxml",
              name: "LOAN ARREARS REPORT",
              display: this.allloanportfoliosreport,
              function: this.getAllMembersReport
            },
          ]
        },

        {
          title: "Standing Order Reports",
          display: true,
          specificReports: [
            {
              isDynamic: true,
              requiredFields: ['fromdate', 'todate'],
              filename: "standing_orders_execution.jrxml",
              name: "STO Executions REPORT",
              display: this.allloanportfoliosreport,
              function: this.getAllMembersReport
            },
          ]
        },

        {
          title: "Guarantorship Reports",
          display: true,
          specificReports: [
            {
              name: "All Guarantors Report",
              display: this.guarantorsloansreport,
              function: this.allGuarantorsReport
            },
            {
              name: "Guarantorship By Loan Report",
              display: this.guarantorsloansreport,
              function: this.guarantorshipByLoanReport
            },
            {
              name: "Guarantorship By Person Report",
              display: this.guarantorsloansreport,
              function: this.guarantorShipByPersonReport
            }
          ]
        },
        {
          title: "Welfare Reports",
          display: true,
          specificReports: [
            {
              isDynamic: true,
              requiredFields: ['welfareCode'],
              filename: "welfare_all.jrxml",
              name: "All Welfare transactions",
              display: this.guarantorsloansreport,
              function: this.allGuarantorsReport
            },
            {
              isDynamic: true,
              requiredFields: ["customerCode", "welfareCode"],
              name: "Welfare Transactions By Member",
              filename: "welfare_for_member.jrxml",
              display: this.guarantorsloansreport,
              function: this.guarantorshipByLoanReport
            },
            {
              isDynamic: true,
              requiredFields: [],
              name: "Summed Welfare Transactions",
              display: false,
              function: this.guarantorShipByPersonReport
            }
          ]
        },
        {
          title: "Disbursements Reports",
          display: true,
          specificReports: [
            {
              name: "ALL DISBURSEMENTS REPORT",
              display: this.alldisbursementsreport,
              function: this.getAllDisbursements
            },
            {
              name: "DISBURSEMENTS PER BRANCH",
              display: this.disbursementsperbranchreport,
              function: this.getBranchDisbursements
            },
            {
              name: "USER DISBURSEMENTS REPORT",
              display: this.userdisbursementsreport,
              function: this.getUserDisbursements
            }
          ]
        },
        {
          title: "Loan Demands Reports",
          display: true,
          specificReports: [
            {
              name: "ALL LOAN DEMANDS REPORT",
              display: this.allloandemandsreport,
              function: this.generateAllLoanDemandsReport
            },
            {
             isDynamic: true,
             requiredFields: ['fromdate', 'todate', 'customerCode'],
             name: "LOAN DEMANDS FOR MEMBER REPORT",
             filename: "loan_demands_by_customer.jrxml",
             display: this.allloandemandsreport,
             function: this.guarantorShipByPersonReport
           },
           {
            isDynamic: true,
            requiredFields: ['fromdate', 'todate', 'acid'],
            name: "LOAN DEMANDS FOR A SPECIFIC LOAN REPORT",
            filename: "loan_demands_by_loan.jrxml",
            display: this.allloandemandsreport,
            function: this.guarantorShipByPersonReport
          },
            {
              isDynamic: true,
              requiredFields: ['fromdate', 'todate', 'productCode'],
              name: "PRODUCT LOAN DEMANDS REPORT",
              filename: "loan_demands_by_product.jrxml",
              display: this.allloandemandsreport,
              function: this.guarantorShipByPersonReport
            },
            {
              name: "LOAN DEMAND PER BRANCH REPORT",
              display: this.loandemandperbranchreport,
              function: this.generateBranchLoanDemandsReports
            },
            {
              name: "LOAN DEMAND PER BRANCH AS PER MANAGER REPORT",
              display: this.loandemandperbranchaspermanagerreport,
              function: this.generateUserLoanDemandsReports
            },
          ]
        },
        {
          title: "Arrears Reports ",
          display: true,
          specificReports: [
            {
              name: "ALL ARREARS REPORT",
              display: this.allarrearsreport,
              function: this.generateAllArrearsReport
            },
            {
              name: "ARREARS PER BRANCH REPORT",
              display: false,
              function: this.generateBranchArrearsReport
            },
            {
              name: "ARREARS ON LOAN PER ISSUER REPORT",
              display: false,
              function: this.generateArrearsOnLoansPerIssuerReport
            },
          ]
        },
        {
          title: "Repayments Reports",
          display: true,
          specificReports: [
            {
             isDynamic: true,
             requiredFields: ['fromdate', 'todate'],
             name: "MONTHLY LOAN REPAYMENT REPORT",
             filename: "monthly_loan_repayment.jrxml",
             display: this.allloandemandsreport,
             function: this.guarantorShipByPersonReport
           },
           {
            isDynamic: true,
            requiredFields: ['fromdate', 'todate'],
            name: "MONTHLY LOAN REPAYMENT REPORT BY ACCOUNT",
            filename: "monthly_loan_repayment_by_account.jrxml",
            display: this.allloandemandsreport,
            function: this.guarantorShipByPersonReport
          },
            {
              name: "ALL REPAYMENTS REPORT",
              display: this.allrepaymentsreport,
              function: this.generateAllRepaymentsReport
            },
            
           {
            isDynamic: true,
            requiredFields: ['fromdate', 'todate', 'acid'],
            name: "REPAYMENTS FOR A SPECIFIC LOAN REPORT",
            filename: "loan_demands_by_loan.jrxml",
            display: this.allloandemandsreport,
            function: this.guarantorShipByPersonReport
          },
            {
              name: "REPAYMENTS PER BRANCH REPORT",
              display: false,
              function: this.generateBranchRepaymentsReport
            },
            {
              name: "REPAYMENTS PER MANAGER REPORT",
              display: false,
              function: this.generateRepaymentsByManager
            },
          ]
        },
        {
          title: "General Operational Reports",
          display: true,
          specificReports: [
            {
              isDynamic: true,
              requiredFields: [],
              filename: "balancesheet.jrxml",
              name: "Balance Sheet Report",
              display: this.profitandlossreport,
              function: this.getAllMembersReport
            },            {
              isDynamic: true,
              requiredFields: ['fromdate', 'todate'],
              filename: "trial_balance.jrxml",
              name: "Trial Balance",
              display: this.profitandlossreport,
              function: this.getAllMembersReport
            },
            {
              isDynamic: true,
              requiredFields: [],
              filename: "profitloss.jrxml",
              name: "Profit and Loss Report",
              display: true,
              function: this.getAllMembersReport
            },
            {
              isDynamic: true,
              requiredFields: ['fromdate', 'todate'],
              filename: "profitloss_periodic.jrxml",
              name: "Profit and Loss Report",
              display: this.profitandlossreport,
              function: this.getAllMembersReport
            },
            {
              isDynamic: true,
              requiredFields: ['fromdate', 'todate'],
              filename: "transactionlogs.jrxml",
              name: "Transaction Logs Report",
              display: this.profitandlossreport,
              function: this.getAllMembersReport
            },
            {
              isDynamic: true,
              requiredFields: ['fromdate', 'todate'],
              filename: "profitloss_periodic_detailed.jrxml",
              name: "Detailed Profit and Loss Report",
              display: this.profitandlossreport,
              function: this.getAllMembersReport
            },
          ]
        },
        {
          title: "Expenses Reports",
          display: true,
          specificReports: [
            {
              name: "ALL EXPENSES REPORT",
              display: this.allexpensesreport,
              function: this.generateAllExpensesReport
            },
            {
              name: "BRANCH EXPENSES REPORT",
              display: this.branchexpensesreport,
              function: this.generateBranchExpensesReport
            },
          ]
        },
        {
          title: "Fees Report",
          display: true,
          specificReports: [
            {
              name: "ORGANISATION FEE REPORT",
              display: this.organisationfeereport,
              function: this.generateFeeReport
            },
            {
              name: "BRANCH FEE REPORT",
              display: this.branchfeereport,
              function: this.generateBranchFeeReport
            },
          ]
        },
        {
          title: "Roles And Users",
          display: true,
          specificReports: [            
            {
              isDynamic: true,
              requiredFields: [],
              filename: "users_list.jrxml",
              name: "ALL SYSTEM USERS REPORT",
              display: this.profitandlossreport,
              function: this.getAllMembersReport
            },
            {
              isDynamic: true,
              requiredFields: [],
              filename: "user_powers.jrxml",
              name: "User Powers Report",
              display: this.profitandlossreport,
              function: this.getAllMembersReport
            },
            {
              isDynamic: true,
              requiredFields: [],
              filename: "roles.jrxml",
              name: "All Roles Report",
              display: this.profitandlossreport,
              function: this.getAllMembersReport
            },
            {
              isDynamic: true,
              requiredFields: ['fromdate', 'todate'],
              filename: "auth_sessions.jrxml",
              name: "Use Sessions Report",
              display: this.profitandlossreport,
              function: this.getAllMembersReport
            },
          ]
        },
      ]
  }
  
  //loan_deposits.jrxml

  toAccountsReport(data, action, title) {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.disableClose = false;
    dialogConfig.autoFocus = true;
    dialogConfig.width = '800px';
    dialogConfig.data = {
      data: data,
      action: action,
      reportTitle: title,
    };
    const dialogRef = this.dialog.open(AccountStatementComponent, dialogConfig);
    dialogRef.afterClosed().subscribe((result) => {
      console.log(result);
    });
  }

  accountStatement = (): void => {
    console.log("Clicked account statement");
    this.toAccountsReport("", 'Account Statements', 'Account Statement');
  }

  corporateAccountsReport = (): void => {
    this.toAccountsReport("", 'corporateAccountsReport', 'Corporate Accounts Report');
  }
  groupAccountsReport = (): void => {
    this.toAccountsReport("", 'groupAccountsReport', 'Group Accounts Report');
  }

  retailsAccountsReport = (): void => {
    this.toAccountsReport("", 'Retail Accounts', "Retail Accounts");
  }

  cooperateAccountReport = (): void => {

    this.toAccountsReport("", 'Accounts By Status', "Accounts By Status");
  }

  accountTypesReport = (): void => {
    this.toAccountsReport("", 'Cooperate Accounts', "Cooporate Accounts");
  }

  accountsBySchemeReport = (): void => {
    this.toAccountsReport("", 'accountsBySchemeReport', "Accounts By Scheme Code Report");
  }

  accountByMemberReport = (): void => {
    this.toAccountsReport("", 'accountByMemberReport', "All Accounts For Specific Member");
  }

  accountsByGroupMembershipReport = (): void => {
    this.toAccountsReport("", 'accountsByGroupMembershipReport', "Accounts for Members in a House");
  }

  accountsByProductReport = () => {
    this.toAccountsReport("", 'accountsByProductReport', "Accounts by Product Code");
  }


  toToDisbursementComponent(data, action) {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.disableClose = false;
    dialogConfig.autoFocus = true;
    dialogConfig.width = '800px';
    dialogConfig.data = {
      data: data,
      action: action,
    };
    const dialogRef = this.dialog.open(
      DisbursementReportComponent,
      dialogConfig
    );
    dialogRef.afterClosed().subscribe((result) => {
      console.log(result);
    });
  }

  getAllDisbursements = (): void => {
    this.toToDisbursementComponent("", 'All Disbursements Report');
  }

  getBranchDisbursements = (): void => {
    this.toToDisbursementComponent("", 'Branch Disbursements');
  }

  getUserDisbursements = (): void => {
    this.toToDisbursementComponent("", 'User Disbursements');
  }

  gotToLoanPortfolioReports(data, action) {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.disableClose = false;
    dialogConfig.autoFocus = true;
    dialogConfig.width = '800px';
    dialogConfig.data = {
      data: data,
      action: action,
    };
    const dialogRef = this.dialog.open(
      LoanPortfolioReportsComponent,
      dialogConfig
    );
    dialogRef.afterClosed().subscribe((result) => {
      console.log(result);
    });
  }

  getLoanPortfolios = (): void => {
    this.gotToLoanPortfolioReports("", 'All Loan Portfolios');
  }

  getBranchLoanPortfolios = (): void => {
    this.gotToLoanPortfolioReports("", 'Branch Loan Portfolios');
  }

  getUserLoanPortfolios = (): void => {
    this.gotToLoanPortfolioReports("", 'User Loan Potfolios Reports');
  }

  goToCustomersReport(data, action, reportTitle) {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.disableClose = false;
    dialogConfig.autoFocus = true;
    dialogConfig.width = '800px';
    dialogConfig.data = {
      data: data,
      action: action,
      reportTitle: reportTitle,
    };
    const dialogRef = this.dialog.open(CustomerReportComponent, dialogConfig);
    dialogRef.afterClosed().subscribe((result) => {
      console.log(result);
    });
  }

  getRetailCustomers = (): void => {
    this.goToCustomersReport("", "All Retail Customers", 'All Retail Customers')
  }

  getBranchRetailCustomers = (): void => {
    this.goToCustomersReport("", "Branch Retail Customers", 'Branch Retail Customers');
  }

  getUserRetailCustomers = (): void => {
    this.goToCustomersReport("", "Retail Customers By Manager", 'Retail Customers By Manager');
  }

  generateRetailCustomersReport = (): void => {
    this.goToCustomersReport("", "Retail Customers", 'Retail Customers');
  }

  generateCorporateCustomersReport = (): void => {
    this.goToCustomersReport("", "getCorporateCustomersReport", 'Cooperate Customers');
  }

  generateAllArrearsReport = (): void => {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.disableClose = false;
    dialogConfig.autoFocus = true;
    dialogConfig.width = '800px';
    dialogConfig.data = {
      data: '',
      action: 'All Arrears Report',
    };
    const dialogRef = this.dialog.open(ArrearsReportComponent, dialogConfig);
    dialogRef.afterClosed().subscribe((result) => {
      console.log(result);
    });
  }

  generateBranchArrearsReport = (): void => {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.disableClose = false;
    dialogConfig.autoFocus = true;
    dialogConfig.width = '800px';
    dialogConfig.data = {
      data: '',
      action: 'Branch Arrears Report',
    };
    const dialogRef = this.dialog.open(ArrearsReportComponent, dialogConfig);
    dialogRef.afterClosed().subscribe((result) => {
      console.log(result);
    });
  }

  allGuarantorsReport = (): void => {
    console.log("Guarantor");
    this.gotoLoanDemandReport("", 'allGuarantorsReport', 'All Guarantors Report');
  }
  generateBranchExpensesReport = (): void => {

  }
  getAllMembersReport = (): void => {
    this.goToCustomersReport("", "getAllMembersReport", 'All Sacco Members');
  }
  getListOfHousesReport = (): void => {
    this.goToCustomersReport("", "getListOfHousesReport", 'List of Houses Report');
  }
  getMemberOfHouseReport = (): void => {
    this.goToCustomersReport("", "getMemberOfHouseReport", 'Member of House Report');
  }
  guarantorshipByLoanReport = (): void => {
    this.gotoLoanDemandReport("", 'guarantorshipByLoanReport', 'Guarantorship by Loan Report');
  }

  makeBack() {
    this.displayReport = false;
  }

  generateArrearsOnLoansPerIssuerReport = (): void => {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.disableClose = false;
    dialogConfig.autoFocus = true;
    dialogConfig.width = '800px';
    dialogConfig.data = {
      data: '',
      action: 'Reports Per Issuer',
    };
    const dialogRef = this.dialog.open(ArrearsReportComponent, dialogConfig);
    dialogRef.afterClosed().subscribe((result) => {
      console.log(result);
    });
  }

  generateAllRepaymentsReport = (): void => {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.disableClose = false;
    dialogConfig.autoFocus = true;
    dialogConfig.width = '800px';
    dialogConfig.data = {
      data: '',
      action: 'All Repayments Report',
    };
    const dialogRef = this.dialog.open(RepaymentsReportComponent, dialogConfig);
    dialogRef.afterClosed().subscribe((result) => {
      console.log(result);
    });
  }

  generateBranchRepaymentsReport = (): void => {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.disableClose = false;
    dialogConfig.autoFocus = true;
    dialogConfig.width = '800px';
    dialogConfig.data = {
      data: '',
      action: 'Branch Repayments Report',
    };
    const dialogRef = this.dialog.open(RepaymentsReportComponent, dialogConfig);
    dialogRef.afterClosed().subscribe((result) => {
      console.log(result);
    });
  }

  generateRepaymentsByManager = (): void => {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.disableClose = false;
    dialogConfig.autoFocus = true;
    dialogConfig.width = '800px';
    dialogConfig.data = {
      data: '',
      action: 'Repayments Per Issuer',
    };
    const dialogRef = this.dialog.open(RepaymentsReportComponent, dialogConfig);
    dialogRef.afterClosed().subscribe((result) => {
      console.log(result);
    });
  }

  goToTransactionsReport(data, action, reportTitle) {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.disableClose = false;
    dialogConfig.autoFocus = true;
    dialogConfig.width = '800px';
    dialogConfig.data = {
      data: data,
      action: action,
      reportTitle: reportTitle,
    };
    const dialogRef = this.dialog.open(TransactionReportsComponent, dialogConfig);
    dialogRef.afterClosed().subscribe((result) => {
      console.log(result);
    });
  }

  generateAllTransactionsReport = (): void => {
    this.goToTransactionsReport('', 'All Transactions', 'All User Transactions');
  }



  generateBranchTransactionsReport = (): void => {
    this.goToTransactionsReport('', 'Branch Transactions', 'Branch Transactions');
  }

  generateUserTransactionsReport = (): void => {
    this.toAccountsReport('', 'usertransactions', 'User Transactions');
  }

  generateProductTransactionsReport = (): void => {
    this.toAccountsReport('', 'producttransactions', 'Analysis of Product Transactions');
  }

  generateCustomerTransactionsReport = (): void => {
    this.toAccountsReport('', 'customerTransactionsReport', 'Analysis of Member Account Transactions');
  }

  generateAccountTypeTransactionsReport = (): void => {
    // this.toAccountsReport('',  'accounttypetransactionsreport', 'Account Type Transactions Reports');
    this.goToTransactionsReport('', 'Account Type Transactions Reports', 'Account Type Transactions Reports');
  }

  generateMemberAccountTransactionReport = (): void => {
    this.toAccountsReport('', 'memberAccountTransactionsreport', 'Member Account Transaction Report');

  }

  generateAllLoanDemandsReport = (): void => {
    this.gotoLoanDemandReport("", 'All Loan Demands Reports', 'All Loan Demands Reports');
  }

  generateBranchLoanDemandsReports = (): void => {
    this.gotoLoanDemandReport("", 'Branch Loan Demands Reports', 'Branch Loan Demands Reports');
  }

  generateUserLoanDemandsReports = (): void => {
    this.gotoLoanDemandReport("", 'Manager Loan Demands Reports', 'Manager Loan Demands Reports');
  }

  gotoLoanDemandReport = (data, action, title): void => {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.disableClose = false;
    dialogConfig.autoFocus = true;
    dialogConfig.width = '800px';
    dialogConfig.data = {
      data: data,
      action: action,
      reportTitle: title,
    };
    const dialogRef = this.dialog.open(
      LoanDemandsReportComponent,
      dialogConfig
    );
    dialogRef.afterClosed().subscribe((result) => {
      console.log(result);
    });
  }

  guarantorShipByPersonReport = (): void => {
    this.gotoLoanDemandReport("", 'Guarantor Loans Report', 'Guarantor Loans Report');
  }



  getBalanceSheet = (): void => {
    Swal.fire({
      title: 'Generate balance sheet report?',
      text: " ",
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#3085d6',
      cancelButtonColor: '#d33',
      confirmButtonText: 'Generate!',
    }).then((result) => {
      if (result.isConfirmed) {
        const params = new HttpParams();
        this.generateReportDynamically(params, this.reportsService.balanceSheetReport, "Balancesheet");
        // this.subscription = this.reportsService
        //   .generateBalanceSheet()
        //   .subscribe(
        //     (response) => {
        //       let url = window.URL.createObjectURL(response.data);

        //       window.open(url);


        //       console.log(url);

        //       // this._snackBar.open('Report generated successfully !', 'X', {
        //       //   horizontalPosition: this.horizontalPosition,
        //       //   verticalPosition: this.verticalPosition,
        //       //   duration: 3000,
        //       //   panelClass: ['green-snackbar', 'login-snackbar'],
        //       // });
        //     },
        //     (err) => {
        //       console.log(err);
        //       this._snackBar.open(`Error generating report !`, 'X', {
        //         horizontalPosition: this.horizontalPosition,
        //         verticalPosition: this.verticalPosition,
        //         duration: 3000,
        //         panelClass: ['red-snackbar', 'login-snackbar'],
        //       });
        //     }
        //   );
      }
    });
  }

  getBranchBalanceSheet = (): void => {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.disableClose = false;
    dialogConfig.autoFocus = true;
    dialogConfig.width = '800px';
    dialogConfig.data = {
      data: '',
      action: 'Branch Balance Sheet',
    };
    const dialogRef = this.dialog.open(
      BalanceSheetDialogComponent,
      dialogConfig
    );
    dialogRef.afterClosed().subscribe((result) => {
      console.log(result);
    });
  }

  generatebranchexpensesreport = (): void => {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.disableClose = false;
    dialogConfig.autoFocus = true;
    dialogConfig.width = '800px';
    dialogConfig.data = {
      data: '',
      action: 'Branch Expenses Report',
    };
    const dialogRef = this.dialog.open(
      ExpensesReportDialogueComponent,
      dialogConfig
    );
    dialogRef.afterClosed().subscribe((result) => {
      console.log(result);
    });
  }

  generateFeeReport = (): void => {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.disableClose = false;
    dialogConfig.autoFocus = true;
    dialogConfig.width = '800px';
    dialogConfig.data = {
      data: '',
      action: 'Fee Report',
    };
    const dialogRef = this.dialog.open(
      FeeReportDialogueComponent,
      dialogConfig
    );
    dialogRef.afterClosed().subscribe((result) => {
      console.log(result);
    });
  }

  generateBranchFeeReport = (): void => {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.disableClose = false;
    dialogConfig.autoFocus = true;
    dialogConfig.width = '800px';
    dialogConfig.data = {
      data: '',
      action: 'Branch Fee Report',
    };
    const dialogRef = this.dialog.open(
      FeeReportDialogueComponent,
      dialogConfig
    );
    dialogRef.afterClosed().subscribe((result) => {
      console.log(result);
    });
  }

  generateTrialBalanceReport = (): void => {
    Swal.fire({
      title: 'Generate trial balance report?',
      text: " ",
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#3085d6',
      cancelButtonColor: '#d33',
      confirmButtonText: 'Generate!',
    }).then((result) => {
      if (result.isConfirmed) {

        this.subscription = this.reportsService
          .generateTrialBalanceReport()
          .subscribe(
            (response) => {
              let url = window.URL.createObjectURL(response.data);

              window.open(url);

              this._snackBar.open('Report generated successfully!', 'X', {
                horizontalPosition: this.horizontalPosition,
                verticalPosition: this.verticalPosition,
                duration: 3000,
                panelClass: ['green-snackbar', 'login-snackbar'],
              });
            },
            (err) => {
              this.error = err;
              this.loading = false;

              this._snackBar.open(`Error generating report !`, 'X', {
                horizontalPosition: this.horizontalPosition,
                verticalPosition: this.verticalPosition,
                duration: 3000,
                panelClass: ['red-snackbar', 'login-snackbar'],
              });
            }
          );
      }
    });
  }

  generateRolesReport = (): void => {
    console.log(this.reportsService);
    Swal.fire({
      title: 'Generate All Roles Report?',
      text: " ",
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#3085d6',
      cancelButtonColor: '#d33',
      confirmButtonText: 'Generate!',
    }).then((result) => {
      if (result.isConfirmed) {
        const params = new HttpParams();
        console.log(this.reportsService);
        this.generateReportDynamically(params, this.reportsService.allRolesReport, "AllRolesReport");
      }
    });
  }

  generateAllExpensesReport = (): void => {
    console.log(this.reportsService);
    Swal.fire({
      title: 'Generate All Expenses Report?',
      text: " ",
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#3085d6',
      cancelButtonColor: '#d33',
      confirmButtonText: 'Generate!',
    }).then((result) => {
      if (result.isConfirmed) {
        const params = new HttpParams();
        console.log(this.reportsService);
        this.generateReportDynamically(params, this.reportsService.allExpensesReport, "AllExpensesReport");
      }
    });
  }

  generateAllUsersReport = (): void => {
    console.log(this.reportsService);
    Swal.fire({
      title: 'Generate All Users?',
      text: " ",
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#3085d6',
      cancelButtonColor: '#d33',
      confirmButtonText: 'Generate!',
    }).then((result) => {
      if (result.isConfirmed) {
        const params = new HttpParams();
        console.log(this.reportsService);
        this.generateReportDynamically(params, this.reportsService.allUsersReport, "AllUsersReport");
      }
    });
  }

  generateProfitAndLossReport = (): void => {
    console.log(this.reportsService);
    Swal.fire({
      title: 'Generate profit and loss report?',
      text: " ",
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#3085d6',
      cancelButtonColor: '#d33',
      confirmButtonText: 'Generate!',
    }).then((result) => {
      if (result.isConfirmed) {
        const params = new HttpParams();
        console.log(this.reportsService);
        this.generateReportDynamically(params, this.reportsService.profitAndLossUrl, "ProfitAndLoss");
      }
    });
  }

  generateReportDynamically(params, url, filename) {
    this.subscription = this.reportsService
      .dynamicReportGenerator(params, url, filename)
      .subscribe(
        (response) => {
          let url = window.URL.createObjectURL(response.data);

          window.open(url);

          console.log(url);

          this.loading = false;

          this._snackBar.open('Report generated successfully !', 'X', {
            horizontalPosition: this.horizontalPosition,
            verticalPosition: this.verticalPosition,
            duration: 3000,
            panelClass: ['green-snackbar', 'login-snackbar'],
          });
        },
        (err) => {
          this.error = err;
          this.loading = false;


          this._snackBar.open(`Error generating report !`, 'X', {
            horizontalPosition: this.horizontalPosition,
            verticalPosition: this.verticalPosition,
            duration: 3000,
            panelClass: ['red-snackbar', 'login-snackbar'],
          });
        }
      );
  }

  generateBranchTrialBalanceReport = (): void => {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.disableClose = false;
    dialogConfig.autoFocus = true;
    dialogConfig.width = '800px';
    dialogConfig.data = {
      data: '',
      action: 'Branch Trial Balance Report',
    };
    const dialogRef = this.dialog.open(
      TrialBalanceReportDialogueComponent,
      dialogConfig
    );
    dialogRef.afterClosed().subscribe((result) => {
      console.log(result);
    });
  }
}
