import { HttpParams } from '@angular/common/http';
import { Component, Input, OnInit } from '@angular/core';
import { FormArray, FormBuilder, FormGroup } from '@angular/forms';
import { Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';
import { WorkClassService } from '../../work-class/work-class.service';
import { WorkClassActionServiceService } from '../work-class-action-service/work-class-action-service.service';
import { __values } from 'tslib';
import { log } from 'console';
import { eventNames } from 'process';

@Component({
  selector: 'app-work-class-actions',
  templateUrl: './work-class-actions.component.html',
  styleUrls: ['./work-class-actions.component.scss']
})


export class WorkClassActionsComponent implements OnInit {
  loading = false;
  function_type: string;
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
  subscription!: Subscription;
  rolesData: any[] = [];
  btnColor: any;
  btnText: any;
  hideBtn = false;
  onShowResults = false;
  classdata: any;
  priviledgedata: any;
 

  basicActionsAddOns = [

    { filter:'ACCESS MANAGEMENT',id: '', name: 'ADD', selected: false, code: 2 },
    { filter:'ACCESS MANAGEMENT',id: '', name: 'DELETE', selected: false, code: 5 },
    { filter:'ACCESS MANAGEMENT',id: '', name: 'INQUIRE', selected: false, code: 9 },
    { filter:'ACCESS MANAGEMENT',id: '', name: 'VERIFY', selected: false, code: 13 },
    { filter:'ACCESS MANAGEMENT',id: '', name: 'MODIFY', selected: false, code: 14 },

    { filter:'APPROVALS',id: '', name: 'VERIFY', selected: false, code: 13 },
    { filter:'APPROVALS',id: '', name: "REJECT", "selected": false, "code": 74 },
    
    
   
  
    { filter:'REPORTS',id: '', name: 'ACCOUNT STATEMENTS REPORT', selected: false, code: 20 },
    { filter:'REPORTS',id: '', name: 'RETAIL ACCOUNTS REPORT', selected: false, code: 21 },
    { filter:'REPORTS',id: '', name: 'ACCOUNTS BY STATUS REPORT', selected: false, code: 22 },
    { filter:'REPORTS',id: '', name: 'ALL DISBURSEMENTS REPORT', selected: false, code: 23 },
    { filter:'REPORTS',id: '', name: 'DISBURSEMENTS PER BRANCH REPORT', selected: false, code: 24 },
    { filter:'REPORTS',id: '', name: 'USER DISBURSEMENTS REPORT', selected: false, code: 25 },
    { filter:'REPORTS',id: '', name: 'ALL LOAN PORTFOLIOS REPORT', selected: false, code: 26 },
    { filter:'REPORTS',id: '', name: 'BRANCH LOAN PORTFOLIOS REPORT', selected: false, code: 27 },
    { filter:'REPORTS',id: '', name: 'USER LOAN PORTFOLIOS REPORT', selected: false, code: 28 },
    { filter:'REPORTS',id: '', name: 'RETAIL CUSTOMER REPORT', selected: false, code: 29 },
    { filter:'REPORTS',id: '', name: 'GROUP MEMBERS REPORT', selected: false, code: 30 },
    { filter:'REPORTS',id: '', name: 'ALL ARREARS REPORT', selected: false, code: 31 },
    { filter:'REPORTS',id: '', name: 'ARREARS PER BRANCH REPORT', selected: false, code: 32 },
    { filter:'REPORTS',id: '', name: 'ARREARS ON LOAN PER ISSUER REPORT', selected: false, code: 33 },
    { filter:'REPORTS',id: '', name: 'ALL REPAYMENTS REPORT', selected: false, code: 34 },
    { filter:'REPORTS',id: '', name: 'REPAYMENTS PER BRANCH REPORT', selected: false, code: 34 },
    { filter:'REPORTS',id: '', name: 'REPAYMENTS PER MANAGER REPORT', selected: false, code: 36 },
    { filter:'REPORTS',id: '', name: 'ALL TRANSACTIONS REPORT', selected: false, code: 37 },
    { filter:'REPORTS',id: '', name: 'TRANSACTIONS PER BRANCH REPORT', selected: false, code: 38 },
    { filter:'REPORTS',id: '', name: 'ACCOUNT TYPE TRANSACTIONS REPORT', selected: false, code: 39 },
    { filter:'REPORTS',id: '', name: 'CUSTOMER ACCOUNT TYPE TRANSACTIONS REPORT', selected: false, code: 40 },
    { filter:'REPORTS',id: '', name: 'ALL LOAN DEMANDS REPORT', selected: false, code: 41 },
    { filter:'REPORTS',id: '', name: 'LOAN DEMAND PER BRANCH REPORT', selected: false, code: 42 },
    { filter:'REPORTS',id: '', name: 'LOAN DEMAND PER BRANCH AS PER MANAGER REPORT', selected: false, code: 43 },
    { filter:'REPORTS',id: '', name: 'GUARANTORS LOANS REPORT', selected: false, code: 44 },
    { filter:'REPORTS',id: '', name: 'BALANCE SHEET REPORT', selected: false, code: 45 },
    { filter:'REPORTS',id: '', name: 'TRIAL BALANCE REPORT', selected: false, code: 46 },
    { filter:'REPORTS',id: '', name: 'PROFIT AND LOSS REPORT', selected: false, code: 47 },
    { filter:'REPORTS',id: '', name: 'ALL EXPENSES REPORT', selected: false, code: 48 },
    { filter:'REPORTS',id: '', name: 'BRANCH EXPENSES REPORT', selected: false, code: 49 },
    { filter:'REPORTS',id: '', name: 'ORGANISATION FEE REPORT', selected: false, code: 50 },
    { filter:'REPORTS',id: '', name: 'BRANCH FEE REPORT', selected: false, code: 51 },
    { filter:'REPORTS',id: '', name: 'CONSOLIDATED LIQUIDITY REPORT', selected: false, code: 52 },
    { filter:'REPORTS',id: '', name: 'LIQUIDITY STATEMENTS REPORT', selected: false, code: 53 },
    { filter:'REPORTS',id: '', name: 'STATEMENTS COMPREHENSIVE INCOME REPORT', selected: false, code: 54 },
    { filter:'REPORTS',id: '', name: 'STATEMENT OF DEPOSIT RETURN REPORT', selected: false, code: 55 },
    { filter:'REPORTS',id: '', name: 'STATEMENT OF FINANCIAL POSITION REPORT', selected: false, code: 56 },
    { filter:'REPORTS',id: '', name: 'CAPITAL ADEQUACY REPORT', selected: false, code: 57 },
    { filter:'REPORTS',id: '', name: 'INVESTMENT RETURN REPORT', selected: false, code: 58 },
    { filter:'REPORTS',id: '', name: 'RISK OF CLASSIFICATION REPORT', selected: false, code: 59 },

    { filter:'ACCOUNTS MANAGEMENT',id: '', name: 'GET PAY OFFS', selected: false, code: 60 },
    { filter:'ACCOUNTS MANAGEMENT',id: '', name: "CLOSE", "selected": false, "code": 61 },
    { filter:'ACCOUNTS MANAGEMENT',id: '', name: "ACTIVATE", "selected": false, "code": 72 },
    { filter:'ACCOUNTS MANAGEMENT',id: '', name: "REJECT", "selected": false, "code": 74 },
    { filter:'ACCOUNTS MANAGEMENT',id: '', name: "FORCE DEMANDS", "selected": false, "code": 76 },
    { filter:'ACCOUNTS MANAGEMENT',id: '', name: 'LOAN-RESCHEDULING', selected: false, code: 12 },
    { filter:'ACCOUNTS MANAGEMENT',id: '', name: 'DISBURSE', selected: false, code: 8 },
    { filter:'ACCOUNTS MANAGEMENT',id: '', name: 'ADD', selected: false, code: 2 },
    { filter:'ACCOUNTS MANAGEMENT',id: '', name: 'DELETE', selected: false, code: 5 },
    { filter:'ACCOUNTS MANAGEMENT',id: '', name: 'INQUIRE', selected: false, code: 9 },
    { filter:'ACCOUNTS MANAGEMENT',id: '', name: 'VERIFY', selected: false, code: 13 },
    { filter:'ACCOUNTS MANAGEMENT',id: '', name: 'MODIFY', selected: false, code: 14 },
    { filter:'ACCOUNTS MANAGEMENT', id: '', name: 'ACCRUAL', selected: false, code: 1 },
    { filter:'ACCOUNTS MANAGEMENT',id: '', name: 'BOOKING', selected: false, code: 4 },
    { filter:'ACCOUNTS MANAGEMENT',id: '', name: 'DEMANDS', selected: false, code: 6 },
    { filter:'ACCOUNTS MANAGEMENT',id: '', name: 'STATEMENT', selected: false, code: 17 },
    { filter:'ACCOUNTS MANAGEMENT',id: '', name: 'ATTACH', selected: false, code: 3 },
    { filter:'ACCOUNTS MANAGEMENT',id: '', name: 'DETACH', selected: false, code: 7 },
    { filter:'ACCOUNTS MANAGEMENT',id: '', name: "SATISFY", "selected": false, "code": 62 },

    { filter:'TRANSACTION MAINTENANCE',id: '', name: 'ADD', selected: false, code: 2 },
    { filter:'TRANSACTION MAINTENANCE',id: '', name: 'DELETE', selected: false, code: 5 },
    { filter:'TRANSACTION MAINTENANCE',id: '', name: 'INQUIRE', selected: false, code: 9 },
    { filter:'TRANSACTION MAINTENANCE',id: '', name: 'VERIFY', selected: false, code: 13 },
    { filter:'TRANSACTION MAINTENANCE',id: '', name: 'MODIFY', selected: false, code: 14 },
    { filter:'TRANSACTION MAINTENANCE',id: '', name: "ACKNOWLEDGE", "selected": false, "code": 73 },
    { filter:'TRANSACTION MAINTENANCE',id: '', name: "REVERSE TRANSACTIONS", "selected": false, "code": 68 },
    { filter:'TRANSACTION MAINTENANCE',id: '', name: "PETTY CASH", "selected": false, "code": 69 },
    { filter:'TRANSACTION MAINTENANCE',id: '', name: "CLEAR CHEQUE", "selected": false, "code": 70 },
    { filter:'TRANSACTION MAINTENANCE',id: '', name: "BOUNCE CHEQUE", "selected": false, "code": 71 },
    { filter:'TRANSACTION MAINTENANCE',id: '', name: "FUND TELLER", "selected": false, "code": 63 },
    { filter:'TRANSACTION MAINTENANCE',id: '', name: "COLLECT TELLER FUND", "selected": false, "code": 64 },
    { filter:'TRANSACTION MAINTENANCE',id: '', name: "POST EXPENSE", "selected": false, "code": 65 },
    { filter:'TRANSACTION MAINTENANCE',id: '', name: "POST OFFICE JOURNALS", "selected": false, "code": 66 },
    { filter:'TRANSACTION MAINTENANCE',id: '', name: 'ENTER', selected: false, code: 10 },
    { filter:'TRANSACTION MAINTENANCE',id: '', name: 'POST', selected: false, code: 11 },
    { filter:'TRANSACTION MAINTENANCE',id: '', name: 'CASH DEPOSIT', selected: false, code: 15 },
    { filter:'TRANSACTION MAINTENANCE',id: '', name: 'CASH WITHDRAWAL', selected: false, code: 16 },
    { filter:'TRANSACTION MAINTENANCE',id: '', name: 'TRANSFER', selected: false, code: 18 },
    { filter:'TRANSACTION MAINTENANCE',id: '', name: 'PROCESS CHEQUE', selected: false, code: 19 },
    { filter:'TRANSACTION MAINTENANCE',id: '', name: "REJECT", "selected": false, "code": 74 },
    { filter:'TRANSACTION MAINTENANCE',id: '', name: "RECONCILE ACCOUNTS", "selected": false, "code": 67 },
    { filter:'TRANSACTION MAINTENANCE',id: '', name: "PAYBILL WITHDRAWAL", "selected": false, "code": 75 },
    { filter:'TRANSACTION MAINTENANCE',id: '', name: 'ATTACH', selected: false, code: 3 },
    { filter:'TRANSACTION MAINTENANCE',id: '', name: 'DETACH', selected: false, code: 7 },

    { filter:'MEMBERSHIP MANAGEMENT',id: '', name: 'ADD', selected: false, code: 2 },
    { filter:'MEMBERSHIP MANAGEMENT',id: '', name: 'DELETE', selected: false, code: 5 },
    { filter:'MEMBERSHIP MANAGEMENT',id: '', name: 'INQUIRE', selected: false, code: 9 },
    { filter:'MEMBERSHIP MANAGEMENT',id: '', name: 'VERIFY', selected: false, code: 13 },
    { filter:'MEMBERSHIP MANAGEMENT',id: '', name: 'MODIFY', selected: false, code: 14 },
    { filter:'MEMBERSHIP MANAGEMENT',id: '', name: "REJECT", "selected": false, "code": 74 },

    { filter:'PRODUCTS',id: '', name: 'ADD', selected: false, code: 2 },
    { filter:'PRODUCTS',id: '', name: 'DELETE', selected: false, code: 5 },
    { filter:'PRODUCTS',id: '', name: 'INQUIRE', selected: false, code: 9 },
    { filter:'PRODUCTS',id: '', name: 'VERIFY', selected: false, code: 13 },
    { filter:'PRODUCTS',id: '', name: 'MODIFY', selected: false, code: 14 },
    { filter:'PRODUCTS',id: '', name: "REJECT", "selected": false, "code": 74 },

    { filter:'INTEREST MAINTENANCE',id: '', name: 'ADD', selected: false, code: 2 },
    { filter:'INTEREST MAINTENANCE',id: '', name: 'DELETE', selected: false, code: 5 },
    { filter:'INTEREST MAINTENANCE',id: '', name: 'INQUIRE', selected: false, code: 9 },
    { filter:'INTEREST MAINTENANCE',id: '', name: 'VERIFY', selected: false, code: 13 },
    { filter:'INTEREST MAINTENANCE',id: '', name: 'MODIFY', selected: false, code: 14 },
    { filter:'INTEREST MAINTENANCE',id: '', name: "REJECT", "selected": false, "code": 74 },

    { filter:'CHARGE PARAMS',id: '', name: 'ADD', selected: false, code: 2 },
    { filter:'CHARGE PARAMS',id: '', name: 'DELETE', selected: false, code: 5 },
    { filter:'CHARGE PARAMS',id: '', name: 'INQUIRE', selected: false, code: 9 },
    { filter:'CHARGE PARAMS',id: '', name: 'VERIFY', selected: false, code: 13 },
    { filter:'CHARGE PARAMS',id: '', name: 'MODIFY', selected: false, code: 14 },
    { filter:'CHARGE PARAMS',id: '', name: "REJECT", "selected": false, "code": 74 },

    { filter:'CONFIGURATIONS',id: '', name: 'ADD', selected: false, code: 2 },
    { filter:'CONFIGURATIONS',id: '', name: 'DELETE', selected: false, code: 5 },
    { filter:'CONFIGURATIONS',id: '', name: 'INQUIRE', selected: false, code: 9 },
    { filter:'CONFIGURATIONS',id: '', name: 'VERIFY', selected: false, code: 13 },
    { filter:'CONFIGURATIONS',id: '', name: 'MODIFY', selected: false, code: 14 },
    { filter:'CONFIGURATIONS',id: '', name: "REJECT", "selected": false, "code": 74 },

    { filter:'COLLATERALS MANAGEMENT',id: '', name: 'ADD', selected: false, code: 2 },
    { filter:'COLLATERALS MANAGEMENT',id: '', name: 'DELETE', selected: false, code: 5 },
    { filter:'COLLATERALS MANAGEMENT',id: '', name: 'INQUIRE', selected: false, code: 9 },
    { filter:'COLLATERALS MANAGEMENT',id: '', name: 'VERIFY', selected: false, code: 13 },
    { filter:'COLLATERALS MANAGEMENT',id: '', name: 'MODIFY', selected: false, code: 14 },
    { filter:'COLLATERALS MANAGEMENT',id: '', name: "REJECT", "selected": false, "code": 74 },
   
  ]

  filtered: { filter: string ;name: string; selected: boolean; code: number; }[];
  basicActions: FormArray<any>;
  displayArray: {filter: string ; id: string; name: string; selected: boolean; code: number; }[];
  obj: any;

  public _selectedPrivilege: { id: any, name: string };

  get selectedPrivilege(): { id: any, name: string } {
    return this._selectedPrivilege;
  }
  set selectedPrivilege(value: { id: any, name: string }) {
    this._selectedPrivilege = value;
  }
  // reading the name of the privilage selected

  tab: any;
 
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
      this.router.navigate([`/system/`], { skipLocationChange: true });
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
  getData( privilegeId: string | number | boolean, workclassId: string | number | boolean) {
    this.loading = true;
    let params = new HttpParams()
      .set('privilegeId', privilegeId)
      .set('workclassId', workclassId)
    this.workActionAPI.get(params).subscribe(res => {
      this.loading = false;
      this.displayArray = res.entity;
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
      const selectedId = e.target.value;
    const selectedName = e.target.options[e.target.selectedIndex].text;
    this.selectedPrivilege = { id: selectedId, name: selectedName };
    //console.log(this.selectedPrivilege.name)
    if (this._selectedPrivilege.name=='CONFIGURATIONS') {
      this.displayArray=this.basicActionsAddOns.filter(
        (item) => item.filter === 'CONFIGURATIONS')
    }
    else if (this._selectedPrivilege.name=='CHARGE PARAMS'){this.displayArray=this.basicActionsAddOns.filter(
      (item) => item.filter === 'CHARGE PARAMS')}
    else if (this._selectedPrivilege.name=='INTEREST MAINTENANCE'){this.displayArray=this.basicActionsAddOns.filter(
      (item) => item.filter === 'INTEREST MAINTENANCE')}
    else if (this._selectedPrivilege.name=='PRODUCTS'){this.displayArray=this.basicActionsAddOns.filter(
      (item) => item.filter === 'PRODUCTS')}
    else if (this._selectedPrivilege.name=='MEMBERSHIP MANAGEMENT'){this.displayArray=this.basicActionsAddOns.filter(
      (item) => item.filter === 'MEMBERSHIP MANAGEMENT')}
    else if (this._selectedPrivilege.name=='ACCOUNTS MANAGEMENT'){this.displayArray=this.basicActionsAddOns.filter(
      (item) => item.filter === 'ACCOUNTS MANAGEMENT')}
    else if (this._selectedPrivilege.name=='COLLATERALS MANAGEMENT'){this.displayArray=this.basicActionsAddOns.filter(
      (item) => item.filter === 'COLLATERALS MANAGEMENT')}
    else if (this._selectedPrivilege.name=='TRANSACTION MAINTENANCE'){this.displayArray=this.basicActionsAddOns.filter(
      (item) => item.filter === 'TRANSACTION MAINTENANCE')}
    else if (this._selectedPrivilege.name=='EOD MANAGEMENT'){this.displayArray=this.basicActionsAddOns.filter(
      (item) => item.filter === 'EOD MANAGEMENT')}
    else if (this._selectedPrivilege.name=='REPORTS'){this.displayArray=this.basicActionsAddOns.filter(
      (item) => item.filter === 'REPORTS')}
    else if (this._selectedPrivilege.name=='APPROVALS'){this.displayArray=this.basicActionsAddOns.filter(
      (item) => item.filter === 'APPROVALS')}
    else if (this._selectedPrivilege.name=='ACCESS MANAGEMENT'){this.displayArray=this.basicActionsAddOns.filter(
      (item) => item.filter === 'ACCESS MANAGEMENT')}
    else if (this._selectedPrivilege.name=='DASHBOARD'){this.displayArray=this.basicActionsAddOns.filter(
      (item) => item.filter === 'DASHBOARD')}
    
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
        this.displayArray = this.basicActionsAddOns;
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
          if (res.statusCode === 200 || res.statusCode === 201) {
            this.loading = false;
            this.notificationAPI.alertSuccess(res.message);
            this.router.navigate([`/system/workclassactions/maintenance`], {
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
        if (res.statusCode === 200 || res.statusCode === 201) {
          this.loading = false;
          this.notificationAPI.alertSuccess(res.message);
          this.router.navigate([`/system/workclassactions/maintenance`], {
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
          this.router.navigate([`/system/workclassactions/maintenance`], {
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
      this.workActionAPI.delete(params).subscribe(res => {
        this.loading = false;
        this.notificationAPI.alertSuccess(res);
        this.router.navigate([`/system/workclassactions/maintenance`], {
          skipLocationChange: true
        });
      }, err => {
        this.loading = false;
        this.notificationAPI.alertWarning(err.message);
      })
    }
  }

}


