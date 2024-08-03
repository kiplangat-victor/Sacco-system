import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SubSegmentLookupComponent } from './sub-segment-lookup.component';

describe('SubSegmentLookupComponent', () => {
  let component: SubSegmentLookupComponent;
  let fixture: ComponentFixture<SubSegmentLookupComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SubSegmentLookupComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SubSegmentLookupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
