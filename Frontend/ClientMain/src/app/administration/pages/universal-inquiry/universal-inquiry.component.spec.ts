import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UniversalInquiryComponent } from './universal-inquiry.component';

describe('UniversalInquiryComponent', () => {
  let component: UniversalInquiryComponent;
  let fixture: ComponentFixture<UniversalInquiryComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ UniversalInquiryComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(UniversalInquiryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
