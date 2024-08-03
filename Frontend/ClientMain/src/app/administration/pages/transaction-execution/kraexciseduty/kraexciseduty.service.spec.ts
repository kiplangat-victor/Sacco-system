import { TestBed } from '@angular/core/testing';

import { KraexcisedutyService } from './kraexciseduty.service';

describe('KraexcisedutyService', () => {
  let service: KraexcisedutyService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(KraexcisedutyService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
