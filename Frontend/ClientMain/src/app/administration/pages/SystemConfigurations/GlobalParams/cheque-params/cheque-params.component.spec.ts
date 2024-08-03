import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ChequeParamsComponent } from './cheque-params.component';

describe('ChequeParamsComponent', () => {
  let component: ChequeParamsComponent;
  let fixture: ComponentFixture<ChequeParamsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ChequeParamsComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ChequeParamsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
