import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder, Validators } from '@angular/forms';

@Component({
  selector: 'app-office-accounts',
  templateUrl: './office-accounts.component.html',
  styleUrls: ['./office-accounts.component.scss']
})
export class OfficeAccountsComponent implements OnInit {

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
