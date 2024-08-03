import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GeneralAccountDetailsComponent } from './general-account-details.component';

describe('GeneralAccountDetailsComponent', () => {
  let component: GeneralAccountDetailsComponent;
  let fixture: ComponentFixture<GeneralAccountDetailsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ GeneralAccountDetailsComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(GeneralAccountDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
