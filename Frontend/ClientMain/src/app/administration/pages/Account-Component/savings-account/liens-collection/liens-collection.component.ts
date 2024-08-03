import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';
import { GeneralAccountsLookupComponent } from '../../general-accounts-lookup/general-accounts-lookup.component';
import { LiensServiceService } from '../liens-service.service';
import { AccountsService } from 'src/app/administration/Service/AccountsService/accounts/accounts.service';

@Component({
  selector: 'app-liens-collection',
  templateUrl: './liens-collection.component.html',
  styleUrls: ['./liens-collection.component.scss']
})
export class LiensCollectionComponent implements OnInit , OnDestroy{
  loading = false;
  function_type: string;
  lien_code:any;
  branchCode: any;
  error: any;
  fetchData: any;
  fmData: any;
  isDisabled = false;
  postedBy: any;
  verifiedFlag: any;
  verifiedBy: any;
  submitted = false;
  onShowResults = true;
  postedTime: any;
  status = true;
  lienType = true;
  sourceAcc = true;
  destinationAcc = true;
  impactImmediately = true;
  btnColor: any;
  btnText: any;
  showAdjustment = true;
  onShowDate: boolean = true;
  onShowWarning: boolean = true;
  onShowSearchIcon: boolean = true;
  liensStatusArray: any[] = ['ACTIVE', 'CLOSED'];
  liensTypeArray: any[] = ['Full Amount', 'Partial Amount'];
  liensPriorityArray: any[] = [
    {
      value: 1,
      name: "FIRST PRIORITY"
    },
    {
      value: 2,
      name: "SECOND PRIORITY"
    },
    {
      value: 3,
      name: "THIRD PRIORITY"
    }
  ];
  destroy$: Subject<boolean> = new Subject<boolean>();
  accountlookupData: any;
  resData: any;
  results: any;
  formBuilder: any;
hideBtn: any;
  
  constructor(
    public fb: FormBuilder,
    private router: Router,
    private dialog: MatDialog, 
    private liensAPI: LiensServiceService,
    private notificationAPI: NotificationService
  ) {
    this.fmData = this.router.getCurrentNavigation().extras.queryParams.formData;
    this.function_type = this.fmData.function_type;
    this.lien_code= this.fmData.lien_code;
  }
  ngOnInit() {
    this.getPage();
  
    // Use this.formBuilder.group to create a form group
    this.fb.group({
      lienAmount: [this.fmData.lienAmount],
      lienType:[this.fmData.lienType],
    });
  }
  
  ngOnDestroy(): void {
    this.destroy$.next(true);
    this.destroy$.complete();
  }
  formData = this.fb.group({
    id: [''],
    lienAmount: [''],
    //lienAdjustedAmount: [''],
    lienCode: [''],
    lienType: ['', Validators.required],
    priority: ['', Validators.required],
    impactImmediately: [true],
    effectiveFrom: [new Date()],
    expiryDate: [new Date()],
    status: [''],
    lienDescription: ['', Validators.required],
    sourceAcid: ['', Validators.required],
    destinationAcid: ['']
  });
  disabledFormControls() {
    this.formData.disable();
  }
  get f() {
    return this.formData.controls;
  }
  sourceAccountLookup(): void {
    const dialogRef = this.dialog.open(GeneralAccountsLookupComponent,);
    dialogRef.afterClosed().subscribe((result) => {
      console.log('Dialog Result:', result);
      this.accountlookupData = result.data;
      console.log('Selected Account:', this.accountlookupData);
      this.formData.patchValue({
      sourceAcid: this.accountlookupData.acid
      });
    });
  }
  destinationAccountLookup(): void {
    const dialogRef = this.dialog.open(GeneralAccountsLookupComponent,);
    dialogRef.afterClosed().subscribe((result) => {
      console.log('Dialog Result:', result);
      this.accountlookupData = result.data;
      console.log('Selected Account:', this.accountlookupData);
      this.formData.patchValue({
      // destinationAcid: this.accountlookupData.acid
      });
    });
  }
  getFunctions() {
    this.loading = false;
    this.onShowResults = true;
    this.onShowDate = false;
    this.onShowWarning = false;
    this.onShowSearchIcon = false;
  }
  getData(lien_code) {
    this.loading = true;
    console.log('LIENCODE',this.fmData.lien_code);
    this.liensAPI.findByCode(this.fmData.lien_code).pipe(takeUntil(this.destroy$)).subscribe(
      
      data => {

        if (data.statusCode === 302) {
          this.loading = false;
          this.results = data;
          this.resData = this.results.entity;
          console.log("RESULTS", this.resData);
          this.formData = this.fb.group({
            id: [this.results.id],
            lienAmount: [this.resData.lienAmount, Validators.required],
            lienDescription: [this.resData.lienDescription, Validators.required],
            //lienAdjustedAmount: [this.resData.lienAdjustedAmount],
            lienCode: [this.resData.lienCode],
            lienType: [this.resData.lienType, Validators.required],
            priority: [this.resData.priority],
            impactImmediately: [true],
            effectiveFrom: [this.resData.effectiveFrom],
            expiryDate: [this.resData.expiryDate],
            status: [this.resData.status],
            sourceAcid: [this.resData.sourceAcc, Validators.required],
            destinationAcid: [this.resData.sourceAcc, Validators.required]
          });
        } else {
          this.loading = false;
          this.notificationAPI.alertWarning(data.message);
          this.router.navigate([`system/liens-account/maintenance`], { skipLocationChange: true });
        }
      },
      err => {
        this.loading = false;
        this.notificationAPI.alertWarning("SERVER ERROR: ");
        this.router.navigate([`system/liens-account/maintenance`], { skipLocationChange: true });
      }
    )
  }
  getPage() {
    if (this.function_type == 'ADD') {
      this.disabledFormControls(); 
      console.log('hideeeee',this.hideBtn)
      console.log('function type',this.function_type) // Corrected the placement of this line
      this.formData = this.fb.group({
        id: [''],
        lienAmount: ['', Validators.required],
        //lienAdjustedAmount: [''],
        lienCode: [''],
        lienType: ['this.fmData.lienType', Validators.required],
        priority: [''],
        impactImmediately: [true],
        effectiveFrom: [new Date()],
        expiryDate: [new Date()],
        status: ['', Validators.required],
        lienDescription: ['', Validators.required],
        sourceAcid: ['', Validators.required],
        destinationAcid: ['', Validators.required]
      });
      this.btnColor = 'primary';
      this.btnText = 'SUBMIT';
      this.formData.enable();  // Enable the form after setting up the controls
    }
  
 else if (this.function_type == 'INQUIRE') {
    
      this.disabledFormControls();
      this.getData(this.fmData.lien_code);
      this.getFunctions;
    }
    else if (this.function_type == 'VERIFY') {

      this.disabledFormControls();
      this.getData(this.fmData.lien_code);
      //this.getFunctions;
      this.btnColor = 'primary';
      this.btnText = 'VERIFY';
    }
    else if (this.function_type == 'MODIFY') {
      
      this.getData(this.fmData.lien_code);
      this.btnColor = 'primary';
      this.btnText = 'MODIFY';
    }
    else if (this.function_type == 'CLOSE LIEN') {

      this.disabledFormControls();
      this.getData(this.fmData.lien_code);
      this.getFunctions;
      this.btnColor = 'primary';
      this.btnText = 'CLOSE LIEN';
    }
    else if (this.function_type == 'VERIFY CLOSURE') {

      this.disabledFormControls();
      this.getData(this.fmData.lien_code);
      this.getFunctions;
      this.btnColor = 'primary';
      this.btnText = 'VERIFY CLOSE';
    }
  }
  onSubmit() {
    console.log(" onSubmit() {");
    console.log(new Date());
    this.submitted = true;
    this.loading = true;
    console.log(this.formData.value);
    if (this.function_type == 'ADD') {
      this.liensAPI.create(this.formData.value).pipe(takeUntil(this.destroy$)).subscribe(
        data => {
          console.log("Received response");
          if (data.statusCode === 201) {
            this.loading = false;
            this.notificationAPI.alertSuccess(data.message);
            console.log('ADD WORKS',data.message) ;
            this.router.navigate([`system/liens-account/maintenance`], { skipLocationChange: true });
          } else {
            this.loading = false;
            this.notificationAPI.alertWarning(data.message);
          }
        },
        err => {
          this.loading = false;
          this.notificationAPI.alertWarning("SERVER ERROR: CAN NOT INITIATE LIENS");
        }
      )
    } else if (this.function_type == 'VERIFY') {
      this.liensAPI.verify(this.fmData.lien_code).pipe(takeUntil(this.destroy$)).subscribe(
        data => {
          console.log("Form data",this.fmData.lien_code);
          console.log("Received response",this.resData.value);
          if (data.statusCode === 201) {
            this.loading = false;
            this.notificationAPI.alertSuccess(data.message);
            console.log('VERIFY WORKS',data.message) ;
            this.router.navigate([`system/liens-account/maintenance`], { skipLocationChange: true });
          } else {
            this.loading = false;
            this.notificationAPI.alertWarning(data.message);
          }
        },
        err => {
          this.loading = false;
          this.notificationAPI.alertWarning("SERVER ERROR: CAN NOT  VERIFY LIENS");
        }
      )
    }else if (this.function_type == 'MODIFY') {
      this.liensAPI.modify(this.formData.value).pipe(takeUntil(this.destroy$)).subscribe(
        data => {
          console.log("Received response");
          if (data.statusCode === 201) {
            this.loading = false;
            this.notificationAPI.alertSuccess(data.message);
            console.log('MODIFY WORKS',data.message) ;
            this.router.navigate([`system/liens-account/maintenance`], { skipLocationChange: true });
          } else {
            this.loading = false;
            this.notificationAPI.alertWarning(data.message);
          }
        },
        err => {
          this.loading = false;
          this.notificationAPI.alertWarning("SERVER ERROR: CAN NOT MODIFY LIENS");
        }
      )
    }
    else if (this.function_type == 'CLOSE LIEN') {
      console.log("Form data1",this.formData.value);

      this.liensAPI.closeLien(this.fmData.lien_code).pipe(takeUntil(this.destroy$)).subscribe(
        data => {
          console.log("Form data",this.fmData.lien_code);
          console.log("Received response",this.resData.value);
          if (data.statusCode === 201) {
            this.loading = false;
            this.notificationAPI.alertSuccess(data.message);
            console.log('CLOSE LIEN WORKS',data.message) ;
            this.router.navigate([`system/liens-account/maintenance`], { skipLocationChange: true });
          } else {
            this.loading = false;
            this.notificationAPI.alertWarning(data.message);
          }
        },
        err => {
          this.loading = false;
          this.notificationAPI.alertWarning("SERVER ERROR: CAN NOT CLOSE LIENS");
        }
      )
    }
    else if (this.function_type == 'VERIFY CLOSURE') {
      console.log("Form data1",this.formData.value);

      this.liensAPI.verifyLienClose(this.fmData.lien_code).pipe(takeUntil(this.destroy$)).subscribe(
        data => {
          console.log("Form data",this.fmData.lien_code);
          console.log("Received response",this.resData.value);
          if (data.statusCode === 201) {
            this.loading = false;
            this.notificationAPI.alertSuccess(data.message);
            console.log('VERIFY CLOSE LIEN WORKS',data.message) ;
            this.router.navigate([`system/liens-account/maintenance`], { skipLocationChange: true });
          } else {
            this.loading = false;
            this.notificationAPI.alertWarning(data.message);
          }
        },
        err => {
          this.loading = false;
          this.notificationAPI.alertWarning("SERVER ERROR: CAN NOT VERIFY LIEN CLOSURE");
        }
      )
    }

    
  }
}
