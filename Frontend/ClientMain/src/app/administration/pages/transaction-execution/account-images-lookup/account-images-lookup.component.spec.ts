import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AccountImagesLookupComponent } from './account-images-lookup.component';

describe('AccountImagesLookupComponent', () => {
  let component: AccountImagesLookupComponent;
  let fixture: ComponentFixture<AccountImagesLookupComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AccountImagesLookupComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AccountImagesLookupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
