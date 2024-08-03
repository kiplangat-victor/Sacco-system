import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StandingOrdersComponent } from './standing-orders.component';

describe('StandingOrdersComponent', () => {
  let component: StandingOrdersComponent;
  let fixture: ComponentFixture<StandingOrdersComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ StandingOrdersComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(StandingOrdersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
