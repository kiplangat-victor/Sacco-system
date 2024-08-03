import { ComponentFixture, TestBed } from '@angular/core/testing';

import { WorkClassActionsMaintenanceComponent } from './work-class-actions-maintenance.component';

describe('WorkClassActionsMaintenanceComponent', () => {
  let component: WorkClassActionsMaintenanceComponent;
  let fixture: ComponentFixture<WorkClassActionsMaintenanceComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ WorkClassActionsMaintenanceComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(WorkClassActionsMaintenanceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
