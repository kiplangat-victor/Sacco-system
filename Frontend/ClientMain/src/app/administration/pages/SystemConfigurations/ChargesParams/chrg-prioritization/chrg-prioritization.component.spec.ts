import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ChrgPrioritizationComponent } from './chrg-prioritization.component';

describe('ChrgPrioritizationComponent', () => {
  let component: ChrgPrioritizationComponent;
  let fixture: ComponentFixture<ChrgPrioritizationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ChrgPrioritizationComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ChrgPrioritizationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
