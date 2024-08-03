import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';
import { SavingsProductLookupComponent } from '../../../ProductModule/savings/savings-product-lookup/savings-product-lookup.component';
import { MembershipConfigService } from './membership-config.service';

@Component({
  selector: 'app-membership-config',
  templateUrl: './membership-config.component.html',
  styleUrls: ['./membership-config.component.scss']
})
export class MembershipConfigComponent implements OnInit, OnDestroy {
  customer_db_array: any =
    [
      {
        value: "retailcustomer",
        name: "INDIVIDUAL"
      },
      {
        value: "group_member",
        name: "GROUP OR ENTERPRISE"
      },
      {
        value: "office",
        name: "OFFICE"
      }
    ]



  loading = false;
  function_type: any;
  results: any;
  fmData: any;
  code_structure = "TYPERUNNO";
  submitted = false;
  onShowResults = false;
  hideBtn = false;
  btnColor: any;
  btnText: any;
  showIsMinor: boolean = false;
  onShowWarning: boolean = true;
  destroy$: Subject<boolean> = new Subject<boolean>();
  lookupdata: any;
  randomCode: string;
  onShowSearch: boolean = true;
  constructor(
    public fb: FormBuilder,
    private router: Router,
    private dialog: MatDialog,
    private notificationAPI: NotificationService,
    private memberConfigAPI: MembershipConfigService
  ) {
    this.fmData = this.router.getCurrentNavigation().extras.queryParams.formData;
    this.function_type = this.fmData.function_type;
  }
  ngOnInit() {
    this.randomCode = "CDE" + Math.floor(Math.random() * (999));
    this.getPage();
  }
  ngOnDestroy(): void {
    this.destroy$.next(true);
    this.destroy$.complete();
  }
  formData = this.fb.group({
    id: [''],
    contributionProduct: ['', Validators.required],
    customerType: ['', Validators.required],
    dbTable: ['', Validators.required],
    isJunior: ['N'],
    minimumMonthlyContribution: ['', Validators.required],
    registrationFeeAmount: ['', Validators.required],
    runningSize: ['4'],
    codeStructure: [''],
    description: ['', Validators.required]
  });
  get f() { return this.formData.controls; }
  disabledFormControll() {
    this.formData.disable();
    this.onShowWarning = false;
  }
  onSelect(event: any) {
    if (event.target.value == 'retailcustomer') {
      this.showIsMinor = true;
    } else if (event.target.value !== 'retailcustomer') {
      this.showIsMinor = false;
    }else if (event.target.value === 'group_member' || 'office') {
      this.showIsMinor = false;
    }

  }
  schemeCodeLookup(): void {
    const dialogRef = this.dialog.open(SavingsProductLookupComponent, {
      width: '40%',
      autoFocus: false,
      maxHeight: '90vh'
    });
    dialogRef.afterClosed().subscribe(result => {
      this.lookupdata = result.data;
      this.formData.controls.contributionProduct.setValue(this.lookupdata.productCode);
    });
  }
  getData() {
    this.loading = true;
    this.memberConfigAPI.findByTypeCode(this.fmData.customerType).pipe(takeUntil(this.destroy$)).subscribe(
      {
        next: (res) => {
          if (res.statusCode === 302) {
            this.results = res.entity;
            this.loading = false;
            if (this.results.dbTable == 'retailcustomer') {
              this.showIsMinor = true;
            }
            else if (this.results.dbTable !== 'retailcustomer') {
              this.showIsMinor = false;
            }
            this.formData = this.fb.group({
              id: [this.results.id],
              contributionProduct: [this.results.contributionProduct],
              codeStructure: [this.results.codeStructure],
              customerType: [this.results.customerType],
              isJunior: [this.results.isJunior],
              runningSize: [this.results.runningSize],
              minimumMonthlyContribution: [this.results.minimumMonthlyContribution],
              registrationFeeAmount: [this.results.registrationFeeAmount],
              dbTable: [this.results.dbTable],
              description: [this.results.description]
            });

          } else {
            this.loading = false;
            this.notificationAPI.alertWarning("Code Records Not Found!!");
            this.router.navigate([`system/configurations-member/maintenance`], { skipLocationChange: true });
          }
        },
        error: (err) => {
          this.loading = false;
          this.notificationAPI.alertWarning("Server Error: !!");
          this.router.navigate([`system/configurations-member/maintenance`], { skipLocationChange: true });
        },
        complete: () => {

        }
      }
    )

  }
  getPage() {
    this.loading = true;
    if (this.function_type == "ADD") {
      this.loading = false;
      this.formData = this.fb.group({
        id: [''],
        contributionProduct: ['', Validators.required],
        customerType: [this.fmData.customerType],
        dbTable: ['', Validators.required],
        isJunior: ['N'],
        minimumMonthlyContribution: ['', Validators.required],
        registrationFeeAmount: ['', Validators.required],
        runningSize: ['4'],
        codeStructure: [this.randomCode],
        description: ['', Validators.required]
      });
      this.btnColor = 'primary';
      this.btnText = 'SUBMIT';
    }
    else if (this.function_type == "INQUIRE") {
      this.loading = false;
      this.getData();
      this.onShowResults = true;
      this.showIsMinor = true;
      this.disabledFormControll();
    }
    else if (this.function_type == "MODIFY") {
      this.loading = false;
      this.getData();
      this.onShowResults = true;
      this.btnColor = 'primary';
      this.btnText = 'MODIFY';
    }
    else if (this.function_type == "VERIFY") {
      this.loading = false;
      this.getData();
      this.onShowResults = true;
      this.disabledFormControll();
      this.btnColor = 'primary';
      this.btnText = 'VERIFY';
    }
    else if (this.function_type == "DELETE") {
      this.loading = false;
      this.getData();
      this.onShowResults = true;
      this.disabledFormControll();
      this.btnColor = 'accent';
      this.btnText = 'DELETE';
    }
  }
  onSubmit() {
    this.loading = true;
    this.submitted = true;
    this.hideBtn = true;
    if (this.function_type == "ADD") {
      if (this.formData.valid) {
        this.memberConfigAPI.create(this.formData.value).pipe(takeUntil(this.destroy$)).subscribe(res => {

          console.log("resresres:: ", res)

          if (res.statusCode == 201) {
            this.loading = false;
            this.notificationAPI.alertSuccess(res.message);
            this.router.navigate([`system/configurations-member/maintenance`], { skipLocationChange: true });
          } else {
            this.loading = false;
            this.hideBtn = false;
            this.notificationAPI.alertWarning(res.message);
          }
          this.loading = false;
        }, err => {
          this.loading = false;
          this.hideBtn = false;
          this.notificationAPI.alertWarning("Server Error: !!");
        })

      } else {
        this.loading = false;
        this.hideBtn = false;
        this.notificationAPI.alertWarning("Code config form data is invalid");
      }
    }
    else if (this.function_type == "MODIFY") {
      if (this.formData.valid) {
        this.memberConfigAPI.modify(this.formData.value).pipe(takeUntil(this.destroy$)).subscribe(res => {
          if (res.statusCode == 200) {
            this.loading = false;
            this.notificationAPI.alertSuccess(res.message);
            this.router.navigate([`system/configurations-member/maintenance`], { skipLocationChange: true });
          } else {
            this.loading = false;
            this.hideBtn = false;
            this.notificationAPI.alertWarning(res.message);
          }
        }, err => {
          this.loading = false;
          this.hideBtn = false;
          this.notificationAPI.alertWarning("Server Error: !!");
        })
      } else {
        this.loading = false;
        this.hideBtn = false;
        this.notificationAPI.alertWarning("Code config form data is invalid");
      }
    }
    else if (this.function_type == "VERIFY") {
      this.memberConfigAPI.verify(this.results.id).pipe(takeUntil(this.destroy$)).subscribe(res => {
        if (res.statusCode == 200) {
          this.loading = false;
          this.notificationAPI.alertSuccess(res.message);
          this.router.navigate([`system/configurations-member/maintenance`], { skipLocationChange: true });
        } else {
          this.loading = false;
          this.hideBtn = false;
          this.notificationAPI.alertWarning(res.message);
        }
      }, err => {
        this.loading = false;
        this.hideBtn = false;
        this.notificationAPI.alertWarning("Server Error: !!");
      })
    }

    else if (this.function_type == "DELETE") {
      this.memberConfigAPI.delete(this.results.id).pipe(takeUntil(this.destroy$)).subscribe(res => {
        if (res.statusCode == 200) {
          this.loading = false;
          this.router.navigate([`system/configurations-member/maintenance`], { skipLocationChange: true });
          this.notificationAPI.alertSuccess(res.message);
        } else {
          this.hideBtn = false;
          this.loading = false;
          this.notificationAPI.alertWarning(res.message);
        }
      }, err => {
        this.loading = false;
        this.hideBtn = false;
        this.notificationAPI.alertWarning("Server Error: !!");
      })
    }

  }
}
