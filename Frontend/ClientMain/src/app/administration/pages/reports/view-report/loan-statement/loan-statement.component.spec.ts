import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LOanStatementComponent } from './loan-statement.component';

describe('LOanStatementComponent', () => {
  let component: LOanStatementComponent;
  let fixture: ComponentFixture<LOanStatementComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ LOanStatementComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(LOanStatementComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
