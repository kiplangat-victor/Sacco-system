import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ExciseDutiesComponent } from './excise-duties.component';

describe('ExciseDutiesComponent', () => {
  let component: ExciseDutiesComponent;
  let fixture: ComponentFixture<ExciseDutiesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ExciseDutiesComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ExciseDutiesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
