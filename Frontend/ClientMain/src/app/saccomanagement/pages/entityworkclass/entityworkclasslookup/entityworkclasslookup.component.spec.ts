import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EntityworkclasslookupComponent } from './entityworkclasslookup.component';

describe('EntityworkclasslookupComponent', () => {
  let component: EntityworkclasslookupComponent;
  let fixture: ComponentFixture<EntityworkclasslookupComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ EntityworkclasslookupComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EntityworkclasslookupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
