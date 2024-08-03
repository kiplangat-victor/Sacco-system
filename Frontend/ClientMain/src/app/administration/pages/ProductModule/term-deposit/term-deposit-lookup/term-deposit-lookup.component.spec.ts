import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TermDepositLookupComponent } from './term-deposit-lookup.component';

describe('TermDepositLookupComponent', () => {
  let component: TermDepositLookupComponent;
  let fixture: ComponentFixture<TermDepositLookupComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ TermDepositLookupComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TermDepositLookupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
