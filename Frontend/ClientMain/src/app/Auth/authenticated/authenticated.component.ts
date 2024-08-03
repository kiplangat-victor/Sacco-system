import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { first } from 'rxjs/operators';

import { AuthService  } from 'src/@core/AuthService/auth.service';


@Component({
  selector: 'app-authenticated',
  templateUrl: './authenticated.component.html',
  styleUrls: ['./authenticated.component.scss']
})
export class AuthenticatedComponent implements OnInit {
  activateForm!: UntypedFormGroup;
  loading = false;
  submitted = false;
  returnUrl: string | undefined;
  error = '';
  public token = "";
  constructor(
      private formBuilder: UntypedFormBuilder,
      private route: ActivatedRoute,
      private router: Router,
      private authService: AuthService
  ) {
      // redirect to home if already logged in

  }

  ngOnInit() {
    //   get snapshot
    const token = this.route.snapshot.paramMap.get('{token}');
    console.log(this.token);
      this.activateForm = this.formBuilder.group({
          token:    [this.token],
      });
  }

  // convenience getter for easy access to form fields
  get f() { return this.activateForm.controls; }
}
