import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SavingsProductLookupComponent } from './savings-product-lookup.component';

describe('SavingsProductLookupComponent', () => {
  let component: SavingsProductLookupComponent;
  let fixture: ComponentFixture<SavingsProductLookupComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SavingsProductLookupComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SavingsProductLookupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
