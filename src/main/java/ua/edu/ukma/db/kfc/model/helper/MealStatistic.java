package ua.edu.ukma.db.kfc.model.helper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MealStatistic {

    private int id;

    private String title;

    private int clientMealsCount;

    private LocalDateTime lastOrderedDate;

    private boolean isActual;
}
