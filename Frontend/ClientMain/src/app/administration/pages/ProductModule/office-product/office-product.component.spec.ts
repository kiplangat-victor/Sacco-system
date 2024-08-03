import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OfficeProductComponent } from './office-product.component';

describe('OfficeProductComponent', () => {
  let component: OfficeProductComponent;
  let fixture: ComponentFixture<OfficeProductComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ OfficeProductComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(OfficeProductComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
