import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SubSegmentMaintenanceComponent } from './sub-segment-maintenance.component';

describe('SubSegmentMaintenanceComponent', () => {
  let component: SubSegmentMaintenanceComponent;
  let fixture: ComponentFixture<SubSegmentMaintenanceComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SubSegmentMaintenanceComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SubSegmentMaintenanceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
