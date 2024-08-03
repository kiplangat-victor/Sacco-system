import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EntityuserlookupComponent } from './entityuserlookup.component';

describe('EntityuserlookupComponent', () => {
  let component: EntityuserlookupComponent;
  let fixture: ComponentFixture<EntityuserlookupComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ EntityuserlookupComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EntityuserlookupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
