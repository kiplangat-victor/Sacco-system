import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LoansProductLookUpComponent } from './loans-product-look-up.component';

describe('LoansProductLookUpComponent', () => {
  let component: LoansProductLookUpComponent;
  let fixture: ComponentFixture<LoansProductLookUpComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ LoansProductLookUpComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(LoansProductLookUpComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
