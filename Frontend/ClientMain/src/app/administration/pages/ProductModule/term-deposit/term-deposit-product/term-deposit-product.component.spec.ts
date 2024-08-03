import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TermDepositProductComponent } from './term-deposit-product.component';

describe('TermDepositProductComponent', () => {
  let component: TermDepositProductComponent;
  let fixture: ComponentFixture<TermDepositProductComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ TermDepositProductComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TermDepositProductComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
