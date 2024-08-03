import { TestBed } from '@angular/core/testing';

import { ExceptionsCodesServiceService } from './exceptions-codes-service.service';

describe('ExceptionsCodesServiceService', () => {
  let service: ExceptionsCodesServiceService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ExceptionsCodesServiceService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
