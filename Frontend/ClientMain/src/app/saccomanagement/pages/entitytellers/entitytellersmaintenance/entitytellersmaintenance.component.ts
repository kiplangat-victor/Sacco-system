import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder, Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';
import { DataStoreService } from 'src/@core/helpers/data-store.service';
import { EntitytellerslookupComponent } from '../entitytellerslookup/entitytellerslookup.component';
import { TellersService } from 'src/app/administration/pages/AccessManagement/tellers-management/tellers.service';

@Component({
  selector: 'app-entitytellersmaintenance',
  templateUrl: './entitytellersmaintenance.component.html',
  styleUrls: ['./entitytellersmaintenance.component.scss']
})
export class EntitytellersmaintenanceComponent implements OnInit {
  lookupData: any;
  id: any;
  existingData: boolean;
  workClassName: any;
  function_type: any;
  resData: any;
  loading = false;
  submitted = false;
  functionArray: any;
  constructor(
    public fb: UntypedFormBuilder,
    private router: Router,
    private dialog: MatDialog,
    private tellersService: TellersService,
    private dataStoreApi: DataStoreService,
    private notificationService: NotificationService
  ) {
    this.functionArray = this.dataStoreApi.getActionsByPrivilege("ACCESS MANAGEMENT");
    this.functionArray = this.functionArray.filter(
      (arr: string) =>
        arr === 'ADD' ||
        arr === 'DELETE' ||
        arr === 'INQUIRE' ||
        arr === 'MODIFY' ||
        arr === 'VERIFY');
  }
  ngOnInit(): void {
    this.lookupData = {};
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
    this.loading = true;
    this.submitted = true;
    if (this.formData.valid) {
      if (this.function_type == 'ADD') {
        this.router.navigate([`/saccomanagement/entity-tellers/data-view`], {
          skipLocationChange: true,
          queryParams: {
            formData: this.formData.value,
            fetchData: this.lookupData,
          },
        });
      } else if (this.function_type != 'ADD') {
        this.tellersService.findById(this.formData.value.id).subscribe(
          (res) => {
            this.resData = res;
            this.loading = false;
            if (this.resData) {
              this.lookupData = this.resData;
              this.router.navigate([`/saccomanagement/entity-tellers/data-view`], {
                skipLocationChange: true,
                queryParams: {
                  formData: this.formData.value,
                  fetchData: this.lookupData,
                },
              });
            } else {
              this.loading = false;
              this.notificationService.alertWarning("TELLER ID NOT FOUND");
            }
          },
          (err) => {
            this.loading = false;
            this.notificationService.alertWarning(err);
          }
        );
      }
    } else {
      this.loading = false;
      this.notificationService.alertWarning("TELLER FORM DATA IS INVALID");
    }
  }
  maintainedTellersLookup(): void {
    const dialogRef = this.dialog.open(EntitytellerslookupComponent, {
    });
    dialogRef.afterClosed().subscribe((result) => {
      this.lookupData = result.data;
      this.id = this.lookupData.id;
      this.formData.patchValue({
        id: this.lookupData.id
      })
    });
  }
}
