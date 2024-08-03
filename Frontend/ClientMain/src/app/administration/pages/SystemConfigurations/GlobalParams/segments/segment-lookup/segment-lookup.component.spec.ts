import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SegmentLookupComponent } from './segment-lookup.component';

describe('SegmentLookupComponent', () => {
  let component: SegmentLookupComponent;
  let fixture: ComponentFixture<SegmentLookupComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SegmentLookupComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SegmentLookupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
