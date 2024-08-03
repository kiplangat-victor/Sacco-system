import { ComponentFixture, TestBed } from '@angular/core/testing';

import { WorkClassComponent } from './work-class.component';

describe('WorkClassComponent', () => {
  let component: WorkClassComponent;
  let fixture: ComponentFixture<WorkClassComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ WorkClassComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(WorkClassComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
