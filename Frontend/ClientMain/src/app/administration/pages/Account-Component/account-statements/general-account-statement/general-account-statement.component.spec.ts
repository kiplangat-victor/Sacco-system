import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GeneralAccountStatementComponent } from './general-account-statement.component';

describe('GeneralAccountStatementComponent', () => {
  let component: GeneralAccountStatementComponent;
  let fixture: ComponentFixture<GeneralAccountStatementComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ GeneralAccountStatementComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(GeneralAccountStatementComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
