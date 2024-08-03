import { TestBed } from '@angular/core/testing';

import { SalaryChargesService } from './salary-charges.service';

describe('SalaryChargesService', () => {
  let service: SalaryChargesService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SalaryChargesService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
