
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { SaccomanagementComponent } from './saccomanagement.component';
import { CanActivateModuleGuard } from 'src/@core/helpers/CanActivateModule.guard';
import { DashboardComponent } from './pages/dashboard/dashboard.component';
import { EntitybasicactionsComponent } from './pages/entitybasicactions/entitybasicactions.component';
import { EntitybasicactionsmaintenanceComponent } from './pages/entitybasicactions/entitybasicactionsmaintenance/entitybasicactionsmaintenance.component';
import { EntitybranchesComponent } from './pages/entitybranches/entitybranches.component';
import { EntitybranchmaintenanceComponent } from './pages/entitybranches/entitybranchmaintenance/entitybranchmaintenance.component';
import { EntityrolesComponent } from './pages/entityroles/entityroles.component';
import { EntityrolesmaintenanceComponent } from './pages/entityroles/entityrolesmaintenance/entityrolesmaintenance.component';
import { EntitytellersComponent } from './pages/entitytellers/entitytellers.component';
import { EntitytellersmaintenanceComponent } from './pages/entitytellers/entitytellersmaintenance/entitytellersmaintenance.component';
import { EntityuserComponent } from './pages/entityuser/entityuser.component';
import { EntityusermaintenanceComponent } from './pages/entityuser/entityusermaintenance/entityusermaintenance.component';
import { EntityworkclassComponent } from './pages/entityworkclass/entityworkclass.component';
import { EntityworkclassmaintenanceComponent } from './pages/entityworkclass/entityworkclassmaintenance/entityworkclassmaintenance.component';
import { SaccoEntityMaintenanceComponent } from './pages/sacco-entity/sacco-entity-maintenance/sacco-entity-maintenance.component';
import { SaccoEntityComponent } from './pages/sacco-entity/sacco-entity.component';

const routes: Routes = [
  {
    path: '',
    component: SaccomanagementComponent,
    children: [
      {
        path: '',
        component: DashboardComponent,
        pathMatch: 'full',
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'DASHBOARD', preload: true },
      },
      // SACCO ENTITIES
      {
        path: 'sacco-entity/maintenance',
        component: SaccoEntityMaintenanceComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'CONFIGURATIONS', preload: true }
      },
      {
        path: 'sacco-entity/data-view',
        component: SaccoEntityComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'CONFIGURATIONS', preload: true }
      },
      // ENTITY BRANCHES
      {
        path: 'entity-branches/maintenance',
        component: EntitybranchmaintenanceComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'CONFIGURATIONS', preload: true }
      },
      {
        path: 'entity-branches/data-view',
        component: EntitybranchesComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'CONFIGURATIONS', preload: true }
      },
      // ENTITY ROLES
      {
        path: 'entity-roles/maintenance',
        component: EntityrolesmaintenanceComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'ACCESS MANAGEMENT', preload: true }
      },
      {
        path: 'entity-roles/data-view',
        component: EntityrolesComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'ACCESS MANAGEMENT', preload: true }
      },
      // ENTITY PRIVILEDGES/WORKCLASS
      {
        path: 'entity-work-class/maintenance',
        component: EntityworkclassmaintenanceComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'ACCESS MANAGEMENT', preload: true }
      },
      {
        path: 'entity-work-class/data-view',
        component: EntityworkclassComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'ACCESS MANAGEMENT', preload: true }
      },
      // ENTITY BASIC ACTIONS
      {
        path: 'entity-basic-actions/maintenance',
        component: EntitybasicactionsmaintenanceComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'ACCESS MANAGEMENT', preload: true }
      },
      {
        path: 'entity-basic-actions/data-view',
        component: EntitybasicactionsComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'ACCESS MANAGEMENT', preload: true }
      },
      // ENTITY USERS
      {
        path: 'entity-users/maintenance',
        component: EntityusermaintenanceComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'ACCESS MANAGEMENT', preload: true }
      },
      {
        path: 'entity-users/data-view',
        component: EntityuserComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'ACCESS MANAGEMENT', preload: true }
      },
      // ENTITY TELLERS
      {
        path: 'entity-tellers/maintenance',
        component: EntitytellersmaintenanceComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'ACCESS MANAGEMENT', preload: true }
      },
      {
        path: 'entity-tellers/data-view',
        component: EntitytellersComponent,
        canActivate: [CanActivateModuleGuard],
        data: { permission: 'ACCESS MANAGEMENT', preload: true }
      },
    ]
  }
];
@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class SaccomanagementRoutingModule { }
