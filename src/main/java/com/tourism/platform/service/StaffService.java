package com.tourism.platform.service;

import com.tourism.platform.model.Staff;
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
 * Line format ({@code staff.txt}):
 * {@code id,username,password,fullName,email,department}
 */
@Service
public class StaffService {

    private final Path staffFile;
    private final DelimitedFileUtil delim;

    public StaffService(Path dataDirectory, DelimitedFileUtil delim) {
        this.staffFile = dataDirectory.resolve("staff.txt");
        this.delim = delim;
    }

    public List<Staff> findAll() {
        return loadAll().stream()
                .sorted(Comparator.comparing(Staff::getId, Comparator.nullsLast(Long::compareTo)))
                .collect(Collectors.toList());
    }

    public List<Staff> search(String query) {
        String q = DelimitedFileUtil.safeTrim(query).toLowerCase();
        if (q.isEmpty()) {
            return findAll();
        }
        return findAll().stream()
                .filter(s -> contains(s.getUsername(), q)
                        || contains(s.getFullName(), q)
                        || contains(s.getEmail(), q)
                        || contains(s.getDepartment(), q))
                .collect(Collectors.toList());
    }

    private boolean contains(String field, String q) {
        return field != null && field.toLowerCase().contains(q);
    }

    public Optional<Staff> findById(Long id) {
        return findAll().stream().filter(s -> Objects.equals(s.getId(), id)).findFirst();
    }

    public Optional<Staff> findByUsernamePassword(String username, String password) {
        String u = DelimitedFileUtil.safeTrim(username);
        String p = DelimitedFileUtil.safeTrim(password);
        return findAll().stream()
                .filter(s -> u.equalsIgnoreCase(DelimitedFileUtil.safeTrim(s.getUsername()))
                        && p.equals(DelimitedFileUtil.safeTrim(s.getPassword())))
                .findFirst();
    }

    public boolean usernameExistsIgnoreCase(String username, Long excludingId) {
        String key = DelimitedFileUtil.safeTrim(username).toLowerCase();
        return findAll().stream()
                .filter(s -> excludingId == null || !Objects.equals(s.getId(), excludingId))
                .anyMatch(s -> key.equals(DelimitedFileUtil.safeTrim(s.getUsername()).toLowerCase()));
    }

    public Staff save(Staff staff) {
        List<Staff> all = loadAll();
        if (staff.getId() == null) {
            staff.getId();
            long maxId = all.stream().map(Staff::getId).filter(Objects::nonNull).max(Long::compareTo).orElse(0L);
            staff.setId(maxId + 1);
            all.add(staff);
        } else {
            boolean replaced = false;
            for (int i = 0; i < all.size(); i++) {
                if (Objects.equals(all.get(i).getId(), staff.getId())) {
                    all.set(i, staff);
                    replaced = true;
                    break;
                }
            }
            if (!replaced) {
                all.add(staff);
            }
        }
        writeAll(all);
        return staff;
    }

    public void deleteById(Long id) {
        List<Staff> all = loadAll();
        all.removeIf(s -> Objects.equals(s.getId(), id));
        writeAll(all);
    }

    private List<Staff> loadAll() {
        try {
            List<Staff> list = new ArrayList<>();
            for (String line : delim.readLines(staffFile)) {
                List<String> p = delim.parseCsvLine(line);
                if (p.size() < 6) {
                    continue;
                }
                Staff s = new Staff();
                s.setId(Long.parseLong(DelimitedFileUtil.safeTrim(p.get(0))));
                s.setUsername(DelimitedFileUtil.safeTrim(p.get(1)));
                s.setPassword(DelimitedFileUtil.safeTrim(p.get(2)));
                s.setFullName(DelimitedFileUtil.safeTrim(p.get(3)));
                s.setEmail(DelimitedFileUtil.safeTrim(p.get(4)));
                s.setDepartment(DelimitedFileUtil.safeTrim(p.get(5)));
                list.add(s);
            }
            return list;
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private void writeAll(List<Staff> staffList) {
        List<String> lines = new ArrayList<>();
        lines.add("# id,username,password,fullName,email,department");
        for (Staff s : staffList.stream()
                .sorted(Comparator.comparing(Staff::getId, Comparator.nullsLast(Long::compareTo)))
                .toList()) {
            lines.add(delim.formatLine(List.of(
                    String.valueOf(s.getId()),
                    s.getUsername() == null ? "" : s.getUsername(),
                    s.getPassword() == null ? "" : s.getPassword(),
                    s.getFullName() == null ? "" : s.getFullName(),
                    s.getEmail() == null ? "" : s.getEmail(),
                    s.getDepartment() == null ? "" : s.getDepartment()
            )));
        }
        try {
            delim.writeLines(staffFile, lines);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
