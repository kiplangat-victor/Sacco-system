import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EntityworkclassComponent } from './entityworkclass.component';

describe('EntityworkclassComponent', () => {
  let component: EntityworkclassComponent;
  let fixture: ComponentFixture<EntityworkclassComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ EntityworkclassComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EntityworkclassComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
