import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ShareCapitalParamsMaintenanceComponent } from './share-capital-params-maintenance.component';

describe('ShareCapitalParamsMaintenanceComponent', () => {
  let component: ShareCapitalParamsMaintenanceComponent;
  let fixture: ComponentFixture<ShareCapitalParamsMaintenanceComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ShareCapitalParamsMaintenanceComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ShareCapitalParamsMaintenanceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
