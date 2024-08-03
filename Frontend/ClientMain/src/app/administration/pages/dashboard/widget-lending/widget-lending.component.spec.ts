import { ComponentFixture, TestBed } from '@angular/core/testing';

import { WidgetLendingComponent } from './widget-lending.component';

describe('WidgetLendingComponent', () => {
  let component: WidgetLendingComponent;
  let fixture: ComponentFixture<WidgetLendingComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ WidgetLendingComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(WidgetLendingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
