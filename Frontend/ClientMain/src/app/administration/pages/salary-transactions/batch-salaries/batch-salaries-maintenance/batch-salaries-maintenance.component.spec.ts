import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BatchSalariesMaintenanceComponent } from './batch-salaries-maintenance.component';

describe('BatchSalariesMaintenanceComponent', () => {
  let component: BatchSalariesMaintenanceComponent;
  let fixture: ComponentFixture<BatchSalariesMaintenanceComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ BatchSalariesMaintenanceComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(BatchSalariesMaintenanceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
