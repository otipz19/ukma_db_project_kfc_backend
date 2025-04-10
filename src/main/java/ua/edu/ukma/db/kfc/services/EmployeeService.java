package ua.edu.ukma.db.kfc.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.ws.rs.NotFoundException;
import ua.edu.ukma.db.kfc.mappers.EmployeeMapper;
import ua.edu.ukma.db.kfc.mappers.EnumsMapper;
import ua.edu.ukma.db.kfc.model.entities.EmployeeEntity;
import ua.edu.ukma.db.kfc.repositories.EmployeeRepository;
import ua.edu.ukma.db.kfc.rest.model.EmployeeDto;
import ua.edu.ukma.db.kfc.rest.model.EmployeeHiringDto;
import ua.edu.ukma.db.kfc.rest.model.UpdateEmployeeDto;
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

    public List<EmployeeDto> getAllEmployees(Integer restaurantId) {
        List<EmployeeEntity> entities = repository.findAll(restaurantId);
        validator.validForView(entities);
        return mapper.toResponse(entities);
    }

    public int hireEmployee(EmployeeHiringDto employeeHiringDto) {
        int userId = userService.create(employeeHiringDto.getUsername(), employeeHiringDto.getPassword(),
                enumsMapper.mapToRole(employeeHiringDto.getPosition()));
        EmployeeEntity employee = new EmployeeEntity();
        employee.setUserId(userId);
        employee.setPosition(enumsMapper.map(employeeHiringDto.getPosition()));
        employee.setManagerUserId(employeeHiringDto.getManagerUserId());
        employee.setManagerId(repository.findIdByUserId(employeeHiringDto.getManagerUserId()).orElse(null));
        employee.setRestaurantId(employeeHiringDto.getRestaurantId());
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
        repository.deleteByUserId(userId);
        userService.delete(userId);
    }
}
