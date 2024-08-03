import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CollateralMaintenanceComponent } from './collateral-maintenance.component';

describe('CollateralMaintenanceComponent', () => {
  let component: CollateralMaintenanceComponent;
  let fixture: ComponentFixture<CollateralMaintenanceComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CollateralMaintenanceComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CollateralMaintenanceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
