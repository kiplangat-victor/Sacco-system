import { TestBed } from '@angular/core/testing';

import { LoanPayOffService } from './loan-pay-off.service';

describe('LoanPayOffService', () => {
  let service: LoanPayOffService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(LoanPayOffService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
