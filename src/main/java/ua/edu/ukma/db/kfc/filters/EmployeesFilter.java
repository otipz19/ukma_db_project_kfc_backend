package ua.edu.ukma.db.kfc.filters;

import lombok.SneakyThrows;
import ua.edu.ukma.db.kfc.mappers.EnumsMapper;
import ua.edu.ukma.db.kfc.rest.model.EmployeesFilterDto;
import ua.edu.ukma.db.kfc.transactions.Transaction;
import ua.edu.ukma.db.kfc.utils.TimeUtils;

import java.sql.Array;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EmployeesFilter extends BaseFilter<EmployeesFilterDto> {

    private final EnumsMapper enumsMapper;

    public EmployeesFilter(EmployeesFilterDto filter, EnumsMapper enumsMapper) {
        super(filter);
        this.enumsMapper = enumsMapper;
    }

    @Override
    protected List<String> formConditions(Map<String, String> fieldExpressionMap) {
        List<String> conditions = new ArrayList<>();
        if (filter.getQuery() != null && !filter.getQuery().isBlank()) {
            conditions.add(
                String.format("LOWER(CONCAT_WS(' ', %s, %s, %s, %s, %s, %s, %s)) LIKE LOWER('%%' || ? || '%%')",
                        fieldExpressionMap.get("username"),
                        fieldExpressionMap.get("passportNumber"),
                        fieldExpressionMap.get("surname"),
                        fieldExpressionMap.get("firstName"),
                        fieldExpressionMap.get("middleName"),
                        fieldExpressionMap.get("phone"),
                        fieldExpressionMap.get("email"))
            );
        }
        if (filter.getMinSalary() != null)
            conditions.add(fieldExpressionMap.get("salary") + " >= ?");
        if (filter.getMaxSalary() != null)
            conditions.add(fieldExpressionMap.get("salary") + " <= ?");
        if (filter.getMinBirthDate() != null)
            conditions.add(fieldExpressionMap.get("birthDate") + " >= ?");
        if (filter.getMaxBirthDate() != null)
            conditions.add(fieldExpressionMap.get("birthDate") + " <= ?");
        if (filter.getPositions() != null && !filter.getPositions().isEmpty())
            conditions.add(fieldExpressionMap.get("position") + " = ANY (?)");
        if (filter.getManagerUserId() != null)
            conditions.add(fieldExpressionMap.get("managerUserId") + " = ?");
        if (filter.getRestaurantId() != null)
            conditions.add(fieldExpressionMap.get("restaurantId") + " = ?");
        return conditions;
    }

    @Override
    @SneakyThrows
    protected void setWhereClauseParametersInternal(PreparedStatement st, Transaction tr, int parametersIndexOffset) {
        if (filter.getQuery() != null && !filter.getQuery().isBlank())
            st.setString(parametersIndexOffset++, filter.getQuery());
        if (filter.getMinSalary() != null)
            st.setBigDecimal(parametersIndexOffset++, filter.getMinSalary());
        if (filter.getMaxSalary() != null)
            st.setBigDecimal(parametersIndexOffset++, filter.getMaxSalary());
        if (filter.getMinBirthDate() != null)
            st.setDate(parametersIndexOffset++, TimeUtils.mapToSqlDate(filter.getMinBirthDate()));
        if (filter.getMaxBirthDate() != null)
            st.setDate(parametersIndexOffset++, TimeUtils.mapToSqlDate(filter.getMaxBirthDate()));
        if (filter.getPositions() != null && !filter.getPositions().isEmpty()) {
            Array arr = tr.createArrayOf(enumsMapper.map(filter.getPositions(), enumsMapper::mapToRole), "varchar");
            st.setArray(parametersIndexOffset++, arr);
        }
        if (filter.getManagerUserId() != null)
            st.setInt(parametersIndexOffset++, filter.getManagerUserId());
        if (filter.getRestaurantId() != null)
            st.setInt(parametersIndexOffset, filter.getRestaurantId());
    }
}
