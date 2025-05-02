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
        Query split = splitQuery(query);
        StringBuilder updatedQuery = new StringBuilder(split.select()).append('\n').append(split.from());
        formWhereClause(updatedQuery, fieldExpressionMap);
        updatedQuery.append('\n').append(split.groupBy());
        formHavingClause(updatedQuery, fieldExpressionMap);
        return updatedQuery.toString();
    }

    public String addFilteringAndPagination(String query, Map<String, String> fieldExpressionMap) {
        if (filter == null) return query;
        StringBuilder updatedQuery = new StringBuilder(addFiltering(query, fieldExpressionMap));
        formPaginationClause(updatedQuery, fieldExpressionMap);
        return updatedQuery.toString();
    }

    private Query splitQuery(String query) {
        String lowerCaseQuery = query.toLowerCase();
        int indexOfFrom = lowerCaseQuery.indexOf("from");
        int indexOfGroupBy = lowerCaseQuery.indexOf("group by");
        String select = query.substring(0, indexOfFrom);
        String from = query.substring(indexOfFrom, indexOfGroupBy == -1 ? query.length() : indexOfGroupBy);
        String groupBy = indexOfGroupBy == -1 ? "" : query.substring(indexOfGroupBy);
        return new Query(select, from, groupBy);
    }

    private void formWhereClause(StringBuilder updatedQuery, Map<String, String> fieldExpressionMap) {
        List<String> conditions = formWhereConditions(fieldExpressionMap);
        if (conditions.isEmpty()) return;
        updatedQuery.append("\nWHERE ");
        for (int i = 0; i < conditions.size(); i++) {
            if (i > 0) updatedQuery.append(" AND ");
            updatedQuery.append(conditions.get(i));
        }
    }

    protected List<String> formWhereConditions(Map<String, String> fieldExpressionMap) {
        return List.of();
    }

    private void formHavingClause(StringBuilder updatedQuery, Map<String, String> fieldExpressionMap) {
        List<String> conditions = formHavingConditions(fieldExpressionMap);
        if (conditions.isEmpty()) return;
        updatedQuery.append("\nHAVING ");
        for (int i = 0; i < conditions.size(); i++) {
            if (i > 0) updatedQuery.append(" AND ");
            updatedQuery.append(conditions.get(i));
        }
    }

    protected List<String> formHavingConditions(Map<String, String> fieldExpressionMap) {
        return List.of();
    }

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

    public void setParameters(PreparedStatement st, Transaction tr) {
        setParameters(st, tr, 1);
    }

    public void setParameters(PreparedStatement st, Transaction tr, int parametersIndexOffset) {
        if (filter == null) return;
        setParametersInternal(st, tr, parametersIndexOffset);
    }

    protected void setParametersInternal(PreparedStatement st, Transaction tr, int parametersIndexOffset) {}

    private record Query(String select, String from, String groupBy) {}
}
