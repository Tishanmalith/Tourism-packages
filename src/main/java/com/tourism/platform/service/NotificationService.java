package com.tourism.platform.service;

import com.tourism.platform.model.Notification;
import com.tourism.platform.util.DelimitedFileUtil;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * File-based notification service.
 * notifications.txt format: id,userId,bookingId,message,read,createdAt
 */
@Service
public class NotificationService {

    private static final DateTimeFormatter DT_FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    private final Path notificationsFile;
    private final DelimitedFileUtil delim;

    public NotificationService(Path dataDirectory, DelimitedFileUtil delim) {
        this.notificationsFile = dataDirectory.resolve("notifications.txt");
        this.delim = delim;
    }

    /** All notifications for a given user, newest first. */
    public List<Notification> findByUserId(String userId) {
        return loadAll().stream()
                .filter(n -> Objects.equals(n.getUserId(), userId))
                .sorted(Comparator.comparing(Notification::getId, Comparator.reverseOrder()))
                .collect(Collectors.toList());
    }

    /** Count of unread notifications for a given user. */
    public long countUnread(String userId) {
        return findByUserId(userId).stream()
                .filter(n -> !n.isRead())
                .count();
    }

    public Optional<Notification> findById(Long id) {
        return loadAll().stream()
                .filter(n -> Objects.equals(n.getId(), id))
                .findFirst();
    }

    /** Create and persist a new notification. */
    public Notification create(String userId, Long bookingId, String message) {
        Notification n = new Notification();
        n.setUserId(userId);
        n.setBookingId(bookingId);
        n.setMessage(message);
        n.setRead(false);
        n.setCreatedAt(LocalDateTime.now().format(DT_FMT));
        return save(n);
    }

    /** Mark a single notification as read. */
    public void markRead(Long id) {
        findById(id).ifPresent(n -> {
            n.setRead(true);
            save(n);
        });
    }

    /** Mark all notifications for a user as read. */
    public void markAllRead(String userId) {
        List<Notification> all = loadAll();
        boolean changed = false;
        for (Notification n : all) {
            if (Objects.equals(n.getUserId(), userId) && !n.isRead()) {
                n.setRead(true);
                changed = true;
            }
        }
        if (changed) {
            writeAll(all);
        }
    }

    private Notification save(Notification notification) {
        List<Notification> all = loadAll();
        if (notification.getId() == null) {
            notification.setId(nextId(all));
            all.add(notification);
        } else {
            boolean replaced = false;
            for (int i = 0; i < all.size(); i++) {
                if (Objects.equals(all.get(i).getId(), notification.getId())) {
                    all.set(i, notification);
                    replaced = true;
                    break;
                }
            }
            if (!replaced) {
                all.add(notification);
            }
        }
        writeAll(all);
        return notification;
    }

    private List<Notification> loadAll() {
        try {
            List<Notification> list = new ArrayList<>();
            for (String line : delim.readLines(notificationsFile)) {
                List<String> p = delim.parseCsvLine(line);
                if (p.size() < 6) continue;
                Notification n = new Notification();
                n.setId(Long.parseLong(DelimitedFileUtil.safeTrim(p.get(0))));
                n.setUserId(DelimitedFileUtil.safeTrim(p.get(1)));
                try {
                    n.setBookingId(Long.parseLong(DelimitedFileUtil.safeTrim(p.get(2))));
                } catch (NumberFormatException ignored) { /* null stays null */ }
                n.setMessage(DelimitedFileUtil.safeTrim(p.get(3)));
                n.setRead("true".equalsIgnoreCase(DelimitedFileUtil.safeTrim(p.get(4))));
                n.setCreatedAt(DelimitedFileUtil.safeTrim(p.get(5)));
                list.add(n);
            }
            return list;
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private void writeAll(List<Notification> all) {
        List<String> lines = new ArrayList<>();
        lines.add("# id,userId,bookingId,message,read,createdAt");
        for (Notification n : all.stream()
                .sorted(Comparator.comparing(Notification::getId, Comparator.nullsLast(Long::compareTo)))
                .toList()) {
            lines.add(delim.formatLine(List.of(
                    String.valueOf(n.getId()),
                    n.getUserId(),
                    n.getBookingId() == null ? "" : String.valueOf(n.getBookingId()),
                    n.getMessage() == null ? "" : n.getMessage(),
                    n.isRead() ? "true" : "false",
                    n.getCreatedAt() == null ? "" : n.getCreatedAt()
            )));
        }
        try {
            delim.writeLines(notificationsFile, lines);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private long nextId(List<Notification> list) {
        return list.stream()
                .map(Notification::getId)
                .filter(Objects::nonNull)
                .max(Long::compareTo)
                .map(v -> v + 1)
                .orElse(1L);
    }
}
