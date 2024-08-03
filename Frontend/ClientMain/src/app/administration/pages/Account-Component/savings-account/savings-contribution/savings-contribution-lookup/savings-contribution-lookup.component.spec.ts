import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SavingsContributionLookupComponent } from './savings-contribution-lookup.component';

describe('SavingsContributionLookupComponent', () => {
  let component: SavingsContributionLookupComponent;
  let fixture: ComponentFixture<SavingsContributionLookupComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SavingsContributionLookupComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SavingsContributionLookupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
