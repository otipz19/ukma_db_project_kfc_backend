package ua.edu.ukma.db.kfc.filters;

import lombok.SneakyThrows;
import ua.edu.ukma.db.kfc.rest.model.IngredientsFilterDto;
import ua.edu.ukma.db.kfc.transactions.Transaction;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class IngredientsFilter extends BaseFilter<IngredientsFilterDto> {

    public IngredientsFilter(IngredientsFilterDto filter) {
        super(filter);
    }

    @Override
    protected List<String> formWhereConditions(Map<String, String> fieldExpressionMap) {
        List<String> conditions = new ArrayList<>();
        if (filter.getIds() != null && !filter.getIds().isEmpty())
            conditions.add(fieldExpressionMap.get("id") + " = ANY (?)");
        if (filter.getIdsNot() != null && !filter.getIdsNot().isEmpty())
            conditions.add(fieldExpressionMap.get("id") + " <> ALL (?)");
        if (filter.getQuery() != null && !filter.getQuery().isBlank()) {
            conditions.add(
                    String.format("LOWER(%s) LIKE LOWER('%%' || ? || '%%')", fieldExpressionMap.get("title"))
            );
        }
        if (filter.getMinEnergeticValue() != null)
            conditions.add(fieldExpressionMap.get("energeticValue") + " >= ?");
        if (filter.getMaxEnergeticValue() != null)
            conditions.add(fieldExpressionMap.get("energeticValue") + " <= ?");
        if (filter.getMinWeight() != null)
            conditions.add(fieldExpressionMap.get("weight") + " >= ?");
        if (filter.getMaxWeight() != null)
            conditions.add(fieldExpressionMap.get("weight") + " <= ?");
        if (filter.getMinPrice() != null)
            conditions.add(fieldExpressionMap.get("price") + " >= ?");
        if (filter.getMaxPrice() != null)
            conditions.add(fieldExpressionMap.get("price") + " <= ?");
        if (filter.getIsActual() != null)
            conditions.add(fieldExpressionMap.get("isActual") + " = ?");
        return conditions;
    }

    @Override
    @SneakyThrows
    protected void setParametersInternal(PreparedStatement st, Transaction tr, int parametersIndexOffset) {
        if (filter.getIds() != null && !filter.getIds().isEmpty())
            st.setArray(parametersIndexOffset++, tr.createArrayOf(filter.getIds(), Integer.class));
        if (filter.getIdsNot() != null && !filter.getIdsNot().isEmpty())
            st.setArray(parametersIndexOffset++, tr.createArrayOf(filter.getIdsNot(), Integer.class));
        if (filter.getQuery() != null && !filter.getQuery().isBlank())
            st.setString(parametersIndexOffset++, filter.getQuery());
        if (filter.getMinEnergeticValue() != null)
            st.setInt(parametersIndexOffset++, filter.getMinEnergeticValue());
        if (filter.getMaxEnergeticValue() != null)
            st.setInt(parametersIndexOffset++, filter.getMaxEnergeticValue());
        if (filter.getMinWeight() != null)
            st.setInt(parametersIndexOffset++, filter.getMinWeight());
        if (filter.getMaxWeight() != null)
            st.setInt(parametersIndexOffset++, filter.getMaxWeight());
        if (filter.getMinPrice() != null)
            st.setBigDecimal(parametersIndexOffset++, filter.getMinPrice());
        if (filter.getMaxPrice() != null)
            st.setBigDecimal(parametersIndexOffset++, filter.getMaxPrice());
        if (filter.getIsActual() != null)
            st.setBoolean(parametersIndexOffset, filter.getIsActual());
    }
}
