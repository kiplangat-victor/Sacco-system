import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LiensMaintenanceComponent } from './liens-maintenance.component';

describe('LiensMaintenanceComponent', () => {
  let component: LiensMaintenanceComponent;
  let fixture: ComponentFixture<LiensMaintenanceComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ LiensMaintenanceComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(LiensMaintenanceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
