import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GlSubheadLookupComponent } from './gl-subhead-lookup.component';

describe('GlSubheadLookupComponent', () => {
  let component: GlSubheadLookupComponent;
  let fixture: ComponentFixture<GlSubheadLookupComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ GlSubheadLookupComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(GlSubheadLookupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
