import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MisSubSectorComponent } from './mis-sub-sector.component';

describe('MisSubSectorComponent', () => {
  let component: MisSubSectorComponent;
  let fixture: ComponentFixture<MisSubSectorComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ MisSubSectorComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(MisSubSectorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
