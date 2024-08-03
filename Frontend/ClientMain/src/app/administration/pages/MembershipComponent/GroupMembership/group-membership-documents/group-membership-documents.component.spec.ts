import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GroupMembershipDocumentsComponent } from './group-membership-documents.component';

describe('GroupMembershipDocumentsComponent', () => {
  let component: GroupMembershipDocumentsComponent;
  let fixture: ComponentFixture<GroupMembershipDocumentsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ GroupMembershipDocumentsComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(GroupMembershipDocumentsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
