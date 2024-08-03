import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TermDepositAccountMaintenanceComponent } from './term-deposit-account-maintenance.component';

describe('TermDepositAccountMaintenanceComponent', () => {
  let component: TermDepositAccountMaintenanceComponent;
  let fixture: ComponentFixture<TermDepositAccountMaintenanceComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ TermDepositAccountMaintenanceComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TermDepositAccountMaintenanceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
