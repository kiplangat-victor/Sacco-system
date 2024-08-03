import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GroupMembershipLookUpComponent } from './group-membership-look-up.component';

describe('GroupMembershipLookUpComponent', () => {
  let component: GroupMembershipLookUpComponent;
  let fixture: ComponentFixture<GroupMembershipLookUpComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ GroupMembershipLookUpComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(GroupMembershipLookUpComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
