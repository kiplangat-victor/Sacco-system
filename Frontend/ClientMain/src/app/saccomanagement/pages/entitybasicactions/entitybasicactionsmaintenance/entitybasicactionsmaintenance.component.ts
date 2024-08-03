import { Component, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { DataStoreService } from 'src/@core/helpers/data-store.service';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';

@Component({
  selector: 'app-entitybasicactionsmaintenance',
  templateUrl: './entitybasicactionsmaintenance.component.html',
  styleUrls: ['./entitybasicactionsmaintenance.component.scss']
})
export class EntitybasicactionsmaintenanceComponent implements OnInit {
  loading = false;
  submitted = false;
  functionArray: any;
  lookupData: any;
  id: any;
  existingData: boolean;
  workClassName: any;
  function_type: any;
  resData: any;

  constructor(
    public fb: FormBuilder,
    private router: Router,
    private dataStoreApi: DataStoreService,
    private notificationService: NotificationService,
  ) {
    this.functionArray = this.dataStoreApi.getActionsByPrivilege("ACCESS MANAGEMENT");
    this.functionArray = this.functionArray.filter(
      (arr: string) => arr === 'ADD' ||
        arr === 'DELETE' ||
        arr === 'INQUIRE' ||
        arr === 'MODIFY' ||
      arr ===  'VERIFY'
    );
  }
  ngOnInit(): void {
    this.lookupData = {};
  }
  formData = this.fb.group({
    function_type: ['', [Validators.required]],
  });

  onSelectFunction(event: any) {
    this.function_type = event.target.value;
    if (event.target.value == 'ADD') {
      this.existingData = false;
    } else if (event.target.value != 'ADD') {
      this.existingData = true;
    }
  }
  get f() {
    return this.formData.controls;
  }
  onSubmit() {
    this.loading = true;
    this.submitted = true;
    if (this.formData.valid) {
      this.router.navigate([`/saccomanagement/entity-basic-actions/data-view`], {
        skipLocationChange: true,
        queryParams: {
          formData: this.formData.value,
          fetchData: this.lookupData,
        },
      });
    } else {
      this.loading = false;
      this.notificationService.alertWarning("Sacco Entity Basic Actions Form Data is invalid");
    }
  }
}
