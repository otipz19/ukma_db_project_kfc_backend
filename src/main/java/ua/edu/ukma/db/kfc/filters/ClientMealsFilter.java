package ua.edu.ukma.db.kfc.filters;

import lombok.SneakyThrows;
import ua.edu.ukma.db.kfc.rest.model.ClientMealsFilterDto;
import ua.edu.ukma.db.kfc.transactions.Transaction;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ClientMealsFilter extends BaseFilter<ClientMealsFilterDto> {

    public ClientMealsFilter(ClientMealsFilterDto filter) {
        super(filter);
    }

    @Override
    protected List<String> formWhereConditions(Map<String, String> fieldExpressionMap) {
        List<String> conditions = new ArrayList<>();
        if (filter.getIds() != null && !filter.getIds().isEmpty())
            conditions.add(fieldExpressionMap.get("id") + " = ANY (?)");
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
        if (filter.getOrderId() != null)
            conditions.add(fieldExpressionMap.get("orderId") + " = ?");
        if (filter.getMealId() != null)
            conditions.add(fieldExpressionMap.get("mealId") + " = ?");
        if (filter.getMinAmountInOrder() != null)
            conditions.add(fieldExpressionMap.get("amountInOrder") + " >= ?");
        if (filter.getMaxAmountInOrder() != null)
            conditions.add(fieldExpressionMap.get("amountInOrder") + " <= ?");
        return conditions;
    }

    @Override
    @SneakyThrows
    protected void setParametersInternal(PreparedStatement st, Transaction tr, int parametersIndexOffset) {
        if (filter.getIds() != null && !filter.getIds().isEmpty())
            st.setArray(parametersIndexOffset++, tr.createArrayOf(filter.getIds(), Integer.class));
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
        if (filter.getOrderId() != null)
            st.setInt(parametersIndexOffset++, filter.getOrderId());
        if (filter.getMealId() != null)
            st.setInt(parametersIndexOffset++, filter.getMealId());
        if (filter.getMinAmountInOrder() != null)
            st.setInt(parametersIndexOffset++, filter.getMinAmountInOrder());
        if (filter.getMaxAmountInOrder() != null)
            st.setInt(parametersIndexOffset, filter.getMaxAmountInOrder());
    }
}
