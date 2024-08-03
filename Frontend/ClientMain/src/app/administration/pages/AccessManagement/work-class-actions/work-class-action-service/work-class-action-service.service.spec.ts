import { TestBed } from '@angular/core/testing';

import { WorkClassActionServiceService } from './work-class-action-service.service';

describe('WorkClassActionServiceService', () => {
  let service: WorkClassActionServiceService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(WorkClassActionServiceService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
