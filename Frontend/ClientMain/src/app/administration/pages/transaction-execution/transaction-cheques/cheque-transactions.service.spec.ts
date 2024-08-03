import { TestBed } from '@angular/core/testing';

import { ChequeTransactionsService } from './cheque-transactions.service';

describe('ChequeTransactionsService', () => {
  let service: ChequeTransactionsService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ChequeTransactionsService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
