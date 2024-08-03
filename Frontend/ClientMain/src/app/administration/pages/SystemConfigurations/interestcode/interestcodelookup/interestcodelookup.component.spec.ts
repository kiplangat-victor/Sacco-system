import { ComponentFixture, TestBed } from '@angular/core/testing';

import { InterestcodelookupComponent } from './interestcodelookup.component';

describe('InterestcodelookupComponent', () => {
  let component: InterestcodelookupComponent;
  let fixture: ComponentFixture<InterestcodelookupComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ InterestcodelookupComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(InterestcodelookupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
