import { TestBed } from '@angular/core/testing';

import { TransactionExecutionService } from './transaction-execution.service';

describe('TransactionExecutionService', () => {
  let service: TransactionExecutionService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TransactionExecutionService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
