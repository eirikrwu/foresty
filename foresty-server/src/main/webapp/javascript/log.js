/**
 * Created by EveningSun on 14-3-20.
 */
$(document).ready(function () {
    // bind button handler
    $('#close-self-button').click(function (e) {
        parent.closeLogView();
    });

    // show logs
    var eventId = getUrlQueryParameters()["eventId"];
    showLog(eventId);
});

function getUrlQueryParameters() {
    var vars = [], hash;
    var hashes = window.location.href.slice(window.location.href.indexOf('?') + 1).split('&');
    for (var i = 0; i < hashes.length; i++) {
        hash = hashes[i].split('=');
        vars.push(hash[0]);
        vars[hash[0]] = hash[1];
    }
    return vars;
}

function showLog(eventId) {
    $.getJSON("q/events/" + eventId, function (data) {
        // pre*processing data
        for (var i = 0; i < data.length; i++) {
            var log = data[i];
            switch (log['level']) {
                case (10000):
                    log['level'] = "DEBUG";
                    break;
                case (20000):
                    log['level'] = "INFO";
                    break;
                case (30000):
                    log['level'] = "WARN";
                    break;
                case (40000):
                    log['level'] = "ERROR";
                    break;
                case (50000):
                    log['level'] = "FATAL";
                    break;
            }
        }

        var logTable = $('#log-table');
        var dynatable = logTable.data('dynatable');
        if (dynatable != null) {
            dynatable.settings.dataset.originalRecords = data;
            dynatable.process();
        } else {
            logTable.dynatable({
                dataset: {
                    records: data,
                    perPageDefault: 1000,
                    perPageOptions: [100, 200, 500, 1000]
                },
                writers: {
                    _rowWriter: logRowWriter,
                    _cellWriter: logCellWriter
                }
            });
        }
    })
}

function logCellWriter(column, record) {
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

function logRowWriter(rowIndex, record, columns, cellWriter) {
    var tr = '';

    // grab the record's attribute for each column
    for (var i = 0, len = columns.length; i < len; i++) {
        tr += cellWriter(columns[i], record);
    }

    return '<tr class="level' + record['level'] + '"' + '>' + tr + '</tr>';
}