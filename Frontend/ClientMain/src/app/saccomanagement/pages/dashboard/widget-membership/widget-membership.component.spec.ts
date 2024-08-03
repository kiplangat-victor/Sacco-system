import { ComponentFixture, TestBed } from '@angular/core/testing';

import { WidgetMembershipComponent } from './widget-membership.component';

describe('WidgetMembershipComponent', () => {
  let component: WidgetMembershipComponent;
  let fixture: ComponentFixture<WidgetMembershipComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ WidgetMembershipComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(WidgetMembershipComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
