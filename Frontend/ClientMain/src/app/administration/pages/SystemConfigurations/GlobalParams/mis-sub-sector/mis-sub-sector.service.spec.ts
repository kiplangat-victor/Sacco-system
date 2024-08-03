import { TestBed } from '@angular/core/testing';

import { MisSubSectorService } from './mis-sub-sector.service';

describe('MisSubSectorService', () => {
  let service: MisSubSectorService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(MisSubSectorService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
