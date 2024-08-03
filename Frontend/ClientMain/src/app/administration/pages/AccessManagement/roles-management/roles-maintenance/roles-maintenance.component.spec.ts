import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RolesMaintenanceComponent } from './roles-maintenance.component';

describe('RolesMaintenanceComponent', () => {
  let component: RolesMaintenanceComponent;
  let fixture: ComponentFixture<RolesMaintenanceComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ RolesMaintenanceComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RolesMaintenanceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
