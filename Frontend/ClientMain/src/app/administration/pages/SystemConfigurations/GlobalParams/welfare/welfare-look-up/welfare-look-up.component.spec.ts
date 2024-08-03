import { ComponentFixture, TestBed } from '@angular/core/testing';

import { WelfareLookUpComponent } from './welfare-look-up.component';

describe('WelfareLookUpComponent', () => {
  let component: WelfareLookUpComponent;
  let fixture: ComponentFixture<WelfareLookUpComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ WelfareLookUpComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(WelfareLookUpComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
