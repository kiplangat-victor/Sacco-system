import { TestBed } from '@angular/core/testing';

import { LoanScheduleService } from './loan-schedule.service';

describe('LoanScheduleService', () => {
  let service: LoanScheduleService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(LoanScheduleService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
