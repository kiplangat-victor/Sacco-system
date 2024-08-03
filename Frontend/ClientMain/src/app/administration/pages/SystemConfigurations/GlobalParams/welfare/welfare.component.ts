import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';
import { BranchesService } from '../branches/branches.service';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { WelfareService } from './welfare.service';
import { MatDialog } from '@angular/material/dialog';
import { OfficeAccountsLookUpsComponent } from '../../../Account-Component/office-account/office-accounts-look-ups/office-accounts-look-ups.component';

@Component({
  selector: 'app-welfare',
  templateUrl: './welfare.component.html',
  styleUrls: ['./welfare.component.scss']
})
export class WelfareComponent implements OnInit, OnDestroy {
  welfareActionColumns: string[] = [
    "index",
    "actionAccount",
    'actionName',
    'allowAccountChange',
    'tranAction',
    "actions",
  ];
  welfareActionDataSource: MatTableDataSource<any>;
  @ViewChild("welfareActionPaginator") welfareActionPaginator!: MatPaginator;
  @ViewChild(MatSort) welfareActionSort!: MatSort;
  index: number;
  welfareActionForm!: FormGroup;
  welfareActionArray: any[] = [];
  editButton: boolean = false;
  addButton: boolean = true;
  disableAddButton: boolean = false;
  disableActions: boolean = false;
  submittedCaa: boolean = false;
  hideBtn: boolean = false;
  btnColor: any;
  loading = false;
  function_type: string;
  welfareCode: any;
  error: any;
  results: any;
  fmData: any;
  submitted = false;
  onShowResults = false;
  showSearch: boolean = true;
  btnText: any;
  destroy$: Subject<boolean> = new Subject<boolean>();
  submitting: boolean = false;
  showWarning: boolean = true;
  showgwelfareActionForm: boolean = true;
  welfareName: any;
  lookupData: any;
  constructor(
    public fb: FormBuilder,
    private router: Router,
    private dialog: MatDialog,
    private welfareAPI: WelfareService,
    private notificationAPI: NotificationService
  ) {
    this.fmData = this.router.getCurrentNavigation().extras.queryParams.formData;
    this.function_type = this.fmData.function_type;
    this.welfareCode = this.fmData.welfareCode;
    this.welfareName = this.fmData.welfareName;

  }
  ngOnInit() {
    this.getPage();
    this.initialisewelfareActionForm();
  }
  ngOnDestroy() {
    this.destroy$.next(true);
    this.destroy$.complete();
  }
  formData: FormGroup = this.fb.group({
    actions: [[]],
    id: [""],
    welfareCode: ["", [Validators.required]],
    welfareName: ["", [Validators.required]]
  });
  initialisewelfareActionForm() {
    this.initwelfareActionForm();
    this.welfareActionForm.controls.actionAccount.setValidators([Validators.required]);
    this.welfareActionForm.controls.actionCode.setValidators([Validators.required]);
    this.welfareActionForm.controls.actionName.setValidators([]);
    this.welfareActionForm.controls.allowAccountChange.setValidators([Validators.required]);
    this.welfareActionForm.controls.id.setValidators([]);
    this.welfareActionForm.controls.tranAction.setValidators([Validators.required]);
  }
  initwelfareActionForm() {
    this.welfareActionForm = this.fb.group({
      actionAccount: [''],
      actionCode: [''],
      actionName: [''],
      allowAccountChange: [''],
      id: [''],
      tranAction: ['']
    })
  }
  addWelfareAction() {
    if (this.welfareActionForm.valid) {
      this.welfareActionArray.push(this.welfareActionForm.value);
      this.resetwelfareActionForm();
    }
  }
  getWelfareAction() {
    this.welfareActionDataSource = new MatTableDataSource(this.welfareActionArray);
    this.welfareActionDataSource.paginator = this.welfareActionPaginator;
    this.welfareActionDataSource.sort = this.welfareActionSort;
  }
  resetwelfareActionForm() {
    this.formData.patchValue({
      actions: this.welfareActionArray
    });
    this.getWelfareAction();
    this.initwelfareActionForm();

  }
  editglWelfareAction(data: any) {
    this.index = this.welfareActionArray.indexOf(data);
    this.editButton = true;
    this.addButton = false;
    this.welfareActionForm.patchValue({
      actionAccount: data.actionAccount,
      actionCode: data.actionCode,
      actionName: data.actionName,
      allowAccountChange: data.allowAccountChange,
      id: data.id,
      tranAction: data.tranAction
    });
  }
  updateWelfareAction() {
    this.editButton = false;
    this.addButton = true;
    this.welfareActionArray[this.index] = this.welfareActionForm.value;
    this.formData.patchValue({
      actions: this.welfareActionArray
    });
    this.resetwelfareActionForm();
  }
  deleteWelfareAction(data: any) {
    let deleteIndex = this.welfareActionArray.indexOf(data);
    this.welfareActionArray.splice(deleteIndex, 1);
    this.resetwelfareActionForm();
  }
  applyWelfareActionFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.welfareActionDataSource.filter = filterValue.trim().toLowerCase();
    if (this.welfareActionDataSource.paginator) {
      this.welfareActionDataSource.paginator.firstPage();
    }
  }
  actionAccountLookup(): void {
    const dialogRef = this.dialog.open(OfficeAccountsLookUpsComponent, {
      width: '45%',
      autoFocus: false,
      maxHeight: '90vh'
    });
    dialogRef.afterClosed().subscribe(result => {
      this.lookupData = result.data;
      this.welfareActionForm.controls.actionAccount.setValue(this.lookupData.acid);
    });
  }

  disabledFormControll() {
    this.formData.disable();
    this.showWarning = false;
    this.showgwelfareActionForm = false;
  }
  get f() {
    return this.formData.controls;
  }
  getData() {
    this.loading = true;
    this.welfareAPI.welfareCode(this.fmData.welfareCode).pipe(takeUntil(this.destroy$)).subscribe(
      {
        next: (res) => {
          if (res.statusCode === 200) {
            this.loading = false;
            this.results = res.entity;
            this.formData = this.fb.group({
              actions: [[]],
              id: [this.results.id],
              welfareCode: [this.results.welfareCode],
              welfareName: [this.results.welfareName]
            });
            this.results.actions.forEach((element: any) => {
              this.welfareActionArray.push(element);
            });
            this.getWelfareAction();
            this.formData.patchValue({
              actions: this.welfareActionArray
            });
          } else {
            this.loading = false;
            this.notificationAPI.alertWarning("Welfare Records Not Found: !!");
            this.router.navigate([`/system/configurations/welfare/maintenance`], { skipLocationChange: true });
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
        actions: [[]],
        id: [""],
        welfareCode: [this.welfareCode],
        welfareName: [this.welfareName]
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
    this.submitting = true;
    if (this.function_type == "ADD") {
      if (this.welfareActionForm.valid) {
        if (this.formData.valid) {
          console.log("FORM DATA", this.formData.value);
          
          this.welfareAPI.create(this.formData.value).pipe(takeUntil(this.destroy$)).subscribe(
            (data) => {
              if (data.statusCode === 201) {
                this.loading = false;
                this.notificationAPI.alertSuccess(data.message);
                this.router.navigate([`/system/configurations/welfare/maintenance`], { skipLocationChange: true });
              } else {
                this.loading = false;
                this.submitting = false;
                this.notificationAPI.alertWarning(data.message);
              }
            }, (err) => {
              this.loading = false;
              this.submitting = false;
              this.notificationAPI.alertWarning("Server Error: !!");
            }
          );
        } else {
          this.loading = false;
          this.submitting = false;
          this.notificationAPI.alertWarning("Welfare Form Data is invalid");
        }
      }
      else {
        this.loading = false;
        this.submitting = false;
        this.notificationAPI.alertWarning("Welfare Form Data is invalid");
      }

    }
    else if (this.function_type == "MODIFY") {
      if (this.formData.valid) {
        this.welfareAPI.modify(this.formData.value).pipe(takeUntil(this.destroy$)).subscribe(res => {
          if (res.statusCode == 200) {
            this.loading = false;
            this.results = res;
            this.notificationAPI.alertSuccess(this.results.message);
            this.router.navigate([`/system/configurations/welfare/maintenance`], { skipLocationChange: true });
          } else {
            this.loading = false;
            this.submitting = false;
            this.results = res;
            this.getData();
            this.notificationAPI.alertWarning(this.results.message);
          }
        }, err => {
          this.loading = false;
          this.submitting = false;
          this.getData();
          this.notificationAPI.alertWarning("Server Error: !!");
        })
      } else {
        this.loading = false;
        this.submitting = false;
        this.getData();
        this.notificationAPI.alertWarning("Welfare Form Data is Invalid");
      }
    }
    else if (this.function_type == "VERIFY") {
      this.welfareAPI.verify(this.results.id).pipe(takeUntil(this.destroy$)).subscribe(res => {
        if (res.statusCode == 200) {
          this.results = res;
          this.loading = false;
          this.notificationAPI.alertSuccess(this.results.message);
          this.router.navigate([`/system/configurations/welfare/maintenance`], { skipLocationChange: true });

        } else {
          this.results = res;
          this.loading = false;
          this.submitting = false;
          this.getData();
          this.notificationAPI.alertWarning(this.results.message);
        }
      }, err => {
        this.loading = false;
        this.submitting = false;
        this.getData();
        this.notificationAPI.alertWarning("Server Error: !!");
      })
    }
    else if (this.function_type == "DELETE") {
      this.welfareAPI.delete(this.results.id).pipe(takeUntil(this.destroy$)).subscribe(res => {
        if (res.statusCode == 200) {
          this.results = res;
          this.loading = false;
          this.notificationAPI.alertSuccess(this.results.message);
          this.router.navigate([`/system/configurations/welfare/maintenance`], { skipLocationChange: true });
        } else {
          this.results = res;
          this.loading = false;
          this.submitting = false;
          this.getData();
          this.notificationAPI.alertWarning(this.results.message);
        }
      }, err => {
        this.loading = false;
        this.submitting = false;
        this.getData();
        this.notificationAPI.alertWarning("Server Error: !!");
      })
    }
  }
}
