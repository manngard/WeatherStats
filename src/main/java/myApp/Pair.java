package myApp;

/**
 * The specification of a generic Pair object that stores two parameters
 *
 * @param <F> The pair's first parameter
 * @param <S> The pair's second parameter
 */

public class Pair <F,S> {
    private F first;
    private S second;

    public Pair(F first,S second){
        this.first = first;
        this.second = second;
    }

    public S getSecond() {
        return second;
    }

    public F getFirst() {
        return first;
    }
}
