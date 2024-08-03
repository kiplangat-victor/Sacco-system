import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TellerLookupComponent } from './teller-lookup.component';

describe('TellerLookupComponent', () => {
  let component: TellerLookupComponent;
  let fixture: ComponentFixture<TellerLookupComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ TellerLookupComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TellerLookupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
