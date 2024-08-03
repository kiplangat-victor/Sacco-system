import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MisSubSectorMaintenanceComponent } from './mis-sub-sector-maintenance.component';

describe('MisSubSectorMaintenanceComponent', () => {
  let component: MisSubSectorMaintenanceComponent;
  let fixture: ComponentFixture<MisSubSectorMaintenanceComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ MisSubSectorMaintenanceComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(MisSubSectorMaintenanceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
