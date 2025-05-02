package ua.edu.ukma.db.kfc.filters;

import lombok.SneakyThrows;
import ua.edu.ukma.db.kfc.rest.model.RestaurantsStatisticFilterDto;
import ua.edu.ukma.db.kfc.transactions.Transaction;
import ua.edu.ukma.db.kfc.utils.TimeUtils;

import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class RestaurantsStatisticFilter extends BaseFilter<RestaurantsStatisticFilterDto> {

    public RestaurantsStatisticFilter(RestaurantsStatisticFilterDto filter) {
        super(filter);
    }

    @Override
    protected List<String> formWhereConditions(Map<String, String> fieldExpressionMap) {
        List<String> conditions = new ArrayList<>();
        if (filter.getAddress() != null && !filter.getAddress().isBlank()) {
            conditions.add(
                    String.format("LOWER(%s) LIKE LOWER('%%' || ? || '%%')",
                            fieldExpressionMap.get("address"))
            );
        }
        if (filter.getHasManager() != null) {
            conditions.add(
                    String.format("%s IS %s NULL",
                            fieldExpressionMap.get("managerUserId"),
                            filter.getHasManager() ? "NOT" : "")
            );
        }
        if (filter.getIsDeleted() != null)
            conditions.add(fieldExpressionMap.get("isDeleted") + " = ?");
        return conditions;
    }

    @Override
    protected List<String> formHavingConditions(Map<String, String> fieldExpressionMap) {
        List<String> conditions = new ArrayList<>();
        if (filter.getMinNumberOfOrders() != null)
            conditions.add(fieldExpressionMap.get("numberOfOrders") + " >= ?");
        if (filter.getMaxNumberOfOrders() != null)
            conditions.add(fieldExpressionMap.get("numberOfOrders") + " <= ?");
        if (filter.getMinTotalOrdersPrice() != null)
            conditions.add(fieldExpressionMap.get("totalOrdersPrice") + " >= ?");
        if (filter.getMaxTotalOrdersPrice() != null)
            conditions.add(fieldExpressionMap.get("totalOrdersPrice") + " <= ?");
        return conditions;
    }

    @Override
    @SneakyThrows
    protected void setParametersInternal(PreparedStatement st, Transaction tr, int parametersIndexOffset) {
        LocalDate fromDate = Objects.requireNonNullElse(filter.getFromDate(), TimeUtils.minDate());
        st.setDate(parametersIndexOffset++, TimeUtils.mapToSqlDate(fromDate));
        LocalDate toDate = Objects.requireNonNullElse(filter.getToDate(), TimeUtils.maxDate());
        st.setDate(parametersIndexOffset++, TimeUtils.mapToSqlDate(toDate));

        if (filter.getAddress() != null && !filter.getAddress().isBlank())
            st.setString(parametersIndexOffset++, filter.getAddress());
        if (filter.getIsDeleted() != null)
            st.setBoolean(parametersIndexOffset++, filter.getIsDeleted());

        if (filter.getMinNumberOfOrders() != null)
            st.setInt(parametersIndexOffset++, filter.getMinNumberOfOrders());
        if (filter.getMaxNumberOfOrders() != null)
            st.setInt(parametersIndexOffset++, filter.getMaxNumberOfOrders());
        if (filter.getMinTotalOrdersPrice() != null)
            st.setBigDecimal(parametersIndexOffset++, filter.getMinTotalOrdersPrice());
        if (filter.getMaxTotalOrdersPrice() != null)
            st.setBigDecimal(parametersIndexOffset, filter.getMaxTotalOrdersPrice());
    }
}
