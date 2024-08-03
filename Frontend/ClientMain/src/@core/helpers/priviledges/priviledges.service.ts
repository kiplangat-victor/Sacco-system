import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class PriviledgesService {

  constructor() { }
  priviledgesWorkclasses() {
    return [
      { name: 'DASHBOARD', selected: true, code: 1 },
      { name: 'CONFIGURATIONS', selected: false, code: 2 },
      { name: 'CHARGE PARAMS', selected: false, code: 3 },
      { name: 'INTEREST MAINTENANCE', selected: false, code: 4 },
      { name: 'PRODUCTS', selected: false, code: 5 },
      { name: 'MEMBERSHIP MANAGEMENT', selected: false, code: 6 },
      { name: 'ACCOUNTS MANAGEMENT', selected: false, code: 7 },
      { name: 'COLLATERALS MANAGEMENT', selected: false, code: 8 },
      { name: 'TRANSACTION MAINTENANCE', selected: false, code: 9 },
      { name: 'EOD MANAGEMENT', selected: false, code: 10 },
      { name: 'REPORTS', selected: false, code: 11 },
      { name: 'ACCESS MANAGEMENT', selected: false, code: 12 },
    ]
  }
}
