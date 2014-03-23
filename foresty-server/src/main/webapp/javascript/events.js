$(document).ready(function () {
    // hide log-frame by default
    closeLogView();

    // bind node row click event
    showEvents(null, null);
});

function closeLogView() {
    $('#log-frame').hide();
}

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
        // pre-process data first
        for (var i = 0; i < data.length; i++) {
            var event = data[i];
            event['startTime'] = moment(event['startTime']).format("MM/DD HH:mm:ss:SSS");

            switch (event['highestLevel']) {
                case (10000):
                    event['highestLevel'] = "DEBUG";
                    break;
                case (20000):
                    event['highestLevel'] = "INFO";
                    break;
                case (30000):
                    event['highestLevel'] = "WARN";
                    break;
                case (40000):
                    event['highestLevel'] = "ERROR";
                    break;
                case (50000):
                    event['highestLevel'] = "FATAL";
                    break;
            }
        }

        var eventTable = $('#event-table');
        var dynatable = eventTable.data('dynatable');
        if (dynatable != null) {
            dynatable.settings.dataset.originalRecords = data;
            dynatable.process();
        } else {
            eventTable.on('dynatable:afterUpdate', function (event, rows) {
                $(".clickableRow", eventTable).click(function () {
                    $('#log-frame').show();
                    var eventId = $(this).attr("eventId");
                    $('#log-frame').attr('src', "log.html?eventId=" + eventId);
                });
            });

            eventTable.dynatable({
                dataset: {
                    records: data,
                    perPageDefault: 1000,
                    perPageOptions: [100, 200, 500, 1000]
                },
                writers: {
                    _rowWriter: eventRowWriter,
                    _cellWriter: eventCellWriter
                }
            });
        }
    });
}

function eventCellWriter(column, record) {
    var html = column.attributeWriter(record),
        td = '<td';

    if (column.hidden || column.textAlign) {
        td += ' style="';

        // keep cells for hidden column headers hidden
        if (column.hidden) {
            td += 'display: none;';
        }

        // keep cells aligned as their column headers are aligned
        if (column.textAlign) {
            td += 'text-align: ' + column.textAlign + ';';
        }

        td += '"';
    }

    td += 'class="column' + column.id + '"';

    return td + '>' + html + '</td>';
}

function eventRowWriter(rowIndex, record, columns, cellWriter) {
    var tr = '';

    // grab the record's attribute for each column
    for (var i = 0, len = columns.length; i < len; i++) {
        tr += cellWriter(columns[i], record);
    }

    return '<tr class="clickableRow level' + record['highestLevel'] + '" eventid=' + record.id + '>' + tr + '</tr>';
}