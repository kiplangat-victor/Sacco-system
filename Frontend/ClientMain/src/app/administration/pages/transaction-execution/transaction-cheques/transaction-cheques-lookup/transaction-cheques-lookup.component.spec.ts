import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TransactionChequesLookupComponent } from './transaction-cheques-lookup.component';

describe('TransactionChequesLookupComponent', () => {
  let component: TransactionChequesLookupComponent;
  let fixture: ComponentFixture<TransactionChequesLookupComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ TransactionChequesLookupComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TransactionChequesLookupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
