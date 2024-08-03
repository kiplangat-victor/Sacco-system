import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GlCodeLookupComponent } from './gl-code-lookup.component';

describe('GlCodeLookupComponent', () => {
  let component: GlCodeLookupComponent;
  let fixture: ComponentFixture<GlCodeLookupComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ GlCodeLookupComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(GlCodeLookupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
