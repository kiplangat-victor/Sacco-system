import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';
import { GroupMembershipDetailsComponent } from '../../../MembershipComponent/GroupMembership/group-membership-details/group-membership-details.component';
// import { MemberDetailsComponent } from '../../../MembershipComponent/Membership/member-details/member-details.component';
import { UniversalMembershipLookUpComponent } from '../../../MembershipComponent/universal-membership-look-up/universal-membership-look-up.component';
import { SavingsInstructionsService } from './savings-instructions.service';

@Component({
  selector: 'app-savings-contribution',
  templateUrl: './savings-contribution.component.html',
  styleUrls: ['./savings-contribution.component.scss']
})
export class SavingsContributionComponent implements OnInit, OnDestroy {
  executionTypeArray = [
    {
      value: "SHARES_ONLY",
      name: 'SHARES ONLY'
    },
    {
      value: "DEPOSITS_ONLY",
      name: 'DEPOSITS ONLY'
    },
    {
      value: "BOTH",
      name: 'SHARES AND DEPOSITS'
    },
  ];
  frequencyArray: any[] = ["DAYS", "MONTHS", "YEARS"];
  loading = false;
  function_type: any;
  savingCode: any;
  error: any;
  results: any;
  fmData: any;
  submitted = false;
  onShowResults = false;
  hideBtn = false;
  btnColor: any;
  btnText: any;
  onHideStatus: boolean = false;
  showSearch: boolean = true;
  submitting: boolean = false;
  showWarning: boolean = true;
  customer_lookup: any;
  customer_account_id: any;
  customer_account_code: any;
  onMemberDetailsLookup: boolean = false;
  onShowEndDate: boolean = true;
  onShowApplicationDate: boolean = false;
  onShowNextCollectionDate: boolean = true;
  destroy$: Subject<boolean> = new Subject<boolean>();
  constructor(
    public fb: FormBuilder,
    private router: Router,
    private dialog: MatDialog,
    private notificationAPI: NotificationService,
    private savingsContPI: SavingsInstructionsService,
  ) {
    this.fmData = this.router.getCurrentNavigation().extras.queryParams.formData;
    this.function_type = this.fmData.function_type;
    this.savingCode = this.fmData.savingCode;
  }
  ngOnInit() {
    this.getPage();
  }
  ngOnDestroy() {
    this.destroy$.next(true);
    this.destroy$.complete();
  }

  formData = this.fb.group({
    amount: ["", Validators.required],
    applicationDate: [new Date()],
    customerCode: ["", Validators.required],
    description: ["Saving processing instruction", Validators.required],
    endDate: [new Date()],
    executionType: ["BOTH", Validators.required],
    frequency: ["", Validators.required],
    id: [""],
    lastCollectionDate: ['2040-01-01'],
    nextCollectionDate: [new Date()],
    savingCode: [""],
    status: ["ACTIVE"],
  });
  disabledFormControll() {
    this.formData.disable();
    this.showSearch = false;
    this.showWarning = false;
    this.onHideStatus = true;
    this.onShowEndDate = false;
    this.onShowNextCollectionDate = false;
  }
  get f() {
    return this.formData.controls;
  }
  customerLookup(): void {
    const dialogRef = this.dialog.open(UniversalMembershipLookUpComponent, {
      width: '60%',
      autoFocus: false,
      maxHeight: '90vh'
    });
    dialogRef.afterClosed().subscribe(result => {
      this.customer_lookup = result.data;
      this.customer_account_id = this.customer_lookup.id;
      this.customer_account_code = this.customer_lookup.customerCode;
      this.formData.controls.customerCode.setValue(this.customer_account_code);
      if (this.customer_account_code === null || this.customer_account_code === undefined || this.customer_account_code === '-') {
        this.onMemberDetailsLookup = false;
      } else {
        this.onMemberDetailsLookup = true;
      }
    });
  }
  // memberDetailsLookup(data: number) {
  //   if (this.customer_lookup.customerType == '12') {
  //     this.dialog.open(GroupMembershipDetailsComponent, {
  //       maxWidth: '100vw',
  //       maxHeight: '100vh',
  //       height: '100%',
  //       width: '100%',
  //       panelClass: 'full-screen-modal',
  //       data: this.customer_account_id
  //     });
  //   } else if (this.customer_lookup.customerType !== '12') {
  //     this.dialog.open(MemberDetailsComponent, {
  //       maxWidth: '100vw',
  //       maxHeight: '100vh',
  //       height: '100%',
  //       width: '100%',
  //       panelClass: 'full-screen-modal',
  //       data: this.customer_account_id
  //     });
  //   }
  // }
  getData() {
    this.loading = true;
    this.savingsContPI.savingsCode(this.fmData.savingCode).pipe(takeUntil(this.destroy$)).subscribe(
      {
        next: (res) => {
          if (res.statusCode === 302) {
            this.loading = false;
            this.results = res.entity;
            this.formData = this.fb.group({
              amount: [this.results.amount],
              applicationDate: [this.results.applicationDate],
              customerCode: [this.results.customerCode],
              description: [this.results.description],
              endDate: [this.results.endDate],
              executionType: [this.results.executionType],
              frequency: [this.results.frequency],
              id: [this.results.id],
              lastCollectionDate: [this.results.lastCollectionDate],
              nextCollectionDate: [this.results.nextCollectionDate],
              savingCode: [this.results.savingCode],
              status: [this.results.status],
            });
          } else {
            this.loading = false;
            this.notificationAPI.alertWarning("Savings Instruction Contribution with Code " + this.fmData.savingCode + " Not Found: !!");
            this.router.navigate([`/system/savings-instruction-contribution/maintenance`], { skipLocationChange: true });
          }
        },
        error: (err) => {
          this.loading = false;
          this.notificationAPI.alertWarning("Server  Error: !!");
          this.router.navigate([`/system/savings-instruction-contribution/maintenance`], { skipLocationChange: true });
        },
        complete: () => {

        }
      }
    )
  }

  getPage() {
    if (this.function_type == "ADD") {
      this.formData = this.fb.group({
        amount: ["", Validators.required],
        applicationDate: [new Date()],
        customerCode: ["", Validators.required],
        description: ["Saving processing instruction", Validators.required],
        endDate: [new Date()],
        executionType: ["BOTH", Validators.required],
        frequency: ["", Validators.required],
        id: [""],
        lastCollectionDate: ['2040-01-01'],
        nextCollectionDate: [new Date()],
        savingCode: [""],
        status: ["ACTIVE"],
      });
      this.btnColor = 'primary';
      this.btnText = 'SUBMIT';
    }
    else if (this.function_type == "INQUIRE") {
      this.getData();
      this.onShowResults = true;
      this.disabledFormControll();
    }
    else if (this.function_type == "MODIFY") {
      this.getData();
      this.onShowResults = true;
      this.onHideStatus = true;
      this.btnColor = 'primary';
      this.btnText = 'MODIFY';
    }
    else if (this.function_type == "VERIFY") {
      this.getData();
      this.disabledFormControll();
      this.onShowResults = true;
      this.btnColor = 'primary';
      this.btnText = 'VERIFY';
    }
    else if (this.function_type == "EXECUTE") {
      console.log(this.function_type);
      this.getData();
      this.disabledFormControll();
      this.onShowResults = true;
      this.btnColor = 'primary';
      this.btnText = 'EXECUTE 15 DAYS AHEAD';
      console.log(this.function_type);
    }
    else if (this.function_type == "DELETE") {
      this.getData();
      this.disabledFormControll();
      this.onShowResults = true;
      this.btnColor = 'accent';
      this.btnText = 'DELETE';
    }
  }
  onSubmit() {
    this.loading = true;
    this.submitted = true;
    if (this.function_type == "ADD") {
      if (this.formData.valid) {
        this.savingsContPI.create(this.formData.value).pipe(takeUntil(this.destroy$)).subscribe(
          (data) => {
            if (data.statusCode === 201) {
              this.loading = false;
              this.notificationAPI.alertSuccess(data.message);
              this.router.navigate([`/system/savings-instruction-contribution/maintenance`], { skipLocationChange: true });
            } else {
              this.loading = false;
              this.submitting = false;
              this.notificationAPI.alertWarning(data.message);
            }
          }, (err) => {
            this.loading = false;
            this.submitting = false;
            this.notificationAPI.alertWarning("Server Error: !!");
          }
        );
      } else {
        this.loading = false;
        this.submitting = false;
        this.notificationAPI.alertWarning("Saving Contribution Form is invalid");
      }
    }
    else if (this.function_type == "MODIFY") {
      if (this.formData.valid) {
        this.savingsContPI.modify(this.formData.value).pipe(takeUntil(this.destroy$)).subscribe(res => {
          if (res.statusCode == 200) {
            this.loading = false;
            this.results = res;
            this.notificationAPI.alertSuccess(this.results.message);
            this.router.navigate([`/system/savings-instruction-contribution/maintenance`], { skipLocationChange: true });
          } else {
            this.loading = false;
            this.submitting = false;
            this.results = res;
            this.getData();
            this.notificationAPI.alertWarning(this.results.message);
          }
        }, err => {
          this.loading = false;
          this.submitting = false;
          this.getData();
          this.notificationAPI.alertWarning("Server Error: !!");
        })
      } else {
        this.loading = false;
        this.submitting = false;
        this.getData();
        this.notificationAPI.alertWarning("Saving Contribution Form is invalid");
      }
    }
    else if (this.function_type == "VERIFY") {
      console.log(this.function_type);
      this.savingsContPI.verify(this.results.id).pipe(takeUntil(this.destroy$)).subscribe(res => {
        if (res.statusCode == 200) {
          this.results = res;
          this.loading = false;
          this.notificationAPI.alertSuccess(this.results.message);
          this.router.navigate([`/system/savings-instruction-contribution/maintenance`], { skipLocationChange: true });
        } else {
          this.results = res;
          this.loading = false;
          this.submitting = false;
          this.getData();
          this.notificationAPI.alertWarning(this.results.message);
        }
      }, err => {
        this.loading = false;
        this.submitting = false;
        this.getData();
        this.notificationAPI.alertWarning("Server Error: !!");
      })
    }
    else if (this.function_type == "EXECUTE") {
      console.log(this.function_type);
      this.savingsContPI.execute(this.results.id).pipe(takeUntil(this.destroy$)).subscribe(res => {
        if (res.statusCode == 200) {
          this.results = res;
          this.loading = false;
          this.notificationAPI.alertSuccess(this.results.message);
          this.router.navigate([`/system/savings-instruction-contribution/maintenance`], { skipLocationChange: true });
        } else {
          this.results = res;
          this.loading = false;
          this.submitting = false;
          this.getData();
          this.notificationAPI.alertWarning(this.results.message);
        }
      }, err => {
        this.loading = false;
        this.submitting = false;
        this.getData();
        this.notificationAPI.alertWarning("Am suffering: !!");
      })
    }
    else if (this.function_type == "DELETE") {
      this.savingsContPI.delete(this.results.id).pipe(takeUntil(this.destroy$)).subscribe(res => {
        if (res.statusCode == 200) {
          this.results = res;
          this.loading = false;
          this.notificationAPI.alertSuccess(this.results.message);
          this.router.navigate([`/system/savings-instruction-contribution/maintenance`], { skipLocationChange: true });
        } else {
          this.results = res;
          this.loading = false;
          this.submitting = false;
          this.getData();
          this.notificationAPI.alertWarning(this.results.message);
        }
      }, err => {
        this.loading = false;
        this.submitting = false;
        this.getData();
        this.notificationAPI.alertWarning("Server Error: !!");
      })
    }
  }
}
