import { Injectable } from '@angular/core';
import {
  MatSnackBarHorizontalPosition,
  MatSnackBarVerticalPosition,
  MatSnackBar,
} from '@angular/material/snack-bar';

@Injectable({
  providedIn: 'root',
})
export class DataStoreService {
  horizontalPosition: MatSnackBarHorizontalPosition = 'end';
  verticalPosition: MatSnackBarVerticalPosition = 'top';
  constructor(private _snackbar: MatSnackBar) { }
  currentUser() {
    let currentUser = JSON.parse(localStorage.getItem('auth-user'));
    return currentUser;
  }
  getRoles() {
    let data = this.currentUser().roles;
    return data;
  }
  getWokclass() {
    if (this.currentUser().workclasses != null) {
      let data = this.currentUser().workclasses
      return data;
    } else {
      this._snackbar.open('No Workclass attached to this user', 'X', {
        horizontalPosition: 'end',
        verticalPosition: 'top',
        duration: 5000,
        panelClass: ['red-snackbar', 'login-snackbar'],
      });
      return null;
    }
  }
  getPriviledges() {
    if (this.getWokclass().privileges.length > 0) {
      let data = this.getWokclass().privileges;
      let permissions = new Set<string>();
      for (let i = 0; i < data.length; i++) {
        if (data[i].selected == true) {
          permissions.add(data[i].name);
        }
      }
      return permissions;
    } else {
      let permissions = new Set<string>();
      return permissions;
    }
  }
  getActions(privilage:any) {
    if (this.getWokclass().privileges.length > 0) {
      let data = this.getWokclass().privileges;
      let filtered = data.filter(arr => arr.name === privilage);
      let basicActions = filtered[0].basicactions
      let selectedBasicActions = basicActions.filter(arr=>arr.selected === true);
      let actions = new Array()
      for(let i =0 ; i< selectedBasicActions.length; i++){
        actions.push(selectedBasicActions[i]);
      }
      return actions;
    } else {
      let actions = new Array();
      return actions;
    }
  }
  getActionsByPrivilege(privilage:any) {
    if (this.getWokclass().privileges.length > 0) {
      let data = this.getWokclass().privileges;
      let filtered = data.filter((arr: { name: any; }) => arr.name === privilage);
      let basicActions = filtered[0].basicactions
      let selectedBasicActions = basicActions.filter((arr: { selected: boolean; }) => arr.selected === true);
      let actions = new Array()
      for (let i = 0; i < selectedBasicActions.length; i++) {
        actions.push(selectedBasicActions[i].name);
      }
      return actions;
    } else {
      let actions = new Array();
      return actions;
    }
  }
}
