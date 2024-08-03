import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TransactionExecutionLookupComponent } from './transaction-execution-lookup.component';

describe('TransactionExecutionLookupComponent', () => {
  let component: TransactionExecutionLookupComponent;
  let fixture: ComponentFixture<TransactionExecutionLookupComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ TransactionExecutionLookupComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TransactionExecutionLookupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
