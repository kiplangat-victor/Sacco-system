import { ComponentFixture, TestBed } from '@angular/core/testing';

import { WorkClassActionsComponent } from './work-class-actions.component';

describe('WorkClassActionsComponent', () => {
  let component: WorkClassActionsComponent;
  let fixture: ComponentFixture<WorkClassActionsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ WorkClassActionsComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(WorkClassActionsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
