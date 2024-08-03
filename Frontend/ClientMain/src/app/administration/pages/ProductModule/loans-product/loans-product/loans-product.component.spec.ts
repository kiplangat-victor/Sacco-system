import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LoansProductComponent } from './loans-product.component';

describe('LoansProductComponent', () => {
  let component: LoansProductComponent;
  let fixture: ComponentFixture<LoansProductComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ LoansProductComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(LoansProductComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
