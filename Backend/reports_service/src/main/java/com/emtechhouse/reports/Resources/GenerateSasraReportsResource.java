package com.emtechhouse.reports.Resources;


import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("api/v1/reports")
@Api(tags = "Sasra Reports API")
@Slf4j
@CrossOrigin
public class GenerateSasraReportsResource {
    @Value("${spring.datasource.url}")
    private String db;
    @Value("${spring.datasource.username}")
    private String username;
    @Value("${spring.datasource.password}")
    private String password;

    @Value("${sacco.reports.path}")
    private String path;

    @Value("${sacco.reports.icon}")
    private String report_icon;

    @Value("${sacco.reports.sacco_name}")
    private String sacco_name;


    @GetMapping("/capital-adequacy")
    public ResponseEntity<?> generateCapitalAdequacyReport(@RequestParam String fromdate, @RequestParam String todate) {

        try {

            Connection connection = DriverManager.getConnection(this.db, this.username, this.password);
            JasperReport compilereport = JasperCompileManager.compileReport(new FileInputStream(path + "/sasra/capital_adequacy_report_cs.jrxml"));

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("from_date", fromdate);
            parameters.put("to_date", todate);
            parameters.put("report_icon", report_icon);
            parameters.put("sacco_name", sacco_name);
            parameters.put("cs_number", "AWS002");
            parameters.put("share_capital_code", "500");
            parameters.put("statutory_reserves_code", "600");
            parameters.put("capital_grants_code", "000");
            parameters.put("investments_in_subsidiary_equity_instruments_code", "000");
            parameters.put("retained_earnings_code", "60"); //gl
            parameters.put("general_reserves_code", "000");
            parameters.put("other_reserves_code", "601");
            parameters.put("cash_code", "130");
            parameters.put("net_surplus_after_tax_code", "000");
            parameters.put("deposits_balances_at_other_institutions_code", "000");
            parameters.put("placement_with_banks_code", "000");
            parameters.put("loans_and_advances_code", "110");
            parameters.put("investments_code", "120");
            parameters.put("property_and_equipment_code", "10"); //gl
            parameters.put("other_assets_code", "140");
            parameters.put("other_deductions_code", "000");
            parameters.put("off_balance_sheet_assets_code", "000");
            parameters.put("long_term_liabilities_deposits_code", "210"); //#1
            parameters.put("total_deposits_code", "220");                 //#2
            parameters.put("government_securities_code", "000");

            JasperPrint report = JasperFillManager.fillReport(compilereport, parameters, connection);
            byte[] data = JasperExportManager.exportReportToPdf(report);
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=capital-adequacy-report.pdf");

            return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(data);

        }catch (Exception exc){
            System.out.println(exc.getLocalizedMessage());
            return null;
        }

    }

    @GetMapping("/investment-return")
    public ResponseEntity<?> generateInvestmentReturnReport(@RequestParam String fromdate, @RequestParam String todate){

        try {

            Connection connection = DriverManager.getConnection(this.db, this.username, this.password);
            JasperReport compilereport = JasperCompileManager.compileReport(new FileInputStream(path + "/sasra/investment_return.jrxml"));

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("from_date", fromdate);
            parameters.put("to_date", todate);
            parameters.put("report_icon", report_icon);
            parameters.put("sacco_name", sacco_name);
            parameters.put("cs_number", "AWS002");
            parameters.put("core_capital_code", "000");
            parameters.put("fixed_assets_code", "10"); //gl
            parameters.put("loans_assets_code", "11"); //gl
            parameters.put("current_assets_code", "12");//gl
            parameters.put("cash_and_cash_equivalents_assets_code", "13");//gl
            parameters.put("interest_due_assets_code", "14"); //gl
            parameters.put("long_term_liabilities_deposits_code", "210");
            parameters.put("member_deposits_code", "220");
            parameters.put("non_earning_assets_code", "000");
            parameters.put("financial_investments_code", "120");
            parameters.put("land_and_building_per_balancesheet_code", "000");


            JasperPrint report = JasperFillManager.fillReport(compilereport, parameters, connection);
            byte[] data = JasperExportManager.exportReportToPdf(report);
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=investments-return-report.pdf");

            return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(data);

        }catch (Exception exc){
            System.out.println(exc.getLocalizedMessage());
            return null;
        }

    }

    @GetMapping("/financial-position")
    public ResponseEntity<?> generateStatementofFinancialPositionReport(@RequestParam String fromdate, @RequestParam String todate){

        try {

            //edit form numbergit

            Connection connection = DriverManager.getConnection(this.db, this.username, this.password);
            JasperReport compilereport = JasperCompileManager.compileReport(new FileInputStream(path + "/sasra/statement_of_financial_position.jrxml"));

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("from_date", fromdate);
            parameters.put("to_date", todate);
            parameters.put("report_icon", report_icon);
            parameters.put("sacco_name", sacco_name);
            parameters.put("cs_number", "AWS002");
            parameters.put("statutory_reserve_code","600");
            parameters.put("cash_and_equivalents_code", "13"); // gl
            parameters.put("cash_in_hand_petty_cash_code", "130"); //#1 glsubhead and acid
            parameters.put("cash_in_hand_teller_1_code", "130");   //#2 glsubhead and acid
            parameters.put("cash_in_hand_teller_2_code", "130");   //#3 glsubhead and acid
            parameters.put("cash_at_bank_GTBank_current_account_code", "130"); //@1 glsubhead and acid
            parameters.put("cash_at_bank_GTBank_savings_account_code", "130"); //@2 glsubhead and acid
            parameters.put("cash_at_bank_NIC_Bank_statutory_code", "130");     //@3 glsubhead and acid
            parameters.put("cash_at_bank_Mpesa_deposit_code", "130");          //@4 glsubhead and acid
            parameters.put("cash_at_bank_treasury_code", "130");               //@5 glsubhead and acid
            parameters.put("government_securities_bills_bonds_code", "000");
            parameters.put("balances_with_other_sacco_code", "000");
            parameters.put("investment_in_companies_code", "120");
            parameters.put("investment_properties_code", "000");
            parameters.put("gross_loan_portfolio_code", "110");
            parameters.put("allowances_for_loan_loss_code", "000"); //ask for further info
            parameters.put("tax_recoverable_code", "000");
            parameters.put("diferred_tax_assets_code", "000");  //*1 glsubhead and acid  rl //ask for further info
            parameters.put("retirement_benefit_asset_code", "000");
            parameters.put("property_and_equipment_code", "10"); //gl
            parameters.put("prepaid_lease_rentals_code", "000");
            parameters.put("intangible_assets_code", "000");
            parameters.put("other_assets_code", "000");
            parameters.put("savings_deposits_code", "220");    // glsubhead and acid
            parameters.put("short_term_deposits_code", "220"); // glsubhead and acid
            parameters.put("non_withdrawable_deposits_code", "210");
            parameters.put("tax_payable_code", "250"); // glsubhead and acid            rl
            parameters.put("dividends_payable_code","000");
            parameters.put("diferred_tax_liability_code","000"); //ask for further info rl
            parameters.put("retirement_benefits_liability_code","000");
            parameters.put("other_liabilities_code","000"); //ask for further info
            parameters.put("external_borrowings_code","000");
            parameters.put("share_capital_code","500");
            parameters.put("capital_grants_code","000");
            parameters.put("current_year_surplus_code","000");
            parameters.put("retained_earnings_code","60"); //gl
            parameters.put("other_reserves_code","000");
            parameters.put("revaluation_reserves_code","000");
            parameters.put("proposed_dividends_code","000");
            parameters.put("adjustment_to_equity_code","000");
            parameters.put("repayment_and_sundry_receivables_code","000");
            parameters.put("prior_year_retained_earnings_code","000");
            parameters.put("general_reserves_code","000");
            parameters.put("other_securities_code","000");



            JasperPrint report = JasperFillManager.fillReport(compilereport, parameters, connection);
            byte[] data = JasperExportManager.exportReportToPdf(report);
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=statement_of-financial-position-report.pdf");

            return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(data);

        }catch (Exception exc){
            System.out.println(exc.getLocalizedMessage());
            return null;
        }

    }


    @GetMapping("/comprehensive-income")
    public ResponseEntity<?> generateStatementofComprehensiveIncomeReport(@RequestParam String fromdate, @RequestParam String todate){

        try {

            Connection connection = DriverManager.getConnection(this.db, this.username, this.password);
            JasperReport compilereport = JasperCompileManager.compileReport(new FileInputStream(path + "/sasra/statement_of_comprehensive_income.jrxml"));

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("from_date", fromdate);
            parameters.put("to_date", todate);
            parameters.put("report_icon", report_icon);
            parameters.put("sacco_name", sacco_name);
            parameters.put("cs_number", "AWS002");
            parameters.put("interest_on_loan_portfolio_code", "300");
            parameters.put("fees_and_commission_on_loan_code", "302");
            parameters.put("government_securities_code", "000");
            parameters.put("deposits_with_banks_code", "000");
            parameters.put("other_investments_code", "000");
            parameters.put("other_operating_income_code", "000");
            parameters.put("interest_expense_on_deposits_code", "403"); // glsubhead and acid
            parameters.put("cost_of_external_borrowings_code", "000");
            parameters.put("dividend_expenses_code", "000");
            parameters.put("other_financial_expenses_code", "000");
            parameters.put("fees_and_commision_expense_code", "000");
            parameters.put("other_expense_code", "404");
            parameters.put("net_operating_income_code", "30"); //gl
            parameters.put("provision_for_loan_losses_code", "000");
            parameters.put("value_for_loans_recovered_code", "000");
            parameters.put("personnel_expenses_code", "401");  // glsubhead and acid
            parameters.put("governance_expenses_code", "000");
            parameters.put("marketing_expenses_code", "000");
            parameters.put("depreciation_and_amortization_code", "000");
            parameters.put("administrative_expenses_code", "400");
            parameters.put("non_operating_income_code", "000");
            parameters.put("non_operating_expense_code", "000");
            parameters.put("taxes_code", "250");  // glsubhead and acid
            parameters.put("donations_code", "000");

            JasperPrint report = JasperFillManager.fillReport(compilereport, parameters, connection);
            byte[] data = JasperExportManager.exportReportToPdf(report);
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=statement-comprehensive-income-report.pdf");

            return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(data);

        }catch (Exception exc){
            System.out.println(exc.getLocalizedMessage());
            return null;
        }

    }

    @GetMapping("/consolidated-daily-liquidity")
    public ResponseEntity<?> generateConsolidatedDailyLiquidityReport(){

        try {

            Connection connection = DriverManager.getConnection(this.db, this.username, this.password);
            JasperReport compilereport = JasperCompileManager.compileReport(new FileInputStream(path + "/sasra/consolidated_daily_liquidity.jrxml"));

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("report_icon", report_icon);
            parameters.put("sacco_name", sacco_name);
            parameters.put("cs_number", "AWS002");
            parameters.put("bank_balances_code", "0001");
            parameters.put("cash_at_bank_GTBank_current_account_code", "130"); //@1 glsubhead and acid
            parameters.put("cash_at_bank_GTBank_savings_account_code", "130"); //@2 glsubhead and acid
            parameters.put("cash_at_bank_NIC_Bank_statutory_code", "130");     //@3 glsubhead and acid
            parameters.put("cash_at_bank_Mpesa_deposit_code", "130");          //@4 glsubhead and acid
            parameters.put("cash_at_bank_treasury_code", "130");               //@5 glsubhead and acid
            parameters.put("consolidated_treasury_cash_code", "130"); //glsubhead and acid
            parameters.put("teller_2_balances_code", "130"); //#1glsubhead and acid
            parameters.put("teller_1_balances_code", "130"); //#2glsubhead and acid
            parameters.put("petty_cash_code", "130"); // glsubhead and acid
            parameters.put("mobile_money_channels_code", "130"); //glsubhead and acid
            parameters.put("placement_with_banks_code", "000");
            parameters.put("deposits_from_long_term_members_other_than_loan_code", "210"); //#1
            parameters.put("deposits_from_members_other_than_loan_code", "220"); //#2
            parameters.put("cash_loan_repayment_code", "000");
            parameters.put("other_cash_receipts_code", "000");
            parameters.put("cash_withdrawal_from_members_other_than_loan_code", "000");
            parameters.put("cash_payments_other_than_to_members_code", "000");
            parameters.put("other_cash_payments_code", "000");
            parameters.put("bank_balances_closing_code", "0013");
            parameters.put("closing_consolidated_treasury_cash_balance_code", "130"); //glsubhead and acid
            parameters.put("closing_tellers_balance_code", "0015");
            parameters.put("closing_mobile_money_channels_code", "0016");
            parameters.put("closing_placement_with_banks_code", "0017");
            parameters.put("FOSA_deposits_code", "000");
            parameters.put("BOSA_deposits_code", "210");

            JasperPrint report = JasperFillManager.fillReport(compilereport, parameters, connection);
            byte[] data = JasperExportManager.exportReportToPdf(report);
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=consolidated-daily-liquidity-report.pdf");

            return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(data);

        }catch (Exception exc){
            System.out.println(exc.getLocalizedMessage());
            return null;
        }

    }

    @GetMapping("/deposit-return")
    public ResponseEntity<?> generateStatementDepositReturnReport(@RequestParam String fromdate, @RequestParam String todate){

        try {

            Connection connection = DriverManager.getConnection(this.db, this.username, this.password);
            JasperReport compilereport = JasperCompileManager.compileReport(new FileInputStream(path + "/sasra/statement_of_deposit_return.jrxml"));

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("report_icon", report_icon);
            parameters.put("sacco_name", sacco_name);
            parameters.put("cs_number", "AWS002");
            parameters.put("from_date", "0001");
            parameters.put("to_date", "0001");
            parameters.put("non_withdrawable_deposit_code", "210");
            parameters.put("savings_deposit_code", "220");
            parameters.put("term_deposit_code", "220");

            JasperPrint report = JasperFillManager.fillReport(compilereport, parameters, connection);
            byte[] data = JasperExportManager.exportReportToPdf(report);
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=statement-deposit-return-report.pdf");

            return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(data);

        }catch (Exception exc){
            System.out.println(exc.getLocalizedMessage());
            return null;
        }

    }

    @GetMapping("/liquidity-statement")
    public ResponseEntity<?> generateLiquidityStatementReport(@RequestParam String fromdate, @RequestParam String todate){

        try {

            Connection connection = DriverManager.getConnection(this.db, this.username, this.password);
            JasperReport compilereport = JasperCompileManager.compileReport(new FileInputStream(path + "/sasra/liquidity_statement.jrxml"));

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("report_icon", report_icon);
            parameters.put("sacco_name", sacco_name);
            parameters.put("cs_number", "AWS002");
            parameters.put("from_date", fromdate);
            parameters.put("to_date", todate);
            parameters.put("local_notes_and_coins_code", "000");
            parameters.put("foreign_notes_and_coins_code", "000");
            parameters.put("balance_with_commercial_banks_code", "000");
            parameters.put("time_deposits_with_banks_code", "000");
            parameters.put("overdraft_and_matured_loans_code", "000");
            parameters.put("balances_with_other_sacco_code", "000");
            parameters.put("balances_with_other_institutions_code", "000");
            parameters.put("balances_due_to_other_than_banks_code", "000");
            parameters.put("matured_loans_and_advances_received_code", "000");
            parameters.put("treasury_bills_code", "000");
            parameters.put("treasury_bonds_code", "000");
            parameters.put("long_term_deposits_from_members_code", "210");
            parameters.put("deposits_from_members_code", "220");
            parameters.put("deposits_from_other_sources_code", "000");
            parameters.put("balances_due_to_sacco_societies_code", "000");
            parameters.put("balances_due_to_other_financial_insitutions_than_banks_code", "000");
            parameters.put("matured_liabilities_code", "000");
            parameters.put("liabilities_maturing_within_91_days_code", "000");


            JasperPrint report = JasperFillManager.fillReport(compilereport, parameters, connection);
            byte[] data = JasperExportManager.exportReportToPdf(report);
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=liquidity-statement-report.pdf");

            return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(data);

        }catch (Exception exc){
            System.out.println(exc.getLocalizedMessage());
            return null;
        }

    }


    @GetMapping("/risk-classification")
    public ResponseEntity<?> generateRiskClassificationReport(@RequestParam String fromdate, @RequestParam String todate){

        try {

            Connection connection = DriverManager.getConnection(this.db, this.username, this.password);
            JasperReport compilereport = JasperCompileManager.compileReport(new FileInputStream(path + "/sasra/risk_classification_of_assets.jrxml"));

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("report_icon", report_icon);
            parameters.put("sacco_name", sacco_name);
            parameters.put("cs_number", "AWS002");
            parameters.put("start_date", fromdate);
            parameters.put("end_date", todate);
            parameters.put("perfoming_to_period", 30);
            parameters.put("watch_from_period", 31);
            parameters.put("watch_to_period", 60);
            parameters.put("substandard_from_period", 61);
            parameters.put("substandard_to_period", 120);
            parameters.put("doubtful_from_period", 121);
            parameters.put("doubtful_to_period", 365);
            parameters.put("loss_period", 365);

            JasperPrint report = JasperFillManager.fillReport(compilereport, parameters, connection);
            byte[] data = JasperExportManager.exportReportToPdf(report);
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=risk-classification-report.pdf");

            return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(data);

        }catch (Exception exc){
            System.out.println(exc.getLocalizedMessage());
            return null;
        }

    }


    @GetMapping("/sacco-agency-return")
    public ResponseEntity<?> generateSaccoAgencyReturnReport(@RequestParam String fromdate, @RequestParam String todate){

        try {

            Connection connection = DriverManager.getConnection(this.db, this.username, this.password);
            JasperReport compilereport = JasperCompileManager.compileReport(new FileInputStream(path + "/sasra/sacco_agency_returns.jrxml"));

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("report_icon", report_icon);
            parameters.put("sacco_name", sacco_name);
            parameters.put("cs_number", "AWS002");
            parameters.put("from_date", fromdate);
            parameters.put("to_date", todate);

            JasperPrint report = JasperFillManager.fillReport(compilereport, parameters, connection);
            byte[] data = JasperExportManager.exportReportToPdf(report);
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=sacco-agency-return-report.pdf");

            return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(data);

        }catch (Exception exc){
            System.out.println(exc.getLocalizedMessage());
            return null;
        }

    }




}
