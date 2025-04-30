package ua.edu.ukma.db.kfc.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ua.edu.ukma.db.kfc.model.entities.OrderEntity;
import ua.edu.ukma.db.kfc.rest.model.BaseOrderDto;
import ua.edu.ukma.db.kfc.rest.model.OrderDto;
import ua.edu.ukma.db.kfc.rest.model.OrdersListDto;

@Mapper(config = MapperConfiguration.class)
public interface OrderMapper extends IListMapper<OrderEntity, OrderDto, OrdersListDto, BaseOrderDto> {

    @Mapping(target = "isCompleted", source = "completed")
    OrderDto toResponse(OrderEntity entity);
}
