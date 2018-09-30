import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Router } from '@angular/router';
import { IRole } from 'app/shared/model/role.model';
import { IWebservice } from 'app/shared/model/webservice.model';
import { WebserviceService } from 'app/entities/webservice';
import { JhiAlertService } from 'ng-jhipster';
import { AccessService } from 'app/entities/access/access.service';
import { RoleService } from 'app/entities/role/index';
import { IAccess } from 'app/shared/model/access.model';

@Component({
    selector: 'jhi-role-detail',
    templateUrl: './role-detail.component.html'
})
export class RoleDetailComponent implements OnInit {
    isSaving: boolean;
    access = <IAccess>{};
    role: IRole;
    plus = false;
    webservices: IWebservice[];
    webservice: IWebservice;
    accesses: IAccess[];
    tables: string[];
    constructor(
        private jhiAlertService: JhiAlertService,
        private accessService: AccessService,
        private roleService: RoleService,
        private webserviceService: WebserviceService,
        private activatedRoute: ActivatedRoute,
        private router: Router
    ) {}
    ngOnInit() {
        this.plus = false;
        this.activatedRoute.data.subscribe(({ role }) => {
            this.role = role;
        });
        this.access.role = this.role;
        this.webserviceService.query().subscribe(
            (res: HttpResponse<IWebservice[]>) => {
                this.webservices = res.body;
            },
            (res: HttpErrorResponse) => this.onError(res.message)
        );
        this.activatedRoute.data.subscribe(({ accessLists }) => {
            this.accesses = this.role.accessLists;
        });
    }

    save() {
        window.history.back();
    }

    previousState() {
        window.history.back();
    }

    private onError(errorMessage: string) {
        this.jhiAlertService.error(errorMessage, null, null);
    }

    onClickPlus(): void {
        this.plus = !this.plus;
    }
    gettables(id) {
        this.accessService.getTables(id).subscribe(
            (res: HttpResponse<any>) => {
                this.tables = res.body;
            },
            (res: HttpErrorResponse) => this.onError(res.message)
        );
    }
    private subscribeToSaveResponse(result: Observable<HttpResponse<IAccess>>) {
        result.subscribe((res: HttpResponse<IAccess>) => this.onSaveSuccess(res), (res: HttpErrorResponse) => this.onSaveError());
    }
    private onSaveSuccess(res: HttpResponse<IAccess>) {
        this.isSaving = false;
        console.log(res.body);
        this.access = res.body;
        this.accesses.push(this.access);
        this.access = <IAccess>{};
        this.plus = false;
        // this.previousState();
    }

    private onSaveError() {
        this.isSaving = false;
    }
    addAccess() {
        this.isSaving = true;
        if (this.access.id !== undefined) {
            this.subscribeToSaveResponse(this.accessService.update(this.access));
        } else {
            this.subscribeToSaveResponse(this.accessService.create(this.access));
        }
    }

    /* private subscribeToDeleteResponse(result: Observable<HttpResponse<any>>) {
        result.subscribe();
    } */
    deleteAccess(access: IAccess) {
        this.accessService.delete(access.id).subscribe(
            (res: HttpResponse<any>) => {
                const index = this.accesses.indexOf(access);
                this.accesses.splice(index, 1);
            },
            (res: HttpErrorResponse) => this.onError(res.message)
        );
    }
}
