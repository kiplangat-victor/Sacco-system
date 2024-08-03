import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { Subject, Subscription } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';
import { ManualLoanPenaltiesService } from '../manual-loan-penalties.service';

@Component({
  selector: 'app-loan-penalties-lookup',
  templateUrl: './loan-penalties-lookup.component.html',
  styleUrls: ['./loan-penalties-lookup.component.scss']
})
export class LoanPenaltiesLookupComponent implements OnInit, OnDestroy {
  displayedColumns: string[] = [
    'index',
    'loanAcid',
    'loanName',
    'penaltyAmount',
    'penaltyDescription',
    'postedFlag',
    'postedTime',
    'postedBy',
    'verifiedFlag'
  ];
  dataSource!: MatTableDataSource<any>;
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;
  data: any;
  loading: boolean;
  destroy$: Subject<boolean> = new Subject<boolean>();
  constructor(
    public fb: FormBuilder,
    private notificationAPI: NotificationService,
    private loanPenaltyAPI: ManualLoanPenaltiesService,
    public dialogRef: MatDialogRef<LoanPenaltiesLookupComponent>
  ) { }
  formData = this.fb.group({
    acid: ['']
  });

  ngOnDestroy(): void {
    this.destroy$.next(true);
    this.destroy$.complete();
  }
  ngOnInit() {
    this.getUnverified();
  }

  readPenaltiesList(res){
    if (res.statusCode === 302) {
      this.loading = false;
      this.data = res.entity;
      console.log("RES", this.data);
      this.dataSource = new MatTableDataSource(this.data);
      this.dataSource.paginator = this.paginator;
      this.dataSource.sort = this.sort;
    } else {
      this.loading = false;
      this.notificationAPI.alertWarning("No Loan Penalies for Acount " + this.formData.controls.acid.value);
    }
  }

  getUnverified(){
    this.loanPenaltyAPI
    .findUnverified().pipe(takeUntil(this.destroy$)).subscribe(
      {
        next: (
          (res) => {
            this.readPenaltiesList(res);
          }
        ), error: (
          (err) => {
            this.loading = false;
            this.notificationAPI.alertWarning("Server Error: !!");
          }
        ),
        complete: (
          () => {

          }
        )
      }
    ), Subscription;
  }

  getAccounts() {
    this.loading = true;
    if (this.formData.valid) {
      let acid = this.formData.controls.acid.value;
      this.loanPenaltyAPI
        .findByCode(acid).pipe(takeUntil(this.destroy$)).subscribe(
          {
            next: (
              (res) => {
                this.readPenaltiesList(res);
              }
            ), error: (
              (err) => {
                this.loading = false;
                this.notificationAPI.alertWarning("Server Error: !!");
              }
            ),
            complete: (
              () => {

              }
            )
          }
        ), Subscription;
    } else if (!this.formData.valid) {
      this.loading = false;
      this.notificationAPI.alertWarning("Account Lookup Form Data invalid: !!");
    }
  }
  onSelect(data: any) {
    this.dialogRef.close({ event: 'close', data: data });
  }
  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();
    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }
}
