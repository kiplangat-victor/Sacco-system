import { HttpParams } from '@angular/common/http';
import { Component, OnInit, ViewChild } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { Router } from '@angular/router';
import { Subject, Subscription } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';
import { AccountsService } from 'src/app/administration/Service/AccountsService/accounts/accounts.service';
import { GroupMembershipService } from 'src/app/administration/Service/GroupMembership/group-membership.service';
import { MembershipService } from 'src/app/administration/Service/Membership/membership.service';
import { TransactionExecutionService } from '../../transaction-execution/transaction-execution.service';
import { DatePipe } from '@angular/common';

@Component({
  selector: 'app-notification-allerts',
  templateUrl: './notification-allerts.component.html',
  styleUrls: ['./notification-allerts.component.scss']
})
export class NotificationAllertsComponent implements OnInit {
  membershipColumns: string[] = ['index', 'customerUniqueId', 'customerName', 'postedOn', 'verifiedFlag', 'verify'];
  nonLoanccountsColumns: string[] = [
    'index',
    'customer_code',
    'acid',
    'account_name',
    'account_type',
    'product_code',
    'product_name',
    'account_status',
    'verified_flag',
    'verify'
  ];
  loanAccountsColumns: string[] = [
    'index',
    'customer_code',
    'acid',
    'account_name',
    'account_type',
    'product_code',
    'product_name',
    'account_status',
    'verification',
    'verify'
  ];
  accountsColumns: string[] = [
    'index',
    'customer_code',
    'acid',
    'account_name',
    'account_type',
    'product_code',
    'product_name',
    'account_status',
    'verified_flag',
    'verify'
  ];
  transactionsColumns: string[] = [
    'index',
    'transactionCode',
    'transactionType',
    // 'totalAmount',
    'verifiedFlag',
    'verifiedFlag_2',
    'verifiedBy_2',
    'verify'
  ];
  salariesColumns: string[] = [
    'index',
    'salaryUploadCode',
    'transactionType',
    'enteredBy',
    'enteredTime',
    'verifiedFlag',
    'verify'
  ];
  membershipDataSource: MatTableDataSource<any>;
  @ViewChild("membershipPaginator") membershipPaginator!: MatPaginator;
  @ViewChild(MatSort) membershipSort!: MatSort;

  nonLoanAccountsDataSource: MatTableDataSource<any>;
  @ViewChild("nonLonAccountsPaginator") nonLonAccountsPaginator!: MatPaginator;
  @ViewChild(MatSort) nonLoanAccountsSort!: MatSort;

  loanAccountsDataSource: MatTableDataSource<any>;
  @ViewChild("loanAccountsPaginator") loanAccountsPaginator!: MatPaginator;
  @ViewChild(MatSort) loanAccountsSort!: MatSort;

  accountsDataSource: MatTableDataSource<any>;
  @ViewChild("accountsPaginator") accountsPaginator!: MatPaginator;
  @ViewChild(MatSort) accountsSort!: MatSort;

  transactionsDataSource: MatTableDataSource<any>;
  @ViewChild("transactionsPaginator") transactionsPaginator!: MatPaginator;
  @ViewChild(MatSort) transactionsSort!: MatSort;

  salariesDataSource: MatTableDataSource<any>;
  @ViewChild("salariesPaginator") salariesPaginator!: MatPaginator;
  @ViewChild(MatSort) salariesSort!: MatSort;

  loadingMemberData: boolean = false;
  loadingNewAccountsData: boolean = false;
  loadingLoanAccountsData: boolean = false;
  loadingTransactionsData: boolean = false;
  totalAccounts = 0;
  totalTransactions = 0.0;
  data: any;
  totalRetailMembers = 0;
  totalGroupMembers = 0;
  totalMembership = 0;
  params: HttpParams;
  results: any;
  submitted: boolean = false;
  destroy$: Subject<boolean> = new Subject<boolean>();
  transData: any;
  accountsData: any;
  fromDate: any = new Date(Date.now() - 1000 * 360 * (3600 * 1000 * 24));
  toDate: any = new Date(Date.now() + (3600 * 1000 * 24));
  nonLoanAccountsData: any;
  loanAccountsData: any;
  constructor(
    public fb: FormBuilder,
    private router: Router,
    private datepipe: DatePipe,
    private accountsAPI: AccountsService,
    private membershipAPI: MembershipService,
    private notificationAPI: NotificationService,
    private transactionAPI: TransactionExecutionService
  ) { }
  ngOnDestroy(): void {
    this.destroy$.next(true);
    this.destroy$.complete();
  }
  ngOnInit(): void {
    this.getUnVerifiedMembers();
    this.getUnVerifiedLoanAccounts();
    this.getUnVerifiedNonLoanAccounts();
    this.getTransactionApprovals();
  }
  getUnVerifiedMembers() {
    this.loadingMemberData = true;
    this.params = new HttpParams()
      .set('fromDate', this.datepipe.transform(this.fromDate, 'yyyy-MM-dd'))
      .set('toDate', this.datepipe.transform(this.toDate, 'yyyy-MM-dd'));
    this.membershipAPI.readAllUnverified(this.params).pipe(takeUntil(this.destroy$)).subscribe(
      {
        next: (
          (res) => {
            if (res.statusCode === 302) {
              this.results = res.entity;
              this.membershipDataSource = new MatTableDataSource(this.results);
              this.membershipDataSource.paginator = this.membershipPaginator;
              this.membershipDataSource.sort = this.membershipSort;
              this.loadingMemberData = false;
            } else {
              this.loadingMemberData = false;
            }
          }
        ),
        error: (
          (err) => {
            this.loadingMemberData = false;
            this.notificationAPI.alertWarning("No new member to verify !!");
          }
        ),
        complete: (
          () => {

          }
        )
      }
    ), Subscription;
  }

  membershipFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.membershipDataSource.filter = filterValue.trim().toLowerCase();
    if (this.membershipDataSource.paginator) {
      this.membershipDataSource.paginator.firstPage();
    }
  }

  verifyMember(row: any) {
    if(row.identity == 'group'){
      this.router.navigate([`/system/group-membership/data/view`],
       { skipLocationChange: true,
         queryParams: { formData: {
          customerCode: row.customerCode,
          function_type: ['VERIFY'],
          id: row.id,
          uniqueId: row.uniqueId,
          backBtn: ["APPROVAL"]
        } } });
    }else{
      this.router.navigate([`/system/membership/data/view`], {
        skipLocationChange: true, queryParams: {
          formData: {
            customerCode: row.customerCode,
            function_type: ['VERIFY'],
            id: row.id,
            uniqueId: row.uniqueId,
            backBtn: ["APPROVAL"]
          }
        }
      });
    }
  }
  getUnVerifiedNonLoanAccounts() {
    this.loadingNewAccountsData = true;
    this.accountsAPI.getUnVerifiedNonLoanAccounts().pipe(takeUntil(this.destroy$)).subscribe(
      {
        next: (
          (res) => {
            if (res.statusCode === 302) {
              this.nonLoanAccountsData = res.entity;
              this.loadingNewAccountsData = false;
              this.nonLoanAccountsDataSource = new MatTableDataSource(this.nonLoanAccountsData);
              this.nonLoanAccountsDataSource.paginator = this.nonLonAccountsPaginator;
              this.nonLoanAccountsDataSource.sort = this.nonLoanAccountsSort;
            } else {
              this.loadingNewAccountsData = false;
            }
          }
        ),
        error: (
          (err) => {
            this.loadingNewAccountsData = false;
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
  nonLoanccountsFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.nonLoanAccountsDataSource.filter = filterValue.trim().toLowerCase();
    if (this.nonLoanAccountsDataSource.paginator) {
      this.nonLoanAccountsDataSource.paginator.firstPage();
    }
  }
  verifyNonLoanAccount(row: any) {
    let send = {
      function_type: "VERIFY",
      account_code: row.acid,
      account_type: row.account_type,
      backBtn: ["APPROVAL"]
    }
    console.log(send);
    if (row.account_type == "SBA") {
      this.router.navigate([`/system/savings-account/data/view`], { skipLocationChange: false, queryParams: { formData: send } });
    }
    if (row.account_type == "OAB") {
      this.router.navigate([`/system/office-account/data/view`], { skipLocationChange: false, queryParams: { formData: send } });
    }
    if (row.account_type == "TDA") {
      this.router.navigate([`/system/term-deposit/account/data-view`], { skipLocationChange: false, queryParams: { formData: send } });
    }
  }
  getUnVerifiedLoanAccounts() {
    this.loadingLoanAccountsData = true;
    this.params = new HttpParams()
      .set('accountType', "LAA")
    this.accountsAPI.getUnVerifiedLoans().pipe(takeUntil(this.destroy$)).subscribe(
      {
        next: (
          (res) => {
            if (res.statusCode === 302) {
              this.loadingLoanAccountsData = false;
              this.loanAccountsData = res.entity;
              this.loanAccountsDataSource = new MatTableDataSource(this.loanAccountsData);
              this.loanAccountsDataSource.paginator = this.loanAccountsPaginator;
              this.loanAccountsDataSource.sort = this.loanAccountsSort;
            } else {
              this.loadingLoanAccountsData = false;
            }
          }
        ),
        error: (
          (err) => {
            this.loadingLoanAccountsData = false;
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
  loanccountsFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.loanAccountsDataSource.filter = filterValue.trim().toLowerCase();
    if (this.loanAccountsDataSource.paginator) {
      this.loanAccountsDataSource.paginator.firstPage();
    }
  }
  verifyLoanAccount(row: any) {
    if(row.verification == 'Disbursement'){
      this.router.navigate([`system/loan-account/data/view`],
       { skipLocationChange: true,
         queryParams: { formData:  {
          function_type: "VERIFICATION",
          account_code: row.acid,
          account_type: row.account_type,
          backBtn: ["APPROVAL"]
        }, fetchedData: this.data} });
    }else if(row.verification == 'Application') {
      this.router.navigate([`/system/loan-account/data/view`], {
        skipLocationChange: true, queryParams: {
          formData: {
            function_type: "VERIFY",
            account_code: row.acid,
            account_type: row.account_type,
            backBtn: ["APPROVAL"]
          }
        }
      });
    }
  }
  getTransactionApprovals() {
    this.loadingTransactionsData = true;
    this.transactionAPI.getApprovalList().pipe(takeUntil(this.destroy$)).subscribe(
      {
        next: (
          (res) => {
            if (res.statusCode === 302) {
              this.transData = res.entity;
              this.loadingTransactionsData = false;
              this.transactionsDataSource = new MatTableDataSource(this.transData);
              this.transactionsDataSource.paginator = this.transactionsPaginator;
              this.transactionsDataSource.sort = this.transactionsSort;
            } else {
              this.loadingTransactionsData = false;
            }
          }
        ),
        error: (
          (err) => {
            this.loadingTransactionsData = false;
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
  verifyTransaction(tranCode: any, type: any) {
    this.router.navigate([`/system/transactions/normal/data/view`], {
      skipLocationChange: true, queryParams: {
        formData: {
          function_type: "VERIFY",
          transactionCode: tranCode,
          transactionType: type,
          backBtn: ["APPROVAL"]
        }
      }
    });
  }
  transactionsFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.transactionsDataSource.filter = filterValue.trim().toLowerCase();
    if (this.transactionsDataSource.paginator) {
      this.transactionsDataSource.paginator.firstPage();
    }
  }
  reloadPage() {
    this.getTransactionApprovals();
}
}
