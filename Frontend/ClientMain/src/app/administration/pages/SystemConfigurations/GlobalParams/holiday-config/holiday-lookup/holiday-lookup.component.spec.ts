import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HolidayLookupComponent } from './holiday-lookup.component';

describe('HolidayLookupComponent', () => {
  let component: HolidayLookupComponent;
  let fixture: ComponentFixture<HolidayLookupComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ HolidayLookupComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(HolidayLookupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
