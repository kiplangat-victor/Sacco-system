import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MembershipLookUpComponent } from './membership-look-up.component';

describe('MembershipLookUpComponent', () => {
  let component: MembershipLookUpComponent;
  let fixture: ComponentFixture<MembershipLookUpComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ MembershipLookUpComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MembershipLookUpComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
