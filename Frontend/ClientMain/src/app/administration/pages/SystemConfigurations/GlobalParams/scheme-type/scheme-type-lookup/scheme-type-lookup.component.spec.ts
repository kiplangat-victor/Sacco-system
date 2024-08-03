import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SchemeTypeLookupComponent } from './scheme-type-lookup.component';

describe('SchemeTypeLookupComponent', () => {
  let component: SchemeTypeLookupComponent;
  let fixture: ComponentFixture<SchemeTypeLookupComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SchemeTypeLookupComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SchemeTypeLookupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
