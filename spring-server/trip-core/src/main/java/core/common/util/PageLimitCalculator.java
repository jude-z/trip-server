package core.common.util;

public class PageLimitCalculator {

    public static int calculatePageLimit(int page, int pageSize, int movablePageCount) {

        return (((page - 1) / movablePageCount) + 1) * pageSize * movablePageCount + 1;
    }
}
