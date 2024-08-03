import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';
import { UniversalMembershipLookUpComponent } from '../../../MembershipComponent/universal-membership-look-up/universal-membership-look-up.component';
import { EmployeeConfigService } from './employee-config.service';

@Component({
  selector: 'app-employee-config',
  templateUrl: './employee-config.component.html',
  styleUrls: ['./employee-config.component.scss']
})
export class EmployeeConfigComponent implements OnInit, OnDestroy {
  loading = false;
  function_type: string;
  branchCode: any;
  error: any;
  results: any;
  fmData: any;
  submitted = false;
  onShowResults = false;
  hideBtn = false;
  btnColor: any;
  btnText: any;
  showWarning: boolean = true;
  onShowSearchIcon: boolean = true;

  destroy$: Subject<boolean> = new Subject<boolean>();
  customer_lookup: any;
  constructor(
    public fb: FormBuilder,
    private router: Router,
    private dialog: MatDialog,
    private employeeAPI: EmployeeConfigService,
    private notificationAPI: NotificationService
  ) {
    this.fmData = this.router.getCurrentNavigation().extras.queryParams.formData;
    this.function_type = this.fmData.function_type;
  }
  ngOnInit() {
    this.getPage();
  }
  ngOnDestroy() {
    this.destroy$.next(true);
    this.destroy$.complete();
  }
  formData = this.fb.group({
    address: [''],
    customerCode: [''],
    customerName: [''],
    defaultSalariesPaymentAccount: [''],
    email: [''],
    employerCode: ['', Validators.required],
    id: [''],
    name: ['', Validators.required],
    organizationType: [''],
    phone: [''],
    town: [''],
    website: ['']
  });
  disabledFormControll() {
    this.formData.disable();
    this.showWarning = false;
  }
  get f() {
    return this.formData.controls;
  }
  customerLookup(): void {
    const dialogRef = this.dialog.open(UniversalMembershipLookUpComponent, {
      width: '50%',
      autoFocus: false,
      maxHeight: '90vh'
    });
    dialogRef.afterClosed().pipe(takeUntil(this.destroy$)).subscribe(result => {
      this.customer_lookup = result.data;
      this.formData.controls.customerCode.setValue(this.customer_lookup.customerCode);
      this.formData.controls.customerName.setValue(this.customer_lookup.customerName);
    });
  }

  getData() {
    this.loading = true;
    this.employeeAPI.findBYCode(this.fmData.employerCode).pipe(takeUntil(this.destroy$)).subscribe(
      {
        next: (res) => {
          if (res.statusCode === 302) {
            this.loading = false;
            this.results = res.entity;
            this.formData = this.fb.group({
              address: [this.results.address],
              customerCode: [this.results.customerCode],
              customerName: [this.results.customerName],
              defaultSalariesPaymentAccount: [this.results.defaultSalariesPaymentAccount],
              email: [this.results.email],
              employerCode: [this.results.employerCode],
              id: [this.results.id],
              name: [this.results.name],
              organizationType: [this.results.organizationType],
              phone: [this.results.phone],
              town: [this.results.town],
              website: [this.results.website]
            });
          } else {
            this.loading = false;
            this.notificationAPI.alertWarning("Employee Not Found!");
            this.router.navigate([`/system/configurations-employee/maintenance`], { skipLocationChange: true });
          }
        },
        error: (err) => {
          this.loading = false;
          this.notificationAPI.alertWarning("Server Error: !!");
        },
        complete: () => {

        }
      }
    )
  }
  getPage() {
    if (this.function_type == "ADD") {
      this.formData = this.fb.group({
        address: [''],
        customerCode: [''],
        customerName: [''],
        defaultSalariesPaymentAccount: [''],
        email: [''],
        employerCode: [this.fmData.employerCode],
        id: [''],
        name: ['', Validators.required],
        organizationType: [''],
        phone: [''],
        town: [''],
        website: ['']
      });
      this.btnColor = 'primary';
      this.btnText = 'SUBMIT';
    }
    else if (this.function_type == "INQUIRE") {
      this.getData();
      this.onShowResults = true;
      this.showWarning = false;
      this.onShowSearchIcon = false;
      this.disabledFormControll();
    }
    else if (this.function_type == "MODIFY") {
      this.getData();
      this.onShowResults = true;
      this.btnColor = 'primary';
      this.btnText = 'MODIFY';
    }
    else if (this.function_type == "VERIFY") {
      this.getData();
      this.disabledFormControll();
      this.onShowResults = true;
      this.showWarning = false;
      this.onShowSearchIcon = false;
      this.btnColor = 'primary';
      this.btnText = 'VERIFY';
    }
    else if (this.function_type == "DELETE") {
      this.getData();
      this.disabledFormControll();
      this.onShowResults = true;
      this.showWarning = false;
      this.onShowSearchIcon = false;
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
        this.employeeAPI.create(this.formData.value).pipe(takeUntil(this.destroy$)).subscribe(
          (data) => {
            if (data.statusCode === 201) {
              this.loading = false;
              this.notificationAPI.alertSuccess(data.message);
              this.router.navigate([`/system/configurations-employee/maintenance`], { skipLocationChange: true });
            } else {
              this.loading = false;
              this.hideBtn = false;
              this.notificationAPI.alertWarning(data.message);
            }
          }, (err) => {
            this.loading = false;
            this.hideBtn = false;
            this.notificationAPI.alertWarning("Server error: !!");
          }
        );
      } else {
        this.loading = false;
        this.hideBtn = false;
        this.notificationAPI.alertWarning("Employer form data is invalid");
      }
    }
    else if (this.function_type == "MODIFY") {
      if (this.formData.valid) {
        this.employeeAPI.modify(this.formData.value).pipe(takeUntil(this.destroy$)).subscribe(res => {
          if (res.statusCode == 200) {
            this.loading = false;
            this.results = res;
            this.notificationAPI.alertSuccess(this.results.message);
            this.router.navigate([`/system/configurations-employee/maintenance`], { skipLocationChange: true });
          } else {
            this.loading = false;
            this.hideBtn = false;
            this.results = res;
            this.getData();
            this.notificationAPI.alertWarning(this.results.message);
          }
        }, err => {
          this.loading = false;
          this.hideBtn = false;
          this.getData();
          this.notificationAPI.alertWarning("Server error: !!");
        })
      } else {
        this.loading = false;
        this.hideBtn = false;
        this.getData();
        this.notificationAPI.alertWarning("Employer form data is invalid");
      }
    }
    else if (this.function_type == "VERIFY") {
      this.employeeAPI.verify(this.results.id).pipe(takeUntil(this.destroy$)).subscribe(res => {
        console.log(res);
        if (res.statusCode == 200) {
          this.results = res;
          this.loading = false;
          this.notificationAPI.alertSuccess(this.results.message);
          this.router.navigate([`/system/configurations-employee/maintenance`], { skipLocationChange: true });

        } else {
          this.results = res;
          this.loading = false;
          this.hideBtn = false;
          this.getData();
          this.notificationAPI.alertWarning(this.results.message);
        }
      }, err => {
        this.loading = false;
        this.hideBtn = false;
        this.getData();
        this.notificationAPI.alertWarning("Server error: !!");
      })
    }
    else if (this.function_type == "DELETE") {
      this.employeeAPI.delete(this.results.id).pipe(takeUntil(this.destroy$)).subscribe(res => {
        if (res.statusCode == 200) {
          this.results = res;
          this.loading = false;
          this.notificationAPI.alertSuccess(this.results.message);
          this.router.navigate([`/system/configurations-employee/maintenance`], { skipLocationChange: true });
        } else {
          this.results = res;
          this.loading = false;
          this.hideBtn = false;
          this.getData();
          this.notificationAPI.alertWarning(this.results.message);
        }
      }, err => {
        this.loading = false;
        this.hideBtn = false;
        this.getData();
        this.notificationAPI.alertWarning("Server error: !!");
      })
    }
  }
}
