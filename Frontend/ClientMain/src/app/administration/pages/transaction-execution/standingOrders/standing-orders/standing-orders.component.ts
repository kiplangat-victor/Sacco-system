import { Component, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { Router } from '@angular/router';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';
import { GeneralAccountsLookupComponent } from '../../../Account-Component/general-accounts-lookup/general-accounts-lookup.component';
import { StandingOrdersService } from '../standing-orders.service';

@Component({
  selector: 'app-standing-orders',
  templateUrl: './standing-orders.component.html',
  styleUrls: ['./standing-orders.component.scss']
})
export class StandingOrdersComponent implements OnInit {
  displayedColumns: string[] = [
    'index',
    'standingOrderCode',
    'destinationAccountNo',
    'amount',
    'actions',
  ];
  dataSource!: MatTableDataSource<any>;
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  lookupData: any;
  loading = false;
  submitted = false;
  onShowSearchIcon = true;
  functionArray: any;
  destroy$: Subject<boolean> = new Subject<boolean>();
  fmData: any;
  function_type: any;
  standingOrderCode: any;
  results: any;
  isDisabled: boolean = false;
  onShowResults: boolean = false;
  isSubmitting = false;
  onShowWarning = true;
  onShowDate: boolean = true;
  statusArray: any[] = ['ACTIVE', 'CLOSED'];
  typeArray: any[] = ['LOANS', 'SALARY', 'PERSONAL'];
  showButtons = true;
  onShowUpdateButton = false;
  onShowAddButton = true;
  table = true;
  actions = true;
  applicationDate = false;
  status = false;
  ondestinationForm = true;
  hideBtn = false;
  btnColor: any;
  btnText: any;
  element: any;
  arrayIndex: any;
  customer_lookup: any;
  accountlookupData: any;
  index: number;
  destinationForm!: FormGroup;
  orderDestinationArray: any[] = [];
  editButton: boolean = false;
  addButton: boolean = true;
  disableAddButton: boolean = false;
  disableActions: boolean = false;
  constructor(
    public fb: FormBuilder,
    private router: Router,
    private dialog: MatDialog,
    private notificationAPI: NotificationService,
    private standingOrdersAPI: StandingOrdersService
  ) {
    this.fmData = this.router.getCurrentNavigation().extras.queryParams.formData;
    this.function_type = this.fmData.function_type;
    this.standingOrderCode = this.fmData.standingOrderCode;
  }
  ngOnInit(): void {
    this.getPage();
    this.initDestinationForm();
  }
  ngOnDestroy(): void {
    this.destroy$.next(true);
    this.destroy$.complete();
  }
  formData: FormGroup = this.fb.group({
    id: [''],
    standingOrderCode: [''],
    applicationDate: [new Date()],
    status: [''],
    sourceAccountNo: ['', Validators.required],
    customerCode: ['', Validators.required],
    amount: ['', Validators.required],
    type: ['', Validators.required],
    allowPartialDeduction: ['', Validators.required],
    effectiveStartDate: [new Date()],
    duration: ['', Validators.required],
    endDate: [new Date()],
    nextRunDate: [new Date()],
    description: ['', Validators.required],
    standingorderdestinations: [[]],
  });
  initialiseDestinationForm() {
    this.initDestinationForm();
    this.destinationForm.controls.destinationAccountNo.setValidators([
      Validators.required,
    ]);
    this.destinationForm.controls.amount.setValidators([
      Validators.required,
    ]);
  }
  initDestinationForm() {
    this.destinationForm = this.fb.group({
      id: [''],
      standingOrderCode: [this.standingOrderCode],
      destinationAccountNo: ['', Validators.required],
      amount: ['', Validators.required]
    });
  }
  addDestinations() {
    if (this.destinationForm.valid) {
      this.orderDestinationArray.push(this.destinationForm.value);
      this.resetDestinationsForm();
    }
  }

  getDestinations() {
    this.dataSource = new MatTableDataSource(this.orderDestinationArray);
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
  }
  resetDestinationsForm() {
    this.formData.patchValue({
      standingorderdestinations: this.orderDestinationArray,
    });
    this.getDestinations();
    this.initialiseDestinationForm();
  }
  editItem(data: any) {
    this.index = this.orderDestinationArray.indexOf(data);
    this.editButton = true;
    this.addButton = false;
    this.destinationForm.patchValue({
      id: data.id,
      standingOrderCode: data.standingOrderCode,
      destinationAccountNo: data.destinationAccountNo,
      amount: data.amount
    });
  }
  updateDestinations() {
    this.editButton = false;
    this.addButton = true;
    this.orderDestinationArray[this.index] = this.destinationForm.value;
    this.formData.patchValue({
      standingorderdestinations: this.orderDestinationArray,
    });
    this.resetDestinationsForm();
  }
  deleteItem(data: any) {
    let deleteIndex = this.orderDestinationArray.indexOf(data);
    this.orderDestinationArray.splice(deleteIndex, 1);
    this.resetDestinationsForm();
  }
  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();
    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }
  get f() {
    return this.formData.controls;
  }
  disabledFormControl() {
    this.formData.disable();
  }
  sourceAccountLookup(): void {
    const dialogRef = this.dialog.open(GeneralAccountsLookupComponent,
      {
        width: '50%',
      autoFocus: false,
      maxHeight: '90vh'
      });
    dialogRef.afterClosed().subscribe((result) => {
      this.accountlookupData = result.data;
      this.formData.patchValue({
        sourceAccountNo: this.accountlookupData.acid,
        customerCode: this.accountlookupData.customerCode
      });
    });
  }
  destinationAccountLookup(): void {
    const dialogRef = this.dialog.open(GeneralAccountsLookupComponent, {
      width: '50%',
      autoFocus: false,
      maxHeight: '90vh'
    });
    dialogRef.afterClosed().subscribe((result) => {
      this.accountlookupData = result.data;
      this.destinationForm.patchValue({
        destinationAccountNo: this.accountlookupData.acid
      });
    });
  }
  getFunctions() {
    this.applicationDate = true;
    this.onShowDate = false;
    this.status = true;
    this.actions = false;
    this.onShowResults = true;
    this.ondestinationForm = false;
    this.onShowSearchIcon = false;
    this.onShowWarning = false;
  }
  getData() {
    this.loading = true;
    this.standingOrdersAPI.findById(this.fmData.id).subscribe(
      (res) => {
        this.loading = false;
        this.results = res.entity;
        this.formData = this.fb.group({
          id: [this.results.id],
          standingOrderCode: [this.results.standingOrderCode],
          applicationDate: [this.results.applicationDate],
          status: [this.results.status],
          sourceAccountNo: [this.results.sourceAccountNo],
          customerCode: [this.results.customerCode],
          amount: [this.results.amount],
          type: [this.results.type],
          allowPartialDeduction: [this.results.allowPartialDeduction],
          effectiveStartDate: [this.results.effectiveStartDate],
          duration: [this.results.duration],
          endDate: [this.results.endDate],
          nextRunDate: [this.results.nextRunDate],
          description: [this.results.description],
          standingorderdestinations: [[]]
        })
        this.results.standingorderdestinations.forEach((element: any) => {
          this.orderDestinationArray.push(element);
        });
        this.getDestinations();
      }
    )
  }
  getPage() {
    if (this.function_type == 'ADD') {
      this.formData = this.fb.group({
        id: [''],
        standingOrderCode: [this.standingOrderCode],
        applicationDate: [new Date()],
        status: [''],
        sourceAccountNo: ['', Validators.required],
        customerCode: ['', Validators.required],
        amount: ['', Validators.required],
        type: ['', Validators.required],
        allowPartialDeduction: ['', Validators.required],
        effectiveStartDate: [new Date()],
        duration: ['', Validators.required],
        endDate: [new Date()],
        nextRunDate: [new Date()],
        description: ['', Validators.required],
        standingorderdestinations: [[]],
      });
      this.initialiseDestinationForm();
      this.btnColor = 'primary';
      this.btnText = 'SUBMIT';
    } else if (this.function_type == 'INQUIRE') {
      this.getData();
      this.getFunctions();
      this.disabledFormControl();
    } else if (this.function_type == 'MODIFY') {
      this.getData();
      this.onShowResults = true;
      this.status = true;
      this.btnColor = 'primary';
      this.btnText = 'MODIFY';
    }
    else if (this.function_type == 'VERIFY') {
      this.getData();
      this.getFunctions();
      this.disabledFormControl();
      this.btnColor = 'primary';
      this.btnText = 'VERIFY';
    } else if (this.function_type == 'DELETE') {
      this.getData();
      this.getFunctions();
      this.disabledFormControl();
      this.btnColor = 'accent';
      this.btnText = 'DELETE';
    }
  }
  onSubmit() {
    this.submitted = true;
    this.loading = true
    if (this.function_type == 'ADD') {
      if (this.formData.valid) {
        this.standingOrdersAPI.create(this.formData.value).pipe(takeUntil(this.destroy$)).subscribe(
          data => {
            if (data.statusCode === 201) {
              this.loading = false;
              this.notificationAPI.alertSuccess("");
              this.router.navigate([`/system/transactions/standing/orders/maintenance`], { skipLocationChange: true });
            } else {
              this.loading = false;
              this.notificationAPI.alertSuccess(data.message);
            }
          },
          error => {
            this.loading = false;
            this.notificationAPI.alertWarning("SERVER ERROR");
          }
        )
      } else {
        this.loading = false;
        this.notificationAPI.alertWarning("STANDING ORDER FORM DATA IS INVALID");
      }
    }
    else if (this.function_type == 'MODIFY') {
      if (this.formData.valid) {
        this.standingOrdersAPI.update(this.formData.value).pipe(takeUntil(this.destroy$)).subscribe(
          data => {
            if (data.statusCode === 200) {
              this.loading = false;
              this.notificationAPI.alertSuccess("STANDING ORDER MODIFIED SUCCESSFULLY");
              this.router.navigate([`/system/transactions/standing/orders/maintenance`], { skipLocationChange: true });
            } else {
              this.loading = false;
              this.notificationAPI.alertSuccess(data.message);
            }
          },
          error => {
            this.loading = false;
            this.notificationAPI.alertWarning("SERVER ERROR");
          }
        )
      } else {
        this.loading = false;
        this.notificationAPI.alertWarning("STANDING ORDER FORM DATA IS INVALID");
      }
    }
    else if (this.function_type == 'VERIFY') {
      this.standingOrdersAPI.verify(this.fmData.id).pipe(takeUntil(this.destroy$)).subscribe(
        data => {
          if (data.statusCode === 200) {
            this.loading = false;
            this.notificationAPI.alertSuccess("STANDING ORDER VERIFIED SUCCESSFULLY");
            this.router.navigate([`/system/transactions/standing/orders/maintenance`], { skipLocationChange: true });
          } else {
            this.loading = false;
            this.notificationAPI.alertSuccess(data.message)
          }
        }, err => {
          this.loading = false;
          this.notificationAPI.alertWarning("SERVER ERROR");
        }
      )
    }
    else if (this.function_type == 'DELETE') {
      this.standingOrdersAPI.verify(this.fmData.id).pipe(takeUntil(this.destroy$)).subscribe(
        data => {
          if (data.statusCode === 200) {
            this.loading = false;
            this.notificationAPI.alertSuccess("STANDING ORDER DELETED SUCCESSFULLY");
            this.router.navigate([`/system/transactions/standing/orders/maintenance`], { skipLocationChange: true });
          } else {
            this.loading = false;
            this.notificationAPI.alertSuccess(data.message)
          }
        }, err => {
          this.loading = false;
          this.notificationAPI.alertWarning("SERVER ERROR");
        }
      )
    }
  }
}
