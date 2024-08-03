import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SegmentMaintenanceComponent } from './segment-maintenance.component';

describe('SegmentMaintenanceComponent', () => {
  let component: SegmentMaintenanceComponent;
  let fixture: ComponentFixture<SegmentMaintenanceComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SegmentMaintenanceComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SegmentMaintenanceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
