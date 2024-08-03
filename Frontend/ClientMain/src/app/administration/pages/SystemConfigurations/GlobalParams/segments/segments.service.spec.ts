import { TestBed } from '@angular/core/testing';

import { SegmentsService } from './segments.service';

describe('SegmentsService', () => {
  let service: SegmentsService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SegmentsService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
