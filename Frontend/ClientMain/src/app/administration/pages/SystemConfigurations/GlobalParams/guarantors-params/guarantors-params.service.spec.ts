import { TestBed } from '@angular/core/testing';

import { GuarantorsParamsService } from './guarantors-params.service';

describe('GuarantorsParamsService', () => {
  let service: GuarantorsParamsService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(GuarantorsParamsService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
