import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TellerLookupMaintainedComponent } from './teller-lookup-maintained.component';

describe('TellerLookupMaintainedComponent', () => {
  let component: TellerLookupMaintainedComponent;
  let fixture: ComponentFixture<TellerLookupMaintainedComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ TellerLookupMaintainedComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TellerLookupMaintainedComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
