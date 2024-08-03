import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder, Validators } from '@angular/forms';

@Component({
  selector: 'app-arrearsgeneralstatement',
  templateUrl: './arrearsgeneralstatement.component.html',
  styleUrls: ['./arrearsgeneralstatement.component.scss']
})
export class ARREARSGENERALSTATEMENTComponent implements OnInit {

  constructor(
    private fb: UntypedFormBuilder
  ) { }

  ngOnInit(): void {
    this.onInitForm();
  }

  downloadReport = this.fb.group({
    reportType: ['', [Validators.required]],
    fromDate: ['', [Validators.required]],
    toDate: ['', [Validators.required]],
  });

  onInitForm(){
    this.downloadReport = this.fb.group({
      reportType: ['', [Validators.required]],
      fromDate: ['', [Validators.required]],
      toDate: ['', [Validators.required]],
    });
  }

  generateReport(){
    window.open('/assets/liquidity_statment.pdf', '_blank');
  }
  // loanrepayment() {
//   window.open('/assets/classified_assets.pdf', '_blank');
// }

}
