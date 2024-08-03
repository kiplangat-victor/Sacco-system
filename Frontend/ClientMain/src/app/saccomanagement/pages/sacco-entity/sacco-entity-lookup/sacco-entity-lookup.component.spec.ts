import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SaccoEntityLookupComponent } from './sacco-entity-lookup.component';

describe('SaccoEntityLookupComponent', () => {
  let component: SaccoEntityLookupComponent;
  let fixture: ComponentFixture<SaccoEntityLookupComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SaccoEntityLookupComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SaccoEntityLookupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
