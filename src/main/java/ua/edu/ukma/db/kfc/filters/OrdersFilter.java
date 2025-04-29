package ua.edu.ukma.db.kfc.filters;

import lombok.SneakyThrows;
import ua.edu.ukma.db.kfc.rest.model.OrdersFilterDto;
import ua.edu.ukma.db.kfc.transactions.Transaction;
import ua.edu.ukma.db.kfc.utils.TimeUtils;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OrdersFilter extends BaseFilter<OrdersFilterDto> {

    public OrdersFilter(OrdersFilterDto filter) {
        super(filter);
    }

    @Override
    protected List<String> formConditions(Map<String, String> fieldExpressionMap) {
        List<String> conditions = new ArrayList<>();
        if (filter.getId() != null)
            conditions.add(fieldExpressionMap.get("id") + " = ?");
        if (filter.getRestaurantId() != null)
            conditions.add(fieldExpressionMap.get("restaurantId") + " = ?");
        if (filter.getEmployeeUserId() != null)
            conditions.add(fieldExpressionMap.get("employeeUserId") + " = ?");
        if (filter.getClientUserId() != null)
            conditions.add(fieldExpressionMap.get("clientUserId") + " = ?");
        if (filter.getMinCost() != null)
            conditions.add(fieldExpressionMap.get("cost") + " >= ?");
        if (filter.getMaxCost() != null)
            conditions.add(fieldExpressionMap.get("cost") + " <= ?");
        if (filter.getMinDateCreated() != null)
            conditions.add(fieldExpressionMap.get("dateCreated") + " >= ?");
        if (filter.getMaxDateCreated() != null)
            conditions.add(fieldExpressionMap.get("dateCreated") + " <= ?");
        if (filter.getIsCompleted() != null)
            conditions.add(fieldExpressionMap.get("isCompleted") + " = ?");
        return conditions;
    }

    @Override
    @SneakyThrows
    protected void setWhereClauseParametersInternal(PreparedStatement st, Transaction tr, int parametersIndexOffset) {
        if (filter.getId() != null)
            st.setInt(parametersIndexOffset++, filter.getId());
        if (filter.getRestaurantId() != null)
            st.setInt(parametersIndexOffset++, filter.getRestaurantId());
        if (filter.getEmployeeUserId() != null)
            st.setInt(parametersIndexOffset++, filter.getEmployeeUserId());
        if (filter.getClientUserId() != null)
            st.setInt(parametersIndexOffset++, filter.getClientUserId());
        if (filter.getMinCost() != null)
            st.setBigDecimal(parametersIndexOffset++, filter.getMinCost());
        if (filter.getMaxCost() != null)
            st.setBigDecimal(parametersIndexOffset++, filter.getMaxCost());
        if (filter.getMinDateCreated() != null)
            st.setTimestamp(parametersIndexOffset++, TimeUtils.mapToSqlTimestamp(TimeUtils.mapToUtcDateTime(filter.getMinDateCreated())));
        if (filter.getMaxDateCreated() != null)
            st.setTimestamp(parametersIndexOffset++, TimeUtils.mapToSqlTimestamp(TimeUtils.mapToUtcDateTime(filter.getMaxDateCreated())));
        if (filter.getIsCompleted() != null)
            st.setBoolean(parametersIndexOffset, filter.getIsCompleted());
    }
}
