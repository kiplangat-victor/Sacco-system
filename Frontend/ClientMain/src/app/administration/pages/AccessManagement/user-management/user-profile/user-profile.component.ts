import { Component, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { AuthService } from 'src/@core/AuthService/auth.service';
import { PassowrdResetComponent } from '../passowrd-reset/passowrd-reset.component';

@Component({
  selector: 'app-user-profile',
  templateUrl: './user-profile.component.html',
  styleUrls: ['./user-profile.component.scss']
})
export class UserProfileComponent implements OnInit {
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
  currentUser: any;
  defaultRole: any;
  acid: any;
  customerType: any;
  disableTeller = false;
  dialogConfig: MatDialogConfig<any>;

  constructor(
    private fb: FormBuilder,
    private dialog: MatDialog,
    private authService: AuthService
  ) {
    this.currentUser = JSON.parse(localStorage.getItem('auth-user'));
  }
  ngOnInit(): void {
    this.getRoles();
    this.onInit();
    this.defaultRole = this.currentUser.roles[0].name;
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
    role: ['', Validators.required],
    entityId: [''],
    isTeller: ['', Validators.required],
    memberCode: ['', Validators.required],
    onBoardingMethod: ['Web']
  });

  onInit() {
    this.formData = this.fb.group({
      sn: [this.currentUser.sn],
      email: [this.currentUser.email],
      firstName: [this.currentUser.firstName],
      lastName: [this.currentUser.lastName],
      modifiedBy: [this.currentUser.modifiedBy],
      phoneNo: [this.currentUser.phoneNo],
      solCode: [this.currentUser.solCode],
      username: [this.currentUser.username],
      role: [this.currentUser.roles[0].name],
      isTeller: [this.currentUser.isTeller],
      entityId: [this.currentUser.entityId],
      memberCode: [this.currentUser.memberCode],
      onBoardingMethod: [this.currentUser.onBoardingMethod]
    });
  }
  getRoles() {
    this.authService.getRoles().subscribe(
      (res) => {
        this.rolesArray = res;
      },
      (err) => {}
    );
  }
  onResetPass(email: any) {
    this.dialogConfig = new MatDialogConfig();
    this.dialogConfig.disableClose = false;
    this.dialogConfig.autoFocus = true;
    this.dialogConfig.width = "30%";
    this.dialogConfig.data = email;
    const dialogRef = this.dialog.open(PassowrdResetComponent, this.dialogConfig);
    dialogRef.afterClosed().subscribe(res => {
      this.onInit();
    })
  }
}
