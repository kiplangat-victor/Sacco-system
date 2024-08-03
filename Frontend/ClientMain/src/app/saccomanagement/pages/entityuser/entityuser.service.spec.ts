import { TestBed } from '@angular/core/testing';

import { EntityuserService } from './entityuser.service';

describe('EntityuserService', () => {
  let service: EntityuserService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(EntityuserService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
