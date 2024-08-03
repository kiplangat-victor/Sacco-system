import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { FormGroup, UntypedFormBuilder, Validators } from '@angular/forms';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { Router } from '@angular/router';
import { Subject } from 'rxjs';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';
import { EventIdService } from 'src/app/administration/pages/SystemConfigurations/ChargesParams/event-id/event-id.service';
import { OfficeAccountsLookUpsComponent } from '../../../Account-Component/office-account/office-accounts-look-ups/office-accounts-look-ups.component';
import { CurrencyLookupComponent } from '../../GlobalParams/currency-config/currency-lookup/currency-lookup.component';
import { EventTypeLookupComponent } from '../event-type/event-type-lookup/event-type-lookup.component';

@Component({
  selector: 'app-event-id',
  templateUrl: './event-id.component.html',
  styleUrls: ['./event-id.component.scss'],
})
export class EventIdComponent implements OnInit, OnDestroy {

  displayedColumns: string[] = [
    'index',
    'tierOption',
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
  chargeTypeArray: string[] = [
    'WITHDRAWALFEE',
    'LOANPROCESSINGFEE',
    'OTHER'
  ]
  amt_derivation_Array: any = [
    { code: 'FIXED', description: 'FIXED AMT' },
    { code: 'PCNT', description: 'PERCENTAGE' },
    { code: 'TC', description: 'Tiered Charges' },
  ];
  exercise_duty_Array: any = [
    { code: 'FIXED', description: 'FIXED AMOUNT' },
    { code: 'PCNT', description: 'PERCENTAGE' }
  ];
  loading = false;
  onShowResults = false;
  function_type: string;
  eventIdCode: any;
  error: any;
  results: any;
  fetchData: any;
  fmData: any;
  isDisabled: boolean;
  postedBy: any;
  verifiedFlag: any;
  verifiedBy: any;
  showContractInput = false;
  showDerivationInput = false;
  showAmtDerivationInput = false;
  showPercentageDerivationInput = false;
  showFilenameDerivationInput = false;
  showChargecodeDerivationInput = false;
  showMrtFilenameDerivationInput = false;
  showExerciseDutyPercentageInput = false;
  showExerciseDutyPercentageSelected = false;
  isEnabled = true;
  flagArray: any = ['Y', 'N'];
  destroy$: Subject<boolean> = new Subject<boolean>();

  dialogValue: any;
  dialogData: any;
  event_type: any;
  chrg_calc_crncy: any;
  chrg_coll_crncy: any;
  params: any;
  eventId: any;
  message: any;
  event_description: any;
  event_type_description: any;
  event_type_desc: any;
  eventid_id: any;
  event_id_desc: any;
  min_amt_ccy_name: any;
  chrg_coll_crncy_name: any;
  max_amt_ccy_name: any;
  chrg_calc_crncy_name: any;
  submitted = false;
  max_amt_ccy: any;
  min_amt_ccy: any;
  ac_placeholder: any;
  event_type_code: any;
  description: any;
  showTCInput = false;
  chargesForm!: FormGroup;
  tChargesArray: any[] = [];
  Valid = false;
  editButton: boolean = false;
  addButton: boolean = true;
  disableAddButton: boolean = false;
  index: number;
  tierChargesValid: boolean;
  lookupData: any;
  showExerciseDutyDerivation: boolean = false;
  showExerciseDutyDerivationInput: boolean = false;
  constructor(
    public fb: UntypedFormBuilder,
    private router: Router,
    private dialog: MatDialog,
    private eventIdAPI: EventIdService,
    private notificationAPI: NotificationService
  ) {
    this.fmData =
      this.router.getCurrentNavigation().extras.queryParams.formData;
    this.fetchData =
      this.router.getCurrentNavigation().extras.queryParams.fetchData;
    this.function_type = this.fmData.function_type;
    this.eventIdCode = this.fmData.eventIdCode;
    this.postedBy = this.fetchData.postedBy;
    this.verifiedFlag = this.fetchData.verifiedFlag;
    this.verifiedBy = this.fetchData.verifiedBy;
    this.verifiedBy = this.fetchData.verifiedBy;
  }
  ngOnDestroy() {
    this.destroy$.next(true);
    this.destroy$.complete();
  }
  ngOnInit() {
    this.initChargesForm();
    this.getPage();
    this.getTCharges();
    if (this.fetchData.amt_derivation_type == 'TC') {
      this.showTCInput = true;
      this.tChargesArray = this.fetchData.tieredcharges;
      this.getTCharges();
    }
  }

  formData = this.fb.group({
    eventIdCode: [''],
    eventTypeCode: [''],
    event_type_desc: [''],
    event_id_desc: [''],
    ac_placeholder: ['', [Validators.required]],
    amt_derivation_type: ['', [Validators.required]],
    amt: [''],
    percentage: [''],
    chrg_code: [''],
    file_name: [''],
    chrg_calc_crncy: [''],
    chrg_coll_crncy: [''],
    min_amt_ccy: ['', [Validators.required]],
    min_amt: [''],
    chargeType: [''],
    max_amt_ccy: ['', [Validators.required]],
    max_amt: ['', [Validators.required]],
    fee_report_code: [''],
    rate_code: [''],
    tran_remarks_state: ['Y'],
    tran_remarks: [''],
    tran_particulars_state: ['N'],
    tran_particulars: [''],
    round_off_flag: [''],
    round_off_value: [''],
    exciseDutyCollAc: [''],
    excise_duty_derivation: [""],
    exercise_duty_fixed_amt: ["0.00"],
    has_exercise_duty: [''],
    exercise_duty_percentage: [''],
    tieredcharges: [[]],
    monthlyFee: ['']
  });
  accountLookup(): void {
    const dialogRef = this.dialog.open(OfficeAccountsLookUpsComponent, {
      width: '600px',
    });
    dialogRef.afterClosed().subscribe((result) => {
      this.ac_placeholder = result.data.accountOwnership;
      this.dialogValue = result.data;
      this.formData.patchValue({
        ac_placeholder: result.data.acid,
      });
    });
  }
  eventTypeLookup(): void {
    const dialogConfig = new MatDialogConfig();
    const dialogRef = this.dialog.open(EventTypeLookupComponent, {});
    dialogRef.afterClosed().subscribe((result) => {
      this.description = result.data.description;
      this.event_description = result.data.description;
      this.event_type_code = result.data.code;
      this.formData.patchValue({
        eventTypeCode: result.data.eventTypeCode,
        event_type_desc: result.data.description,
      });
    });
  }

  chrgCalcCrncyLookup(): void {
    const dialogRef = this.dialog.open(CurrencyLookupComponent, {});
    dialogRef.afterClosed().subscribe((result) => {
      this.chrg_calc_crncy = result.data.ccy;
      this.chrg_calc_crncy_name = result.data.ccy_name;
      this.formData.controls.chrg_calc_crncy.setValue(result.data.ccy);
    });
  }
  chrgCollCrncyLookup(): void {
    const dialogRef = this.dialog.open(CurrencyLookupComponent, {});
    dialogRef.afterClosed().subscribe((result) => {
      this.chrg_coll_crncy = result.data.ccy;
      this.chrg_coll_crncy_name = result.data.ccy_name;
      this.formData.controls.chrg_coll_crncy.setValue(result.data.ccy);
    });
  }

  onYesExerciseDuty(event: any) {
    this.showExerciseDutyPercentageInput = true;
    this.showExerciseDutyDerivation = true;
    this.formData.controls.exercise_duty_percentage.setValidators([
      Validators.required,
    ]);
  }
  onNoExerciseDuty(event: any) {
    this.showExerciseDutyPercentageInput = false;
    this.showExerciseDutyDerivation = false;
    this.formData.controls.exercise_duty_percentage.setValue('0');
  }
  officeAccountLookup(): void {
    const dialogRef = this.dialog.open(OfficeAccountsLookUpsComponent, {
      width: '45%',
      autoFocus: false,
      maxHeight: '90vh',
    });
    dialogRef.afterClosed().subscribe(result => {
      this.lookupData = result.data;
      this.formData.controls.exciseDutyCollAc.setValue(this.lookupData.acid);
    });
  }
  onSelectExerciseDutyDerivation() {
    if (this.formData.controls.excise_duty_derivation.value == 'FIXED') {
      this.showExerciseDutyDerivationInput = true;
      this.formData.controls.exercise_duty_fixed_amt.setValidators([Validators.required]);
    }
    else if (this.formData.controls.excise_duty_derivation.value == 'PCNT') {
      this.showExerciseDutyDerivationInput = false;
      this.formData.controls.exercise_duty_fixed_amt.setValidators([]);
    }
  }

  onSelectAmtDerivationType() {
    if (this.formData.controls.amt_derivation_type.value != 'FIXED') {
      this.showAmtDerivationInput = false;
      this.formData.controls.amt.setValue(0);
      this.formData.controls.amt.setValidators([]);
    } else if (this.formData.controls.amt_derivation_type.value == 'FIXED') {
      this.showAmtDerivationInput = true;
      this.formData.controls.amt.setValidators([Validators.required]);
    }
    if (this.formData.controls.amt_derivation_type.value != 'PCNT') {
      this.showPercentageDerivationInput = false;
      this.formData.controls.percentage.setValue(0);
      this.formData.controls.percentage.setValidators([]);
    } else if (this.formData.controls.amt_derivation_type.value == 'PCNT') {
      this.showPercentageDerivationInput = true;
      this.formData.controls.percentage.setValidators([Validators.required]);
    }
    if (this.formData.controls.amt_derivation_type.value != 'CHRG') {
      this.showChargecodeDerivationInput = false;
      this.formData.controls.chrg_code.setValue(0);
      this.formData.controls.chrg_code.setValidators([]);
    } else if (this.formData.controls.amt_derivation_type.value == 'CHRG') {
      this.showChargecodeDerivationInput = true;
      this.formData.controls.chrg_code.setValidators([Validators.required]);
    }
    if (this.formData.controls.amt_derivation_type.value != 'MRT') {
      this.showMrtFilenameDerivationInput = false;
      this.formData.controls.file_name.setValue('NULL');
      this.formData.controls.file_name.setValidators([]);
    } else if (this.formData.controls.amt_derivation_type.value == 'MRT') {
      this.showMrtFilenameDerivationInput = true;
      this.formData.controls.file_name.setValidators([Validators.required]);
    }
    if (this.formData.controls.amt_derivation_type.value != 'SCRPT') {
      this.showFilenameDerivationInput = false;
      this.formData.controls.file_name.setValue('NULL');
      this.formData.controls.file_name.setValidators([]);
    } else if (this.formData.controls.amt_derivation_type.value == 'SCRPT') {
      this.showFilenameDerivationInput = true;
      this.formData.controls.file_name.setValidators([Validators.required]);
    }
    if (this.formData.controls.amt_derivation_type.value != 'TC') {
      this.showTCInput = false;
    } else if (this.formData.controls.amt_derivation_type.value == 'TC') {
      this.showTCInput = true;
    }
  }
  disabledFormControll() {
    this.disableAddButton = true;
    this.formData.disable()
  }
  get f() {
    return this.formData.controls;
  }
  getData() {
    this.formData = this.fb.group({
      id: [this.fetchData.id],
      eventIdCode: [this.eventIdCode],
      eventTypeCode: [this.fetchData.event_type],
      event_type_desc: [this.fetchData.event_type_desc],
      event_id_desc: [this.fetchData.event_id_desc],
      ac_placeholder: [this.fetchData.ac_placeholder],
      amt_derivation_type: [this.fetchData.amt_derivation_type],
      amt: [this.fetchData.amt],
      percentage: [this.fetchData.percentage],
      chrg_code: [this.fetchData.chrg_code],
      file_name: [this.fetchData.file_name],
      chrg_calc_crncy: [this.fetchData.chrg_calc_crncy],
      chrg_coll_crncy: [this.fetchData.chrg_coll_crncy],
      min_amt_ccy: [this.fetchData.min_amt_ccy],
      min_amt: [this.fetchData.min_amt],
      chargeType: [this.fetchData.chargeType],
      max_amt_ccy: [this.fetchData.max_amt_ccy],
      max_amt: [this.fetchData.max_amt],
      fee_report_code: [this.fetchData.fee_report_code],
      rate_code: [this.fetchData.rate_code],
      tran_remarks_state: [this.fetchData.tran_remarks_state],
      tran_remarks: [this.fetchData.tran_remarks],
      tran_particulars_state: [this.fetchData.tran_particulars_state],
      tran_particulars: [this.fetchData.tran_particulars],
      round_off_flag: [this.fetchData.round_off_flag],
      round_off_value: [this.fetchData.round_off_value],
      exciseDutyCollAc: [this.fetchData.exciseDutyCollAc],
      excise_duty_derivation: [this.fetchData.excise_duty_derivation],
      exercise_duty_fixed_amt: [this.fetchData.exercise_duty_fixed_amt],
      has_exercise_duty: [this.fetchData.has_exercise_duty],
      exercise_duty_percentage: [this.fetchData.exercise_duty_percentage],
      tieredcharges: [this.fetchData.tieredcharges],
      monthlyFee: [this.fetchData.monthlyFee]
    });
    this.onSelectAmtDerivationType();
  }
  getPage() {
    if (this.function_type == 'ADD') {
      this.formData = this.fb.group({
        eventIdCode: [this.eventIdCode],
        event_id_desc: [''],
        eventTypeCode: [''],
        event_type_desc: [''],
        ac_placeholder: [''],
        amt_derivation_type: [''],
        amt: [''],
        percentage: [''],
        chrg_code: [''],
        file_name: [''],
        chrg_calc_crncy: [''],
        chrg_coll_crncy: [''],
        min_amt_ccy: [''],
        min_amt: [''],
        chargeType: [''],
        max_amt_ccy: [''],
        max_amt: [''],
        fee_report_code: [''],
        rate_code: [''],
        tran_remarks_state: ['Y'],
        tran_remarks: [''],
        tran_particulars_state: ['N'],
        tran_particulars: [''],
        round_off_flag: [''],
        round_off_value: [''],
        exciseDutyCollAc: [''],
        excise_duty_derivation: [""],
        exercise_duty_fixed_amt: ["0.00"],
        has_exercise_duty: [''],
        exercise_duty_percentage: [''],
        tieredcharges: [[]],
        monthlyFee: ['']

      });
    } else if (this.function_type == 'INQUIRE') {
      this.getData();
      this.onShowResults = true;
      this.disabledFormControll();
      this.isDisabled = true;
    } else if (this.function_type == 'MODIFY') {
      this.getData();
      this.onShowResults = true;
    } else if (this.function_type == 'VERIFY') {
      this.getData();
      this.onShowResults = true;
      this.disabledFormControll();
    } else if (this.function_type == 'DELETE') {
      this.getData();
      this.onShowResults = true;
      this.disabledFormControll();
    }
  }
  onSubmit() {
    if (this.tierChargesValid) {
      this.loading = true;
      this.submitted = true;
      if (this.function_type == 'ADD') {
        if (this.formData.valid) {
          this.eventIdAPI.create(this.formData.value).subscribe(
            (res) => {
              if (res.statusCode === 201) {
                this.loading = false;
                this.notificationAPI.alertSuccess(res.message);
                this.router.navigate([`/system/configurations/charge/event-id/maintenance`], {
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
              this.notificationAPI.alertWarning("SERVER ERROR!! TRY AGAIN LATER");
            }
          );
        } else {
          this.loading = false;
          this.notificationAPI.alertWarning("CHARGE EVENT ID FORM DATA INVALID");
        }
      }
    else if (this.function_type == 'MODIFY') {
        if (!this.formData.invalid) {
          this.eventIdAPI.modify(this.formData.value).subscribe(
            (res) => {
              if (res.statusCode === 200) {
                this.loading = false;
                this.notificationAPI.alertSuccess(res.message);
                this.router.navigate([`/system/configurations/charge/event-id/maintenance`], {
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
              this.notificationAPI.alertWarning("SERVER ERROR!! TRY AGAIN LATER");
            }
          );
        } else {
          this.loading = false;
          this.notificationAPI.alertWarning("CHARGE EVENT ID FORM DATA INVALIF");
        }
      }

     else if (this.function_type == 'VERIFY') {
        this.eventIdAPI.verify(this.fetchData.id).subscribe(
          (res) => {
            if (res.statusCode === 200) {
              this.loading = false;
              this.notificationAPI.alertSuccess(res.message);
              this.router.navigate([`/system/configurations/charge/event-id/maintenance`], {
                skipLocationChange: true,
              });
            } else {
              this.loading = false;
              this.notificationAPI.alertWarning(res.message);
            }
          },
          (err) => {
            this.error = err;
            console.log(err);
            this.loading = false;
            this.notificationAPI.alertWarning("SERVER ERROR!! TRY AGAIN LATER");
          }
        );
      }
    else  if (this.function_type == 'DELETE') {
        this.eventIdAPI.delete(this.fetchData.id).subscribe(
          (res) => {
            if (res.statusCode === 201) {
              this.loading = false;
              this.notificationAPI.alertSuccess(res.message);
              this.router.navigate([`/system/configurations/charge/event-id/maintenance`], {
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
            this.notificationAPI.alertWarning("SERVER ERROR!! TRY AGAIN LATER");
          }
        );
      }
    } else if (!this.tierChargesValid) {
      this.validateData();
      this.loading = false;
    }
  }

  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();
    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }
  resetTierCharges() {
    this.tierChargesValid = true;
    this.tChargesArray = [];
  }

  initChargesForm() {
    this.chargesForm = this.fb.group({
      id: [''],
      chargeAmount: ['', Validators.required],
      lowerLimit: [1, Validators.required],
      percentage: ['', Validators.required],
      tierOption: ['', Validators.required],
      upperLimit: ['', Validators.required],
      usePercentage: ['N'],
    });
  }

  getTCharges() {
    this.dataSource = new MatTableDataSource(this.tChargesArray);
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
  }
  addToArray() {
    if (this.chargesForm.valid) {
      this.tChargesArray.push(this.chargesForm.value);
      this.resetTierChargesForm();
      this.validateData();
    }
  }

  resetTierChargesForm() {
    let lastChargeItem = this.tChargesArray[this.tChargesArray.length - 1];
    let nextLowerLimit = lastChargeItem.upperLimit + 1;

    this.formData.patchValue({
      tieredcharges: this.tChargesArray,
    });
    this.getTCharges();
    this.initChargesForm();
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
        this.tierChargesValid = true;
        this.notificationAPI.alertSuccess("SLAB RANGE IS VALID");
      } else {
        this.tierChargesValid = false;
        this.notificationAPI.alertWarning("SLAB RANGE IS VALID");
      }
    } else {
      this.tierChargesValid = true;
    }
  }
  editItem(data) {
    this.index = this.tChargesArray.indexOf(data);
    this.editButton = true;
    this.addButton = false;
    this.chargesForm.patchValue({
      id: data.id,
      chargeAmount: data.chargeAmount,
      lowerLimit: data.lowerLimit,
      percentage: data.percentage,
      tierOption: data.tierOption,
      upperLimit: data.upperLimit,
      usePercentage: data.usePercentage,
    });
  }
  updateChargeDetails() {
    this.editButton = false;
    this.addButton = true;
    this.tChargesArray[this.index] = this.chargesForm.value;
    this.formData.patchValue({
      tieredcharges: this.tChargesArray,
    });

    this.resetTierChargesForm();
    this.validateData();
  }
  deleteItem(data: any) {
    let deleteIndex = this.tChargesArray.indexOf(data);
    this.tChargesArray.splice(deleteIndex, 1);
    this.resetTierChargesForm();
    this.validateData();
  }
}
