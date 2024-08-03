import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ChrgPrioritizationLookupComponent } from './chrg-prioritization-lookup.component';

describe('ChrgPrioritizationLookupComponent', () => {
  let component: ChrgPrioritizationLookupComponent;
  let fixture: ComponentFixture<ChrgPrioritizationLookupComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ChrgPrioritizationLookupComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ChrgPrioritizationLookupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
