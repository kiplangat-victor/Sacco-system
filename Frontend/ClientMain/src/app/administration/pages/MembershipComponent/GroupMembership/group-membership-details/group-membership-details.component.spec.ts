import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GroupMembershipDetailsComponent } from './group-membership-details.component';

describe('GroupMembershipDetailsComponent', () => {
  let component: GroupMembershipDetailsComponent;
  let fixture: ComponentFixture<GroupMembershipDetailsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ GroupMembershipDetailsComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(GroupMembershipDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
