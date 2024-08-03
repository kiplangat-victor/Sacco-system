import { TestBed } from '@angular/core/testing';

import { PrivilegeGuardGuard } from './privilege-guard.guard';

describe('PrivilegeGuardGuard', () => {
  let guard: PrivilegeGuardGuard;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    guard = TestBed.inject(PrivilegeGuardGuard);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });
});
