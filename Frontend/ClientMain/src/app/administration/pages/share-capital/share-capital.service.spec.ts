import { TestBed } from '@angular/core/testing';

import { ShareCapitalService } from './share-capital.service';

describe('ShareCapitalService', () => {
  let service: ShareCapitalService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ShareCapitalService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
