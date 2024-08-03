import { TestBed } from '@angular/core/testing';

import { PostalCodesService } from './postal-codes.service';

describe('PostalCodesService', () => {
  let service: PostalCodesService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(PostalCodesService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
