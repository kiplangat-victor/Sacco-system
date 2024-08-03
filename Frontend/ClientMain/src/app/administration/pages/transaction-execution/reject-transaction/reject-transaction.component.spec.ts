import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RejectTransactionComponent } from './reject-transaction.component';

describe('RejectTransactionComponent', () => {
  let component: RejectTransactionComponent;
  let fixture: ComponentFixture<RejectTransactionComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ RejectTransactionComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RejectTransactionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
