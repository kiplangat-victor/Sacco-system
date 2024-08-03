import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TransactionExecutionMainComponent } from './transaction-execution-main.component';

describe('TransactionExecutionMainComponent', () => {
  let component: TransactionExecutionMainComponent;
  let fixture: ComponentFixture<TransactionExecutionMainComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ TransactionExecutionMainComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TransactionExecutionMainComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
