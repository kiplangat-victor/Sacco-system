import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TermDepositAccountComponent } from './term-deposit-account.component';

describe('TermDepositAccountComponent', () => {
  let component: TermDepositAccountComponent;
  let fixture: ComponentFixture<TermDepositAccountComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ TermDepositAccountComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TermDepositAccountComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
