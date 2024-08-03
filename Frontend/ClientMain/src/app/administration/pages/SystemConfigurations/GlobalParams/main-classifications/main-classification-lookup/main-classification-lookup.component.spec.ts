import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MainClassificationLookupComponent } from './main-classification-lookup.component';

describe('MainClassificationLookupComponent', () => {
  let component: MainClassificationLookupComponent;
  let fixture: ComponentFixture<MainClassificationLookupComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ MainClassificationLookupComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(MainClassificationLookupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
