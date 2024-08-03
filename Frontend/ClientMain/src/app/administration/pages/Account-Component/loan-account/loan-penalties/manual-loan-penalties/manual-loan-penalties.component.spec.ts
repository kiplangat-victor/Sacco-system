import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ManualLoanPenaltiesComponent } from './manual-loan-penalties.component';

describe('ManualLoanPenaltiesComponent', () => {
  let component: ManualLoanPenaltiesComponent;
  let fixture: ComponentFixture<ManualLoanPenaltiesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ManualLoanPenaltiesComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ManualLoanPenaltiesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
