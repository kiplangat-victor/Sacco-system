import { HttpParams } from '@angular/common/http';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { Subject, Subscription } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';
import { MembershipService } from 'src/app/administration/Service/Membership/membership.service';
import { WorkClassService } from 'src/app/administration/pages/AccessManagement/work-class/work-class.service';
import { UniversalMembershipLookUpComponent } from 'src/app/administration/pages/MembershipComponent/universal-membership-look-up/universal-membership-look-up.component';
import { EntitybrancheslookupComponent } from '../entitybranches/entitybrancheslookup/entitybrancheslookup.component';
import { SaccoEntityLookupComponent } from '../sacco-entity/sacco-entity-lookup/sacco-entity-lookup.component';
import { RolesService } from 'src/app/administration/pages/AccessManagement/roles-management/roles.service';
import { EntityuserService } from './entityuser.service';
import { AuthService } from 'src/@core/AuthService/auth.service';
@Component({
  selector: 'app-entityuser',
  templateUrl: './entityuser.component.html',
  styleUrls: ['./entityuser.component.scss']
})
export class EntityuserComponent implements OnInit, OnDestroy {
  lookupData: any;
  solCode: any;
  isEntityUser: any;
  loading = false;
  error: any;
  isTeller = false;
  submitted: boolean = false;
  rolesArray: any;
  role: any[];
  results: any;
  listOfErrors: any;
  newerr: string;
  acid: any;
  customerType: any;
  workClassArray: any;
  fmData: any;
  onShowResults = false;
  hideBtn = false;
  btnColor: any;
  btnText: any;
  destroy$: Subject<boolean> = new Subject<boolean>();
  disableTeller: boolean = false;
  function_type: any;
  username: any;
  params: HttpParams;
  userRoles: any;
  constructor(
    private router: Router,
    private fb: FormBuilder,
    private dialog: MatDialog,
    private authService: AuthService,
    private rolesService: RolesService,
    private workClassAPI: WorkClassService,
    private membershipAPI: MembershipService,
    private entityUserAPI: EntityuserService,
    private notificationAPI: NotificationService
  ) {
    this.fmData = this.router.getCurrentNavigation().extras.queryParams.formData;
    this.function_type = this.fmData.function_type;
    this.username = this.fmData.username;
  }
  ngOnDestroy(): void {
    this.destroy$.next(true);
    this.destroy$.complete();
  }
  ngOnInit() {
    this.getPage();
    this.getRoles();
  }
  formData = this.fb.group({
    email: ["", [Validators.required]],
    firstName: ['', [Validators.required]],
    lastName: ['', [Validators.required]],
    phoneNo: ['', [Validators.required]],
    solCode: [''],
    sn: [''],
    username: ['', [Validators.required]],
    roleFk: ['', [Validators.required]],
    workclassFk: ['', Validators.required],
    entityId: ['', Validators.required],
    isTeller: ['', [Validators.required]],
    memberCode: [''],
    onBoardingMethod: ["Web"],
    isEntityUser: ["Yes"]
  });
  disabledFormControl() {
    this.formData.disable();
  }
  get f() {
    return this.formData.controls;
  }
  entityIdKeyUp(event: any) {

  }
  entityIDLookup(): void {
    const dialogRef = this.dialog.open(SaccoEntityLookupComponent, {
      width: "45%",
      autoFocus: false,
      maxHeight: '90vh'
    });
    dialogRef.afterClosed().subscribe(result => {
      this.lookupData = result.data;
      this.formData.controls.entityId.setValue(this.lookupData.entityId);
    });
  }

  onKeyUp(event: any) {
    this.loading = true;
    this.membershipAPI.retrieveAccount(event.target.value).pipe(takeUntil(this.destroy$)).subscribe(
      {
        next: (
          (res) => {
            if (res.statusCode === 200) {
              this.loading = false;
              this.results = res.entity;
              this.formData.controls.memberCode.setValue(this.results.customerCode);
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
    )
  }
  membershipLookup(): void {
    const dialogRef = this.dialog.open(UniversalMembershipLookUpComponent, {
      width: '50%',
      autoFocus: false,
      maxHeight: '90vh'
    });
    dialogRef.afterClosed().pipe(takeUntil(this.destroy$)).subscribe(
      results => {
        this.lookupData = results.data;
        this.formData.controls.memberCode.setValue(this.lookupData.customerCode);
      })
  }
  getRoles() {
    this.loading = true;
    this.authService.getRoles().pipe(takeUntil(this.destroy$)).subscribe(
      (res) => {
        this.loading = false;
        this.rolesArray = res;
      },
      (err) => {
        this.loading = false;
        this.notificationAPI.alertWarning(err);
      }
    );
  }
  // getAllRoles() {
  //   this.loading = true;
  //   this.rolesService.getRoles().pipe(takeUntil(this.destroy$)).subscribe(
  //     {
  //       next: (res) => {
  //         if (res.statusCode === 200) {
  //           this.rolesArray = res.entity;
  //           this.loading = true;
  //         } else {
  //           this.loading = false;
  //         }
  //       },
  //       error: (err) => {
  //         this.loading = false;
  //         this.notificationAPI.alertWarning("Server Error: !!");
  //       },
  //       complete: () => {

  //       }
  //     }), Subscription;
  // }
  onSelect(e: any) {
    this.loading = true;
    this.workClassAPI.getWorkClasses(e.target.value).pipe(takeUntil(this.destroy$)).subscribe(
      (res) => {
        this.loading = false;
        this.workClassArray = res.entity;
      }, err => {
        this.loading = false;
        this.notificationAPI.alertWarning(err);
      }
    );
    if (e.target.value == '6') {
      this.formData.patchValue({
        isTeller: 'No',
      });
      this.disableTeller = true;
    } else {
      this.disableTeller = false;
    }
  }

  branchesCodeLookup(): void {
    const dialogRef = this.dialog.open(EntitybrancheslookupComponent, {
      width: "40%",
      autoFocus: false,
      maxHeight: '90vh'
    });
    dialogRef.afterClosed().subscribe(result => {
      this.lookupData = result.data;
      this.formData.controls.solCode.setValue(this.lookupData.branchCode);
    });
  }
  getData() {
    this.loading = true;
    this.params = new HttpParams()
      .set('user', this.fmData.username);
    this.entityUserAPI.username(this.fmData.username).pipe(takeUntil(this.destroy$)).subscribe(
      {
        next: (res) => {
          if (res.statusCode === 302) {
            this.loading = false;
            this.results = res.entity;
            this.userRoles = this.results.roles[0].id;
            this.formData = this.fb.group({
              email: [this.results.email],
              firstName: [this.results.firstName],
              lastName: [this.results.lastName],
              phoneNo: [this.results.phoneNo],
              solCode: [this.results.solCode],
              isEntityUser: [this.results.isEntityUser],
              sn: [this.results.sn],
              username: [this.results.username],
              roleFk: [this.userRoles],
              workclassFk: [this.results.workclassFk],
              entityId: [this.results.entityId],
              isTeller: [this.results.isTeller],
              memberCode: [this.results.memberCode],
              onBoardingMethod: [this.results.onBoardingMethod]
            });
          } else {
            this.loading = false;
            this.notificationAPI.alertWarning("Sacco Entity Records not found: !!");
            this.router.navigate([`/saccomanagement`], { skipLocationChange: true });
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
    if (this.function_type == 'ADD') {
      this.formData = this.fb.group({
        email: ["", [Validators.required]],
        firstName: ['', [Validators.required]],
        lastName: ['', [Validators.required]],
        phoneNo: ['', [Validators.required]],
        solCode: ['001'],
        isEntityUser: ["Yes"],
        sn: [''],
        username: [this.fmData.username],
        roleFk: ['', [Validators.required]],
        workclassFk: ['', Validators.required],
        entityId: ['', Validators.required],
        isTeller: ['', [Validators.required]],
        memberCode: [''],
        onBoardingMethod: ["Web"]
      });
      this.btnColor = 'primary';
      this.btnText = 'SUBMIT';
    } else if (this.function_type == 'INQUIRE') {
      this.disabledFormControl();
      this.getData();
      this.hideBtn = true;
      this.onShowResults = true;
    } else if (this.function_type == 'MODIFY') {
      this.getData();
      this.btnColor = 'primary';
      this.btnText = 'MODIFY';
      this.onShowResults = true;
    } else if (this.function_type == 'VERIFY') {
      this.getData();
      this.disabledFormControl();
      this.btnColor = 'primary';
      this.btnText = 'VERIFY';
      this.onShowResults = true;
    } else if (this.function_type == 'DELETE') {
      this.getData();
      this.disabledFormControl();
      this.btnColor = 'warn';
      this.btnText = 'DELETE';
      this.onShowResults = true;
    }
  }

  onSubmit() {
    this.submitted = true;
    this.loading = true;
      if (this.function_type == "ADD") {
        if (this.formData.valid) {
          console.log("Form Data", this.formData.value);

          this.entityUserAPI.createEntityUser(this.formData.value).pipe(takeUntil(this.destroy$)).subscribe(
            (data) => {
              if (data.statusCode === 201) {
                this.loading = false;
                this.notificationAPI.alertSuccess(data.message);
                this.router.navigate([`/saccomanagement`], { skipLocationChange: true });
              } else {
                this.loading = false;
                this.notificationAPI.alertWarning(data.message);
              }
            }, (err) => {
              this.loading = false;
              this.notificationAPI.alertWarning(err.message);
            }
          );
        } else {
          this.loading = false;
          this.notificationAPI.alertWarning("User Form Data is Invalid: !!");
        }
      }
      if (this.function_type == "VERIFY") {
        if (this.formData.valid) {
          console.log("Form Data", this.formData.value);

          this.entityUserAPI.verify(this.formData.controls.sn.value).pipe(takeUntil(this.destroy$)).subscribe(
            (data) => {
              if (data.statusCode === 201) {
                this.loading = false;
                this.notificationAPI.alertSuccess(data.message);
                this.router.navigate([`/saccomanagement`], { skipLocationChange: true });
              } else {
                this.loading = false;
                this.notificationAPI.alertWarning(data.message);
              }
            }, (err) => {
              this.loading = false;
              this.notificationAPI.alertWarning(err.message);
            }
          );
        } else {
          this.loading = false;
          this.notificationAPI.alertWarning("User Form Data is Invalid: !!");
        }
      }
      if (this.function_type == "MODIFY") {
        if (this.formData.valid) {
          console.log("Form Data", this.formData.value);

          this.entityUserAPI.modify(this.formData.value).pipe(takeUntil(this.destroy$)).subscribe(
            (data) => {
              if (data.statusCode === 201) {
                this.loading = false;
                this.notificationAPI.alertSuccess(data.message);
                this.router.navigate([`/saccomanagement`], { skipLocationChange: true });
              } else {
                this.loading = false;
                this.notificationAPI.alertWarning(data.message);
              }
            }, (err) => {
              this.loading = false;
              this.notificationAPI.alertWarning(err.message);
            }
          );
        } else {
          this.loading = false;
          this.notificationAPI.alertWarning("User Form Data is Invalid: !!");
        }
      }
      if (this.function_type == "DELETE") {
        if (this.formData.valid) {
          console.log("Form Data", this.formData.value);

          this.entityUserAPI.delete(this.formData.value).pipe(takeUntil(this.destroy$)).subscribe(
            (data) => {
              if (data.statusCode === 201) {
                this.loading = false;
                this.notificationAPI.alertSuccess(data.message);
                this.router.navigate([`/saccomanagement`], { skipLocationChange: true });
              } else {
                this.loading = false;
                this.notificationAPI.alertWarning(data.message);
              }
            }, (err) => {
              this.loading = false;
              this.notificationAPI.alertWarning(err.message);
            }
          );
        } else {
          this.loading = false;
          this.notificationAPI.alertWarning("User Form Data is Invalid: !!");
        }
      }
    }

}
