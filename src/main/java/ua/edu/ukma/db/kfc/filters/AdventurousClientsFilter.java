package ua.edu.ukma.db.kfc.filters;

import lombok.SneakyThrows;
import ua.edu.ukma.db.kfc.rest.model.AdventurousClientsFilterDto;
import ua.edu.ukma.db.kfc.transactions.Transaction;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AdventurousClientsFilter extends BaseFilter<AdventurousClientsFilterDto> {

    public AdventurousClientsFilter(AdventurousClientsFilterDto filter) {
        super(filter);
    }

    @Override
    protected List<String> formWhereConditions(Map<String, String> fieldExpressionMap) {
        List<String> conditions = new ArrayList<>();
        conditions.add(
                String.format(
                        """
                        NOT EXISTS (
                            SELECT *
                            FROM meals
                            WHERE (price BETWEEN ? AND ?) AND is_actual AND id NOT IN (
                                SELECT meal_id
                                FROM orders JOIN client_meals ON orders.id = client_meals.order_id
                                WHERE client_user_id = %1$s
                            )
                        ) AND EXISTS (SELECT * FROM meals WHERE (price BETWEEN ? AND ?) AND is_actual)
                        """, fieldExpressionMap.get("userId")
                )
        );
        return conditions;
    }

    @Override
    @SneakyThrows
    protected void setParametersInternal(PreparedStatement st, Transaction tr, int parametersIndexOffset) {
        BigDecimal minPrice = filter.getMinPrice() != null ? filter.getMinPrice() : BigDecimal.ZERO;
        BigDecimal maxPrice = filter.getMaxPrice() != null ? filter.getMaxPrice() : BigDecimal.valueOf(Double.MAX_VALUE);
        st.setBigDecimal(parametersIndexOffset++, minPrice);
        st.setBigDecimal(parametersIndexOffset++, maxPrice);
        st.setBigDecimal(parametersIndexOffset++, minPrice);
        st.setBigDecimal(parametersIndexOffset, maxPrice);
    }
}
