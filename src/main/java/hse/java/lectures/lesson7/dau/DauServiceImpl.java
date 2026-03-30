package hse.java.lectures.lesson7.dau;

import java.time.Clock;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class DauServiceImpl implements DauService {

    private final Clock clock;
    private final ConcurrentHashMap<Integer, ConcurrentHashMap<LocalDate, Set<Integer>>> data = new ConcurrentHashMap<>();

    public DauServiceImpl() {
        this(Clock.systemDefaultZone());
    }

    public DauServiceImpl(Clock clock) {
        this.clock = clock;
    }

    @Override
    public void postEvent(Event event) {
        LocalDate today = LocalDate.now(clock);
        data.computeIfAbsent(event.authorId(), k -> new ConcurrentHashMap<>())
            .computeIfAbsent(today, k -> ConcurrentHashMap.newKeySet())
            .add(event.userId());
    }

    @Override
    public Map<Integer, Long> getDauStatistics(List<Integer> authorIds) {
        return authorIds.stream()
            .collect(Collectors.toMap(id -> id, this::getAuthorDauStatistics));
    }

    @Override
    public Long getAuthorDauStatistics(int authorId) {
        LocalDate yesterday = LocalDate.now(clock).minusDays(1);
        Map<LocalDate, Set<Integer>> byDate = data.get(authorId);
        if (byDate == null) return 0L;
        Set<Integer> users = byDate.get(yesterday);
        return users == null ? 0L : (long) users.size();
    }
}
