import { Component, OnInit, OnDestroy } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Router } from '@angular/router';
import { Subject } from 'rxjs';
import { NotificationService } from 'src/@core/helpers/NotificationService/notification.service';

@Component({
  selector: 'app-general-account-statement',
  templateUrl: './general-account-statement.component.html',
  styleUrls: ['./general-account-statement.component.scss']
})
export class GeneralAccountStatementComponent implements OnInit, OnDestroy {
  loading = false;
  submitted = false;
  functionArray: any;
  error: any;
  destroy$: Subject<boolean> = new Subject<boolean>();
  function_type: any;
  constructor(
    public fb: FormBuilder,
    private router: Router,
    private notificationAPI: NotificationService
  ) { }
  ngOnDestroy() {
    this.destroy$.next(true);
    this.destroy$.complete();
  }
  ngOnInit(): void {
  }

}
