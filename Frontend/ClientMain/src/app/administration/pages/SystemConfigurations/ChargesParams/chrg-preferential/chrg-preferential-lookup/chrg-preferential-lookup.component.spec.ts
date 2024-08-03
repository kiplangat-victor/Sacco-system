import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ChrgPreferentialLookupComponent } from './chrg-preferential-lookup.component';

describe('ChrgPreferentialLookupComponent', () => {
  let component: ChrgPreferentialLookupComponent;
  let fixture: ComponentFixture<ChrgPreferentialLookupComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ChrgPreferentialLookupComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ChrgPreferentialLookupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
