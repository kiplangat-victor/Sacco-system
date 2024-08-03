import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UniversalMembershipLookUpComponent } from './universal-membership-look-up.component';

describe('UniversalMembershipLookUpComponent', () => {
  let component: UniversalMembershipLookUpComponent;
  let fixture: ComponentFixture<UniversalMembershipLookUpComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ UniversalMembershipLookUpComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(UniversalMembershipLookUpComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
