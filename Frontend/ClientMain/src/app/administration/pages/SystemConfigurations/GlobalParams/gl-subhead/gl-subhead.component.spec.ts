import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GlSubheadComponent } from './gl-subhead.component';

describe('GlSubheadComponent', () => {
  let component: GlSubheadComponent;
  let fixture: ComponentFixture<GlSubheadComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ GlSubheadComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(GlSubheadComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
