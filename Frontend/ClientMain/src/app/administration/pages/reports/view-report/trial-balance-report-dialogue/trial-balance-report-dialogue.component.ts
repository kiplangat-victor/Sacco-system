import { DatePipe } from '@angular/common';
import { HttpParams } from '@angular/common/http';
import { Component, Inject, OnInit } from '@angular/core';
import { FormGroup, UntypedFormBuilder, Validators } from '@angular/forms';
import { MatDialog, MatDialogConfig, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatSnackBar, MatSnackBarHorizontalPosition, MatSnackBarVerticalPosition } from '@angular/material/snack-bar';
import { Subscription } from 'rxjs';
import { AuthService } from 'src/@core/AuthService/auth.service';
import { ReportsService } from 'src/app/administration/Service/reports/reports.service';
import { SolCodeLookupComponent } from '../../accounts-lookup/sol-code-lookup/sol-code-lookup.component';
import { ViewReportComponent } from '../view-report.component';

@Component({
  selector: 'app-trial-balance-report-dialogue',
  templateUrl: './trial-balance-report-dialogue.component.html',
  styleUrls: ['./trial-balance-report-dialogue.component.scss']
})
export class TrialBalanceReportDialogueComponent implements OnInit {
  trialBalanceForm: FormGroup;
  action: string;
  dialogTitle: string;
  schemeTypes: any = [
    { code: 'LAA', description: 'Loan Accounts' },
    { code: 'ODA', description: 'Overdraft Accounts' },
    { code: 'SBA', description: 'Savings Accounts' },
    { code: 'TDA', description: 'Term Deposits' },
    { code: 'CAA', description: 'Current Accounts' },
  ];
  types: any = ["Retail Accounts", "Cooperate Accounts"];
  scope: any = ["All Accounts", "Branch Accounts"];
  users: any[] = [];

  subscription!: Subscription;
  error: any;
  loading: boolean;
  accountTypesReports: boolean = true;
  accountStatetement: boolean = false;
  horizontalPosition: MatSnackBarHorizontalPosition = 'end';
  verticalPosition: MatSnackBarVerticalPosition = 'top';
  reportTitle: string;
  trialBalanceSelected: boolean = false;
  branchTrialBalanceSelected: boolean = false;

  constructor(
    private fb: UntypedFormBuilder,
    private datepipe: DatePipe,
    private dialog: MatDialog,
    private _snackBar: MatSnackBar,
    private reportsService: ReportsService,
    public dialogRef: MatDialogRef<ViewReportComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private authService: AuthService
  ) { }

  ngOnInit(): void {
    console.log(this.data);

    this.reportTitle = this.data.action;

    if (this.data.action == "Trial Balance Report") {
      this.trialBalanceSelected = true;
      this.branchTrialBalanceSelected = false;
    } else {
      this.trialBalanceSelected = false;
      this.branchTrialBalanceSelected = true;
    }


    this.trialBalanceForm = this.initTrialBalanceForm();

  }

  getData() {
    this.subscription = this.authService.allUsers().subscribe(res => {
      this.users = res;

      console.log("Users : ", this.users)
    }, err => {
      console.log(err);
    })
  }


  initTrialBalanceForm(): FormGroup {
    return this.fb.group({
      branchCode: ['', [Validators.required]],
    });
  }


  solCodeLookup() {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.disableClose = false;
    dialogConfig.autoFocus = true;
    dialogConfig.width = '400px';
    dialogConfig.data = {
      data: '',
    };
    const dialogRef = this.dialog.open(SolCodeLookupComponent, dialogConfig);
    dialogRef.afterClosed().subscribe((result) => {

      this.trialBalanceForm.patchValue({ branchCode: result.data.branchCode })
    });
  }

  onSubmit() {
    if (this.trialBalanceSelected) {
      this.generateTrialBalanceReport();
    } else {
      this.generateBranchTrialBalanceReport();
    }
  }


  generateBranchTrialBalanceReport() {

    this.loading = true;

    if (this.trialBalanceForm.valid) {
      const params = new HttpParams()
        .set('branchCode', this.trialBalanceForm.value.branchCode);

      this.subscription = this.reportsService
        .generateBranchTrialBalanceReport(params)
        .subscribe(
          (response) => {
            let url = window.URL.createObjectURL(response.data);

            // if you want to open PDF in new tab
            window.open(url);

            let a = document.createElement('a');
            document.body.appendChild(a);
            a.setAttribute('style', 'display: none');
            a.setAttribute('target', 'blank');
            a.href = url;
            a.download = response.filename;
            a.click();
            window.URL.revokeObjectURL(url);
            a.remove();

            this.loading = false;

            this.dialogRef.close();

            this._snackBar.open('Report generated successfully !', 'X', {
              horizontalPosition: this.horizontalPosition,
              verticalPosition: this.verticalPosition,
              duration: 3000,
              panelClass: ['green-snackbar', 'login-snackbar'],
            });
          },
          (err) => {
            this.error = err;
            this.loading = false;

            this.dialogRef.close();

            this._snackBar.open(`Error generating report !`, 'X', {
              horizontalPosition: this.horizontalPosition,
              verticalPosition: this.verticalPosition,
              duration: 3000,
              panelClass: ['red-snackbar', 'login-snackbar'],
            });
          }
        );
    } else {
      this._snackBar.open(`Please fill out the form correctly !`, 'X', {
        horizontalPosition: this.horizontalPosition,
        verticalPosition: this.verticalPosition,
        duration: 3000,
        panelClass: ['red-snackbar', 'login-snackbar'],
      });

      this.loading = false;
    }
  }

  generateTrialBalanceReport() {
    this.loading = true;

    const params = new HttpParams()
      .set('branchCode', this.trialBalanceForm.value.branchCode);

    this.subscription = this.reportsService
      .generateTrialBalanceReport()
      .subscribe(
        (response) => {
          let url = window.URL.createObjectURL(response.data);

          // if you want to open PDF in new tab
          window.open(url);

          let a = document.createElement('a');
          document.body.appendChild(a);
          a.setAttribute('style', 'display: none');
          a.setAttribute('target', 'blank');
          a.href = url;
          a.download = response.filename;
          a.click();
          window.URL.revokeObjectURL(url);
          a.remove();

          this.loading = false;

          this.dialogRef.close();

          this._snackBar.open('Report generated successfully !', 'X', {
            horizontalPosition: this.horizontalPosition,
            verticalPosition: this.verticalPosition,
            duration: 3000,
            panelClass: ['green-snackbar', 'login-snackbar'],
          });
        },
        (err) => {
          this.error = err;
          this.loading = false;

          this.dialogRef.close();

          this._snackBar.open(`Error generating report !`, 'X', {
            horizontalPosition: this.horizontalPosition,
            verticalPosition: this.verticalPosition,
            duration: 3000,
            panelClass: ['red-snackbar', 'login-snackbar'],
          });
        }
      );
  }

  cancel() {
    this.dialogRef.close();
  }
}
