import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSnackBar, MatSnackBarHorizontalPosition, MatSnackBarVerticalPosition } from '@angular/material/snack-bar';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { AuthService } from 'src/@core/AuthService/auth.service';
import { PassowrdResetComponent } from './passowrd-reset/passowrd-reset.component';

@Component({
  selector: 'app-user-management',
  templateUrl: './user-management.component.html',
  styleUrls: ['./user-management.component.scss']
})
export class UserManagementComponent implements OnInit, OnDestroy {
  horizontalPosition: MatSnackBarHorizontalPosition = 'end';
  verticalPosition: MatSnackBarVerticalPosition = 'top';
  displayedColumns: string[] = ['index', 'entityId', 'username', 'fullName', 'role', 'phoneNo', 'email', 'solCode', 'acctActive', 'acctLocked', 'postedTime',
    // 'verifiedBy',
    'deletedFlag', 'action'];
  dataSource!: MatTableDataSource<any>;
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;
  subscription!: Subscription;
  data: any;
  error: any;
  loading = false;
  results: any;
  dialogConfig: MatDialogConfig<any>;
  constructor(
    private dialog: MatDialog,
    private authService: AuthService,
    private fb: UntypedFormBuilder,
    private router: Router,
    private _snackBar: MatSnackBar,
  ) { }
  ngOnInit() {
    this.getData();
  }
  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }

  getData() {
    this.loading = true
    this.subscription = this.authService.allUsers().subscribe(res => {
      this.data = res;
      this.loading = false;
      this.dataSource = new MatTableDataSource(this.data);
      this.dataSource.paginator = this.paginator;
      this.dataSource.sort = this.sort;
    })
  }
  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();
    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }

  updateAccount(data: any) {
    this.router.navigate([`/system/manage/user/update/`], { skipLocationChange: true, queryParams: { formData: data } });
  }
  onResetPass(email: any) {
    this.dialogConfig = new MatDialogConfig();
    this.dialogConfig.disableClose = false;
    this.dialogConfig.autoFocus = true;
    this.dialogConfig.width = "30%";
    this.dialogConfig.data = email;
    const dialogRef = this.dialog.open(PassowrdResetComponent, this.dialogConfig);
    dialogRef.afterClosed().subscribe(res => {

    })
  }

  onLock(id: any) {
    if (window.confirm('Are you sure to Lock?')) {
      this.authService.lock(id).subscribe(res => {
        this.results = res;
        if (this.results.statusCode == 200) {
          this._snackBar.open(this.results.message, "X", {
            horizontalPosition: 'end',
            verticalPosition: 'top',
            duration: 8000,
            panelClass: ['green-snackbar', 'login-snackbar'],
          });
          this.loading = false;
          this.getData()
        } else {
          this._snackBar.open(this.results.message, "X", {
            horizontalPosition: 'end',
            verticalPosition: 'top',
            duration: 3000,
            panelClass: ['red-snackbar', 'login-snackbar'],
          });
          this.loading = false;
          this.getData()
        }
      })
    }
  }
  onUnLock(id: any) {
    if (window.confirm('Are you sure to UnLock?')) {
      this.authService.unlock(id).subscribe(res => {
        this.results = res;
        if (this.results.statusCode == 200) {
          this._snackBar.open(this.results.message, "X", {
            horizontalPosition: 'end',
            verticalPosition: 'top',
            duration: 8000,
            panelClass: ['green-snackbar', 'login-snackbar'],
          });
          this.loading = false;
          this.getData()
        } else {
          this._snackBar.open(this.results.message, "X", {
            horizontalPosition: 'end',
            verticalPosition: 'top',
            duration: 3000,
            panelClass: ['red-snackbar', 'login-snackbar'],
          });
          this.loading = false;
          this.getData()
        }
      })
    }
  }
  onUnLogout(id: any) {
    if (window.confirm('Are you sure to Logout?')) {
      this.authService.signout(id).subscribe(res => {
        this.results = res;
        if (this.results.statusCode == 200) {
          this._snackBar.open(this.results.message, "X", {
            horizontalPosition: 'end',
            verticalPosition: 'top',
            duration: 8000,
            panelClass: ['green-snackbar', 'login-snackbar'],
          });
          this.loading = false;
          this.getData()
        } else {
          this._snackBar.open(this.results.message, "X", {
            horizontalPosition: 'end',
            verticalPosition: 'top',
            duration: 3000,
            panelClass: ['red-snackbar', 'login-snackbar'],
          });
          this.loading = false;
          this.getData()
        }
      })
    }
  }
  onDelete(id: any) {
    if (window.confirm('Are you sure to delete?')) {
      this.authService.delete(id).subscribe(res => {
        this.results = res;
        if (this.results.statusCode == 200) {
          this._snackBar.open(this.results.message, "X", {
            horizontalPosition: 'end',
            verticalPosition: 'top',
            duration: 8000,
            panelClass: ['green-snackbar', 'login-snackbar'],
          });
          this.loading = false;
          this.getData()
        } else {
          this._snackBar.open(this.results.message, "X", {
            horizontalPosition: 'end',
            verticalPosition: 'top',
            duration: 3000,
            panelClass: ['red-snackbar', 'login-snackbar'],
          });
          this.loading = false;
          this.getData()
        }
      })
    }
  }
  onResetPassword(data: any) {
    this.loading = true;
    let password = Math.random().toString(36).slice(-8);
    let resetPassForm = this.fb.group({
      emailAddress: [data.email],
      password: [password],
      confirmPassword: [password],
    });
    this.subscription = this.authService.resetPassword(resetPassForm.value).subscribe(res => {
      this._snackBar.open("Successfull!", "X", {
        horizontalPosition: 'end',
        verticalPosition: 'top',
        duration: 3000,
        panelClass: ['green-snackbar', 'login-snackbar'],
      });
      this.loading = false;
    }, err => {
      this._snackBar.open(this.error, "X", {
        horizontalPosition: 'end',
        verticalPosition: 'top',
        duration: 3000,
        panelClass: ['red-snackbar', 'login-snackbar'],
      });
      this.loading = false;
    })

  }
}
