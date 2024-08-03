import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EntitytellerslookupComponent } from './entitytellerslookup.component';

describe('EntitytellerslookupComponent', () => {
  let component: EntitytellerslookupComponent;
  let fixture: ComponentFixture<EntitytellerslookupComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ EntitytellerslookupComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EntitytellerslookupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
