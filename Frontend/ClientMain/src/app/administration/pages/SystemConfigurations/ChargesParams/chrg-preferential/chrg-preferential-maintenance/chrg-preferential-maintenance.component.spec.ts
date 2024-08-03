import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ChrgPreferentialMaintenanceComponent } from './chrg-preferential-maintenance.component';

describe('ChrgPreferentialMaintenanceComponent', () => {
  let component: ChrgPreferentialMaintenanceComponent;
  let fixture: ComponentFixture<ChrgPreferentialMaintenanceComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ChrgPreferentialMaintenanceComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ChrgPreferentialMaintenanceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
