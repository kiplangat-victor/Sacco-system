import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MisSectorComponent } from './mis-sector.component';

describe('MisSectorComponent', () => {
  let component: MisSectorComponent;
  let fixture: ComponentFixture<MisSectorComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ MisSectorComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(MisSectorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
