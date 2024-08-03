import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder, Validators } from '@angular/forms';

@Component({
  selector: 'app-loan-repayment',
  templateUrl: './loan-repayment.component.html',
  styleUrls: ['./loan-repayment.component.scss']
})
export class LoanRepaymentComponent implements OnInit {

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

  onInitForm() {
    this.downloadReport = this.fb.group({
      reportType: ['', [Validators.required]],
      fromDate: ['', [Validators.required]],
      toDate: ['', [Validators.required]],
    });
  }

  generateReport() {
    window.open('/assets/classified_assets.pdf', '_blank');
  }
  // loanrepayment() {
  //   window.open('/assets/classified_assets.pdf', '_blank');
  // }

}
