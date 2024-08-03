import { TestBed } from '@angular/core/testing';

import { SavingsInstructionsService } from './savings-instructions.service';

describe('SavingsInstructionsService', () => {
  let service: SavingsInstructionsService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SavingsInstructionsService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
