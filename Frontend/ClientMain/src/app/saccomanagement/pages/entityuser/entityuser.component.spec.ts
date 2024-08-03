import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EntityuserComponent } from './entityuser.component';

describe('EntityuserComponent', () => {
  let component: EntityuserComponent;
  let fixture: ComponentFixture<EntityuserComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ EntityuserComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EntityuserComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
