import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CurrencyMaintenanceComponent } from './currency-maintenance.component';

describe('CurrencyMaintenanceComponent', () => {
  let component: CurrencyMaintenanceComponent;
  let fixture: ComponentFixture<CurrencyMaintenanceComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CurrencyMaintenanceComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CurrencyMaintenanceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
