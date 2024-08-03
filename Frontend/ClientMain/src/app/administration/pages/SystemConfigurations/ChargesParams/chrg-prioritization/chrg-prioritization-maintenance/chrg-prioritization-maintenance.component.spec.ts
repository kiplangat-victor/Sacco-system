import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ChrgPrioritizationMaintenanceComponent } from './chrg-prioritization-maintenance.component';

describe('ChrgPrioritizationMaintenanceComponent', () => {
  let component: ChrgPrioritizationMaintenanceComponent;
  let fixture: ComponentFixture<ChrgPrioritizationMaintenanceComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ChrgPrioritizationMaintenanceComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ChrgPrioritizationMaintenanceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
