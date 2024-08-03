import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HolidayMaintenanceComponent } from './holiday-maintenance.component';

describe('HolidayMaintenanceComponent', () => {
  let component: HolidayMaintenanceComponent;
  let fixture: ComponentFixture<HolidayMaintenanceComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ HolidayMaintenanceComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(HolidayMaintenanceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
