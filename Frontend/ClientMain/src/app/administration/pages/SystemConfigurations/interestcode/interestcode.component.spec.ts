import { ComponentFixture, TestBed } from '@angular/core/testing';

import { InterestcodeComponent } from './interestcode.component';

describe('InterestcodeComponent', () => {
  let component: InterestcodeComponent;
  let fixture: ComponentFixture<InterestcodeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ InterestcodeComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(InterestcodeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
