package com.foresty.quartz.job;

import com.foresty.model.Event;
import com.foresty.repository.EventRepository;
import com.foresty.service.EventService;
import com.foresty.service.EventServiceException;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.List;

/**
 * Created by EveningSun on 14-3-29.
 */
@Component
public class EventRollingJob implements Job {
    private static final Logger LOGGER = LogManager.getLogger(EventRollingJob.class);

    @Autowired
    private EventService eventService;
    @Autowired
    private EventRepository eventRepository;

    private int rollingDays = 1;

    @Override
    @Transactional
    public void execute(JobExecutionContext context) throws JobExecutionException {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, -24 * this.rollingDays);

        EventRepository.EventCriteria criteria = new EventRepository.EventCriteria();
        criteria.setMaxStartTime(calendar.getTime());

        try {
            List<Event> events = this.eventRepository.getEventsByCriterion(criteria, null).getContent();
            for (Event event : events) {
                this.eventService.deleteEventById(event.getId());
            }

            LOGGER.info("Deleted " + events.size() + " events because of events rolling.");
        } catch (DataAccessException e) {
            throw new JobExecutionException("Can't query for events: " + e.getMessage(), e);
        } catch (EventServiceException e) {
            throw new JobExecutionException("Can't delete event: " + e.getMessage(), e);
        }
    }
}
