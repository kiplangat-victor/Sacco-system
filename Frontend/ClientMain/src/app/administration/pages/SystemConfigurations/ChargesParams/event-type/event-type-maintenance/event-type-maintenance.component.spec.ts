import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EventTypeMaintenanceComponent } from './event-type-maintenance.component';

describe('EventTypeMaintenanceComponent', () => {
  let component: EventTypeMaintenanceComponent;
  let fixture: ComponentFixture<EventTypeMaintenanceComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ EventTypeMaintenanceComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(EventTypeMaintenanceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
