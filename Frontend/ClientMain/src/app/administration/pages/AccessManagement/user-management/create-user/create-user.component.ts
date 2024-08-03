import { Component, NgZone, OnDestroy, OnInit } from '@angular/core';
import { FormGroup, UntypedFormBuilder, Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { AuthService } from 'src/@core/AuthService/auth.service';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';
import { BranchesLookupComponent } from 'src/app/administration/pages/SystemConfigurations/GlobalParams/branches/branches-lookup/branches-lookup.component';
import { MembershipService } from 'src/app/administration/Service/Membership/membership.service';
import { UniversalMembershipLookUpComponent } from '../../../MembershipComponent/universal-membership-look-up/universal-membership-look-up.component';
import { WorkClassService } from '../../work-class/work-class.service';

@Component({
  selector: 'app-create-user',
  templateUrl: './create-user.component.html',
  styleUrls: ['./create-user.component.scss'],
})
export class CreateUserComponent implements OnInit, OnDestroy {
  lookupData: any;
  solCode: any;
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
  destroy$: Subject<boolean> = new Subject<boolean>();
  disableTeller: boolean = false;

  currentUser: any; 

  formData: FormGroup;

  constructor(
    private fb: UntypedFormBuilder,
    private dialog: MatDialog,
    private router: Router,
    private ngZone: NgZone,
    private _snackBar: MatSnackBar,
    private authService: AuthService,
    private workClassAPI: WorkClassService,
    private membershipAPI: MembershipService,
    private notificationAPI: NotificationService
  ) { }
  ngOnDestroy(): void {
    this.destroy$.next(true);
    this.destroy$.complete();
  }
  ngOnInit(): void {
    this.getRoles();

    this.currentUser = JSON.parse(localStorage.getItem('auth-user'));

    this.formData = this.fb.group({
      email: ["", [Validators.required]],
      firstName: ['', [Validators.required]],
      lastName: ['', [Validators.required]],
      phoneNo: ['', [Validators.required]],
      solCode: ['', [Validators.required]],
      username: ['', [Validators.required]],
      roleFk: ['', [Validators.required]],
      workclassFk: ['', Validators.required],
      entityId: [this.currentUser.entityId],
      isTeller: ['', [Validators.required]],
      memberCode: [''],
      onBoardingMethod: ["Web"]
    });

  

    
  }
  
  get f() {
    return this.formData.controls;
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
    const dialogRef = this.dialog.open(BranchesLookupComponent, {});
    dialogRef.afterClosed().pipe(takeUntil(this.destroy$)).subscribe((result) => {
      this.lookupData = result.data;
      this.solCode = this.lookupData.branchCode;
      this.formData.controls.solCode.setValue(this.lookupData.branchCode);
    });
  }
  onSubmit() {
    this.submitted = true;
    this.loading = true;
    if (this.formData.valid) {
      this.authService
        .registerUser(this.formData.value).pipe(takeUntil(this.destroy$))
        .subscribe(
          (res) => {
            this.results = res;
            if (this.results.statusCode == 201) {
              this._snackBar.open(this.results.message, 'X', {
                horizontalPosition: 'end',
                verticalPosition: 'top',
                duration: 8000,
                panelClass: ['green-snackbar', 'login-snackbar'],
              });
              this.ngZone.run(() =>
                this.router.navigateByUrl('system/manage/user')
              );
            } else {
              this._snackBar.open(this.results.message, 'X', {
                horizontalPosition: 'end',
                verticalPosition: 'top',
                duration: 3000,
                panelClass: ['red-snackbar', 'login-snackbar'],
              });
              this.loading = false;
            }
            this.loading = false;
          },
          (err) => {
            this.loading = false;
            this.error = err;
            this.listOfErrors = this.error.error.errors;
          }
        );
    } else {
      this._snackBar.open('Invalid Form', 'X', {
        horizontalPosition: 'end',
        verticalPosition: 'top',
        duration: 3000,
        panelClass: ['red-snackbar', 'login-snackbar'],
      });
      this.loading = false;
    }
  }
}
