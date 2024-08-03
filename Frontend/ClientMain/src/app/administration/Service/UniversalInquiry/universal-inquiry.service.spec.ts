import { TestBed } from '@angular/core/testing';

import { UniversalInquiryService } from './universal-inquiry.service';

describe('UniversalInquiryService', () => {
  let service: UniversalInquiryService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(UniversalInquiryService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
