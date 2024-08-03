import { TestBed } from '@angular/core/testing';

import { OfficeProductService } from './office-product.service';

describe('OfficeProductService', () => {
  let service: OfficeProductService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(OfficeProductService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
