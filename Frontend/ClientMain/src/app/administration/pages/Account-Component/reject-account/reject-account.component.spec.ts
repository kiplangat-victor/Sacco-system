import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RejectAccountComponent } from './reject-account.component';

describe('RejectAccountComponent', () => {
  let component: RejectAccountComponent;
  let fixture: ComponentFixture<RejectAccountComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ RejectAccountComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RejectAccountComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
