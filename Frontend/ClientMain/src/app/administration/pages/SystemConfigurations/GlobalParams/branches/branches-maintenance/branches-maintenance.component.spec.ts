import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BranchesMaintenanceComponent } from './branches-maintenance.component';

describe('BranchesMaintenanceComponent', () => {
  let component: BranchesMaintenanceComponent;
  let fixture: ComponentFixture<BranchesMaintenanceComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ BranchesMaintenanceComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(BranchesMaintenanceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
