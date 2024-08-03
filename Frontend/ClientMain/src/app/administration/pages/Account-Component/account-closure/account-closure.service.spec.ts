import { TestBed } from '@angular/core/testing';

import { AccountClosureService } from './account-closure.service';

describe('AccountClosureService', () => {
  let service: AccountClosureService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AccountClosureService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
