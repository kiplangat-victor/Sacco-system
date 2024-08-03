import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CollateralLookupComponent } from './collateral-lookup.component';

describe('CollateralLookupComponent', () => {
  let component: CollateralLookupComponent;
  let fixture: ComponentFixture<CollateralLookupComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CollateralLookupComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CollateralLookupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
