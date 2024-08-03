import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SavingsContributionComponent } from './savings-contribution.component';

describe('SavingsContributionComponent', () => {
  let component: SavingsContributionComponent;
  let fixture: ComponentFixture<SavingsContributionComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SavingsContributionComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SavingsContributionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
