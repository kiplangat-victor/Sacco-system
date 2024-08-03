import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BatchSalariesLookupComponent } from './batch-salaries-lookup.component';

describe('BatchSalariesLookupComponent', () => {
  let component: BatchSalariesLookupComponent;
  let fixture: ComponentFixture<BatchSalariesLookupComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ BatchSalariesLookupComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(BatchSalariesLookupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
