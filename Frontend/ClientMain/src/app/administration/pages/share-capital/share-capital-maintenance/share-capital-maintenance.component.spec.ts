import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ShareCapitalMaintenanceComponent } from './share-capital-maintenance.component';

describe('ShareCapitalMaintenanceComponent', () => {
  let component: ShareCapitalMaintenanceComponent;
  let fixture: ComponentFixture<ShareCapitalMaintenanceComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ShareCapitalMaintenanceComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ShareCapitalMaintenanceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
