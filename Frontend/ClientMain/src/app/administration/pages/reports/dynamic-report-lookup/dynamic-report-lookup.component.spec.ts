import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DynamicReportLookupComponent } from './dynamic-report-lookup.component';

describe('DynamicReportLookupComponent', () => {
  let component: DynamicReportLookupComponent;
  let fixture: ComponentFixture<DynamicReportLookupComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DynamicReportLookupComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DynamicReportLookupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
