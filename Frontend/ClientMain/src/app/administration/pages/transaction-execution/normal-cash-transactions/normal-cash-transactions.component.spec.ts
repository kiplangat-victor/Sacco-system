import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NormalCashTransactionsComponent } from './normal-cash-transactions.component';

describe('NormalCashTransactionsComponent', () => {
  let component: NormalCashTransactionsComponent;
  let fixture: ComponentFixture<NormalCashTransactionsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ NormalCashTransactionsComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(NormalCashTransactionsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
