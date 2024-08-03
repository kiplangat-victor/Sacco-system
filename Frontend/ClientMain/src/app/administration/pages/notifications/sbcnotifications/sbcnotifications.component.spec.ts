import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SBCNotificationsComponent } from './sbcnotifications.component';

describe('SBCNotificationsComponent', () => {
  let component: SBCNotificationsComponent;
  let fixture: ComponentFixture<SBCNotificationsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SBCNotificationsComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SBCNotificationsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
