import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StandingOrdersLookupComponent } from './standing-orders-lookup.component';

describe('StandingOrdersLookupComponent', () => {
  let component: StandingOrdersLookupComponent;
  let fixture: ComponentFixture<StandingOrdersLookupComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ StandingOrdersLookupComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(StandingOrdersLookupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
