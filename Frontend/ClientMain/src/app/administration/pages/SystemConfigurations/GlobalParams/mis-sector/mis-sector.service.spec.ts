import { TestBed } from '@angular/core/testing';

import { MisSectorService } from './mis-sector.service';

describe('MisSectorService', () => {
  let service: MisSectorService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(MisSectorService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
