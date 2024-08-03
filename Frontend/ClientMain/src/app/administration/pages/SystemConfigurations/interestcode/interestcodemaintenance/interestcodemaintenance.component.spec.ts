import { ComponentFixture, TestBed } from '@angular/core/testing';

import { InterestcodemaintenanceComponent } from './interestcodemaintenance.component';

describe('InterestcodemaintenanceComponent', () => {
  let component: InterestcodemaintenanceComponent;
  let fixture: ComponentFixture<InterestcodemaintenanceComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ InterestcodemaintenanceComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(InterestcodemaintenanceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
