import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ChrgPreferentialComponent } from './chrg-preferential.component';

describe('ChrgPreferentialComponent', () => {
  let component: ChrgPreferentialComponent;
  let fixture: ComponentFixture<ChrgPreferentialComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ChrgPreferentialComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ChrgPreferentialComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
