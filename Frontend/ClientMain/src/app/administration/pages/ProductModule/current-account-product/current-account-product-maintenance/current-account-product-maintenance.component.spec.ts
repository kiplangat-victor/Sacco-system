import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CurrentAccountProductMaintenanceComponent } from './current-account-product-maintenance.component';

describe('CurrentAccountProductMaintenanceComponent', () => {
  let component: CurrentAccountProductMaintenanceComponent;
  let fixture: ComponentFixture<CurrentAccountProductMaintenanceComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CurrentAccountProductMaintenanceComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CurrentAccountProductMaintenanceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
