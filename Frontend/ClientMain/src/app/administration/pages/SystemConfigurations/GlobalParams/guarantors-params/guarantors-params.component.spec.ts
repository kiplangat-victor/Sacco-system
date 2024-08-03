import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GuarantorsParamsComponent } from './guarantors-params.component';

describe('GuarantorsParamsComponent', () => {
  let component: GuarantorsParamsComponent;
  let fixture: ComponentFixture<GuarantorsParamsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ GuarantorsParamsComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(GuarantorsParamsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
