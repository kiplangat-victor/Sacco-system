import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SavingsContributionMaintenanceComponent } from './savings-contribution-maintenance.component';

describe('SavingsContributionMaintenanceComponent', () => {
  let component: SavingsContributionMaintenanceComponent;
  let fixture: ComponentFixture<SavingsContributionMaintenanceComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SavingsContributionMaintenanceComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SavingsContributionMaintenanceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
