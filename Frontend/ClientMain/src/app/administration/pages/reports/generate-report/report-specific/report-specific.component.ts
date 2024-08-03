import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { Subscription } from 'rxjs';
import { ReportService } from '../../report.service';

@Component({
  selector: 'app-report-specific',
  templateUrl: './report-specific.component.html',
  styleUrls: ['./report-specific.component.scss']
})
export class ReportSpecificComponent implements OnInit {

  subscription!: Subscription;
  paramName: any;

  constructor(
    private fb: FormBuilder,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private reportService: ReportService
  ) { }

  ngOnInit(): void {
    this.onInitForm();
    this.paramName = this.data.test
    console.log("data: ", this.data.test)
  }

  downloadReport = this.fb.group({
   
    fromDate: ['', [Validators.required]],
    toDate: ['', [Validators.required]],
  });

  onInitForm(){
    this.downloadReport = this.fb.group({
     
      fromDate: ['', [Validators.required]],
      toDate: ['', [Validators.required]],
    });
  }

  generateReport(){
   
    if(this.paramName == 'consDailyLiquidity'){
      
      window.open('/assets/liquidity_statment.pdf', '_blank');

    }
    else if(this.paramName == 'liquiditystatement'){

      window.open('/assets/liquidity_statment.pdf', '_blank');
    }
    else if(this.paramName == 'statementofcomprehensiveincome'){

      window.open('/assets/liquidity_statment.pdf', '_blank');
    }

   

  }


}
