import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OverDraftProductComponent } from './over-draft-product.component';

describe('OverDraftProductComponent', () => {
  let component: OverDraftProductComponent;
  let fixture: ComponentFixture<OverDraftProductComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ OverDraftProductComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(OverDraftProductComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
