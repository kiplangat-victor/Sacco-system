import { TestBed } from '@angular/core/testing';

import { MembershipslistService } from './membershipslist.service';

describe('MembershipslistService', () => {
  let service: MembershipslistService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(MembershipslistService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
