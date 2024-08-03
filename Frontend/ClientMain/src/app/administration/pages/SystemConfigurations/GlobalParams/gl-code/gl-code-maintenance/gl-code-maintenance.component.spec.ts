import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GlCodeMaintenanceComponent } from './gl-code-maintenance.component';

describe('GlCodeMaintenanceComponent', () => {
  let component: GlCodeMaintenanceComponent;
  let fixture: ComponentFixture<GlCodeMaintenanceComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ GlCodeMaintenanceComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(GlCodeMaintenanceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
