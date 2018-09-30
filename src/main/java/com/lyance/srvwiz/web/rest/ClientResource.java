package com.lyance.srvwiz.web.rest;

import com.lyance.srvwiz.domain.*;
import com.lyance.srvwiz.domain.enumeration.AccessType;
import com.lyance.srvwiz.domain.enumeration.DatabaseType;
import com.lyance.srvwiz.domain.enumeration.SqlProduct;
import com.lyance.srvwiz.repository.AccessRepository;
import com.lyance.srvwiz.repository.AppRepository;
import com.lyance.srvwiz.repository.WebserviceRepository;
import com.lyance.srvwiz.service.mysqlService;
import com.lyance.srvwiz.service.postgresService;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.*;

@RestController
@RequestMapping("/api")
public class ClientResource {

    private final mysqlService ms;

    private final postgresService ps;

    private final AppRepository appRepository;

    private final WebserviceRepository webserviceRepository;

    private final AccessRepository accessRepository;

    public ClientResource(mysqlService ms, postgresService ps, AppRepository appRepository, WebserviceRepository webserviceRepository, AccessRepository accessRepository) {


        this.ms =ms;
        this.ps = ps;
        this.appRepository = appRepository;
        this.webserviceRepository = webserviceRepository;
        this.accessRepository = accessRepository;
    }

    /**
     * GET /users : get all .
     *
     * @return the ResponseEntity with status 200 (OK) and with body all users
     */
    @GetMapping("{key}/{table}")

    public ResponseEntity getAll(@PathVariable String key,@PathVariable String table) {
        System.out.println("jjjjjjjj");
        List<Map> mapList=new ArrayList<Map>();
        Optional<App> app= Optional.ofNullable(appRepository.findByApiKey(key));
        System.out.println("hiiiiii");
        if (app!=null&&app.get().isActive()&&app.get().getRole().isActive()){
            System.out.println("fiiiiiiiiind app");
            Role role=app.get().getRole();
            List<Access> accessList=  accessRepository.findByRole(role);
            System.out.println("get accesslist"+accessList);
           Access access=  accessList.stream().filter(a->a.getTableName().equals(table)&& a.getAccessType().equals(AccessType.GET)).findFirst().get();
            if (access!=null){
                System.out.println("access"+access);
                DataSource ds=access.getWebservice().getDatasource();

                if (ds.getDatabaseType().equals(DatabaseType.SQL)){
                    System.out.println("sqlllll");

                        if (ds.getDatabaseProduct().equals(SqlProduct.MYSQL)){
                            if (ms.testConnection(ds.getDatabasePath(),ds.getDbUsername(),ds.getDbPass())){
                                try {
                                    mapList= ms.findAll(table);
                                } catch (Exception e) {
                                    return ResponseEntity.badRequest().body(e);
                                }
                            }

                        }
                        else if (ds.getDatabaseProduct().equals(SqlProduct.POSTGRESQL)){
                            System.out.println("postgres");
                            if (ps.testConnection(ds.getDatabasePath(),ds.getDbUsername(),ds.getDbPass())){
                                try {
                                    mapList= ps.findAll(table);
                                } catch (Exception e) {
                                    return ResponseEntity.badRequest().body(e);
                                }
                            }

                        }


                }


            }
          //  System.out.println("noooooooooo access");

        }


        return ResponseEntity.ok(mapList);
    }

    @GetMapping("{key}/{table}/{id}")
    public ResponseEntity FindById(@PathVariable String key, @PathVariable String table,@PathVariable String id ){
        Map map=new HashMap();
        Optional<App> app= Optional.ofNullable(appRepository.findByApiKey(key));
        if (app!=null&&app.get().isActive()&&app.get().getRole().isActive()){
            Role role=app.get().getRole();
            List<Access> accessList=  accessRepository.findByRole(role);
            Access access=  accessList.stream().filter(a->a.getTableName().equals(table)&& a.getAccessType().equals(AccessType.GET)).findFirst().get();
            if (access!=null){
                System.out.println("access"+access);
                DataSource ds=access.getWebservice().getDatasource();
                if (ds.getDatabaseType().equals(DatabaseType.SQL)){
                    System.out.println("sqlllll");

                    if (ds.getDatabaseProduct().equals(SqlProduct.MYSQL)){
                        if (ms.testConnection(ds.getDatabasePath(),ds.getDbUsername(),ds.getDbPass())){
                            try {
                                map= ms.FindById(table,id);
                            } catch (Exception e) {
                                return ResponseEntity.badRequest().body(e);
                            }
                        }

                    }
                    else if (ds.getDatabaseProduct().equals(SqlProduct.POSTGRESQL)){
                        System.out.println("postgres");
                        if (ps.testConnection(ds.getDatabasePath(),ds.getDbUsername(),ds.getDbPass())){
                            try {
                                map= ps.FindById(table,id);
                            } catch (Exception e) {
                                return ResponseEntity.badRequest().body(e);
                            }
                        }

                    }


                }
            }
            //  System.out.println("noooooooooo access");

        }


        return ResponseEntity.ok(map);
    }

    @GetMapping("{key}/{table}/{column}/{columnValue}")

    public ResponseEntity getBy(@PathVariable String key,@PathVariable String table,@PathVariable String column,@PathVariable String columnValue) {
        List<Map> mapList=new ArrayList<Map>();
        Optional<App> app= Optional.ofNullable(appRepository.findByApiKey(key));
        if (app!=null&&app.get().isActive()&&app.get().getRole().isActive()){
            Role role=app.get().getRole();
            List<Access> accessList=  accessRepository.findByRole(role);
            Access access=  accessList.stream().filter(a->a.getTableName().equals(table)&& a.getAccessType().equals(AccessType.GET)).findFirst().get();
            if (access!=null){
                System.out.println("access"+access);
                DataSource ds=access.getWebservice().getDatasource();
                if (ds.getDatabaseType().equals(DatabaseType.SQL)){
                    System.out.println("sqlllll");

                    if (ds.getDatabaseProduct().equals(SqlProduct.MYSQL)){
                        if (ms.testConnection(ds.getDatabasePath(),ds.getDbUsername(),ds.getDbPass())){
                            try {
                                mapList= ms.FindOneBy(table,column,columnValue);
                            } catch (Exception e) {
                                return ResponseEntity.badRequest().body(e);
                            }
                        }

                    }
                    else if (ds.getDatabaseProduct().equals(SqlProduct.POSTGRESQL)){
                        System.out.println("postgres");
                        if (ps.testConnection(ds.getDatabasePath(),ds.getDbUsername(),ds.getDbPass())){
                            try {
                                mapList= ps.FindOneBy(table,column,columnValue);
                            } catch (Exception e) {
                                return ResponseEntity.badRequest().body(e);
                            }
                        }

                    }


                }
            }
            //  System.out.println("noooooooooo access");

        }


        return ResponseEntity.ok(mapList);
    }

    @PostMapping("{key}/{table}")
    public ResponseEntity add(@PathVariable String key, @PathVariable String table, @RequestBody Map map ){
        Optional<App> app= Optional.ofNullable(appRepository.findByApiKey(key));
        if (app!=null&&app.get().isActive()&&app.get().getRole().isActive()){
            Role role=app.get().getRole();
            List<Access> accessList=  accessRepository.findByRole(role);
            Access access=  accessList.stream().filter(a->a.getTableName().equals(table)&& a.getAccessType().equals(AccessType.POST)).findFirst().get();
            if (access!=null){
                DataSource ds=access.getWebservice().getDatasource();
                if (ds.getDatabaseType().equals(DatabaseType.SQL)){
                    System.out.println("sqlllll");

                    if (ds.getDatabaseProduct().equals(SqlProduct.MYSQL)){
                        if (ms.testConnection(ds.getDatabasePath(),ds.getDbUsername(),ds.getDbPass())){
                            try {
                                ms.Add(map,table);
                            } catch (Exception e) {
                                return ResponseEntity.badRequest().body(e);
                            }
                        }

                    }
                    else if (ds.getDatabaseProduct().equals(SqlProduct.POSTGRESQL)){
                        System.out.println("postgres");
                        if (ps.testConnection(ds.getDatabasePath(),ds.getDbUsername(),ds.getDbPass())){
                            try {
                                 ps.Add(map,table);
                            } catch (Exception e) {
                                return ResponseEntity.badRequest().body(e);
                            }
                        }

                    }


                }
            }
        }
        return ResponseEntity.ok(map);
    }

    @PutMapping("{key}/{table}")
    public ResponseEntity update(@PathVariable String key, @PathVariable String table, @RequestBody Map map ){
        Optional<App> app= Optional.ofNullable(appRepository.findByApiKey(key));
        if (app!=null&&app.get().isActive()&&app.get().getRole().isActive()){
            Role role=app.get().getRole();
            List<Access> accessList=  accessRepository.findByRole(role);
            Access access=  accessList.stream().filter(a->a.getTableName().equals(table)&& a.getAccessType().equals(AccessType.PUT)).findFirst().get();
            if (access!=null){
                DataSource ds=access.getWebservice().getDatasource();
                if (ds.getDatabaseType().equals(DatabaseType.SQL)){
                    System.out.println("sqlllll");

                    if (ds.getDatabaseProduct().equals(SqlProduct.MYSQL)){
                        if (ms.testConnection(ds.getDatabasePath(),ds.getDbUsername(),ds.getDbPass())){
                            try {
                                ms.Update(map,table);
                            } catch (Exception e) {
                                return ResponseEntity.badRequest().body(e);
                            }
                        }

                    }
                    else if (ds.getDatabaseProduct().equals(SqlProduct.POSTGRESQL)){
                        System.out.println("postgres");
                        if (ps.testConnection(ds.getDatabasePath(),ds.getDbUsername(),ds.getDbPass())){
                            try {
                                ps.Update(map,table);
                            } catch (Exception e) {
                                return ResponseEntity.badRequest().body(e);
                            }
                        }

                    }


                }
            }
        }
        return ResponseEntity.ok(map);
    }

    @DeleteMapping("{key}/{table}/{id}")
    public ResponseEntity update(@PathVariable String key, @PathVariable String table,@PathVariable String id ){
        Optional<App> app= Optional.ofNullable(appRepository.findByApiKey(key));
        if (app!=null&&app.get().isActive()&&app.get().getRole().isActive()){
            Role role=app.get().getRole();
            List<Access> accessList=  accessRepository.findByRole(role);
            Access access=  accessList.stream().filter(a->a.getTableName().equals(table)&& a.getAccessType().equals(AccessType.DELETE)).findFirst().get();
            if (access!=null){
                DataSource ds=access.getWebservice().getDatasource();
                if (ds.getDatabaseType().equals(DatabaseType.SQL)){
                    System.out.println("sqlllll");

                    if (ds.getDatabaseProduct().equals(SqlProduct.MYSQL)){
                        if (ms.testConnection(ds.getDatabasePath(),ds.getDbUsername(),ds.getDbPass())){
                            try {
                                ms.Delete(table,id);
                            } catch (Exception e) {
                                return ResponseEntity.badRequest().body(e);
                            }
                        }

                    }
                    else if (ds.getDatabaseProduct().equals(SqlProduct.POSTGRESQL)){
                        System.out.println("postgres");
                        if (ps.testConnection(ds.getDatabasePath(),ds.getDbUsername(),ds.getDbPass())){
                            try {
                                ps.Delete(table,id);
                            } catch (Exception e) {
                                return ResponseEntity.badRequest().body(e);
                            }
                        }

                    }


                }
            }
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping("tables/{wsid}")
    public ResponseEntity getTables(@PathVariable long wsid) {
        List<String> list=new ArrayList<>();
        Webservice ws=webserviceRepository.findById(wsid).get();
        if (ws!=null){
            DataSource ds=ws.getDatasource();
            if (ds.getDatabaseType().equals(DatabaseType.SQL)){
                System.out.println("sqlllll");

                if (ds.getDatabaseProduct().equals(SqlProduct.MYSQL)){
                    if (ms.testConnection(ds.getDatabasePath(),ds.getDbUsername(),ds.getDbPass())){
                        list=ms.getTables();
                    }

                }
                else if (ds.getDatabaseProduct().equals(SqlProduct.POSTGRESQL)){
                    System.out.println("postgres");
                    if (ps.testConnection(ds.getDatabasePath(),ds.getDbUsername(),ds.getDbPass())){
                        list=ps.getTables();
                    }

                }


            }

        }

        return ResponseEntity.ok(list);

    }

}
