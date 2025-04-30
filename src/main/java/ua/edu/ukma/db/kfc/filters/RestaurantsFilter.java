package ua.edu.ukma.db.kfc.filters;

import lombok.SneakyThrows;
import ua.edu.ukma.db.kfc.rest.model.RestaurantsFilterDto;
import ua.edu.ukma.db.kfc.transactions.Transaction;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RestaurantsFilter extends BaseFilter<RestaurantsFilterDto> {

    public RestaurantsFilter(RestaurantsFilterDto filter) {
        super(filter);
    }

    @Override
    protected List<String> formConditions(Map<String, String> fieldExpressionMap) {
        List<String> conditions = new ArrayList<>();
        conditions.add("is_deleted = false");
        if (filter.getQuery() != null && !filter.getQuery().isBlank()) {
            conditions.add(
                String.format("LOWER(%s) LIKE LOWER('%%' || ? || '%%')", fieldExpressionMap.get("address"))
            );
        }
        return conditions;
    }

    @Override
    @SneakyThrows
    protected void setWhereClauseParametersInternal(PreparedStatement st, Transaction tr, int parametersIndexOffset) {
        if (filter.getQuery() != null && !filter.getQuery().isBlank())
            st.setString(parametersIndexOffset, filter.getQuery());
    }
}
