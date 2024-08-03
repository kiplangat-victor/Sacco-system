import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { CanActivateModuleGuard } from 'src/@core/helpers/CanActivateModule.guard';
import { AdministrationComponent } from './administration.component';
import { PageError500Component } from './page-error500/page-error500.component';
import { RolesMaintenanceComponent } from './pages/AccessManagement/roles-management/roles-maintenance/roles-maintenance.component';
import { RolesManagementComponent } from './pages/AccessManagement/roles-management/roles-management.component';
import { TellerMaintenanceComponent } from './pages/AccessManagement/tellers-management/teller-maintenance/teller-maintenance.component';
import { TellersManagementComponent } from './pages/AccessManagement/tellers-management/tellers-management.component';
import { CreateUserComponent } from './pages/AccessManagement/user-management/create-user/create-user.component';
import { UpdateUserComponent } from './pages/AccessManagement/user-management/update-user/update-user.component';
import { UserManagementComponent } from './pages/AccessManagement/user-management/user-management.component';
import { UserProfileComponent } from './pages/AccessManagement/user-management/user-profile/user-profile.component';
import { WorkClassActionsMaintenanceComponent } from './pages/AccessManagement/work-class-actions/work-class-actions-maintenance/work-class-actions-maintenance.component';
import { WorkClassActionsComponent } from './pages/AccessManagement/work-class-actions/work-class-actions/work-class-actions.component';
import { WorkClassMaintenanceComponent } from './pages/AccessManagement/work-class/work-class-maintenance/work-class-maintenance.component';
import { WorkClassComponent } from './pages/AccessManagement/work-class/work-class.component';
import { AccountClosureMaintenanceComponent } from './pages/Account-Component/account-closure/account-closure-maintenance/account-closure-maintenance.component';
import { AccountClosureComponent } from './pages/Account-Component/account-closure/account-closure.component';
import { AccountStatementMaintenanceComponent } from './pages/Account-Component/account-statements/account-statement-maintenance/account-statement-maintenance.component';
import { GeneralAccountStatementComponent } from './pages/Account-Component/account-statements/general-account-statement/general-account-statement.component';
import { LoanDisbursementMaintenanceComponent } from './pages/Account-Component/loan-account/Loan-disbursement/loan-disbursement-maintenance/loan-disbursement-maintenance.component';
import { ForceLoanDemandsComponent } from './pages/Account-Component/loan-account/force-loan-demands/force-loan-demands.component';
import { LoanAccountMaintenanceComponent } from './pages/Account-Component/loan-account/loan-account-maintenance/loan-account-maintenance.component';
import { LoanAccountComponent } from './pages/Account-Component/loan-account/loan-account/loan-account.component';
import { LoanInformationComponent } from './pages/Account-Component/loan-account/loan-information/loan-information.component';
import { ManualLoanPenaltiesComponent } from './pages/Account-Component/loan-account/loan-penalties/manual-loan-penalties/manual-loan-penalties.component';
import { ManualPenaltiesMaintenanceComponent } from './pages/Account-Component/loan-account/loan-penalties/manual-penalties-maintenance/manual-penalties-maintenance.component';
import { LoanRepaymentComponent } from './pages/Account-Component/loan-account/loan-repayment/loan-repayment.component';
import { LoanFeeRepaymentComponent } from './pages/Account-Component/loan-account/repayment/loan-fee-repayment/loan-fee-repayment.component';
import { LoanRepaymentMaintenanceComponent } from './pages/Account-Component/loan-account/repayment/loan-repayment-maintenance/loan-repayment-maintenance.component';
import { LoanRestructureComponent } from './pages/Account-Component/loan-account/restructure/loan-restructure/loan-restructure.component';
import { RestructureManitenanceComponent } from './pages/Account-Component/loan-account/restructure/restructure-manitenance/restructure-manitenance.component';
import { OfficeAccountMaintenanceComponent } from './pages/Account-Component/office-account/office-account-maintenance/office-account-maintenance.component';
import { OfficeAccountComponent } from './pages/Account-Component/office-account/office-account.component';
import { LiensCollectionComponent } from './pages/Account-Component/savings-account/liens-collection/liens-collection.component';
import { LiensMaintenanceComponent } from './pages/Account-Component/savings-account/liens-maintenance/liens-maintenance.component';
import { SavingsAccountComponent } from './pages/Account-Component/savings-account/savings-account.component';
import { SavingsContributionMaintenanceComponent } from './pages/Account-Component/savings-account/savings-contribution/savings-contribution-maintenance/savings-contribution-maintenance.component';
import { SavingsContributionComponent } from './pages/Account-Component/savings-account/savings-contribution/savings-contribution.component';
import { SavingsMaintenanceComponent } from './pages/Account-Component/savings-account/savings-maintenance/savings-maintenance.component';
import { TermDepositAccountMaintenanceComponent } from './pages/Account-Component/term-deposit/term-deposit-account-maintenance/term-deposit-account-maintenance.component';
import { TermDepositAccountComponent } from './pages/Account-Component/term-deposit/term-deposit-account/term-deposit-account.component';
import { GroupMembershipMaintenanceComponent } from './pages/MembershipComponent/GroupMembership/group-membership-maintenance/group-membership-maintenance.component';
import { NewGroupMembershipComponent } from './pages/MembershipComponent/GroupMembership/new-group-membership/new-group-membership.component';
import { MembershipMaintenanceComponent } from './pages/MembershipComponent/Membership/membership-maintenance/membership-maintenance.component';
import { NewMembershipComponent } from './pages/MembershipComponent/Membership/new-membership/new-membership.component';
import { CurrentAccountProductMaintenanceComponent } from './pages/ProductModule/current-account-product/current-account-product-maintenance/current-account-product-maintenance.component';
import { CurrentAccountProductComponent } from './pages/ProductModule/current-account-product/current-account-product/current-account-product.component';
import { LoansProductMaintenanceComponent } from './pages/ProductModule/loans-product/loans-product-maintenance/loans-product-maintenance.component';
import { LoansProductComponent } from './pages/ProductModule/loans-product/loans-product/loans-product.component';
import { OfficeProductMaintenanceComponent } from './pages/ProductModule/office-product/office-product-maintenance/office-product-maintenance.component';
import { OfficeProductComponent } from './pages/ProductModule/office-product/office-product.component';
import { OverDraftProductMaintenanceComponent } from './pages/ProductModule/overdraft-product/over-draft-product-maintenance/over-draft-product-maintenance.component';
import { OverDraftProductComponent } from './pages/ProductModule/overdraft-product/over-draft-product/over-draft-product.component';
import { SavingsProductMaintenanceComponent } from './pages/ProductModule/savings/savings-product-maintenance/savings-product-maintenance.component';
import { SavingsProductComponent } from './pages/ProductModule/savings/savings-product/savings-product.component';
import { TermDepositMaintenanceComponent } from './pages/ProductModule/term-deposit/term-deposit-maintenance/term-deposit-maintenance.component';
import { TermDepositProductComponent } from './pages/ProductModule/term-deposit/term-deposit-product/term-deposit-product.component';
import { ChrgPreferentialMaintenanceComponent } from './pages/SystemConfigurations/ChargesParams/chrg-preferential/chrg-preferential-maintenance/chrg-preferential-maintenance.component';
import { ChrgPreferentialComponent } from './pages/SystemConfigurations/ChargesParams/chrg-preferential/chrg-preferential.component';
import { ChrgPrioritizationMaintenanceComponent } from './pages/SystemConfigurations/ChargesParams/chrg-prioritization/chrg-prioritization-maintenance/chrg-prioritization-maintenance.component';
import { ChrgPrioritizationComponent } from './pages/SystemConfigurations/ChargesParams/chrg-prioritization/chrg-prioritization.component';
import { EventIdMaintenanceComponent } from './pages/SystemConfigurations/ChargesParams/event-id/event-id-maintenance/event-id-maintenance.component';
import { EventIdComponent } from './pages/SystemConfigurations/ChargesParams/event-id/event-id.component';
import { EventTypeMaintenanceComponent } from './pages/SystemConfigurations/ChargesParams/event-type/event-type-maintenance/event-type-maintenance.component';
import { EventTypeComponent } from './pages/SystemConfigurations/ChargesParams/event-type/event-type.component';
import { BranchesMaintenanceComponent } from './pages/SystemConfigurations/GlobalParams/branches/branches-maintenance/branches-maintenance.component';
import { BranchesComponent } from './pages/SystemConfigurations/GlobalParams/branches/branches.component';
import { ChequeParamsMaintenanceComponent } from './pages/SystemConfigurations/GlobalParams/cheque-params/cheque-params-maintenance/cheque-params-maintenance.component';
import { ChequeParamsComponent } from './pages/SystemConfigurations/GlobalParams/cheque-params/cheque-params.component';
import { CurrencyConfigComponent } from './pages/SystemConfigurations/GlobalParams/currency-config/currency-config.component';
import { CurrencyMaintenanceComponent } from './pages/SystemConfigurations/GlobalParams/currency-config/currency-maintenance/currency-maintenance.component';
import { EmployeeConfigMaintenanceComponent } from './pages/SystemConfigurations/GlobalParams/employee-config/employee-config-maintenance/employee-config-maintenance.component';
import { EmployeeConfigComponent } from './pages/SystemConfigurations/GlobalParams/employee-config/employee-config.component';
import { ExceptionsCodesMaintenanceComponent } from './pages/SystemConfigurations/GlobalParams/exceptions-codes/exceptions-codes-maintenance/exceptions-codes-maintenance.component';
import { ExceptionsCodesComponent } from './pages/SystemConfigurations/GlobalParams/exceptions-codes/exceptions-codes.component';
import { GlCodeMaintenanceComponent } from './pages/SystemConfigurations/GlobalParams/gl-code/gl-code-maintenance/gl-code-maintenance.component';
import { GlCodeComponent } from './pages/SystemConfigurations/GlobalParams/gl-code/gl-code.component';
import { GlSubheadMaintenanceComponent } from './pages/SystemConfigurations/GlobalParams/gl-subhead/gl-subhead-maintenance/gl-subhead-maintenance.component';
import { GlSubheadComponent } from './pages/SystemConfigurations/GlobalParams/gl-subhead/gl-subhead.component';
import { GuarantorsParamsMaintenanceComponent } from './pages/SystemConfigurations/GlobalParams/guarantors-params/guarantors-params-maintenance/guarantors-params-maintenance.component';
import { GuarantorsParamsComponent } from './pages/SystemConfigurations/GlobalParams/guarantors-params/guarantors-params.component';
import { HolidayConfigComponent } from './pages/SystemConfigurations/GlobalParams/holiday-config/holiday-config.component';
import { HolidayMaintenanceComponent } from './pages/SystemConfigurations/GlobalParams/holiday-config/holiday-maintenance/holiday-maintenance.component';
import { LinkedOrganizationMaintenanceComponent } from './pages/SystemConfigurations/GlobalParams/linked-organization/linked-organization-maintenance/linked-organization-maintenance.component';
import { LinkedOrganizationComponent } from './pages/SystemConfigurations/GlobalParams/linked-organization/linked-organization.component';
import { MainClassificationMaintenanceComponent } from './pages/SystemConfigurations/GlobalParams/main-classifications/main-classification-maintenance/main-classification-maintenance.component';
import { MainClassificationsComponent } from './pages/SystemConfigurations/GlobalParams/main-classifications/main-classifications.component';
import { MembershipConfigMaintenanceComponent } from './pages/SystemConfigurations/GlobalParams/membership-config/membership-config-maintenance/membership-config-maintenance.component';
import { MembershipConfigComponent } from './pages/SystemConfigurations/GlobalParams/membership-config/membership-config.component';
import { MisSectorMaintenanceComponent } from './pages/SystemConfigurations/GlobalParams/mis-sector/mis-sector-maintenance/mis-sector-maintenance.component';
import { MisSectorComponent } from './pages/SystemConfigurations/GlobalParams/mis-sector/mis-sector.component';
import { MisSubSectorMaintenanceComponent } from './pages/SystemConfigurations/GlobalParams/mis-sub-sector/mis-sub-sector-maintenance/mis-sub-sector-maintenance.component';
import { MisSubSectorComponent } from './pages/SystemConfigurations/GlobalParams/mis-sub-sector/mis-sub-sector.component';
import { SalaryChargesMaintenanceComponent } from './pages/SystemConfigurations/GlobalParams/salary-charges/salary-charges-maintenance/salary-charges-maintenance.component';
import { SalaryChargesComponent } from './pages/SystemConfigurations/GlobalParams/salary-charges/salary-charges.component';
import { SchemeTypeMaintenanceComponent } from './pages/SystemConfigurations/GlobalParams/scheme-type/scheme-type-maintenance/scheme-type-maintenance.component';
import { SchemeTypeComponent } from './pages/SystemConfigurations/GlobalParams/scheme-type/scheme-type.component';
import { SegmentMaintenanceComponent } from './pages/SystemConfigurations/GlobalParams/segments/segment-maintenance/segment-maintenance.component';
import { SegmentsComponent } from './pages/SystemConfigurations/GlobalParams/segments/segments.component';
import { ShareCapitalParamsMaintenanceComponent } from './pages/SystemConfigurations/GlobalParams/share-capital-params/share-capital-params-maintenance/share-capital-params-maintenance.component';
import { ShareCapitalParamsComponent } from './pages/SystemConfigurations/GlobalParams/share-capital-params/share-capital-params.component';
import { SubSegmentMaintenanceComponent } from './pages/SystemConfigurations/GlobalParams/sub-segment/sub-segment-maintenance/sub-segment-maintenance.component';
import { SubSegmentComponent } from './pages/SystemConfigurations/GlobalParams/sub-segment/sub-segment/sub-segment.component';
import { WelfareMaintenanceComponent } from './pages/SystemConfigurations/GlobalParams/welfare/welfare-maintenance/welfare-maintenance.component';
import { WelfareComponent } from './pages/SystemConfigurations/GlobalParams/welfare/welfare.component';
import { InterestcodeComponent } from './pages/SystemConfigurations/interestcode/interestcode.component';
import { InterestcodemaintenanceComponent } from './pages/SystemConfigurations/interestcode/interestcodemaintenance/interestcodemaintenance.component';
import { ChequeBookMaintenanceComponent } from './pages/cheque-books/cheque-book-maintenance/cheque-book-maintenance.component';
import { ChequeBooksComponent } from './pages/cheque-books/cheque-books.component';
import { CollateralMaintenanceComponent } from './pages/collateral-limits/collateral/collateral-maintenance/collateral-maintenance.component';
import { CollateralComponent } from './pages/collateral-limits/collateral/collateral.component';
import { DashboardComponent } from './pages/dashboard/dashboard.component';
import { EndOfDayComponent } from './pages/end-of-day/end-of-day.component';
import { NotificationAllertsComponent } from './pages/notifications/notification-allerts/notification-allerts.component';
import { ReportsComponent } from './pages/reports/reports.component';
import { SasraReportsComponent } from './pages/reports/sasra-reports/sasra-reports.component';
import { SpecificReportComponent } from './pages/reports/specific-report/specific-report.component';
import { ViewReportComponent } from './pages/reports/view-report/view-report.component';
import { BatchSalariesMaintenanceComponent } from './pages/salary-transactions/batch-salaries/batch-salaries-maintenance/batch-salaries-maintenance.component';
import { BatchSalariesComponent } from './pages/salary-transactions/batch-salaries/batch-salaries.component';
import { SalaryTransactionsDataComponent } from './pages/salary-transactions/salary-transactions-data/salary-transactions-data.component';
import { SalaryTransactionsComponent } from './pages/salary-transactions/salary-transactions.component';
import { ShareCapitalMaintenanceComponent } from './pages/share-capital/share-capital-maintenance/share-capital-maintenance.component';
import { ShareCapitalComponent } from './pages/share-capital/share-capital.component';
import { CashTransactionExecutionComponent } from './pages/transaction-execution/cash-transaction-execution/cash-transaction-execution.component';
import { KraexcisedutyComponent } from './pages/transaction-execution/kraexciseduty/kraexciseduty.component';
import { NormalTransactionComponent } from './pages/transaction-execution/normal-transaction/normal-transaction.component';
import { StandingOrdersMaintenanceComponent } from './pages/transaction-execution/standingOrders/standing-orders-maintenance/standing-orders-maintenance.component';
import { StandingOrdersComponent } from './pages/transaction-execution/standingOrders/standing-orders/standing-orders.component';
import { TransactionChequesMaintenanceComponent } from './pages/transaction-execution/transaction-cheques/transaction-cheques-maintenance/transaction-cheques-maintenance.component';
import { TransactionChequesComponent } from './pages/transaction-execution/transaction-cheques/transaction-cheques.component';
import { TransactionExecutionComponent } from './pages/transaction-execution/transaction-execution.component';
import { KratariffComponent } from './pages/transaction-execution/exciseDuties/kratariff/kratariff.component';
import { ExciseDutiesComponent } from './pages/transaction-execution/exciseDuties/excise-duties/excise-duties.component';
import { MassiveInterestComponent } from './pages/Account-Component/loan-account/massive-interest/massive-interest.component';
import { EntityuserComponent } from '../saccomanagement/pages/entityuser/entityuser.component';
import * as path from 'path';
import { CardApplicationMaintenanceComponent } from './pages/Account-Component/card-application-maintenance/card-application-maintenance.component';
import { CardApplicationComponent } from './pages/Account-Component/card-application/card-application.component';

const routes: Routes = [
  {
    path: '',
    component: AdministrationComponent,
    children: [
      {
        path: '',
        component: DashboardComponent,
        pathMatch: 'full',
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'DASHBOARD', preload: true },
      },

      {
        path: 'branches/maintenance',
        component: BranchesMaintenanceComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'CONFIGURATIONS', preload: true }
      },
      {
        path: 'branches/data/view',
        component: BranchesComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'CONFIGURATIONS', preload: true }
      },
      {
        path: 'configurations-member/maintenance',
        component: MembershipConfigMaintenanceComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'CONFIGURATIONS', preload: true }
      },
      {
        path: 'configurations-member/data/view',
        component: MembershipConfigComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'CONFIGURATIONS', preload: true }
      },
      {
        path: 'configurations-employee/maintenance',
        component: EmployeeConfigMaintenanceComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'CONFIGURATIONS', preload: true }
      },
      {
        path: 'configurations-employee/data/view',
        component: EmployeeConfigComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'CONFIGURATIONS', preload: true }
      },
      {
        path: 'configurations/salary/charges/maintenance',
        component: SalaryChargesMaintenanceComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'CONFIGURATIONS', preload: true }
      },
      {
        path: 'configurations/salary/charges/data/view',
        component: SalaryChargesComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'CONFIGURATIONS', preload: true }
      },
      //CHEQUE CONFIGS
      {
        path: 'configurations/cheque/charges/maintenance',
        component: ChequeParamsMaintenanceComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'CONFIGURATIONS', preload: true }
      },
      {
        path: 'configurations/cheque/charges/data/view',
        component: ChequeParamsComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'CONFIGURATIONS', preload: true }
      },
      {
        path: 'configurations/global/linked/organization/maintenance',
        component: LinkedOrganizationMaintenanceComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'CONFIGURATIONS', preload: true }
      },
      {
        path: 'configurations/global/linked/organization/data/view',
        component: LinkedOrganizationComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'CONFIGURATIONS', preload: true }
      },
      {
        path: 'configurations/global/currency/maintenance',
        component: CurrencyMaintenanceComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'CONFIGURATIONS', preload: true }
      },
      {
        path: 'configurations/global/currency/data/view',
        component: CurrencyConfigComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'CONFIGURATIONS', preload: true }
      },
      {
        path: 'configurations/global/holiday/maintenance',
        component: HolidayMaintenanceComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'CONFIGURATIONS', preload: true }
      },
      {
        path: 'configurations/global/holiday/data/view',
        component: HolidayConfigComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'CONFIGURATIONS', preload: true }
      },
      // Scheme Type
      {
        path: 'configurations/global/scheme-type/maintenance',
        component: SchemeTypeMaintenanceComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'CONFIGURATIONS', preload: true }
      },
      {
        path: 'configurations/global/scheme-type/data/view',
        component: SchemeTypeComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'CONFIGURATIONS', preload: true }
      },
      // Exceptions Controller
      {
        path: 'configurations/global/exceptions-codes/maintenance',
        component: ExceptionsCodesMaintenanceComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'CONFIGURATIONS', preload: true }
      },
      {
        path: 'configurations/global/exceptions-codes/data/view',
        component: ExceptionsCodesComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'CONFIGURATIONS', preload: true }
      },
      //MIS Codes Maintenance
      {
        path: 'configurations/global/mis-sector/maintenance',
        component: MisSectorMaintenanceComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'CONFIGURATIONS', preload: true }
      },
      {
        path: 'configurations/global/mis-sector/data/view',
        component: MisSectorComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'CONFIGURATIONS', preload: true }
      },
      //Mis Sub Sector
      {
        path: 'configurations/global/mis-sub-sector/maintenance',
        component: MisSubSectorMaintenanceComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'CONFIGURATIONS', preload: true }
      },
      {
        path: 'configurations/global/mis-sub-sector/data/view',
        component: MisSubSectorComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'CONFIGURATIONS', preload: true }
      },
      //Guarantors Config
      {
        path: 'configurations/global/guarantors/maintenance',
        component: GuarantorsParamsMaintenanceComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'CONFIGURATIONS', preload: true }
      },
      {
        path: 'configurations/global/guarantors/data/view',
        component: GuarantorsParamsComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'CONFIGURATIONS', preload: true }
      },
      // GL Code Maintenance
      {
        path: 'configurations/global/gl-code/maintenance',
        component: GlCodeMaintenanceComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'CONFIGURATIONS', preload: true }
      },
      {
        path: 'configurations/global/gl-code',
        component: GlCodeComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'CONFIGURATIONS', preload: true }
      },
      // GL subhead code Maintenance
      {
        path: 'configurations/global/gl-subhead/maintenance',
        component: GlSubheadMaintenanceComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'CONFIGURATIONS', preload: true }
      },
      {
        path: 'configurations/global/gl-subhead',
        component: GlSubheadComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'CONFIGURATIONS', preload: true }
      },
      //Segments
      {
        path: 'configurations/global/segment/maintenance',
        component: SegmentMaintenanceComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'CONFIGURATIONS', preload: true }
      },
      {
        path: 'configurations/global/segment/data-view',
        component: SegmentsComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'CONFIGURATIONS', preload: true }
      },
      {
        path: 'configurations/global/sub-segment/maintenance',
        component: SubSegmentMaintenanceComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'CONFIGURATIONS', preload: true }
      },
      {
        path: 'configurations/global/sub-segment/data-view',
        component: SubSegmentComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'CONFIGURATIONS', preload: true }
      },
      //WELFARE CONFIG
      {
        path: 'configurations/welfare/maintenance',
        component: WelfareMaintenanceComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'CONFIGURATIONS', preload: true }
      },
      {
        path: 'configurations/welfare/data-view',
        component: WelfareComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'CONFIGURATIONS', preload: true }
      },
      // Charge Preferentials
      {
        path: 'configurations/charge/preferentials/maintenance',
        component: ChrgPreferentialMaintenanceComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'CHARGE PARAMS', preload: true }
      },
      {
        path: 'configurations/charge/preferentials/data/view',
        component: ChrgPreferentialComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'CHARGE PARAMS', preload: true }
      },
      // Charge Prioritization
      {
        path: 'configurations/charge/prioritization/maintenance',
        component: ChrgPrioritizationMaintenanceComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'CHARGE PARAMS', preload: true }
      },
      {
        path: 'configurations/charge/prioritization/data/view',
        component: ChrgPrioritizationComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'CHARGE PARAMS', preload: true }
      },
      // Event Type
      {
        path: 'configurations/charge/event-type/maintenance',
        component: EventTypeMaintenanceComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'CHARGE PARAMS', preload: true }
      },
      {
        path: 'configurations/charge/event-type/data/view',
        component: EventTypeComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'CHARGE PARAMS', preload: true }
      },
      // event id
      {
        path: 'configurations/charge/event-id/maintenance',
        component: EventIdMaintenanceComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'CHARGE PARAMS', preload: true }
      },
      {
        path: 'configurations/charge/event-id/data/view',
        component: EventIdComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'CHARGE PARAMS', preload: true }
      },
      {
        path: 'configurations/charge/event-id/maintenance',
        component: EventIdMaintenanceComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'CHARGE PARAMS', preload: true }
      },
      {
        path: 'interestcode/maintenance',
        component: InterestcodemaintenanceComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'INTEREST MAINTENANCE', preload: true }
      },
      {
        path: 'interestcode/data/view',
        component: InterestcodeComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'INTEREST MAINTENANCE', preload: true }
      },
      {
        path: 'product/office-product/maintenance',
        component: OfficeProductMaintenanceComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'PRODUCTS', preload: true }
      },
      {
        path: 'product/office-product/data/view',
        component: OfficeProductComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'PRODUCTS', preload: true }
      },
      {
        path: 'product/loans/maintenance',
        component: LoansProductMaintenanceComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'PRODUCTS', preload: true }
      },
      {
        path: 'product/loans/data/view',
        component: LoansProductComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'PRODUCTS', preload: true }
      },
      {
        path: 'product/term-deposit/maintenance',
        component: TermDepositMaintenanceComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'PRODUCTS', preload: true }
      },
      {
        path: 'product/term-deposit/data/view',
        component: TermDepositProductComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'PRODUCTS', preload: true }
      },
      {
        path: 'product/savings/maintenance',
        component: SavingsProductMaintenanceComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'PRODUCTS', preload: true }
      },
      {
        path: 'product/savings/data/view',
        component: SavingsProductComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'PRODUCTS', preload: true }
      },
      {
        path: 'product/current-account/maintenance',
        component: CurrentAccountProductMaintenanceComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'PRODUCTS', preload: true }
      },
      {
        path: 'product/current-account/data/view',
        component: CurrentAccountProductComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'PRODUCTS', preload: true }
      },
      {
        path: 'product/overdrafts/maintenance',
        component: OverDraftProductMaintenanceComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'PRODUCTS', preload: true }
      },
      {
        path: 'product/overdrafts/data/view',
        component: OverDraftProductComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'PRODUCTS', preload: true }
      },
      {
        path: 'loan-account/maintenance',
        component: LoanAccountMaintenanceComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'ACCOUNTS MANAGEMENT', preload: true }
      },
      {
        path: 'loan-account/data/view',
        component: LoanAccountComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'ACCOUNTS MANAGEMENT', preload: true }
      },
      {
        path: 'loan-account-force-demands/data/view',
        component: ForceLoanDemandsComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'ACCOUNTS MANAGEMENT', preload: true }
      },
      {
        path: 'loan-account-massive-interest/data/view',
        component: MassiveInterestComponent ,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'ACCOUNTS MANAGEMENT', preload: true }
      },
      {
        path: 'loan-disbursement/maintenance',
        component: LoanDisbursementMaintenanceComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'ACCOUNTS MANAGEMENT', preload: true }
      },
      {
        path: 'loan-rescheduling',
        component: RestructureManitenanceComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'ACCOUNTS MANAGEMENT', preload: true }
      },
      {
        path: 'loan-rescheduling-data-view',
        component: LoanRestructureComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'ACCOUNTS MANAGEMENT', preload: true }
      },
      {
        path: 'loans-account/information',
        component: LoanInformationComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'ACCOUNTS MANAGEMENT', preload: true }
      },
      {
        path: 'loans-account-repayment/maintenance',
        component: LoanRepaymentMaintenanceComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'ACCOUNTS MANAGEMENT', preload: true }
      },
      {
        path: 'loans-account/fee/repayment/data/view',
        component: LoanFeeRepaymentComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'ACCOUNTS MANAGEMENT', preload: true }
      },
      {
        path: 'loans-account/repayment',
        component: LoanRepaymentComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'ACCOUNTS MANAGEMENT', preload: true }
      },
      // Savings Account
      {
        path: 'savings-account/maintenance',
        component: SavingsMaintenanceComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'ACCOUNTS MANAGEMENT', preload: true }
      },
      {
        path: 'savings-account/data/view',
        component: SavingsAccountComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'ACCOUNTS MANAGEMENT', preload: true }
      },
      //LIENS
      {
        path: 'liens-account/maintenance',
        component: LiensMaintenanceComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'ACCOUNTS MANAGEMENT', preload: true }
      },
      {
        path: 'liens-account/data/view',
        component: LiensCollectionComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'ACCOUNTS MANAGEMENT', preload: true }
      },
      //SAVINGS CONTRIBUTION
      {
        path: 'savings-instruction-contribution/maintenance',
        component: SavingsContributionMaintenanceComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'ACCOUNTS MANAGEMENT', preload: true }
      },
      {
        path: 'savings-instruction-contribution/data/view',
        component: SavingsContributionComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'ACCOUNTS MANAGEMENT', preload: true }
      },
      //Office Account
      {
        path: 'office/account/maintenance',
        component: OfficeAccountMaintenanceComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'ACCOUNTS MANAGEMENT', preload: true }
      },
      {
        path: 'office-account/data/view',
        component: OfficeAccountComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'ACCOUNTS MANAGEMENT', preload: true }
      },
      //Term Deposit Account
      {
        path: 'term-deposit/account/maintenance',
        component: TermDepositAccountMaintenanceComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'ACCOUNTS MANAGEMENT', preload: true }
      },
      {
        path: 'term-deposit/account/data-view',
        component: TermDepositAccountComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'ACCOUNTS MANAGEMENT', preload: true }
      },
      // Account CLOSSURE
      {
        path: 'account-closure/maintenance',
        component: AccountClosureMaintenanceComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'ACCOUNTS MANAGEMENT', preload: true }
      },
      {
        path: 'account-closure/data/view',
        component: AccountClosureComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'ACCOUNTS MANAGEMENT', preload: true }
      },
      // MANUAL ACCOUNT PENALTIES
      {
        path: 'loan/account/penalties/maintenance',
        component: ManualPenaltiesMaintenanceComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'ACCOUNTS MANAGEMENT', preload: true }
      },
      {
        path: 'loan/account/penalties/data/view',
        component: ManualLoanPenaltiesComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'ACCOUNTS MANAGEMENT', preload: true }
      },
      // ACCOUNT STATEMENTS
      {
        path: 'accounts/statement/maintenance',
        component: AccountStatementMaintenanceComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'ACCOUNTS STATEMENT', preload: true }
      },
      {
        path: 'accounts/statement/data/view',
        component: GeneralAccountStatementComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'ACCOUNTS STATEMENT', preload: true }
      },
      // collateral
      {
        path: 'configurations-collateral-maintenance',
        component: CollateralMaintenanceComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'COLLATERALS MANAGEMENT', preload: true }
      },
      {
        path: 'configurations-collateral-data-view',
        component: CollateralComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'COLLATERALS MANAGEMENT', preload: true }
      },
      {
        path: 'transactions/maintenance',
        component: TransactionExecutionComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'TRANSACTION MAINTENANCE', preload: true }
      },
      {
        path: 'transactions/normal/data/view',
        component: NormalTransactionComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'TRANSACTION MAINTENANCE', preload: true }
      },
      {
        path: 'transactions/normal-cash/data/view',
        component: CashTransactionExecutionComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'TRANSACTION MAINTENANCE', preload: true }
      },
      {
        path: 'transactions/cheques-maintenance',
        component: TransactionChequesMaintenanceComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'TRANSACTION MAINTENANCE', preload: true }
      },
      {
        path: 'transactions/cheques-data-view',
        component: TransactionChequesComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'TRANSACTION MAINTENANCE', preload: true }
      },
      {
        path: 'transactions/exciseduty',
        component: KraexcisedutyComponent,
        // canActivate: [CanActivateModuleGuard],
        // data: { permission: 'TRANSACTION MAINTENANCE', preload: true }
      },
      {
        path: 'chequebook/maintenance',
        component: ChequeBooksComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'TRANSACTION MAINTENANCE', preload: true }
      },
      {
        path: 'chequebook/data/view',
        component: ChequeBookMaintenanceComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'TRANSACTION MAINTENANCE', preload: true }
      },
      {
        path: 'transactions/standing/orders/view',
        component: StandingOrdersComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'TRANSACTION MAINTENANCE', preload: true }
      },
      {
        path: 'transactions/standing/orders/maintenance',
        component: StandingOrdersMaintenanceComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'TRANSACTION MAINTENANCE', preload: true }
      },
      {
        path: 'transactions/bank/kra/list',
        component: KratariffComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'TRANSACTION MAINTENANCE', preload: true }
      },
      {
        path: 'transactions/bank/exercise/duty/list',
        component: ExciseDutiesComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'TRANSACTION MAINTENANCE', preload: true }
      },
      // *******************************************************************************************\
      //                                         Customers Management
      // *******************************************************************************************
      //
      {
        path: 'membership/maintenance',
        component: MembershipMaintenanceComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'MEMBERSHIP MANAGEMENT', preload: true }
      },
      {
        path: 'membership/data/view',
        component: NewMembershipComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'MEMBERSHIP MANAGEMENT', preload: true }
      },
      {
        path: 'group-membership/maintenance',
        component: GroupMembershipMaintenanceComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'MEMBERSHIP MANAGEMENT', preload: true }
      },
      {
        path: 'group-membership/data/view',
        component: NewGroupMembershipComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'MEMBERSHIP MANAGEMENT', preload: true }
      },
      // *******************************************************************************************\
      //                                         GLS Management
      // *******************************************************************************************
      // Share capital Configurations
      {
        path: 'configurations/global/share-capital/params/maintenance',
        component: ShareCapitalParamsMaintenanceComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'SHARE CAPITAL MAINTAINANCE', preload: true }
      },
      // Share capital Configurations
      {
        path: 'configurations/global/share-capital/params/data/view',
        component: ShareCapitalParamsComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'SHARE CAPITAL MAINTAINANCE', preload: true }
      },
      {
        path: 'share-capital/data/view',
        component: ShareCapitalComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'SHARE CAPITAL MAINTAINANCE', preload: true }
      },
      {
        path: 'share-capital/maintenance',
        component: ShareCapitalMaintenanceComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'SHARE CAPITAL MAINTAINANCE', preload: true }
      },
      {
        path: 'share-capital/new-account/maintenance',
        component: ShareCapitalMaintenanceComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'SHARE CAPITAL MAINTAINANCE', preload: true }
      },
      //  Reports
      {
        path: 'add-report',
        component: ReportsComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'REPORTS', preload: true }
      },
      {
        path: 'view-report',
        component: ViewReportComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'REPORTS', preload: true }
      },
      {
        path: 'sasra-reports',
        component: SasraReportsComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'REPORTS', preload: true }
      },
      {
        path: 'specific-report',
        component: SpecificReportComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'REPORTS', preload: true }
      },
      // Asset Classification
      {
        path: 'configurations/global/main-classification/maintenance',
        component: MainClassificationMaintenanceComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'CONFIGURATIONS', preload: true }
      },
      {
        path: 'configurations/global/main-classification/data/view',
        component: MainClassificationsComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'CONFIGURATIONS', preload: true }
      },
      // User management
      {
        path: 'manage/user',
        component: UserManagementComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'ACCESS MANAGEMENT', preload: true }
      },
      {
        path: 'manage/user/create',
        component: CreateUserComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'ACCESS MANAGEMENT', preload: true }
      },
      {
        path: 'manage/entity/user/create',
        component: EntityuserComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'ACCESS MANAGEMENT', preload: true }
      },
      {
        path: 'manage/user/update',
        component: UpdateUserComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'ACCESS MANAGEMENT', preload: true }
      },
      {
        path: 'manage/user/profile',
        component: UserProfileComponent,
        // canActivate: [CanActivateModuleGuard],
        // data: { permission: 'ACCESS MANAGEMENT', preload: true }
      },
      {
        path: 'end-of-day',
        component: EndOfDayComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'EOD MANAGEMENT', preload: true }
      },
      // /system/salary/
      {
        path: 'salary-transaction/maintenance',
        component: SalaryTransactionsComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'TRANSACTION MAINTENANCE', preload: true }
      },
      {
        path: 'salary-transaction/data/view',
        component: SalaryTransactionsDataComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'TRANSACTION MAINTENANCE', preload: true }
      },
      {
        path: 'batch-salaries-transaction/maintenance',
        component: BatchSalariesMaintenanceComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'TRANSACTION MAINTENANCE', preload: true }
      },
      {
        path: 'batch-salaries-transaction/data/view',
        component: BatchSalariesComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'TRANSACTION MAINTENANCE', preload: true }
      },
      // WORK CLASS  MANAGEMENT
      {
        path: 'workclass/maintenance',
        component: WorkClassMaintenanceComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'ACCESS MANAGEMENT', preload: true }
      },
      {
        path: 'workclass/data/view',
        component: WorkClassComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'ACCESS MANAGEMENT', preload: true }
      },
      // WORK CLASS ACTION MANAGEMENT
      {
        path: 'workclassactions/maintenance',
        component: WorkClassActionsMaintenanceComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'ACCESS MANAGEMENT', preload: true }
      },
      {
        path: 'workclassactions/data/view',
        component: WorkClassActionsComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'ACCESS MANAGEMENT', preload: true }
      },

      {
        path: 'roles/maintenance',
        component: RolesMaintenanceComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'ACCESS MANAGEMENT', preload: true }
      },
      {
        path: 'roles/data/view',
        component: RolesManagementComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'ACCESS MANAGEMENT', preload: true }
      },
      // TELLERS MANAGEMENT
      {
        path: 'tellers/maintenance',
        component: TellerMaintenanceComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'ACCESS MANAGEMENT', preload: true }
      },
      //system/salary
      {
        path: 'tellers/data/view',
        component: TellersManagementComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'ACCESS MANAGEMENT', preload: true }
      },
      //notifications all
      {
        path: 'new/notification/allerts',
        component: NotificationAllertsComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'APPROVALS', preload: true }
      },
      //atm card application routes
      {
        path:'card-maintenance',
        component:CardApplicationMaintenanceComponent,
      },
      {
        path:'application',
        component: CardApplicationComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'ACCOUNTS MANAGEMENT', preload: true }
      },
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class AdministrationRoutingModule { }
