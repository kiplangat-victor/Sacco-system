import { Component, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { DataStoreService } from 'src/@core/helpers/data-store.service';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';
import { ChequeBookLookupComponent } from './cheque-book-lookup/cheque-book-lookup.component';
import { ChequeBookService } from './cheque-books.service';

@Component({
  selector: 'app-cheque-books',
  templateUrl: './cheque-books.component.html',
  styleUrls: ['./cheque-books.component.scss']
})
export class ChequeBooksComponent implements OnInit {

  lookupData: any;
  id: any;
  existingData: boolean;
  workClassName: any;
  function_type: any;
  resData: any;
  error: any;
  destroy$: Subject<boolean> = new Subject<boolean>();
  loading = false;
  submitted = false;
  functionArray: any;
  constructor(
    public fb: FormBuilder,
    private router: Router,
    private dialog: MatDialog,
    private notificationService: NotificationService,
    private chequebookAPI: ChequeBookService,
    private dataStoreApi: DataStoreService
  ) {
    this.functionArray = this.dataStoreApi.getActionsByPrivilege("TRANSACTION MAINTENANCE");
    this.functionArray = this.functionArray.filter(
      (arr: string) =>
        arr === 'ADD' ||
        arr === 'INQUIRE' ||
        arr === 'MODIFY' ||
        arr === 'VERIFY' ||
        arr === 'DELETE');
  }
  ngOnInit(): void {
    this.lookupData = {};
  }
  ngOnDestroy(): void {
    this.destroy$.next(true);
    this.destroy$.complete();
  }

  formData = this.fb.group({
    function_type: ['', [Validators.required]],
    id: [''],
  });

  onSelectFunction(event: any) {
    this.function_type = event.target.value;
    if (event.target.value == 'ADD') {
      this.existingData = false;
      this.formData.controls.id.clearValidators();
      this.formData.controls.id.updateValueAndValidity();
    } else if (event.target.value != 'ADD') {
      this.existingData = true;
      this.formData.controls['id'].setValidators([Validators.required]);
    }
  }
  get f() {
    return this.formData.controls;
  }
  onSubmit() {
    this.submitted = true;
    if (this.formData.valid) {
      this.loading = true;
      if (this.function_type == 'ADD') {
        this.router.navigate([`/system/chequebook/data/view`], {
          skipLocationChange: true,
          queryParams: {
            formData: this.formData.value,
            fetchData: this.lookupData,
          },
        });
      }
      else if (this.function_type != 'ADD') {
        this.chequebookAPI.findById(this.formData.value.id).pipe(takeUntil(this.destroy$)).subscribe(
          (res) => {
            this.resData = res;
            this.loading = false;
            if (this.resData) {
              this.lookupData = this.resData;
              this.router.navigate([`/system/chequebook/data/view`], {
                skipLocationChange: true,
                queryParams: {
                  formData: this.formData.value,
                  fetchData: this.lookupData,
                },
              });
            } else {
              this.loading = false;
              this.notificationService.alertWarning("This ID does not exist!");
            }
          },
          (err) => {
            this.error = err;
            this.loading = false;
            this.notificationService.alertWarning(this.error);
          }
        );
      }
    } else {
      this.loading = false;
      this.notificationService.alertWarning("Invalid Form Data!");
    }
  }
  chequebookLookUp(): void {
    const dialogRef = this.dialog.open(ChequeBookLookupComponent, {
    });
    dialogRef.afterClosed().subscribe((result) => {
      this.lookupData = result.data;
      this.id = this.lookupData.id;
      this.workClassName = this.lookupData.workClassName;
      this.formData.controls.id.setValue(this.id);
    });
  }
}
