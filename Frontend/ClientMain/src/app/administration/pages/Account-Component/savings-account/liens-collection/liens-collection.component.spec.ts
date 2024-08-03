import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LiensCollectionComponent } from './liens-collection.component';

describe('LiensCollectionComponent', () => {
  let component: LiensCollectionComponent;
  let fixture: ComponentFixture<LiensCollectionComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ LiensCollectionComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(LiensCollectionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
