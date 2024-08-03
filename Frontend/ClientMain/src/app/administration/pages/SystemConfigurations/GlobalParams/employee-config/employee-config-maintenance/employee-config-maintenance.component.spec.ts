import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EmployeeConfigMaintenanceComponent } from './employee-config-maintenance.component';

describe('EmployeeConfigMaintenanceComponent', () => {
  let component: EmployeeConfigMaintenanceComponent;
  let fixture: ComponentFixture<EmployeeConfigMaintenanceComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ EmployeeConfigMaintenanceComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EmployeeConfigMaintenanceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
