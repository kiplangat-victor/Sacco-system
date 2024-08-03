import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ClassificationLookupComponent } from './classification-lookup.component';

describe('ClassificationLookupComponent', () => {
  let component: ClassificationLookupComponent;
  let fixture: ComponentFixture<ClassificationLookupComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ClassificationLookupComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ClassificationLookupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
