import { HttpParams } from '@angular/common/http';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { Subject, Subscription } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';
import { AccountsNotificationService } from 'src/@core/helpers/accounts/accounts-notification.service';
import { GeneralAccountsLookupComponent } from '../../Account-Component/general-accounts-lookup/general-accounts-lookup.component';
import { OfficeAccountsLookUpsComponent } from '../../Account-Component/office-account/office-accounts-look-ups/office-accounts-look-ups.component';
import { ChequeParamsLookUpComponent } from '../../SystemConfigurations/GlobalParams/cheque-params/cheque-params-look-up/cheque-params-look-up.component';
import { ChequeTransactionsService } from './cheque-transactions.service';

@Component({
  selector: 'app-transaction-cheques',
  templateUrl: './transaction-cheques.component.html',
  styleUrls: ['./transaction-cheques.component.scss']
})
export class TransactionChequesComponent implements OnInit, OnDestroy {
  loading = false;
  function_type: string;
  chequeRandCode: any;
  error: any;
  results: any;
  fmData: any;
  submitted = false;
  onShowResults = false;
  hideBtn = false;
  btnColor: any;
  btnText: any;
  onShowSearch: boolean = true;
  onShowmaturityDate: boolean = true;
  showpenaltyDetails: boolean = false;
  destroy$: Subject<boolean> = new Subject<boolean>();
  lookupData: any;
  imageFile: any;
  chequedocument: any;
  imageSrc: string;
  showStatus: boolean = false;
  params: HttpParams;
  matdivider: boolean = false;
  onShowDocumentsUpload: boolean = true;
  onShowUploadedDocuments: boolean = false;
  showTransactionReciept: boolean = false;
  currentUser: any;
  resMessage: any;
  resEntity: any;
  isAlert: boolean = false;
  chequesParams: any;
  hideRejectBtn: boolean = false;
  btnRejectColor: any;
  rejectBtnText: any;
  rejectionData: any;
  displayBackButton: boolean = true;
  displayApprovalBtn: boolean = false;
  constructor(
    private router: Router,
    private fb: FormBuilder,
    private dialog: MatDialog,
    private chequeAPI: ChequeTransactionsService,
    private notificationAPI: NotificationService,
    private accountsNotification: AccountsNotificationService
  ) {
    this.currentUser = JSON.parse(localStorage.getItem('auth-user'));
    this.fmData = this.router.getCurrentNavigation().extras.queryParams.formData;
    this.function_type = this.fmData.function_type;
    this.chequeRandCode = this.fmData.chequeRandCode;
    if (this.fmData.backBtn == 'APPROVAL') {
      this.displayBackButton = false;
      this.displayApprovalBtn = true;
    }
  }
  ngOnInit() {
    this.getPage();
  }
  ngOnDestroy() {
    this.destroy$.next(true);
    this.destroy$.complete();
  }

  formData = this.fb.group({
    id: [''],
    amount: ['', Validators.required],
    chargeCode: ['', Validators.required],
    chequeDocument: [''],
    chequeNumber: ['', Validators.required],
    bankName: ['', Validators.required],
    bankCode: [''],
    bankBranch: [''],
    debitOABAccount: ['', Validators.required],
    eventId: [''],
    maturityDate: [new Date(), Validators.required],
    nameAsPerCheque: ['', Validators.required],
    creditCustOperativeAccount: ['', Validators.required],
    status: [''],
    chequeRandCode: [''],
    penaltyAmount: [''],
    penaltyCollAc: ['']
  });
  disabledFormControll() {
    this.formData.disable();
  }

  getFunctions() {
    this.matdivider = true;
    this.onShowSearch = false;
    this.onShowmaturityDate = false;
    this.onShowResults = true;
    this.showStatus = true;
    this.onShowResults = true;
    this.onShowDocumentsUpload = false;
    this.onShowUploadedDocuments = true;
    this.showpenaltyDetails = true;
  }
  get f() {
    return this.formData.controls;
  }
  chargeCodeLookup(): void {
    const dialogRef = this.dialog.open(ChequeParamsLookUpComponent, {
      width: "40%",
      autoFocus: false,
      maxHeight: '90vh'
    });
    dialogRef.afterClosed().subscribe(result => {
      this.lookupData = result.data;
      this.formData.controls.chargeCode.setValue(this.lookupData.chargeCode);
      this.formData.controls.eventId.setValue(this.lookupData.eventId);
    });
  }
  // savingsCodeLookUp(): void {
  //   const dialogRef = this.dialog.open(SavingsLookupComponent, {
  //     width: '50%',
  //     autoFocus: false,
  //     maxHeight: '90vh'
  //   });
  //   dialogRef.afterClosed().pipe(takeUntil(this.destroy$)).subscribe(result => {
  //     this.lookupData = result.data;
  //     this.formData.controls.creditCustOperativeAccount.setValue(this.lookupData.acid);
  //   });
  // }

  savingsCodeLookUp(): void {
    const dialogRef = this.dialog.open(GeneralAccountsLookupComponent, {
      width: '50%',
      autoFocus: false,
      maxHeight: '90vh'
    });
    dialogRef.afterClosed().pipe(takeUntil(this.destroy$)).subscribe(result => {
      this.lookupData = result.data;
      this.formData.controls.creditCustOperativeAccount.setValue(this.lookupData.acid);
    });
  }
  debitOABAccountLookup(): void {
    const dialogRef = this.dialog.open(OfficeAccountsLookUpsComponent, {
      width: '45%',
      autoFocus: false,
      maxHeight: '90vh'
    });
    dialogRef.afterClosed().subscribe(result => {
      this.lookupData = result.data;
      this.formData.controls.debitOABAccount.setValue(this.lookupData.acid);
    });
  }
  penaltyCollAcLookup(): void {
    const dialogRef = this.dialog.open(OfficeAccountsLookUpsComponent, {
      width: '45%',
      autoFocus: false,
      maxHeight: '90vh'
    });
    dialogRef.afterClosed().subscribe(result => {
      this.lookupData = result.data;
      this.formData.controls.penaltyCollAc.setValue(this.lookupData.acid);
    });
  }
  onImageFileChange(event: any) {
    this.imageFile = event.target.files[0];
    if (event.target.files && event.target.files[0]) {
      var reader = new FileReader();
      reader.readAsDataURL(event.target.files[0]);
      reader.onload = () => {
        this.chequedocument = reader.result;
        this.formData.controls.chequeDocument.setValue(this.chequedocument);
        this.imageSrc = reader.result as string;
      }
      reader.onerror = function (error) {
      };
    }
  }
  chequeReciept() {
    let chequeRandCode = this.results.chequeRandCode;
    const params = new HttpParams()
      .set('chequeRandCode', chequeRandCode);
    this.chequeAPI
      .generateReciept(params).pipe(takeUntil(this.destroy$))
      .subscribe(
        (res) => {
          this.results = res;
          this.loading = false;
          this.notificationAPI.alertSuccess("downloading " + this.results.filename);
          this.loading = false;
          let url = window.URL.createObjectURL(res.data);
          window.open(url);
          // this.getData();
        },
        (err) => {
          this.loading = false;
          this.notificationAPI.alertWarning("Error generating reciept !");
        }
      );
  }
  getData() {
    this.loading = true;
    this.params = new HttpParams()
      .set('chequeRandCode', this.fmData.chequeRandCode);
    this.chequeAPI.find(this.params).pipe(takeUntil(this.destroy$)).subscribe(
      {
        next: (res) => {
          if (res.statusCode === 200) {
            this.loading = false;
            this.results = res.entity;
            this.showTransactionReciept = true;
            if (this.results.verifiedFlag_2 == 'Y') {
              this.hideRejectBtn = false;
            } else if (this.results.verifiedFlag_2 == 'N') {
              this.btnRejectColor = 'accent';
              this.rejectBtnText = 'REJECT';
              this.hideRejectBtn = true;
            }
            this.formData = this.fb.group({
              id: [this.results.id],
              amount: [this.results.amount],
              chargeCode: [this.results.chargeCode],
              chequeDocument: [this.results.chequeDocument],
              chequeNumber: [this.results.chequeNumber],
              bankName: [this.results.bankName],
              bankCode: [this.results.bankCode],
              bankBranch: [this.results.bankBranch],
              debitOABAccount: [this.results.debitOABAccount],
              eventId: [this.results.eventId],
              maturityDate: [this.results.maturityDate],
              nameAsPerCheque: [this.results.nameAsPerCheque],
              creditCustOperativeAccount: [this.results.creditCustOperativeAccount],
              status: [this.results.status],
              chequeRandCode: [this.results.chequeRandCode],
              penaltyAmount: [this.results.penaltyAmount],
              penaltyCollAc: [this.results.penaltyCollAc]
            });
          } else {
            this.loading = false;
            this.notificationAPI.alertWarning("CHEQUES RECORDS NOT FOUND");
            this.router.navigate([`/system/transactions/cheques-maintenance`], { skipLocationChange: true });
          }
        },
        error: (err) => {
          this.loading = false;
          this.notificationAPI.alertWarning("Server Error: !!");
          this.router.navigate([`/system/transactions/cheques-maintenance`], { skipLocationChange: true });
        },
        complete: () => {

        }
      }
    )
  }
  getPage() {
    if (this.function_type == "ENTER") {
      this.formData = this.fb.group({
        id: [''],
        amount: ['', Validators.required],
        chequeDocument: [''],
        chargeCode: ['', Validators.required],
        chequeNumber: ['', Validators.required],
        bankName: ['', Validators.required],
        bankCode: [''],
        bankBranch: [''],
        debitOABAccount: ['', Validators.required],
        eventId: [''],
        maturityDate: [new Date(), Validators.required],
        nameAsPerCheque: ['', Validators.required],
        creditCustOperativeAccount: ['', Validators.required],
        status: [''],
        chequeRandCode: [this.chequeRandCode],
        penaltyAmount: [''],
        penaltyCollAc: ['']
      });
      this.btnColor = 'primary';
      this.btnText = 'ENTER';
    }
    else if (this.function_type == "INQUIRE") {
      this.getData();
      this.getFunctions();
      this.disabledFormControll();
    }
    else if (this.function_type == "MODIFY") {
      this.getData();
      this.matdivider = true;
      this.onShowResults = true;
      this.onShowUploadedDocuments = true;
      this.showStatus = true;
      this.btnColor = 'primary';
      this.btnText = 'MODIFY';
    }
    else if (this.function_type == "CLEAR CHEQUE") {
      this.getData();
      this.getFunctions();
      this.disabledFormControll();
      this.btnColor = 'primary';
      this.btnText = 'CLEAR CHEQUE';
    }
    else if (this.function_type == "BOUNCE CHEQUE") {
      this.getData();
      this.getFunctions();
      this.btnColor = 'primary';
      this.btnText = 'BOUNCE CHEQUE';
    }
    else if (this.function_type == "VERIFY") {
      this.getData();
      this.disabledFormControll();
      this.getFunctions();
      this.btnColor = 'primary';
      this.btnText = 'VERIFY';
    }
    else if (this.function_type == "POST CHEQUE") {
      this.getData();
      this.disabledFormControll();
      this.getFunctions();
      this.btnColor = 'primary';
      this.btnText = 'POST CHEQUE';
    }
    else if (this.function_type == "DELETE") {
      this.getData();
      this.disabledFormControll();
      this.getFunctions();
      this.btnColor = 'accent';
      this.btnText = 'DELETE';
    }
  }
  onSubmit() {
    this.loading = true;
    if (this.function_type == "ENTER") {
      this.submitted = true;
      this.loading = true;
      if (this.formData.valid) {
        this.chequeAPI.enter(this.formData.value).pipe(takeUntil(this.destroy$)).subscribe(
          {
            next: (
              (res) => {
                if (res.statusCode === 201) {
                  this.loading = false;
                  this.results = res.entity;
                  this.accountsNotification.alertSuccess(res.message);
                  this.router.navigate([`/system/transactions/cheques-maintenance`], { skipLocationChange: true });
                } else {
                  this.loading = false;
                  this.resMessage = res.message;
                  this.resEntity = res.entity;
                  if (this.resEntity == null) {
                    this.accountsNotification.alertWarning(res.message);
                  } else {
                    for (let i = 0; i < this.resEntity.length; i++) {
                      this.accountsNotification.alertWarning(this.resEntity[i].message);
                    }
                  }
                }
              }
            ),
            error: (
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
        );
      } else {
        this.loading = false;
        this.notificationAPI.alertWarning("CHEQUE FORM DATA IS INVALID");
      }
    }
    else if (this.function_type == "POST CHEQUE") {
      this.params = new HttpParams()
        .set('chequeRandCode', this.fmData.chequeRandCode);
      this.chequeAPI.post(this.params).pipe(takeUntil(this.destroy$)).subscribe(
        {
          next: (
            (res) => {
              if (res.statusCode === 200) {
                this.loading = false;
                // this.showTransactionReciept = true;
                this.accountsNotification.alertSuccess(res.message);
                if (res.entity.postedFlag == 'Y') {
                  this.showTransactionReciept = true;
                } else if (res.entity.postedFlag == 'N') {
                  this.showTransactionReciept = false;
                }
                // this.router.navigate([`/system/transactions/cheques-maintenance`], { skipLocationChange: true });
              } else {
                this.loading = false;
                this.notificationAPI.alertWarning(res.message);
              }
            }
          ),
          error: (
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
      );
    }
    else if (this.function_type == "MODIFY") {
      this.submitted = true;
      if (this.formData.valid) {
        this.chequeAPI.modify(this.formData.value).pipe(takeUntil(this.destroy$)).subscribe(
          {
            next: (
              (res) => {
                if (res.statusCode === 200) {
                  this.loading = false;
                  this.notificationAPI.alertSuccess(res.message);
                  this.router.navigate([`/system/transactions/cheques-maintenance`], { skipLocationChange: true });
                } else {
                  this.loading = false;
                  this.notificationAPI.alertWarning(res.message);
                }
              }
            ),
            error: (
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
        );
      } else {
        this.loading = false;
        this.notificationAPI.alertWarning("CHEQUE FORM DATA IS INVALID");
      }
    }
    else if (this.function_type == "CLEAR CHEQUE") {
      this.params = new HttpParams()
        .set('chequeRandCode', this.fmData.chequeRandCode);
      this.chequeAPI.clear(this.params).pipe(takeUntil(this.destroy$)).subscribe(
        {
          next: (
            (res) => {
              if (res.statusCode === 201) {
                this.loading = false;
                this.accountsNotification.alertSuccess(res.message);
                this.router.navigate([`/system/transactions/cheques-maintenance`], { skipLocationChange: true });
              } else {
                this.loading = false;
                this.loading = false;
                this.resMessage = res.message;
                this.resEntity = res.entity;
                if (this.resEntity == null) {
                  this.accountsNotification.alertWarning(res.message);
                } else {
                  for (let i = 0; i < this.resEntity.length; i++) {
                    this.accountsNotification.alertWarning(this.resEntity[i].message);
                  }
                }
              }
            }
          ),
          error: (
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
      );
    }
    else if (this.function_type == "BOUNCE CHEQUE") {
      this.loading = false;
      if (this.formData.controls.penaltyAmount.value == null) {
        this.loading = false;
        this.notificationAPI.alertWarning("Please Enter Penalty Amount!!");
      }
      if (this.formData.controls.penaltyCollAc.value == null) {
        this.loading = false;
        this.notificationAPI.alertWarning("Please Choose Penalty Collection to continue!!");
      }
      else {
        this.loading = true;
        this.params = new HttpParams()
          .set('chequeRandCode', this.fmData.chequeRandCode)
          .set('penaltyAmount', this.formData.controls.penaltyAmount.value)
          .set('penaltyCollAc', this.formData.controls.penaltyCollAc.value);
        this.chequeAPI.bounce(this.params).pipe(takeUntil(this.destroy$)).subscribe(
          {
            next: (
              (res) => {
                if (res.statusCode === 201) {
                  this.loading = false;
                  this.accountsNotification.alertSuccess(res.message);
                  this.router.navigate([`/system/transactions/cheques-maintenance`], { skipLocationChange: true });
                } else {
                  this.loading = false;
                  this.notificationAPI.alertWarning(res.message);
                }
              }
            ),
            error: (
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
        );
      }
    }
    else if (this.function_type == "VERIFY") {
      this.params = new HttpParams()
        .set('chequeRandCode', this.fmData.chequeRandCode);
      this.chequeAPI.verify(this.params).pipe(takeUntil(this.destroy$)).subscribe(
        {
          next: (
            (res) => {
              if (res.statusCode === 200) {
                this.loading = false;
                this.notificationAPI.alertSuccess(res.message);
                this.router.navigate([`/system/transactions/cheques-maintenance`], { skipLocationChange: true });
              } else {
                this.loading = false;
                this.notificationAPI.alertSuccess(res.message);
              }
            }
          ),
          error: (
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
    else if (this.function_type == "DELETE") {
      this.params = new HttpParams()
        .set('chequeRandCode', this.fmData.chequeRandCode);
      this.chequeAPI.delete(this.params).pipe(takeUntil(this.destroy$)).subscribe(
        {
          next: (
            (res) => {
              if (res.statusCode === 200) {
                this.loading = false;
                this.notificationAPI.alertSuccess(res.message);
                this.router.navigate([`/system/transactions/cheques-maintenance`], { skipLocationChange: true });
              } else {
                this.loading = false;
                this.notificationAPI.alertWarning(res.message);
              }
            }
          ),
          error: (
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
      );
    }
  }
   onRejectTransaction() {
     if (window.confirm("ARE YOU SURE YOU WANT TO REJECT CHEQUE TRANSACTION FOR " + this.results.nameAsPerCheque + " WITH CODE: " + this.results.chequeRandCode + " ?")) {
       this.chequeAPI.reject(this.fmData.chequeRandCode).pipe(takeUntil(this.destroy$)).subscribe(
         {
           next: (
             (res) => {
               if (res.statusCode === 200) {
                 this.loading = false;
                 this.notificationAPI.alertSuccess(res.message);
                 this.router.navigate([`/system/transactions/cheques-maintenance`], { skipLocationChange: true });
               } else {
                 this.loading = false;
                 this.notificationAPI.alertSuccess(res.message);
               }
             }
           ),
           error: (
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
  }
}
