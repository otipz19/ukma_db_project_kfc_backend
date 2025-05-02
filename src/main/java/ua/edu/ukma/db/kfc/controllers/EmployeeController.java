package ua.edu.ukma.db.kfc.controllers;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import ua.edu.ukma.db.kfc.rest.api.EmployeeControllerApi;
import ua.edu.ukma.db.kfc.rest.model.EmployeeHiringDto;
import ua.edu.ukma.db.kfc.rest.model.EmployeesFilterDto;
import ua.edu.ukma.db.kfc.rest.model.EmployeesStatisticFilterDto;
import ua.edu.ukma.db.kfc.rest.model.UpdateEmployeeDto;
import ua.edu.ukma.db.kfc.services.EmployeeService;

@ApplicationScoped
public class EmployeeController implements EmployeeControllerApi {

    @Inject
    private EmployeeService service;

    @Override
    public Response getEmployeesByFilter(EmployeesFilterDto filter) {
        return Response.ok(service.getEmployeesByFilter(filter)).build();
    }

    @Override
    public Response getEmployeesStatisticByFilter(EmployeesStatisticFilterDto filter) {
        return Response.ok(service.getEmployeesStatisticByFilter(filter)).build();
    }

    @Override
    public Response getEmployeeByUserId(Integer userId) {
        return Response.ok(service.getEmployeeByUserId(userId)).build();
    }

    @Override
    public Response hireEmployee(EmployeeHiringDto employeeHiringDto) {
        return Response.ok(service.hireEmployee(employeeHiringDto)).build();
    }

    @Override
    public Response updateEmployeeByUserId(Integer userId, UpdateEmployeeDto updateEmployeeDto) {
        service.updateEmployeeByUserId(userId, updateEmployeeDto);
        return Response.noContent().build();
    }

    @Override
    public Response fireEmployeeByUserId(Integer userId) {
        service.fireEmployeeByUserId(userId);
        return Response.noContent().build();
    }
}
