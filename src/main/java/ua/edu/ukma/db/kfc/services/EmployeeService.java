package ua.edu.ukma.db.kfc.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.ws.rs.NotFoundException;
import ua.edu.ukma.db.kfc.filters.EmployeesFilter;
import ua.edu.ukma.db.kfc.filters.EmployeesStatisticFilter;
import ua.edu.ukma.db.kfc.mappers.EmployeeMapper;
import ua.edu.ukma.db.kfc.mappers.EnumsMapper;
import ua.edu.ukma.db.kfc.model.entities.EmployeeEntity;
import ua.edu.ukma.db.kfc.model.helper.EmployeeStatistic;
import ua.edu.ukma.db.kfc.repositories.EmployeeRepository;
import ua.edu.ukma.db.kfc.rest.model.*;
import ua.edu.ukma.db.kfc.transactions.interceptor.TransactionInterceptor;
import ua.edu.ukma.db.kfc.validators.EmployeeValidator;

import java.util.List;

@ApplicationScoped
@Interceptors(TransactionInterceptor.class)
public class EmployeeService {

    @Inject
    private UserService userService;
    @Inject
    private EmployeeRepository repository;
    @Inject
    private EmployeeMapper mapper;
    @Inject
    private EnumsMapper enumsMapper;
    @Inject
    private EmployeeValidator validator;

    public EmployeeDto getEmployeeByUserId(int userId) {
        EmployeeEntity entity = repository.findByUserId(userId).orElseThrow(NotFoundException::new);
        validator.validForView(entity);
        return mapper.toResponse(entity);
    }

    public EmployeesListDto getEmployeesByFilter(EmployeesFilterDto filterDto) {
        EmployeesFilter filter = new EmployeesFilter(filterDto, enumsMapper);
        List<EmployeeEntity> entities = repository.findByFilter(filter);
        validator.validForView(entities);
        long total = repository.countByFilter(filter);
        return mapper.toResponse(entities, total);
    }

    public EmployeesStatisticListDto getEmployeesStatisticByFilter(EmployeesStatisticFilterDto filterDto) {
        EmployeesStatisticFilter filter = new EmployeesStatisticFilter(filterDto, enumsMapper);
        List<EmployeeStatistic> statistics = repository.findStatisticByFilter(filter);
        validator.validForViewStatistics(statistics);
        long total = repository.countStatisticByFilter(filter);
        return mapper.toStatisticResponse(statistics, total);
    }

    public int hireEmployee(EmployeeHiringDto employeeHiringDto) {
        int userId = userService.create(employeeHiringDto.getUsername(), employeeHiringDto.getPassword(),
                enumsMapper.mapToRole(employeeHiringDto.getPosition()));
        EmployeeEntity employee = new EmployeeEntity();
        employee.setUserId(userId);
        mapper.toEntity(employeeHiringDto, employee);
        validator.validForCreate(employee);
        repository.save(employee);
        return userId;
    }

    public void updateEmployeeByUserId(int userId, UpdateEmployeeDto updateEmployeeDto) {
        EmployeeEntity entity = repository.findByUserId(userId).orElseThrow(NotFoundException::new);
        mapper.toEntity(updateEmployeeDto, entity);
        validator.validForUpdate(entity);
        repository.update(entity);
    }

    public void fireEmployeeByUserId(int userId) {
        EmployeeEntity entity = repository.findByUserId(userId).orElseThrow(NotFoundException::new);
        validator.validForDelete(entity);
        userService.delete(userId);
    }
}
