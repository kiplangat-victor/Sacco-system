import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ShareCapitalParamsComponent } from './share-capital-params.component';

describe('ShareCapitalParamsComponent', () => {
  let component: ShareCapitalParamsComponent;
  let fixture: ComponentFixture<ShareCapitalParamsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ShareCapitalParamsComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ShareCapitalParamsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
