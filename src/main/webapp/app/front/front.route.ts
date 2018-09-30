import { Routes } from '@angular/router';
import { frontpageRoute } from 'app/front/frontpage/frontpage.route';

const FRONT_ROUTES = [frontpageRoute];

export const frontState: Routes = [
    {
        path: '',
        children: FRONT_ROUTES
    }
];
