import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ForceLoanDemandsComponent } from './force-loan-demands.component';

describe('ForceLoanDemandsComponent', () => {
  let component: ForceLoanDemandsComponent;
  let fixture: ComponentFixture<ForceLoanDemandsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ForceLoanDemandsComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ForceLoanDemandsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
