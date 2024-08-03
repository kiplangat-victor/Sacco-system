import { TestBed } from '@angular/core/testing';

import { EntitybranchesService } from './entitybranches.service';

describe('EntitybranchesService', () => {
  let service: EntitybranchesService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(EntitybranchesService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
