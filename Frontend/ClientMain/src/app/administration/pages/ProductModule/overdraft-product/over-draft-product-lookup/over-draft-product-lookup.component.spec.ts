import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OverDraftProductLookupComponent } from './over-draft-product-lookup.component';

describe('OverDraftProductLookupComponent', () => {
  let component: OverDraftProductLookupComponent;
  let fixture: ComponentFixture<OverDraftProductLookupComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ OverDraftProductLookupComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(OverDraftProductLookupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
