package ua.edu.ukma.db.kfc.filters;

import lombok.SneakyThrows;
import ua.edu.ukma.db.kfc.rest.model.MealsStatisticFilterDto;
import ua.edu.ukma.db.kfc.transactions.Transaction;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MealsStatisticFilter extends BaseFilter<MealsStatisticFilterDto> {

    public MealsStatisticFilter(MealsStatisticFilterDto filter) {
        super(filter);
    }

    @Override
    protected List<String> formWhereConditions(Map<String, String> fieldExpressionMap) {
        List<String> conditions = new ArrayList<>();
        if (filter.getIds() != null && !filter.getIds().isEmpty())
            conditions.add(fieldExpressionMap.get("id") + " = ANY (?)");
        if (filter.getTitle() != null && !filter.getTitle().isBlank()) {
            conditions.add(
                    String.format("LOWER(%s) LIKE LOWER('%%' || ? || '%%')",
                            fieldExpressionMap.get("title"))
            );
        }
        if (filter.getIsActual() != null)
            conditions.add(fieldExpressionMap.get("isActual") + " = ?");
        return conditions;
    }

    @Override
    protected List<String> formHavingConditions(Map<String, String> fieldExpressionMap) {
        List<String> conditions = new ArrayList<>();
        if (filter.getMinClientMealsCount() != null)
            conditions.add(fieldExpressionMap.get("clientMealsCount") + " >= ?");
        if (filter.getMaxClientMealsCount() != null)
            conditions.add(fieldExpressionMap.get("clientMealsCount") + " <= ?");
        if (filter.getMinLastOrderedDate() != null)
            conditions.add(fieldExpressionMap.get("lastOrderedDate") + " >= ?");
        if (filter.getMaxLastOrderedDate() != null){
            conditions.add(
                    String.format("COALESCE (%s <= ?, TRUE)",
                            fieldExpressionMap.get("lastOrderedDate"))
            );
        }

        return conditions;
    }

    @Override
    @SneakyThrows
    protected void setWhereClauseParametersInternal(PreparedStatement st, Transaction tr, int parametersIndexOffset) {
        if (filter.getIds() != null && !filter.getIds().isEmpty())
            st.setArray(parametersIndexOffset++, tr.createArrayOf(filter.getIds(), Integer.class));
        if (filter.getTitle() != null && !filter.getTitle().isBlank())
            st.setString(parametersIndexOffset++, filter.getTitle());
        if (filter.getIsActual() != null)
            st.setBoolean(parametersIndexOffset++, filter.getIsActual());

        if (filter.getMinClientMealsCount() != null)
            st.setInt(parametersIndexOffset++, filter.getMinClientMealsCount());
        if (filter.getMaxClientMealsCount() != null)
            st.setInt(parametersIndexOffset++, filter.getMaxClientMealsCount());
        if (filter.getMinLastOrderedDate() != null)
            st.setObject(parametersIndexOffset++, filter.getMinLastOrderedDate());
        if (filter.getMaxLastOrderedDate() != null)
            st.setObject(parametersIndexOffset, filter.getMaxLastOrderedDate());
    }
}
