import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MembershipConfigMaintenanceComponent } from './membership-config-maintenance.component';

describe('MembershipConfigMaintenanceComponent', () => {
  let component: MembershipConfigMaintenanceComponent;
  let fixture: ComponentFixture<MembershipConfigMaintenanceComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ MembershipConfigMaintenanceComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MembershipConfigMaintenanceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
