import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SaccomanagementComponent } from './saccomanagement.component';

describe('SaccomanagementComponent', () => {
  let component: SaccomanagementComponent;
  let fixture: ComponentFixture<SaccomanagementComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SaccomanagementComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SaccomanagementComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
