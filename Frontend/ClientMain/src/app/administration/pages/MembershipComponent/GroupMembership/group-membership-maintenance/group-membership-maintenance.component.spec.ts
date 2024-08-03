import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GroupMembershipMaintenanceComponent } from './group-membership-maintenance.component';

describe('GroupMembershipMaintenanceComponent', () => {
  let component: GroupMembershipMaintenanceComponent;
  let fixture: ComponentFixture<GroupMembershipMaintenanceComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ GroupMembershipMaintenanceComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(GroupMembershipMaintenanceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
