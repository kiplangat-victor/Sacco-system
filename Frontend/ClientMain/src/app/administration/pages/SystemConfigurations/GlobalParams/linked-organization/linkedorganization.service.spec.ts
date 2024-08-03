import { TestBed } from '@angular/core/testing';

import { LinkedorganizationService } from './linkedorganization.service';

describe('LinkedorganizationService', () => {
  let service: LinkedorganizationService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(LinkedorganizationService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
