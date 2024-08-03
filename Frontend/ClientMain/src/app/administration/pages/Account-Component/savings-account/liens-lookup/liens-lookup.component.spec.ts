import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LiensLookupComponent } from './liens-lookup.component';

describe('LiensLookupComponent', () => {
  let component: LiensLookupComponent;
  let fixture: ComponentFixture<LiensLookupComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ LiensLookupComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(LiensLookupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
