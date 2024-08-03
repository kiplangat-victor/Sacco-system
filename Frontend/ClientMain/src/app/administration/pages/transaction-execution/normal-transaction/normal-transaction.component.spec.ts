import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NormalTransactionComponent } from './normal-transaction.component';

describe('NormalTransactionComponent', () => {
  let component: NormalTransactionComponent;
  let fixture: ComponentFixture<NormalTransactionComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ NormalTransactionComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(NormalTransactionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
