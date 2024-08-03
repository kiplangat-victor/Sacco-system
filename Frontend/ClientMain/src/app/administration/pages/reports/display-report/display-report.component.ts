import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { MatSnackBarHorizontalPosition, MatSnackBarVerticalPosition, MatSnackBar } from '@angular/material/snack-bar';
import { DomSanitizer } from '@angular/platform-browser';
import { ReportsService } from 'src/app/administration/Service/reports/reports.service';

@Component({
  selector: 'app-display-report',
  templateUrl: './display-report.component.html',
  styleUrls: ['./display-report.component.scss']
})
export class DisplayReportComponent implements OnInit {

  horizontalPosition: MatSnackBarHorizontalPosition = 'end';
  verticalPosition: MatSnackBarVerticalPosition = 'top';
  loading = false;
  pdfurl!: any;
  @Output() back = new EventEmitter<string>();
  @Input() displaydata: any;
  constructor (
    private _snackBar: MatSnackBar, 
    protected sanitizer: DomSanitizer,
    private reportsService: ReportsService) { }

  ngOnInit(): void {
    console.log("In display report");
    console.log(this.displaydata);
    this.generateReportDynamically(this.displaydata.data.params, this.displaydata.data.fileName, false);
  }

  toMain() {
    this.back.emit("");
  }

  ownTab() {
    this.generateReportDynamically(this.displaydata.data.params, this.displaydata.data.fileName, true);
  }

  generateReportDynamically(params, filename, owntab) {
    this.loading = true;
    this.reportsService
    .loadDynamic(params, filename)
    .subscribe(
      (response) => {
        console.log("Received data");
        console.log(response.data);

        if(owntab) {
          let url = window.URL.createObjectURL(response.data);
          // let url2 =this.sanitizer.bypassSecurityTrustResourceUrl(url);
          window.open(url, '_blank');
          // window.open(url2);
        }else{
          let url = window.URL.createObjectURL(response.data);
          this.pdfurl=this.sanitizer.bypassSecurityTrustResourceUrl(url);
          console.log(url);
        }

        // window.open(url);

        // let a = document.createElement('a');
        // document.body.appendChild(a);
        // a.setAttribute('style', 'display: none');
        // a.setAttribute('target', 'blank');
        // a.href = url;
        // a.download = response.filename;
        // a.click();
        // window.URL.revokeObjectURL(url);
        // a.remove();

        this.loading = false;


        this._snackBar.open('Report generated successfully !', 'X', {
          horizontalPosition: this.horizontalPosition,
          verticalPosition: this.verticalPosition,
          duration: 3000,
          panelClass: ['green-snackbar', 'login-snackbar'],
        });
      },
      (err) => {
        console.log(err);
        this.loading = false;

        this._snackBar.open(`Error generating report !`, 'X', {
          horizontalPosition: this.horizontalPosition,
          verticalPosition: this.verticalPosition,
          duration: 3000,
          panelClass: ['red-snackbar', 'login-snackbar'],
        });
      }
    );
  }
}
