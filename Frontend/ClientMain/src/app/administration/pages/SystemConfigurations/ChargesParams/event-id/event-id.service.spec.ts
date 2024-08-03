import { TestBed } from '@angular/core/testing';

import { EventIdService } from './event-id.service';

describe('EventIdService', () => {
  let service: EventIdService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(EventIdService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
