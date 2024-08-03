import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SchemeTypeComponent } from './scheme-type.component';

describe('SchemeTypeComponent', () => {
  let component: SchemeTypeComponent;
  let fixture: ComponentFixture<SchemeTypeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SchemeTypeComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SchemeTypeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
