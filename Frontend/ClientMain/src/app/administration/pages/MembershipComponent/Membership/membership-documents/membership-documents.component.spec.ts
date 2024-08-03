import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MembershipDocumentsComponent } from './membership-documents.component';

describe('MembershipDocumentsComponent', () => {
  let component: MembershipDocumentsComponent;
  let fixture: ComponentFixture<MembershipDocumentsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ MembershipDocumentsComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MembershipDocumentsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
