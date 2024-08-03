import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ShareCapitalParamsLookupComponent } from './share-capital-params-lookup.component';

describe('ShareCapitalParamsLookupComponent', () => {
  let component: ShareCapitalParamsLookupComponent;
  let fixture: ComponentFixture<ShareCapitalParamsLookupComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ShareCapitalParamsLookupComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ShareCapitalParamsLookupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
