import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TransactionApprovalComponent } from './transaction-approval.component';

describe('TransactionApprovalComponent', () => {
  let component: TransactionApprovalComponent;
  let fixture: ComponentFixture<TransactionApprovalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ TransactionApprovalComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TransactionApprovalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
