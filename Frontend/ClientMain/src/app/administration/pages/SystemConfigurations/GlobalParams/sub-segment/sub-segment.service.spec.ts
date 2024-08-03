import { TestBed } from '@angular/core/testing';

import { SubSegmentService } from './sub-segment.service';

describe('SubSegmentService', () => {
  let service: SubSegmentService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SubSegmentService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
