import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OfficeAccountsLookUpsComponent } from './office-accounts-look-ups.component';

describe('OfficeAccountsLookUpsComponent', () => {
  let component: OfficeAccountsLookUpsComponent;
  let fixture: ComponentFixture<OfficeAccountsLookUpsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ OfficeAccountsLookUpsComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(OfficeAccountsLookUpsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
