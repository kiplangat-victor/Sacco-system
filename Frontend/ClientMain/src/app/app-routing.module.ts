import { PageError404Component } from './Auth/page-error404/page-error404.component';
import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { CanLoadModuleGuard } from 'src/@core/helpers/CanLoadModule.guard';
import { AppComponent } from './app.component';
import { CardApplicationComponent } from './administration/pages/Account-Component/card-application/card-application.component';


const routes: Routes = [
  {
    path: '',
    component: AppComponent,
    children: [
   
      {
        path: 'sso',
        loadChildren: () => import('./Auth/auth.module').then(m => m.AuthModule)
      },
      {
        path: 'system',
        loadChildren: () => import('./administration/administration.module').then(m => m.AdministrationModule),
        canLoad: [CanLoadModuleGuard],
        data: { preload: false }
      },
      {
        path: 'saccomanagement',
        loadChildren: () => import('./saccomanagement/saccomanagement.module').then(m => m.SaccomanagementModule),
        canLoad: [CanLoadModuleGuard],
        data: { preload: false }
      }
    ]
  },
  { path: "**", component: PageError404Component },
  {
path:'card/application',
component:CardApplicationComponent,
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes, { useHash: false })],
  exports: [RouterModule]
})
export class AppRoutingModule { }
