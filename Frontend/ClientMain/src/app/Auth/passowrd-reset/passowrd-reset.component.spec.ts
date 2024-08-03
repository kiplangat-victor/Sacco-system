import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PassowrdResetComponent } from './passowrd-reset.component';

describe('PassowrdResetComponent', () => {
  let component: PassowrdResetComponent;
  let fixture: ComponentFixture<PassowrdResetComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PassowrdResetComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PassowrdResetComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
