import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NewGroupMembershipComponent } from './new-group-membership.component';

describe('NewGroupMembershipComponent', () => {
  let component: NewGroupMembershipComponent;
  let fixture: ComponentFixture<NewGroupMembershipComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ NewGroupMembershipComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(NewGroupMembershipComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
