import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EntityrolesComponent } from './entityroles.component';

describe('EntityrolesComponent', () => {
  let component: EntityrolesComponent;
  let fixture: ComponentFixture<EntityrolesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ EntityrolesComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EntityrolesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
