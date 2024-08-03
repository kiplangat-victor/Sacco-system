import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GuarantorsParamsMaintenanceComponent } from './guarantors-params-maintenance.component';

describe('GuarantorsParamsMaintenanceComponent', () => {
  let component: GuarantorsParamsMaintenanceComponent;
  let fixture: ComponentFixture<GuarantorsParamsMaintenanceComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ GuarantorsParamsMaintenanceComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(GuarantorsParamsMaintenanceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
