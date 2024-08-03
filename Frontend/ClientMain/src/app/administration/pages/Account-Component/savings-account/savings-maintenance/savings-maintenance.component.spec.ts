import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SavingsMaintenanceComponent } from './savings-maintenance.component';

describe('SavingsMaintenanceComponent', () => {
  let component: SavingsMaintenanceComponent;
  let fixture: ComponentFixture<SavingsMaintenanceComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SavingsMaintenanceComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SavingsMaintenanceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
