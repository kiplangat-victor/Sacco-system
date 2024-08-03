import { TestBed } from '@angular/core/testing';

import { ManualLoanPenaltiesService } from './manual-loan-penalties.service';

describe('ManualLoanPenaltiesService', () => {
  let service: ManualLoanPenaltiesService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ManualLoanPenaltiesService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
