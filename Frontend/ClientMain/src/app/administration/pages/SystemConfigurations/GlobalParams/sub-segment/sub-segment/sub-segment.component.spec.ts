import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SubSegmentComponent } from './sub-segment.component';

describe('SubSegmentComponent', () => {
  let component: SubSegmentComponent;
  let fixture: ComponentFixture<SubSegmentComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SubSegmentComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SubSegmentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
