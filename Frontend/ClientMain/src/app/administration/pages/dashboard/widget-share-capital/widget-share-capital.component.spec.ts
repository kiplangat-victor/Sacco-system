import { ComponentFixture, TestBed } from '@angular/core/testing';

import { WidgetShareCapitalComponent } from './widget-share-capital.component';

describe('WidgetShareCapitalComponent', () => {
  let component: WidgetShareCapitalComponent;
  let fixture: ComponentFixture<WidgetShareCapitalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ WidgetShareCapitalComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(WidgetShareCapitalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
