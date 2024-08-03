import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LoanRestructureComponent } from './loan-restructure.component';

describe('LoanRestructureComponent', () => {
  let component: LoanRestructureComponent;
  let fixture: ComponentFixture<LoanRestructureComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ LoanRestructureComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(LoanRestructureComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
