import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DirectApprovalsComponent } from './direct-approvals.component';

describe('DirectApprovalsComponent', () => {
  let component: DirectApprovalsComponent;
  let fixture: ComponentFixture<DirectApprovalsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DirectApprovalsComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DirectApprovalsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
