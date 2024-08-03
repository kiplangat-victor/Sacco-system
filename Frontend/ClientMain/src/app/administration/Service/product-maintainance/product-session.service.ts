import { Injectable } from '@angular/core';

const PRODUCT_META_DATA = "product-meta-data";
@Injectable({
  providedIn: 'root'
})
export class ProductSessionService {

  constructor() { }

  saveProductMetaData(product) {
    sessionStorage.removeItem(PRODUCT_META_DATA);
    sessionStorage.setItem(
      PRODUCT_META_DATA,
      JSON.stringify(product)
    );
  }

  getProductMetaData() {
    return sessionStorage.getItem(PRODUCT_META_DATA);
  }

  removeProductMetaData() {
    sessionStorage.removeItem(PRODUCT_META_DATA);
  }

  
}
