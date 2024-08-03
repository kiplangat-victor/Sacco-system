import { TestBed } from '@angular/core/testing';

import { ExcisedutyService } from './exciseduty.service';

describe('ExcisedutyService', () => {
  let service: ExcisedutyService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ExcisedutyService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
