package com.foresty.repository;

import com.foresty.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

/**
 * Created by EveningSun on 14-3-19.
 */
public interface EventRepository extends EventRepositoryCustom, JpaRepository<Event, String> {
    @Query("SELECT DISTINCT (e.name) FROM Event e")
    public List<String> getEventNames();

    @Query("SELECT COUNT(e) FROM Event e")
    public Long getTotalEventCount();

    public static class EventCriteria {
        private String name;
        private Date minStartTime;
        private Date maxStartTime;
        private Integer minHighestLevel;
        private String orderBy;
        private boolean orderDesc;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getMinHighestLevel() {
            return minHighestLevel;
        }

        public void setMinHighestLevel(Integer minHighestLevel) {
            this.minHighestLevel = minHighestLevel;
        }

        public String getOrderBy() {
            return orderBy;
        }

        public void setOrderBy(String orderBy) {
            this.orderBy = orderBy;
        }

        public boolean isOrderDesc() {
            return orderDesc;
        }

        public void setOrderDesc(boolean orderDesc) {
            this.orderDesc = orderDesc;
        }

        public Date getMinStartTime() {
            return minStartTime;
        }

        public void setMinStartTime(Date startDate) {
            this.minStartTime = startDate;
        }

        public Date getMaxStartTime() {
            return maxStartTime;
        }

        public void setMaxStartTime(Date endDate) {
            this.maxStartTime = endDate;
        }
    }
}
