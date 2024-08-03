import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OfficeAccountMaintenanceComponent } from './office-account-maintenance.component';

describe('OfficeAccountMaintenanceComponent', () => {
  let component: OfficeAccountMaintenanceComponent;
  let fixture: ComponentFixture<OfficeAccountMaintenanceComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ OfficeAccountMaintenanceComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(OfficeAccountMaintenanceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
