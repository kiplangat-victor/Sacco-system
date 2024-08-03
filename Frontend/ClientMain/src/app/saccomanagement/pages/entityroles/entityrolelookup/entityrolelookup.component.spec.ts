import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EntityrolelookupComponent } from './entityrolelookup.component';

describe('EntityrolelookupComponent', () => {
  let component: EntityrolelookupComponent;
  let fixture: ComponentFixture<EntityrolelookupComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ EntityrolelookupComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EntityrolelookupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
