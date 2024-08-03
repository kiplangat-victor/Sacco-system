import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ChequeBookMaintenanceComponent } from './cheque-book-maintenance.component';

describe('ChequeBookMaintenanceComponent', () => {
  let component: ChequeBookMaintenanceComponent;
  let fixture: ComponentFixture<ChequeBookMaintenanceComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ChequeBookMaintenanceComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ChequeBookMaintenanceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
