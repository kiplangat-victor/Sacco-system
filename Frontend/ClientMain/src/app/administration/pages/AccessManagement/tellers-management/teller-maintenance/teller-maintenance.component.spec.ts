import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TellerMaintenanceComponent } from './teller-maintenance.component';

describe('TellerMaintenanceComponent', () => {
  let component: TellerMaintenanceComponent;
  let fixture: ComponentFixture<TellerMaintenanceComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ TellerMaintenanceComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TellerMaintenanceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
