import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ExceptionsCodesMaintenanceComponent } from './exceptions-codes-maintenance.component';

describe('ExceptionsCodesMaintenanceComponent', () => {
  let component: ExceptionsCodesMaintenanceComponent;
  let fixture: ComponentFixture<ExceptionsCodesMaintenanceComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ExceptionsCodesMaintenanceComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ExceptionsCodesMaintenanceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
