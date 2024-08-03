import { TestBed } from '@angular/core/testing';

import { GroupMembershipService } from './group-membership.service';

describe('GroupMembershipService', () => {
  let service: GroupMembershipService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(GroupMembershipService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
