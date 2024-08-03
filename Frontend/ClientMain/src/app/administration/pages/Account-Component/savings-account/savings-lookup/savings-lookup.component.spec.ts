import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SavingsLookupComponent } from './savings-lookup.component';

describe('SavingsLookupComponent', () => {
  let component: SavingsLookupComponent;
  let fixture: ComponentFixture<SavingsLookupComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SavingsLookupComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SavingsLookupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
