import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UnpostedChequesComponent } from './unposted-cheques.component';

describe('UnpostedChequesComponent', () => {
  let component: UnpostedChequesComponent;
  let fixture: ComponentFixture<UnpostedChequesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ UnpostedChequesComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(UnpostedChequesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
