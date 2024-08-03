import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EventIdMaintenanceComponent } from './event-id-maintenance.component';

describe('EventIdMaintenanceComponent', () => {
  let component: EventIdMaintenanceComponent;
  let fixture: ComponentFixture<EventIdMaintenanceComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ EventIdMaintenanceComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(EventIdMaintenanceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
