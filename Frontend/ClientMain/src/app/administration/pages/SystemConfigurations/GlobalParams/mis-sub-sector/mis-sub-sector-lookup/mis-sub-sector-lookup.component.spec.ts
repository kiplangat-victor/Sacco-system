import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MisSubSectorLookupComponent } from './mis-sub-sector-lookup.component';

describe('MisSubSectorLookupComponent', () => {
  let component: MisSubSectorLookupComponent;
  let fixture: ComponentFixture<MisSubSectorLookupComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ MisSubSectorLookupComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(MisSubSectorLookupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
