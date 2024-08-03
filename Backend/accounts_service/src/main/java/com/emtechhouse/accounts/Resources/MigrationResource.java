//package com.emtechhouse.accounts.Resources;//package com.emtechhouse.accounts.Resources;
//
//import com.emtechhouse.accounts.Models.Accounts.Migration.MigrationService;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//
//import com.emtechhouse.accounts.Models.Accounts.Loans.LoanCalculatorService;
//import com.emtechhouse.accounts.Models.Accounts.Loans.LoanDemands.DemandSatisfaction.AssetClassificationService;
//import com.emtechhouse.accounts.Models.Accounts.Loans.LoanDemands.DemandSatisfaction.DemandSatisfactionService;
//import com.emtechhouse.accounts.Models.Accounts.Loans.LoanRepository;
//import com.emtechhouse.accounts.Models.Accounts.Loans.LoanSchedule.LoanScheduleRepo;
//import com.emtechhouse.accounts.Models.Accounts.Loans.LoanService;
//import com.emtechhouse.accounts.Models.Accounts.Migration.MigrationRepo;
//import com.emtechhouse.accounts.Utils.Responses.EntityResponse;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.time.LocalDate;
//import java.time.ZoneId;
//import java.util.Calendar;
//import java.util.Date;
//import java.util.List;
//
//@Slf4j
//@RequestMapping("accounts/migration")
//@RestController
//public class MigrationResource {
//    @Autowired
//    MigrationService migrationService;
//
//    @Autowired
//    LoanRepository loanRepository;
//
//    @Autowired
//    LoanScheduleRepo scheduleRepo;
//
//    @Autowired
//    private LoanCalculatorService loanCalculatorService;
//
//    @Autowired
//    LoanService loanService;
//    @Autowired
//    MigrationRepo migrationRepo;
//    @Autowired
//    private AssetClassificationService assetClassificationService;
//    @Autowired
//    private DemandSatisfactionService demandSatisfactionService;
//
//    @Autowired
////    private DemandGenerationService demandGenerationService;
////
////    @PostMapping("open")
////    public ResponseEntity<?> openAccount(@RequestBody MigrationRepo.CustomerInterface savingsModel) {
////        try{
////            return ResponseEntity.status(HttpStatus.CREATED).body(migrationService.addSavingsAccounts(savingsModel));
////        }catch (Exception e) {
////            log.info("Catched Error {} " + e);
////            return null;
////        }
////    }
////
////    @PostMapping("open/saving-accounts/for-all/group-and-individual")
////    public ResponseEntity<?> openAccount() {
////        try{
////            List<EntityResponse> responses = migrationService.createAllCustomersAccount();
////            responses.addAll(migrationService.createAllGroupCustomersAccount());
////            return ResponseEntity.status(HttpStatus.CREATED).body(responses);
////        }catch (Exception e){
////            log.info("Catched Error {} " + e);
////            return null;
////        }
////    }
//////    @PostMapping("open/group/all")
//////    public ResponseEntity<?> openGroupAccount(){
//////        try{
//////            return ResponseEntity.status(HttpStatus.CREATED).body();
//////        }catch (Exception e){
//////            log.info("Catched Error {} " + e);
//////            return null;
//////        }
//////    }
////
//    @PostMapping("open/loan/account/rb")
//    public ResponseEntity<?> openLoanAccountRb() {
//        try{
//            return ResponseEntity.status(HttpStatus.CREATED).body(migrationService.createAllLoanAccounts2());
//        }catch (Exception e){
//            log.info("Catched Error {} " + e);
//            return null;
//        }
//    }
//
//    @PostMapping("/update/interest/flags")
//    public ResponseEntity<?> updateFlags() {
//        try{
//            migrationService.updateIntFlags();
//            return ResponseEntity.status(HttpStatus.CREATED).body("updates");
//        }catch (Exception e){
//            log.info("Catched Error {} " + e);
//            return null;
//        }
//    }
//
//    @GetMapping("/get/guarantors")
//    public ResponseEntity<?> getGuarantors() {
//        try{
//            EntityResponse res= migrationService.getGuarantors();
//            return ResponseEntity.status(HttpStatus.CREATED).body(res);
//        }catch (Exception e){
//            log.info("Catched Error {} " + e);
//            return null;
//        }
//    }
//
//    @PostMapping("/migrate/guarantors")
//    public ResponseEntity<?> migrateGuarantors() {
//        try{
//            List<EntityResponse> res= migrationService.migrateGuarantors();
//            return ResponseEntity.status(HttpStatus.CREATED).body(res);
//        }catch (Exception e){
//            log.info("Catched Error {} " + e);
//            return null;
//        }
//    }
//
//    @PostMapping("/update/guaranteed/amt")
//    public ResponseEntity<?> updateGuaranteedAmt(String acid) {
//        try{
//            EntityResponse res= migrationService.updateGuarantorCurrentAmount(acid);
//            return ResponseEntity.status(HttpStatus.CREATED).body(res);
//        }catch (Exception e){
//            log.info("Catched Error {} " + e);
//            return null;
//        }
//    }
//    @PostMapping("/update/guaranteed/amt/all")
//    public ResponseEntity<?> updateGuaranteedAmt() {
//        try{
//            List<EntityResponse> res= migrationService.updateGuarantorCurrentAmount();
//            return ResponseEntity.status(HttpStatus.CREATED).body(res);
//        }catch (Exception e){
//            log.info("Catched Error {} " + e);
//            return null;
//        }
//    }
//    @DeleteMapping("/delete/guarantors")
//    public ResponseEntity<?> deleteGuarantors() {
//        try{
//            migrationService.deleteGuarantors();
//            return ResponseEntity.status(HttpStatus.CREATED).body("res");
//        }catch (Exception e){
//            log.info("Catched Error {} " + e);
//            return null;
//        }
//    }
////
//    @PostMapping("open/loan/account/fr")
//    public ResponseEntity<?> openLoanAccountFr() {
//        try{
//            return ResponseEntity.status(HttpStatus.CREATED).body(migrationService.createAllLoanAccounts());
//        }catch (Exception e){
//            log.info("Catched Error {} " + e);
//            return null;
//        }
//    }
//    @PutMapping("payOff/fr")
//    public ResponseEntity<?> loanPayoffFr() {
//        try{
//            return ResponseEntity.status(HttpStatus.CREATED).body(migrationService.payOffLoans());
//        }catch (Exception e){
//            log.info("Catched Error {} " + e);
//            return null;
//        }
//    }
//
//    @PutMapping("verify/payOff/fr")
//    public ResponseEntity<?> verifyLoanPayoffFr() {
//        try{
//            return ResponseEntity.status(HttpStatus.CREATED).body(migrationService.verifyPayOffLoans());
//        }catch (Exception e){
//            log.info("Catched Error {} " + e);
//            return null;
//        }
//    }
////
////    @PutMapping("demand/force")
////    public ResponseEntity<List<EntityResponse> > demandForce(@RequestParam  String acid){
////        try {
////            return ResponseEntity.ok().body(demandGenerationService.demandManualForceMigration(acid));
////        }catch (Exception e){
////            log.info("Catched Error {} " + e);
////            return null;
////        }
////    }
////    @PutMapping("demand/all-loans")
////    public ResponseEntity<List<EntityResponse> > demandAll(){
////        try {
////            return ResponseEntity.ok().body(demandGenerationService.generateDemandsForAllLoansMigration());
////        }catch (Exception e){
////            log.info("Catched Error {} " + e);
////            return null;
////        }
////    }
////
////    @PutMapping("transfer-for-overflow-loan-balances-all")
////    public ResponseEntity<List<EntityResponse>> transferForOverflowLonaBalances(){
////        try {
////            return ResponseEntity.ok().body(migrationService.transferForOverflowLonaBalances());
////        }catch (Exception e){
////            log.info("Catched Error {} " + e);
////            return null;
////        }
////    }
////
//////    @PutMapping("satisfy/demand/force")
//////    public ResponseEntity<List<EntityResponse> > satisfyDemandForce(@RequestParam  String acid){
//////        try {
//////            return ResponseEntity.ok().body(demandSatisfactionService.satisfyDemandManualForceMigration(acid));
//////        }catch (Exception e){
//////            log.info("Catched Error {} " + e);
//////            return null;
//////        }
//////    }
////
//////    @PutMapping("satisfy/demand/all")
//////    public ResponseEntity<List<EntityResponse> > satisfyDemandAll() {
//////        try {
//////            return ResponseEntity.ok().body(demandSatisfactionService.satisfyAllDemands());
//////        }catch (Exception e){
//////            log.info("Catched Error {} " + e);
//////            return null;
//////        }
//////    }
////
////    @PutMapping("asset/classification")
////    public ResponseEntity<?> assetClassification(){
////        try{
////            return ResponseEntity.status(HttpStatus.CREATED).body(migrationService.classifyAllLoans());
////        }catch (Exception e){
////            log.info("Catched Error {} " + e);
////            return null;
////        }
////    }
////
////    @PutMapping("verify/disbursment")
////    public ResponseEntity<?> verifyDisbursment(){
////        try{
////            return ResponseEntity.status(HttpStatus.CREATED).body(migrationService.disburseAllMigratedLoans());
////        }catch (Exception e){
////            log.info("Catched Error {} " + e);
////            return null;
////        }
////    }
////
////    @GetMapping("get/shedules/number")
////    public ResponseEntity<?> getSchedulesNumber(@RequestParam String startDate, @RequestParam String endDate, @RequestParam Integer installments){
////        try{
////            return ResponseEntity.status(HttpStatus.CREATED).body(migrationService.getFulfilledSchedules(LocalDate.parse(startDate),LocalDate.parse(endDate), installments));
////        }catch (Exception e){
////            log.info("Catched Error {} " + e);
////            return null;
////        }
////    }
////
////    @PutMapping("pay/off")
////    public ResponseEntity<?> payOff(@RequestParam String payOffAccount, @RequestParam String maturityDate){
////        try{
////            return ResponseEntity.status(HttpStatus.CREATED).body(migrationService.payOffAllAccounts(payOffAccount, LocalDate.parse(maturityDate)));
////        }catch (Exception e){
////            log.info("Catched Error {} " + e);
////            return null;
////        }
////    }
////
////    @PutMapping("verify/pay/off")
////    public ResponseEntity<?> verifyPayOff(){
////        try{
////            return ResponseEntity.status(HttpStatus.CREATED).body(migrationService.verifyPayOffAllAccounts());
////        }catch (Exception e){
////            log.info("Catched Error {} " + e);
////            return null;
////        }
////    }
//////    @PostMapping("create-loan-schedules")
//////    @Transactional
//////    public ResponseEntity<?> createLoanschedulesForAll() {
//////        try {
//////            List<Loan> loans = loanRepository.findAllForSchedules();
//////            System.out.println("Number of loans: "+loans.size());
//////            for (Loan loan: loans) {
//////                System.out.println(loan.getTotalLoanBalance());
//////                loan.setLoanSchedules(loanCalculatorService.fixedRateLoanScheduleCalculator (
//////                        loan.getNumberOfInstallments(),
//////                        "MONTHS",
//////                        1,
//////                        loan.getPrincipalAmount(),
//////                        loan.getInterestRate(),
//////                        toPaymentDate(loan.getDisbursementDate())
//////                ));
//////                System.out.println("Number of schedules "+loan.getLoanSchedules().size());
////////                System.out.println(Arrays.deepToString(loan.getLoanSchedules().toArray()));
//////                for (LoanSchedule schedule: loan.getLoanSchedules()) {
//////                    schedule.setLoan(loan);
//////                }
//////
//////                loanRepository.save(loan);
////////                loanRepository.save(loan);
//////            }
//////            return ResponseEntity.status(HttpStatus.CREATED).body("Done");
//////        }catch (Exception e) {
//////            log.info("Catched Error {} " + e);
//////            return null;
//////        }
//////    }
////
////    private LocalDate toPaymentDate(Date input) {
////        Calendar cal = Calendar.getInstance();
////        cal.setTime(input);
////        int currentMonth = cal.get(Calendar.MONTH);
////
////        int newDay;
////        int newMonth;
////        int newYear;
////
////        if (cal.get(Calendar.DAY_OF_MONTH) <= 9) {
////            newDay = 9;
////            newMonth = currentMonth+1;
////            newYear = cal.get(Calendar.YEAR);
////        }else{
////            newDay = 9;
////            newMonth = currentMonth+2;
////            newYear = cal.get(Calendar.YEAR);
////        }
////        if (newMonth > 12)
////            newYear++;
////        cal.set(newYear, newMonth, newDay);
////        return cal.getTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
////    }
//}