import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TermDepositMaintenanceComponent } from './term-deposit-maintenance.component';

describe('TermDepositMaintenanceComponent', () => {
  let component: TermDepositMaintenanceComponent;
  let fixture: ComponentFixture<TermDepositMaintenanceComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ TermDepositMaintenanceComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TermDepositMaintenanceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
