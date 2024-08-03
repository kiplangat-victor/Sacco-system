import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EntitybasicactionsComponent } from './entitybasicactions.component';

describe('EntitybasicactionsComponent', () => {
  let component: EntitybasicactionsComponent;
  let fixture: ComponentFixture<EntitybasicactionsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ EntitybasicactionsComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EntitybasicactionsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
