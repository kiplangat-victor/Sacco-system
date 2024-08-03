import { ComponentFixture, TestBed } from '@angular/core/testing';

import { KratariffComponent } from './kratariff.component';

describe('KratariffComponent', () => {
  let component: KratariffComponent;
  let fixture: ComponentFixture<KratariffComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ KratariffComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(KratariffComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
