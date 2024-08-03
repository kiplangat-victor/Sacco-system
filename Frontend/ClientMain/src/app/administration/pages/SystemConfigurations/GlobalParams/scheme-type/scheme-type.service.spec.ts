import { TestBed } from '@angular/core/testing';

import { SchemeTypeService } from './scheme-type.service';

describe('SchemeTypeService', () => {
  let service: SchemeTypeService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SchemeTypeService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
