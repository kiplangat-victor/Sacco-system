import { TestBed } from '@angular/core/testing';

import { WorkClassService } from './work-class.service';

describe('WorkClassService', () => {
  let service: WorkClassService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(WorkClassService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
