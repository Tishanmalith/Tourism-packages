package com.tourism.platform.service;

import com.tourism.platform.model.Feedback;
import com.tourism.platform.util.DelimitedFileUtil;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * feedback.txt:<br/>
 * {@code id,userId,packageId,rating,comment,createdAt}<br/>
 * {@code userId} references users.txt id (UUID).
 */
@Service
public class FeedbackService {

    private final Path feedbackFile;
    private final DelimitedFileUtil delim;

    public FeedbackService(Path dataDirectory, DelimitedFileUtil delim) {
        this.feedbackFile = dataDirectory.resolve("feedback.txt");
        this.delim = delim;
    }

    public List<Feedback> findAll() {
        return loadAll().stream()
                .sorted(Comparator.comparing(Feedback::getId, Comparator.nullsLast(Long::compareTo)))
                .collect(Collectors.toList());
    }

    public Optional<Feedback> findById(Long id) {
        return findAll().stream().filter(f -> Objects.equals(f.getId(), id)).findFirst();
    }

    /** All feedback submitted by a specific user. */
    public List<Feedback> findByUserId(String userId) {
        return findAll().stream()
                .filter(f -> Objects.equals(f.getUserId(), userId))
                .collect(Collectors.toList());
    }

    public Feedback save(Feedback item) {
        List<Feedback> all = loadAll();
        if (item.getId() == null) {
            item.setId(nextId(all));
            all.add(item);
        } else {
            boolean replaced = false;
            for (int i = 0; i < all.size(); i++) {
                if (Objects.equals(all.get(i).getId(), item.getId())) {
                    all.set(i, item);
                    replaced = true;
                    break;
                }
            }
            if (!replaced) {
                all.add(item);
            }
        }
        writeAll(all);
        return item;
    }

    public void deleteById(Long id) {
        List<Feedback> all = loadAll();
        all.removeIf(f -> Objects.equals(f.getId(), id));
        writeAll(all);
    }

    private List<Feedback> loadAll() {
        try {
            List<Feedback> list = new ArrayList<>();
            for (String line : delim.readLines(feedbackFile)) {
                List<String> p = delim.parseCsvLine(line);
                if (p.size() < 6) {
                    continue;
                }
                Feedback f = new Feedback();
                f.setId(Long.parseLong(DelimitedFileUtil.safeTrim(p.get(0))));
                f.setUserId(DelimitedFileUtil.safeTrim(p.get(1)));
                f.setPackageId(Long.parseLong(DelimitedFileUtil.safeTrim(p.get(2))));
                f.setRating(Integer.parseInt(DelimitedFileUtil.safeTrim(p.get(3))));
                f.setComment(DelimitedFileUtil.safeTrim(p.get(4)));
                f.setCreatedAt(DelimitedFileUtil.safeTrim(p.get(5)));
                list.add(f);
            }
            return list;
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private void writeAll(List<Feedback> all) {
        List<String> lines = new ArrayList<>();
        lines.add("# id,userId,packageId,rating,comment,createdAt");
        for (Feedback f : all.stream().sorted(Comparator.comparing(Feedback::getId, Comparator.nullsLast(Long::compareTo)))
                .toList()) {
            lines.add(delim.formatLine(List.of(
                    String.valueOf(f.getId()),
                    f.getUserId(),
                    String.valueOf(f.getPackageId()),
                    String.valueOf(f.getRating()),
                    f.getComment() == null ? "" : f.getComment(),
                    f.getCreatedAt() == null ? "" : f.getCreatedAt())));
        }
        try {
            delim.writeLines(feedbackFile, lines);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private long nextId(List<Feedback> list) {
        return list.stream()
                .map(Feedback::getId)
                .filter(Objects::nonNull)
                .max(Long::compareTo)
                .map(v -> v + 1)
                .orElse(1L);
    }
}
