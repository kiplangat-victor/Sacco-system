import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GlCodeComponent } from './gl-code.component';

describe('GlCodeComponent', () => {
  let component: GlCodeComponent;
  let fixture: ComponentFixture<GlCodeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ GlCodeComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(GlCodeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
