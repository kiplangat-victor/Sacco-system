import { TestBed } from '@angular/core/testing';

import { BatchSalariesService } from './batch-salaries.service';

describe('BatchSalariesService', () => {
  let service: BatchSalariesService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(BatchSalariesService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
