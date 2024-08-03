import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CardApplicationMaintenanceComponent } from './card-application-maintenance.component';

describe('CardApplicationMaintenanceComponent', () => {
  let component: CardApplicationMaintenanceComponent;
  let fixture: ComponentFixture<CardApplicationMaintenanceComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CardApplicationMaintenanceComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CardApplicationMaintenanceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
