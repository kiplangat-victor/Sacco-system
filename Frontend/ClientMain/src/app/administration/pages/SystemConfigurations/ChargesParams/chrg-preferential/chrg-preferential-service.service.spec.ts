import { TestBed } from '@angular/core/testing';

import { ChrgPreferentialServiceService } from './chrg-preferential-service.service';

describe('ChrgPreferentialServiceService', () => {
  let service: ChrgPreferentialServiceService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ChrgPreferentialServiceService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
