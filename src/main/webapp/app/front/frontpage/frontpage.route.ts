import { Route } from '@angular/router';
import { FrontpageComponent } from 'app/front/frontpage/frontpage.component';

export const frontpageRoute: Route = {
    path: '',
    component: FrontpageComponent,
    data: {
        pageTitle: 'Welcome Page'
    }
};
