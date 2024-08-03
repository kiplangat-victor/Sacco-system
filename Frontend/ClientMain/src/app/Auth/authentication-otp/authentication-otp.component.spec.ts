import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AuthenticationOTPComponent } from './authentication-otp.component';

describe('AuthenticationOTPComponent', () => {
  let component: AuthenticationOTPComponent;
  let fixture: ComponentFixture<AuthenticationOTPComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AuthenticationOTPComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AuthenticationOTPComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
