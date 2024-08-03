import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BatchSalariesComponent } from './batch-salaries.component';

describe('BatchSalariesComponent', () => {
  let component: BatchSalariesComponent;
  let fixture: ComponentFixture<BatchSalariesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ BatchSalariesComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(BatchSalariesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
