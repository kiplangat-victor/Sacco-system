import { TestBed } from '@angular/core/testing';

import { TellersService } from './tellers.service';

describe('TellersService', () => {
  let service: TellersService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TellersService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
