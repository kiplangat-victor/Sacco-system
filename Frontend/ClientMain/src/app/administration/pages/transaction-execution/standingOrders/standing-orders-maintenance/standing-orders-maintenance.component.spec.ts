import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StandingOrdersMaintenanceComponent } from './standing-orders-maintenance.component';

describe('StandingOrdersMaintenanceComponent', () => {
  let component: StandingOrdersMaintenanceComponent;
  let fixture: ComponentFixture<StandingOrdersMaintenanceComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ StandingOrdersMaintenanceComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(StandingOrdersMaintenanceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
