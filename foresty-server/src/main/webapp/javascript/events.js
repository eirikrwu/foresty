$(document).ready(function () {
    // bind Alt+F handler
    $(document).keydown(function (e) {
        if (e.keyCode == 70 && e.shiftKey && e.ctrlKey) {
            $('#dynatable-query-search-event-table').focus();
        }
        if (e.keyCode == 27) {
            $('table#event-table > tbody').focus();
            $(document).scrollTop(0);
        }
    });

    // build log dialog
    $("#log-frame-container").dialog({
        height: $(window).height() * 0.9,
        width: $(window).width() * 0.9,
        title: "Log",
        modal: true,
        close: function () {
            $('table#event-table > tbody').focus();
        }
    });

    // hide log-frame by default
    closeLogDialog();

    // show events
    showEvents();
});

function closeLogDialog() {
    $("#log-frame-container").dialog("close");
}

function showEvents() {
    var eventTable = $('#event-table');
    eventTable.on('dynatable:ajax:success', function (e, data) {
        // pre-process data first
        for (var i = 0; i < data.records.length; i++) {
            var event = data.records[i];
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
    });

    eventTable.on('dynatable:afterUpdate', function (event, rows) {
        $(".clickableRow", eventTable).click(function () {
            $("#log-frame-container").dialog("open");

            var eventId = $(this).attr("eventId");
            $('#log-frame').attr('src', "log.html?eventId=" + eventId + '&?sorts[timestamp]=1');

            setTimeout(function () {
                $('#log-frame')[0].contentWindow.focus();
            }, 100);
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

        // bind key navigator
        $('table#event-table > tbody tr').keynavigator({
            keys: {
                13: function ($el, cellIndex, e) {
                    $el.trigger('click');
                }
            }
        });

        // focus on table
        $('table#event-table > tbody').focus();
        $(document).scrollTop(0);
    });

    eventTable.dynatable({
        inputs: {
            searchTarget: $('#event-table-search-container'),
            searchPlacement: 'append',
            perPageTarget: $('#event-table-per-page-container'),
            perPagePlacement: 'append',
            queries: $('#event-table-minimum-level')
        },
        features: {
            search: false
        },
        dataset: {
            ajax: true,
            ajaxUrl: url = "q/events?",
            ajaxOnLoad: true,
            records: [],
            perPageDefault: 1000,
            perPageOptions: [100, 200, 500, 1000]
        },
        writers: {
            _rowWriter: eventRowWriter,
            _cellWriter: eventCellWriter
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

//    td += 'class="column' + column.id + '"';
    if (column.id.localeCompare("timespan") == 0) {
        html += " ms";

        var timespan = record['timespan'];
        if (timespan < 200) {
            td += 'class="normalCell"';
        } else if (timespan < 1000) {
            td += 'class="warningCell"';
        } else {
            td += 'class="errorCell"';
        }
    }

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