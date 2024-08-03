import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LinkedOrganizationComponent } from './linked-organization.component';

describe('LinkedOrganizationComponent', () => {
  let component: LinkedOrganizationComponent;
  let fixture: ComponentFixture<LinkedOrganizationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ LinkedOrganizationComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(LinkedOrganizationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
