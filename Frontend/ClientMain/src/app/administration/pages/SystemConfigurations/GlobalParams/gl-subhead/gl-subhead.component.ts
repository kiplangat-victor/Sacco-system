import { Component, OnDestroy, OnInit } from "@angular/core";
import { FormBuilder, Validators } from "@angular/forms";
import { MatDialog } from "@angular/material/dialog";
import { Router } from "@angular/router";
import { Subject } from "rxjs";
import { takeUntil } from "rxjs/operators";
import { NotificationService } from "src/@core/helpers/NotificationService/notification.service";
import { GlCodeLookupComponent } from "../gl-code/gl-code-lookup/gl-code-lookup.component";
import { GlSubheadService } from "./gl-subhead.service";

@Component({
  selector: 'app-gl-subhead',
  templateUrl: './gl-subhead.component.html',
  styleUrls: ['./gl-subhead.component.scss']
})
export class GlSubheadComponent implements OnInit, OnDestroy {
  loading = false;
  function_type: any;
  submitted = false;
  glSubheadCode: any;
  results: any;
  fmData: any;
  lookupData: any;
  glCode: any;
  glDescription: any;
  hideBtn: boolean = false;
  btnColor: any;
  btnText: any;
  onShowResults: boolean = false;
  showsearchIcon: boolean = true;
  destroy$: Subject<boolean> = new Subject<boolean>();
  showWarning: boolean = true;
  constructor(
    public fb: FormBuilder,
    private router: Router,
    private dialog: MatDialog,
    private glSubheadCodeAPI: GlSubheadService,
    private notificationAPI: NotificationService
  ) {
    this.fmData = this.router.getCurrentNavigation().extras.queryParams.formData;
    this.function_type = this.fmData.function_type;
    this.glSubheadCode = this.fmData.glSubheadCode;
  }
  ngOnDestroy(): void {
    this.destroy$.next(true);
    this.destroy$.complete();
  }
  ngOnInit() {
    this.getPage();
  }
  formData = this.fb.group({
    glCode: ['', Validators.required],
    glSubheadCode: ['', Validators.required],
    glSubheadDescription: ['', Validators.required],
    id: [""]
  });
  refCodeLookup(): void {
    const dialogRef = this.dialog.open(GlCodeLookupComponent, {
    });
    dialogRef.afterClosed().subscribe(result => {
      this.lookupData = result.data;
      this.glCode = this.lookupData.glCode;
      this.glDescription = this.lookupData.glDescription;
      this.formData.controls.glCode.setValue(this.glCode);
    });
  }
  disabledFormControll() {
    this.formData.disable();
    this.showWarning = false;
    this.showsearchIcon = false;
  }
  get f() { return this.formData.controls; }

  getData() {
    this.loading = true;
    this.glSubheadCodeAPI.findByCode(this.fmData.glSubheadCode).pipe(takeUntil(this.destroy$)).subscribe(
      {
        next: (
          (res) => {
            if (res.statusCode === 200) {
              this.loading = false;
              this.results = res.entity;
              this.formData = this.fb.group({
                glCode: [this.results.glCode],
                glSubheadCode: [this.results.glSubheadCode],
                glSubheadDescription: [this.results.glSubheadDescription],
                id: [this.results.id]
              });
            } else {
              this.loading = false;
              this.notificationAPI.alertWarning("GL Subhead Data not found");
              this.router.navigate([`/system/configurations/global/gl-subhead/maintenance`], { skipLocationChange: true });
            }
          }
        ),
        error: (
          (err) => {
            this.loading = false;
            this.notificationAPI.alertWarning("Server Error: !!");
            this.router.navigate([`/system/configurations/global/gl-subhead/maintenance`], { skipLocationChange: true });
          }
        ),
        complete: (
          () => {

          }
        )
      }
    )
  }
  getPage() {
    if (this.function_type == "ADD") {
      this.formData = this.fb.group({
        glCode: ['', Validators.required],
        glSubheadCode: [this.glSubheadCode],
        glSubheadDescription: ['', Validators.required],
        id: [""]
      });
      this.btnColor = 'primary';
      this.btnText = 'SUBMIT';
    }
    else if (this.function_type == "INQUIRE") {
      this.getData();
      this.disabledFormControll();
      this.onShowResults = true;
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
    this.submitted = true;
    this.loading = true;
    this.hideBtn = true;
    if (this.function_type == "ADD") {
      if (this.formData.valid) {
        this.glSubheadCodeAPI.create(this.formData.value).pipe(takeUntil(this.destroy$)).subscribe(res => {
          if (res.statusCode == 201) {
            this.loading = false;
            this.notificationAPI.alertSuccess(res.message);
            this.router.navigate([`/system/configurations/global/gl-subhead/maintenance`], { skipLocationChange: true });
          } else {
            this.loading = false;
            this.hideBtn = false;
            this.notificationAPI.alertWarning(res.message);
          }

        }, err => {
          this.loading = false;
          this.hideBtn = false;
          this.notificationAPI.alertWarning("Server Error: !!");
        });

      } else {
        this.loading = false;
        this.hideBtn = false;
        this.notificationAPI.alertWarning("GL SUBHEAD FORM DATA IS INVALID");
      }
    }
    if (this.function_type == "MODIFY") {
      if (this.formData.valid) {
        this.glSubheadCodeAPI.modify(this.formData.value).pipe(takeUntil(this.destroy$)).subscribe(res => {
          if (res.statusCode == 200) {
            this.loading = false;
            this.notificationAPI.alertSuccess(res.message);
            this.router.navigate([`/system/configurations/global/gl-subhead/maintenance`], { skipLocationChange: true });
          } else {
            this.loading = false;
            this.hideBtn = false;
            this.getData();
            this.notificationAPI.alertWarning(res.message);
          }
        }, err => {
          this.loading = false;
          this.hideBtn = false;
          this.getData();
          this.notificationAPI.alertWarning("Server Error: !!");
        });

      } else {
        this.loading = false;
        this.hideBtn = false;
        this.getData();
        this.notificationAPI.alertWarning("GL SUBHEAD FORM DATA IS INVALID");
      }
    }

    if (this.function_type == "VERIFY") {
      this.glSubheadCodeAPI.verify(this.results.id).pipe(takeUntil(this.destroy$)).subscribe(res => {
        if (res.statusCode == 200) {
          this.loading = false;
          this.notificationAPI.alertSuccess(res.message);
          this.router.navigate([`/system/configurations/global/gl-subhead/maintenance`], { skipLocationChange: true });
        } else {
          this.loading = false;
          this.hideBtn = false;
          this.getData();
          this.notificationAPI.alertWarning(res.message);
        }
      }, err => {
        this.loading = false;
        this.hideBtn = false;
        this.getData();
        this.notificationAPI.alertWarning("Server Error: !!");
      });
    }
    if (this.function_type == "DELETE") {
      this.glSubheadCodeAPI.delete(this.results.id).pipe(takeUntil(this.destroy$)).subscribe(res => {
        if (res.statusCode == 200) {
          this.loading = false;
          this.notificationAPI.alertSuccess(res.message);
          this.router.navigate([`/system/configurations/global/gl-subhead/maintenance`], { skipLocationChange: true });
        } else {
          this.loading = false;
          this.hideBtn = false;
          this.getData();
          this.notificationAPI.alertWarning(res.message);
        }
      }, err => {
        this.loading = false;
        this.hideBtn = false;
        this.getData();
        this.notificationAPI.alertWarning("Server Error: !!");
      });
    }

  }
}
