import { TestBed } from '@angular/core/testing';

import { WelfareService } from './welfare.service';

describe('WelfareService', () => {
  let service: WelfareService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(WelfareService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
