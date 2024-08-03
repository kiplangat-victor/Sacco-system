import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ReportMaintainanceComponent } from './report-maintainance.component';

describe('ReportMaintainanceComponent', () => {
  let component: ReportMaintainanceComponent;
  let fixture: ComponentFixture<ReportMaintainanceComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ReportMaintainanceComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ReportMaintainanceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
