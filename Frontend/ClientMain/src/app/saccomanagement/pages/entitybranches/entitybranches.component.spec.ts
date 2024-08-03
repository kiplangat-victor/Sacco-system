import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EntitybranchesComponent } from './entitybranches.component';

describe('EntitybranchesComponent', () => {
  let component: EntitybranchesComponent;
  let fixture: ComponentFixture<EntitybranchesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ EntitybranchesComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EntitybranchesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
