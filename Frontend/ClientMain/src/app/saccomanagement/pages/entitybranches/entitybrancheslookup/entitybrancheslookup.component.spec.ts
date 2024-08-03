import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EntitybrancheslookupComponent } from './entitybrancheslookup.component';

describe('EntitybrancheslookupComponent', () => {
  let component: EntitybrancheslookupComponent;
  let fixture: ComponentFixture<EntitybrancheslookupComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ EntitybrancheslookupComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EntitybrancheslookupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
