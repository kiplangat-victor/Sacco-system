import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MatRadioChange } from '@angular/material/radio';
import { Router } from '@angular/router';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';
import { EventIdLookupComponent } from '../../ChargesParams/event-id/event-id-lookup/event-id-lookup.component';
import { ChequeParamsService } from './cheque-params.service';

@Component({
  selector: 'app-cheque-params',
  templateUrl: './cheque-params.component.html',
  styleUrls: ['./cheque-params.component.scss']
})
export class ChequeParamsComponent implements OnInit, OnDestroy {
  loading = false;
  function_type: string;
  error: any;
  results: any;
  fmData: any;
  submitted = false;
  onShowResults = false;
  hideBtn: boolean = false;
  btnColor: any;
  btnText: any;
  destroy$: Subject<boolean> = new Subject<boolean>();
  chargeCode: any;
  lookupdata: any;
  showSearch: boolean = true;
  showWarning: boolean = true;
  showEventId: boolean = false;
  constructor(
    public fb: FormBuilder,
    private router: Router,
    private dialog: MatDialog,
    private notificationAPI: NotificationService,
    private chequeparamsAPI: ChequeParamsService,
  ) {
    this.fmData = this.router.getCurrentNavigation().extras.queryParams.formData;
    this.function_type = this.fmData.function_type;
    this.chargeCode = this.fmData.chargeCode;
    console.log("FMDATA", this.fmData.chargeCode);

  }
  ngOnInit() {
    this.getPage();
  }
  ngOnDestroy() {
    this.destroy$.next(true);
    this.destroy$.complete();
  }

  formData = this.fb.group({
    chargeCode: ["", Validators.required],
    collectCharges: ["", Validators.required],
    eventId: [""],
    id: [""],
    name: ["", Validators.required],
  });
  disabledFormControll() {
    this.formData.disable();
    this.showSearch = false;
    this.showWarning = false;
  }
  get f() {
    return this.formData.controls;
  }
  handleChange(event: MatRadioChange) {
    if (event.value == 'Y') {
      this.showEventId = true;
    }
    else if (event.value == 'N') {
      this.showEventId = false;
    }
  }
  eventIdCodeLookup(): void {
    const dialogRef = this.dialog.open(EventIdLookupComponent, {
      width: '40%'
    });
    dialogRef.afterClosed().subscribe(result => {
      this.lookupdata = result.data;
      this.formData.controls.eventId.setValue(this.lookupdata.eventIdCode);
    });
  }
  getData() {
    this.loading = true;
    this.chequeparamsAPI.findByCode(this.fmData.chargeCode).pipe(takeUntil(this.destroy$)).subscribe(
      {
        next: (res) => {
          if (res.statusCode === 302) {
            this.loading = false;
            this.results = res.entity;
            if (this.results.collectCharges == 'Y') {
              this.showEventId = true;
              this.formData.controls.eventId.disable;
            } else if (this.results.collectCharges == 'N') {
              this.showEventId = false;
            }
            this.formData = this.fb.group({
              chargeCode: [this.results.chargeCode],
              collectCharges: [this.results.collectCharges],
              eventId: [this.results.eventId],
              id: [this.results.id],
              name: [this.results.name],
            });
          } else {
            this.loading = false;
            this.notificationAPI.alertWarning("Cheque Params Data Not Found");
            this.router.navigate([`/system/configurations/cheque/charges/maintenance`], { skipLocationChange: true });
          }
        },
        error: (err) => {
          this.loading = false;
          this.notificationAPI.alertWarning("Server Error: !!");
          this.router.navigate([`/system/configurations/cheque/charges/maintenance`], { skipLocationChange: true });
        },
        complete: () => {

        }
      }
    )
  }

  getPage() {
    if (this.function_type == "ADD") {
      this.formData = this.fb.group({
        chargeCode: [this.chargeCode],
        collectCharges: ["", Validators.required],
        eventId: [""],
        id: [""],
        name: ["", Validators.required],
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
    this.hideBtn = true;
    if (this.function_type == "ADD") {
      if (this.formData.valid) {
        this.chequeparamsAPI.create(this.formData.value).pipe(takeUntil(this.destroy$)).subscribe(
          (data) => {
            if (data.statusCode === 201) {
              this.loading = false;
              this.notificationAPI.alertSuccess(data.message);
              this.router.navigate([`/system/configurations/cheque/charges/maintenance`], { skipLocationChange: true });
            } else {
              this.loading = false;
              this.hideBtn = false;
              this.notificationAPI.alertWarning(data.message);
            }
          }, (err) => {
            this.loading = false;
            this.hideBtn = false;
            this.notificationAPI.alertWarning("Server Error: !!");
          }
        );
      } else {
        this.loading = false;
        this.notificationAPI.alertWarning("Cheque Params Form Data is Invalid");
      }
    }
    else if (this.function_type == "MODIFY") {
      if (this.formData.valid) {
        this.chequeparamsAPI.modify(this.formData.value).pipe(takeUntil(this.destroy$)).subscribe(res => {
          console.log(res);
          if (res.statusCode == 200) {
            this.loading = false;
            this.results = res;
            this.notificationAPI.alertSuccess(this.results.message);
            this.router.navigate([`/system/configurations/cheque/charges/maintenance`], { skipLocationChange: true });
          } else {
            this.loading = false;
            this.results = res;
            this.hideBtn = false;
            this.notificationAPI.alertWarning(this.results.message);
          }
        }, err => {
          this.loading = false;
          this.hideBtn = false;
          this.notificationAPI.alertWarning("Server Error: !!");
        })
      } else {
        this.loading = false;
        this.hideBtn = false;
        this.notificationAPI.alertWarning("Cheque Params Form Data is Invalid");
      }
    }
    else if (this.function_type == "VERIFY") {
      this.chequeparamsAPI.verify(this.results.id).pipe(takeUntil(this.destroy$)).subscribe(res => {
        if (res.statusCode == 200) {
          this.results = res;
          this.loading = false;
          this.notificationAPI.alertSuccess(this.results.message);
          this.router.navigate([`/system/configurations/cheque/charges/maintenance`], { skipLocationChange: true });

        } else {
          this.results = res;
          this.loading = false;
          this.hideBtn = false;
          this.notificationAPI.alertWarning(this.results.message);
        }
      }, err => {
        this.loading = false;
        this.hideBtn = false;
        this.notificationAPI.alertWarning("Server Error: !!");
      })
    }
    else if (this.function_type == "DELETE") {
      this.chequeparamsAPI.delete(this.results.id).pipe(takeUntil(this.destroy$)).subscribe(res => {
        if (res.statusCode == 200) {
          this.results = res;
          this.loading = false;
          this.notificationAPI.alertSuccess(this.results.message);
          this.router.navigate([`/system/configurations/cheque/charges/maintenance`], { skipLocationChange: true });
        } else {
          this.results = res;
          this.loading = false;
          this.hideBtn = false;
          this.notificationAPI.alertWarning(this.results.message);
        }
      }, err => {
        this.loading = false;
        this.hideBtn = false;
        this.notificationAPI.alertWarning("Server Error: !!");
      })
    }
  }
}
