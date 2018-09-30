import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FrontpageComponent } from './frontpage/frontpage.component';
import { frontState } from 'app/front/front.route';
import { RouterModule } from '@angular/router';

@NgModule({
    imports: [CommonModule, RouterModule.forChild(frontState)],
    declarations: [FrontpageComponent]
})
export class FrontModule {}
