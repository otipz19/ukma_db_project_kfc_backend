package ua.edu.ukma.db.kfc.filters;

import lombok.SneakyThrows;
import ua.edu.ukma.db.kfc.rest.model.ValuableIngredientsFilterDto;
import ua.edu.ukma.db.kfc.transactions.Transaction;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ValuableIngredientsFilter extends BaseFilter<ValuableIngredientsFilterDto> {

    public ValuableIngredientsFilter(ValuableIngredientsFilterDto filter) {
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
                            WHERE price >= ? AND is_actual AND id NOT IN (
                                SELECT meal_id
                                FROM meals_ingredients
                                WHERE ingredient_id = %s
                            )
                        ) AND EXISTS (SELECT * FROM meals WHERE price >= ? AND is_actual)
                        """, fieldExpressionMap.get("id")
                )
        );
        conditions.add(fieldExpressionMap.get("isActual"));
        return conditions;
    }

    @Override
    @SneakyThrows
    protected void setParametersInternal(PreparedStatement st, Transaction tr, int parametersIndexOffset) {
        st.setBigDecimal(parametersIndexOffset++, filter.getMinMealPrice());
        st.setBigDecimal(parametersIndexOffset, filter.getMinMealPrice());
    }
}
