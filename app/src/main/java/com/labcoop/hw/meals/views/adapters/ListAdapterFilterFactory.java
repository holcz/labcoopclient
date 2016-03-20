package com.labcoop.hw.meals.views.adapters;

import com.labcoop.hw.meals.models.Meal;
import com.labcoop.hw.meals.views.MealDateFormatHelper;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

/**
 * Created by holcz on 19/03/16.
 */
public class ListAdapterFilterFactory {

    public enum FILTER_OPERATION{
        AND,
        OR;
    };

    public static ListAdapterFilter createFilterToday(){
        return new ListAdapterFilter() {
            @Override
            public boolean filter(Meal meal) {
                Calendar mealCal = Calendar.getInstance();
                mealCal.setTimeInMillis(meal.getDateInMilis());
                Calendar today = Calendar.getInstance();
                return mealCal.get(Calendar.DAY_OF_MONTH) == today.get(Calendar.DAY_OF_MONTH) &&
                        mealCal.get(Calendar.MONTH) == today.get(Calendar.MONTH) &&
                        mealCal.get(Calendar.YEAR) == today.get(Calendar.YEAR);
            }
        };
    }

    public static ListAdapterFilter createTimeIntervalFilter(final String from, final String to){
        try {
            Calendar fromCal = Calendar.getInstance();
            fromCal.setTime(MealDateFormatHelper.timeFormater().parse(from));

            Calendar toCal = Calendar.getInstance();
            toCal.setTime(MealDateFormatHelper.timeFormater().parse(to));

            return createTimeIntervalFilter(
                    fromCal.get(Calendar.HOUR_OF_DAY), fromCal.get(Calendar.MINUTE),
                    toCal.get(Calendar.HOUR_OF_DAY), toCal.get(Calendar.MINUTE)
            );
        } catch (ParseException e) {
            return new ListAdapterFilter() {
                @Override
                public boolean filter(Meal meal) {
                    return false;
                }
            };
        }
    }

    public static ListAdapterFilter createTimeIntervalFilter(final int fromHour, final int fromMinute,
                                                             final int toHour, final int toMinute){
        return new ListAdapterFilter() {
            @Override
            public boolean filter(Meal meal) {
                Calendar fromCal = Calendar.getInstance();
                fromCal.setTime(meal.getDate());
                fromCal.set(Calendar.HOUR_OF_DAY, fromHour);
                fromCal.set(Calendar.MINUTE, fromMinute);

                Calendar toCal = Calendar.getInstance();
                toCal.setTime(meal.getDate());
                toCal.set(Calendar.HOUR_OF_DAY, toHour);
                toCal.set(Calendar.MINUTE, toMinute);

                Calendar mealCal = Calendar.getInstance();
                mealCal.setTime(meal.getDate());

                return mealCal.after(fromCal) && mealCal.before(toCal);
            }
        };
    }

    public static ListAdapterFilter createDateIntervalFilter(int fromYear, int fromMonth, int fromDayOfMonth,
                                                             int toYear, int toMonth, int toDayOfMonth){
        Calendar fromCal = Calendar.getInstance();
        fromCal.set(Calendar.HOUR_OF_DAY, 0);
        fromCal.set(Calendar.MINUTE, 0);
        fromCal.set(Calendar.YEAR, fromYear);
        fromCal.set(Calendar.MONTH, fromMonth);
        fromCal.set(Calendar.DAY_OF_MONTH, fromDayOfMonth);

        Calendar toCal = Calendar.getInstance();
        toCal.set(Calendar.HOUR_OF_DAY, 0);
        toCal.set(Calendar.MINUTE, 0);
        toCal.set(Calendar.YEAR, toYear);
        toCal.set(Calendar.MONTH, toMonth);
        toCal.set(Calendar.DAY_OF_MONTH, toDayOfMonth);
        return createIntervalFilter(fromCal, toCal);
    }

    public static ListAdapterFilter createIntervalFilter(final Calendar from, final Calendar to){
        return new ListAdapterFilter() {
            @Override
            public boolean filter(Meal meal) {
                Calendar mealCal = Calendar.getInstance();
                mealCal.setTime(meal.getDate());
                return mealCal.after(from) && mealCal.before(to);
            }
        };
    }

    public static ListAdapterFilter createFilter(final int calendarField, final int amountToSubtractFromNow){
        return new ListAdapterFilter() {
            @Override
            public boolean filter(Meal meal) {
                Calendar fromDate = Calendar.getInstance();
                if (calendarField < Calendar.HOUR){ //If a bigger field (Day, Week, etc.) reset the hour and minute
                    fromDate.set(Calendar.HOUR_OF_DAY, 0);
                    fromDate.set(Calendar.MINUTE, 0);
                }
                fromDate.add(calendarField, (-1) * amountToSubtractFromNow);
                Calendar mealCal = Calendar.getInstance();
                mealCal.setTimeInMillis(meal.getDateInMilis());
                return mealCal.after(fromDate);
            }
        };
    }

    public static ListAdapterFilter createMonthFilter(final int numberOfWeeks){
        return new ListAdapterFilter() {
            @Override
            public boolean filter(Meal meal) {
                Calendar lastWeek = Calendar.getInstance();
                lastWeek.set(Calendar.HOUR_OF_DAY,0);
                lastWeek.set(Calendar.MINUTE, 0);
                lastWeek.add(Calendar.DAY_OF_MONTH, (-7 * numberOfWeeks));
                Calendar mealCal = Calendar.getInstance();
                mealCal.setTimeInMillis(meal.getDateInMilis());
                return mealCal.after(lastWeek);
            }
        };
    }

    public static ListAdapterFilter createAllFilter(){
        return new ListAdapterFilter() {
            @Override
            public boolean filter(Meal meal) {
                return true;
            }
        };
    }

    public static ListAdapterFilter createOPFilter(FILTER_OPERATION op, ListAdapterFilter... args){
        ListAdapterOpFilter opFilter= null;
        switch (op) {
            case AND:
                opFilter = new ListAdapterANDFilter(args);
                break;
            case OR:
                opFilter = new ListAdapterORFilter(args);
                break;
        }
        return opFilter;
    }

    public class Builder{
        ListAdapterOpFilter opfilter = null;
        Builder(ListAdapterFilterFactory.FILTER_OPERATION op){
            ListAdapterOpFilter opFilter= null;
            switch (op) {
                case AND:
                    opFilter = new ListAdapterANDFilter();
                    break;
                case OR:
                    opFilter = new ListAdapterORFilter();
                    break;
            }
        }

        Builder addFilter(ListAdapterFilter filter){
            opfilter.addFilter(filter);
            return this;
        }

        ListAdapterFilter build(){
            return opfilter;
        }
    }
}



abstract class ListAdapterOpFilter implements ListAdapterFilter{

    List<ListAdapterFilter> filters;

    ListAdapterOpFilter(){
        filters = new ArrayList<>();
    }

    ListAdapterOpFilter(ListAdapterFilter[] filters){
        this();
        this.filters.addAll(Arrays.asList(filters));
    }

    void addFilter(ListAdapterFilter filter){
        this.filters.add(filter);
    }

    void removeFilter(ListAdapterFilter filter){
        this.filters.remove(filter);
    }

    void addFilterAll(Collection<ListAdapterFilter> filters){
        this.filters.addAll(filters);
    }

    void clearFilters(){
        this.filters.clear();
    }
}

class ListAdapterANDFilter extends ListAdapterOpFilter{

    ListAdapterANDFilter(){
        super();
    }

    ListAdapterANDFilter(ListAdapterFilter... args){
        super(args);
    }

    @Override
    public boolean filter(Meal meal) {
        for (ListAdapterFilter filter:
                filters) {
            if (!filter.filter(meal)){
                return false;
            }
        }
        return true;
    }
}

class ListAdapterORFilter extends ListAdapterOpFilter{

    ListAdapterORFilter(){
        super();
    }

    ListAdapterORFilter(ListAdapterFilter... args){
        super(args);
    }

    @Override
    public boolean filter(Meal meal) {
        for (ListAdapterFilter filter:
                filters) {
            if (filter.filter(meal)){
                return true;
            }
        }
        return false;
    }
}
