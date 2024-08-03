import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MembershipMaintenanceComponent } from './membership-maintenance.component';

describe('MembershipMaintenanceComponent', () => {
  let component: MembershipMaintenanceComponent;
  let fixture: ComponentFixture<MembershipMaintenanceComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ MembershipMaintenanceComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MembershipMaintenanceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
