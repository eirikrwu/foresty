$(document).ready(function () {
    // build log dialog
    $("#log-frame-container").dialog({
        height: $(window).height() * 0.9,
        width: $(window).width() * 0.8,
        title: "Log",
        modal: true
    });

    // hide log-frame by default
    $("#log-frame-container").dialog("close");

    // show events
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
                    $("#log-frame-container").dialog("open");

                    var eventId = $(this).attr("eventId");
                    $('#log-frame').attr('src', "log.html?eventId=" + eventId);
                });

                $('.delete-event-button').click(function (e) {
                    e.stopPropagation();
                    var url = "q/events/" + $(this).attr("eventid");
                    $.ajax({
                        url: url,
                        type: 'DELETE',
                        success: function (data, textStatus, jaXHR) {
                            showEvents(null, null);
                        }
                    })
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
    if (column.id.localeCompare("operation") == 0) {
        return '<td><a class="delete-event-button" href="#" eventid=' + record.id + '>Delete</a></td>'
    }

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

    var levelClass = "";
    if (record['highestLevel'].localeCompare("WARN") == 0) {
        levelClass = "warning";
    } else if (record['highestLevel'].localeCompare("ERROR") == 0 || record['highestLevel'].localeCompare("FATAL") == 0) {
        levelClass = "danger";
    }

    return '<tr class="clickableRow ' + levelClass + '" eventid=' + record.id + '>' + tr + '</tr>';
}