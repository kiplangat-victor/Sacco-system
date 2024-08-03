import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GeneralProductLookUpComponent } from './general-product-look-up.component';

describe('GeneralProductLookUpComponent', () => {
  let component: GeneralProductLookUpComponent;
  let fixture: ComponentFixture<GeneralProductLookUpComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ GeneralProductLookUpComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(GeneralProductLookUpComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
