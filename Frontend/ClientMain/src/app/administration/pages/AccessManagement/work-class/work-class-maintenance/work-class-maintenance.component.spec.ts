import { ComponentFixture, TestBed } from '@angular/core/testing';

import { WorkClassMaintenanceComponent } from './work-class-maintenance.component';

describe('WorkClassMaintenanceComponent', () => {
  let component: WorkClassMaintenanceComponent;
  let fixture: ComponentFixture<WorkClassMaintenanceComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ WorkClassMaintenanceComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(WorkClassMaintenanceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
