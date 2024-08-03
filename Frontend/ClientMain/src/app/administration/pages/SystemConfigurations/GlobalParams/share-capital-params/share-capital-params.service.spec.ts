import { TestBed } from '@angular/core/testing';

import { ShareCapitalParamsService } from './share-capital-params.service';

describe('ShareCapitalParamsService', () => {
  let service: ShareCapitalParamsService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ShareCapitalParamsService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
