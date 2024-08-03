import { TestBed } from '@angular/core/testing';

import { InterestcodeserviceService } from './interestcodeservice.service';

describe('InterestcodeserviceService', () => {
  let service: InterestcodeserviceService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(InterestcodeserviceService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
