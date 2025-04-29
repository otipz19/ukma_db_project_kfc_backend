package ua.edu.ukma.db.kfc.filters;

import lombok.Getter;
import ua.edu.ukma.db.kfc.rest.model.BaseFilterDto;
import ua.edu.ukma.db.kfc.transactions.Transaction;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Map;

@Getter
public abstract class BaseFilter<F extends BaseFilterDto> {

    protected int page;
    protected int size;
    protected String sortBy = "id";
    protected boolean descendingOrder;
    protected F filter;

    public BaseFilter(F filter) {
        if (filter != null) {
            this.filter = filter;
            if (filter.getPage() != null)
                this.page = filter.getPage();
            if (filter.getSize() != null)
                this.size = filter.getSize();
            if (filter.getSortBy() != null)
                this.sortBy = filter.getSortBy();
            if (filter.getDescendingOrder() != null)
                this.descendingOrder = filter.getDescendingOrder();
        }
    }

    public String addFiltering(String query, Map<String, String> fieldExpressionMap) {
        if (filter == null) return query;
        StringBuilder updatedQuery = new StringBuilder(query);
        formWhereClause(updatedQuery, fieldExpressionMap);
        return updatedQuery.toString();
    }

    public String addFilteringAndPagination(String query, Map<String, String> fieldExpressionMap) {
        if (filter == null) return query;
        StringBuilder updatedQuery = new StringBuilder(query);
        formWhereClause(updatedQuery, fieldExpressionMap);
        formPaginationClause(updatedQuery, fieldExpressionMap);
        return updatedQuery.toString();
    }

    private void formWhereClause(StringBuilder updatedQuery, Map<String, String> fieldExpressionMap) {
        List<String> conditions = formConditions(fieldExpressionMap);
        if (conditions.isEmpty()) return;
        updatedQuery.append("\nWHERE ");
        for (int i = 0; i < conditions.size(); i++) {
            if (i > 0) updatedQuery.append(" AND ");
            updatedQuery.append(conditions.get(i));
        }
    }

    protected abstract List<String> formConditions(Map<String, String> fieldExpressionMap);

    private void formPaginationClause(StringBuilder updatedQuery, Map<String, String> fieldExpressionMap) {
        String orderExpression = fieldExpressionMap.get(sortBy);
        if (orderExpression != null)
            updatedQuery.append("\nORDER BY ").append(orderExpression).append(" ").append(descendingOrder ? "DESC" : "ASC");
        if (size > 0) {
            updatedQuery.append("\nLIMIT ").append(size);
            if (page > 0)
                updatedQuery.append("\nOFFSET ").append(page * size);
        }
    }

    public void setWhereClauseParameters(PreparedStatement st, Transaction tr) {
        setWhereClauseParameters(st, tr, 1);
    }

    public void setWhereClauseParameters(PreparedStatement st, Transaction tr, int parametersIndexOffset) {
        if (filter == null) return;
        setWhereClauseParametersInternal(st, tr, parametersIndexOffset);
    }

    protected abstract void setWhereClauseParametersInternal(PreparedStatement st, Transaction tr, int parametersIndexOffset);
}
