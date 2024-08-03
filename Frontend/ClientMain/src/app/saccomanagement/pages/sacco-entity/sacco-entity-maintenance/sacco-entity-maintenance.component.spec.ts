import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SaccoEntityMaintenanceComponent } from './sacco-entity-maintenance.component';

describe('SaccoEntityMaintenanceComponent', () => {
  let component: SaccoEntityMaintenanceComponent;
  let fixture: ComponentFixture<SaccoEntityMaintenanceComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SaccoEntityMaintenanceComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SaccoEntityMaintenanceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
