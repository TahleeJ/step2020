// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps;

import java.util.Collection;
import java.util.Comparator;
import java.util.Arrays;
import java.util.ArrayList;

public final class FindMeetingQuery {
    private static ArrayList<Event> optionalEvents = new ArrayList<>();

    public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
        Collection<TimeRange> times = new ArrayList<>();

        if ((int) request.getDuration() > TimeRange.WHOLE_DAY.duration()) {
            return times;
        } else if (events.size() == 0) {
            times.add(TimeRange.WHOLE_DAY);

            return times;
        } else if (events.size() == 1) {
            boolean included = false;

            for (Event event : events) {
                if (attends(event, request)) {
                    included = true;
                }
            }

            if (included) {
                Event[] e = events.toArray(new Event[0]);
                TimeRange time = e[0].getWhen();

                if (time.start() - TimeRange.START_OF_DAY >= (int) request.getDuration()) {
                    times.add(TimeRange.fromStartEnd(TimeRange.START_OF_DAY, time.start(), false));
                }

                if (TimeRange.END_OF_DAY - time.end() >= (int) request.getDuration()) {
                    times.add(TimeRange.fromStartEnd(time.end(), TimeRange.END_OF_DAY, true));
                }
            } else {
                times.add(TimeRange.WHOLE_DAY);
            }

            return times;
        } else {
            Event[] e = events.toArray(new Event[0]);
            Event[] sortedEvents = insertionSort(e, TimeRange.ORDER_BY_START, request);;

            if (sortedEvents.length == 0) {
                if (optionalEvents.size() == 0) {
                    times.add(TimeRange.WHOLE_DAY);
                }
        
                return times;
            } else if (sortedEvents.length == 1) {
                TimeRange time = sortedEvents[0].getWhen();

                if (time.start() - TimeRange.START_OF_DAY >= (int) request.getDuration()) {
                    times.add(TimeRange.fromStartEnd(TimeRange.START_OF_DAY, time.start(), false));
                }

                if (TimeRange.END_OF_DAY - time.end() >= (int) request.getDuration()) {
                    times.add(TimeRange.fromStartEnd(time.end(), TimeRange.END_OF_DAY, true));
                }

                return times;
            } else {
                int startTime = TimeRange.START_OF_DAY;
                int endTime = TimeRange.END_OF_DAY;

                TimeRange first = sortedEvents[0].getWhen();
                TimeRange second = sortedEvents[1].getWhen();

                int tempStart = startTime;
                int tempEnd = endTime;

                boolean optional = false;
                int previous = startTime;

                if (first.overlaps(second)) {
                    tempEnd = Math.min(first.start(), second.start());
                    tempStart = Math.max(first.end(), second.end());

                    if (optionalEvents.contains(first)) {
                        if (first.start() - startTime < (int) request.getDuration()) {
                            tempEnd = second.start();
                            optional = true;
                            tempStart = previous;
                        }
                    }
                } else {
                    if (optionalEvents.contains(first)) {
                        if (first.start() - startTime < (int) request.getDuration()) {
                            tempEnd = second.start();
                            optional = true;
                        }
                    } else {
                        tempEnd = first.start();
                        previous = first.end();
                    }
                }

                if (tempEnd - startTime >= (int) request.getDuration()) {
                    times.add(TimeRange.fromStartEnd(startTime, tempEnd, false));
                }

                for (int i = 0; (i + 1) < sortedEvents.length; i++) {
                    first = sortedEvents[i].getWhen();
                    second = sortedEvents[i + 1].getWhen();

                    if (first.overlaps(second)) {
                        tempStart = Math.max(first.end(), second.end());

                        if (optionalEvents.contains(first)) {
                            if (first.end() - tempStart < (int) request.getDuration()) {
                                tempStart = previous;
                            }
                        } else {
                            previous = tempStart;
                        }
                    } else {
                        tempStart = first.end();
                        
                        if (optionalEvents.contains(first)) {
                            if (second.start() - first.end() < (int) request.getDuration()) {
                                tempStart = previous;
                            }
                        } else {
                            previous = tempStart;
                        }
                    }

                    if (second.start() - tempStart >= (int) request.getDuration()) {
                        times.add(TimeRange.fromStartEnd(tempStart, second.start(), false));
                    }
                }

                tempStart = Math.max(previous, second.end());

                if (endTime - tempStart >= (int) request.getDuration()) {
                    times.add(TimeRange.fromStartEnd(tempStart, endTime, true));
                }
            
                return times;
            }
        }
    }

    private static Event[] insertionSort(Event[] events, Comparator<TimeRange> comparator, MeetingRequest request) {
        Event[] sortedArray = new Event[events.length];
        ArrayList<Event> sorted = new ArrayList<>();
        int back;

        for (int k = 1; k < events.length; k++) {
            back = k;
            
            while (back > 0) {
                if (comparator.compare(events[back].getWhen(), events[back - 1].getWhen()) < 0) {
                    swap(events, back, back - 1);
                }
                sortedArray[back - 1] = events[back - 1];
                sortedArray[back] = events[back];

                back--;
            }
        }

        for (Event event : sortedArray) {
            if (attends(event, request)) {
                sorted.add(event);
            }
        }

        return sorted.toArray(new Event[sorted.size()]);
    }

    private static boolean attends(Event event, MeetingRequest request) {
        boolean included = false;

        if (event != null) {
            Collection<String> attendees = event.getAttendees();

            for (String eventAttendee : attendees) {
                if (request.getOptionalAttendees().contains(eventAttendee)) {
                    if (event.getWhen().duration() < TimeRange.WHOLE_DAY.duration()) {
                        included = true;
                    }
                    optionalEvents.add(event);

                    break;
                } else if (request.getAttendees().contains(eventAttendee)) {
                    included = true;
                    break;
                }
            }
        }

        return included;
    }

    private static void swap(Event[] eventArray, int index1, int index2) {
        Event data = eventArray[index1];
        eventArray[index1] = eventArray[index2];
        eventArray[index2] = data;
    }
}