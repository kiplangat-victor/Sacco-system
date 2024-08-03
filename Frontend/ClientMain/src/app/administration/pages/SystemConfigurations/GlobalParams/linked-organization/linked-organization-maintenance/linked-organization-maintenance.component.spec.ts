import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LinkedOrganizationMaintenanceComponent } from './linked-organization-maintenance.component';

describe('LinkedOrganizationMaintenanceComponent', () => {
  let component: LinkedOrganizationMaintenanceComponent;
  let fixture: ComponentFixture<LinkedOrganizationMaintenanceComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ LinkedOrganizationMaintenanceComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(LinkedOrganizationMaintenanceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
