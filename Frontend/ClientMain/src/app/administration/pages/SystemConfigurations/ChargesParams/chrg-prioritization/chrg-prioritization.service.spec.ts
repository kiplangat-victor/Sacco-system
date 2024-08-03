import { TestBed } from '@angular/core/testing';

import { ChrgPrioritizationService } from './chrg-prioritization.service';

describe('ChrgPrioritizationService', () => {
  let service: ChrgPrioritizationService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ChrgPrioritizationService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
