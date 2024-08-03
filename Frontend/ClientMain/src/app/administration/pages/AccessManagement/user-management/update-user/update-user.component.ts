import { Component, NgZone, OnInit } from '@angular/core';
import { UntypedFormBuilder, Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { Subject, Subscription } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { AuthService } from 'src/@core/AuthService/auth.service';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';
import { BranchesLookupComponent } from 'src/app/administration/pages/SystemConfigurations/GlobalParams/branches/branches-lookup/branches-lookup.component';
import { MembershipService } from 'src/app/administration/Service/Membership/membership.service';
import { UniversalMembershipLookUpComponent } from '../../../MembershipComponent/universal-membership-look-up/universal-membership-look-up.component';
import { WorkClassService } from '../../work-class/work-class.service';
@Component({
  selector: 'app-update-user',
  templateUrl: './update-user.component.html',
  styleUrls: ['./update-user.component.scss'],
})
export class UpdateUserComponent implements OnInit {
  lookupData: any;
  solCode: any;
  loading = false;
  error: any;
  isTeller = false;
  submitted: boolean;
  rolesArray: any;
  role: any[];
  results: any;
  listOfErrors: any;
  newerr: string;
  data: any;
  fmData: any;
  defaultRole: any;
  acid: any;
  customerType: any;
  disableTeller = false;
  workClassArray: any;
  destroy$: Subject<boolean> = new Subject<boolean>();
  constructor(
    private fb: UntypedFormBuilder,
    private dialog: MatDialog,
    private router: Router,
    private ngZone: NgZone,
    private authService: AuthService,
    private workClassAPI: WorkClassService,
    private membershipAPI: MembershipService,
    private notificationAPI: NotificationService
  ) {
    this.fmData =
      this.router.getCurrentNavigation().extras.queryParams.formData;
      console.log(this.fmData);
  }

  ngOnDestroy(): void {
    this.destroy$.next(true);
    this.destroy$.complete();
  }
  ngOnInit(): void {
    this.getRoles();
    this.onInit();
    this.defaultRole = this.fmData.roles[0].name;
  }
  password = Math.random().toString(36).slice(-8);
  formData = this.fb.group({
    sn: ['', Validators.required],
    email: ['', Validators.required],
    firstName: ['', Validators.required],
    lastName: ['', Validators.required],
    modifiedBy: ['', Validators.required],
    phoneNo: ['', Validators.required],
    solCode: ['', Validators.required],
    username: ['', Validators.required],
    workclassFk: ['', Validators.required],
    role: ['', Validators.required],
    entityId: [''],
    isTeller: ['', Validators.required],
    memberCode: [''],
    onBoardingMethod: ["Web"]
  });

  onInit() {
    this.formData = this.fb.group({
      sn: [this.fmData.sn],
      email: [this.fmData.email],
      firstName: [this.fmData.firstName],
      lastName: [this.fmData.lastName],
      modifiedBy: [this.fmData.modifiedBy],
      phoneNo: [this.fmData.phoneNo],
      solCode: [this.fmData.solCode],
      username: [this.fmData.username],
      workclassFk: [this.fmData.workclassFk],
      roleFk: [this.fmData.roles[0].id],
      isTeller: [this.fmData.isTeller],
      entityId: [this.fmData.entityId],
      memberCode: [this.fmData.memberCode],
      onBoardingMethod: [this.fmData.onBoardingMethod]
    });
    this.reloadWorkclasses(this.fmData.roles[0].id);
    this.ifSuperUserAccount(this.fmData.roles[0].name);
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
    this.authService.getRoles().subscribe(
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
  onSelect(e: any) {
    this.reloadWorkclasses(e.target.value)
  }

  reloadWorkclasses(role){
    this.loading = true;
    this.workClassAPI.getWorkClasses(role).subscribe(
      (res) => {
        this.loading = false;
        this.workClassArray = res.entity;
        console.log("Received workclass");
        console.log(this.workClassArray);
        console.log(this.formData.value);
      }, err => {
        this.loading = false;
        this.notificationAPI.alertWarning(err);
      }
    )
    // if (e.target.value == '6') {
    //   this.formData.patchValue({
    //     isTeller: 'No',
    //   });
    //   this.disableTeller = true;
    // } else {
    //   this.disableTeller = false;
    // }
  }

  ifSuperUserAccount(role: any) {
    if (role == 'ROLE_SUPERUSER') {
      this.formData.patchValue({
        isTeller: 'No',
      });
      this.disableTeller = true;
    } else {
      this.disableTeller = false;
    }
  }
  branchesCodeLookup(): void {
    const dialogRef = this.dialog.open(BranchesLookupComponent, {});
    dialogRef.afterClosed().subscribe((result) => {
      this.lookupData = result.data;
      this.solCode = this.lookupData.branchCode;
      this.formData.controls.solCode.setValue(this.lookupData.branchCode);
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
  onSubmit() {
    this.submitted = true;
    console.log("Submitting");
    console.log(this.formData.value);
    if (this.formData.valid) {
      this.loading = true;
      this.formData.patchValue({
        role: [this.formData.value.role],
      });
      this.authService
        .updateUser(this.formData.value).pipe(takeUntil(this.destroy$))
        .subscribe(
          (res) => {
            this.results = res;
            if (this.results.statusCode == 200 || this.results.statusCode == 201) {
              this.loading = false;
              this.notificationAPI.alertSuccess(this.results.message);
              this.onInit();
              this.ngZone.run(() => this.router.navigateByUrl('system/manage/user'));
            } else {
              this.loading = false;
              this.notificationAPI.alertWarning(this.results.message);
            }
          },
          (err) => {
            this.loading = false;
            this.error = err;
            this.listOfErrors = this.error.error.errors;
            this.notificationAPI.alertWarning(err);
          }
        );
    } else {
      this.notificationAPI.alertWarning("USER FORM DATA INVALID");
      this.loading = false;
    }
  }
}
