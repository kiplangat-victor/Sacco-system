import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OfficeAccountsComponent } from './office-accounts.component';

describe('OfficeAccountsComponent', () => {
  let component: OfficeAccountsComponent;
  let fixture: ComponentFixture<OfficeAccountsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ OfficeAccountsComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(OfficeAccountsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
