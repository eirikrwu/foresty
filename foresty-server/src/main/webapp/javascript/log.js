/**
 * Created by EveningSun on 14-3-20.
 */
$(document).ready(function () {
    // handle ESC
    $(document).keydown(function (e) {
        if (e.keyCode == 70 && e.shiftKey && e.ctrlKey) {
            $('#dynatable-query-search-log-table').focus();
        }

        if (e.keyCode == 27) {
            if ($('#dynatable-query-search-log-table').is(":focus")) {
                $('body').focus();
            } else {
                window.parent.closeLogDialog();
            }
        }
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
            log['timestamp'] = moment(log['timestamp']).format("MM/DD HH:mm:ss:SSS");

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
                inputs: {
                    searchTarget: $('#log-table-search-container'),
                    searchPlacement: 'append',
                    perPageTarget: $('#log-table-per-page-container'),
                    perPagePlacement: 'append'
                },
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

    var levelClass = "";
    var level = record['level'];
    if (level.localeCompare("INFO") == 0) {
        levelClass = "info";
    } else if (level.localeCompare("WARN") == 0) {
        levelClass = "warning";
    } else if (level.localeCompare("ERROR") == 0 || level.localeCompare("FATAL") == 0) {
        levelClass = "danger";
    }

//    return '<tr class="level' + record['level'] + '"' + '>' + tr + '</tr>';
    return '<tr class="' + levelClass + '"' + '>' + tr + '</tr>';
}