package ru.surf.course.movierecommendations.interactor.tmdbTasks;


public enum Filters {
    popular,
    top_rated,
    upcoming,
    custom,
    on_the_air,
    recommendations;

    public static boolean isFilter(String compare) {
        return compare.equalsIgnoreCase(popular.toString()) ||
                compare.equalsIgnoreCase(top_rated.toString()) ||
                compare.equalsIgnoreCase(upcoming.toString()) ||
                compare.equalsIgnoreCase(custom.toString()) ||
                compare.equalsIgnoreCase(on_the_air.toString()) ||
                compare.equalsIgnoreCase(recommendations.toString());
    }
}
