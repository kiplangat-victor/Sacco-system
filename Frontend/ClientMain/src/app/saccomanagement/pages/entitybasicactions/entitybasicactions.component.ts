import { HttpParams } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { FormGroup, FormArray, FormBuilder } from '@angular/forms';
import { Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';
import { WorkClassActionServiceService } from 'src/app/administration/pages/AccessManagement/work-class-actions/work-class-action-service/work-class-action-service.service';
import { WorkClassService } from 'src/app/administration/pages/AccessManagement/work-class/work-class.service';

@Component({
  selector: 'app-entitybasicactions',
  templateUrl: './entitybasicactions.component.html',
  styleUrls: ['./entitybasicactions.component.scss']
})
export class EntitybasicactionsComponent implements OnInit {
  loading = false;
  function_type: any;
  branchCode: any;
  error: any;
  results: any;
  fetchData: any;
  fmData: any;
  isDisabled: boolean;
  postedBy: any;
  verifiedFlag: any;
  verifiedBy: any;
  formData: FormGroup;
  lookUpData: any;
  disableCheckBoxes = false;
  submitted = false;
  rolesData: any[] = [];
  btnColor: any;
  btnText: any;
  hideBtn = false;
  onShowResults = false;
  classdata: any;
  priviledgedata: any;
  basicActionsAddOns = [
    { id: '', name: 'ACCRUAL', selected: false, code: 1 },
    { id: '', name: 'ADD', selected: false, code: 2 },
    { id: '', name: 'ATTACH', selected: false, code: 3 },
    { id: '', name: 'BOOKING', selected: false, code: 4 },
    { id: '', name: 'DELETE', selected: false, code: 5 },
    { id: '', name: 'DEMANDS', selected: false, code: 6 },
    { id: '', name: 'DETACH', selected: false, code: 7 },
    { id: '', name: 'DISBURSE', selected: false, code: 8 },
    { id: '', name: 'INQUIRE', selected: false, code: 9 },
    { id: '', name: 'ENTER', selected: false, code: 10 },
    { id: '', name: 'POST', selected: false, code: 11 },
    { id: '', name: 'LOAN-RESCHEDULING', selected: false, code: 12 },
    { id: '', name: 'VERIFY', selected: false, code: 13 },
    { id: '', name: 'MODIFY', selected: false, code: 14 },
    { id: '', name: 'CASH DEPOSIT', selected: false, code: 15 },
    { id: '', name: 'CASH WITHDRAWAL', selected: false, code: 16 },
    { id: '', name: 'STATEMENT', selected: false, code: 17 },
    { id: '', name: 'TRANSFER', selected: false, code: 18 },
    { id: '', name: 'PROCESS CHEQUE', selected: false, code: 19 },
    { id: '', name: 'ACCOUNT STATEMENTS REPORT', selected: false, code: 20 },
    { id: '', name: 'RETAIL ACCOUNTS REPORT', selected: false, code: 21 },
    { id: '', name: 'ACCOUNTS BY STATUS REPORT', selected: false, code: 22 },
    { id: '', name: 'ALL DISBURSEMENTS REPORT', selected: false, code: 23 },
    { id: '', name: 'DISBURSEMENTS PER BRANCH REPORT', selected: false, code: 24 },
    { id: '', name: 'USER DISBURSEMENTS REPORT', selected: false, code: 25 },
    { id: '', name: 'ALL LOAN PORTFOLIOS REPORT', selected: false, code: 26 },
    { id: '', name: 'BRANCH LOAN PORTFOLIOS REPORT', selected: false, code: 27 },
    { id: '', name: 'USER LOAN PORTFOLIOS REPORT', selected: false, code: 28 },
    { id: '', name: 'RETAIL CUSTOMER REPORT', selected: false, code: 29 },
    { id: '', name: 'GROUP MEMBERS REPORT', selected: false, code: 30 },
    { id: '', name: 'ALL ARREARS REPORT', selected: false, code: 31 },
    { id: '', name: 'ARREARS PER BRANCH REPORT', selected: false, code: 32 },
    { id: '', name: 'ARREARS ON LOAN PER ISSUER REPORT', selected: false, code: 33 },
    { id: '', name: 'ALL REPAYMENTS REPORT', selected: false, code: 34 },
    { id: '', name: 'REPAYMENTS PER BRANCH REPORT', selected: false, code: 34 },
    { id: '', name: 'REPAYMENTS PER MANAGER REPORT', selected: false, code: 36 },
    { id: '', name: 'ALL TRANSACTIONS REPORT', selected: false, code: 37 },
    { id: '', name: 'TRANSACTIONS PER BRANCH REPORT', selected: false, code: 38 },
    { id: '', name: 'ACCOUNT TYPE TRANSACTIONS REPORT', selected: false, code: 39 },
    { id: '', name: 'CUSTOMER ACCOUNT TYPE TRANSACTIONS REPORT', selected: false, code: 40 },
    { id: '', name: 'ALL LOAN DEMANDS REPORT', selected: false, code: 41 },
    { id: '', name: 'LOAN DEMAND PER BRANCH REPORT', selected: false, code: 42 },
    { id: '', name: 'LOAN DEMAND PER BRANCH AS PER MANAGER REPORT', selected: false, code: 43 },
    { id: '', name: 'GUARANTORS LOANS REPORT', selected: false, code: 44 },
    { id: '', name: 'BALANCE SHEET REPORT', selected: false, code: 45 },
    { id: '', name: 'TRIAL BALANCE REPORT', selected: false, code: 46 },
    { id: '', name: 'PROFIT AND LOSS REPORT', selected: false, code: 47 },
    { id: '', name: 'ALL EXPENSES REPORT', selected: false, code: 48 },
    { id: '', name: 'BRANCH EXPENSES REPORT', selected: false, code: 49 },
    { id: '', name: 'ORGANISATION FEE REPORT', selected: false, code: 50 },
    { id: '', name: 'BRANCH FEE REPORT', selected: false, code: 51 },
    { id: '', name: 'CONSOLIDATED LIQUIDITY REPORT', selected: false, code: 52 },
    { id: '', name: 'LIQUIDITY STATEMENTS REPORT', selected: false, code: 53 },
    { id: '', name: 'STATEMENTS COMPREHENSIVE INCOME REPORT', selected: false, code: 54 },
    { id: '', name: 'STATEMENT OF DEPOSIT RETURN REPORT', selected: false, code: 55 },
    { id: '', name: 'STATEMENT OF FINANCIAL POSITION REPORT', selected: false, code: 56 },
    { id: '', name: 'CAPITAL ADEQUACY REPORT', selected: false, code: 57 },
    { id: '', name: 'INVESTMENT RETURN REPORT', selected: false, code: 58 },
    { id: '', name: 'RISK OF CLASSIFICATION REPORT', selected: false, code: 59 },
    { id: '', name: 'GET PAY OFFS', selected: false, code: 60 },
    { id: '', name: "CLOSE", "selected": false, "code": 61 },
    { id: '', name: "SATISFY", "selected": false, "code": 62 },
    { id: '', name: "FUND TELLER", "selected": false, "code": 63 },
    { id: '', name: "COLLECT TELLER FUND", "selected": false, "code": 64 },
    { id: '', name: "POST EXPENSE", "selected": false, "code": 65 },
    { id: '', name: "POST OFFICE JOURNALS", "selected": false, "code": 66 },
    { id: '', name: "RECONCILE ACCOUNTS", "selected": false, "code": 67 },
    { id: '', name: "REVERSE TRANSACTIONS", "selected": false, "code": 68 },
    { id: '', name: "PETTY CASH", "selected": false, "code": 69 },
    { id: '', name: "CLEAR CHEQUE", "selected": false, "code": 70 },
    { id: '', name: "BOUNCE CHEQUE", "selected": false, "code": 71 },
    { id: '', name: "ACTIVATE", "selected": false, "code": 72 },
    { id: '', name: "ACKNOWLEDGE", "selected": false, "code": 73 },
    { id: '', name: "REJECT", "selected": false, "code": 74 },
    { id: '', name: "PAYBILL WITHDRAWAL", "selected": false, "code": 75 },
    { id: '', name: "FORCE DEMANDS", "selected": false, "code": 76 }
  ]
  filtered: { name: string; selected: boolean; code: number; }[];
  basicActions: FormArray<any>;
  displayArray: { id: string; name: string; selected: boolean; code: number; }[];
  obj: any;

  constructor(
    private router: Router,
    public fb: FormBuilder,
    private workClassAPI: WorkClassService,
    private notificationAPI: NotificationService,
    private workActionAPI: WorkClassActionServiceService
  ) {
    this.fmData = this.router.getCurrentNavigation().extras.queryParams.formData;
    this.fetchData = this.router.getCurrentNavigation().extras.queryParams.fetchData;
    if (this.router.getCurrentNavigation().extras.queryParams == null) {
      this.router.navigate([`/saccomanagement/`], { skipLocationChange: true });
    }
    this.function_type = this.fmData.function_type;
    if (this.fetchData.entity != null) {
      this.postedBy = this.fetchData.entity.postedBy;
      this.verifiedFlag = this.fetchData.entity.verifiedFlag;
      this.verifiedBy = this.fetchData.entity.verifiedBy;
    }
  }
  ngOnInit() {
    this.getPage();
    this.getWorkClass();
  }
  getWorkClass() {
    this.loading = true;
    this.workClassAPI.find().subscribe(
      (res) => {
        this.loading = false
        this.classdata = res.entity;
      },
      err => {
        this.loading = false;
        this.notificationAPI.alertWarning(err);
      })
  }
  onWorkClassChange(event: any) {
    this.loading = true;
    this.workClassAPI.getPriviledges(event.target.value).subscribe(
      (res) => {
        this.loading = false;
        this.priviledgedata = res.entity;
      },
      (err) => {
        this.loading = false;
        this.notificationAPI.alertWarning(err);
      }
    )
  }
  apiFormData = this.fb.group({
    id: [''],
    privilegeId: [''],
    workclassId: [''],
    basicactions: new FormArray([])
  })
  onInitForm() {
    this.apiFormData = this.fb.group({
      id: [''],
      privilegeId: [''],
      workclassId: [''],
      basicactions: new FormArray([])
    })
  }
  createEmptyBasicactions(): FormGroup {
    return this.fb.group({
      code: [''],
      id: [''],
      name: [''],
      selected: [''],
    });
  }
  addBasicaction(): void {
    this.basicActions = this.apiFormData.get('basicactions') as FormArray;
    this.basicActions.push(this.createEmptyBasicactions());
  }
  updateEmptyBasicactions(data: any): FormGroup {
    return this.fb.group({
      code: [data.code],
      id: [data.id],
      name: [data.name],
      selected: [data.selected],
    });
  }
  updateBasicaction(data: any): void {
    this.basicActions = this.apiFormData.get('basicactions') as FormArray;
    this.basicActions.push(this.updateEmptyBasicactions(data));
  }
  getData(privilegeId: string | number | boolean, workclassId: string | number | boolean) {
    this.loading = true;
    let params = new HttpParams()
      .set('privilegeId', privilegeId)
      .set('workclassId', workclassId);
    this.workActionAPI.get(params).subscribe(res => {
      this.loading = false;
      this.displayArray = res.entity;
      console.log("display array", res);
      return this.displayArray;
    }, err => {
      this.loading = false;
      this.notificationAPI.alertWarning(err);
    })
  }
  onChange(e: any, i: any) {
    this.displayArray[i].selected = e.checked
  }
  onSelecteWorkClass(e: any) {
    if (this.function_type == 'ADD') {
      this.displayArray = this.basicActionsAddOns
    }
    if (this.function_type != 'ADD') {
      this.getData(this.apiFormData.controls.privilegeId.value, this.apiFormData.controls.workclassId.value);
    }
  }
  disabledFormControl() {
    this.apiFormData.disable();
    this.isDisabled = true;
  }
  get f() {
    return this.formData.controls;
  }

  getPage() {
    if (this.function_type == 'ADD') {
      this.displayArray = this.basicActionsAddOns
      this.basicActionsAddOns = this.basicActionsAddOns;
      this.onInitForm();
      this.btnColor = 'primary';
      this.btnText = 'SUBMIT';
    } else if (this.function_type == 'INQUIRE') {
      this.isDisabled = true;
      this.hideBtn = true;
      this.onShowResults = true;
    } else if (this.function_type == 'MODIFY') {
      this.onInitForm();
      this.btnColor = 'primary';
      this.btnText = 'MODIFY';
    } else if (this.function_type == 'DELETE') {
      this.isDisabled = true;
      this.btnColor = 'warn';
      this.btnText = 'DELETE';
      this.onShowResults = true;
    }
  }

  onSubmit() {
    for (let i = 0; i < this.displayArray.length; i++) {
      this.updateBasicaction(this.displayArray[i])
    }

    if (this.function_type == 'ADD') {
      if (this.apiFormData.valid) {
        this.loading = true;
        this.workActionAPI.add(this.apiFormData.value).subscribe(res => {
          if (res.statusCode === 201) {
            this.loading = false;
            this.notificationAPI.alertSuccess(res.message);
            this.router.navigate([`/saccomanagement/entity-basic-actions/data-view`], {
              skipLocationChange: true
            });
          } else {
            this.loading = false;
            this.notificationAPI.alertWarning(res.message)
          }
        }, err => {
          this.loading = false;
          this.notificationAPI.alertWarning(err.message)
        })
      } else {
        this.loading = false;
        this.notificationAPI.alertWarning("Invalid Form")
      }
    }
    else if (this.function_type == 'MODIFY') {
      this.loading = true;
      this.workActionAPI.modify(this.apiFormData.value).subscribe(res => {
        if (res.statusCode === 200) {
          this.loading = false;
          this.notificationAPI.alertSuccess(res.message);
          this.router.navigate([`/saccomanagement/entity-basic-actions/data-view`], {
            skipLocationChange: true
          });
        } else {
          this.loading = false;
          this.notificationAPI.alertWarning(res.message)
        }
      }, err => {
        this.loading = false;
        this.notificationAPI.alertWarning(err.message)
      })
    }
    else if (this.function_type == 'VERIFY') {
      this.loading = true;
      let params = new HttpParams()
        .set('privilegeId', this.apiFormData.controls.workclassId.value)
        .set('workclassId', this.apiFormData.controls.privilegeId.value);
      this.workActionAPI.delete(params).subscribe(res => {
        if (res.statusCode == 200) {
          this.loading = false;
          this.notificationAPI.alertSuccess(res.message);
          this.router.navigate([`/saccomanagement/entity-basic-actions/data-view`], {
            skipLocationChange: true
          });
        } else {
          this.loading = false;
          this.notificationAPI.alertWarning(res.message);
        }
      }, err => {
        this.loading = false;
        this.notificationAPI.alertWarning(err.message);
      })
    }
    else if (this.function_type == 'DELETE') {
      this.loading = true;
      let params = new HttpParams()
        .set('privilegeId', this.apiFormData.controls.workclassId.value)
        .set('workclassId', this.apiFormData.controls.privilegeId.value);
      console.log("delete params", params);
      this.workActionAPI.delete(params).subscribe(res => {
        this.loading = false;
        this.notificationAPI.alertSuccess(res);
        this.router.navigate([`/saccomanagement/entity-basic-actions/data-view`], {
          skipLocationChange: true
        });
      }, err => {
        this.loading = false;
        this.notificationAPI.alertWarning(err.message);
      })
    }
  }

}
