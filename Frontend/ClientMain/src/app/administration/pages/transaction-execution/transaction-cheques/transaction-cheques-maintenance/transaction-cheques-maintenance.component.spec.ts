import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TransactionChequesMaintenanceComponent } from './transaction-cheques-maintenance.component';

describe('TransactionChequesMaintenanceComponent', () => {
  let component: TransactionChequesMaintenanceComponent;
  let fixture: ComponentFixture<TransactionChequesMaintenanceComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ TransactionChequesMaintenanceComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TransactionChequesMaintenanceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
