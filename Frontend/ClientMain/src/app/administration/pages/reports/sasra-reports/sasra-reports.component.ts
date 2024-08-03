import { Component, OnInit } from '@angular/core';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { DataStoreService } from 'src/@core/helpers/data-store.service';
import { SasraReportDialogueComponent } from './sasra-report-dialogue/sasra-report-dialogue.component';
@Component({
  selector: 'app-sasra-reports',
  templateUrl: './sasra-reports.component.html',
  styleUrls: ['./sasra-reports.component.scss']
})
export class SasraReportsComponent implements OnInit {
  reports!: any
  actionsArray: any[];
  consolidatedliquidityreport=false;
  liquiditystatementsreport=false;
  statementscomprehensiveincomereport=false;
  statementofdepositreturnreport=false;
  statementoffinancialpositionreport=false;
  capitaladequacyreport=false;
  investementreturnreport=false;
  riskofclassificationreport=false;

  constructor(
    private dialog: MatDialog,
     private dataStoreApi: DataStoreService
   ) {
     this.iniAuthorization();
   }
   ngOnInit(): void {}
   iniAuthorization(){
     this.actionsArray = this.dataStoreApi.getActions("REPORTS");
     for(let i = 0; i< this.actionsArray.length; i++){
       let obj = this.actionsArray[i];
       if(obj.name === "CONSOLIDATED LIQUIDITY REPORT" && obj.selected === true){
         this.consolidatedliquidityreport = true;
       }
       if(obj.name === "LIQUIDITY STATEMENTS REPORT" && obj.selected === true){
         this.liquiditystatementsreport = true;
       }
       if(obj.name === "STATEMENTS COMPREHENSIVE INCOME REPORT" && obj.selected === true){
         this.statementscomprehensiveincomereport = true;
       }
       if(obj.name === "STATEMENT OF DEPOSIT RETURN REPORT" && obj.selected === true){
         this.statementofdepositreturnreport = true;
       }
       if(obj.name === "STATEMENT OF FINANCIAL POSITION REPORT" && obj.selected === true){
         this.statementoffinancialpositionreport = true;
       }
       if(obj.name === "CAPITAL ADEQUACY REPORT" && obj.selected === true){
         this.capitaladequacyreport = true;
       }
       if(obj.name === "INVESTEMENT RETURN REPORT" && obj.selected === true){
         this.investementreturnreport = true;
       }
       if(obj.name === "RISK OF CLASSIFICATION REPORT" && obj.selected === true){
         this.riskofclassificationreport = true;
       }
     }
   }
  generateCapitalAdequacyReport(): void {
  const dialogConfig = new MatDialogConfig();
  dialogConfig.disableClose = false;
  dialogConfig.autoFocus = true;
  dialogConfig.width = "800px";
  dialogConfig.data = {
    data: "",
    action: "Adequacy Report"
  };
  const dialogRef = this.dialog.open(SasraReportDialogueComponent, dialogConfig);
  dialogRef.afterClosed().subscribe((result) => {

  });
}

generateStatementComprehensiveReport(): void {
  const dialogConfig = new MatDialogConfig();
  dialogConfig.disableClose = false;
  dialogConfig.autoFocus = true;
  dialogConfig.width = "800px";
  dialogConfig.data = {
    data: "",
    action: "Statement Comprehensive Report"
  };
  const dialogRef = this.dialog.open(SasraReportDialogueComponent, dialogConfig);
  dialogRef.afterClosed().subscribe((result) => {

  });
}

generateStatementofFinancialPositionReport(): void {
  const dialogConfig = new MatDialogConfig();
  dialogConfig.disableClose = false;
  dialogConfig.autoFocus = true;
  dialogConfig.width = "800px";
  dialogConfig.data = {
    data: "",
    action: "Statement of Finacial Position"
  };
  const dialogRef = this.dialog.open(SasraReportDialogueComponent, dialogConfig);
  dialogRef.afterClosed().subscribe((result) => {

  });
}

generateStatementofDepositReturn(): void {
  const dialogConfig = new MatDialogConfig();
  dialogConfig.disableClose = false;
  dialogConfig.autoFocus = true;
  dialogConfig.width = "800px";
  dialogConfig.data = {
    data: "",
    action: "Statement of Deposit Return"
  };
  const dialogRef = this.dialog.open(SasraReportDialogueComponent, dialogConfig);
  dialogRef.afterClosed().subscribe((result) => {

  });
}

generateInvestmentReturnReport(): void {
  const dialogConfig = new MatDialogConfig();
  dialogConfig.disableClose = false;
  dialogConfig.autoFocus = true;
  dialogConfig.width = "800px";
  dialogConfig.data = {
    data: "",
    action: "Investment Return Report"
  };
  const dialogRef = this.dialog.open(SasraReportDialogueComponent, dialogConfig);
  dialogRef.afterClosed().subscribe((result) => {
  });
}

generateConsolidatedDailyLiquidityReport(): void {
  const dialogConfig = new MatDialogConfig();
  dialogConfig.disableClose = false;
  dialogConfig.autoFocus = true;
  dialogConfig.width = "800px";
  dialogConfig.data = {
    data: "",
    action: "Consolidated Daily Liquidity Report"
  };
  const dialogRef = this.dialog.open(SasraReportDialogueComponent, dialogConfig);
  dialogRef.afterClosed().subscribe((result) => {

  });
}

generateRiskClassificationReport(): void {
  const dialogConfig = new MatDialogConfig();
  dialogConfig.disableClose = false;
  dialogConfig.autoFocus = true;
  dialogConfig.width = "800px";
  dialogConfig.data = {
    data: "",
    action: "Risk Classification Report"
  };
  const dialogRef = this.dialog.open(SasraReportDialogueComponent, dialogConfig);
  dialogRef.afterClosed().subscribe((result) => {
  });
}

generateLiquiditystatement(): void {
  const dialogConfig = new MatDialogConfig();
  dialogConfig.disableClose = false;
  dialogConfig.autoFocus = true;
  dialogConfig.width = "800px";
  dialogConfig.data = {
    data: "",
    action: "Liquidity Statement"
  };
  const dialogRef = this.dialog.open(SasraReportDialogueComponent, dialogConfig);
  dialogRef.afterClosed().subscribe((result) => {
  });
}



}
