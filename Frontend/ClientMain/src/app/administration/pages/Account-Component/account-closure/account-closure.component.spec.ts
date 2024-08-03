import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AccountClosureComponent } from './account-closure.component';

describe('AccountClosureComponent', () => {
  let component: AccountClosureComponent;
  let fixture: ComponentFixture<AccountClosureComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AccountClosureComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AccountClosureComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
