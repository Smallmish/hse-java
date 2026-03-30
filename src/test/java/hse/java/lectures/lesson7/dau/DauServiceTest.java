package hse.java.lectures.lesson7.dau;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.*;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@Tag("dau")
class DauServiceTest {
    private static final LocalDate TODAY = LocalDate.of(2024, 6, 15);
    private static final LocalDate YESTERDAY = TODAY.minusDays(1);

    private DauServiceImpl serviceAt(LocalDate date) {
        return new DauServiceImpl(fixedClock(date));
    }

    private static Clock fixedClock(LocalDate date) {
        return Clock.fixed(date.atTime(12, 0).atZone(ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault());
    }

    @Test
    void returnsZeroForUnknownAuthor() {
        assertEquals(0L, serviceAt(TODAY).getAuthorDauStatistics(42));
    }

    @Test
    void returnsZeroWhenEventsOnlyToday() {
        MutableClock clock = new MutableClock(TODAY);
        DauServiceImpl svc = new DauServiceImpl(clock);
        svc.postEvent(new Event(1, 100));
        assertEquals(0L, svc.getAuthorDauStatistics(100));
    }

    @Test
    void countsUniqueUsersFromYesterday() {
        MutableClock clock = new MutableClock(YESTERDAY);
        DauServiceImpl svc = new DauServiceImpl(clock);

        svc.postEvent(new Event(1, 100));
        svc.postEvent(new Event(2, 100));
        svc.postEvent(new Event(1, 100)); // duplicate

        clock.setDate(TODAY);
        assertEquals(2L, svc.getAuthorDauStatistics(100));
    }

    @Test
    void doesNotCountEventsFromTwoDaysAgo() {
        MutableClock clock = new MutableClock(TODAY.minusDays(2));
        DauServiceImpl svc = new DauServiceImpl(clock);

        svc.postEvent(new Event(1, 100));

        clock.setDate(TODAY);
        assertEquals(0L, svc.getAuthorDauStatistics(100));
    }

    @Test
    void getDauStatisticsReturnsAllAuthors() {
        MutableClock clock = new MutableClock(YESTERDAY);
        DauServiceImpl svc = new DauServiceImpl(clock);

        svc.postEvent(new Event(1, 100));
        svc.postEvent(new Event(2, 100));
        svc.postEvent(new Event(1, 200));

        clock.setDate(TODAY);
        Map<Integer, Long> stats = svc.getDauStatistics(List.of(100, 200, 300));
        assertEquals(2L, stats.get(100));
        assertEquals(1L, stats.get(200));
        assertEquals(0L, stats.get(300));
    }

    @Test
    void differentAuthorsAreIndependent() {
        MutableClock clock = new MutableClock(YESTERDAY);
        DauServiceImpl svc = new DauServiceImpl(clock);

        svc.postEvent(new Event(1, 100));
        svc.postEvent(new Event(2, 200));
        svc.postEvent(new Event(3, 200));

        clock.setDate(TODAY);
        assertEquals(1L, svc.getAuthorDauStatistics(100));
        assertEquals(2L, svc.getAuthorDauStatistics(200));
    }

    static class MutableClock extends Clock {
        private Instant instant;
        private final ZoneId zone = ZoneId.systemDefault();

        MutableClock(LocalDate date) {
            this.instant = date.atTime(12, 0).atZone(zone).toInstant();
        }

        void setDate(LocalDate date) {
            this.instant = date.atTime(12, 0).atZone(zone).toInstant();
        }

        @Override public ZoneId getZone() { return zone; }
        @Override public Clock withZone(ZoneId zone) { return this; }
        @Override public Instant instant() { return instant; }
    }
}
