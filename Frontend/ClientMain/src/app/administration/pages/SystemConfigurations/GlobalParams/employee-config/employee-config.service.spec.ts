import { TestBed } from '@angular/core/testing';

import { EmployeeConfigService } from './employee-config.service';

describe('EmployeeConfigService', () => {
  let service: EmployeeConfigService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(EmployeeConfigService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
