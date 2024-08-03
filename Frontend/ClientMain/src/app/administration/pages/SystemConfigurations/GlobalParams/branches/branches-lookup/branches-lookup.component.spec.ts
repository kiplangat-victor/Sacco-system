import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BranchesLookupComponent } from './branches-lookup.component';

describe('BranchesLookupComponent', () => {
  let component: BranchesLookupComponent;
  let fixture: ComponentFixture<BranchesLookupComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ BranchesLookupComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(BranchesLookupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
