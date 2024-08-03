import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SaccoEntityComponent } from './sacco-entity.component';

describe('SaccoEntityComponent', () => {
  let component: SaccoEntityComponent;
  let fixture: ComponentFixture<SaccoEntityComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SaccoEntityComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SaccoEntityComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
