import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ExceptionsCodesLookupComponent } from './exceptions-codes-lookup.component';

describe('ExceptionsCodesLookupComponent', () => {
  let component: ExceptionsCodesLookupComponent;
  let fixture: ComponentFixture<ExceptionsCodesLookupComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ExceptionsCodesLookupComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ExceptionsCodesLookupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
