import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MainClassificationMaintenanceComponent } from './main-classification-maintenance.component';

describe('MainClassificationMaintenanceComponent', () => {
  let component: MainClassificationMaintenanceComponent;
  let fixture: ComponentFixture<MainClassificationMaintenanceComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ MainClassificationMaintenanceComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(MainClassificationMaintenanceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
