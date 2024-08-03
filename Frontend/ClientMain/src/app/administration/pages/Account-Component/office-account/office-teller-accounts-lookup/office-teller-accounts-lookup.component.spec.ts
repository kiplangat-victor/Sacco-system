import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OfficeTellerAccountsLookupComponent } from './office-teller-accounts-lookup.component';

describe('OfficeTellerAccountsLookupComponent', () => {
  let component: OfficeTellerAccountsLookupComponent;
  let fixture: ComponentFixture<OfficeTellerAccountsLookupComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ OfficeTellerAccountsLookupComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(OfficeTellerAccountsLookupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
