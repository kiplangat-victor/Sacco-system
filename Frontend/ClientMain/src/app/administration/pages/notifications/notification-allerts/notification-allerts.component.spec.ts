import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NotificationAllertsComponent } from './notification-allerts.component';

describe('NotificationAllertsComponent', () => {
  let component: NotificationAllertsComponent;
  let fixture: ComponentFixture<NotificationAllertsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ NotificationAllertsComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(NotificationAllertsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
