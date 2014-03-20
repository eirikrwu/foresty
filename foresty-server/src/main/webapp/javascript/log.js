/**
 * Created by EveningSun on 14-3-20.
 */
$(document).ready(function () {
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
        var logTable = $('#log-table');
        var dynatable = logTable.data('dynatable');
        if (dynatable != null) {
            dynatable.settings.dataset.originalRecords = data;
            dynatable.process();
        } else {
            logTable.dynatable({
                dataset: {
                    records: data
                }
            });
        }
    });
}