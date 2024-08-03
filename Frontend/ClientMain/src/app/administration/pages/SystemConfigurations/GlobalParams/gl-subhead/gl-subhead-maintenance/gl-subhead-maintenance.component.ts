import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { Subject, Subscription } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { DataStoreService } from 'src/@core/helpers/data-store.service';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';
import { GlCodeService } from '../../gl-code/gl-code.service';
import { GlSubheadLookupComponent } from '../gl-subhead-lookup/gl-subhead-lookup.component';
import { GlSubheadService } from '../gl-subhead.service';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';

@Component({
  selector: 'app-gl-subhead-maintenance',
  templateUrl: './gl-subhead-maintenance.component.html',
  styleUrls: ['./gl-subhead-maintenance.component.scss']
})
export class GlSubheadMaintenanceComponent implements OnInit, OnDestroy {
  displayedColumns: string[] = ['index', 'glCode', 'glDescription', 'classification', 'verifiedFlag'];
  dataSource!: MatTableDataSource<any>;
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;
  lookupData: any;
  glSubheadCode: any;
  glSubheadDescription: any;
  respData: any;
  data: any;
  loading = false;
  submitted = false;
  functionArray: any
  randomCode: any;
  showCode: boolean = false;
  destroy$: Subject<boolean> = new Subject<boolean>();
  function_type: any;
  results: any;
  proceed: boolean = false;
  existingData: boolean =false;
  constructor(
    public fb: FormBuilder,
    private router: Router,
    private dialog: MatDialog,
    private glcodeAPI: GlCodeService,
    private dataStoreApi: DataStoreService,
    private glSubheadCodeAPI: GlSubheadService,
    private notificationAPI: NotificationService
  ) {
    this.functionArray = this.dataStoreApi.getActionsByPrivilege("CONFIGURATIONS");
    this.functionArray = this.functionArray.filter(
      (arr: string) =>
        arr === 'ADD' ||
        arr === 'INQUIRE' ||
        arr === 'MODIFY' ||
        arr === 'VERIFY' ||
        arr === 'DELETE');
  }
  ngOnDestroy(): void {
    this.destroy$.next(true);
    this.destroy$.complete();
  }
  ngOnInit(): void {
    this.getData();
    this.randomCode = "GLS" + Math.floor(Math.random() * (999));
  }

  formData = this.fb.group({
    function_type: ['', [Validators.required]],
    glSubheadCode: ['', [Validators.required]],
  });
  onSelectFunction(event: any) {
    this.function_type = event.target.value;
    if (this.function_type == 'ADD') {
      this.existingData = false;
      this.showCode = true;
      this.formData.controls.glSubheadCode.setValue(this.randomCode);
    } else if (this.function_type !== 'ADD') {
      this.existingData = true;
      this.showCode = true;
      this.formData.controls.glSubheadCode.setValue("");
    }
  }
  get f() { return this.formData.controls; }
  onSubmit() {
    this.loading = true;
    this.submitted = true;
    this.proceed = true;
    if (this.formData.valid) {
      this.glSubheadCode = this.formData.controls.glSubheadCode.value;
      if (this.function_type == 'ADD') {
        this.glSubheadCodeAPI.findByCode(this.glSubheadCode).pipe(takeUntil(this.destroy$)).subscribe(
          (data) => {
            if (data.statusCode === 404) {
              this.loading = false;
              this.router.navigate(['system/configurations/global/gl-subhead'], { skipLocationChange: true, queryParams: { formData: this.formData.value, fetchData: this.lookupData } });
            } else if (
              data.statusCode === 200) {
              this.loading = false;
              this.results = data;
              this.proceed = false;
              this.notificationAPI.alertWarning(this.results.message);
            }
          },
          (err) => {
            this.loading = false;
            this.proceed = false;
            this.notificationAPI.alertWarning("Server Error: !!!");
          }
        )
      } else if (this.function_type !== 'ADD') {
        this.router.navigate(['system/configurations/global/gl-subhead'], { skipLocationChange: true, queryParams: { formData: this.formData.value, fetchData: this.lookupData } });
      }
    }
    else if (this.formData.controls.function_type.value == "") {
      this.loading = false;
      this.submitted = true;
      this.proceed = false;
      this.notificationAPI.alertWarning("GL SUBHEAD FORM FUNCTION TYPE IS INVALID");
    }
    else if (this.formData.controls.glSubheadCode.value == "") {
      this.loading = false;
      this.submitted = true;
      this.proceed = false;
      this.notificationAPI.alertWarning("GL SUBHEAD FORM CODE IS INVALID");
    } else {
      this.loading = false;
      this.submitted = true;
      this.proceed = false;
      this.notificationAPI.alertWarning("GL SUBHEAD FORM FUNCTION INVALID");
    }
  }
  glSubheadCodeLookup(): void {
    const dialogRef = this.dialog.open(GlSubheadLookupComponent, {
      width: '35%',
      autoFocus: false,
      maxHeight: '90vh'
    });
    dialogRef.afterClosed().subscribe(result => {
      this.lookupData = result.data;
      this.glSubheadCode = this.lookupData.glSubheadCode;
      this.glSubheadDescription = this.lookupData.glSubheadDescription;
      this.formData.controls.glSubheadCode.setValue(this.glSubheadCode);
    });
  }

  getData() {
    this.loading = true;
    this.glcodeAPI.find().pipe(takeUntil(this.destroy$)).subscribe(
      {
        next: (
          (res) => {
            if (res.statusCode === 302) {
              this.respData = res.entity
              this.loading = false;
              this.dataSource = new MatTableDataSource(this.respData);
              this.dataSource.paginator = this.paginator;
              this.dataSource.sort = this.sort;
            } else {
              this.loading = false;
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
  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();
    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }
}
