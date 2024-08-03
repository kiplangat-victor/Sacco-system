import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OfficeProductLookUpComponent } from './office-product-look-up.component';

describe('OfficeProductLookUpComponent', () => {
  let component: OfficeProductLookUpComponent;
  let fixture: ComponentFixture<OfficeProductLookUpComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ OfficeProductLookUpComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(OfficeProductLookUpComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
