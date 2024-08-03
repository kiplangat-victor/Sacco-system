import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GuarantorsParamsLookupComponent } from './guarantors-params-lookup.component';

describe('GuarantorsParamsLookupComponent', () => {
  let component: GuarantorsParamsLookupComponent;
  let fixture: ComponentFixture<GuarantorsParamsLookupComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ GuarantorsParamsLookupComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(GuarantorsParamsLookupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
