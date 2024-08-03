import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CurrentAccountProductComponent } from './current-account-product.component';

describe('CurrentAccountProductComponent', () => {
  let component: CurrentAccountProductComponent;
  let fixture: ComponentFixture<CurrentAccountProductComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CurrentAccountProductComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CurrentAccountProductComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
