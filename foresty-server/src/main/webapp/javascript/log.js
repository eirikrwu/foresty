var currentNodeDepth;
var currentNodeName;

$(document).ready(function () {
    // bind node row click event

    showChildNodes(0, "empty");
});

function showChildNodes(parentNodeDepth, parentNodeName) {
    $.getJSON("/q/report/" + parentNodeDepth + "/" + parentNodeName + "/nodes", function (data) {
        currentNodeDepth = parentNodeDepth;
        currentNodeName = parentNodeName;

        var dynatable = $('#node-table').data('dynatable');
        if (dynatable != null) {
            dynatable.settings.dataset.originalRecords = data;
            dynatable.process();
        } else {
            $('#node-table').on('dynatable:afterUpdate', function(event, rows) {
                $(".clickableRow").click(function() {
                    showChildNodes(currentNodeDepth + 1, $(this).attr("nodename"));
                });

                showLog(currentNodeDepth, currentNodeName);
            });

            $('#node-table').dynatable({
                dataset: {
                    records: data
                },
                writers: {
                    _rowWriter: clickableRowWriter
                }
            });
        }
    });
}

function showLog(parentNodeDepth, parentNodeName) {
    $.getJSON("/q/report/" + parentNodeDepth + "/" + parentNodeName + "/log", function (data) {
        var dynatable = $('#log-table').data('dynatable');
        if (dynatable != null) {
            dynatable.settings.dataset.originalRecords = data;
            dynatable.process();
        } else {
            $('#log-table').dynatable({
                dataset: {
                    records: data
                }
            });
        }
    });
}

// dynatable extension

function clickableRowWriter(rowIndex, record, columns, cellWriter) {
    var tr = '';

    // grab the record's attribute for each column
    for (var i = 0, len = columns.length; i < len; i++) {
        tr += cellWriter(columns[i], record);
    }

    return '<tr class="clickableRow" nodeName=' + record.node + '>' + tr + '</tr>';
}