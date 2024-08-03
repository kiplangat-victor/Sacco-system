import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OverDraftProductMaintenanceComponent } from './over-draft-product-maintenance.component';

describe('OverDraftProductMaintenanceComponent', () => {
  let component: OverDraftProductMaintenanceComponent;
  let fixture: ComponentFixture<OverDraftProductMaintenanceComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ OverDraftProductMaintenanceComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(OverDraftProductMaintenanceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
