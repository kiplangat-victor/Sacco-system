import { TestBed } from '@angular/core/testing';

import { GlSubheadService } from './gl-subhead.service';

describe('GlSubheadService', () => {
  let service: GlSubheadService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(GlSubheadService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
