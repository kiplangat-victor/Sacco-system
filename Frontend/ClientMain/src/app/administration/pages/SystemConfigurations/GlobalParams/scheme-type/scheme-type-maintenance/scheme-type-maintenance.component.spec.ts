import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SchemeTypeMaintenanceComponent } from './scheme-type-maintenance.component';

describe('SchemeTypeMaintenanceComponent', () => {
  let component: SchemeTypeMaintenanceComponent;
  let fixture: ComponentFixture<SchemeTypeMaintenanceComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SchemeTypeMaintenanceComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SchemeTypeMaintenanceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
