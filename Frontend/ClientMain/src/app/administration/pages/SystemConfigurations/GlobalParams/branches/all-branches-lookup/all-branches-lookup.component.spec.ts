import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AllBranchesLookupComponent } from './all-branches-lookup.component';

describe('AllBranchesLookupComponent', () => {
  let component: AllBranchesLookupComponent;
  let fixture: ComponentFixture<AllBranchesLookupComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AllBranchesLookupComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AllBranchesLookupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
