import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SavingsProductComponent } from './savings-product.component';

describe('SavingsProductComponent', () => {
  let component: SavingsProductComponent;
  let fixture: ComponentFixture<SavingsProductComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SavingsProductComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SavingsProductComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
