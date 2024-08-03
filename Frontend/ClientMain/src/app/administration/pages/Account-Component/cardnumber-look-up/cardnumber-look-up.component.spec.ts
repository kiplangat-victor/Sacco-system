import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CardnumberLookUpComponent } from './cardnumber-look-up.component';

describe('CardnumberLookUpComponent', () => {
  let component: CardnumberLookUpComponent;
  let fixture: ComponentFixture<CardnumberLookUpComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CardnumberLookUpComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CardnumberLookUpComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
