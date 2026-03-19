package util;

import java.time.LocalDate;

public class DateHolder {
    private LocalDate date;

    public DateHolder() {
        this.date = LocalDate.now();
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalDate getDate() {
        return this.date;
    }
}
