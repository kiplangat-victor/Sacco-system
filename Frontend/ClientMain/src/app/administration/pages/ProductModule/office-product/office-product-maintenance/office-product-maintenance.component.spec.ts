import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OfficeProductMaintenanceComponent } from './office-product-maintenance.component';

describe('OfficeProductMaintenanceComponent', () => {
  let component: OfficeProductMaintenanceComponent;
  let fixture: ComponentFixture<OfficeProductMaintenanceComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ OfficeProductMaintenanceComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(OfficeProductMaintenanceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
