import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ChequeParamsMaintenanceComponent } from './cheque-params-maintenance.component';

describe('ChequeParamsMaintenanceComponent', () => {
  let component: ChequeParamsMaintenanceComponent;
  let fixture: ComponentFixture<ChequeParamsMaintenanceComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ChequeParamsMaintenanceComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ChequeParamsMaintenanceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
