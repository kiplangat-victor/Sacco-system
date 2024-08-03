import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CurrentAccountProductLookupComponent } from './current-account-product-lookup.component';

describe('CurrentAccountProductLookupComponent', () => {
  let component: CurrentAccountProductLookupComponent;
  let fixture: ComponentFixture<CurrentAccountProductLookupComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CurrentAccountProductLookupComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CurrentAccountProductLookupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
