import { Component, OnInit, ViewChild } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  Validators
} from '@angular/forms';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { Router } from '@angular/router';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';
import { InterestcodeserviceService } from './interestcodeservice.service';

@Component({
  selector: 'app-interestcode',
  templateUrl: './interestcode.component.html',
  styleUrls: ['./interestcode.component.scss'],
})
export class InterestcodeComponent implements OnInit {
  displayedColumns: string[] = [
    'index',
    'slabName',
    'lowerLimit',
    'upperLimit',
    'chargeAmount',
    'percentage',
    'usePercentage',
    'actions',
  ];
  dataSource!: MatTableDataSource<any>;
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;
  penalInterestTypeArray: any = [
    {
      value: 'Fixed_amount',
      name: 'FIXED AMOUNT'
    },
    {
      value: 'Percentage',
      name: 'PERCENTAGE'
    }
  ];
  onShowResults: boolean = false;
  loading = false;
  function_type: string;
  interestCode: any;
  error: any;
  results: any;
  fetchData: any;
  fmData: any;
  isDisabled: boolean;
  postedBy: any;
  verifiedFlag: any;
  verifiedBy: any;
  submitted = false;
  onShowFlatRate = false;
  onShowSlabs = false;
  isEnabled = false;
  chargesForm!: FormGroup;
  tChargesArray: any[] = [];
  editButton: boolean = false;
  addButton: boolean = true;
  disableAddButton: boolean = false;
  disableActions: boolean = false;
  index: number;
  slabChargesValid: boolean;
  showChargeField: boolean = true;
  showPercentageField: boolean = false;
  today = new Date();
  prevDate = new Date(new Date().setDate(this.today.getDate() - 1000));
  nextDate = new Date(new Date().setDate(this.today.getDate() + 36600));
  onShowDate = false;
  hideBtn = false;
  btnColor: any;
  btnText: any;
  postedTime: any;
  destroy$: Subject<boolean> = new Subject<boolean>();
  constructor(
    public fb: FormBuilder,
    private router: Router,
    private notificationAPI: NotificationService,
    private interestAPI: InterestcodeserviceService
  ) {
    this.fmData =
      this.router.getCurrentNavigation().extras.queryParams.formData;
    this.fetchData =
      this.router.getCurrentNavigation().extras.queryParams.fetchData;
    this.function_type = this.fmData.function_type;
    this.interestCode = this.fmData.interestCode;
    this.postedBy = this.fetchData.postedBy;
    this.verifiedFlag = this.fetchData.verifiedFlag;
    this.verifiedBy = this.fetchData.verifiedBy;
  }
  ngOnDestroy() {
    this.destroy$.next(true);
    this.destroy$.complete();
  }
  ngOnInit() {
    this.initChargesForm();
    this.getPage();
    this.getSlabCharges();
    if (this.fetchData.interestType == 'TC') {
      this.onShowSlabs = true;
      this.tChargesArray = this.fetchData.interestcodeslabs;
      this.getSlabCharges();
    }
  }
  formData: FormGroup = this.fb.group({
    interestCode: ['', [Validators.required]],
    interestName: ['', [Validators.required]],
    interestType: ['', [Validators.required]],
    interestrate: ['', [Validators.required]],
    parttrantype: ['', [Validators.required]],
    penalInterest: ['', [Validators.required]],
    penalInterestType: ['', Validators.required],
    fromDate: [this.prevDate],
    toDate: [this.nextDate],
    calculationMethod: [''],
    interestPeriod: [''],
    interestcodeslabs: [[]],
    penalIntBasedOn: ['']
  });
  get f() {
    return this.formData.controls;
  }
  onFlatRate(e: any) {
    this.onShowFlatRate = true;
    this.onShowSlabs = false;
  }
  onSlabFormat(e: any) {
    this.onShowSlabs = true;
    this.onShowFlatRate = false;
  }
  onSlabUsePercentage(event: any) {
    if (event.value == 'Y') {
      this.showPercentageField = true;
      this.showChargeField = false;
      this.chargesForm.controls.percentage.setValidators([Validators.required]);
      this.chargesForm.controls.chargeAmount.clearValidators();
      this.chargesForm.controls.chargeAmount.updateValueAndValidity();
    } else if (event.value == 'N') {
      this.showPercentageField = false;
      this.showChargeField = true;
      this.chargesForm.controls.chargeAmount.setValidators([
        Validators.required,
      ]);
      this.chargesForm.controls.percentage.clearValidators();
      this.chargesForm.controls.percentage.updateValueAndValidity();
    }
  }
  disabledFormControll() {
    this.disableActions = true;
    this.disableAddButton = true;
    this.chargesForm.disable();
    this.formData.disable();
  }
  existingData() {
    this.fetchData.interestcodeslabs.forEach((element: any) => {
      this.tChargesArray.push(element);
    });
    this.getSlabCharges();
    if (this.fetchData.interestType == 'SlabFormat') {
      this.onShowFlatRate = false;
      this.onShowSlabs = true;
    } else if (this.fetchData.interestType == 'FlatRate') {
      this.onShowFlatRate = true;
      this.onShowSlabs = false;
    }
  }
  initialiseChargesForm() {
    this.initChargesForm();
    this.showPercentageField = false;
    this.showChargeField = true;
    this.chargesForm.controls.chargeAmount.setValidators([
      Validators.required,
    ]);
  }
  getData() {
    this.formData = this.fb.group({
      interestCode: [this.interestCode.interestCode],
      interestName: [this.fetchData.interestName],
      interestType: [this.fetchData.interestType],
      interestrate: [this.fetchData.interestrate],
      parttrantype: [this.fetchData.parttrantype],
      penalInterest: [this.fetchData.penalInterest],
      penalInterestType: [this.fetchData.penalInterestType],
      calculationMethod: [this.fetchData.calculationMethod],
      interestPeriod: [this.fetchData.interestPeriod],
      fromDate: [this.fetchData.fromDate],
      toDate: [this.fetchData.toDate],
      interestcodeslabs: [this.fetchData.interestcodeslabs],
      penalIntBasedOn: [this.fetchData.penalIntBasedOn]
    });
  }
  getPage() {
    if (this.function_type == 'ADD') {
      this.isEnabled = true;
      this.formData = this.fb.group({
        interestCode: [this.interestCode],
        interestName: [''],
        interestType: [''],
        interestrate: [''],
        parttrantype: [''],
        penalInterest: [''],
        penalInterestType: [''],
        fromDate: [this.prevDate],
        toDate: [this.nextDate],
        calculationMethod: [''],
        interestPeriod: [''],
        interestcodeslabs: [[]],
        penalIntBasedOn: ['']
      });
      this.initialiseChargesForm();
      this.btnColor = 'primary';
      this.btnText = 'SUBMIT';
    } else if (this.function_type == 'INQUIRE') {
      this.getData();
      this.onShowResults = true;
      this.isEnabled = false;
      this.existingData();
      this.disabledFormControll();
      this.isDisabled = true;
    } else if (this.function_type == 'MODIFY') {
      this.getData();
      this.onShowResults = true;
      this.isEnabled = false;
      this.existingData();
      this.btnColor = 'primary';
      this.btnText = 'MODIFY';
    } else if (this.function_type == 'VERIFY') {
      this.getData();
      this.onShowResults = true;
      this.isEnabled = false;
      this.existingData();
      this.disabledFormControll();
      this.btnColor = 'primary';
      this.btnText = 'VERIFY';
    } else if (this.function_type == 'DELETE') {
      this.getData();
      this.onShowResults = true;
      this.isEnabled = false;
      this.existingData();
      this.disabledFormControll();
      this.btnColor = 'accent';
      this.btnText = 'DELETE';
    }
  }
  onSubmit() {
    this.submitted = true;
    console.log("FORM DATA", this.formData.value);
    
    if (this.slabChargesValid) {

      if (this.function_type == 'ADD') {
        this.loading = true;
        if (this.formData.valid) {
          this.interestAPI.create(this.formData.value).pipe(takeUntil(this.destroy$)).subscribe(
            (res) => {
              if (res.statusCode === 201) {
                this.loading = false;
                this.notificationAPI.alertSuccess(res.message);
                this.router.navigate([`/system/interestcode/maintenance`], {
                  skipLocationChange: true,
                });
              } else {
                this.loading = false;
                this.notificationAPI.alertWarning(res.message);
              }
            },
            (err) => {
              this.error = err;
              this.loading = false;
              this.notificationAPI.alertSuccess("SERVER ERROR!!");
            }
          );
        } else {
          this.loading = false;
          this.notificationAPI.alertWarning("INTEREST FORM DATA IS INVALID");
        }
      }
      else if (this.function_type == 'MODIFY') {
        this.loading = true;
        if (this.formData.valid) {
          console.log("MODIFY DATA", this.formData.value);

          this.interestAPI.modify(this.formData.value).subscribe(
            (res) => {
              if (res.statusCode === 200) {
                this.loading = false;
                this.notificationAPI.alertSuccess(res.message);
                this.router.navigate([`/system/interestcode/maintenance`], {
                  skipLocationChange: true,
                });
              } else {
                this.loading = false;
                this.notificationAPI.alertWarning(res.message);
              }
            },
            (err) => {
              this.loading = false;
              this.error = err;
              this.notificationAPI.alertSuccess("SERVER ERROR!!");
            }
          );
        } else {
          this.loading = false;
          this.notificationAPI.alertWarning("INTEREST FORM DATA IS INVALID");
        }
      }
      else if (this.function_type == 'VERIFY') {
        this.loading = true;
        this.interestAPI.verify(this.fetchData.id).subscribe(
          (res) => {
            if (res.statusCode === 200) {
              this.loading = false;
              this.notificationAPI.alertSuccess(res.message);
              this.router.navigate([`/system/interestcode/maintenance`], {
                skipLocationChange: true,
              });
            } else {
              this.loading = false;
              this.notificationAPI.alertWarning(res.message);
            }
          },
          (err) => {
            this.loading = false;
            this.notificationAPI.alertSuccess("SERVER ERROR!!");
          }
        );
      }
      else if (this.function_type == 'DELETE') {
        this.loading = true;
        this.interestAPI.delete(this.fetchData.id).subscribe(
          (res) => {
            if (res.statusCode === 200) {
              this.loading = false;
              this.notificationAPI.alertSuccess(res.message);
              this.router.navigate([`/system/interestcode/maintenance`], {
                skipLocationChange: true,
              });
            } else {
              this.loading = false;
              this.notificationAPI.alertWarning(res.message);
            }
          },
          (err) => {
            this.loading = false;
            this.notificationAPI.alertSuccess("SERVER ERROR!!");
          }
        );
      }
    } else if (!this.slabChargesValid) {
      this.validateData();
      this.loading = false;
    }
  }
  resetTierCharges() {
    this.slabChargesValid = true;
    this.tChargesArray = [];
  }
  initChargesForm() {
    this.chargesForm = this.fb.group({
      id: [''],
      chargeAmount: [''],
      lowerLimit: [1, Validators.required],
      percentage: [''],
      slabName: ['', Validators.required],
      upperLimit: ['', Validators.required],
      usePercentage: ['N']
    });
  }
  getSlabCharges() {
    this.dataSource = new MatTableDataSource(this.tChargesArray);
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
  }
  addToArray() {
    if (this.chargesForm.valid) {
      this.tChargesArray.push(this.chargesForm.value);
      this.resetSlabChargesForm();
      this.validateData();
    }
  }
  resetSlabChargesForm() {
    let lastChargeItem = this.tChargesArray[this.tChargesArray.length - 1];
    let nextLowerLimit = lastChargeItem.upperLimit + 1;

    this.formData.patchValue({
      interestcodeslabs: this.tChargesArray,
    });
    this.getSlabCharges();
    this.initialiseChargesForm();
    this.chargesForm.patchValue({
      lowerLimit: nextLowerLimit,
    });
  }
  validateData() {
    if (this.tChargesArray.length > 1) {
      let arrayOfUL = [];
      let arrayOfLL = [];
      this.tChargesArray.forEach((element) => {
        arrayOfLL.push(element.lowerLimit);
        arrayOfUL.push(element.upperLimit);
      });
      arrayOfLL = arrayOfLL.slice(1);
      arrayOfUL = arrayOfUL.slice(0, -1);
      let arrayOfDifferences = [];
      arrayOfLL.forEach((num1, index) => {
        const num2 = arrayOfUL[index];
        arrayOfDifferences.push(num1 - num2);
      });
      if (arrayOfDifferences.every((n) => n == 1)) {
        this.slabChargesValid = true;
        this.notificationAPI.alertWarning("SLAB RANGE IS VALID!");
      } else {
        this.slabChargesValid = false;
        this.notificationAPI.alertWarning("RANGE OF SLABS IS INVALID!");
      }
    } else {
      this.slabChargesValid = true;
    }
  }
  editItem(data: any) {
    this.index = this.tChargesArray.indexOf(data);
    this.editButton = true;
    this.addButton = false;
    this.chargesForm.patchValue({
      id: data.id,
      chargeAmount: data.chargeAmount,
      lowerLimit: data.lowerLimit,
      percentage: data.percentage,
      slabName: data.slabName,
      upperLimit: data.upperLimit,
      usePercentage: data.usePercentage,
    });
  }
  updateChargeDetails() {
    this.editButton = false;
    this.addButton = true;
    this.tChargesArray[this.index] = this.chargesForm.value;
    this.formData.patchValue({
      interestcodeslabs: this.tChargesArray,
    });
    this.resetSlabChargesForm();
    this.validateData();
  }
  deleteItem(data: any) {
    let deleteIndex = this.tChargesArray.indexOf(data);
    this.tChargesArray.splice(deleteIndex, 1);
    this.resetSlabChargesForm();
    this.validateData();
  }
  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();
    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }
}
