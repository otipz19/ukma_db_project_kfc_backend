package ua.edu.ukma.db.kfc.filters;

import lombok.SneakyThrows;
import ua.edu.ukma.db.kfc.rest.model.IngredientsInActiveUseFilterDto;
import ua.edu.ukma.db.kfc.transactions.Transaction;
import ua.edu.ukma.db.kfc.utils.TimeUtils;

import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class IngredientsInActiveUseFilter extends BaseFilter<IngredientsInActiveUseFilterDto> {

    public IngredientsInActiveUseFilter(IngredientsInActiveUseFilterDto filter) {
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
                            FROM meals_ingredients
                            WHERE ingredient_id = %1$s AND meal_id NOT IN (
                                SELECT meal_id
                                FROM orders JOIN client_meals ON orders.id = client_meals.order_id
                                WHERE date_created BETWEEN ? AND ?
                            )
                        ) AND EXISTS (SELECT * FROM meals_ingredients WHERE ingredient_id = %1$s)
                        """, fieldExpressionMap.get("id")
                )
        );
        if (filter.getIsActual() != null)
          conditions.add(fieldExpressionMap.get("isActual") + " = ?");
        return conditions;
    }

    @Override
    @SneakyThrows
    protected void setParametersInternal(PreparedStatement st, Transaction tr, int parametersIndexOffset) {
        LocalDate fromDate = Objects.requireNonNullElse(filter.getFromDate(), TimeUtils.minDate());
        st.setDate(parametersIndexOffset++, TimeUtils.mapToSqlDate(fromDate));
        LocalDate toDate = Objects.requireNonNullElse(filter.getToDate(), TimeUtils.maxDate());
        st.setDate(parametersIndexOffset++, TimeUtils.mapToSqlDate(toDate));

        if (filter.getIsActual() != null)
            st.setBoolean(parametersIndexOffset, filter.getIsActual());
    }
}
