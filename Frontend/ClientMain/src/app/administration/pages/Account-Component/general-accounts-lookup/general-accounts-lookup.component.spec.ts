import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GeneralAccountsLookupComponent } from './general-accounts-lookup.component';

describe('GeneralAccountsLookupComponent', () => {
  let component: GeneralAccountsLookupComponent;
  let fixture: ComponentFixture<GeneralAccountsLookupComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ GeneralAccountsLookupComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(GeneralAccountsLookupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
