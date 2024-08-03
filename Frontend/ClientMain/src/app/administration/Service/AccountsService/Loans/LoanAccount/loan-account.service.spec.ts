import { TestBed } from '@angular/core/testing';

import { LoanAccountService } from './loan-account.service';

describe('LoanAccountService', () => {
  let service: LoanAccountService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(LoanAccountService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
