import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MisSectorMaintenanceComponent } from './mis-sector-maintenance.component';

describe('MisSectorMaintenanceComponent', () => {
  let component: MisSectorMaintenanceComponent;
  let fixture: ComponentFixture<MisSectorMaintenanceComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ MisSectorMaintenanceComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(MisSectorMaintenanceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
