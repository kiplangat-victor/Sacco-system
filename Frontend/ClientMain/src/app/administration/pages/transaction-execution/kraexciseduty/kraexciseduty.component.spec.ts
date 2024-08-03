import { ComponentFixture, TestBed } from '@angular/core/testing';

import { KraexcisedutyComponent } from './kraexciseduty.component';

describe('KraexcisedutyComponent', () => {
  let component: KraexcisedutyComponent;
  let fixture: ComponentFixture<KraexcisedutyComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ KraexcisedutyComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(KraexcisedutyComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
