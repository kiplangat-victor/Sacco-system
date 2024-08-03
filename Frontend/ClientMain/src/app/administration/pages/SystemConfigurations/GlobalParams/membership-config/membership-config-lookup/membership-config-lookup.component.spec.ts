import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MembershipConfigLookupComponent } from './membership-config-lookup.component';

describe('MembershipConfigLookupComponent', () => {
  let component: MembershipConfigLookupComponent;
  let fixture: ComponentFixture<MembershipConfigLookupComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ MembershipConfigLookupComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MembershipConfigLookupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
