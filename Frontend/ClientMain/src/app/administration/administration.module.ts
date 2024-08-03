import { AgmCoreModule } from '@agm/core';
import { CommonModule, DatePipe } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { DataTablesModule } from 'angular-datatables';
import { HighchartsChartModule } from 'highcharts-angular';
import { MatTableExporterModule } from 'mat-table-exporter';
import { authInterceptorProviders } from 'src/@core/helpers/auth.interceptor';
import { MaterialModule } from '../material.module';
import { AdministrationRoutingModule } from './administration-routing.module';
import { AdministrationComponent } from './administration.component';
import { FooterComponent } from './layouts/footer/footer.component';
import { HeaderComponent } from './layouts/header/header.component';
import { MenuOptionBarComponent } from './layouts/menu-option-bar/menu-option-bar.component';
import { SidebarComponent } from './layouts/sidebar/sidebar.component';
import { PageError404Component } from './page-error404/page-error404.component';
import { PageError500Component } from './page-error500/page-error500.component';
import { RolesLookupComponent } from './pages/AccessManagement/roles-management/roles-lookup/roles-lookup.component';
import { RolesMaintenanceComponent } from './pages/AccessManagement/roles-management/roles-maintenance/roles-maintenance.component';
import { RolesManagementComponent } from './pages/AccessManagement/roles-management/roles-management.component';
import { TellerLookupMaintainedComponent } from './pages/AccessManagement/tellers-management/teller-lookup-maintained/teller-lookup-maintained.component';
import { TellerLookupComponent } from './pages/AccessManagement/tellers-management/teller-lookup/teller-lookup.component';
import { TellerMaintenanceComponent } from './pages/AccessManagement/tellers-management/teller-maintenance/teller-maintenance.component';
import { TellersManagementComponent } from './pages/AccessManagement/tellers-management/tellers-management.component';
import { CreateUserComponent } from './pages/AccessManagement/user-management/create-user/create-user.component';
import { PassowrdResetComponent } from './pages/AccessManagement/user-management/passowrd-reset/passowrd-reset.component';
import { UpdateUserComponent } from './pages/AccessManagement/user-management/update-user/update-user.component';
import { UserManagementComponent } from './pages/AccessManagement/user-management/user-management.component';
import { UserProfileComponent } from './pages/AccessManagement/user-management/user-profile/user-profile.component';
import { WorkClassActionsMaintenanceComponent } from './pages/AccessManagement/work-class-actions/work-class-actions-maintenance/work-class-actions-maintenance.component';
import { WorkClassActionsComponent } from './pages/AccessManagement/work-class-actions/work-class-actions/work-class-actions.component';
import { WorkClassLookupComponent } from './pages/AccessManagement/work-class/work-class-lookup/work-class-lookup.component';
import { WorkClassMaintenanceComponent } from './pages/AccessManagement/work-class/work-class-maintenance/work-class-maintenance.component';
import { WorkClassComponent } from './pages/AccessManagement/work-class/work-class.component';
import { AccountClosureMaintenanceComponent } from './pages/Account-Component/account-closure/account-closure-maintenance/account-closure-maintenance.component';
import { AccountClosureComponent } from './pages/Account-Component/account-closure/account-closure.component';
import { AccountDoumentsComponent } from './pages/Account-Component/account-douments/account-douments.component';
import { AccountStatementMaintenanceComponent } from './pages/Account-Component/account-statements/account-statement-maintenance/account-statement-maintenance.component';
import { GeneralAccountStatementComponent } from './pages/Account-Component/account-statements/general-account-statement/general-account-statement.component';
import { AccountUsersComponent } from './pages/Account-Component/account-users/account-users.component';
import { AccountsApprovalComponent } from './pages/Account-Component/accounts-approval/accounts-approval.component';
import { AllAccountsLookUpComponent } from './pages/Account-Component/all-accounts-look-up/all-accounts-look-up.component';
import { GeneralAccountDetailsComponent } from './pages/Account-Component/general-account-details/general-account-details.component';
import { GeneralAccountsLookupComponent } from './pages/Account-Component/general-accounts-lookup/general-accounts-lookup.component';
import { LoanDisbursementLookUpComponent } from './pages/Account-Component/loan-account/Loan-disbursement/loan-disbursement-look-up/loan-disbursement-look-up.component';
import { LoanDisbursementMaintenanceComponent } from './pages/Account-Component/loan-account/Loan-disbursement/loan-disbursement-maintenance/loan-disbursement-maintenance.component';
import { ForceLoanDemandsComponent } from './pages/Account-Component/loan-account/force-loan-demands/force-loan-demands.component';
import { LoanAccountMaintenanceComponent } from './pages/Account-Component/loan-account/loan-account-maintenance/loan-account-maintenance.component';
import { CollateralDialogComponent } from './pages/Account-Component/loan-account/loan-account/collateral-dialog/collateral-dialog.component';
import { LoanAccountComponent } from './pages/Account-Component/loan-account/loan-account/loan-account.component';
import { UpdateCollateralDialogComponent } from './pages/Account-Component/loan-account/loan-account/update-collateral-dialog/update-collateral-dialog.component';
import { LoanInformationComponent } from './pages/Account-Component/loan-account/loan-information/loan-information.component';
import { LoanPenaltiesLookupComponent } from './pages/Account-Component/loan-account/loan-penalties/loan-penalties-lookup/loan-penalties-lookup.component';
import { ManualLoanPenaltiesComponent } from './pages/Account-Component/loan-account/loan-penalties/manual-loan-penalties/manual-loan-penalties.component';
import { ManualPenaltiesMaintenanceComponent } from './pages/Account-Component/loan-account/loan-penalties/manual-penalties-maintenance/manual-penalties-maintenance.component';
import { LoanRepaymentComponent } from './pages/Account-Component/loan-account/loan-repayment/loan-repayment.component';
import { LoansAccountsLookUpComponent } from './pages/Account-Component/loan-account/loans-accounts-look-up/loans-accounts-look-up.component';
import { LoanFeeRepaymentComponent } from './pages/Account-Component/loan-account/repayment/loan-fee-repayment/loan-fee-repayment.component';
import { LoanRepaymentMaintenanceComponent } from './pages/Account-Component/loan-account/repayment/loan-repayment-maintenance/loan-repayment-maintenance.component';
import { LoanRestructureComponent } from './pages/Account-Component/loan-account/restructure/loan-restructure/loan-restructure.component';
import { RestructureManitenanceComponent } from './pages/Account-Component/loan-account/restructure/restructure-manitenance/restructure-manitenance.component';
import { OfficeAccountMaintenanceComponent } from './pages/Account-Component/office-account/office-account-maintenance/office-account-maintenance.component';
import { OfficeAccountComponent } from './pages/Account-Component/office-account/office-account.component';
import { OfficeAccountsLookUpsComponent } from './pages/Account-Component/office-account/office-accounts-look-ups/office-accounts-look-ups.component';
import { OfficeTellerAccountsLookupComponent } from './pages/Account-Component/office-account/office-teller-accounts-lookup/office-teller-accounts-lookup.component';
import { RejectAccountComponent } from './pages/Account-Component/reject-account/reject-account.component';
import { RepaymentAccountsComponent } from './pages/Account-Component/repayment-accounts/repayment-accounts.component';
import { LiensCollectionComponent } from './pages/Account-Component/savings-account/liens-collection/liens-collection.component';
import { LiensLookupComponent } from './pages/Account-Component/savings-account/liens-lookup/liens-lookup.component';
import { LiensMaintenanceComponent } from './pages/Account-Component/savings-account/liens-maintenance/liens-maintenance.component';
import { SavingsAccountComponent } from './pages/Account-Component/savings-account/savings-account.component';
import { SavingsContributionLookupComponent } from './pages/Account-Component/savings-account/savings-contribution/savings-contribution-lookup/savings-contribution-lookup.component';
import { SavingsContributionMaintenanceComponent } from './pages/Account-Component/savings-account/savings-contribution/savings-contribution-maintenance/savings-contribution-maintenance.component';
import { SavingsContributionComponent } from './pages/Account-Component/savings-account/savings-contribution/savings-contribution.component';
import { SavingsLookupComponent } from './pages/Account-Component/savings-account/savings-lookup/savings-lookup.component';
import { SavingsMaintenanceComponent } from './pages/Account-Component/savings-account/savings-maintenance/savings-maintenance.component';
import { TermDepositAccountLookUpComponent } from './pages/Account-Component/term-deposit/term-deposit-account-look-up/term-deposit-account-look-up.component';
import { TermDepositAccountMaintenanceComponent } from './pages/Account-Component/term-deposit/term-deposit-account-maintenance/term-deposit-account-maintenance.component';
import { TermDepositAccountComponent } from './pages/Account-Component/term-deposit/term-deposit-account/term-deposit-account.component';
import { GroupMembershipDetailsComponent } from './pages/MembershipComponent/GroupMembership/group-membership-details/group-membership-details.component';
import { GroupMembershipDocumentsComponent } from './pages/MembershipComponent/GroupMembership/group-membership-documents/group-membership-documents.component';
import { GroupMembershipLookUpComponent } from './pages/MembershipComponent/GroupMembership/group-membership-look-up/group-membership-look-up.component';
import { GroupMembershipMaintenanceComponent } from './pages/MembershipComponent/GroupMembership/group-membership-maintenance/group-membership-maintenance.component';
import { NewGroupMembershipComponent } from './pages/MembershipComponent/GroupMembership/new-group-membership/new-group-membership.component';
import { MembershipDocumentsComponent } from './pages/MembershipComponent/Membership/membership-documents/membership-documents.component';
import { MembershipLookUpComponent } from './pages/MembershipComponent/Membership/membership-look-up/membership-look-up.component';
import { MembershipMaintenanceComponent } from './pages/MembershipComponent/Membership/membership-maintenance/membership-maintenance.component';
import { NewMembershipComponent } from './pages/MembershipComponent/Membership/new-membership/new-membership.component';
import { UniversalMembershipLookUpComponent } from './pages/MembershipComponent/universal-membership-look-up/universal-membership-look-up.component';
import { CurrentAccountProductLookupComponent } from './pages/ProductModule/current-account-product/current-account-product-lookup/current-account-product-lookup.component';
import { CurrentAccountProductMaintenanceComponent } from './pages/ProductModule/current-account-product/current-account-product-maintenance/current-account-product-maintenance.component';
import { CurrentAccountProductComponent } from './pages/ProductModule/current-account-product/current-account-product/current-account-product.component';
import { GeneralProductLookUpComponent } from './pages/ProductModule/general-product-look-up/general-product-look-up.component';
import { LoansProductLookUpComponent } from './pages/ProductModule/loans-product/loans-product-look-up/loans-product-look-up.component';
import { LoansProductMaintenanceComponent } from './pages/ProductModule/loans-product/loans-product-maintenance/loans-product-maintenance.component';
import { LoansProductComponent } from './pages/ProductModule/loans-product/loans-product/loans-product.component';
import { OfficeProductLookUpComponent } from './pages/ProductModule/office-product/office-product-look-up/office-product-look-up.component';
import { OfficeProductMaintenanceComponent } from './pages/ProductModule/office-product/office-product-maintenance/office-product-maintenance.component';
import { OfficeProductComponent } from './pages/ProductModule/office-product/office-product.component';
import { OverDraftProductLookupComponent } from './pages/ProductModule/overdraft-product/over-draft-product-lookup/over-draft-product-lookup.component';
import { OverDraftProductMaintenanceComponent } from './pages/ProductModule/overdraft-product/over-draft-product-maintenance/over-draft-product-maintenance.component';
import { OverDraftProductComponent } from './pages/ProductModule/overdraft-product/over-draft-product/over-draft-product.component';
import { SavingsProductLookupComponent } from './pages/ProductModule/savings/savings-product-lookup/savings-product-lookup.component';
import { SavingsProductMaintenanceComponent } from './pages/ProductModule/savings/savings-product-maintenance/savings-product-maintenance.component';
import { SavingsProductComponent } from './pages/ProductModule/savings/savings-product/savings-product.component';
import { TermDepositLookupComponent } from './pages/ProductModule/term-deposit/term-deposit-lookup/term-deposit-lookup.component';
import { TermDepositMaintenanceComponent } from './pages/ProductModule/term-deposit/term-deposit-maintenance/term-deposit-maintenance.component';
import { TermDepositProductComponent } from './pages/ProductModule/term-deposit/term-deposit-product/term-deposit-product.component';
import { ChrgPreferentialLookupComponent } from './pages/SystemConfigurations/ChargesParams/chrg-preferential/chrg-preferential-lookup/chrg-preferential-lookup.component';
import { ChrgPreferentialMaintenanceComponent } from './pages/SystemConfigurations/ChargesParams/chrg-preferential/chrg-preferential-maintenance/chrg-preferential-maintenance.component';
import { ChrgPreferentialComponent } from './pages/SystemConfigurations/ChargesParams/chrg-preferential/chrg-preferential.component';
import { ChrgPrioritizationLookupComponent } from './pages/SystemConfigurations/ChargesParams/chrg-prioritization/chrg-prioritization-lookup/chrg-prioritization-lookup.component';
import { ChrgPrioritizationMaintenanceComponent } from './pages/SystemConfigurations/ChargesParams/chrg-prioritization/chrg-prioritization-maintenance/chrg-prioritization-maintenance.component';
import { ChrgPrioritizationComponent } from './pages/SystemConfigurations/ChargesParams/chrg-prioritization/chrg-prioritization.component';
import { EventIdLookupComponent } from './pages/SystemConfigurations/ChargesParams/event-id/event-id-lookup/event-id-lookup.component';
import { EventIdMaintenanceComponent } from './pages/SystemConfigurations/ChargesParams/event-id/event-id-maintenance/event-id-maintenance.component';
import { EventIdComponent } from './pages/SystemConfigurations/ChargesParams/event-id/event-id.component';
import { EventTypeLookupComponent } from './pages/SystemConfigurations/ChargesParams/event-type/event-type-lookup/event-type-lookup.component';
import { EventTypeMaintenanceComponent } from './pages/SystemConfigurations/ChargesParams/event-type/event-type-maintenance/event-type-maintenance.component';
import { EventTypeComponent } from './pages/SystemConfigurations/ChargesParams/event-type/event-type.component';
import { AllBranchesLookupComponent } from './pages/SystemConfigurations/GlobalParams/branches/all-branches-lookup/all-branches-lookup.component';
import { BranchesLookupComponent } from './pages/SystemConfigurations/GlobalParams/branches/branches-lookup/branches-lookup.component';
import { BranchesMaintenanceComponent } from './pages/SystemConfigurations/GlobalParams/branches/branches-maintenance/branches-maintenance.component';
import { BranchesComponent } from './pages/SystemConfigurations/GlobalParams/branches/branches.component';
import { ChequeParamsLookUpComponent } from './pages/SystemConfigurations/GlobalParams/cheque-params/cheque-params-look-up/cheque-params-look-up.component';
import { ChequeParamsMaintenanceComponent } from './pages/SystemConfigurations/GlobalParams/cheque-params/cheque-params-maintenance/cheque-params-maintenance.component';
import { ChequeParamsComponent } from './pages/SystemConfigurations/GlobalParams/cheque-params/cheque-params.component';
import { CurrencyConfigComponent } from './pages/SystemConfigurations/GlobalParams/currency-config/currency-config.component';
import { CurrencyLookupComponent } from './pages/SystemConfigurations/GlobalParams/currency-config/currency-lookup/currency-lookup.component';
import { CurrencyMaintenanceComponent } from './pages/SystemConfigurations/GlobalParams/currency-config/currency-maintenance/currency-maintenance.component';
import { EmployeeConfigLookupComponent } from './pages/SystemConfigurations/GlobalParams/employee-config/employee-config-lookup/employee-config-lookup.component';
import { EmployeeConfigMaintenanceComponent } from './pages/SystemConfigurations/GlobalParams/employee-config/employee-config-maintenance/employee-config-maintenance.component';
import { EmployeeConfigComponent } from './pages/SystemConfigurations/GlobalParams/employee-config/employee-config.component';
import { ExceptionsCodesLookupComponent } from './pages/SystemConfigurations/GlobalParams/exceptions-codes/exceptions-codes-lookup/exceptions-codes-lookup.component';
import { ExceptionsCodesMaintenanceComponent } from './pages/SystemConfigurations/GlobalParams/exceptions-codes/exceptions-codes-maintenance/exceptions-codes-maintenance.component';
import { ExceptionsCodesComponent } from './pages/SystemConfigurations/GlobalParams/exceptions-codes/exceptions-codes.component';
import { GlCodeLookupComponent } from './pages/SystemConfigurations/GlobalParams/gl-code/gl-code-lookup/gl-code-lookup.component';
import { GlCodeMaintenanceComponent } from './pages/SystemConfigurations/GlobalParams/gl-code/gl-code-maintenance/gl-code-maintenance.component';
import { GlCodeComponent } from './pages/SystemConfigurations/GlobalParams/gl-code/gl-code.component';
import { GlSubheadLookupComponent } from './pages/SystemConfigurations/GlobalParams/gl-subhead/gl-subhead-lookup/gl-subhead-lookup.component';
import { GlSubheadMaintenanceComponent } from './pages/SystemConfigurations/GlobalParams/gl-subhead/gl-subhead-maintenance/gl-subhead-maintenance.component';
import { GlSubheadComponent } from './pages/SystemConfigurations/GlobalParams/gl-subhead/gl-subhead.component';
import { GuarantorsParamsLookupComponent } from './pages/SystemConfigurations/GlobalParams/guarantors-params/guarantors-params-lookup/guarantors-params-lookup.component';
import { GuarantorsParamsMaintenanceComponent } from './pages/SystemConfigurations/GlobalParams/guarantors-params/guarantors-params-maintenance/guarantors-params-maintenance.component';
import { GuarantorsParamsComponent } from './pages/SystemConfigurations/GlobalParams/guarantors-params/guarantors-params.component';
import { HolidayConfigComponent } from './pages/SystemConfigurations/GlobalParams/holiday-config/holiday-config.component';
import { HolidayLookupComponent } from './pages/SystemConfigurations/GlobalParams/holiday-config/holiday-lookup/holiday-lookup.component';
import { HolidayMaintenanceComponent } from './pages/SystemConfigurations/GlobalParams/holiday-config/holiday-maintenance/holiday-maintenance.component';
import { LinkedOrganizationLookupComponent } from './pages/SystemConfigurations/GlobalParams/linked-organization/linked-organization-lookup/linked-organization-lookup.component';
import { LinkedOrganizationMaintenanceComponent } from './pages/SystemConfigurations/GlobalParams/linked-organization/linked-organization-maintenance/linked-organization-maintenance.component';
import { LinkedOrganizationComponent } from './pages/SystemConfigurations/GlobalParams/linked-organization/linked-organization.component';
import { MainClassificationLookupComponent } from './pages/SystemConfigurations/GlobalParams/main-classifications/main-classification-lookup/main-classification-lookup.component';
import { MainClassificationMaintenanceComponent } from './pages/SystemConfigurations/GlobalParams/main-classifications/main-classification-maintenance/main-classification-maintenance.component';
import { MainClassificationsComponent } from './pages/SystemConfigurations/GlobalParams/main-classifications/main-classifications.component';
import { MembershipConfigLookupComponent } from './pages/SystemConfigurations/GlobalParams/membership-config/membership-config-lookup/membership-config-lookup.component';
import { MembershipConfigMaintenanceComponent } from './pages/SystemConfigurations/GlobalParams/membership-config/membership-config-maintenance/membership-config-maintenance.component';
import { MembershipConfigComponent } from './pages/SystemConfigurations/GlobalParams/membership-config/membership-config.component';
import { MisSectorLookupComponent } from './pages/SystemConfigurations/GlobalParams/mis-sector/mis-sector-lookup/mis-sector-lookup.component';
import { MisSectorMaintenanceComponent } from './pages/SystemConfigurations/GlobalParams/mis-sector/mis-sector-maintenance/mis-sector-maintenance.component';
import { MisSectorComponent } from './pages/SystemConfigurations/GlobalParams/mis-sector/mis-sector.component';
import { MisSubSectorLookupComponent } from './pages/SystemConfigurations/GlobalParams/mis-sub-sector/mis-sub-sector-lookup/mis-sub-sector-lookup.component';
import { MisSubSectorMaintenanceComponent } from './pages/SystemConfigurations/GlobalParams/mis-sub-sector/mis-sub-sector-maintenance/mis-sub-sector-maintenance.component';
import { MisSubSectorComponent } from './pages/SystemConfigurations/GlobalParams/mis-sub-sector/mis-sub-sector.component';
import { SalaryChargesLookUpComponent } from './pages/SystemConfigurations/GlobalParams/salary-charges/salary-charges-look-up/salary-charges-look-up.component';
import { SalaryChargesMaintenanceComponent } from './pages/SystemConfigurations/GlobalParams/salary-charges/salary-charges-maintenance/salary-charges-maintenance.component';
import { SalaryChargesComponent } from './pages/SystemConfigurations/GlobalParams/salary-charges/salary-charges.component';
import { SchemeTypeLookupComponent } from './pages/SystemConfigurations/GlobalParams/scheme-type/scheme-type-lookup/scheme-type-lookup.component';
import { SchemeTypeMaintenanceComponent } from './pages/SystemConfigurations/GlobalParams/scheme-type/scheme-type-maintenance/scheme-type-maintenance.component';
import { SchemeTypeComponent } from './pages/SystemConfigurations/GlobalParams/scheme-type/scheme-type.component';
import { SegmentLookupComponent } from './pages/SystemConfigurations/GlobalParams/segments/segment-lookup/segment-lookup.component';
import { SegmentMaintenanceComponent } from './pages/SystemConfigurations/GlobalParams/segments/segment-maintenance/segment-maintenance.component';
import { SegmentsComponent } from './pages/SystemConfigurations/GlobalParams/segments/segments.component';
import { ShareCapitalParamsLookupComponent } from './pages/SystemConfigurations/GlobalParams/share-capital-params/share-capital-params-lookup/share-capital-params-lookup.component';
import { ShareCapitalParamsMaintenanceComponent } from './pages/SystemConfigurations/GlobalParams/share-capital-params/share-capital-params-maintenance/share-capital-params-maintenance.component';
import { ShareCapitalParamsComponent } from './pages/SystemConfigurations/GlobalParams/share-capital-params/share-capital-params.component';
import { SubSegmentLookupComponent } from './pages/SystemConfigurations/GlobalParams/sub-segment/sub-segment-lookup/sub-segment-lookup.component';
import { SubSegmentMaintenanceComponent } from './pages/SystemConfigurations/GlobalParams/sub-segment/sub-segment-maintenance/sub-segment-maintenance.component';
import { SubSegmentComponent } from './pages/SystemConfigurations/GlobalParams/sub-segment/sub-segment/sub-segment.component';
import { WelfareLookUpComponent } from './pages/SystemConfigurations/GlobalParams/welfare/welfare-look-up/welfare-look-up.component';
import { WelfareMaintenanceComponent } from './pages/SystemConfigurations/GlobalParams/welfare/welfare-maintenance/welfare-maintenance.component';
import { WelfareComponent } from './pages/SystemConfigurations/GlobalParams/welfare/welfare.component';
import { InterestcodeComponent } from './pages/SystemConfigurations/interestcode/interestcode.component';
import { InterestcodelookupComponent } from './pages/SystemConfigurations/interestcode/interestcodelookup/interestcodelookup.component';
import { InterestcodemaintenanceComponent } from './pages/SystemConfigurations/interestcode/interestcodemaintenance/interestcodemaintenance.component';
import { ChequeBookLookupComponent } from './pages/cheque-books/cheque-book-lookup/cheque-book-lookup.component';
import { ChequeBookMaintenanceComponent } from './pages/cheque-books/cheque-book-maintenance/cheque-book-maintenance.component';
import { ChequeBooksComponent } from './pages/cheque-books/cheque-books.component';
import { CollateralLookupComponent } from './pages/collateral-limits/collateral/collateral-lookup/collateral-lookup.component';
import { CollateralMaintenanceComponent } from './pages/collateral-limits/collateral/collateral-maintenance/collateral-maintenance.component';
import { CollateralComponent } from './pages/collateral-limits/collateral/collateral.component';
import { DocumentsComponent } from './pages/collateral-limits/collateral/documents/documents.component';
import { DashboardComponent } from './pages/dashboard/dashboard.component';
import { MainDashboardPageComponent } from './pages/dashboard/main-dashboard-page/main-dashboard-page.component';
import { SpinnerComponent } from './pages/dashboard/spinner/spinner.component';
import { WidgetChargesComponent } from './pages/dashboard/widget-charges/widget-charges.component';
import { WidgetLendingComponent } from './pages/dashboard/widget-lending/widget-lending.component';
import { WidgetMembershipComponent } from './pages/dashboard/widget-membership/widget-membership.component';
import { WidgetShareCapitalComponent } from './pages/dashboard/widget-share-capital/widget-share-capital.component';
import { EndOfDayComponent } from './pages/end-of-day/end-of-day.component';
import { NotificationAllertsComponent } from './pages/notifications/notification-allerts/notification-allerts.component';
import { AccountsLookupComponent } from './pages/reports/accounts-lookup/accounts-lookup.component';
import { SchemeCodeLookupComponent } from './pages/reports/accounts-lookup/scheme-code-lookup/scheme-code-lookup.component';
import { SolCodeLookupComponent } from './pages/reports/accounts-lookup/sol-code-lookup/sol-code-lookup.component';
import { DisplayReportComponent } from './pages/reports/display-report/display-report.component';
import { DynamicReportLookupComponent } from './pages/reports/dynamic-report-lookup/dynamic-report-lookup.component';
import { ReportMaintainanceComponent } from './pages/reports/report-maintainance/report-maintainance.component';
import { ReportsComponent } from './pages/reports/reports.component';
import { SasraReportDialogueComponent } from './pages/reports/sasra-reports/sasra-report-dialogue/sasra-report-dialogue.component';
import { SasraReportsComponent } from './pages/reports/sasra-reports/sasra-reports.component';
import { SpecificReportComponent } from './pages/reports/specific-report/specific-report.component';
import { AccountStatementComponent } from './pages/reports/view-report/account-statement/account-statement.component';
import { ArrearsReportComponent } from './pages/reports/view-report/arrears-report/arrears-report.component';
import { ARREARSGENERALSTATEMENTComponent } from './pages/reports/view-report/arrearsgeneralstatement/arrearsgeneralstatement.component';
import { ASSETSCLASSIFICATIONSComponent } from './pages/reports/view-report/assetsclassifications/assetsclassifications.component';
import { BalanceSheetDialogComponent } from './pages/reports/view-report/balance-sheet-dialog/balance-sheet-dialog.component';
import { CustomerReportComponent } from './pages/reports/view-report/customer-report/customer-report.component';
import { DisbursementReportComponent } from './pages/reports/view-report/disbursement-report/disbursement-report.component';
import { ExpensesReportDialogueComponent } from './pages/reports/view-report/expenses-report-dialogue/expenses-report-dialogue.component';
import { FeeReportDialogueComponent } from './pages/reports/view-report/fee-report-dialogue/fee-report-dialogue.component';
import { LoanDemandsReportComponent } from './pages/reports/view-report/loan-demands-report/loan-demands-report.component';
import { LoanPortfolioReportsComponent } from './pages/reports/view-report/loan-portfolio-reports/loan-portfolio-reports.component';
import { LOanStatementComponent } from './pages/reports/view-report/loan-statement/loan-statement.component';
import { OfficeAccountsComponent } from './pages/reports/view-report/office-accounts/office-accounts.component';
import { RepaymentsReportComponent } from './pages/reports/view-report/repayments-report/repayments-report.component';
import { TransactionReportsComponent } from './pages/reports/view-report/transaction-reports/transaction-reports.component';
import { TrialBalanceReportDialogueComponent } from './pages/reports/view-report/trial-balance-report-dialogue/trial-balance-report-dialogue.component';
import { ViewReportComponent } from './pages/reports/view-report/view-report.component';
import { BatchSalariesLookupComponent } from './pages/salary-transactions/batch-salaries/batch-salaries-lookup/batch-salaries-lookup.component';
import { BatchSalariesMaintenanceComponent } from './pages/salary-transactions/batch-salaries/batch-salaries-maintenance/batch-salaries-maintenance.component';
import { BatchSalariesComponent } from './pages/salary-transactions/batch-salaries/batch-salaries.component';
import { ExcelUploadComponent } from './pages/salary-transactions/excel-upload/excel-upload.component';
import { FilesService } from './pages/salary-transactions/salary-transactions-data/fileconversion/files.service';
import { SafePipe } from './pages/salary-transactions/salary-transactions-data/fileconversion/safe.pipe';
import { SalaryTransactionsDataComponent } from './pages/salary-transactions/salary-transactions-data/salary-transactions-data.component';
import { SalaryTransactionsLookupComponent } from './pages/salary-transactions/salary-transactions-lookup/salary-transactions-lookup.component';
import { SalaryTransactionsComponent } from './pages/salary-transactions/salary-transactions.component';
import { CustomerSharesLookupComponent } from './pages/share-capital/customer-shares-lookup/customer-shares-lookup.component';
import { ShareCapitalInstallmentsComponent } from './pages/share-capital/share-capital-installments/share-capital-installments.component';
import { ShareCapitalMaintenanceComponent } from './pages/share-capital/share-capital-maintenance/share-capital-maintenance.component';
import { ShareCapitalComponent } from './pages/share-capital/share-capital.component';
import { SharesLookupComponent } from './pages/share-capital/shares-lookup/shares-lookup.component';
import { AccountImagesLookupComponent } from './pages/transaction-execution/account-images-lookup/account-images-lookup.component';
import { CashTransactionExecutionComponent } from './pages/transaction-execution/cash-transaction-execution/cash-transaction-execution.component';
import { GeneralTransactionLookUpComponent } from './pages/transaction-execution/general-transaction-look-up/general-transaction-look-up.component';
import { KraexcisedutyComponent } from './pages/transaction-execution/kraexciseduty/kraexciseduty.component';
import { NormalCashTransactionsComponent } from './pages/transaction-execution/normal-cash-transactions/normal-cash-transactions.component';
import { NormalTransactionComponent } from './pages/transaction-execution/normal-transaction/normal-transaction.component';
import { StandingOrdersLookupComponent } from './pages/transaction-execution/standingOrders/standing-orders-lookup/standing-orders-lookup.component';
import { StandingOrdersMaintenanceComponent } from './pages/transaction-execution/standingOrders/standing-orders-maintenance/standing-orders-maintenance.component';
import { StandingOrdersComponent } from './pages/transaction-execution/standingOrders/standing-orders/standing-orders.component';
import { TransactionApprovalComponent } from './pages/transaction-execution/transaction-approval/transaction-approval.component';
import { TransactionChequesLookupComponent } from './pages/transaction-execution/transaction-cheques/transaction-cheques-lookup/transaction-cheques-lookup.component';
import { TransactionChequesMaintenanceComponent } from './pages/transaction-execution/transaction-cheques/transaction-cheques-maintenance/transaction-cheques-maintenance.component';
import { TransactionChequesComponent } from './pages/transaction-execution/transaction-cheques/transaction-cheques.component';
import { TransactionExecutionLookupComponent } from './pages/transaction-execution/transaction-execution-lookup/transaction-execution-lookup.component';
import { TransactionExecutionMainComponent } from './pages/transaction-execution/transaction-execution-main/transaction-execution-main.component';
import { TransactionExecutionComponent } from './pages/transaction-execution/transaction-execution.component';
import { UniversalInquiryComponent } from './pages/universal-inquiry/universal-inquiry.component';
import { AreaComponent } from './widgets/area/area.component';
import { CardComponent } from './widgets/card/card.component';
import { PieComponent } from './widgets/pie/pie.component';
import { SBCNotificationsComponent } from './pages/notifications/sbcnotifications/sbcnotifications.component';
import { RejectTransactionComponent } from './pages/transaction-execution/reject-transaction/reject-transaction.component';
import { OfficeAccountsByGlsComponent } from './pages/Account-Component/office-account/office-accounts-by-gls/office-accounts-by-gls.component';
import { ExciseDutiesComponent } from './pages/transaction-execution/exciseDuties/excise-duties/excise-duties.component';
import { KratariffComponent } from './pages/transaction-execution/exciseDuties/kratariff/kratariff.component';
import { MemberDocumentsComponent } from './pages/transaction-execution/member-documents/member-documents.component';
import { DirectApprovalsComponent } from './pages/notifications/notification-allerts/direct-approvals/direct-approvals.component';
import { UnpostedChequesComponent } from './pages/transaction-execution/transaction-cheques/unposted-cheques/unposted-cheques.component';

import { MassiveInterestComponent } from './pages/Account-Component/loan-account/massive-interest/massive-interest.component';

import { ClassificationLookupComponent } from './pages/GeneralLookups/classification-lookup/classification-lookup.component';
import { CardApplicationComponent } from './pages/Account-Component/card-application/card-application.component';
import { CardApplicationMaintenanceComponent } from './pages/Account-Component/card-application-maintenance/card-application-maintenance.component';
import { CardnumberLookUpComponent } from './pages/Account-Component/cardnumber-look-up/cardnumber-look-up.component';

@NgModule({
    declarations: [
        PageError404Component,
        PageError500Component,
        SafePipe,
        AdministrationComponent,
        CardComponent,
        PieComponent,
        AreaComponent,
        ClassificationLookupComponent,
        HeaderComponent,
        MenuOptionBarComponent,
        FooterComponent,
        SidebarComponent,
        EventIdComponent,
        DashboardComponent,
        EventIdMaintenanceComponent,
        CurrencyConfigComponent,
        LinkedOrganizationComponent,
        LinkedOrganizationMaintenanceComponent,
        LinkedOrganizationLookupComponent,
        CurrencyMaintenanceComponent,
        CurrencyLookupComponent,
        EventTypeComponent,
        EventTypeMaintenanceComponent,
        EventTypeLookupComponent,
        ChrgPreferentialComponent,
        ChrgPreferentialMaintenanceComponent,
        ChrgPreferentialLookupComponent,
        TermDepositMaintenanceComponent,
        TermDepositLookupComponent,
        ChrgPrioritizationComponent,
        ChrgPrioritizationLookupComponent,
        ChrgPrioritizationMaintenanceComponent,
        SchemeTypeComponent,
        SchemeTypeLookupComponent,
        SchemeTypeMaintenanceComponent,
        GlCodeComponent,
        GlCodeLookupComponent,
        GlCodeMaintenanceComponent,
        GlSubheadComponent,
        GlSubheadLookupComponent,
        GlSubheadMaintenanceComponent,
        ExceptionsCodesComponent,
        ExceptionsCodesMaintenanceComponent,
        ExceptionsCodesLookupComponent,
        CollateralComponent,
        CollateralMaintenanceComponent,
        CollateralLookupComponent,
        BranchesComponent,
        BranchesLookupComponent,
        BranchesMaintenanceComponent,
        LoanAccountComponent,
        EventIdLookupComponent,
        MisSectorComponent,
        MisSectorLookupComponent,
        MisSectorMaintenanceComponent,
        MisSubSectorComponent,
        MisSubSectorMaintenanceComponent,
        MisSubSectorLookupComponent,
        TransactionExecutionComponent,
        TransactionExecutionLookupComponent,
        TransactionExecutionMainComponent,
        ShareCapitalParamsComponent,
        ShareCapitalComponent,
        ShareCapitalMaintenanceComponent,
        ShareCapitalParamsMaintenanceComponent,
        ShareCapitalParamsLookupComponent,
        GuarantorsParamsComponent,
        GuarantorsParamsMaintenanceComponent,
        GuarantorsParamsLookupComponent,
        ShareCapitalInstallmentsComponent,
        SegmentsComponent,
        SegmentLookupComponent,
        SegmentMaintenanceComponent,
        SubSegmentLookupComponent,
        SubSegmentMaintenanceComponent,
        SubSegmentComponent,
        ReportsComponent,
        ReportMaintainanceComponent,
        ViewReportComponent,
        SpecificReportComponent,
        MainClassificationsComponent,
        MainClassificationLookupComponent,
        MainClassificationMaintenanceComponent,
        LoanAccountComponent,
        LoanAccountMaintenanceComponent,
        SavingsAccountComponent,
        SavingsMaintenanceComponent,
        SavingsLookupComponent,
        OfficeAccountComponent,
        OfficeAccountMaintenanceComponent,
        WidgetMembershipComponent,
        WidgetLendingComponent,
        WidgetChargesComponent,
        WidgetShareCapitalComponent,
        LoanRepaymentComponent,
        AccountStatementComponent,
        LOanStatementComponent,
        OfficeAccountsComponent,
        ARREARSGENERALSTATEMENTComponent,
        ASSETSCLASSIFICATIONSComponent,
        InterestcodeComponent,
        InterestcodemaintenanceComponent,
        InterestcodelookupComponent,
        LoansAccountsLookUpComponent,
        OfficeAccountsLookUpsComponent,
        HolidayConfigComponent,
        HolidayMaintenanceComponent,
        HolidayLookupComponent,
        GeneralAccountsLookupComponent,
        CreateUserComponent,
        UpdateUserComponent,
        SolCodeLookupComponent,
        AccountsLookupComponent,
        UserManagementComponent,
        SchemeCodeLookupComponent,
        DisbursementReportComponent,
        LoanPortfolioReportsComponent,
        LoanDisbursementMaintenanceComponent,
        LoanDisbursementLookUpComponent,
        SharesLookupComponent,
        CustomerReportComponent,
        EndOfDayComponent,
        SalaryTransactionsComponent,
        SalaryTransactionsDataComponent,
        ExcelUploadComponent,
        SalaryTransactionsLookupComponent,
        ArrearsReportComponent,
        RepaymentsReportComponent,
        TransactionReportsComponent,
        LoanDemandsReportComponent,
        SasraReportsComponent,
        SasraReportDialogueComponent,
        AccountUsersComponent,
        AccountImagesLookupComponent,
        CustomerSharesLookupComponent,
       BalanceSheetDialogComponent,
        WorkClassComponent,
        WorkClassMaintenanceComponent,
        WorkClassLookupComponent,
        RolesManagementComponent,
        ExpensesReportDialogueComponent,
        TrialBalanceReportDialogueComponent,
        FeeReportDialogueComponent,
        TellersManagementComponent,
        TellerLookupComponent,
        TellerMaintenanceComponent,
        RolesLookupComponent,
        RolesMaintenanceComponent,
        MembershipMaintenanceComponent,
        NewMembershipComponent,
        MembershipLookUpComponent,
        GroupMembershipMaintenanceComponent,
        NewGroupMembershipComponent,
        GroupMembershipLookUpComponent,
        TellerLookupMaintainedComponent,
        AllAccountsLookUpComponent,
        ChequeBookMaintenanceComponent,
        ChequeBookLookupComponent,
        ChequeBooksComponent,
        RepaymentAccountsComponent,
        DocumentsComponent,
        LoanRestructureComponent,
        RestructureManitenanceComponent,
        LoanInformationComponent,
        UniversalMembershipLookUpComponent,
        GroupMembershipDocumentsComponent,
        MembershipDocumentsComponent,
        AccountDoumentsComponent,
        SpinnerComponent,
        PassowrdResetComponent,
        WorkClassActionsComponent,
        WorkClassActionsMaintenanceComponent,
        UserProfileComponent,
        LoanRepaymentMaintenanceComponent,
        LoanFeeRepaymentComponent,
        StandingOrdersComponent,
        StandingOrdersMaintenanceComponent,
        StandingOrdersLookupComponent,
        LiensMaintenanceComponent,
        LiensLookupComponent,
        LiensCollectionComponent,
        AccountClosureComponent,
        AccountClosureMaintenanceComponent,
        MembershipConfigComponent,
        MembershipConfigMaintenanceComponent,
        MembershipConfigLookupComponent,
        UniversalInquiryComponent,
        KraexcisedutyComponent,
        DynamicReportLookupComponent,
        TermDepositAccountLookUpComponent,
        TermDepositAccountComponent,
        TermDepositAccountMaintenanceComponent,
        NormalTransactionComponent,
        EmployeeConfigComponent,
        EmployeeConfigMaintenanceComponent,
        EmployeeConfigLookupComponent,
        DisplayReportComponent,
        OfficeTellerAccountsLookupComponent,
        TermDepositProductComponent,
        OfficeProductComponent,
        OfficeProductMaintenanceComponent,
        OfficeProductLookUpComponent,
        SavingsProductComponent,
        SavingsProductLookupComponent,
        SavingsProductMaintenanceComponent,
        OverDraftProductComponent,
        OverDraftProductMaintenanceComponent,
        OverDraftProductLookupComponent,
        LoansProductComponent,
        LoansProductMaintenanceComponent,
        LoansProductLookUpComponent,
        CurrentAccountProductComponent,
        CurrentAccountProductMaintenanceComponent,
        CurrentAccountProductLookupComponent,
        GeneralProductLookUpComponent,
        NormalCashTransactionsComponent,
        MainDashboardPageComponent,
        CollateralDialogComponent,
        UpdateCollateralDialogComponent,
        TransactionChequesComponent,
        TransactionChequesMaintenanceComponent,
        TransactionChequesLookupComponent,
        ManualLoanPenaltiesComponent,
        ManualPenaltiesMaintenanceComponent,
        LoanPenaltiesLookupComponent,
        CashTransactionExecutionComponent,
        MemberDocumentsComponent,
        GroupMembershipDetailsComponent,
        GeneralAccountDetailsComponent,
        GeneralAccountStatementComponent,
        AccountStatementMaintenanceComponent,
        AllBranchesLookupComponent,
        SalaryChargesComponent,
        SalaryChargesMaintenanceComponent,
        SalaryChargesLookUpComponent,
        GeneralTransactionLookUpComponent,
        NotificationAllertsComponent,
        ChequeParamsComponent,
        ChequeParamsMaintenanceComponent,
        ChequeParamsLookUpComponent,
        TransactionApprovalComponent,
        BatchSalariesComponent,
        BatchSalariesMaintenanceComponent,
        BatchSalariesLookupComponent,
        AccountsApprovalComponent,
        RejectAccountComponent,
        WelfareComponent,
        WelfareLookUpComponent,
        WelfareMaintenanceComponent,
        ForceLoanDemandsComponent,
        SavingsContributionComponent,
        SavingsContributionMaintenanceComponent,
        SavingsContributionLookupComponent,
        SBCNotificationsComponent,
        RejectTransactionComponent,
        OfficeAccountsByGlsComponent,
        KratariffComponent,
        ExciseDutiesComponent,
        DirectApprovalsComponent,
        MassiveInterestComponent,
        CardApplicationComponent,
        CardApplicationMaintenanceComponent,
        CardnumberLookUpComponent,

       
     
       
      
    ],
    exports: [
        HeaderComponent,
        FooterComponent,
        SidebarComponent,
        MenuOptionBarComponent
    ],
    providers: [authInterceptorProviders, EventIdMaintenanceComponent, DatePipe, FilesService],
    imports: [
        CommonModule,
        AgmCoreModule,
        AdministrationRoutingModule,
        DataTablesModule,
        RouterModule,
        HighchartsChartModule,
        MatTableExporterModule,
        MaterialModule,
        UnpostedChequesComponent
    ]
})
export class AdministrationModule { }
    