package ua.edu.ukma.db.kfc.filters;

import lombok.SneakyThrows;
import ua.edu.ukma.db.kfc.mappers.EnumsMapper;
import ua.edu.ukma.db.kfc.rest.model.EmployeesStatisticFilterDto;
import ua.edu.ukma.db.kfc.transactions.Transaction;
import ua.edu.ukma.db.kfc.utils.TimeUtils;

import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.util.*;

public class EmployeesStatisticFilter extends BaseFilter<EmployeesStatisticFilterDto> {

    private final EnumsMapper enumsMapper;

    public EmployeesStatisticFilter(EmployeesStatisticFilterDto filter, EnumsMapper enumsMapper) {
        super(filter);
        this.enumsMapper = enumsMapper;
    }

    @Override
    protected List<String> formWhereConditions(Map<String, String> fieldExpressionMap) {
        List<String> conditions = new ArrayList<>();
        if (filter.getPassportNumber() != null && !filter.getPassportNumber().isBlank()) {
            conditions.add(
                String.format("LOWER(%s) LIKE LOWER('%%' || ? || '%%')",
                        fieldExpressionMap.get("passportNumber"))
            );
        }
        if (filter.getPosition() != null)
            conditions.add(fieldExpressionMap.get("position") + " = ?");
        if (filter.getRestaurantId() != null)
            conditions.add(fieldExpressionMap.get("restaurantId") + " = ?");
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

        if (filter.getPassportNumber() != null && !filter.getPassportNumber().isBlank())
            st.setString(parametersIndexOffset++, filter.getPassportNumber());
        if (filter.getPosition() != null)
            st.setString(parametersIndexOffset++, enumsMapper.mapToSting(enumsMapper.mapToRole(filter.getPosition())));
        if (filter.getRestaurantId() != null)
            st.setInt(parametersIndexOffset++, filter.getRestaurantId());

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
