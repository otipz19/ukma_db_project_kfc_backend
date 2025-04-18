package ua.edu.ukma.db.kfc.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ua.edu.ukma.db.kfc.model.entities.OrderEntity;
import ua.edu.ukma.db.kfc.rest.model.BaseOrderDto;
import ua.edu.ukma.db.kfc.rest.model.OrderDto;

@Mapper(config = MapperConfiguration.class)
public interface OrderMapper extends IMapper<OrderEntity, OrderDto, BaseOrderDto> {

    @Mapping(target = "isCompleted", source = "completed")
    OrderDto toResponse(OrderEntity entity);
}
