import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GlSubheadMaintenanceComponent } from './gl-subhead-maintenance.component';

describe('GlSubheadMaintenanceComponent', () => {
  let component: GlSubheadMaintenanceComponent;
  let fixture: ComponentFixture<GlSubheadMaintenanceComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ GlSubheadMaintenanceComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(GlSubheadMaintenanceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
