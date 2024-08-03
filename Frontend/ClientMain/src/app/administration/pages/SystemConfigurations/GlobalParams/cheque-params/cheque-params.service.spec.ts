import { TestBed } from '@angular/core/testing';

import { ChequeParamsService } from './cheque-params.service';

describe('ChequeParamsService', () => {
  let service: ChequeParamsService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ChequeParamsService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
