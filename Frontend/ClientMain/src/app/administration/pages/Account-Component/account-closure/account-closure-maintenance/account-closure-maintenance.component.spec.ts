import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AccountClosureMaintenanceComponent } from './account-closure-maintenance.component';

describe('AccountClosureMaintenanceComponent', () => {
  let component: AccountClosureMaintenanceComponent;
  let fixture: ComponentFixture<AccountClosureMaintenanceComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AccountClosureMaintenanceComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AccountClosureMaintenanceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
