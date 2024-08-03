import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class MembershipslistService {

  constructor() { }
  membeshipList() {
    return [
      {
        value: '01',
        name: 'ORDINARY ACCOUNT'
      },
      {
        value: '02',
        name: 'BUSINESS ACCOUNT'
      },
      {
        value: '03',
        name: 'JOINT ACCOUNT'
      },
      {
        value: '04',
        name: 'CHILDREN ACCOUNT'
      },
      {
        value: '05',
        name: 'FIXED ACCOUNT'
      },
      {
        value: '06',
        name: 'SPECIAL ACCOUNT'
      },
      {
        value: '07',
        name: 'INSITUTION ACCOUNT'
      },
      {
        value: '08',
        name: 'TEMPORARY ACCOUNT'
      },
      {
        value: '09',
        name: 'STUDENT ACCOUNT'
      },
      {
        value: '10',
        name: 'STAFF ACCOUNT'
      }
    ]
  }
}
