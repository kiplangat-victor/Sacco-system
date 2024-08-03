import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ManualPenaltiesMaintenanceComponent } from './manual-penalties-maintenance.component';

describe('ManualPenaltiesMaintenanceComponent', () => {
  let component: ManualPenaltiesMaintenanceComponent;
  let fixture: ComponentFixture<ManualPenaltiesMaintenanceComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ManualPenaltiesMaintenanceComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ManualPenaltiesMaintenanceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
