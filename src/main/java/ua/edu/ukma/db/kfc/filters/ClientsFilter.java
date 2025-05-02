package ua.edu.ukma.db.kfc.filters;

import lombok.SneakyThrows;
import ua.edu.ukma.db.kfc.rest.model.ClientsFilterDto;
import ua.edu.ukma.db.kfc.transactions.Transaction;
import ua.edu.ukma.db.kfc.utils.TimeUtils;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ClientsFilter extends BaseFilter<ClientsFilterDto> {

    public ClientsFilter(ClientsFilterDto filter) {
        super(filter);
    }

    @Override
    protected List<String> formWhereConditions(Map<String, String> fieldExpressionMap) {
        List<String> conditions = new ArrayList<>();
        if (filter.getQuery() != null && !filter.getQuery().isBlank()) {
            conditions.add(
                String.format("LOWER(CONCAT_WS(' ', %s, %s, %s, %s, %s, %s)) LIKE LOWER('%%' || ? || '%%')",
                        fieldExpressionMap.get("username"),
                        fieldExpressionMap.get("surname"),
                        fieldExpressionMap.get("firstName"),
                        fieldExpressionMap.get("middleName"),
                        fieldExpressionMap.get("phone"),
                        fieldExpressionMap.get("email"))
            );
        }
        if (filter.getMinBonuses() != null)
            conditions.add(fieldExpressionMap.get("bonuses") + " >= ?");
        if (filter.getMaxBonuses() != null)
            conditions.add(fieldExpressionMap.get("bonuses") + " <= ?");
        if (filter.getMinBirthDate() != null)
            conditions.add(fieldExpressionMap.get("birthDate") + " >= ?");
        if (filter.getMaxBirthDate() != null)
            conditions.add(fieldExpressionMap.get("birthDate") + " <= ?");
        return conditions;
    }

    @Override
    @SneakyThrows
    protected void setWhereClauseParametersInternal(PreparedStatement st, Transaction tr, int parametersIndexOffset) {
        if (filter.getQuery() != null && !filter.getQuery().isBlank())
            st.setString(parametersIndexOffset++, filter.getQuery());
        if (filter.getMinBonuses() != null)
            st.setInt(parametersIndexOffset++, filter.getMinBonuses());
        if (filter.getMaxBonuses() != null)
            st.setInt(parametersIndexOffset++, filter.getMaxBonuses());
        if (filter.getMinBirthDate() != null)
            st.setDate(parametersIndexOffset++, TimeUtils.mapToSqlDate(filter.getMinBirthDate()));
        if (filter.getMaxBirthDate() != null)
            st.setDate(parametersIndexOffset, TimeUtils.mapToSqlDate(filter.getMaxBirthDate()));
    }
}
