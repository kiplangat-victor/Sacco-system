import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PageError500Component } from './page-error500.component';

describe('PageError500Component', () => {
  let component: PageError500Component;
  let fixture: ComponentFixture<PageError500Component>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PageError500Component ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PageError500Component);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
