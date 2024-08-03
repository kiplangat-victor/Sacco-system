import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import {ReportDefination2} from '../interfaces/report-defination2';
import {ReportService} from "../report.service";
import {DownloadRequest} from "../interfaces/downloadRequest";
import {DomSanitizer} from "@angular/platform-browser";

@Component({
  selector: 'app-specific-report',
  templateUrl: './specific-report.component.html',
  styleUrls: ['./specific-report.component.scss']
})
export class SpecificReportComponent implements OnInit {
  message!:ReportDefination2;
  reportPresent = false;
  request!: DownloadRequest;
  pdfurl!: any;

  params!: any;

  accountCode!: string
  startDate!: Date
  endDate!: Date
  sol_code!: string
  manager!: string
  branch!: string

  accountCodeAvailable: boolean=false
  startDateAvailable: boolean=false
  endDateAvailable: boolean=false
  sol_codeAvailable: boolean=false
  managerAvailable: boolean=false
  branchAvailable: boolean=false



  sols: string[]=[
    "KIAMBU","THIKA","KERICHO","NAIROBI"
  ]
  testurl: string;

  constructor(private router: Router, private reportservice:ReportService,public sanitizer: DomSanitizer,) {
    this.message = this.router.getCurrentNavigation()!.extras.state!['message']
    this.params={}
   }

  ngOnInit(): void {
    for (let param of this.message.parameterList!) {
      if(param.parameterName=="start_date"){
        this.startDateAvailable=true
      }
      if(param.parameterName=="end_date"){
        this.endDateAvailable=true
      }
      if(param.parameterName=="account_no"){
        this.accountCodeAvailable=true
      }
      if(param.parameterName=="manager"){
        this.managerAvailable=true
      }
      if(param.parameterName=="sol_code"){
        this.sol_codeAvailable=true
      }

    }
  }

  parameters(){
  }

downloadReport() {
  for (let param of this.message.parameterList!) {
    if(param.parameterName=="start_date"){
      this.params.start_date=this.startDate
    }
    if(param.parameterName=="end_date"){
      this.params.end_date=this.endDate
    }
    if(param.parameterName=="account_no"){
      this.params.account_no=this.accountCode
    }
    if(param.parameterName=="manager"){
      this.params.manager=this.manager
    }
    if(param.parameterName=="sol_code"){
      this.params.sol_code=this.sol_code
    }
  }
    this.request= {
      sn: this.message.sn,
      download: true,
      type: "PDF",
      parameters: this.params
    }
  this.reportservice.downloadReport(this.request).subscribe(response => {
      this.testurl = window.URL.createObjectURL(response.data);
      this.reportPresent = true;
      //this.pdfurl=this.sanitizer.bypassSecurityTrustResourceUrl(this.testurl);
    }, error => {
    }
  )
}

  pdfURL() {
    return this.sanitizer.bypassSecurityTrustResourceUrl(this.testurl);
  }

}
