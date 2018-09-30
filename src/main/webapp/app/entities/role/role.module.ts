import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { SrvwizSharedModule } from 'app/shared';
import { SrvwizAdminModule } from 'app/admin/admin.module';
import { SrvwizAccessModule } from 'app/entities/access/access.module';
import {
    RoleComponent,
    RoleDetailComponent,
    RoleUpdateComponent,
    RoleDeletePopupComponent,
    RoleDeleteDialogComponent,
    roleRoute,
    rolePopupRoute
} from './';
import { JhiMainComponent } from 'app/layouts';

const ENTITY_STATES = [...roleRoute, ...rolePopupRoute];

@NgModule({
    imports: [SrvwizAccessModule, SrvwizSharedModule, SrvwizAdminModule, RouterModule.forChild(ENTITY_STATES)],
    declarations: [RoleComponent, RoleDetailComponent, RoleUpdateComponent, RoleDeleteDialogComponent, RoleDeletePopupComponent],
    entryComponents: [RoleComponent, RoleUpdateComponent, RoleDeleteDialogComponent, RoleDeletePopupComponent],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class SrvwizRoleModule {}
