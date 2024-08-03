import { ComponentFixture, TestBed } from '@angular/core/testing';

import { WelfareMaintenanceComponent } from './welfare-maintenance.component';

describe('WelfareMaintenanceComponent', () => {
  let component: WelfareMaintenanceComponent;
  let fixture: ComponentFixture<WelfareMaintenanceComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ WelfareMaintenanceComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(WelfareMaintenanceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
