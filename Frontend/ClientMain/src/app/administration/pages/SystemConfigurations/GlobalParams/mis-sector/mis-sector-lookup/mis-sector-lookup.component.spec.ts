import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MisSectorLookupComponent } from './mis-sector-lookup.component';

describe('MisSectorLookupComponent', () => {
  let component: MisSectorLookupComponent;
  let fixture: ComponentFixture<MisSectorLookupComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ MisSectorLookupComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(MisSectorLookupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
