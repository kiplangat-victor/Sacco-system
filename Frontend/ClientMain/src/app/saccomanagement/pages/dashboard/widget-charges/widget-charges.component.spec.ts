import { ComponentFixture, TestBed } from '@angular/core/testing';

import { WidgetChargesComponent } from './widget-charges.component';

describe('WidgetChargesComponent', () => {
  let component: WidgetChargesComponent;
  let fixture: ComponentFixture<WidgetChargesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ WidgetChargesComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(WidgetChargesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
