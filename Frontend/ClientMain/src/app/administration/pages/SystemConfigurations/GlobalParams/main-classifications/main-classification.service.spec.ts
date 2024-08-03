import { TestBed } from '@angular/core/testing';

import { MainClassificationService } from './main-classification.service';

describe('MainClassificationService', () => {
  let service: MainClassificationService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(MainClassificationService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
