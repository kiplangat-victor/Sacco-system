import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LoansProductMaintenanceComponent } from './loans-product-maintenance.component';

describe('LoansProductMaintenanceComponent', () => {
  let component: LoansProductMaintenanceComponent;
  let fixture: ComponentFixture<LoansProductMaintenanceComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ LoansProductMaintenanceComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(LoansProductMaintenanceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
