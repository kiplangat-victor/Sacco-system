import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MembershipConfigComponent } from './membership-config.component';

describe('MembershipConfigComponent', () => {
  let component: MembershipConfigComponent;
  let fixture: ComponentFixture<MembershipConfigComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ MembershipConfigComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MembershipConfigComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
