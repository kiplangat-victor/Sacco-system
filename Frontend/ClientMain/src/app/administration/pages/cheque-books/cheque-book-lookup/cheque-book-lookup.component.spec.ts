import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ChequeBookLookupComponent } from './cheque-book-lookup.component';

describe('ChequeBookLookupComponent', () => {
  let component: ChequeBookLookupComponent;
  let fixture: ComponentFixture<ChequeBookLookupComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ChequeBookLookupComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ChequeBookLookupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
