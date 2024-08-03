import { TestBed } from '@angular/core/testing';

import { SaccoEntityService } from './sacco-entity.service';

describe('SaccoEntityService', () => {
  let service: SaccoEntityService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SaccoEntityService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
