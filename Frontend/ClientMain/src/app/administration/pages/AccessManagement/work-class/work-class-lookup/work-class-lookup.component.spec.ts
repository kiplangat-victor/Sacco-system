import { ComponentFixture, TestBed } from '@angular/core/testing';

import { WorkClassLookupComponent } from './work-class-lookup.component';

describe('WorkClassLookupComponent', () => {
  let component: WorkClassLookupComponent;
  let fixture: ComponentFixture<WorkClassLookupComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ WorkClassLookupComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(WorkClassLookupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
