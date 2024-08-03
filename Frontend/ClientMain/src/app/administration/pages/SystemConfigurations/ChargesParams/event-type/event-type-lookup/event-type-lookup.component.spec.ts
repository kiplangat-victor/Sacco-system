import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EventTypeLookupComponent } from './event-type-lookup.component';

describe('EventTypeLookupComponent', () => {
  let component: EventTypeLookupComponent;
  let fixture: ComponentFixture<EventTypeLookupComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ EventTypeLookupComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(EventTypeLookupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
