package ua.edu.ukma.db.kfc.filters;

import lombok.SneakyThrows;
import ua.edu.ukma.db.kfc.rest.model.RestaurantsFilterDto;
import ua.edu.ukma.db.kfc.transactions.Transaction;

import java.sql.Array;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RestaurantsFilter extends BaseFilter<RestaurantsFilterDto> {

    public RestaurantsFilter(RestaurantsFilterDto filter) {
        super(filter);
    }

    @Override
    protected List<String> formWhereConditions(Map<String, String> fieldExpressionMap) {
        List<String> conditions = new ArrayList<>();
        if (filter.getIds() != null && !filter.getIds().isEmpty())
            conditions.add(fieldExpressionMap.get("id") + " = ANY (?)");
        if (filter.getQuery() != null && !filter.getQuery().isBlank()) {
            conditions.add(
                String.format("LOWER(%s) LIKE LOWER('%%' || ? || '%%')", fieldExpressionMap.get("address"))
            );
        }
        if (filter.getIsDeleted() != null)
            conditions.add(fieldExpressionMap.get("isDeleted") + " = ?");
        return conditions;
    }

    @Override
    @SneakyThrows
    protected void setWhereClauseParametersInternal(PreparedStatement st, Transaction tr, int parametersIndexOffset) {
        if (filter.getIds() != null && !filter.getIds().isEmpty()) {
            Array array = tr.createArrayOf(filter.getIds(), Integer.class);
            st.setArray(parametersIndexOffset++, array);
        }
        if (filter.getQuery() != null && !filter.getQuery().isBlank())
            st.setString(parametersIndexOffset++, filter.getQuery());
        if (filter.getIsDeleted() != null)
            st.setBoolean(parametersIndexOffset, filter.getIsDeleted());
    }
}
