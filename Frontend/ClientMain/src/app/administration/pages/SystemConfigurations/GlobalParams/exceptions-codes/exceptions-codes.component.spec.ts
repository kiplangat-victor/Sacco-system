import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ExceptionsCodesComponent } from './exceptions-codes.component';

describe('ExceptionsCodesComponent', () => {
  let component: ExceptionsCodesComponent;
  let fixture: ComponentFixture<ExceptionsCodesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ExceptionsCodesComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ExceptionsCodesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
