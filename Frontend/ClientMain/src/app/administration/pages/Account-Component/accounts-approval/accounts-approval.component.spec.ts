import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AccountsApprovalComponent } from './accounts-approval.component';

describe('AccountsApprovalComponent', () => {
  let component: AccountsApprovalComponent;
  let fixture: ComponentFixture<AccountsApprovalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AccountsApprovalComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AccountsApprovalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
