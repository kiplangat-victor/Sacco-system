import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-page-error404',
  templateUrl: './page-error404.component.html',
  styleUrls: ['./page-error404.component.scss']
})
export class PageError404Component implements OnInit {
  date = new Date();
  constructor() { }

  ngOnInit(): void {
  }

}
