import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OfficeAccountComponent } from './office-account.component';

describe('OfficeAccountComponent', () => {
  let component: OfficeAccountComponent;
  let fixture: ComponentFixture<OfficeAccountComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ OfficeAccountComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(OfficeAccountComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
