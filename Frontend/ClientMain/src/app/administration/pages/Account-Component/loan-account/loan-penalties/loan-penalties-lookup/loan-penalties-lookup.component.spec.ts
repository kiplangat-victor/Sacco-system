import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LoanPenaltiesLookupComponent } from './loan-penalties-lookup.component';

describe('LoanPenaltiesLookupComponent', () => {
  let component: LoanPenaltiesLookupComponent;
  let fixture: ComponentFixture<LoanPenaltiesLookupComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ LoanPenaltiesLookupComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(LoanPenaltiesLookupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
