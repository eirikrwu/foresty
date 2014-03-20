$(document).ready(function () {
    // bind node row click event
    showEvents(null, null);
});

//function showEventGroups() {
//    $.getJSON("q/event-groups", function (data) {
//        var eventGroupTable = $('#event-group-table');
//        var dynatable = eventGroupTable.data('dynatable');
//        if (dynatable != null) {
//            dynatable.settings.dataset.originalRecords = data;
//            dynatable.process();
//        } else {
//            eventGroupTable.on('dynatable:afterUpdate', function(event, rows) {
//                $(".clickableRow", eventGroupTable).click(function() {
//                    var eventName = $(this).attr("eventname");
//                    showEvent(eventName);
//                });
//            });
//
//            eventGroupTable.dynatable({
//                dataset: {
//                    records: data
//                },
//                writers: {
//                    _rowWriter: eventGroupRowWriter
//                }
//            });
//        }
//    });
//}

function showEvents(name, level) {
    var url = "q/events?";
    if (name != null) {
        url += "name=" + name;
    }
    if (level != null) {
        url += "&level=" + level;
    }

    $.getJSON(url, function (data) {
        var eventTable = $('#event-table');
        var dynatable = eventTable.data('dynatable');
        if (dynatable != null) {
            dynatable.settings.dataset.originalRecords = data;
            dynatable.process();
        } else {
            eventTable.on('dynatable:afterUpdate', function(event, rows) {
                $(".clickableRow", eventTable).click(function() {
                    var eventId = $(this).attr("eventId");
                    $('#log-frame').attr('src', "log.html?eventId=" + eventId);
                });
            });

            eventTable.dynatable({
                dataset: {
                    records: data
                },
                writers: {
                    _rowWriter: eventRowWriter
                }
            });
        }
    });
}

function eventRowWriter(rowIndex, record, columns, cellWriter) {
    var tr = '';

    // grab the record's attribute for each column
    for (var i = 0, len = columns.length; i < len; i++) {
        tr += cellWriter(columns[i], record);
    }

    return '<tr class="clickableRow" eventid=' + record.id + '>' + tr + '</tr>';
}