import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder, Validators } from '@angular/forms';

@Component({
  selector: 'app-loan-statement',
  templateUrl: './loan-statement.component.html',
  styleUrls: ['./loan-statement.component.scss']
})
export class LOanStatementComponent implements OnInit {

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
    window.open('/assets/loan_portifolio.pdf', '_blank');
  }
  // loanrepayment() {
  //   window.open('/assets/classified_assets.pdf', '_blank');
  // }

}
