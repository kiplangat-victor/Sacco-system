import { TestBed } from '@angular/core/testing';

import { AccountsNotificationService } from './accounts-notification.service';

describe('AccountsNotificationService', () => {
  let service: AccountsNotificationService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AccountsNotificationService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
