import { TestBed } from '@angular/core/testing';

import { LiensServiceService } from './liens-service.service';

describe('LiensServiceService', () => {
  let service: LiensServiceService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(LiensServiceService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
