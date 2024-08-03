import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MainClassificationsComponent } from './main-classifications.component';

describe('MainClassificationsComponent', () => {
  let component: MainClassificationsComponent;
  let fixture: ComponentFixture<MainClassificationsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ MainClassificationsComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(MainClassificationsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
