import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TransactionChequesComponent } from './transaction-cheques.component';

describe('TransactionChequesComponent', () => {
  let component: TransactionChequesComponent;
  let fixture: ComponentFixture<TransactionChequesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ TransactionChequesComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TransactionChequesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
