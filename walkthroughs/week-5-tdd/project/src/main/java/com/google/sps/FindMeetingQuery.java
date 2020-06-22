package com.google.sps;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collection;

public final class FindMeetingQuery {
    // Optional events are events that only contain optional attendees.

    // optionalEvents holds the all of the optionalEvents within the initially given collection of
    // events
    private static ArrayList<Event> optionalEvents = new ArrayList<>();

    public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
        // times holds all of the valid time ranges for the given meeting request
        Collection<TimeRange> times = new ArrayList<>();

        if ((int) request.getDuration() > TimeRange.WHOLE_DAY.duration()) {
            return times;
        }

        // If there are no events taking place during the day, the entire day is a valid time range
        if (events.size() == 0) {
            times.add(TimeRange.WHOLE_DAY);

            return times;
        }

        if (events.size() == 1) {
            boolean included = false;

            for (Event event : events) {
                if (attends(event, request)) {
                    included = true;
                    break;
                }
            }

            if (included) {
                Event[] eventArray = events.toArray(new Event[0]);
                TimeRange time = eventArray[0].getWhen();

                // Evaluate the time of day before the event's start time.
                if (time.start() - TimeRange.START_OF_DAY >= (int) request.getDuration()) {
                    times.add(TimeRange.fromStartEnd(TimeRange.START_OF_DAY, time.start(), false));
                }

                // Evaluate the time of day after the event's end time.
                if (TimeRange.END_OF_DAY - time.end() >= (int) request.getDuration()) {
                    times.add(TimeRange.fromStartEnd(time.end(), TimeRange.END_OF_DAY, true));
                }
            } else {
                times.add(TimeRange.WHOLE_DAY);
            }

            return times;
        }
        // Sort the events according to their start time.
        Event[] eventArray = events.toArray(new Event[0]);
        Arrays.sort(eventArray, Event.EVENT_COMPARATOR);
        
        ArrayList<Event> sorted = new ArrayList<>();

        // Build a list of valid events (mandatory events and optional events that do not last the
        // entire day).
        for (Event event : eventArray) {
            if (attends(event, request)) {
                sorted.add(event);
            }
        }

        eventArray = sorted.toArray(new Event[0]);

        // Return no valid time ranges if the mandatory and optional events last the entire day.
        if (eventArray.length == 0) {
            return times;
        }

        if (eventArray.length == 1) {
            TimeRange time = eventArray[0].getWhen();

            // Evaluate the time of day before the event's start time.
            if (time.start() - TimeRange.START_OF_DAY >= (int) request.getDuration()) {
                times.add(TimeRange.fromStartEnd(TimeRange.START_OF_DAY, time.start(), false));
            }

            // Evaluate the time of day after the event's end time.
            if (TimeRange.END_OF_DAY - time.end() >= (int) request.getDuration()) {
                times.add(TimeRange.fromStartEnd(time.end(), TimeRange.END_OF_DAY, true));
            }

            return times;
        }

        int startTime = TimeRange.START_OF_DAY;
        int endTime = TimeRange.END_OF_DAY;

        // The first and second events' time ranges.
        TimeRange first = eventArray[0].getWhen();
        TimeRange second = eventArray[1].getWhen();

        // The possible start and end of the meeting.
        int tempStartofMeeting = startTime;
        int tempEndofMeeting = endTime;

        // The end time of the previous mandatory event.
        int lastPossibleMandatoryStartofMeeting = startTime;

        Event firstEvent = eventArray[0];

        // Evaluates the period of time before the first event.

        // If there is overlap between the second and first events .           
        if (first.contains(second)) {
            tempEndofMeeting = Math.min(first.start(), second.start());
            tempStartofMeeting = Math.max(first.end(), second.end());

            // If the first event is an optional event.
            if (optionalEvents.contains(firstEvent)) {
                // If the optional event creates a valid meeting time before it, treat it as a mandatory.
                // meeting
                if (first.start() - startTime >= (int) request.getDuration()) {
                    tempEndofMeeting = first.start();
                } else {
                    // Ignore the optional meeting if it would erase a valid meeting time.
                    tempEndofMeeting = second.start();
                }
             }

            // The start of the next possible meeting time is the max end time of the overlap.
            lastPossibleMandatoryStartofMeeting = tempStartofMeeting;
         } else {
            if (optionalEvents.contains(firstEvent)) {
                if (first.start() - startTime >= (int) request.getDuration()) {
                    // The possible end of the meeting is the beginning of the first meeting.
                    tempEndofMeeting = first.start();
                    // The start of the next possible meeting is the end of the first meeting.
                    lastPossibleMandatoryStartofMeeting = first.end();
                } else {
                    // The start of the next possible meeting is the end of the second meeting since the first
                    // meeting will be ignored.
                    lastPossibleMandatoryStartofMeeting = second.end();
                }
            } else {
                // The possible end of the meeting is the beginning of the first meeting.
                tempEndofMeeting = first.start();
                // The start of the next possible meeting is the end of the first meeting.
                lastPossibleMandatoryStartofMeeting = first.end();
            }
        }

        // A possible meeting time is between the beginning of the day and the determined possible end
        // time of the meeting.
        if (tempEndofMeeting - startTime >= (int) request.getDuration()) {
            times.add(TimeRange.fromStartEnd(startTime, tempEndofMeeting, false));
        }

        // Evaluates the period of time after each event.
        for (int i = 0; (i + 1) < eventArray.length; i++) {
            // The time range of the first and second events.
            first = eventArray[i].getWhen();
            second = eventArray[i + 1].getWhen();
            firstEvent = eventArray[i];

            if (first.contains(second)) {
                // The possible start of the meeting is the max end time of the first and second meetings.
                tempStartofMeeting = Math.max(first.end(), second.end());
            } else {
                // The possible start time of the meeting is the end of the first meeting.
                tempStartofMeeting = first.end();
            }

            if (optionalEvents.contains(firstEvent)) {
                // Evaluates if using the optional meeting would result in a time slot less than the
                // required meeting duration.
                if (second.start() - tempStartofMeeting < (int) request.getDuration() && second.start() - first.end() > 0) {
                    // The possible start of the meeting is the end time of the previous valid event.
                    tempStartofMeeting = lastPossibleMandatoryStartofMeeting;
                } else {
                    // The start of the next possible meeting is the max end time of the overlapping events.
                    lastPossibleMandatoryStartofMeeting = tempStartofMeeting;
                }
            }

            // Add the time range of the meeting if its possible start time to the beginning of the event
            // after it is of the required duration.
            if (second.start() - tempStartofMeeting >= (int) request.getDuration()) {
                times.add(TimeRange.fromStartEnd(tempStartofMeeting, second.start(), false));
            }
        }

        // The start of the next possible meeting is the maximum of the end time of the previous valid
        // event and the end time of the previous event.
        tempStartofMeeting = Math.max(lastPossibleMandatoryStartofMeeting, second.end());

        // Add the time range of the meeting if its possible start time to the end of the day is of the
        // required duration.
        if (endTime - tempStartofMeeting >= (int) request.getDuration()) {
            times.add(TimeRange.fromStartEnd(tempStartofMeeting, endTime, true));
        }

        return times;   
    }

    // Determines whether a given event contains mandatory attendees or is an optional event that does not
    // last the entire day.
    // If an event is optional, it will be added to optionalEvents if it does not last the entire day.
    private static boolean attends(Event event, MeetingRequest request) {
        boolean included = false;

        if (event != null) {
            Collection<String> attendees = event.getAttendees();

            for (String eventAttendee : attendees) {
                if (request.getOptionalAttendees().contains(eventAttendee)) {
                    if ((int) event.getWhen().duration() < TimeRange.WHOLE_DAY.duration()) {
                        included = true;
                        optionalEvents.add(event);
                    }

                    break;
                } else if (request.getAttendees().contains(eventAttendee)) {
                    included = true;
                    break;
                }
            }
        }

        return included;
    }
}