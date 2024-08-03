import { Component, OnDestroy, OnInit } from '@angular/core';
import { EntityuserlookupComponent } from '../entityuserlookup/entityuserlookup.component';
import { takeUntil } from 'rxjs/operators';
import { FormBuilder, Validators } from '@angular/forms';
import { EntityuserService } from '../entityuser.service';
import { DataStoreService } from 'src/@core/helpers/data-store.service';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';
import { Router } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { Subject } from 'rxjs';
import { HttpParams } from '@angular/common/http';
import { EntityuserComponent } from '../entityuser.component';

@Component({
  selector: 'app-entityusermaintenance',
  templateUrl: './entityusermaintenance.component.html',
  styleUrls: ['./entityusermaintenance.component.scss']
})
export class EntityusermaintenanceComponent implements OnInit, OnDestroy {
  lookupData: any;
  loading = false;
  submitted = false;
  onShowSearchIcon = false;
  functionArray: any;
  error: any;
  destroy$: Subject<boolean> = new Subject<boolean>();
  function_type: any;
  results: any;
  randomCode: any;
  onsShowCode: boolean = false;
  submitting: boolean = false;
  user: string;
  params: HttpParams;
  constructor(
    public fb: FormBuilder,
    private router: Router,
    private dialog: MatDialog,
    private dataStoreApi: DataStoreService,
    private entityUserAPI: EntityuserService,
    private notificationAPI: NotificationService,
   // private entityUser: EntityuserComponent,
  ) {
    this.functionArray = this.dataStoreApi.getActionsByPrivilege("CONFIGURATIONS");
    this.functionArray = this.functionArray.filter(
      (arr: string)=> arr === 'ADD' ||
        arr === 'INQUIRE' ||
        arr === 'MODIFY' ||
        arr === 'VERIFY' ||
        arr === 'DELETE');
  }
  formData = this.fb.group({
    function_type: ['', [Validators.required]],
    username: ['', [Validators.required]],
  });
  ngOnInit() {
  }
  ngOnDestroy() {
    this.destroy$.next(true);
    this.destroy$.complete();
  }
  onSelectFunction(event: any) {
    this.function_type = event.target.value;
    if (this.function_type == 'ADD') {
      this.onShowSearchIcon = false;
      this.onsShowCode = true;
      this.formData.controls.username.setValue("ENTITYUSER");
    } else if (this.function_type !== 'ADD') {
      this.onShowSearchIcon = true;
      this.onsShowCode = true;
    }
  }
  get f() { return this.formData.controls; }

  onSubmit() {
    this.loading = true;
    this.submitted = true;
    this.submitting = true;
    if (this.formData.valid) {
      // if(this.function_type == 'ADD') {
      //   //const routerLink="/system/manage/user/create">
      //   this.router.navigate([`/system/manage/entity/user/create`]);
      // }
       if (this.function_type !== 'ADD') {
        this.user = this.formData.controls.username.value;
        this.params = new HttpParams()
          .set('user', this.user);
        this.entityUserAPI.username(this.params).pipe(takeUntil(this.destroy$)).subscribe(
          (data) => {
            if (data.statusCode === 404) {
              this.loading = false;
              this.router.navigate([`/saccomanagement/entity-users/data-view`], { skipLocationChange: true, queryParams: { formData: this.formData.value, fetchData: this.lookupData } });
            } else if (
              data.statusCode === 302) {
              this.loading = false;
              this.submitting = false;
              this.results = data;
              this.notificationAPI.alertWarning(this.results.message);
            }
          },
          (err) => {
            this.loading = false;
            this.submitting = false;
            this.notificationAPI.alertWarning("Server Error: !!");
          }
        )
      } else if (this.function_type == 'ADD') {
        this.router.navigate([`/saccomanagement/entity-users/data-view`], { skipLocationChange: true, queryParams: { formData: this.formData.value, fetchData: this.lookupData } });
      }
    }
    else if (this.formData.controls.function_type.value == "") {
      this.loading = false;
      this.submitted = true;
      this.submitting = false;
      this.notificationAPI.alertWarning("Sacco Entity User Form Function is Invalid");
    }
    else if (this.formData.controls.username.value == "") {
      this.loading = false;
      this.submitted = true;
      this.submitting = false;
      this.notificationAPI.alertWarning("Entity Username is invalid");
    } else {
      this.loading = false;
      this.submitted = true;
      this.submitting = false;
      this.notificationAPI.alertWarning("Choose Sacco User Form Function");
    }
  }

  entityUserLookup(): void {
    const dialogRef = this.dialog.open(EntityuserlookupComponent, {
      width: "40%",
      autoFocus: false,
      maxHeight: '90vh'
    });
    dialogRef.afterClosed().subscribe(result => {
      this.lookupData = result.data;
      this.formData.controls.username.setValue(this.lookupData.username);
    });
  }
}
