import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SavingsProductMaintenanceComponent } from './savings-product-maintenance.component';

describe('SavingsProductMaintenanceComponent', () => {
  let component: SavingsProductMaintenanceComponent;
  let fixture: ComponentFixture<SavingsProductMaintenanceComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SavingsProductMaintenanceComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SavingsProductMaintenanceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
