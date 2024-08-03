import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder, Validators } from '@angular/forms';

@Component({
  selector: 'app-assetsclassifications',
  templateUrl: './assetsclassifications.component.html',
  styleUrls: ['./assetsclassifications.component.scss']
})
export class ASSETSCLASSIFICATIONSComponent implements OnInit {

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
    window.open('/assets/account_statement.pdf', '_blank');
  }
  // loanrepayment() {
//   window.open('/assets/classified_assets.pdf', '_blank');
// }

}
