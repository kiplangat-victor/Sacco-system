import { TestBed } from '@angular/core/testing';

import { MembershipConfigService } from './membership-config.service';

describe('MembershipConfigService', () => {
  let service: MembershipConfigService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(MembershipConfigService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
