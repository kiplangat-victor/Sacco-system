import { TestBed } from '@angular/core/testing';

import { StandingOrdersService } from './standing-orders.service';

describe('StandingOrdersService', () => {
  let service: StandingOrdersService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(StandingOrdersService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
