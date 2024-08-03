import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AccountDoumentsComponent } from './account-douments.component';

describe('AccountDoumentsComponent', () => {
  let component: AccountDoumentsComponent;
  let fixture: ComponentFixture<AccountDoumentsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AccountDoumentsComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AccountDoumentsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
