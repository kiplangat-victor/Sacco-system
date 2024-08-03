import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LinkedOrganizationLookupComponent } from './linked-organization-lookup.component';

describe('LinkedOrganizationLookupComponent', () => {
  let component: LinkedOrganizationLookupComponent;
  let fixture: ComponentFixture<LinkedOrganizationLookupComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ LinkedOrganizationLookupComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(LinkedOrganizationLookupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
