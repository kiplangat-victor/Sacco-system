import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';
import { DataStoreService } from 'src/@core/helpers/data-store.service';
import { InterestcodelookupComponent } from '../interestcodelookup/interestcodelookup.component';

@Component({
  selector: 'app-interestcodemaintenance',
  templateUrl: './interestcodemaintenance.component.html',
  styleUrls: ['./interestcodemaintenance.component.scss'],
})
export class InterestcodemaintenanceComponent implements OnInit {
  lookupData: any;
  interestCode: any;
  existingData: boolean;
  interestName: any;
  formData: FormGroup;
  loading = false;
  submitted = false;
  functionArray: any;
  showCode: boolean = false;
  destroy$: Subject<boolean> = new Subject<boolean>();
  randomCode: string;
  constructor(
    public fb: FormBuilder,
    private router: Router,
    private dialog: MatDialog,
    private dataStoreApi: DataStoreService,
    private notificationAPI: NotificationService
  ) {
    this.functionArray = this.dataStoreApi.getActionsByPrivilege("CONFIGURATIONS");
    this.functionArray = this.functionArray.filter(
      (arr: string) => arr === 'ADD' ||
        arr === 'INQUIRE' ||
        arr === 'MODIFY' ||
        arr === 'VERIFY' ||
        arr === 'DELETE');
  }
  ngOnDestroy() {
    this.destroy$.next(true);
    this.destroy$.complete();
  }
  ngOnInit(): void {
    this.createFormData();
    this.lookupData = {};
    this.randomCode = "INT" + Math.floor(Math.random() * (999 - 100));
  }

  createFormData() {
    this.formData = this.fb.group({
      function_type: ['', Validators.required],
      interestCode: ['', Validators.required],
    });
  }

  interestCodeLookup(): void {
    const dialogRef = this.dialog.open(InterestcodelookupComponent, {
      autoFocus: false,
      maxHeight: '90vh'
    });
    dialogRef.afterClosed().pipe(takeUntil(this.destroy$)).subscribe((result) => {
      this.lookupData = result.data;
      this.interestCode = this.lookupData.interestCode;
      this.interestName = this.lookupData.interestName;
      this.formData.patchValue({
        interestCode: this.interestCode,
      });
    });
  }
  onSelectFunction(event: any) {
    if (event.target.value !== 'ADD') {
      this.existingData = true;
      this.showCode = true;
      this.formData.controls.interestCode.setValue("");
    } else if (event.target.value == 'ADD') {
      this.existingData = false;
      this.showCode = true;
      this.formData.controls.interestCode.setValue(this.randomCode);
    }
  }
  get f() {
    return this.formData.controls;
  }
  onSubmit() {
    this.loading = true;
    this.submitted = true;
    if (this.formData.valid) {
      this.router.navigate([`/system/interestcode/data/view`], {
        skipLocationChange: true,
        queryParams: {
          formData: this.formData.value,
          fetchData: this.lookupData,
        },
      });
    } else if (this.formData.invalid) {
      this.loading = false;
      this.notificationAPI.alertWarning("INTERESET FORM DATA IS INVALID");
    }
  }
}
