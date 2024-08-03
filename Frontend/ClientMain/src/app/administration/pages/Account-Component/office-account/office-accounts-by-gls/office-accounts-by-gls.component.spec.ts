import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OfficeAccountsByGlsComponent } from './office-accounts-by-gls.component';

describe('OfficeAccountsByGlsComponent', () => {
  let component: OfficeAccountsByGlsComponent;
  let fixture: ComponentFixture<OfficeAccountsByGlsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ OfficeAccountsByGlsComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(OfficeAccountsByGlsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
