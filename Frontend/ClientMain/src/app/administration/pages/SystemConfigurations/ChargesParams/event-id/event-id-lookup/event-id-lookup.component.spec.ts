import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EventIdLookupComponent } from './event-id-lookup.component';

describe('EventIdLookupComponent', () => {
  let component: EventIdLookupComponent;
  let fixture: ComponentFixture<EventIdLookupComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ EventIdLookupComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(EventIdLookupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
