import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AccountStatementMaintenanceComponent } from './account-statement-maintenance.component';

describe('AccountStatementMaintenanceComponent', () => {
  let component: AccountStatementMaintenanceComponent;
  let fixture: ComponentFixture<AccountStatementMaintenanceComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AccountStatementMaintenanceComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AccountStatementMaintenanceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
